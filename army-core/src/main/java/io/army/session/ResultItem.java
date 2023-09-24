package io.army.session;

public interface ResultItem {

    int getResultNo();


    interface ResultAccessSpec {
        /**
         * Returns the number of row
         *
         * @return the number of row
         */
        int getColumnCount();

        /**
         * Get column label of appropriate column
         *
         * @param indexBasedZero index based zero,the first value is 0 .
         * @return the suggested column title              .
         * @throws IllegalArgumentException throw when indexBasedZero error
         */
        String getColumnLabel(int indexBasedZero) throws IllegalArgumentException;


        /**
         * <p>
         * Get column index , if columnLabel duplication ,then return last index that have same columnLabel.
         * <br/>
         *
         * @param columnLabel column label
         * @return index based zero,the first value is 0 .
         * @throws IllegalArgumentException throw when indexBasedZero error
         */
        int getColumnIndex(String columnLabel) throws IllegalArgumentException;
    }


}
