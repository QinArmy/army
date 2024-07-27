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

import io.army.annotation.*;
import io.army.lang.Nullable;
import io.army.struct.CodeEnum;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

final class AnnotationHandler {

    private final ProcessingEnvironment env;

    final List<String> errorMsgList = ArmyCollections.arrayList();

    private final Map<String, Map<String, VariableElement>> parentFieldCache = ArmyCollections.hashMap();

    private final Map<String, Map<Integer, TypeElement>> codeMap = ArmyCollections.hashMap();

    AnnotationHandler(ProcessingEnvironment env) {
        this.env = env;
    }


    void createSourceFiles(Set<? extends Element> domainElementSet) throws IOException {
        final SourceCodeCreator codeCreator = new SourceCodeCreator(this.env.getSourceVersion(), this.env.getFiler());
        final List<String> errorMsgList = this.errorMsgList;
        final TypeElement[] outParent = new TypeElement[1];

        TypeElement domain, parentDomain;
        Map<String, VariableElement> fieldMap;
        List<TypeElement> mappedList;
        Map<String, IndexMode> indexModeMap;
        MappingMode mode;
        String customeTableName, lowerTableName;
        for (Element element : domainElementSet) {
            domain = (TypeElement) element;
            if (domain.getNestingKind() != NestingKind.TOP_LEVEL) {
                continue;
            }
            if (domain.getAnnotation(Inheritance.class) != null) {
                parentDomain = null;
                fieldMap = getParentFieldMap(domain);
            } else {
                outParent[0] = null;
                mappedList = getMappedList(domain, outParent);
                parentDomain = outParent[0];
                if (parentDomain == null) {
                    fieldMap = getFieldSet(mappedList, null);
                } else {
                    fieldMap = getFieldSet(mappedList, getParentFieldMap(parentDomain));
                }
            }

            mode = validateMode(domain, parentDomain);
            // validate table name
            customeTableName = domain.getAnnotation(Table.class).name();
            lowerTableName = customeTableName.toLowerCase(Locale.ROOT);
            if (!customeTableName.equals(lowerTableName) && !customeTableName.equals(customeTableName.toUpperCase(Locale.ROOT))) {  // army don't allow camel
                String m = String.format("%s table name is CamelCase.", MetaUtils.getClassName(domain));
                this.errorMsgList.add(m);
            }
            if (!lowerTableName.trim().equals(lowerTableName)) {
                String m = String.format("please trim %s table name .", MetaUtils.getClassName(domain));
                this.errorMsgList.add(m);
            }
            if (mode == null || errorMsgList.size() > 0) {
                // occur error
                continue;
            }
            indexModeMap = createFieldToIndexModeMap(domain);
            for (String fieldName : indexModeMap.keySet()) {
                if (!fieldMap.containsKey(fieldName)) {
                    String m = String.format("Not found index field[%s] in %s."
                            , fieldName, MetaUtils.getClassName(domain));
                    errorMsgList.add(m);
                }
            }
            if (errorMsgList.size() > 0) {
                continue;
            }

            codeCreator.create(domain, fieldMap, parentDomain, mode, indexModeMap);

        }

        if (errorMsgList.size() == 0) {
            codeCreator.flush(); // finally flush
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
        for (TypeMirror superMirror = superElement.getSuperclass(); ; superMirror = superElement.getSuperclass()) {
            if (superMirror instanceof NoType) {
                break;
            }
            superElement = (TypeElement) ((DeclaredType) superMirror).asElement();
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
                mappedList = ArmyCollections.arrayList();
                mappedList.add(tableElement);
            }
            mappedList.add(superElement);
        }//for

        if (mappedList == null) {
            mappedList = Collections.singletonList(tableElement);
        } else {
            mappedList = Collections.unmodifiableList(mappedList);
        }

        outParent[0] = parentElement;
        return mappedList;

    }

    @Nullable
    private MappingMode validateMode(final TypeElement domain, final @Nullable TypeElement parent) {

        final Inheritance inheritance;
        inheritance = domain.getAnnotation(Inheritance.class);
        final DiscriminatorValue discriminatorValue;
        discriminatorValue = domain.getAnnotation(DiscriminatorValue.class);

        final MappingMode mode;
        if (parent == null && inheritance == null) {
            mode = MappingMode.SIMPLE;
            if (discriminatorValue != null) {
                String m = String.format("Domain %s no parent,couldn't be annotated by %s."
                        , MetaUtils.getClassName(domain), DiscriminatorValue.class.getName());
                this.errorMsgList.add(m);
            }
        } else if (parent == null) {
            mode = MappingMode.PARENT;
            if (discriminatorValue != null && discriminatorValue.value() != 0) {
                String m;
                m = String.format("Domain %s discriminator value must be zero.", MetaUtils.getClassName(domain));
                this.errorMsgList.add(m);
            }
        } else if (discriminatorValue != null) {
            mode = MappingMode.CHILD;
            final int value;
            value = discriminatorValue.value();
            if (value == 0) {
                String m;
                m = String.format("Domain %s discriminator value couldn't be zero.", MetaUtils.getClassName(domain));
                this.errorMsgList.add(m);
            } else {
                final TypeElement element;
                element = this.codeMap.computeIfAbsent(MetaUtils.getClassName(parent), key -> new HashMap<>())
                        .putIfAbsent(value, domain);
                if (element != null && element != domain) {
                    String m = String.format("Domain %s discriminator value[%s] duplication."
                            , MetaUtils.getClassName(domain), value);
                    this.errorMsgList.add(m);
                }
            }
        } else {
            mode = null;
            String m = String.format("Domain %s no parent,couldn't be annotated by %s."
                    , MetaUtils.getClassName(domain), DiscriminatorValue.class.getName());
            this.errorMsgList.add(m);
        }
        return mode;
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
        final Map<String, VariableElement> fieldMap = ArmyCollections.hashMap();
        final Map<String, Boolean> columnNameMap = ArmyCollections.hashMap();

        for (TypeElement mapped : mappedList) {
            className = MetaUtils.getClassName(mapped);

            for (Element element : mapped.getEnclosedElements()) {
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
                    , MetaUtils.getClassName(mappedList.get(0)), discriminatorField));
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


    private void validateField(final String className, final String fieldName, final VariableElement field, final Column column, final boolean discriminatorField) {
        switch (fieldName) {
            case _MetaBridge.ID: {
                if (field.asType().getKind().isPrimitive()) {
                    this.errorMsgList.add(String.format("Field %s.%s couldn't be primitive.", className, fieldName));
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
                if (!discriminatorField && !MetaUtils.hasText(column.comment())) {
                    noCommentError(className, field);
                }
//                if (field.asType().getKind().isPrimitive()) {
//                    this.errorMsgList.add(String.format("Field %s.%s couldn't be primitive."
//                            , className, fieldName));
//                }
            }
        }
    }


    /**
     * @return lower case column
     */
    private String getColumnName(final String className, final String fieldName, final Column column) {
        final String customColumnName, lowerCaseColumnName;
        customColumnName = column.name();
        if (customColumnName.isEmpty()) {
            lowerCaseColumnName = _MetaBridge.camelToLowerCase(fieldName);
        } else {
            lowerCaseColumnName = customColumnName.toLowerCase(Locale.ROOT);
            if (!customColumnName.equals(lowerCaseColumnName)
                    && !customColumnName.equals(customColumnName.toUpperCase(Locale.ROOT))) { // army don't allow camel
                String m = String.format("Field %s.%s column name[%s] is camel.", className, fieldName, customColumnName);
                this.errorMsgList.add(m);
            }
            if (!lowerCaseColumnName.trim().equals(lowerCaseColumnName)) {
                String m = String.format("please trim Field %s.%s column name[%s].", className, fieldName, customColumnName);
                this.errorMsgList.add(m);
            }
        }
        return lowerCaseColumnName;
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
        if (typeMirror instanceof DeclaredType) {
            final Element element = ((DeclaredType) typeMirror).asElement();
            if (element.getKind() != ElementKind.ENUM
                    || !MetaUtils.isCodeEnumType((TypeElement) element)) {
                discriminatorNonCodeNum(className, field);
            }
        } else {
            discriminatorNonCodeNum(className, field);
        }

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


    private Map<String, IndexMode> createFieldToIndexModeMap(final TypeElement domain) {
        final Table table = domain.getAnnotation(Table.class);
        final Index[] indexArray = table.indexes();
        if (indexArray.length == 0) {
            return Collections.emptyMap();
        }
        final Map<String, IndexMode> indexMetaMap = ArmyCollections.hashMap();

        final Map<String, Boolean> indexNameMap = ArmyCollections.hashMap(indexArray.length + 3);
        String[] fieldArray;
        String indexName;
        IndexMode indexMode;
        for (Index index : indexArray) {
            // make index name lower case
            indexName = index.name().toLowerCase(Locale.ROOT);
            if (indexNameMap.putIfAbsent(indexName, Boolean.TRUE) != null) {
                String m = String.format("Domain %s index name[%s] duplication",
                        domain.getQualifiedName(), indexName);
                this.errorMsgList.add(m);
            }
            indexMode = IndexMode.resolve(index);
            for (String fieldName : index.fieldList()) {
                fieldArray = fieldName.split(" ");
                indexMetaMap.put(fieldArray[0], indexMode);
            }
        }
        indexMetaMap.put(_MetaBridge.ID, IndexMode.PRIMARY);
        return Collections.unmodifiableMap(indexMetaMap);
    }


}
