package io.army.modelgen;

import io.army.annotation.*;
import io.army.struct.CodeEnum;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

final class AnnotationHandler {

    final ProcessingEnvironment env;

    final Types types;

    private final List<String> errorMsgList = new ArrayList<>();

    AnnotationHandler(ProcessingEnvironment env) {
        this.env = env;
        this.types = env.getTypeUtils();
    }


    void createSourceFiles(Set<? extends Element> tableSet) throws IOException {
        final ProcessingEnvironment env = this.env;
        StringBuilder builder;
        final Filer filer = env.getFiler();
        FileObject fileObject;
        String className;
        TypeElement tableElement;

        for (Element element : tableSet) {
            tableElement = (TypeElement) element;
            className = tableElement.getQualifiedName().toString();
            if (className.lastIndexOf('>') > 0) {
                className = className.substring(0, className.indexOf('<'));
            }
            builder = new StringBuilder(1024);
            fileObject = filer.createSourceFile(className + _MetaBridge.META_CLASS_NAME_SUFFIX);
            try (PrintWriter pw = new PrintWriter(fileObject.openOutputStream())) {
                pw.println(builder);
            }

        }

    }

    private Map<String, VariableElement> getFieldSet(final TypeElement tableElement) {
        final DiscriminatorValue discriminatorValue;
        discriminatorValue = tableElement.getAnnotation(DiscriminatorValue.class);
        final Inheritance inheritance;
        inheritance = tableElement.getAnnotation(Inheritance.class);
        final String discriminatorField = inheritance == null ? null : inheritance.value();
        final int discriminatorNum = discriminatorValue == null ? 0 : discriminatorValue.value();

        final Types types = this.env.getTypeUtils();
        VariableElement field;
        TypeMirror superMirror;
        Column column;
        boolean foundDiscriminatorColumn = false;
        String className, fieldName, columnName, customColumnName;
        final Map<String, VariableElement> fieldMap = new HashMap<>();
        final Map<String, Boolean> columnNameMap = new HashMap<>();

        for (TypeElement current = tableElement; ; ) {
            className = current.getQualifiedName().toString();
            if (className.lastIndexOf('>') > 0) {
                className = className.substring(0, className.indexOf('<'));
            }
            for (Element element : current.getEnclosedElements()) {
                if (element.getKind() != ElementKind.FIELD
                        || (column = element.getAnnotation(Column.class)) == null) {
                    continue;
                }
                if (element.getModifiers().contains(Modifier.STATIC)) {
                    String m = String.format("%s.%s couldn't is static.", className, element.getSimpleName());
                    this.errorMsgList.add(m);
                    continue;
                }
                field = (VariableElement) element;
                fieldName = field.getSimpleName().toString();
                if (fieldName.lastIndexOf('>') > 0) {
                    fieldName = fieldName.substring(0, fieldName.indexOf('<'));
                }
                if (fieldMap.putIfAbsent(fieldName, field) != null) {
                    this.errorMsgList.add(String.format("Field %s.%s is overridden.", className, fieldName));
                }
                // get column name
                customColumnName = column.name();
                if (_MetaBridge.RESERVED_PROPS.contains(fieldName)) {
                    columnName = _MetaBridge.camelToLowerCase(fieldName);
                    if (!customColumnName.equals(columnName)) {
                        String m = String.format("Field %s.%s is reserved field,so must use column name[%s]."
                                , className, fieldName, columnName);
                        this.errorMsgList.add(m);
                    }
                } else if (customColumnName.isEmpty()) {
                    columnName = _MetaBridge.camelToLowerCase(fieldName);
                } else {
                    columnName = customColumnName.toLowerCase(Locale.ROOT);
                }
                if (columnNameMap.putIfAbsent(columnName, Boolean.TRUE) != null) {
                    String m;
                    m = String.format("Field %s.%s column[%s] duplication.", className, fieldName, customColumnName);
                    this.errorMsgList.add(m);
                }
                switch (fieldName) {
                    case _MetaBridge.ID:
                        //no-op
                        break;
                    case _MetaBridge.CREATE_TIME:
                    case _MetaBridge.UPDATE_TIME:
                        assertDateTime(className, field);
                        break;
                    case _MetaBridge.VERSION:
                        assertVersionField(className, field);
                        break;
                    case _MetaBridge.VISIBLE:
                        assertVisibleField(className, field);
                        break;
                    default: {
                        if (discriminatorField != null && discriminatorField.equals(fieldName)) {
                            foundDiscriminatorColumn = true;
                            assertCodeEnum(className, field);
                        } else if (!Strings.hasText(column.comment())) {
                            noCommentError(className, field);
                        }
                    }
                }

            }// for getEnclosedElements

            superMirror = current.getSuperclass();
            if (superMirror instanceof NoType) {
                break;
            }
            current = (TypeElement) types.asElement(superMirror);
            if (current.getAnnotation(MappedSuperclass.class) == null
                    && current.getAnnotation(Table.class) == null) {
                break;
            }

        }
        if (inheritance != null && !foundDiscriminatorColumn) {
            this.errorMsgList.add(String.format("Domain %s discriminator field[%s] not found."
                    , className, discriminatorField));
        }
        return Collections.unmodifiableMap(fieldMap);
    }

    private void assertDateTime(final String className, final VariableElement field) {
        final String fieldJavaClassName;
        fieldJavaClassName = field.asType().toString();
        if (!(fieldJavaClassName.equals(LocalDateTime.class.getName())
                || fieldJavaClassName.equals(OffsetDateTime.class.getName())
                || fieldJavaClassName.equals(ZonedDateTime.class.getName()))) {
            String m;
            m = String.format("Field %s.%s support only below java type:\n%s\n%s\n%s."
                    , className, field.getSimpleName()
                    , LocalDateTime.class.getName()
                    , OffsetDateTime.class.getName()
                    , ZonedDateTime.class.getName()
            );
            this.errorMsgList.add(m);
        }

    }

    private void assertVersionField(final String className, final VariableElement field) {
        final String fieldJavaClassName;
        fieldJavaClassName = field.asType().toString();
        if (!(fieldJavaClassName.equals(Integer.class.getName())
                || fieldJavaClassName.equals(Long.class.getName())
                || fieldJavaClassName.equals(BigInteger.class.getName()))) {
            String m;
            m = String.format("Field %s.%s support only below java type:\n%s\n%s\n%s."
                    , className, field.getSimpleName()
                    , Integer.class.getName()
                    , Long.class.getName()
                    , BigInteger.class.getName()
            );
            this.errorMsgList.add(m);
        }
    }

    private void assertVisibleField(final String className, final VariableElement field) {
        if (!field.asType().toString().equals(Boolean.class.getName())) {
            String m;
            m = String.format("Field %s.%s support only %s."
                    , className, field.getSimpleName()
                    , Boolean.class.getName()
            );
            this.errorMsgList.add(m);
        }
    }

    private void assertCodeEnum(final String className, final VariableElement field) {
        final TypeMirror typeMirror = field.asType();
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            discriminatorNonCodeNum(className, field);
        } else {
            final Element element = this.types.asElement(typeMirror);
            if (element.getKind() != ElementKind.ENUM
                    || !isCodeEnumType((TypeElement) element)) {
                discriminatorNonCodeNum(className, field);
            }
        }

    }


    private boolean isCodeEnumType(final TypeElement typeElement) {
        final String codeEnum = CodeEnum.class.getName();
        boolean match = false;
        final Types types = this.env.getTypeUtils();
        Element element;
        for (TypeMirror mirror : typeElement.getInterfaces()) {
            if (codeEnum.equals(mirror.toString())) {
                match = true;
                break;
            }
            if (mirror.getKind() != TypeKind.DECLARED) {
                continue;
            }
            element = types.asElement(mirror);
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

    private void discriminatorNonCodeNum(final String className, final VariableElement field) {
        String m = String.format("Discriminator field %s.%s don't implements %s."
                , className, field.getSimpleName(), CodeEnum.class.getName());
        this.errorMsgList.add(m);
    }

    private void noCommentError(final String className, final VariableElement field) {
        String m = String.format("Field %s.%s isn't reserved field or discriminator field,so comment must have text."
                , className, field.getSimpleName());
        this.errorMsgList.add(m);
    }


}
