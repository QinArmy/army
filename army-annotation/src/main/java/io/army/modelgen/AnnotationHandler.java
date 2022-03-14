package io.army.modelgen;

import io.army.annotation.*;
import io.army.struct.CodeEnum;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

final class AnnotationHandler {

    private final ProcessingEnvironment env;

    private final Types types;

    final List<String> errorMsgList = new ArrayList<>();

    private final Map<String, Map<String, VariableElement>> parentFieldCache = new HashMap<>();

    private final Map<String, Map<Integer, TypeElement>> codeMap = new HashMap<>();

    AnnotationHandler(ProcessingEnvironment env) {
        this.env = env;
        this.types = env.getTypeUtils();
    }


    void createSourceFiles(Set<? extends Element> tableSet) throws IOException {
        final SourceCodeBuilder builder = new SourceCodeBuilder(this.types, this.env.getFiler());
        final List<String> errorMsgList = this.errorMsgList;
        final TypeElement[] outParent = new TypeElement[1];

        TypeElement tableElement, parentElement;
        Map<String, VariableElement> fieldMap;
        List<TypeElement> mappedList;

        MappingMode mappingMode;
        for (Element element : tableSet) {
            tableElement = (TypeElement) element;
            if (tableElement.getNestingKind() != NestingKind.TOP_LEVEL) {
                continue;
            }
            outParent[0] = null;
            if (tableElement.getAnnotation(Inheritance.class) != null) {
                parentElement = null;
                fieldMap = getParentFieldMap(tableElement);
            } else {
                mappedList = getMappedList(tableElement, outParent);
                parentElement = outParent[0];
                if (parentElement == null) {
                    fieldMap = getFieldSet(mappedList, null);
                } else {
                    fieldMap = getFieldSet(mappedList, getParentFieldMap(parentElement));
                }
            }

            mappingMode = validateTableElement(tableElement, parentElement);
            if (mappingMode == null || errorMsgList.size() > 0) {
                // occur error
                continue;
            }
            builder.reset(tableElement, fieldMap, parentElement, mappingMode)
                    .build();


        }

    }


    private void addErrorInheritance(final TypeElement tableElement, final TypeElement superElement) {
        String m = String.format("Domain %s couldn't be annotated by %s,because %s is annotated by %s."
                , tableElement.getQualifiedName(), Inheritance.class.getName()
                , superElement.getQualifiedName(), Inheritance.class.getName());
        this.errorMsgList.add(m);
    }

    private List<TypeElement> getMappedList(final TypeElement tableElement, final TypeElement[] outParent) {
        TypeElement superElement = tableElement, parentElement = null;
        List<TypeElement> mappedList = null;
        final Types types = this.types;
        for (TypeMirror superMirror = superElement.getSuperclass(); ; ) {
            if (superMirror instanceof NoType) {
                break;
            }
            superElement = (TypeElement) types.asElement(superMirror);
            if (superElement.getNestingKind() != NestingKind.TOP_LEVEL) {
                break;
            }
            if (superElement.getAnnotation(Inheritance.class) != null) {
                if (tableElement.getAnnotation(Inheritance.class) != null) {
                    addErrorInheritance(tableElement, superElement);
                }
                parentElement = superElement;
                break;
            }
            if (superElement.getAnnotation(MappedSuperclass.class) == null
                    && superElement.getAnnotation(Table.class) == null) {
                break;
            }
            if (mappedList == null) {
                mappedList = new ArrayList<>();
                mappedList.add(tableElement);
            }
            mappedList.add(superElement);
        }

        if (mappedList == null) {
            mappedList = Collections.singletonList(tableElement);
        } else {
            mappedList = Collections.unmodifiableList(mappedList);
        }
        outParent[0] = parentElement;
        return mappedList;

    }

    @Nullable
    private MappingMode validateTableElement(final TypeElement tableElement, final @Nullable TypeElement parentElement) {

        final Inheritance inheritance;
        inheritance = tableElement.getAnnotation(Inheritance.class);
        final DiscriminatorValue discriminatorValue;
        discriminatorValue = tableElement.getAnnotation(DiscriminatorValue.class);

        final MappingMode mappingMode;
        if (parentElement == null && inheritance == null) {
            mappingMode = MappingMode.SIMPLE;
            if (discriminatorValue != null) {
                String m = String.format("Domain %s no parent,couldn't be annotated by %s."
                        , tableElement.getQualifiedName(), DiscriminatorValue.class.getName());
                this.errorMsgList.add(m);
            }
        } else if (parentElement == null) {
            mappingMode = MappingMode.PARENT;
            if (discriminatorValue != null && discriminatorValue.value() != 0) {
                String m;
                m = String.format("Domain %s discriminator value must be zero.", tableElement.getQualifiedName());
                this.errorMsgList.add(m);
            }
        } else if (discriminatorValue != null) {
            mappingMode = MappingMode.CHILD;
            final int value;
            value = discriminatorValue.value();
            if (value == 0) {
                String m;
                m = String.format("Domain %s discriminator value couldn't be zero.", tableElement.getQualifiedName());
                this.errorMsgList.add(m);
            } else {
                final TypeElement element;
                element = this.codeMap.computeIfAbsent(MetaUtils.getClassName(parentElement), key -> new HashMap<>())
                        .putIfAbsent(value, tableElement);
                if (element != null && element != tableElement) {
                    String m = String.format("Domain %s discriminator value[%s] duplication."
                            , tableElement.getQualifiedName(), value);
                    this.errorMsgList.add(m);
                }
            }
        } else {
            mappingMode = null;
            String m;
            m = String.format("Domain %s isn' annotated by %s."
                    , tableElement.getQualifiedName(), DiscriminatorValue.class.getName());
            this.errorMsgList.add(m);
        }
        return mappingMode;
    }

    private Map<String, VariableElement> getFieldSet(final List<TypeElement> mappedList
            , final @Nullable Map<String, VariableElement> parentFieldMap) {

        final TypeElement tableElement;
        tableElement = mappedList.get(0);
        final Inheritance inheritance;
        inheritance = tableElement.getAnnotation(Inheritance.class);
        final String discriminatorField = inheritance == null ? null : inheritance.value();
        final Table table;
        table = tableElement.getAnnotation(Table.class);

        VariableElement field;
        Column column;
        boolean foundDiscriminatorColumn = false;
        String className, fieldName, columnName;
        final Map<String, VariableElement> fieldMap = new HashMap<>();
        final Map<String, Boolean> columnNameMap = new HashMap<>();

        for (TypeElement current : mappedList) {
            className = current.getQualifiedName().toString();
            if (className.lastIndexOf('>') > 0) {
                className = className.substring(0, className.indexOf('<'));
            }

            for (Element element : current.getEnclosedElements()) {
                if (element.getKind() != ElementKind.FIELD
                        || element.getModifiers().contains(Modifier.STATIC)
                        || (column = element.getAnnotation(Column.class)) == null) {
                    continue;
                }
                field = (VariableElement) element;
                fieldName = field.getSimpleName().toString();

                if ((parentFieldMap != null && parentFieldMap.containsKey(fieldName))
                        || fieldMap.putIfAbsent(fieldName, field) != null) {
                    this.errorMsgList.add(String.format("Field %s.%s is overridden.", className, fieldName));
                }
                // get column name
                columnName = getColumnName(className, fieldName, column);
                if (columnNameMap.putIfAbsent(columnName, Boolean.TRUE) != null) {
                    String m = String.format("Field %s.%s column[%s] duplication.", className, fieldName, columnName);
                    this.errorMsgList.add(m);
                }
                if (discriminatorField != null && discriminatorField.equals(fieldName)) {
                    foundDiscriminatorColumn = true;
                    assertCodeEnum(className, field);
                    validateField(className, fieldName, field, column, true);
                } else {
                    validateField(className, fieldName, field, column, false);
                }


            }// for getEnclosedElements

        }

        if (inheritance != null && !foundDiscriminatorColumn) {
            this.errorMsgList.add(String.format("Domain %s discriminator field[%s] not found."
                    , mappedList.get(0).getQualifiedName(), discriminatorField));
        }
        columnNameMap.clear();

        if (parentFieldMap == null) {
            String m;
            if (!fieldMap.containsKey(_MetaBridge.ID)) {
                m = String.format("Domain %s don't definite field %s."
                        , tableElement.getQualifiedName(), _MetaBridge.ID);
                this.errorMsgList.add(m);
            }
            if (!fieldMap.containsKey(_MetaBridge.CREATE_TIME)) {
                m = String.format("Domain %s don't definite field %s."
                        , tableElement.getQualifiedName(), _MetaBridge.CREATE_TIME);
                this.errorMsgList.add(m);
            }
            if (!table.immutable() && !fieldMap.containsKey(_MetaBridge.UPDATE_TIME)) {
                m = String.format("Domain %s don't definite field %s."
                        , tableElement.getQualifiedName(), _MetaBridge.UPDATE_TIME);
                this.errorMsgList.add(m);
            }
        } else {
            field = parentFieldMap.get(_MetaBridge.ID);
            assert field != null;
            fieldMap.put(_MetaBridge.ID, field);
        }
        return Collections.unmodifiableMap(fieldMap);
    }


    private Map<String, VariableElement> getParentFieldMap(final TypeElement parent) {
        final String className;
        className = MetaUtils.getClassName(parent);

        Map<String, VariableElement> fieldMap;
        fieldMap = this.parentFieldCache.get(className);
        if (fieldMap == null) {
            final TypeElement[] outParent = new TypeElement[1];
            List<TypeElement> mappedList;
            mappedList = getMappedList(parent, outParent);
            if (outParent[0] != null) {
                addErrorInheritance(parent, outParent[0]);
            }
            fieldMap = getFieldSet(mappedList, null);
            this.parentFieldCache.put(className, fieldMap);
        }
        return fieldMap;
    }


    private void validateField(final String className, final String fieldName, final VariableElement field
            , final Column column, final boolean discriminatorField) {
        switch (fieldName) {
            case _MetaBridge.ID: {
                if (field.asType().getKind().isPrimitive()) {
                    this.errorMsgList.add(String.format("Field %s.%s couldn't be primitive."
                            , className, fieldName));
                }
            }
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
                if (!discriminatorField && !Strings.hasText(column.comment())) {
                    noCommentError(className, field);
                }
                if (field.asType().getKind().isPrimitive()) {
                    this.errorMsgList.add(String.format("Field %s.%s couldn't be primitive."
                            , className, fieldName));
                }
            }
        }
    }


    /**
     * @return lower case column
     */
    private String getColumnName(final String className, final String fieldName, final Column column) {
        final String customColumnName, columnName;
        customColumnName = column.name();
        if (customColumnName.isEmpty()) {
            columnName = _MetaBridge.camelToLowerCase(fieldName);
        } else if (_MetaBridge.RESERVED_PROPS.contains(fieldName)) {
            columnName = _MetaBridge.camelToLowerCase(fieldName);
            if (!customColumnName.equals(columnName)) {
                String m = String.format("Field %s.%s is reserved field,so must use column name[%s]."
                        , className, fieldName, columnName);
                this.errorMsgList.add(m);
            }
        } else {
            columnName = customColumnName.toLowerCase(Locale.ROOT);
        }
        return columnName;
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
        final Types types = this.types;
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
