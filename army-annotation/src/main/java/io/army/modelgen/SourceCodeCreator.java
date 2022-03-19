package io.army.modelgen;

import io.army.annotation.Column;
import io.army.annotation.Index;
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

    private final String codeCreateTime;

    SourceCodeCreator(Filer filer) {
        this.filer = filer;
        this.codeCreateTime = OffsetDateTime.now().format(ISO_OFFSET_DATETIME_FORMATTER);
    }

    void create(final TypeElement element, final Map<String, VariableElement> fieldMap
            , final @Nullable TypeElement parentElement, final MappingMode mode
            , final Map<String, IndexMode> indexModeMap) throws IOException {
        final int fieldCount = fieldMap.size();
        final StringBuilder builder = new StringBuilder(fieldCount << 8);
        // 1. source army import part
        appendImports(element, builder, mode);

        // 2. source parentMeta class import part
        if (parentElement != null) {
            appendParentClassImport(builder, element, parentElement);
        }

        final String simpleClassName, domainName;
        simpleClassName = MetaUtils.getSimpleClassName(element);

        //3. class definition
        appendClassDefinition(element, simpleClassName, parentElement, mode, builder);

        //4. static block
        domainName = appendStaticBlock(element, simpleClassName, parentElement, fieldCount, builder);

        String fieldName, commentLine, upperCaseFieldName, methodName, metaTypeName;

        final StringBuilder fieldBuilder = new StringBuilder(fieldCount << 7);


        StringBuilder commentBuilder;
        int count = 0;
        boolean primary = false;
        //5. create field name definition and field definitions and append import statement
        for (VariableElement field : fieldMap.values()) {
            fieldName = field.getSimpleName().toString();

            commentBuilder = new StringBuilder()
                    .append(COMMENT_PREFIX)
                    .append("/**  ")
                    .append(getComment(field))
                    .append(" */\n");

            commentLine = commentBuilder.toString();

            // field name definition
            upperCaseFieldName = _MetaBridge.camelToUpperCase(fieldName);
            builder
                    .append(commentLine)
                    .append(FIELD_PREFIX)
                    .append("String ")
                    .append(upperCaseFieldName)
                    .append(" = \"")
                    .append(fieldName)
                    .append("\";\n");

            count++;
            if ((count & 3) == 0) {
                builder.append('\n');
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


        //6. merge three builder
        builder.append("\n\n")
                .append(fieldBuilder)
                .append("\n\n}\n\n");

        //7. output source code.
        final FileObject fileObject;
        fileObject = this.filer.createSourceFile(MetaUtils.getClassName(element) + _MetaBridge.META_CLASS_NAME_SUFFIX);
        try (PrintWriter pw = new PrintWriter(fileObject.openOutputStream())) {
            pw.println(builder);
        }

    }


    /**
     * debugSQL meta source code class definition part
     */
    private void appendClassDefinition(final TypeElement element, final String simpleClassName
            , @Nullable final TypeElement parentElement, final MappingMode mode, final StringBuilder builder) {


        builder.append("\n\n@Generated(value = \"")
                .append(ArmyMetaModelDomainProcessor.class.getName())
                .append("\"\n")
                .append(ANNOTATION_PRE)
                .append(",date = \"")
                .append(this.codeCreateTime)
                .append("\"\n")
                .append(ANNOTATION_PRE)
                .append(",comments = \"")
                .append(element.getAnnotation(Table.class).comment())
                .append("\")")
                .append("\n")
                .append("public abstract class ")
                .append(simpleClassName)
                .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
        ;

        if (parentElement != null) {
            builder.append(" extends ");
            if (isSameClassName(element, parentElement)) {
                builder.append(MetaUtils.getClassName(parentElement));
            } else {
                builder.append(MetaUtils.getSimpleClassName(parentElement));
            }
            builder.append(_MetaBridge.META_CLASS_NAME_SUFFIX);
        }
        builder.append(" {\n\n");

        // append default Constructor
        builder.append(MEMBER_PRE);
        switch (mode) {
            case SIMPLE: {
                // simple domain
                builder.append("private ").
                        append(simpleClassName)
                        .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                        .append("(){\n")
                        .append(MEMBER_PRE)
                        .append("\tthrow new UnsupportedOperationException();\n")
                        .append(MEMBER_PRE)
                        .append('}');
            }
            break;
            case PARENT: {
                // parent domain
                builder.append("protected ").
                        append(simpleClassName)
                        .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                        .append("(){\n")
                        .append(MEMBER_PRE)
                        .append("\tthrow new UnsupportedOperationException();\n")
                        .append(MEMBER_PRE)
                        .append('}');
            }
            break;
            case CHILD: {
                // child domain
                builder.append("private ").
                        append(simpleClassName)
                        .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                        .append("(){\n")
                        .append(MEMBER_PRE)
                        .append('}');
            }
            break;
            default:
                throw new IllegalArgumentException(String.format("unexpected enum %s", mode));
        }
        builder.append("\n\n");
    }


    private static void appendImports(final TypeElement element, final StringBuilder builder, final MappingMode mode) {

        builder.append("package ")
                .append(((PackageElement) element.getEnclosingElement()).getQualifiedName())
                .append(";\n\n");

        builder.append("import io.army.meta.FieldMeta;\n")
                .append("import javax.annotation.Generated;\n")
                .append("import io.army.criteria.impl._TableMetaFactory;\n")
                .append("import io.army.meta.PrimaryFieldMeta;\n");

        boolean hasIndex = false, hasUnique = false;

        for (Index index : element.getAnnotation(Table.class).indexes()) {
            if (!hasIndex) {
                hasIndex = true;
            }
            if (index.unique() && index.fieldList().length == 1) {
                hasUnique = true;
                break;
            }

        }
        if (hasIndex) {
            builder.append("import io.army.meta.IndexFieldMeta;\n");
        }

        if (hasUnique) {
            builder.append("import io.army.meta.UniqueFieldMeta;\n");
        }

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


    private static void appendParentClassImport(final StringBuilder builder, final TypeElement tableElement
            , final TypeElement parentElement) {
        if (!isSameClassName(tableElement, parentElement) && !isSamePackage(tableElement, parentElement)) {
            builder.append("import ")
                    .append(MetaUtils.getClassName(parentElement))
                    .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                    .append(";\n");
        }

    }


    private static String getComment(final VariableElement field) {
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


    private static boolean isSameClassName(TypeElement element1, TypeElement element2) {
        return element1.getSimpleName().equals(element2.getSimpleName());
    }

    private static boolean isSamePackage(TypeElement element1, TypeElement element2) {
        return element1.getQualifiedName().equals(element2.getQualifiedName());
    }

    private static boolean isEnum(final TypeMirror fieldType) {
        return fieldType instanceof DeclaredType
                && ((DeclaredType) fieldType).asElement().getKind() == ElementKind.ENUM;
    }


    private static String appendStaticBlock(final TypeElement element, final String simpleClassName
            , @Nullable final TypeElement parentElement, int fieldCount, final StringBuilder builder) {

        final Table table = element.getAnnotation(Table.class);

        builder.append(FIELD_PREFIX)
                .append(" String ")
                .append(_MetaBridge.TABLE_NAME)
                .append(" = \"")
                .append(table.name())
                .append("\";\n\n");

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
                parentClassName = MetaUtils.getClassName(parentElement);
            } else {
                parentClassName = MetaUtils.getSimpleClassName(parentElement);
            }
        }

        final List<? extends TypeParameterElement> paramList;
        paramList = element.getTypeParameters();
        final int paramSize = paramList.size();

        builder.append(FIELD_PREFIX)
                .append(tableMetaName)
                .append('<');

        final int domainStart;
        domainStart = builder.length();
        builder.append(simpleClassName);

        final String domainName;
        if (paramSize > 0) {
            builder.append('<');
            for (int i = 0; i < paramSize; i++) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append('?');
            }
            builder.append('>');
            domainName = builder.substring(domainStart, builder.length());
        } else {
            domainName = simpleClassName;
        }
        builder.append("> ")
                .append(_MetaBridge.TABLE_META)
                .append(";\n\n")

                .append(MEMBER_PRE)
                .append("static {\n")
                .append(MEMBER_PRE)
                .append('\t');

        if (paramSize > 0) {
            builder.append("final ")
                    .append(tableMetaName)
                    .append("<?> temp;\n")
                    .append(MEMBER_PRE)
                    .append("\ttemp = _TableMetaFactory.")
                    .append(methodName)
                    .append('(');

            if (parentClassName != null) {
                builder.append(parentClassName)
                        .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                        .append('.')
                        .append(_MetaBridge.TABLE_META)
                        .append(',');
            }
            builder.append(simpleClassName)
                    .append(".class);\n")
                    .append(MEMBER_PRE)
                    .append('\t')
                    .append(_MetaBridge.TABLE_META)
                    .append(" = (")
                    .append(tableMetaName)
                    .append('<')
                    .append(domainName)
                    .append(">)temp;\n\n");

        } else {
            builder.append(_MetaBridge.TABLE_META)
                    .append(" = _TableMetaFactory.")
                    .append(methodName)
                    .append('(');

            if (parentClassName != null) {
                builder.append(parentClassName)
                        .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                        .append('.')
                        .append(_MetaBridge.TABLE_META)
                        .append(',');
            }
            builder.append(simpleClassName)
                    .append(".class);\n\n");
        }

        final String varFieldSize = "fieldSize";

        builder.append(MEMBER_PRE)
                .append("\tfinal int ")
                .append(varFieldSize)
                .append(" = ")
                .append(_MetaBridge.TABLE_META)
                .append(".fieldList().size();\n")
                .append(MEMBER_PRE)
                .append("\tif(")
                .append(varFieldSize)
                .append(" != ")
                .append(fieldCount)
                .append("){\n")
                .append(MEMBER_PRE)
                .append("\t\t")
                .append("String m = String.format(\"Domain[%s] field count[%s] error.\",")
                .append(simpleClassName)
                .append(".class.getName(),")
                .append(varFieldSize)
                .append(");\n")
                .append(MEMBER_PRE)
                .append("\t\tthrow new IllegalStateException(m);\n")
                .append(MEMBER_PRE)
                .append("\t}\n")
                .append(MEMBER_PRE)
                .append("}\n\n");

        return domainName;
    }


}