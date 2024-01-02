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

package io.army.mapping.postgre;

import io.army.type.DaoLayer;

import javax.annotation.Nullable;

/**
 * <p>
 * This interface representing postgre  range type. The implementations of this interface must provide public static
 * emptyRange() for return empty range instance.
 * <p>
 * <strong>NOTE</strong> :
 *   <ul>
 *       <li>This interface present only in DAO layer,not service layer,business layer,web layer.</li>
 *       <li>Your class must declare the methods of this interface,but your class possibly isn't the subclass of this interface.</li>
 *   </ul>
 * <p> example:
 *     <pre><br/>  <code>
 *    public static final class Int4Range {
 *        public static Int4Range create(&#64;Nullable Integer lower, boolean includeLower,
 *                                        &#64;Nullable Integer upper, boolean includeUpper) {
 *            return new Int4Range(lower, includeLower, upper, includeUpper);
 *        }
 *        private static final Int4Range EMPTY = new Int4Range(null, false, null, false);
 *        //must provide this public static factory method
 *        public static Int4Range emptyRange() {
 *            return EMPTY;
 *        }
 *        private final Integer lower;
 *        private final boolean includeLower;
 *        private final Integer upper;
 *        private final boolean includeUpper;
 *        private Int4Range(&#64;Nullable Integer lower, boolean includeLower, &#64;Nullable Integer upper,
 *                          boolean includeUpper) {
 *            this.lower = lower;
 *            // when lower is null includeLower is false
 *            this.includeLower = lower != null  &amp;&amp; includeLower;
 *            this.upper = upper;
 *             // when upper is null includeUpper is false
 *            this.includeUpper = upper != null  &amp;&amp; includeUpper;
 *        }
 *        public boolean isEmpty() {
 *            return this == EMPTY;
 *        }
 *
 *        public boolean isIncludeLowerBound() {
 *            if (this == EMPTY) {
 *                throw new IllegalStateException();
 *            }
 *            return this.includeLower;
 *        }
 *
 *        public boolean isIncludeUpperBound() {
 *            if (this == EMPTY) {
 *                throw new IllegalStateException();
 *            }
 *            return this.includeUpper;
 *        }
 *        &#64;Nullable
 *        public Integer getLowerBound() {
 *            if (this == EMPTY) {
 *                throw new IllegalStateException();
 *            }
 *            return this.lower;
 *        }
 *        &#64;Nullable
 *        public Integer getUpperBound() {
 *            if (this == EMPTY) {
 *                throw new IllegalStateException();
 *            }
 *            return this.upper;
 *        }
 *        &#64;Override
 *        public int hashCode() {
 *            return Objects.hash(this.lower, this.includeLower, this.upper, this.includeUpper);
 *        }
 *        &#64;Override
 *        public boolean equals(final Object obj) {
 *            final boolean match;
 *            if (obj == this) {
 *                match = true;
 *            } else if (obj instanceof Int4Range) {
 *                final Int4Range o = (Int4Range) obj;
 *                match = Objects.equals(o.lower, this.lower)
 *                        &amp;&amp; o.includeLower == this.includeLower
 *                        &amp;&amp; Objects.equals(o.upper, this.upper)
 *                        &amp;&amp; o.includeUpper == this.includeUpper;
 *            } else {
 *                match = false;
 *            }
 *            return match;
 *        }
 *
 *    }//Int4Range
 *     </code></pre>
 *
 * @see RangeFunction
 * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html">Range Types</a>
 * @since 0.6.0
 */
@DaoLayer
public interface ArmyPostgreRange<T> {

    /**
     * @return true : empty
     * @see <a href="https://www.postgresql.org/docs/15/rangetypes.html#RANGETYPES-IO"> Range Input/Output</a>
     */
    boolean isEmpty();

    /**
     * @return true: when only include lower bound
     * @throws IllegalStateException when {@link #isEmpty()} is true.
     */
    boolean isIncludeLowerBound();

    /**
     * <p>
     * null representing infinity bound.
     *
     *
     * @throws IllegalStateException when {@link #isEmpty()} is true.
     */
    @Nullable
    T getLowerBound();


    /**
     * <p>
     * null representing infinity bound.
     *
     *
     * @throws IllegalStateException when {@link #isEmpty()} is true.
     */
    @Nullable
    T getUpperBound();

    /**
     * @return true: when only include upper bound
     * @throws IllegalStateException when {@link #isEmpty()} is true.
     */
    boolean isIncludeUpperBound();


}
