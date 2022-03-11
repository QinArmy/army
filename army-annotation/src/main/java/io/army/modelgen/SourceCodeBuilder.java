package io.army.modelgen;

import io.army.annotation.Column;
import io.army.annotation.Index;
import io.army.annotation.Table;
import io.army.lang.Nullable;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

final class SourceCodeBuilder {

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
        final Map<String, Boolean> fieldTypeNameMap = new HashMap<>();
        int count = 0;
        boolean primary;
        for (Map.Entry<String, VariableElement> e : this.fieldMap.entrySet()) {
            field = e.getValue();
            fieldName = e.getKey();

            typeMirror = field.asType();
            fieldTypeName = types.asElement(typeMirror).getSimpleName().toString();
            qualifiedName = typeMirror.toString();

            switch (typeMirror.getKind()) {
                case ARRAY:
                    addImport(qualifiedName, fieldTypeNameMap, builder);
                    break;
                case DECLARED: {
                    if (!isSamePackage(tableElement, (TypeElement) types.asElement(typeMirror))) {
                        addImport(qualifiedName, fieldTypeNameMap, builder);
                    }
                }
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
                case NONE:
                    methodName = "getField";
                    metaTypeName = "FieldMeta";
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

    private void addImport(final String qualifiedName, final Map<String, Boolean> fieldTypeNameMap
            , final StringBuilder builder) {

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
                        typeName = null;
                        break;
                    default:
                        // no-op
                }
            }
            break;
            case '>':
                typeName = qualifiedName.substring(0, qualifiedName.indexOf('<'));
                break;
            default:
                typeName = qualifiedName;
        }

        if (typeName != null && !typeName.startsWith("java.lang.")
                && fieldTypeNameMap.putIfAbsent(typeName, Boolean.TRUE) == null) {
            builder.append("import ")
                    .append(typeName)
                    .append(";\n");
        }

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


}
