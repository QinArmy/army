package io.army.modelgen;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.reflect.Field;
import java.util.*;

final class AttributeMetaParser {

    private static final Set<ElementKind> FIELD_KINDS = Collections.singleton(ElementKind.FIELD);

    private final Map<String, Map<String, VariableElement>> domainPropMap = new HashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(AttributeMetaParser.class);


    /**
     * extract entity mapping property elements.
     * <p>
     * five key point :
     * <ol>
     *     <li>check required field , eg : {@link _MetaBridge#DOMAIN_PROPS}</li>
     *     <li>check property override</li>
     *     <li>check column duplication in a tableMeta</li>
     *     <li>check index filed validation</li>
     *     <li>check discriminator validation</li>
     * </ol>
     * </p>
     *
     * @return property element set of {@code tableElement} .
     */
    Collection<VariableElement> generateAttributes(List<TypeElement> entityMappedElementList,
                                                   List<TypeElement> parentMappedElementList,
                                                   Set<String> indexColumnNameSet) {
        final TypeElement domainElement = MetaUtils.domainElement(entityMappedElementList);
        assert domainElement != null;
        final String domainClassName = domainElement.getQualifiedName().toString();
        final Map<String, VariableElement> domainPropMap = this.domainPropMap.get(domainClassName);
        if (domainPropMap != null) {
            return domainPropMap.values();
        }
        try {
            final Set<String> entityPropNameSet = new HashSet<>();
            final Map<String, VariableElement> parentPropMap;
            //1.check parentMeta mapping then  add super entity props to entityPropNameSet to avoid child class duplicate prop
            if (parentMappedElementList.isEmpty()) {
                parentPropMap = Collections.emptyMap();
            } else {
                // 2. extract entity property elements then check entity mapping
                final TypeElement parentElement = MetaUtils.domainElement(parentMappedElementList);
                assert parentElement != null;
                final String parentClassName = parentElement.getQualifiedName().toString();
                Map<String, VariableElement> temp;
                temp = this.domainPropMap.get(parentClassName);
                if (temp == null) {
                    final Set<String> parentIndexNameSet = MetaUtils.createIndexColumnNameSet(parentElement, true).keySet();
                    temp = generateEntityAttributes(parentMappedElementList, entityPropNameSet, parentIndexNameSet);
                    this.domainPropMap.put(parentClassName, Collections.unmodifiableMap(temp));
                } else {
                    entityPropNameSet.addAll(temp.keySet());
                }
                parentPropMap = temp;
            }

            // scan child or simple domain properties.
            Map<String, VariableElement> entityPropMap;
            entityPropMap = generateEntityAttributes(entityMappedElementList, entityPropNameSet, indexColumnNameSet);

            if (parentPropMap.isEmpty()) {
                if (domainElement.getAnnotation(Inheritance.class) != null) {
                    //cache parent domain properties map
                    entityPropMap = Collections.unmodifiableMap(entityPropMap);
                    this.domainPropMap.put(domainClassName, entityPropMap);
                }
            } else {
                final VariableElement idProp = parentPropMap.get(_MetaBridge.ID);
                if (idProp == null) {
                    final TypeElement parentElement = MetaUtils.domainElement(parentMappedElementList);
                    assert parentElement != null;
                    throw Exceptions.domainNoIdProp(parentElement);
                }
                // add id property to child domain
                entityPropMap.put(_MetaBridge.ID, idProp);
                entityPropMap = Collections.unmodifiableMap(entityPropMap);
            }
            return entityPropMap.values();
        } catch (AnnotationMetaException e) {
            LOG.error("entityMappedElementList:{}\nparentMappedElementList:{}",
                    entityMappedElementList, parentMappedElementList, e);
            throw e;
        }

    }


    /**
     * <ul>
     *     <li>{@link Field#getName()} cannot duplication in Entity mapped by tableMeta</li>
     *     <li>{@link Column#name()} cannot duplication in a tableMeta</li>
     * </ul>
     *
     * @param mappedClassElementList unmodifiable list
     * @param entityPropNameSet      modifiable set
     * @param indexColumnNameSet     a unmodifiable set
     * @return a modifiable map,key: property name of class ; value : {@link VariableElement}.
     */
    private static Map<String, VariableElement> generateEntityAttributes(List<TypeElement> mappedClassElementList,
                                                                         Set<String> entityPropNameSet,
                                                                         Set<String> indexColumnNameSet)
            throws AnnotationMetaException {
        final Map<String, VariableElement> mappedPropMap = new HashMap<>();
        // column name set to avoid duplication
        final Set<String> columnNameSet = new HashSet<>();

        final TypeElement domainElement = MetaUtils.domainElement(mappedClassElementList);
        assert domainElement != null;
        final Table table = domainElement.getAnnotation(Table.class);
        final boolean defaultNullable = table.defaultNullable();
        final String discriminatorColumn = discriminatorColumnName(domainElement);

        boolean foundDiscriminatorColumn = false;
        for (TypeElement mappedElement : mappedClassElementList) {

            for (Element element : mappedElement.getEnclosedElements()) {
                if (!FIELD_KINDS.contains(element.getKind())) {
                    continue;
                }
                final VariableElement mappedProp = (VariableElement) element;
                final Column column = mappedProp.getAnnotation(Column.class);
                if (column == null) {
                    continue;
                }
                final String propName = mappedProp.getSimpleName().toString();
                if (entityPropNameSet.contains(propName)) { // assert prop name not duplication
                    // child class duplicate mapping prop allowed by army
                    throw Exceptions.propNotDuplication(domainElement, propName);
                }
                entityPropNameSet.add(propName);

                final String columnName = MetaUtils.columnName(domainElement, propName, column);
                if (columnNameSet.contains(columnName)) {
                    throw Exceptions.columnDuplication(mappedElement, columnName);
                }
                // assert io.army.annotation.Column
                MetaUtils.assertColumn(domainElement, mappedProp, column, columnName, defaultNullable, discriminatorColumn);
                // assert io.army.annotation.DiscriminatorValue , io.army.annotation.Inheritance
                if (columnName.equals(discriminatorColumn)) {
                    if (MetaUtils.getEnumElement(mappedProp) == null) {
                        throw Exceptions.discriminatorNotCodeEnum(domainElement, propName);
                    }
                    foundDiscriminatorColumn = true;
                }
                columnNameSet.add(columnName);
                mappedPropMap.put(propName, mappedProp);
            }
        }
        if (discriminatorColumn != null && !foundDiscriminatorColumn) {
            throw Exceptions.noDiscriminatorColumn(domainElement, discriminatorColumn);
        }
        MetaUtils.assertIndexColumnNameSet(domainElement, columnNameSet, indexColumnNameSet);
        MetaUtils.assertRequiredProps(domainElement, entityPropNameSet, table.immutable());
        return mappedPropMap;
    }


    /**
     * extract entity's discriminator column name
     *
     * @return lower case column name
     */
    @Nullable
    private static String discriminatorColumnName(TypeElement entityElement) {
        final Inheritance inheritance = entityElement.getAnnotation(Inheritance.class);
        final String columnName;
        if (inheritance == null) {
            columnName = null;
        } else {
            columnName = Strings.toLowerCase(inheritance.value());
        }
        return columnName;
    }


}
