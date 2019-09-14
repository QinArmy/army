package io.army.modelgen;

import io.army.meta.FieldMeta;
import io.army.util.ClassUtils;
import io.army.util.StringUtils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * this class is a implement of {@link MetaAttribute}
 * created  on 2018/11/18.
 */
class DefaultMetaAttribute implements MetaAttribute {

    private final TypeElement entityElement;

    private final VariableElement mappingPropElement;

    private final boolean indexColumn;


    DefaultMetaAttribute(TypeElement entityElement, VariableElement mappingPropElement, boolean indexColumn) {
        this.entityElement = entityElement;
        this.mappingPropElement = mappingPropElement;
        this.indexColumn = indexColumn;
    }


    @Override
    public String getDefinition() {
        String format = "%s %s<%s,%s> %s = %s.%s(%s,%s.class);";
        String typeSimpleName = ClassUtils.getShortName(mappingPropElement.asType().toString());

        String propName = getName();

        String methodName = indexColumn ? "getIndexField" : "getField";
        return String.format(format,

                SourceCreateUtils.PROP_PRE,
                FieldMeta.class.getSimpleName(),
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
        String format = "%s String %s = \"%s\";";
        return String.format(format,
                SourceCreateUtils.PROP_PRE,
                StringUtils.camelToUpperCase(name),
                name
        );
    }
}
