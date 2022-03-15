package io.army.modelgen;

import io.army.lang.Nullable;
import io.army.struct.CodeEnum;

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
