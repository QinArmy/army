package io.army.modelgen;

import io.army.annotation.Column;
import io.army.annotation.Index;
import io.army.annotation.Table;
import io.army.lang.Nullable;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;

final class SourceCodeBuilder {

    private static final String JAVA_LANG = "java.lang.";

    private static final Map<String, TypeWrapper> JDK_TYPE_MAP;

    private static final Map<String, String> JDK_SIMPLE_MAP;

    static {

        final Map<String, TypeWrapper> jdkTypeMap;
        jdkTypeMap = createJdkTypeWrapperMap();
        JDK_TYPE_MAP = jdkTypeMap;

        final Map<String, String> jdkSimpleMap = new HashMap<>();
        for (TypeWrapper wrapper : jdkTypeMap.values()) {
            if (wrapper.qualifiedName == null) {
                if (jdkSimpleMap.putIfAbsent(wrapper.simpleName, JAVA_LANG + wrapper.simpleName) != null) {
                    throw new IllegalStateException("JDK_TYPE_MAP create error.");
                }
            } else if (jdkSimpleMap.putIfAbsent(wrapper.simpleName, wrapper.qualifiedName) != null) {
                throw new IllegalStateException("JDK_TYPE_MAP create error.");
            }
        }
        JDK_SIMPLE_MAP = Collections.unmodifiableMap(jdkSimpleMap);


    }

    private final Types types;

    private final Filer filer;

    private TypeElement tableElement;

    private Map<String, VariableElement> fieldMap;

    private Map<String, IndexMode> fieldToIndexMode;

    private TypeElement parentElement;

    private String packageName;

    private String className;

    private StringBuilder builder;

    private List<String> errorMsgList;

    private MappingMode mappingMode;

    private Set<String> fieldTypeNameSet;

    private Map<String, String> fieldTypeMap;

    SourceCodeBuilder(Types types, Filer filer) {
        this.types = types;
        this.filer = filer;
    }


    SourceCodeBuilder reset(final TypeElement tableElement, final Map<String, VariableElement> fieldMap
            , final @Nullable TypeElement parentElement, final MappingMode mappingMode) {
        this.tableElement = tableElement;
        this.fieldMap = fieldMap;
        this.parentElement = parentElement;
        this.className = MetaUtils.getClassName(tableElement);

        this.packageName = MetaUtils.getPackageName(tableElement);
        this.builder = new StringBuilder(1024);
        this.fieldToIndexMode = getFieldToIndexModeMap(tableElement);
        this.errorMsgList = null;

        this.mappingMode = mappingMode;
        return this;
    }

    void build() throws IOException {
        for (VariableElement field : fieldMap.values()) {
            TypeMirror mirror = field.asType();


            if (!(mirror instanceof ArrayType)) {
                continue;
            }
            System.out.printf("mirror:%s%n", mirror);
            System.out.printf("%s%n", mirror.getClass().getName());
            ArrayType arrayType = (ArrayType) mirror;
            TypeMirror m = arrayType.getComponentType();
            System.out.printf("component:%s%n", m);
            System.out.printf("class:%s%n", m.getClass().getName());
//            DeclaredType declaredType = (DeclaredType) mirror;
//            System.out.printf("declaredType:%s%n", declaredType);
//
//            Element element = declaredType.asElement();
//            System.out.printf("element:%s%n", element);
//
//            TypeElement typeElement = (TypeElement) element;
//            System.out.printf("typeElement:%s%n", typeElement);

        }
    }

    void build0() throws IOException {
        final StringBuilder builder = this.builder;
        // 1. source package part
        builder.append("package ")
                .append(this.packageName)
                .append(";\n\n");

        // 2. source army import part
        appendArmyClassImport(builder);

        final TypeElement tableElement = this.tableElement, parentElement = this.parentElement;
        // 3. source parentMeta class import part
        if (parentElement != null) {
            appendParentClassImport(builder, tableElement, parentElement);
        }

        VariableElement field;
        TypeMirror typeMirror;
        String qualifiedName, fieldName, commentLine, fieldTypeName, upperCaseFieldName, methodName, metaTypeName;
        final Types types = this.types;
        final StringBuilder fieldNameBuilder = new StringBuilder(256);
        final StringBuilder fieldBuilder = new StringBuilder(256);
        final Map<String, IndexMode> fieldIndexModeMap = this.fieldToIndexMode;
        final String domainName = MetaUtils.getSimpleClassName(tableElement);
        StringBuilder commentBuilder;
        final String[] parameters = new String[2];
        int count = 0;
        boolean primary;
        for (Map.Entry<String, VariableElement> e : this.fieldMap.entrySet()) {
            field = e.getValue();
            fieldName = e.getKey();

            typeMirror = field.asType();
            fieldTypeName = types.asElement(typeMirror).getSimpleName().toString();

            switch (typeMirror.getKind()) {
                case ARRAY:
                    addImport(typeMirror.toString(), parameters);
                    break;
                case DECLARED: {
                    if (!isSamePackage(tableElement, (TypeElement) types.asElement(typeMirror))) {
                        addImport(typeMirror.toString(), parameters);
                    }
                }
                break;
                default://no-op
            }
            commentBuilder = new StringBuilder()
                    .append(SourceCreateUtils.COMMENT_PRE)
                    .append("/**  ")
                    .append(getComment(field))
                    .append(" */\n");

            commentLine = commentBuilder.toString();

            // field name definition
            upperCaseFieldName = _MetaBridge.camelToUpperCase(fieldName);
            fieldNameBuilder
                    .append(commentLine)
                    .append(SourceCreateUtils.PROP_PRE)
                    .append(" String ")
                    .append(upperCaseFieldName)
                    .append(" = \"")
                    .append(fieldName)
                    .append("\";\n");

            count++;
            if ((count & 3) == 0) {
                fieldNameBuilder.append('\n');
            }

            // field definitions
            fieldBuilder
                    .append(commentLine)
                    .append(SourceCreateUtils.PROP_PRE);
            primary = false;
            switch (fieldIndexModeMap.getOrDefault(fieldName, IndexMode.NONE)) {
                case NONE:
                    methodName = "getField";
                    metaTypeName = "FieldMeta";
                    break;
                case GENERIC:
                    metaTypeName = "FieldMeta";
                    methodName = "getField";
                    break;
                case UNIQUE:
                    methodName = "getUniqueField";
                    metaTypeName = "UniqueFieldMeta";
                    break;
                case PRIMARY:
                    methodName = "id";
                    metaTypeName = "PrimaryFieldMeta";
                    primary = true;
                    break;
                default: {
                    IndexMode mode = fieldIndexModeMap.getOrDefault(fieldName, IndexMode.NONE);
                    throw new IllegalArgumentException(String.format("IndexMode[%s] unknown", mode));
                }

            }
            fieldBuilder.append(metaTypeName)
                    .append('<')
                    .append(domainName)
                    .append(',')
                    .append(fieldTypeName)
                    .append("> ")
                    .append(fieldName)
                    .append(" = ")
                    .append(_MetaBridge.TABLE_META)
                    .append('.')
                    .append(methodName)
                    .append('(');

            if (!primary) {
                fieldBuilder
                        .append(upperCaseFieldName)
                        .append(',');
            }
            fieldBuilder.append(fieldTypeName)
                    .append(".class);\n");

            if ((count & 3) == 0) {
                fieldBuilder.append('\n');
            }

        }


        builder.append("\n\n");
        final FileObject fileObject;
        fileObject = this.filer.createSourceFile(this.className + _MetaBridge.META_CLASS_NAME_SUFFIX);
        try (PrintWriter pw = new PrintWriter(fileObject.openOutputStream())) {
            pw.println(builder);
        }

    }

    private String addImport(final String qualifiedName, final String[] parameters) {

        String typeName;
        switch (qualifiedName.charAt(qualifiedName.length() - 1)) {
            case ']': {
                typeName = qualifiedName.substring(0, qualifiedName.indexOf('['));
                switch (typeName) {
                    case "boolean":
                    case "byte":
                    case "char":
                    case "double":
                    case "float":
                    case "int":
                    case "long":
                    case "short":
                    case "void":
                        return qualifiedName;
                    default: {
                        // no-op
                        if (typeName.lastIndexOf('>') > 0) {
                            String m = String.format("%s exists array of Generics.", this.className);
                            throw new AnnotationMetaException(m);
                        }
                    }

                }
            }
            break;
            case '>': {
                typeName = qualifiedName.substring(0, qualifiedName.indexOf('<'));
            }
            break;
            default:
                typeName = qualifiedName;
        }

        final String simpleTypeName;
        int index;
        index = typeName.lastIndexOf('.');
        if (index > 0) {
            simpleTypeName = typeName.substring(0, index);
        } else {
            simpleTypeName = typeName;
        }
        if (!typeName.startsWith("java.lang.") && fieldTypeNameSet.contains(typeName)) {
            builder.append("import ")
                    .append(typeName)
                    .append(";\n");
            fieldTypeNameSet.add(typeName);
        }
        return "";
    }

    private Map<String, IndexMode> getFieldToIndexModeMap(final TypeElement tableElement) {
        final Table table = tableElement.getAnnotation(Table.class);
        final Index[] indexArray = table.indexes();
        final Map<String, IndexMode> indexMetaMap = new HashMap<>();

        final Map<String, Boolean> indexNameMap = new HashMap<>(indexArray.length + 3);
        String[] fieldArray;
        for (Index index : indexArray) {
            // make index name lower case
            final String indexName = index.name().toLowerCase(Locale.ROOT);
            if (indexNameMap.putIfAbsent(indexName, Boolean.TRUE) != null) {
                String m = String.format("Domain %s index name[%s] duplication",
                        tableElement.getQualifiedName(), indexName);
                addErrorMsg(m);
            }
            final IndexMode indexMode = IndexMode.resolve(index);
            for (String fieldName : index.fieldList()) {
                fieldArray = fieldName.split(" ");
                indexMetaMap.put(fieldArray[0], indexMode);
            }
        }
        indexMetaMap.put(_MetaBridge.ID, IndexMode.PRIMARY);
        return Collections.unmodifiableMap(indexMetaMap);
    }

    private void addErrorMsg(String msg) {
        List<String> errorMsgList = this.errorMsgList;
        if (errorMsgList == null) {
            errorMsgList = new ArrayList<>();
        }
        errorMsgList.add(msg);
    }

    private void appendArmyClassImport(final StringBuilder builder) {

        builder.append("import io.army.meta.FieldMeta;\n")
                .append("import javax.annotation.Generated;\n")
                .append("import io.army.criteria.impl._TableMetaFactory;\n")
                .append("import io.army.meta.PrimaryFieldMeta;\n\n");

        boolean hasIndex, hasUnique;
        hasIndex = hasUnique = false;
        for (IndexMode mode : this.fieldToIndexMode.values()) {
            switch (mode) {
                case UNIQUE:
                    hasUnique = true;
                    break;
                case GENERIC:
                    hasIndex = true;
                    break;
            }
        }

        if (hasIndex) {
            builder.append("import io.army.meta.IndexFieldMeta;\n");
        }

        if (hasUnique) {
            builder.append("import io.army.meta.UniqueFieldMeta;\n");
        }

        switch (this.mappingMode) {
            case SIMPLE:
                builder.append("import io.army.meta.SimpleTableMeta;\n");
                break;
            case CHILD:
                builder.append("import io.army.meta.ChildTableMeta;\n");
                break;
            case PARENT:
                builder.append("import io.army.meta.ParentTableMeta;\n");
                break;
            default:
                //no-op
        }

    }


    private void appendParentClassImport(final StringBuilder builder, final TypeElement tableElement
            , final TypeElement parentElement) {
        if (!isSameClassName(tableElement, parentElement) && !isSamePackage(tableElement, parentElement)) {
            builder.append("import ");
            builder.append(MetaUtils.getSimpleClassName(parentElement));
            builder.append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                    .append(";\n");
        }

    }

    static boolean isSameClassName(TypeElement element1, TypeElement element2) {
        return element1.getSimpleName().equals(element2.getSimpleName());
    }

    static boolean isSamePackage(TypeElement element1, TypeElement element2) {
        return element1.getQualifiedName().equals(element2.getQualifiedName());
    }

    private String getComment(final VariableElement field) {
        final Column column;
        column = field.getAnnotation(Column.class);
        String comment;
        comment = column.comment();
        if (!Strings.hasText(comment)) {
            switch (field.getSimpleName().toString()) {
                case _MetaBridge.ID:
                    comment = "primary key";
                    break;
                case _MetaBridge.CREATE_TIME:
                    comment = "create time";
                    break;
                case _MetaBridge.UPDATE_TIME:
                    comment = "update time";
                    break;
                case _MetaBridge.VERSION:
                    comment = "version for optimistic lock";
                    break;
                case _MetaBridge.VISIBLE:
                    comment = "visible for logic delete";
                    break;
                default: {
                    final TypeMirror fieldType = field.asType();
                    if (isEnum(fieldType)) {
                        comment = "@see " + fieldType;
                    }
                }

            }
        }
        return comment;
    }

    private boolean isEnum(final TypeMirror fieldType) {
        return fieldType.getKind() == TypeKind.DECLARED
                && this.types.asElement(fieldType).getKind() == ElementKind.ENUM;
    }

    private TypeWrapper getTypeWrapper(final String fieldName, final TypeMirror qualifiedFieldMirror) {


        final TypeMirror fieldMirror;
        TypeWrapper wrapper;
        if (qualifiedFieldMirror instanceof ArrayType) {
            TypeMirror tempMirror = qualifiedFieldMirror;
            while (tempMirror instanceof ArrayType) {
                tempMirror = ((ArrayType) tempMirror).getComponentType();
            }
            fieldMirror = tempMirror;
        } else if (qualifiedFieldMirror instanceof DeclaredType) {
            final DeclaredType declaredType = (DeclaredType) qualifiedFieldMirror;
            final List<? extends TypeMirror> argumentTypeList;
            argumentTypeList = declaredType.getTypeArguments();
            fieldMirror = qualifiedFieldMirror;
            switch (argumentTypeList.size()) {
                case 0: {
                    final String qualifiedName;
                    qualifiedName = qualifiedFieldMirror.toString();
                    final TypeWrapper tempWrapper;
                    tempWrapper = JDK_TYPE_MAP.get(qualifiedName);
                    if (tempWrapper == null) {
                        wrapper = createSimpleTypeWrapper(qualifiedFieldMirror);
                    } else {
                        wrapper = tempWrapper;
                    }
                }
                break;
                case 1: {
                    final TypeWrapper oneWrapper;
                    oneWrapper = getTypeWrapper(fieldName, argumentTypeList.get(0));
                    final TypeElement fieldTypeElement;
                    fieldTypeElement = (TypeElement) ((DeclaredType) qualifiedFieldMirror).asElement();
                    if (isSamePackage(fieldTypeElement, this.parentElement)) {

                    } else {

                    }
                }
                break;
                case 2:
                    break;
                default: {
                    String m = String.format("%s %s have %s type arguments, that isn't supported by army."
                            , this.className, fieldName, argumentTypeList.size());
                    throw new AnnotationMetaException(m);
                }
            }

        } else {
            String m;
            m = String.format("%s exists %s that isn't supported by army.", this.className, qualifiedFieldMirror);
            throw new AnnotationMetaException(m);
        }

        String qualifiedName;
        qualifiedName = fieldMirror.toString();

        wrapper = JDK_TYPE_MAP.get(qualifiedName);
        return null;
    }

    private TypeWrapper getNoArgumentWrapper(final DeclaredType fieldType) {
        final TypeElement element;
        element = (TypeElement) fieldType.asElement();
        String qualifiedName;
        qualifiedName = element.getQualifiedName().toString();

        TypeWrapper wrapper;
        wrapper = JDK_TYPE_MAP.get(qualifiedName);
        if (wrapper != null) {
            return wrapper;
        }

        String simpleName, oldQualifiedName;
        simpleName = element.getSimpleName().toString();

        if ((oldQualifiedName = JDK_SIMPLE_MAP.putIfAbsent(simpleName, qualifiedName)) != null
                && !oldQualifiedName.equals(qualifiedName)) {
            simpleName = qualifiedName;
            qualifiedName = null;
        }

        if (qualifiedName != null && (isSamePackage(this.tableElement, element) || isJavaLange(qualifiedName))) {
            qualifiedName = null;
        }
        return new TypeWrapper(qualifiedName, simpleName);
    }

    private TypeWrapper getArrayComponentWrapper(final ArrayType arrayType) {
        TypeMirror mirror = arrayType;
        int dimension = 0;
        while (mirror instanceof ArrayType) {
            mirror = ((ArrayType) mirror).getComponentType();
            dimension++;
        }

        String qualifiedName, simpleName;
        if (mirror instanceof PrimitiveType) {
            simpleName = mirror.toString();
            qualifiedName = null;
        } else if (!(mirror instanceof DeclaredType)) {
            String m = String.format("unexpected array component type[%s] in %s.", arrayType, this.className);
            throw new AnnotationMetaException(m);
        } else if (((DeclaredType) mirror).getTypeArguments().size() == 0) {
            final TypeElement element = (TypeElement) ((DeclaredType) mirror).asElement();
            qualifiedName = element.getQualifiedName().toString();
            simpleName = element.getSimpleName().toString();

            final String oldQualifiedName;
            if (!JDK_TYPE_MAP.containsKey(qualifiedName)
                    && (oldQualifiedName = JDK_SIMPLE_MAP.putIfAbsent(simpleName, qualifiedName)) != null
                    && !oldQualifiedName.equals(qualifiedName)) {
                simpleName = qualifiedName;
                qualifiedName = null;
            }
            if (qualifiedName != null && (isJavaLange(qualifiedName) || isSamePackage(this.tableElement, element))) {
                qualifiedName = null;
            }
        } else {
            String m = String.format("Generics array[%s] isn't supported by army,%s.", arrayType, this.className);
            throw new AnnotationMetaException(m);
        }
        final StringBuilder builder = new StringBuilder(simpleName.length() + (dimension << 1));
        builder.append(simpleName);
        for (int i = 0; i < dimension; i++) {
            builder.append("[]");
        }
        return new TypeWrapper(qualifiedName, builder.toString());
    }

    private TypeWrapper getWrapper(TypeMirror mirror) {
        throw new UnsupportedOperationException();
    }

    private TypeWrapper getArgumentWrapper(final DeclaredType fieldType) {
        final TypeElement element = (TypeElement) fieldType.asElement();

        String qualifiedName, simpleName;
        qualifiedName = element.getQualifiedName().toString();
        simpleName = element.getSimpleName().toString();

        final List<? extends TypeMirror> argumentList = fieldType.getTypeArguments();
        final int argumentSize = argumentList.size();
        final StringBuilder builder = new StringBuilder(simpleName.length() + argumentSize * 7)
                .append(simpleName)
                .append('<');

        final List<String> qualifiedNameList = new ArrayList<>();
        TypeWrapper w;
        int count = 0;
        for (int i = 0; i < argumentSize; i++) {
            TypeMirror mirror;
            mirror = argumentList.get(i);
            String argSimple;
            if (mirror instanceof DeclaredType) {
                w = getNoArgumentWrapper((DeclaredType) mirror);
                qualifiedNameList.add(w.qualifiedName);
                argSimple = w.simpleName;
            } else if (mirror instanceof ArrayType) {
                w = getArrayComponentWrapper((ArrayType) mirror);
                qualifiedNameList.add(w.qualifiedName);
                argSimple = w.simpleName;
            } else if (mirror instanceof TypeVariable) {
                qualifiedNameList.add(null);

            } else if (mirror instanceof WildcardType) {

            } else {
                String m = String.format("unexpected %s type[%s]"
                        , TypeMirror.class.getName(), mirror.getClass().getName());
                throw new AnnotationMetaException(m);
            }
            if (i > 0) {
                builder.append(',');
            }
            //builder.append(w.simpleName);
            count++;
        }
        builder.append('>');

        return null;
    }


    private TypeWrapper createSimpleTypeWrapper(final TypeMirror fieldMirror) {
        final TypeElement fieldElement;
        fieldElement = (TypeElement) ((DeclaredType) fieldMirror).asElement();
        final TypeWrapper wrapper;
        if (isSamePackage(fieldElement, this.tableElement)) {
            wrapper = new TypeWrapper("", fieldElement.getSimpleName().toString());
        } else {
            wrapper = new TypeWrapper(fieldElement.getQualifiedName().toString()
                    , fieldElement.getSimpleName().toString());
        }
        return wrapper;
    }


    private static boolean isJavaLange(final String qualifiedName) {
        final int index;
        index = qualifiedName.lastIndexOf('.');
        return index > 0 && qualifiedName.substring(0, index).equals("java.lange");
    }


    private static Map<String, TypeWrapper> createJdkTypeWrapperMap() {
        final Map<String, TypeWrapper> map = new HashMap<>((int) (17 / 0.75F));

        map.put(Integer.class.getName(), new TypeWrapper(null, Integer.class.getSimpleName()));
        map.put(Long.class.getName(), new TypeWrapper(null, Long.class.getSimpleName()));
        map.put(Byte.class.getName(), new TypeWrapper(null, Byte.class.getSimpleName()));
        map.put(Short.class.getName(), new TypeWrapper(null, Short.class.getSimpleName()));

        map.put(Float.class.getName(), new TypeWrapper(null, Float.class.getSimpleName()));
        map.put(Double.class.getName(), new TypeWrapper(null, Double.class.getSimpleName()));
        map.put(String.class.getName(), new TypeWrapper(null, String.class.getSimpleName()));
        map.put(Boolean.class.getName(), new TypeWrapper(null, Boolean.class.getSimpleName()));

        map.put(BigDecimal.class.getName(), new TypeWrapper(BigDecimal.class.getName(), BigDecimal.class.getSimpleName()));
        map.put(BigInteger.class.getName(), new TypeWrapper(BigInteger.class.getName(), BigInteger.class.getSimpleName()));
        map.put(LocalDate.class.getName(), new TypeWrapper(LocalDate.class.getName(), LocalDate.class.getSimpleName()));
        map.put(LocalDateTime.class.getName(), new TypeWrapper(LocalDateTime.class.getName(), LocalDateTime.class.getSimpleName()));

        map.put(LocalTime.class.getName(), new TypeWrapper(LocalTime.class.getName(), LocalTime.class.getSimpleName()));
        map.put(OffsetDateTime.class.getName(), new TypeWrapper(OffsetDateTime.class.getName(), OffsetDateTime.class.getSimpleName()));
        map.put(ZonedDateTime.class.getName(), new TypeWrapper(ZonedDateTime.class.getName(), ZonedDateTime.class.getSimpleName()));
        map.put(OffsetTime.class.getName(), new TypeWrapper(OffsetTime.class.getName(), OffsetTime.class.getSimpleName()));

        map.put(Year.class.getName(), new TypeWrapper(Year.class.getName(), Year.class.getSimpleName()));
        map.put(YearMonth.class.getName(), new TypeWrapper(YearMonth.class.getName(), YearMonth.class.getSimpleName()));
        map.put(MonthDay.class.getName(), new TypeWrapper(MonthDay.class.getName(), MonthDay.class.getSimpleName()));
        map.put(UUID.class.getName(), new TypeWrapper(UUID.class.getName(), UUID.class.getSimpleName()));


        return Collections.unmodifiableMap(map);
    }

    private static final class TypeWrapper {

        private final String qualifiedName;

        private final String simpleName;

        private TypeWrapper(@Nullable String qualifiedName, String simpleName) {
            this.qualifiedName = qualifiedName;
            this.simpleName = simpleName;
        }

    }


}
