package io.army.criteria.impl;

import io.army.annotation.Table;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util.StringUtils;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public abstract class TableMetaFactory {

    private TableMetaFactory() {
        throw new UnsupportedOperationException();
    }

    public static <T extends IDomain> TableMeta<T> getTableMeta(Class<T> domainClass) {
        return DefaultTableMeta.getTableMeta(domainClass);
    }

    public static <T extends IDomain> SimpleTableMeta<T> getSimpleTableMeta(Class<T> domainClass) {
        return DefaultTableMeta.getSimpleTableMeta(domainClass);
    }

    public static <T extends IDomain> ParentTableMeta<T> getParentTableMeta(Class<T> domainClass) {
        return DefaultTableMeta.getParentTableMeta(domainClass);
    }

    public static <T extends IDomain> ChildTableMeta<T> getChildTableMeta(Class<T> domainClass) {
        return DefaultTableMeta.getChildTableMeta(domainClass);
    }

    public static <S extends IDomain, T extends S> ChildTableMeta<T> getChildTableMeta(
            ParentTableMeta<S> parentTableMeta, Class<T> domainClass) {
        return DefaultTableMeta.getChildTableMeta(parentTableMeta, domainClass);
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
        try {
            final Map<Class<?>, TableMeta<?>> tableMetaMap = new HashMap<>();
            for (String basePackage : basePackages) {
                //1. convert base package
                if (basePackage.charAt(0) == '/') {
                    basePackage = basePackage.substring(1);
                }
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
                    Files.find(Paths.get(enumeration.nextElement().getPath()), 2048, TableMetaFactory::isJavaClassFile)
                            .map(javaClassFilePath -> readJavaClassFile(javaClassFilePath, schemaMeta)) // read java class file
                            .filter(StringUtils::hasText) // if empty string ,not domain class
                            .map(TableMetaFactory::getTableMeta)// get or create table meta
                            .forEach(tableMeta -> tableMetaMap.put(tableMeta.javaType(), tableMeta));
                }
            }
            return Collections.unmodifiableMap(tableMetaMap);
        } catch (Exception e) {
            throw new TableMetaLoadException(e.getMessage(), e);
        } finally {
            TableMetaUtils.clearCache();
        }
    }

    public static Set<FieldMeta<?, ?>> codecFieldMetaSet() {
        return DefaultFieldMeta.codecFieldMetaSet();
    }


    private static boolean isJavaClassFile(final Path path, BasicFileAttributes attributes) {
        return !Files.isDirectory(path)
                && Files.isReadable(path)
                && path.getFileName().toString().endsWith(".class");
    }


    private static <T extends IDomain> TableMeta<T> getTableMeta(final String className) {
        try {
            final Class<?> clazz;
            clazz = Class.forName(className);
            if (!IDomain.class.isAssignableFrom(clazz)) {
                String m = String.format("class[%s] not implements %s"
                        , className, IDomain.class.getName());
                throw new TableMetaLoadException(m);
            }
            @SuppressWarnings("unchecked")
            Class<T> domainClass = (Class<T>) clazz;
            return DefaultTableMeta.getTableMeta(domainClass);
        } catch (ClassNotFoundException e) {
            // no bug,never here.
            throw new RuntimeException(e);
        }

    }


    /**
     * @return if domain class return class name,or empty string
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.1">The class File Format</a>
     */
    private static String readJavaClassFile(final Path classFilePath, final SchemaMeta schemaMeta) {

        try (FileChannel channel = FileChannel.open(classFilePath)) {
            final long fileSize = channel.size();
            if (fileSize > (Integer.MAX_VALUE - 32)) {
                String m = String.format("Class file[%s] too large,don't support read.", classFilePath);
                throw new IllegalArgumentException(m);
            }
            final ByteBuffer buffer = ByteBuffer.wrap(new byte[(int) fileSize]);
            if (channel.read(buffer) < 10) {
                throw classFileFormatError();
            }
            buffer.flip();

            // 1. read magic
            if (buffer.getInt() != 0xCAFEBABE) {
                throw classFileFormatError();
            }
            // 2. read version
            assertSupportClassFileVersion(buffer, classFilePath);
            // 3. read constant pool
            final Item[] poolItems;
            poolItems = readConstantPool(buffer);

            final int bit16 = 0xFFFF;
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
        } catch (Exception e) {
            String m = String.format("%s,class file[%s]", e.getMessage(), classFilePath);
            throw new TableMetaLoadException(m, e);
        }

    }


    /**
     * @see #readJavaClassFile(Path, SchemaMeta)
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.4">The Constant Pool</a>
     */
    private static Item[] readConstantPool(final ByteBuffer buffer) {
        final int bit16 = 0xFFFF;
        final int constantPoolCount = buffer.getShort() & bit16;
        final Item[] itemArray = new Item[constantPoolCount];
        for (int index = 1; index < constantPoolCount; index++) {
            final byte tag = buffer.get();
            switch (tag) {
                case Symbol.CONSTANT_CLASS_TAG:
                case Symbol.CONSTANT_STRING_TAG:
                case Symbol.CONSTANT_METHOD_TYPE_TAG:
                case Symbol.CONSTANT_MODULE_TAG:
                case Symbol.CONSTANT_PACKAGE_TAG:
                    itemArray[index] = new DualItem(tag, buffer.getShort());
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
                default:
                    throw new IllegalArgumentException(String.format("Unknown tag:%s,index:%s,constantPoolCount:%s", tag, index, constantPoolCount));
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
                    || targetCatalog.equals(StringUtils.toLowerCase(catalog));
            schemaMatch = (schema == null && targetSchema.isEmpty())
                    || targetSchema.equals(StringUtils.toLowerCase(schema));
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
     * @see #readJavaClassFile(Path, SchemaMeta)
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

    private static void assertSupportClassFileVersion(ByteBuffer buffer, Path path) {
        buffer.position(buffer.position() + 4);
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
