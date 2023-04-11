package io.army.mapping;

public abstract class _ArmyArrayType extends _ArmyInnerMapping {


    protected static Class<?> underlyingComponent(final Class<?> arrayType) {
        assert arrayType.isArray();

        Class<?> componentType;
        componentType = arrayType.getComponentType();
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }
        return componentType;

    }


}
