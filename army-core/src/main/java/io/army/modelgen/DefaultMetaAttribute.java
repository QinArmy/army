package io.army.modelgen;

import io.army.meta.FieldMeta;
import io.army.util.ElementUtils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * created  on 2018/11/18.
 */
class DefaultMetaAttribute implements MetaAttribute {

    private final TypeElement type;

    private final VariableElement variableElement;


    DefaultMetaAttribute(TypeElement type, VariableElement variableElement) {
        this.type = type;
        this.variableElement = variableElement;
    }


    @Override
    public String getDefinition() {
        String format = "    public static final %s<%s,%s> %s = T.getField(%s,%s.class);";
        String propSimpleName = ElementUtils.getSimpleName(variableElement.asType().toString());

        String fieldName = getName();

        return String.format(format,

                FieldMeta.class.getSimpleName(),
                type.getSimpleName(),
                propSimpleName,
                fieldName,

                ElementUtils.camelToUpperCase(fieldName),
                propSimpleName
        );

    }

    @Override
    public String getName() {
        return variableElement.getSimpleName().toString();
    }

    @Override
    public String getNameDefinition() {
        String name = variableElement.getSimpleName().toString();
        String format = "    public static final String %s = \"%s\";";
        return String.format(format, ElementUtils.camelToUpperCase(name), name);
    }
}
