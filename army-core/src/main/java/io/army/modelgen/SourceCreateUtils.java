package io.army.modelgen;

import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.criteria.impl.DefaultField;
import io.army.criteria.impl.DefaultTable;
import io.army.meta.Field;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.annotation.Generated;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.*;

/**
 * created  on 2018/11/18.
 */
public abstract class SourceCreateUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SourceCreateUtils.class);

    private static final Set<ElementKind> FIELD_KINDS = EnumSet.of(ElementKind.FIELD);

    private static final Set<String> DOMAIN_REQUIRED_ATTRIBUTES = ArrayUtils.asUnmodifiableSet(
            "id", "createTime", "updateTime", "visible", "version");


    private static final String META_CLASS_NAME_SUFFIX = "_";

    private static final String JAVA_LANG = "java.lang";

    public static final String TABLE_PROPERTY_NAME = "T";


    /**
     * @return first:self's VariableElement,second:super's VariableElement
     */
    @NonNull
    static Set<VariableElement> generateAttributes(
            @NonNull TypeElement type,
            @NonNull List<TypeElement> superList,
            @NonNull List<TypeElement> inheritanceList) {


        // MappedSuperclass super variables
        final Set<VariableElement> mappingSuperVariables = generateAttributes(superList);
        // Inheritance super variables
        final Set<VariableElement> inheritanceVariables = generateAttributes(inheritanceList);

        final Set<VariableElement> entityVariables = new HashSet<>(generateAttributes(type));

        // assert DOMAIN_REQUIRED_ATTRIBUTES
        if (CollectionUtils.isEmpty(inheritanceList)) {
            Set<VariableElement> variables = new HashSet<>(mappingSuperVariables);
            variables.addAll(entityVariables);
            assertRequiredVariables(type, variables);
        } else {
            assertRequiredVariables(type, inheritanceVariables);
        }
        // add non override
        addNonOverride(entityVariables, mappingSuperVariables);

        return Collections.unmodifiableSet(entityVariables);
    }


    private static void addNonOverride(Set<VariableElement> entityVariables,
                                       Set<VariableElement> mappingSuperVariables) {

        final Set<String> fieldNameSet = new HashSet<>();
        for (VariableElement entityVariable : entityVariables) {
            fieldNameSet.add(entityVariable.getSimpleName().toString());
        }

        for (VariableElement mv : mappingSuperVariables) {
            if (!fieldNameSet.contains(mv.getSimpleName().toString())) {
                entityVariables.add(mv);
            }
        }
    }

    private static void assertRequiredVariables(TypeElement type, Collection<VariableElement> variableElements)
            throws MetaException {
        Map<String, Boolean> handledVariableMap = new HashMap<>(10);

        String variableName;
        for (VariableElement variable : variableElements) {
            variableName = variable.getSimpleName().toString();
            LOG.trace(" type variable :{}", variableName);
            if (DOMAIN_REQUIRED_ATTRIBUTES.contains(variableName)
                    && !handledVariableMap.containsKey(variableName)) {
                handledVariableMap.put(variableName, Boolean.TRUE);
            }
        }
        if (DOMAIN_REQUIRED_ATTRIBUTES.size() != handledVariableMap.size()) {
            throw new MetaException(ErrorCode.META_ERROR,
                    String.format("\nDomain[%s] required fields : %s",
                            type.getQualifiedName(), DOMAIN_REQUIRED_ATTRIBUTES));
        }
    }

    private static Set<VariableElement> generateAttributes(List<TypeElement> typeList) {
        Set<VariableElement> set = new HashSet<>();

        for (TypeElement typeElement : typeList) {
            set.addAll(generateAttributes(typeElement));
        }
        return Collections.unmodifiableSet(set);
    }

    private static Set<VariableElement> generateAttributes(@NonNull TypeElement type) {

        Column column;
        VariableElement variableElement;

        Set<VariableElement> set = new HashSet<>();

        for (Element element : type.getEnclosedElements()) {
            if (!FIELD_KINDS.contains(element.getKind())) {
                continue;
            }
            column = element.getAnnotation(Column.class);
            if (column == null) {
                continue;
            }

            variableElement = (VariableElement) element;
            set.add(variableElement);
        }
        return Collections.unmodifiableSet(set);
    }


    static String generateImport(@NonNull TypeElement type, @Nullable TypeElement superType,
                                 @NonNull Collection<VariableElement> variableElements) {
        StringBuilder builder = new StringBuilder("package ");

        builder.append(getPackage(type))
                .append(";\n\n")
                .append("import ")
                .append(Field.class.getName())

                .append(";\n")
                .append("import ")
                .append(DefaultField.class.getName())
                .append(";\n")

                .append("import ")
                .append(Generated.class.getName())
                .append(";\n")
                .append("import ")

                .append(StaticMetamodel.class.getName())
                .append(";\n")
                .append("import ")
                .append(type.getQualifiedName())

                .append(";\n")
                .append("import ")
                .append(DefaultTable.class.getName())
                .append(";\n")

                .append("import ")
                .append(TableMeta.class.getName())
                .append(";\n")
        ;

        if (superType != null && !samePackage(type, superType)) {
            builder.append("import ")
                    .append(superType.getQualifiedName())
                    .append(META_CLASS_NAME_SUFFIX)
                    .append(";\n")
            ;
        }

        Set<String> fieldTypeNameSet = new HashSet<>();
        String fieldTypeName;

        for (VariableElement variableElement : variableElements) {
            fieldTypeName = variableElement.asType().toString();
            if (fieldTypeNameSet.contains(fieldTypeName)
                    || isJavaLang(variableElement.asType())) {
                continue;
            }
            builder.append("import ")
                    .append(fieldTypeName)
                    .append(";\n")
            ;

            fieldTypeNameSet.add(fieldTypeName);
        }
        builder.append("\n\n");
        return builder.toString();
    }

    static String getPackage(TypeElement type) {
        return ((PackageElement) type.getEnclosingElement()).getQualifiedName().toString();
    }

    static String generateClassDefinition(TypeElement type, TypeElement superType) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("@Generated(value = \"%s\")"
                , ArmyMetaModelEntityProcessor.class.getName()))
                .append("\n")
                .append(String.format("@StaticMetamodel(%s.class)", type.getSimpleName()))
                .append("\n")

                .append(String.format("public abstract class %s_ ", type.getSimpleName()))

        ;
        if (superType != null) {
            builder.append("extends ")
                    .append(superType.getSimpleName())
                    .append(META_CLASS_NAME_SUFFIX)
            ;

        }
        builder.append(" {\n\n");
        return builder.toString();
    }

    static boolean samePackage(TypeElement type, TypeElement superType) {
        return type.getEnclosingElement().equals(superType.getEnclosingElement());
    }

    static String generateBody(@NonNull TypeElement type, @Nullable TypeElement superType,
                               @NonNull List<MetaAttribute> attributeList) {
        StringBuilder builder = new StringBuilder();

        String format = "    public static final %s<%s> %s = new %s<>(%s.class);\n\n";

        builder.append(String.format(format,
                TableMeta.class.getSimpleName(),
                type.getSimpleName(),
                TABLE_PROPERTY_NAME,
                DefaultTable.class.getSimpleName(),
                type.getSimpleName()

        ));


        Table table = type.getAnnotation(Table.class);
        if (table != null) {
            format = "    public static final String META_TABLE_NAME = \"%s\";\n\n";
            builder.append(String.format(format, table.name()));
        }

        format = "    public static final int META_FIELD_SELF_COUNT = %s;\n\n";
        builder.append(String.format(format, attributeList.size()));

        if (superType == null) {
            format = "    public static final int META_FIELD_COUNT = %s;\n\n";
            builder.append(String.format(format, "META_FIELD_SELF_COUNT"));
        } else {
            format = "    public static final int META_FIELD_COUNT = META_FIELD_SELF_COUNT + %s_.META_FIELD_SELF_COUNT;\n\n";
            builder.append(String.format(format, superType.getSimpleName()));
        }

        // fieldName define
        int count = 0;
        for (MetaAttribute metaAttribute : attributeList) {
            builder.append(metaAttribute.getNameDefinition())
                    .append("\n");
            count++;
            if (count % 4 == 0) {
                builder.append("\n");
            }
        }
        // field define
        builder.append("\n\n");
        count = 0;
        for (MetaAttribute metaAttribute : attributeList) {
            builder.append(metaAttribute.getDefinition())
                    .append("\n");
            count++;
            if (count % 4 == 0) {
                builder.append("\n");
            }
        }

        builder.append("\n\n}\n\n\n");
        return builder.toString();
    }

    static String getQualifiedName(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString() + META_CLASS_NAME_SUFFIX;
    }

    private static boolean isJavaLang(TypeMirror typeMirror) {
        String name = typeMirror.toString();
        int index = name.lastIndexOf('.');
        boolean match = false;
        if (index > 0) {
            match = JAVA_LANG.equals(name.substring(0, index));
        }
        return match;
    }
}
