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

package io.army.example.common;

public enum Gender {

    UNKNOWN,
    MALE,
    FEMALE;

    public static Gender fromCertificateNo(String certificateNo) {
        final Gender gender;
        if ((Byte.parseByte(Character.toString(certificateNo.charAt(16))) & 1) == 0) {
            gender = Gender.FEMALE;
        } else {
            gender = Gender.MALE;
        }
        return gender;
    }

}
