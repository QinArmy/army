package io.army.modelgen;

import io.army.annotation.Column;
import io.army.annotation.Index;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.meta.TableMeta;
import io.army.util.StringUtils;
import org.springframework.lang.NonNull;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.*;

/**
 * this class is a implementation of  {@link MetaEntity}
 * created  on 2018/11/18.
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
        final Map<String, IndexMode> indexMetaMap = createIndexColumnNameSet(this.entityElement);
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
            attribute = new DefaultMetaAttribute(this.entityElement, mappingProp, column
                    , indexMetaMa.get(columnName));
            list.add(attribute);
        }
        return Collections.unmodifiableList(list);
    }

    private static Map<String, IndexMode> createIndexColumnNameSet(TypeElement entityElement) {
        Table table = entityElement.getAnnotation(Table.class);
        Index[] indexArray = table.indexes();
        Map<String, IndexMode> indexMetaMap = new HashMap<>();

        Set<String> indexNameSet = new HashSet<>(indexArray.length + 3);

        StringTokenizer tokenizer;
        for (Index index : indexArray) {
            // make index name lower case
            String indexName = StringUtils.toLowerCase(index.name());
            if (indexNameSet.contains(indexName)) {
                throw new MetaException("entity[%s] indexMap name[%s] duplication",
                        entityElement.getQualifiedName());
            }
            IndexMode indexMode = IndexMode.resolve(index);
            indexNameSet.add(indexName);
            for (String columnName : index.columnList()) {
                tokenizer = new StringTokenizer(columnName.trim(), " ", false);
                // make index field name lower case
                indexMetaMap.put(StringUtils.toLowerCase(tokenizer.nextToken()), indexMode);
            }
        }
        // add id index
        indexMetaMap.put(TableMeta.ID, IndexMode.PRIMARY);
        return Collections.unmodifiableMap(indexMetaMap);
    }


}
