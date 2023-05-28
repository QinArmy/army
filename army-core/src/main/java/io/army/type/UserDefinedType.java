package io.army.type;


public interface UserDefinedType {

    String sqlTypeName();

    /**
     * @return 0 : unknown
     */
    int sqlTypeIdentifier();

    Object value();


}
