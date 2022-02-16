package io.army.modelgen;

import io.army.annotation.Column;
import io.army.lang.Nullable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;

/**
 * this class is a implement of {@link MetaAttribute}
 *
 * @since 1.0
 */
class DefaultMetaAttribute implements MetaAttribute {

    /**
     * @return a unmodifiable List
     */
    static List<MetaAttribute> createMetaAttributes(TypeElement domainElement
            , Collection<VariableElement> mappingPropSet, Map<String, IndexMode> indexMetaMa) {

        final List<MetaAttribute> list = new ArrayList<>(mappingPropSet.size());
        Column column;
        MetaAttribute attribute;
        String propName;
        for (VariableElement mappingProp : mappingPropSet) {
            propName = mappingProp.getSimpleName().toString();
            column = mappingProp.getAnnotation(Column.class);
            IndexMode indexMode = indexMetaMa.get(propName);
            if (indexMode == null && propName.equals(_MetaBridge.ID)) {
                indexMode = IndexMode.PRIMARY;
            }
            attribute = new DefaultMetaAttribute(domainElement, mappingProp, column, indexMode);
            list.add(attribute);
        }
        return Collections.unmodifiableList(list);
    }


    private final TypeElement entityElement;

    private final VariableElement mappingPropElement;

    private final IndexMode indexMode;

    private final String commentLine;


    private DefaultMetaAttribute(TypeElement entityElement, VariableElement mappingPropElement
            , Column column, @Nullable IndexMode indexMode) {
        this.entityElement = entityElement;
        this.mappingPropElement = mappingPropElement;
        this.commentLine = SourceCreateUtils.COMMENT_PRE + "/**  "
                + createComment(mappingPropElement, column.comment()) + " */";
        this.indexMode = indexMode;
    }


    @Override
    public String getDefinition() {
        String format = "%s\n%s %s<%s%s> %s = %s.%s(%s%s.class);";
        final String qualifiedName = this.mappingPropElement.asType().toString();
        final String typeSimpleName = Strings.getShortName(qualifiedName);
        String typeParameter = "," + typeSimpleName;
        String propName = getName();

        String methodName, fieldMetaTypeName, propRef = null;

        if (indexMode == null) {
            methodName = "getField";
            fieldMetaTypeName = "FieldMeta";
        } else {
            switch (indexMode) {
                case GENERIC:
                    methodName = "getIndexField";
                    fieldMetaTypeName = "IndexFieldMeta";
                    break;
                case UNIQUE:
                    methodName = "getUniqueField";
                    fieldMetaTypeName = "UniqueFieldMeta";
                    break;
                case PRIMARY:
                    methodName = "id";
                    fieldMetaTypeName = "PrimaryFieldMeta";
                    propRef = "";
                    break;
                default:
                    throw new IllegalArgumentException(String.format("IndexMode[%s] unknown", indexMode));

            }
        }

        if (propRef == null) {
            propRef = _MetaBridge.camelToUpperCase(propName) + ",";
        }

        return String.format(format,
                commentLine, // comment part
                SourceCreateUtils.PROP_PRE, // prop prefix (whitespace)
                fieldMetaTypeName, // prop type
                entityElement.getSimpleName(), // entity simple type
                typeParameter, // field type parameter name

                propName,
                _MetaBridge.TABLE_META,
                methodName,
                propRef,

                typeSimpleName
        );

    }

    @Override
    public String getName() {
        return mappingPropElement.getSimpleName().toString();
    }

    @Override
    public String getNameDefinition() {
        String name = mappingPropElement.getSimpleName().toString();
        String format = "%s\n%s String %s = \"%s\";";
        return String.format(format,
                commentLine,
                SourceCreateUtils.PROP_PRE,
                _MetaBridge.camelToUpperCase(name),
                name
        );
    }

    /*################################## blow private method ##################################*/

    private static String createComment(VariableElement mappingPropElement, String comment) {
        final String actualComment;
        if (Strings.hasText(comment)) {
            actualComment = comment;
        } else if (MetaUtils.isReservedProp(mappingPropElement)
                || MetaUtils.getEnumElement(mappingPropElement) != null) {
            actualComment = commentManagedByArmy(mappingPropElement);
        } else {
            actualComment = "";
        }
        return actualComment;
    }

    private static String commentManagedByArmy(VariableElement mappingPropElement) {
        final String comment;
        switch (mappingPropElement.getSimpleName().toString()) {
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
            default:
                comment = "@see " + mappingPropElement.asType().toString();
        }
        return comment;
    }


    @Nullable
    private static TypeElement convertToTypeElement(TypeMirror typeMirror) {

        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return null;
        }

        Element element = ((DeclaredType) typeMirror).asElement();
        if (element.getKind() != ElementKind.CLASS) {
            return null;
        }
        return (TypeElement) element;
    }
}
