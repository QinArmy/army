package io.army.session.record;

import io.army.session.DataAccessException;

public interface ResultItem {

    /**
     * <p>Get result no (based one)
     *
     * @return result no (based one)
     */
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
         * @throws DataAccessException throw when indexBasedZero error
         */
        String getColumnLabel(int indexBasedZero) throws DataAccessException;


        /**
         * <p>
         * Get column index , if columnLabel duplication ,then return last index that have same columnLabel.
         * <br/>
         *
         * @param columnLabel column label
         * @return index based zero,the first value is 0 .
         * @throws DataAccessException throw when indexBasedZero error
         */
        int getColumnIndex(String columnLabel) throws DataAccessException;

    } // ResultAccessSpec


}
