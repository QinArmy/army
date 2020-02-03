package io.army.modelgen;

import io.army.annotation.Column;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;
import io.army.util.ClassUtils;
import io.army.util.StringUtils;
import org.jooq.Meta;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * this class is a implement of {@link MetaAttribute}
 * created  on 2018/11/18.
 */
class DefaultMetaAttribute implements MetaAttribute {

    private final TypeElement entityElement;

    private final VariableElement mappingPropElement;

    private final boolean indexColumn;

    private final String commentLine;


    DefaultMetaAttribute(TypeElement entityElement, VariableElement mappingPropElement
            , Column column, boolean indexColumn) {
        this.entityElement = entityElement;
        this.mappingPropElement = mappingPropElement;
        this.commentLine = SourceCreateUtils.COMMENT_PRE + "/**  "
                + createComment(mappingPropElement, column.comment()) + " */";
        this.indexColumn = indexColumn;
    }


    @Override
    public String getDefinition() {
        String format = "%s\n%s %s<%s%s> %s = %s.%s(%s,%s.class);";
        String qualifiedName = mappingPropElement.asType().toString();
        final String typeSimpleName = ClassUtils.getShortName(qualifiedName);
        String typeParameter = "," + typeSimpleName;
        String propName = getName();

        String methodName, fieldMetaTypeName;
        if (indexColumn) {
            if ("java.lang.Long".equals(qualifiedName)) {
                methodName = "getLongIndexField";
                fieldMetaTypeName = "LongIndexFieldMeta";
                typeParameter = "";
            } else if (isNumberType(mappingPropElement)) {
                methodName = "getNumberIndexField";
                fieldMetaTypeName = "NumberIndexFieldMeta";
            } else {
                methodName = "getIndexField";
                fieldMetaTypeName = "IndexFieldMeta";
            }

        } else {
            if ("java.lang.Long".equals(qualifiedName)) {
                methodName = "getLongField";
                fieldMetaTypeName = "LongFieldMeta";
                typeParameter = "";
            } else if (isNumberType(mappingPropElement)) {
                methodName = "getNumberField";
                fieldMetaTypeName = "NumberFieldMeta";
            } else {
                methodName = "getField";
                fieldMetaTypeName = "FieldMeta";
            }
        }
        return String.format(format,
                commentLine, // comment part
                SourceCreateUtils.PROP_PRE, // prop prefix (whitespace)
                fieldMetaTypeName, // prop type
                entityElement.getSimpleName(), // entity simple type
                typeParameter, // field type parameter name

                propName,
                MetaConstant.TABLE_META,
                methodName,
                StringUtils.camelToUpperCase(propName),

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
                StringUtils.camelToUpperCase(name),
                name
        );
    }

    /*################################## blow private method ##################################*/

    private static String createComment(VariableElement mappingPropElement, String comment) {
        String actualComment;
        if (StringUtils.hasText(comment)) {
            actualComment = comment;
        } else if (MetaUtils.isManagedByArmy(mappingPropElement)
                || MetaUtils.isCodeEnum(mappingPropElement)) {
            actualComment = commentManagedByArmy(mappingPropElement);
        } else {
            actualComment = "";
        }
        return actualComment;
    }

    private static String commentManagedByArmy(VariableElement mappingPropElement) {
        String comment = "";
        switch (mappingPropElement.getSimpleName().toString()) {
            case TableMeta.ID:
                comment = "primary key";
                break;
            case TableMeta.CREATE_TIME:
                comment = "create time";
                break;
            case TableMeta.UPDATE_TIME:
                comment = "update time";
                break;
            case TableMeta.VERSION:
                comment = "version for optimistic lock";
                break;
            case TableMeta.VISIBLE:
                comment = "visible for logic delete";
                break;
            default:
                comment = "@see " + mappingPropElement.asType().toString();
        }
        return comment;
    }

    private static boolean isNumberType(VariableElement mappingPropElement) {
        TypeMirror typeMirror = mappingPropElement.asType();
        boolean match = false;
        for (int i = 0; i < 3; i++) {
            TypeElement typeElement = convertToTypeElement(typeMirror);
            if (typeElement == null) {
                break;
            }
            typeMirror = typeElement.getSuperclass();
            if (typeMirror == null || typeMirror instanceof NoType) {
                break;
            }
            if ("java.lang.Number".equals(typeMirror.toString())) {
                match = true;
                break;
            }
        }
        return match;
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
