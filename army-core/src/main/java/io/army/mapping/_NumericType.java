/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
*/
abstract class _NumericType extends _ArmyNoInjectionMapping {



    /**
     * <p>This class is base class class of below:
     *     <ul>
     *         <li>{@link ByteType}</li>
     *         <li>{@link ShortType}</li>
     *         <li>{@link IntegerType}</li>
     *         <li>{@link LongType}</li>
     *         <li>{@link BigIntegerType}</li>
     *     </ul>
     *
     */
    static abstract class _IntegerType extends _NumericType implements SqlIntegerType {

    }

    /**
     * <p>This class is base class class of below:
     *     <ul>
     *         <li>{@link FloatType}</li>
     *         <li>{@link DoubleType}</li>
     *     </ul>
     *
     */
    static abstract class _FloatNumericType extends _NumericType implements SqlFloatType {

    }


    /**
     * <p>This class is base class class of below:
     *     <ul>
     *         <li>{@link UnsignedBigDecimalType}</li>
     *         <li>{@link _UnsignedIntegerType}</li>
     *     </ul>
     *
     */
    static abstract class _UnsignedNumericType extends _NumericType implements SqlUnsignedNumberType {

    }

    /**
     * <p>This class is base class class of below:
     * <ul>
     *
     *     <li>{@link UnsignedTinyIntType}</li>
     *     <li>{@link UnsignedSmallIntType}</li>
     *     <li>{@link UnsignedMediumIntType}</li>
     *     <li>{@link UnsignedSqlIntType}</li>
     *     <li>{@link UnsignedBigintType}</li>
     *     <li>{@link UnsignedBigIntegerType}</li>
     * </ul>
     */
    static abstract class _UnsignedIntegerType extends _UnsignedNumericType implements SqlIntegerType {

    }


}
