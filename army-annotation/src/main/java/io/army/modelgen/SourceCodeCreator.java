package io.army.modelgen;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.lang.Nullable;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.time.temporal.ChronoField.*;

final class SourceCodeCreator {

    private static final String MEMBER_PRE = "    ";
    private static final String COMMENT_PREFIX = MEMBER_PRE;
    private static final String FIELD_PREFIX = MEMBER_PRE + "public static final ";
    private static final String ANNOTATION_PRE = "        ";

    private static final DateTimeFormatter ISO_OFFSET_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')

            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)

            .optionalStart()
            .appendFraction(MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()

            .appendOffset("+HH:MM:ss", "+00:00")
            .toFormatter(Locale.ENGLISH);


    private final Filer filer;

    SourceCodeCreator(Filer filer) {
        this.filer = filer;
    }

    void create(final TypeElement element, final Collection<VariableElement> fieldCollection
            , final @Nullable TypeElement parentElement, final MappingMode mode
            , final Map<String, IndexMode> indexModeMap) throws IOException {

        final StringBuilder builder = new StringBuilder(1024);
        // 1. source army import part
        appendImports(element, builder, mode);

        // 3. source parentMeta class import part
        if (parentElement != null) {
            appendParentClassImport(builder, element, parentElement);
        }

        final StringBuilder fieldNameBuilder = new StringBuilder(256);

        appendClassDefinition(element, parentElement, fieldNameBuilder);
        appendTableMeta(element, fieldNameBuilder, parentElement);
        appendMetaCount(element, fieldNameBuilder, fieldCollection.size());

        final String className;
        className = element.getSimpleName().toString();
        appendFieldCountValidateMethod(className, fieldNameBuilder);

        String fieldName, commentLine, upperCaseFieldName, methodName, metaTypeName;

        final StringBuilder fieldBuilder = new StringBuilder(256);


        final String domainName = MetaUtils.getSimpleClassName(element);
        StringBuilder commentBuilder;
        int count = 0;
        boolean primary = false, hasIndex = false, hasUnique = false;

        for (VariableElement field : fieldCollection) {
            fieldName = field.getSimpleName().toString();

            commentBuilder = new StringBuilder()
                    .append(COMMENT_PREFIX)
                    .append("/**  ")
                    .append(getComment(field))
                    .append(" */\n");

            commentLine = commentBuilder.toString();

            // field name definition
            upperCaseFieldName = _MetaBridge.camelToUpperCase(fieldName);
            fieldNameBuilder
                    .append(commentLine)
                    .append(FIELD_PREFIX)
                    .append("String ")
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
                    .append(FIELD_PREFIX);
            switch (indexModeMap.getOrDefault(fieldName, IndexMode.NONE)) {
                case NONE:
                    methodName = "getField";
                    metaTypeName = "FieldMeta";
                    break;
                case GENERIC: {
                    metaTypeName = "FieldMeta";
                    methodName = "getField";
                    if (!hasIndex) {
                        hasIndex = true;
                        builder.append("import io.army.meta.IndexFieldMeta;\n");
                    }
                }
                break;
                case UNIQUE: {
                    methodName = "getUniqueField";
                    metaTypeName = "UniqueFieldMeta";
                    if (!hasUnique) {
                        hasUnique = true;
                        builder.append("import io.army.meta.UniqueFieldMeta;\n");
                    }
                }
                break;
                case PRIMARY:
                    methodName = "id";
                    metaTypeName = "PrimaryFieldMeta";
                    primary = true;
                    break;
                default: {
                    IndexMode indexMode = indexModeMap.getOrDefault(fieldName, IndexMode.NONE);
                    throw new IllegalArgumentException(String.format("IndexMode[%s] unknown", indexMode));
                }

            }
            fieldBuilder
                    .append(metaTypeName)
                    .append('<')
                    .append(domainName)
                    .append("> ")
                    .append(fieldName)
                    .append(" = ")
                    .append(_MetaBridge.TABLE_META)
                    .append('.')
                    .append(methodName)
                    .append('(');

            if (primary) {
                primary = false;
            } else {
                fieldBuilder.append(upperCaseFieldName);
            }
            fieldBuilder.append(");\n");

            if ((count & 3) == 0) {
                fieldBuilder.append('\n');
            }

        }// for


        builder.append(fieldNameBuilder)
                .append("\n\n")
                .append(fieldBuilder)
                .append("\n\n}\n\n\n");

        final FileObject fileObject;
        fileObject = this.filer.createSourceFile(className + _MetaBridge.META_CLASS_NAME_SUFFIX);
        try (PrintWriter pw = new PrintWriter(fileObject.openOutputStream())) {
            pw.println(builder);
        }

    }


    private static void appendImports(final TypeElement element, final StringBuilder builder, final MappingMode mode) {

        builder.append("package ")
                .append(((PackageElement) element.getEnclosingElement()).getQualifiedName())
                .append(";\n\n");

        builder.append("import io.army.meta.FieldMeta;\n")
                .append("import javax.annotation.Generated;\n")
                .append("import io.army.criteria.impl._TableMetaFactory;\n")
                .append("import io.army.meta.PrimaryFieldMeta;\n\n");

        switch (mode) {
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
        if (!MetaUtils.hasText(comment)) {
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
        return fieldType instanceof DeclaredType
                && ((DeclaredType) fieldType).asElement().getKind() == ElementKind.ENUM;
    }


    /**
     * debugSQL meta source code class definition part
     */
    private static void appendClassDefinition(final TypeElement element, @Nullable final TypeElement parentElement
            , final StringBuilder builder) {

        final String className = element.getSimpleName().toString();

        builder.append("@Generated(value = \"")
                .append(ArmyMetaModelDomainProcessor.class.getName())
                .append("\"\n")
                .append(ANNOTATION_PRE)
                .append(",date = \"")
                .append(OffsetDateTime.now().format(ISO_OFFSET_DATETIME_FORMATTER))
                .append("\"\n")
                .append(ANNOTATION_PRE)
                .append(",comments = \"")
                .append(element.getAnnotation(Table.class).comment())
                .append("\")")
                .append("\n")
                .append("public abstract class ")
                .append(className)
                .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
        ;

        if (parentElement != null) {
            builder.append(" extends ");
            if (isSameClassName(element, parentElement)) {
                builder.append(parentElement.getQualifiedName());
            } else {
                builder.append(parentElement.getSimpleName());
            }
            builder.append(_MetaBridge.META_CLASS_NAME_SUFFIX);
        }
        builder.append(" {\n\n");

        // append default Constructor
        builder.append(MEMBER_PRE);
        if (parentElement == null && element.getAnnotation(Inheritance.class) != null) {
            // parent domain
            builder.append("protected ").
                    append(className)
                    .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                    .append("(){\n")
                    .append(MEMBER_PRE)
                    .append("\tthrow new UnsupportedOperationException();\n")
                    .append(MEMBER_PRE)
                    .append('}');

        } else if (parentElement == null) {
            // simple domain
            builder.append("private ").
                    append(className)
                    .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                    .append("(){\n")
                    .append(MEMBER_PRE)
                    .append("\tthrow new UnsupportedOperationException();\n")
                    .append(MEMBER_PRE)
                    .append('}');
        } else {
            // child domain
            builder.append("private ").
                    append(className)
                    .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                    .append("(){\n")
                    .append(MEMBER_PRE)
                    .append('}');
        }
        builder.append("\n\n");
    }

    private static void appendTableMeta(final TypeElement element, final StringBuilder builder,
                                        @Nullable final TypeElement parentElement) {

        final String parentClassName, methodName, tableMetaName;
        if (parentElement == null) {
            if (element.getAnnotation(Inheritance.class) == null) {
                methodName = "getSimpleTableMeta";
                tableMetaName = "SimpleTableMeta";
            } else {
                methodName = "getParentTableMeta";
                tableMetaName = "ParentTableMeta";
            }
            parentClassName = null;
        } else {
            methodName = "getChildTableMeta";
            tableMetaName = "ChildTableMeta";
            if (isSameClassName(element, parentElement)) {
                parentClassName = parentElement.getQualifiedName().toString();
            } else {
                parentClassName = parentElement.getSimpleName().toString();
            }
        }

        final List<? extends TypeParameterElement> paramList;
        paramList = element.getTypeParameters();
        final int paramSize = paramList.size();


        final String className;
        className = element.getSimpleName().toString();

        builder.append(FIELD_PREFIX)
                .append(tableMetaName)
                .append('<')
                .append(className);

        if (paramSize > 0) {
            for (int i = 0; i < paramSize; i++) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append('?');
            }

        }
        builder.append("> ")
                .append(_MetaBridge.TABLE_META)
                .append(" =");

        if (paramSize > 0) {
            builder.append(" (")
                    .append(tableMetaName)
                    .append('<')
                    .append(className);
            for (int i = 0; i < paramSize; i++) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append('?');
            }
            builder.append(">)");
        }
        builder.append(" _TableMetaFactory.")
                .append(methodName)
                .append('(');

        if (parentClassName != null) {
            builder.append(parentClassName)
                    .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                    .append('.')
                    .append(_MetaBridge.TABLE_META)
                    .append(',');
        }

        builder.append(className)
                .append(".class);\n\n");
    }


    private static void appendMetaCount(final TypeElement element, final StringBuilder builder
            , final int fieldSize) {
        Table table = element.getAnnotation(Table.class);

        builder.append(FIELD_PREFIX)
                .append(" String ")
                .append(_MetaBridge.TABLE_NAME)
                .append(" = \"")
                .append(table.name())
                .append("\";\n\n")

                .append(FIELD_PREFIX)
                .append(" int ")
                .append(_MetaBridge.FIELD_COUNT)
                .append(" = ")
                .append(fieldSize)
                .append(";\n\n");
    }

    private static void appendFieldCountValidateMethod(final String className, final StringBuilder builder) {
        builder.append("\n")
                .append(MEMBER_PRE)
                .append("static {\n")
                .append(MEMBER_PRE)
                .append("\t")

                .append("if(")
                .append(_MetaBridge.TABLE_META)
                .append(".fieldList().size() != ")
                .append(_MetaBridge.FIELD_COUNT)
                .append("){\n")
                .append(MEMBER_PRE)
                .append("\t\t")
                .append("String m = String.format(\"Domain[%s] field count[%s] error.\",")
                .append(className)
                .append(".class.getName(),")
                .append(_MetaBridge.FIELD_COUNT)
                .append(");\n")
                .append(MEMBER_PRE)
                .append("\t\tthrow new IllegalStateException(m);\n")
                .append(MEMBER_PRE)
                .append("\t}\n")
                .append(MEMBER_PRE)
                .append("}\n\n")
        ;
    }


}
