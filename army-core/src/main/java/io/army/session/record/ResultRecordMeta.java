package io.army.session.record;

import io.army.criteria.Selection;
import io.army.session.DataAccessException;
import io.army.session.Option;
import io.army.sqltype.ArmyType;
import io.army.sqltype.DataType;

import javax.annotation.Nullable;
import java.util.List;

public interface ResultRecordMeta extends ResultItem, ResultItem.ResultAccessSpec {

    List<String> columnLabelList();

    List<? extends Selection> selectionList() throws DataAccessException;


    Selection getSelection(int indexBasedZero) throws DataAccessException;

    DataType getDataType(int indexBasedZero) throws DataAccessException;

    ArmyType getArmyType(int indexBasedZero) throws DataAccessException;

    @Nullable
    <T> T getOf(int indexBasedZero, Option<T> option) throws DataAccessException;

    <T> T getNonNullOf(int indexBasedZero, Option<T> option) throws DataAccessException;


    /**
     * Get catalog name
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return <ul>
     * <li>non-null : catalog name</li>
     * <li>null : no catalog name or unknown</li>
     * </ul>
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    String getCatalogName(int indexBasedZero) throws DataAccessException;


    /**
     * Get schema name
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return <ul>
     * <li>non-null : schema name</li>
     * <li>null : no schema name or unknown</li>
     * </ul>
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    String getSchemaName(int indexBasedZero) throws DataAccessException;


    /**
     * Get table name
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return <ul>
     * <li>non-null : table name</li>
     * <li>null : no table name or unknown</li>
     * </ul>
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    String getTableName(int indexBasedZero) throws DataAccessException;

    /**
     * Get the column's name.
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return <ul>
     * <li>non-null : column name</li>
     * <li>null : no column name or unknown</li>
     * </ul>
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    String getColumnName(int indexBasedZero) throws DataAccessException;


    /**
     * get precision of column.
     * <p>
     * follow below principle:
     * <ul>
     *     <li>decimal type : return precision of decimal,for example decimal(14,2),return 14</li>
     *     <li>text string type : return maximum char length</li>
     *     <li>binary type : return maximum byte length</li>
     *     <li>bit string type : return maximum bit length</li>
     *     <li>integer and float :  return 0</li>
     *     <li>time/date : return 0</li>
     *     <li>other dialect type : it's up to driver developer</li>
     * </ul>
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return precision
     * @throws DataAccessException throw when indexBasedZero error
     * @see #getScale(int)
     */
    int getPrecision(int indexBasedZero) throws DataAccessException;


    /**
     * get precision of column.
     * <p>
     * follow below principle:
     * <ul>
     *     <li>decimal type : return scale of decimal,for example decimal(14,2),return 2</li>
     *     <li>integer and float :  return 0</li>
     *     <li>time and timestamp : return micro second precision,for example : time(5) return 5</li>
     *     <li>other dialect type : it's up to driver developer</li>
     * </ul>
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return precision
     * @throws DataAccessException throw when indexBasedZero error
     * @see #getPrecision(int)
     */
    int getScale(int indexBasedZero) throws DataAccessException;

    FieldType getFieldType(int indexBasedZero) throws DataAccessException;

    @Nullable
    Boolean getAutoIncrementMode(int indexBasedZero) throws DataAccessException;

    /**
     * Get key mode
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws DataAccessException throw when indexBasedZero error
     */
    KeyType getKeyMode(int indexBasedZero) throws DataAccessException;

    /**
     * Get nullable mode
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    Boolean getNullableMode(int indexBasedZero) throws DataAccessException;


    /**
     * <p>
     * Get the first java type of appropriate column.
     * For example :
     *    <ul>
     *        <li>{@link ArmyType#BIGINT} first java type is {@link Long},second java type is null</li>
     *        <li>{@link ArmyType#LONGTEXT} first java type is {@link String},second java type is {@link TextPath}</li>
     *         <li>{@link ArmyType#LONGBLOB} first java type is {@code  byte[]},second java type is {@link BlobPath}</li>
     *        <li>MySQL time first java type is {@link java.time.LocalTime},second java type is {@link java.time.Duration}</li>
     *    </ul>
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws DataAccessException throw when indexBasedZero error
     */
    Class<?> getFirstJavaType(int indexBasedZero) throws DataAccessException;

    /**
     * <p>
     * Get the second java type of appropriate column.
     * For example :
     *    <ul>
     *        <li>{@link ArmyType#BIGINT} first java type is {@link Long},second java type is null</li>
     *        <li>{@link ArmyType#LONGTEXT} first java type is {@link String},second java type is {@link TextPath}</li>
     *         <li>{@link ArmyType#LONGBLOB} first java type is {@code  byte[]},second java type is {@link BlobPath}</li>
     *        <li>MySQL time first java type is {@link java.time.LocalTime},second java type is {@link java.time.Duration}</li>
     *    </ul>
     * <br/>
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    Class<?> getSecondJavaType(int indexBasedZero) throws DataAccessException;

    /*-------------------below column label method-------------------*/


    Selection getSelection(String columnLabel) throws DataAccessException;

    DataType getDataType(String columnLabel) throws DataAccessException;

    ArmyType getArmyType(String columnLabel) throws DataAccessException;

    @Nullable
    <T> T getOf(String columnLabel, Option<T> option) throws DataAccessException;

    <T> T getNonNullOf(String columnLabel, Option<T> option) throws DataAccessException;


    /**
     * Get catalog name
     *
     * @param columnLabel column label
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    String getCatalogName(String columnLabel) throws DataAccessException;


    /**
     * Get schema name
     *
     * @param columnLabel column label
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    String getSchemaName(String columnLabel) throws DataAccessException;


    /**
     * Get table name
     *
     * @param columnLabel column label
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    String getTableName(String columnLabel) throws DataAccessException;

    /**
     * Get the column's name.
     *
     * @param columnLabel column label
     * @return column name
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    String getColumnName(String columnLabel) throws DataAccessException;


    /**
     * get precision of column.
     * <p>
     * follow below principle:
     * <ul>
     *     <li>decimal type : return precision of decimal,for example decimal(14,2),return 14</li>
     *     <li>text string type : return maximum char length</li>
     *     <li>binary type : return maximum byte length</li>
     *     <li>bit string type : return maximum bit length</li>
     *     <li>integer and float :  return 0</li>
     *     <li>time/date : return 0</li>
     *     <li>other dialect type : it's up to driver developer</li>
     * </ul>
     * <br/>
     *
     * @param columnLabel column label
     * @return precision
     * @throws DataAccessException throw when indexBasedZero error
     */
    int getPrecision(String columnLabel) throws DataAccessException;


    /**
     * get precision of column.
     * <p>
     * follow below principle:
     * <ul>
     *     <li>decimal type : return scale of decimal,for example decimal(14,2),return 2</li>
     *     <li>integer and float :  return 0</li>
     *     <li>time and timestamp : return micro second precision,for example : time(5) return 5</li>
     *     <li>other dialect type : it's up to driver developer</li>
     * </ul>
     * <br/>
     *
     * @param columnLabel column label
     * @return precision
     * @throws DataAccessException throw when indexBasedZero error
     */
    int getScale(String columnLabel) throws DataAccessException;

    FieldType getFieldType(String columnLabel) throws DataAccessException;

    @Nullable
    Boolean getAutoIncrementMode(String columnLabel) throws DataAccessException;

    /**
     * Get key mode
     *
     * @param columnLabel column label
     * @throws DataAccessException throw when indexBasedZero error
     */
    KeyType getKeyMode(String columnLabel) throws DataAccessException;

    /**
     * Get nullable mode
     *
     * @param columnLabel column label
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    Boolean getNullableMode(String columnLabel) throws DataAccessException;


    /**
     * <p>
     * Get the first java type of appropriate column.
     * For example :
     *    <ul>
     *        <li>{@link ArmyType#BIGINT} first java type is {@link Long},second java type is null</li>
     *        <li>{@link ArmyType#LONGTEXT} first java type is {@link String},second java type is {@link TextPath}</li>
     *         <li>{@link ArmyType#LONGBLOB} first java type is {@code  byte[]},second java type is {@link BlobPath}</li>
     *        <li>MySQL time first java type is {@link java.time.LocalTime},second java type is {@link java.time.Duration}</li>
     *    </ul>
     * <br/>
     *
     * @param columnLabel column label
     * @throws DataAccessException throw when indexBasedZero error
     */
    Class<?> getFirstJavaType(String columnLabel) throws DataAccessException;

    /**
     * <p>
     * Get the second java type of appropriate column.
     * For example :
     *    <ul>
     *        <li>{@link ArmyType#BIGINT} first java type is {@link Long},second java type is null</li>
     *        <li>{@link ArmyType#LONGTEXT} first java type is {@link String},second java type is {@link TextPath}</li>
     *         <li>{@link ArmyType#LONGBLOB} first java type is {@code  byte[]},second java type is {@link BlobPath}</li>
     *        <li>MySQL time first java type is {@link java.time.LocalTime},second java type is {@link java.time.Duration}</li>
     *    </ul>
     * <br/>
     *
     * @param columnLabel column label
     * @throws DataAccessException throw when indexBasedZero error
     */
    @Nullable
    Class<?> getSecondJavaType(String columnLabel) throws DataAccessException;


}
