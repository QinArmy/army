package io.army.sqltype;

public interface SQLDataType {

    String name();

    default int minPrecision() {
        return 0;
    }

    int maxPrecision();

    default int minScale() {
        return 0;
    }

    default int maxScale() {
        return -1;
    }


    /**
     *
     * @return true match
     * @throws IllegalArgumentException precisionOfField error
     */
    default boolean precisionMatch(int precisionOfField, int columnSize)
            throws IllegalArgumentException{
        return precisionOfField < 0
                || precisionOfField == columnSize;
    }

    /**
     *
     * @return true match
     * @throws IllegalArgumentException precisionOfField error
     */
    default boolean scaleMatch(int scaleOfField, int scaleOfColumn) throws IllegalArgumentException{
        return scaleOfField < 0
                || scaleOfField == scaleOfColumn;
    }

}
