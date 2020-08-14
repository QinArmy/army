package io.army.modelgen;

import io.army.annotation.Column;
import io.army.annotation.Index;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.meta.MetaException;
import io.army.meta.TableMeta;
import io.army.util.StringUtils;
import org.springframework.lang.NonNull;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

/**
 * this class is a implementation of  {@link MetaEntity}
 *
 * @since 1.0
 */
class DefaultMetaEntity implements MetaEntity {

    private final TypeElement entityElement;

    private final String importsBlock;

    private final String classDefinition;

    private final String body;


    /**
     * @param entityMappedElementList {@link io.army.annotation.MappedSuperclass} list ,asSort by  class extends
     * @param parentMappedElementList {@link io.army.annotation.Inheritance}
     */
    DefaultMetaEntity(@NonNull List<TypeElement> entityMappedElementList,
                      @NonNull List<TypeElement> parentMappedElementList) throws MetaException {
        this.entityElement = SourceCreateUtils.entityElement(entityMappedElementList);

        if (this.entityElement == null) {
            throw new MetaException("entityMappedElementList error");
        }
        // indexColumnNameSet help then step
        final Map<String, IndexMode> indexMetaMap = createIndexColumnNameSet(this.entityElement
                , parentMappedElementList.isEmpty());
        // prepare mapping prop element
        final Set<VariableElement> mappingPropSet = SourceCreateUtils.generateAttributes(
                entityMappedElementList,
                parentMappedElementList,
                indexMetaMap.keySet()
        );

        final TypeElement parentEntityElement = SourceCreateUtils.entityElement(parentMappedElementList);

        // 1. generate import part
        this.importsBlock = SourceCreateUtils.generateImport(this.entityElement, parentEntityElement, mappingPropSet);
        // 2. generate class definition part

        this.classDefinition = SourceCreateUtils.generateClassDefinition(this.entityElement, parentEntityElement);
        // 3. generate body of class part
        this.body = SourceCreateUtils.generateBody(this.entityElement,
                parentEntityElement,
                generateMappingPropList(mappingPropSet, indexMetaMap));
    }

    @Override
    public String getQualifiedName() {
        return SourceCreateUtils.getQualifiedName(entityElement);
    }

    @Override
    public String getClassDefinition() {
        return this.classDefinition;
    }



    @Override
    public String getImportBlock() {
        return this.importsBlock;
    }


    @Override
    public String getBody() {
        return this.body;
    }

    /**
     * @return a unmodifiable List
     */
    private List<MetaAttribute> generateMappingPropList(Set<VariableElement> mappingPropSet,
                                                        Map<String, IndexMode> indexMetaMa) {

        List<MetaAttribute> list = new ArrayList<>(mappingPropSet.size());
        Column column;
        MetaAttribute attribute;
        String columnName;
        for (VariableElement mappingProp : mappingPropSet) {
            column = mappingProp.getAnnotation(Column.class);
            columnName = SourceCreateUtils.columnName(this.entityElement, mappingProp, column);
            // make column name lower case
            columnName = StringUtils.toLowerCase(columnName);
            IndexMode indexMode = indexMetaMa.get(columnName);
            if (indexMode == null && mappingProp.getSimpleName().toString().equals(TableMeta.ID)) {
                indexMode = IndexMode.PRIMARY;
            }
            attribute = new DefaultMetaAttribute(this.entityElement, mappingProp, column
                    , indexMode);
            list.add(attribute);
        }
        return Collections.unmodifiableList(list);
    }

    private static Map<String, IndexMode> createIndexColumnNameSet(TypeElement domainElement, boolean noParent) {
        Table table = domainElement.getAnnotation(Table.class);
        Index[] indexArray = table.indexes();
        Map<String, IndexMode> indexMetaMap = new HashMap<>();

        Set<String> indexNameSet = new HashSet<>(indexArray.length + 3);

        StringTokenizer tokenizer;
        for (Index index : indexArray) {
            // make index name lower case
            String indexName = StringUtils.toLowerCase(index.name());
            if (indexNameSet.contains(indexName)) {
                throw new MetaException("entity[%s] indexMap name[%s] duplication",
                        domainElement.getQualifiedName());
            }
            IndexMode indexMode = IndexMode.resolve(index);
            indexNameSet.add(indexName);
            for (String columnName : index.columnList()) {
                tokenizer = new StringTokenizer(columnName.trim(), " ", false);
                // make index field name lower case
                indexMetaMap.put(StringUtils.toLowerCase(tokenizer.nextToken()), indexMode);
            }
        }
        if (noParent || domainElement.getAnnotation(Inheritance.class) != null) {
            indexMetaMap.put(TableMeta.ID, IndexMode.PRIMARY);
        }
        return Collections.unmodifiableMap(indexMetaMap);
    }


}
