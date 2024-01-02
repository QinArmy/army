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

package io.army.modelgen;

import io.army.struct.CodeEnum;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

abstract class MetaUtils {

    private MetaUtils() {
        throw new UnsupportedOperationException();
    }

    static String getClassName(final TypeElement domain) {
        String className;
        className = domain.getQualifiedName().toString();
        if (className.lastIndexOf('>') > 0) {
            className = className.substring(0, className.indexOf('<'));
        }
        return className;
    }

    static String getSimpleClassName(final TypeElement domain) {
        String className;
        className = domain.getSimpleName().toString();
        if (className.lastIndexOf('>') > 0) {
            className = className.substring(0, className.indexOf('<'));
        }
        return className;
    }


    static boolean hasText(@Nullable String str) {
        boolean match = false;
        final int strLen;
        if (str != null && (strLen = str.length()) > 0) {
            for (int i = 0; i < strLen; i++) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }


    static boolean isCodeEnumType(final TypeElement typeElement) {
        final String codeEnum = CodeEnum.class.getName();
        boolean match = false;
        Element element;
        for (TypeMirror mirror : typeElement.getInterfaces()) {
            if (codeEnum.equals(mirror.toString())) {
                match = true;
                break;
            }
            if (!(mirror instanceof DeclaredType)) {
                continue;
            }
            element = ((DeclaredType) mirror).asElement();
            if (element.getKind() != ElementKind.INTERFACE) {
                continue;
            }
            if (isCodeEnumType((TypeElement) element)) {
                match = true;
                break;
            }
        }
        return match;
    }


}
