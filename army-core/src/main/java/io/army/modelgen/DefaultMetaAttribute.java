package io.army.modelgen;

import io.army.annotation.Column;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;
import io.army.util.ClassUtils;
import io.army.util.StringUtils;
import org.jooq.Meta;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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
                + createComment(mappingPropElement,column.comment()) + " */";
        this.indexColumn = indexColumn;
    }


    @Override
    public String getDefinition() {
        String format = "%s\n%s %s<%s,%s> %s = %s.%s(%s,%s.class);";
        String typeSimpleName = ClassUtils.getShortName(mappingPropElement.asType().toString());

        String propName = getName();

        String methodName, fieldMetaTypeName;
        if (indexColumn) {
            methodName = "getIndexField";
            fieldMetaTypeName = IndexFieldMeta.class.getSimpleName();
        } else {
            methodName = "getField";
            fieldMetaTypeName = FieldMeta.class.getSimpleName();
        }
        return String.format(format,
                commentLine,
                SourceCreateUtils.PROP_PRE,
                fieldMetaTypeName,
                entityElement.getSimpleName(),
                typeSimpleName,

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
}
