package io.army.annotation;

public enum GeneratorType {
    PRECEDE,
    POST;


    @Override
    public final String toString() {
        return String.format("%s.%s", GeneratorType.class.getName(), this.name());
    }

}
