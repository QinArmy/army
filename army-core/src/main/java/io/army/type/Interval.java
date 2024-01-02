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

package io.army.type;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;
@Deprecated
public final class Interval implements TemporalAmount {

    @Override
    public long get(TemporalUnit unit) {
        return 0;
    }

    @Override
    public List<TemporalUnit> getUnits() {
        return null;
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        return null;
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        return null;
    }


}
