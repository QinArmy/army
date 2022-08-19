package io.army.mapping;


/**
 * <p>This class is base class class of below:
 *     <ul>
 *         <li>{@link BigDecimalType}</li>
 *         <li>{@link BigIntegerType}</li>
 *         <li>{@link ByteType}</li>
 *         <li>{@link DoubleType}</li>
 *         <li>{@link FloatType}</li>
 *         <li>{@link IntegerType}</li>
 *         <li>{@link LongType}</li>
 *         <li>{@link ShortType}</li>
 *         <li>{@link _UnsignedNumericType}</li>
 *     </ul>
 * </p>
 */
public abstract class _NumericType extends _ArmyNoInjectionMapping {


  public interface _IntegerNumeric {

  }

  public interface _FloatNumeric {

  }

  public interface _UnsignedNumeric {

  }

  public interface _DecimalNumeric {

  }

  /**
   * <p>This class is base class class of below:
   *     <ul>
   *         <li>{@link ByteType}</li>
   *         <li>{@link ShortType}</li>
   *         <li>{@link IntegerType}</li>
   *         <li>{@link LongType}</li>
   *         <li>{@link BigIntegerType}</li>
   *     </ul>
   * </p>
   */
  public static abstract class _IntegerType extends _NumericType implements _IntegerNumeric {

  }

  /**
   * <p>This class is base class class of below:
   *     <ul>
   *         <li>{@link FloatType}</li>
   *         <li>{@link DoubleType}</li>
   *     </ul>
   * </p>
   */
  public static abstract class _FloatNumericType extends _NumericType implements _FloatNumeric {

  }


  /**
   * <p>This class is base class class of below:
   *     <ul>
   *         <li>{@link UnsignedBigDecimalType}</li>
   *         <li>{@link _UnsignedIntegerType}</li>
   *     </ul>
   * </p>
   */
  public static abstract class _UnsignedNumericType extends _NumericType implements _UnsignedNumeric {

  }

  /**
   * <p>This class is base class class of below:
   *     <ul>
   *
   *         <li>{@link UnsignedByteType}</li>
   *         <li>{@link UnsignedShortType}</li>
   *         <li>{@link UnsignedMediumIntType}</li>
   *         <li>{@link UnsignedIntegerType}</li>
   *         <li>{@link UnsignedLongType}</li>
   *         <li>{@link UnsignedBigIntegerType}</li>
   *     </ul>
   * </p>
   */
  public static abstract class _UnsignedIntegerType extends _UnsignedNumericType implements _IntegerNumeric {

  }


}
