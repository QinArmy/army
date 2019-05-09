package org.qinarmy.army.modelgen;

import org.qinarmy.army.meta.DefaultField;
import org.qinarmy.army.meta.Field;
import org.qinarmy.army.util.ElementUtils;

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
        String format = "    public static final %s<%s,%s> %s = new %s<>(T,%s);";
        String simpleName = ElementUtils.getSimpleName(variableElement.asType().toString());

        String fieldName = getName();

        return String.format(format,

                Field.class.getSimpleName(),
                type.getSimpleName(),
                simpleName,
                fieldName,

                DefaultField.class.getSimpleName(),
                ElementUtils.camelToUpperCase(fieldName)


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
