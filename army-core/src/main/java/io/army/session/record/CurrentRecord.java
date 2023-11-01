package io.army.session.record;


public interface CurrentRecord extends DataRecord {

    /**
     * row number of current row
     *
     * @return the row number of current row, based 1 . The first value is 1 .
     */
    long rowNumber();

    /**
     * <p>
     * Create one {@link ResultRecord} with coping all column data.
     * <br/>
     *
     * @return new {@link ResultRecord}
     */
    ResultRecord asResultRecord();

}
