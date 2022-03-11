package io.army.modelgen;

import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.lang.NonNull;
import io.army.lang.Nullable;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

import static java.time.temporal.ChronoField.*;

/**
 * @since 1.0
 */
abstract class SourceCreateUtils {

    private SourceCreateUtils() {
        throw new UnsupportedOperationException();
    }

    private static final String JAVA_LANG = "java.lang.";

    private static final String MEMBER_PRE = "    ";


    static final String PROP_PRE = MEMBER_PRE + "public static final";

    static final String COMMENT_PRE = MEMBER_PRE;

    static final String ANNOTATION_PRE = "        ";

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


    /**
     * debugSQL meta source code import part
     *
     * @param domainElement       entity element that annotated by {@link Table}
     * @param parentElement       entityElement's parentMeta element that annotated by {@link Table}
     * @param mappingPropElements entityElement's mapping prop collection
     * @return source code import part
     */
    static String generateImport(final TypeElement domainElement, @Nullable final TypeElement parentElement
            , Collection<VariableElement> mappingPropElements) {
        final StringBuilder builder = new StringBuilder("package ");
        // 1. source package part
        builder.append(MetaUtils.getPackageName(domainElement))
                .append(";\n\n");

        // 2. source army import part
        //SourceCodeBuilder.appendArmyClassImport(builder);

        // 3. source parentMeta class import part
//        if (parentElement != null) {
//            appendParentClassImport(builder, domainElement, parentElement);
//        }

        // 4. source mapping props class import part
        appendMappingPropsClassImport(builder, mappingPropElements);

        builder.append("\n\n");
        return builder.toString();
    }


    /**
     * debugSQL meta source code class definition part
     */
    static String generateClassDefinition(final TypeElement domainElement, @Nullable final TypeElement parentElement) {
        final String className = domainElement.getSimpleName().toString();

        final StringBuilder builder = new StringBuilder(150);
        builder.append("@Generated(value = \"")
                .append(ArmyMetaModelDomainProcessor.class.getName())
                .append("\"\n")
                .append(ANNOTATION_PRE)
                .append(",date = \"")
                .append(OffsetDateTime.now().format(ISO_OFFSET_DATETIME_FORMATTER))
                .append("\"\n")
                .append(ANNOTATION_PRE)
                .append(",comments = \"")
                .append(domainElement.getAnnotation(Table.class).comment())
                .append("\")")
                .append("\n")
                .append("public abstract class ")
                .append(className)
                .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
        ;

        if (parentElement != null) {
            builder.append(" extends ")
                    .append(parentEntityClassRef(domainElement, parentElement))
                    .append(_MetaBridge.META_CLASS_NAME_SUFFIX);
        }
        builder.append(" {\n\n");

        // append default Constructor
        builder.append(MEMBER_PRE);
        if (parentElement == null && domainElement.getAnnotation(Inheritance.class) != null) {
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
        return builder.append("\n\n")
                .toString();
    }


    /**
     * generate meta source  class body
     */
    static String generateBody(TypeElement entityElement, @Nullable TypeElement parentEntityElement,
                               List<MetaAttribute> mappingPropList) {
        final StringBuilder builder = new StringBuilder(400);

        //1.  TableMeta part
        appendTableMeta(entityElement, builder, parentEntityElement);

        //2.  meta count part
        appendMetaCount(entityElement, builder, parentEntityElement, mappingPropList);

        // 3. field count validate static method
        appendFieldCountValidateMethod(entityElement, builder);

        //4. prop names  definition
        appendMappingPropNames(builder, mappingPropList);

        // new line
        builder.append("\n\n");

        //5. mapping prop meta definition
        appendMappingPropMeta(builder, mappingPropList);

        builder.append("\n\n}\n\n\n");
        return builder.toString();
    }

    /*################################## blow private method ##################################*/

    private static void appendFieldCountValidateMethod(TypeElement entityElement, StringBuilder builder) {
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
                .append(entityElement.getSimpleName())
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

    private static void appendTableMeta(final TypeElement domainElement, final StringBuilder builder,
                                        @Nullable final TypeElement parentElement) {
        String format = "%s %s<%s> %s = _TableMetaFactory.%s(%s%s.class);\n\n";
        String parentTableMetaText, methodName, tableMetaName;
        if (parentElement == null) {
            if (domainElement.getAnnotation(Inheritance.class) == null) {
                methodName = "getSimpleTableMeta";
                tableMetaName = "SimpleTableMeta";
            } else {
                methodName = "getParentTableMeta";
                tableMetaName = "ParentTableMeta";
            }
            parentTableMetaText = "";
        } else {
            methodName = "getChildTableMeta";
            tableMetaName = "ChildTableMeta";
            parentTableMetaText = String.format("%s%s.%s,",
                    parentEntityClassRef(domainElement, parentElement),
                    _MetaBridge.META_CLASS_NAME_SUFFIX,
                    _MetaBridge.TABLE_META
            );
        }
        builder.append(String.format(format,
                PROP_PRE,
                tableMetaName,
                domainElement.getSimpleName(),
                _MetaBridge.TABLE_META,
                methodName,
                parentTableMetaText,
                domainElement.getSimpleName()
        ));
    }

    private static void appendMetaCount(TypeElement entityElement, StringBuilder builder
            , @Nullable TypeElement parentEntityElement, List<MetaAttribute> attributeList) {
        Table table = entityElement.getAnnotation(Table.class);

        builder.append(PROP_PRE)
                .append(" String ")
                .append(_MetaBridge.TABLE_NAME)
                .append(" = \"")
                .append(table.name())
                .append("\";\n\n");

        builder.append(PROP_PRE)
                .append(" int ")
                .append(_MetaBridge.FIELD_COUNT)
                .append(" = ");

        builder.append(attributeList.size());

        builder.append(";\n\n")
                .append(PROP_PRE)
                .append(" int ")
                .append(_MetaBridge.FIELD_TOTAL)
                .append(" = ")
                .append(_MetaBridge.FIELD_COUNT)
        ;

        if (parentEntityElement != null) {
            builder.append(" + ")
                    .append(parentEntityClassRef(entityElement, parentEntityElement))
                    .append(_MetaBridge.META_CLASS_NAME_SUFFIX)
                    .append(".")
                    .append(_MetaBridge.FIELD_TOTAL)
            ;
        }
        builder.append(";\n\n");
    }

    private static void appendMappingPropNames(StringBuilder builder,
                                               @NonNull List<MetaAttribute> mappingPropMetaList) {
        int count = 0;
        for (MetaAttribute mappingPropMeta : mappingPropMetaList) {
            builder.append(mappingPropMeta.getNameDefinition())
                    .append("\n");
            count++;
            if (count % 4 == 0) {
                builder.append("\n");
            }
        }
    }

    private static void appendMappingPropMeta(StringBuilder builder, @NonNull List<MetaAttribute> attributeList) {
        int count = 0;
        for (MetaAttribute metaAttribute : attributeList) {
            builder.append(metaAttribute.getDefinition())
                    .append("\n");
            count++;
            if (count % 4 == 0) {
                builder.append("\n");
            }
        }
    }



    /**
     * @see #generateImport(TypeElement, TypeElement, Collection)
     */
    private static void appendMappingPropsClassImport(StringBuilder builder,
                                                      Collection<VariableElement> mappingPropElements) {
        // set to avoid field type duplicate
        final Set<String> fieldTypeNameSet = new HashSet<>();
        String typeName;
        for (VariableElement field : mappingPropElements) {
            typeName = field.asType().toString();
            if (typeName.charAt(typeName.length() - 1) == ']') {
                typeName = typeName.substring(0, typeName.indexOf('['));
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
                        continue;
                    default:
                        // no-op
                }
            }

            if (typeName.startsWith(JAVA_LANG) || fieldTypeNameSet.contains(typeName)) {
                continue;
            }

            builder.append("import ")
                    .append(typeName)
                    .append(";\n");

            fieldTypeNameSet.add(typeName);
        }
    }

    private static boolean samePackage(TypeElement entityElement, TypeElement parentEntityElement) {
        /*
         * not doc
         * entityElement must be a top-level
         * @see ArmyMetaModelEntityProcessor#assertEntity(TypeElement, Set).
         */
        return entityElement.getEnclosingElement().equals(parentEntityElement.getEnclosingElement());
    }

    private static boolean sameClassName(TypeElement domainElement, TypeElement parentElement) {
        return domainElement.getSimpleName().equals(parentElement.getSimpleName());
    }

    /**
     * @return reference of parentMeta entity class name
     * @see #generateClassDefinition(TypeElement, TypeElement)
     */
    private static String parentEntityClassRef(TypeElement domainElement, TypeElement parentElement) {
        final String parentRef;
        if (sameClassName(domainElement, parentElement)) {
            parentRef = parentElement.getQualifiedName().toString();
        } else {
            parentRef = parentElement.getSimpleName().toString();
        }
        return parentRef;
    }


}
