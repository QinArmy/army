package io.army.modelgen;

import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.annotation.Index;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util.ClassUtils;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * this class is a implementation of  {@link MetaEntity}
 * created  on 2018/11/18.
 */
class DefaultMetaEntity implements MetaEntity {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultMetaEntity.class);

    private final TypeElement entityElement;

    private final String importsBlock;

    private final String classDefinition;

    private final String body;


    /**
     * @param entityMappedElementList {@link io.army.annotation.MappedSuperclass} list ,order by  class extends
     * @param parentMappedElementList {@link io.army.annotation.Inheritance}
     */
    DefaultMetaEntity(@NonNull List<TypeElement> entityMappedElementList,
                      @NonNull List<TypeElement> parentMappedElementList) throws MetaException {
        this.entityElement = SourceCreateUtils.entityElement(entityMappedElementList);
        LOG.trace("entityElement : {}", entityMappedElementList);

        if (this.entityElement == null) {
            throw new MetaException(ErrorCode.META_ERROR, "entityMappedElementList error");
        }
        // indexColumnNameSet help then step
        final Set<String> indexColumnNameSet = createIndexColumnNameSet(this.entityElement);
        // prepare mapping prop element
        final Set<VariableElement> mappingPropSet = SourceCreateUtils.generateAttributes(
                entityMappedElementList,
                parentMappedElementList,
                indexColumnNameSet
        );

        final TypeElement parentEntityElement = SourceCreateUtils.entityElement(parentMappedElementList);

        // 1. generate import part
        this.importsBlock = SourceCreateUtils.generateImport(this.entityElement, parentEntityElement, mappingPropSet);
        // 2. generate class definition part

        this.classDefinition = SourceCreateUtils.generateClassDefinition(this.entityElement, parentEntityElement);
        // 3. generate body of class part
        this.body = SourceCreateUtils.generateBody(this.entityElement,
                parentEntityElement,
                generateMappingPropList(mappingPropSet,
                        ArrayUtils.asUnmodifiableSet(indexColumnNameSet, TableMeta.ID)));
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
     *
     * @return a unmodifiable List
     */
    private List<MetaAttribute> generateMappingPropList(Set<VariableElement> mappingPropSet,
                                                        Set<String> columnNameSet) {

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
                    ,columnNameSet.contains(columnName));
            list.add(attribute);
        }
        return Collections.unmodifiableList(list);
    }

    private static Set<String> createIndexColumnNameSet(TypeElement entityElement) {
        Table table = entityElement.getAnnotation(Table.class);
        Index[] indexArray = table.indexes();
        Set<String> columnNameSet = new HashSet<>();

        Set<String> indexNameSet = new HashSet<>(indexArray.length + 3);

        StringTokenizer tokenizer;
        for (Index index : indexArray) {
            // make index name lower case
            String indexName = StringUtils.toLowerCase(index.name());
            if (indexNameSet.contains(indexName)) {
                throw new MetaException(ErrorCode.META_ERROR, "entity[%s] indexMap name[%s] duplication",
                        entityElement.getQualifiedName());
            }
            indexNameSet.add(indexName);
            for (String columnName : index.columnList()) {
                tokenizer = new StringTokenizer(columnName.trim(), " ", false);
                // make index field name lower case
                columnNameSet.add(StringUtils.toLowerCase(tokenizer.nextToken()));
            }
        }
        return Collections.unmodifiableSet(columnNameSet);
    }


}
