package io.army.criteria.impl;

import io.army.annotation.Table;
import io.army.meta.*;
import io.army.modelgen.ArmyMetaModelDomainProcessor;
import io.army.modelgen._MetaBridge;
import io.army.util._Collections;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public abstract class _TableMetaFactory {

    private _TableMetaFactory() {
        throw new UnsupportedOperationException();
    }

    public static <T> SimpleTableMeta<T> getSimpleTableMeta(final Class<T> domainClass) {
        return DefaultTableMeta.getSimpleTableMeta(domainClass);
    }

    public static <T> ParentTableMeta<T> getParentTableMeta(final Class<T> domainClass) {
        return DefaultTableMeta.getParentTableMeta(domainClass);
    }

    public static <P, T> ComplexTableMeta<P, T> getChildTableMeta(
            ParentTableMeta<P> parent, Class<T> domainClass) {
        return DefaultTableMeta.getChildTableMeta(parent, domainClass);
    }

    /**
     * @return a unmodifiable map.
     */
    public static Map<Class<?>, TableMeta<?>> getTableMetaMap(final SchemaMeta schemaMeta
            , final List<String> basePackages) {
        return getTableMetaMap(schemaMeta, basePackages, Thread.currentThread().getContextClassLoader());
    }

    /**
     * @return a unmodifiable map.
     */
    public static synchronized Map<Class<?>, TableMeta<?>> getTableMetaMap(final SchemaMeta schemaMeta
            , final List<String> basePackages, @Nullable final ClassLoader classLoader) throws TableMetaLoadException {
        URL url = null;
        try {
            final Map<Class<?>, TableMeta<?>> tableMetaMap = _Collections.hashMap();
            final String jarProtocol = "jar", fileProtocol = "file";
            for (String basePackage : basePackages) {
                if (!_StringUtils.hasText(basePackage)) {
                    throw new IllegalArgumentException("basePackage must have text.");
                }
                //1. convert base package
                if (basePackage.indexOf('.') > 0) {
                    basePackage = basePackage.replace('.', '/');
                }
                // 2. get url from base package
                final Enumeration<URL> enumeration;
                if (classLoader == null) {
                    enumeration = ClassLoader.getSystemResources(basePackage);
                } else {
                    enumeration = classLoader.getResources(basePackage);
                }
                // 3. scan java class file in base package for get TableMeta.
                while (enumeration.hasMoreElements()) {
                    url = enumeration.nextElement();
                    final String protocol = url.getProtocol();
                    final Stream<ByteBuffer> stream;
                    if (jarProtocol.equals(protocol)) {
                        stream = scanJavaJarForJavaClassFile(url);
                    } else if (fileProtocol.equals(protocol)) {
                        stream = Files.find(Paths.get(url.getPath()), Integer.MAX_VALUE, _TableMetaFactory::isJavaClassFile)
                                .map(_TableMetaFactory::readJavaClassFileBytes);
                    } else {
                        String m = String.format("url[%s] unsupported", url);
                        throw new IllegalArgumentException(m);
                    }
                    stream.map(buffer -> readJavaClassFile(buffer, schemaMeta)) // read java class file and get class name if match.
                            .filter(_StringUtils::hasText) // if empty string ,not domain class
                            .map(_TableMetaFactory::getOrCreateTableMeta)// get or create table meta
                            .forEach(tableMeta -> {
                                final Class<?> domainClass = tableMeta.javaType();
                                tableMetaMap.put(domainClass, tableMeta);
                                loadDomainMetaHolder(domainClass);
                            });
                }
                url = null;
            }
            return Collections.unmodifiableMap(tableMetaMap);
        } catch (TableMetaLoadException e) {
            throw e;
        } catch (Exception e) {
            String m;
            if (url == null) {
                m = e.getMessage();
            } else {
                m = String.format("url[%s] scan occur error: %s .", url, e.getMessage());
            }
            throw new TableMetaLoadException(m, e);
        } finally {
            TableMetaUtils.clearCache();
        }
    }


    public static Set<FieldMeta<?>> codecFieldMetaSet() {
        return TableFieldMeta.codecFieldMetaSet();
    }

    /*################################## blow private method ##################################*/

    /**
     * @see #getTableMetaMap(SchemaMeta, List, ClassLoader)
     */
    private static Stream<ByteBuffer> scanJavaJarForJavaClassFile(final URL url) {
        try {
            final URLConnection conn = url.openConnection();
            if (!(conn instanceof JarURLConnection)) {
                String m = String.format("url[%s] can' open %s .", url, JarURLConnection.class.getName());
                throw new IllegalArgumentException(m);
            }
            final JarURLConnection jarConn = (JarURLConnection) conn;
            final String rootEntryName = jarConn.getEntryName();
            final JarFile jarFile = jarConn.getJarFile();
            return jarFile
                    .stream()
                    .filter(entry -> isJavaClassEntry(rootEntryName, entry))
                    .map(entry -> readJavaClassEntryBytes(jarFile, entry));
        } catch (IOException e) {
            String m = String.format("jar[%s] scan occur error:%s", url, e.getMessage());
            throw new TableMetaLoadException(m, e);
        }
    }


    /**
     * @see #getTableMetaMap(SchemaMeta, List, ClassLoader)
     */
    private static ByteBuffer readJavaClassFileBytes(final Path classFilePath) {
        try (FileChannel channel = FileChannel.open(classFilePath, StandardOpenOption.READ)) {
            final long fileSize;
            fileSize = channel.size();
            if (fileSize > (Integer.MAX_VALUE - 32)) {
                String m = String.format("Class file[%s] too large,don't support read.", classFilePath);
                throw new IllegalArgumentException(m);
            }
            final ByteBuffer buffer = ByteBuffer.wrap(new byte[(int) fileSize]);
            if (channel.read(buffer) < 10) {
                throw classFileFormatError();
            }
            buffer.flip();
            return buffer;
        } catch (IOException e) {
            String m = String.format("class file[%s] read occur error:%s", classFilePath, e.getMessage());
            throw new TableMetaLoadException(m, e);
        }
    }

    /**
     * @throws TableMetaLoadException when not found table meta holder class of domainClass.
     * @see #getTableMetaMap(SchemaMeta, List, ClassLoader)
     */
    private static void loadDomainMetaHolder(Class<?> domainClass) {
        try {
            Class.forName(domainClass.getName() + _MetaBridge.META_CLASS_NAME_SUFFIX);
        } catch (ClassNotFoundException e) {
            String m = String.format("You compile %s without %s", domainClass.getName()
                    , ArmyMetaModelDomainProcessor.class.getName());
            throw new TableMetaLoadException(m, e);
        }

    }

    /**
     * @throws TableMetaLoadException when occur {@link IOException}
     * @see #scanJavaJarForJavaClassFile(URL)
     */
    private static ByteBuffer readJavaClassEntryBytes(final JarFile jarFile, final JarEntry entry) {
        final long entrySize = entry.getSize();
        if (entrySize > (Integer.MAX_VALUE - 32)) {
            String m = String.format("Class file[%s] too large,don't support read.", entry.getName());
            throw new IllegalArgumentException(m);
        }
        try (InputStream in = jarFile.getInputStream(entry);
             ByteArrayOutputStream out = new ByteArrayOutputStream((int) entrySize)) {
            final byte[] bufferArray = new byte[(int) Math.min(2048, entrySize)];
            int length;
            while ((length = in.read(bufferArray)) > 0) {
                out.write(bufferArray, 0, length);
            }
            return ByteBuffer.wrap(out.toByteArray());
        } catch (IOException e) {
            String m = String.format("Jar class file[%s] read occur error:%s", entry.getName(), e.getMessage());
            throw new TableMetaLoadException(m, e);
        }
    }

    /**
     * @see #scanJavaJarForJavaClassFile(URL)
     */
    private static boolean isJavaClassEntry(final String rootEntryName, final JarEntry entry) {
        final String entryName = entry.getName();
        return entryName.startsWith(rootEntryName)
                && !entry.isDirectory()
                && entryName.endsWith(".class");
    }


    /**
     * @see #getTableMetaMap(SchemaMeta, List, ClassLoader)
     */
    private static boolean isJavaClassFile(final Path path, BasicFileAttributes attributes) {
        return !Files.isDirectory(path)
                && Files.isReadable(path)
                && path.getFileName().toString().endsWith(".class");
    }

    /**
     * @see #getTableMetaMap(SchemaMeta, List, ClassLoader)
     */
    private static <T> TableMeta<T> getOrCreateTableMeta(final String className) {
        try {
            final Class<?> clazz;
            clazz = Class.forName(className);
            @SuppressWarnings("unchecked")
            Class<T> domainClass = (Class<T>) clazz;
            return DefaultTableMeta.getTableMeta(domainClass);
        } catch (ClassNotFoundException e) {
            // no bug,never here.
            String m = String.format("Domain class[%s] not found.", className);
            throw new TableMetaLoadException(m, e);
        }

    }


    /**
     * @return if domain class return class name,or empty string
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.1">The class File Format</a>
     */
    private static String readJavaClassFile(final ByteBuffer buffer, final SchemaMeta schemaMeta) {
        // 1. read magic
        if (buffer.getInt() != 0xCAFEBABE) {
            throw classFileFormatError();
        }
        final int bit16 = 0xFFFF;
        // 2. read version
        final int major, minor;
        minor = buffer.getShort() & bit16;
        major = buffer.getShort() & bit16;
        if (major < 49 || major > 61) { // less than java 1.5 or great than java 17
            String m = String.format("class file version[%s.%s] unsupported.", major, minor);
            throw new IllegalArgumentException(m);
        }
        // 3. read constant pool
        final Item[] poolItems;
        poolItems = readConstantPool(buffer);

        // 4. read access_flags
        final int accessFlags = buffer.getShort() & bit16;
        final int ACC_PUBLIC = 0x0001, ACC_INTERFACE = 0x0200, ACC_ABSTRACT = 0x0400, ACC_ANNOTATION = 0x2000, ACC_ENUM = 0x4000;
        if ((accessFlags & (ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT | ACC_ANNOTATION | ACC_ENUM)) != ACC_PUBLIC) {
            //The class that this class file representing isn't domain class
            return "";
        }
        // 5. read this_class
        final int thisClassIndex = buffer.getShort() & bit16;
        // 6. skip super_class
        buffer.position(buffer.position() + 2); // skip supper class
        // 7. skip interfaces_count and interfaces
        final int interfaceCount = buffer.getShort() & bit16;
        if (interfaceCount > 0) {
            buffer.position(buffer.position() + (interfaceCount << 1));
        }
        // 8. skip fields_count and fields
        skipFieldOrMethod(buffer); // skip Fields
        // 9. skip methods_count and methods
        skipFieldOrMethod(buffer); // skip Methods

        final String domainClassName;
        // 10. read class file attributes_count and attributes
        if (readAttributes(buffer, poolItems, thisClassIndex, schemaMeta)) {
            // 11. get top class name of  this class file.
            final DualItem thisClassItem = (DualItem) poolItems[thisClassIndex];
            domainClassName = ((Utf8Item) poolItems[thisClassItem.index & bit16]).text.replace('/', '.');
        } else {
            // not domain class
            domainClassName = "";
        }
        return domainClassName;
    }


    /**
     * @see #readJavaClassFile(ByteBuffer, SchemaMeta)
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.4">The Constant Pool</a>
     */
    private static Item[] readConstantPool(final ByteBuffer buffer) {
        final int bit16 = 0xFFFF;
        final Item[] itemArray = new Item[buffer.getShort() & bit16];
        for (int index = 1; index < itemArray.length; index++) {
            final byte tag = buffer.get();
            switch (tag) {
                case Symbol.CONSTANT_CLASS_TAG:
                case Symbol.CONSTANT_STRING_TAG:
                    itemArray[index] = new DualItem(tag, buffer.getShort());
                    break;
                case Symbol.CONSTANT_METHOD_TYPE_TAG:
                case Symbol.CONSTANT_MODULE_TAG:
                case Symbol.CONSTANT_PACKAGE_TAG:
                    buffer.position(buffer.position() + 2); // skip
                    break;
                case Symbol.CONSTANT_METHOD_HANDLE_TAG:
                    buffer.position(buffer.position() + 3); // skip
                    break;
                case Symbol.CONSTANT_FIELDREF_TAG:
                case Symbol.CONSTANT_METHODREF_TAG:
                case Symbol.CONSTANT_INTERFACE_METHODREF_TAG:
                case Symbol.CONSTANT_INTEGER_TAG:
                case Symbol.CONSTANT_FLOAT_TAG:
                case Symbol.CONSTANT_NAME_AND_TYPE_TAG:
                case Symbol.CONSTANT_INVOKE_DYNAMIC_TAG:
                case Symbol.CONSTANT_DYNAMIC_TAG:
                    buffer.position(buffer.position() + 4); // skip
                    break;
                case Symbol.CONSTANT_LONG_TAG:
                case Symbol.CONSTANT_DOUBLE_TAG:
                    buffer.position(buffer.position() + 8); // skip
                    index++;
                    break;
                case Symbol.CONSTANT_UTF8_TAG: {
                    final int length = buffer.getShort() & bit16;
                    final byte[] bytes = new byte[length];
                    buffer.get(bytes);
                    itemArray[index] = new Utf8Item(tag, new String(bytes, StandardCharsets.UTF_8));
                }
                break;
                default: {
                    String m = String.format("Unknown tag:%s,index:%s,constantPoolCount:%s"
                            , tag, index, itemArray.length);
                    throw new IllegalArgumentException(m);
                }

            }
        }
        return itemArray;
    }


    /**
     * @return true: the class that the class file representing class is domain class.
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7">Attributes</a>
     */
    private static boolean readAttributes(final ByteBuffer buffer, final Item[] itemArray
            , final int thisClassIndex, SchemaMeta schemaMeta) {
        final String catalog = schemaMeta.catalog(), schema = schemaMeta.schema();
        final int bit16 = 0xFFFF;
        // 1. read attributes_count
        final int attributesCount = buffer.getShort() & bit16;
        final String InnerClasses = "InnerClasses", RuntimeVisibleAnnotations = "RuntimeVisibleAnnotations";

        boolean matchTableAnn = false;
        // 2. read attributes
        outFor:
        for (int i = 0, nameIndex, attrLength, attrDataIndex; i < attributesCount; i++) {
            // 2.1 read attribute_name_index
            nameIndex = buffer.getShort() & bit16;
            if (nameIndex < 1 || nameIndex >= itemArray.length) {
                throw classFileFormatError();
            }
            final Item item = itemArray[nameIndex];
            if (!(item instanceof Utf8Item)) {
                throw classFileFormatError();
            }
            // 2.2 read attribute_length
            attrLength = buffer.getInt();
            // 2.3 below read info of attribute
            attrDataIndex = buffer.position();
            if (InnerClasses.equals(((Utf8Item) item).text)) {
                final int numberOfClasses = buffer.getShort() & bit16;
                for (int j = 0; j < numberOfClasses; j++) {
                    if ((buffer.getShort() & bit16) == thisClassIndex) {
                        // this class is inner class,not domain class
                        matchTableAnn = false;
                        break outFor;
                    }
                    buffer.position(buffer.position() + 6); // skip outer_class_info_index,inner_name_index,inner_class_access_flags
                }
            } else if (!matchTableAnn && RuntimeVisibleAnnotations.equals(((Utf8Item) item).text)) {
                final int numAnnotations = buffer.getShort() & bit16;
                for (int j = 0; j < numAnnotations; j++) {
                    if (readAnnotation(buffer, itemArray, catalog, schema)) {
                        matchTableAnn = true;
                        break; // only break inner 'for' ,because possibly is inner class
                    }
                }
            }
            buffer.position(attrDataIndex + attrLength); //skip rest attribute or  avoid tail filler of attribute.

        }
        return matchTableAnn;

    }


    /**
     * @return true:the class that the class file representing is annotated by
     * {@link Table} and {@link Table#catalog()} {@link Table#schema()} value match.
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.16">annotations of The RuntimeVisibleAnnotations Attribute</a>
     */
    private static boolean readAnnotation(final ByteBuffer buffer, Item[] itemArray, final String targetCatalog, final String targetSchema) {
        final int bit16 = 0xFFFF;
        //1. read type_index for annotationName
        final String typeName = ((Utf8Item) itemArray[buffer.getShort() & bit16]).text;
        final String annotationName = typeName.substring(1, typeName.length() - 1).replace('/', '.');
        final boolean isTableAnn = Table.class.getName().equals(annotationName);

        // 2. read num_element_value_pairs
        final int numPair = buffer.getShort() & bit16;
        String catalog = null, schema = null;
        // 3. below 'for' read element_value_pairs
        for (int i = 0; i < numPair; i++) {
            // 3.1
            final String elementName = ((Utf8Item) itemArray[buffer.getShort() & bit16]).text;
            // 3.2  read value of element_value_pairs
            final String catalogOrSchema;
            catalogOrSchema = readElementValue(buffer, itemArray);
            if (!isTableAnn) {
                continue;
            }
            if (elementName.equals("catalog")) {
                catalog = catalogOrSchema;
            } else if (elementName.equals("schema")) {
                schema = catalogOrSchema;
            }
            if (catalog != null && schema != null) {
                break;
            }

        }
        final boolean match;
        if (isTableAnn) {
            final boolean catalogMatch, schemaMatch;
            catalogMatch = (catalog == null && targetCatalog.isEmpty())
                    || targetCatalog.equals(_StringUtils.toLowerCaseIfNonNull(catalog));
            schemaMatch = (schema == null && targetSchema.isEmpty())
                    || targetSchema.equals(_StringUtils.toLowerCaseIfNonNull(schema));
            match = catalogMatch && schemaMatch;
        } else {
            match = false;
        }
        return match;
    }

    /**
     * @return catalog or schema value of {@link Table}
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.16.1">The element_value structure</a>
     */
    @Nullable
    private static String readElementValue(final ByteBuffer buffer, final Item[] itemArray) {
        final int bit16 = 0xFFFF;
        // 1. read tag
        final char tag = (char) buffer.get();
        // 2. read value of element
        String catalogOrSchema = null;
        switch (tag) {
            case 's': {// String type
                catalogOrSchema = ((Utf8Item) itemArray[buffer.getShort() & bit16]).text;
            }
            break;
            case 'B': // byte type
            case 'C': // char type
            case 'D': // double type
            case 'F': // float type
            case 'I': // int type
            case 'J': // long type
            case 'S': // short type
            case 'Z': // boolean type
            case 'c': // Class type
                buffer.position(buffer.position() + 2); // skip
                break;
            case 'e': // Enum type
                buffer.position(buffer.position() + 4); // skip enum_const_value
                break;
            case '@': {// Annotation type
                // skip result
                readAnnotation(buffer, itemArray, "", "");
            }
            break;
            case '[': {// Array type
                final int numValues = buffer.getShort() & bit16;
                for (int i = 0; i < numValues; i++) {
                    // skip result
                    readElementValue(buffer, itemArray);
                }
            }
            break;
            default: {
                String m = String.format("Unknown element_value tage[%s]", tag);
                throw new IllegalArgumentException(m);
            }
        }
        return catalogOrSchema;
    }

    /**
     * @see #readJavaClassFile(ByteBuffer, SchemaMeta)
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.5">Fields</a>
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.6">Methods</a>
     */
    private static void skipFieldOrMethod(final ByteBuffer buffer) {
        final int bit16 = 0xFFFF;
        final int itemCount = buffer.getShort() & bit16;
        for (int i = 0, attributesCount; i < itemCount; i++) {
            buffer.position(buffer.position() + 6); // skip access_flags,name_index,descriptor_index
            attributesCount = buffer.getShort() & bit16;
            for (int j = 0, attributeLength; j < attributesCount; j++) {
                buffer.position(buffer.position() + 2); //skip attribute_name_index
                attributeLength = buffer.getInt(); // read attributeLength
                buffer.position(buffer.position() + attributeLength); // skip info
            }
        }
    }

    private static IllegalArgumentException classFileFormatError() {
        return new IllegalArgumentException("class file format error");
    }


    private static abstract class Item {

        final byte type;

        private Item(byte type) {
            this.type = type;
        }
    }


    private static final class DualItem extends Item {

        final short index;

        private DualItem(byte type, short index) {
            super(type);
            this.index = index;
        }
    }

    private static final class Utf8Item extends Item {

        final String text;

        private Utf8Item(byte type, String text) {
            super(type);
            this.text = text;
        }

    }

    private static abstract class Symbol {

        private Symbol() {
            throw new UnsupportedOperationException();
        }

        /** The tag value of CONSTANT_Class_info JVMS structures. */
        static final byte CONSTANT_CLASS_TAG = 7;

        /** The tag value of CONSTANT_Fieldref_info JVMS structures. */
        static final byte CONSTANT_FIELDREF_TAG = 9;

        /** The tag value of CONSTANT_Methodref_info JVMS structures. */
        static final byte CONSTANT_METHODREF_TAG = 10;

        /** The tag value of CONSTANT_InterfaceMethodref_info JVMS structures. */
        static final byte CONSTANT_INTERFACE_METHODREF_TAG = 11;

        /** The tag value of CONSTANT_String_info JVMS structures. */
        static final byte CONSTANT_STRING_TAG = 8;

        /** The tag value of CONSTANT_Integer_info JVMS structures. */
        static final byte CONSTANT_INTEGER_TAG = 3;

        /** The tag value of CONSTANT_Float_info JVMS structures. */
        static final byte CONSTANT_FLOAT_TAG = 4;

        /** The tag value of CONSTANT_Long_info JVMS structures. */
        static final byte CONSTANT_LONG_TAG = 5;

        /** The tag value of CONSTANT_Double_info JVMS structures. */
        static final byte CONSTANT_DOUBLE_TAG = 6;

        /** The tag value of CONSTANT_NameAndType_info JVMS structures. */
        static final byte CONSTANT_NAME_AND_TYPE_TAG = 12;

        /** The tag value of CONSTANT_Utf8_info JVMS structures. */
        static final byte CONSTANT_UTF8_TAG = 1;

        /** The tag value of CONSTANT_MethodHandle_info JVMS structures. */
        static final byte CONSTANT_METHOD_HANDLE_TAG = 15;

        /** The tag value of CONSTANT_MethodType_info JVMS structures. */
        static final byte CONSTANT_METHOD_TYPE_TAG = 16;

        /** The tag value of CONSTANT_Dynamic_info JVMS structures. */
        static final byte CONSTANT_DYNAMIC_TAG = 17;

        /** The tag value of CONSTANT_InvokeDynamic_info JVMS structures. */
        static final byte CONSTANT_INVOKE_DYNAMIC_TAG = 18;

        /** The tag value of CONSTANT_Module_info JVMS structures. */
        static final byte CONSTANT_MODULE_TAG = 19;

        /** The tag value of CONSTANT_Package_info JVMS structures. */
        static final byte CONSTANT_PACKAGE_TAG = 20;

    }


}
