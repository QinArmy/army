package io.army.modelgen;

import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.annotation.Index;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
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
        final Set<String> indexColumnNameSet = createIndexColumnNameSet(this.entityElement);

        final Set<VariableElement> mappingPropSet = SourceCreateUtils.generateAttributes(
                entityMappedElementList,
                parentMappedElementList,
                indexColumnNameSet
        );

        final TypeElement parentEntityElement = SourceCreateUtils.entityElement(parentMappedElementList);

        this.importsBlock = SourceCreateUtils.generateImport(this.entityElement, parentEntityElement, mappingPropSet);

        this.classDefinition = SourceCreateUtils.generateClassDefinition(this.entityElement, parentEntityElement);


        this.body = SourceCreateUtils.generateBody(this.entityElement,
                parentEntityElement,
                generateMappingPropList(mappingPropSet,
                        ArrayUtils.asUnmodifiableSet(indexColumnNameSet, TableMeta.ID)));
    }


    @Override
    public String getPackageName() {
        return SourceCreateUtils.getPackage(entityElement);
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
    public String getSuperSimpleName() {
        return ClassUtils.getShortName(entityElement.getSimpleName().toString());
    }

    @Override
    public String getImportBlock() {
        return this.importsBlock;
    }


    @Override
    public String getBody() {
        return this.body;
    }


    private List<MetaAttribute> generateMappingPropList(Set<VariableElement> mappingPropSet,
                                                        Set<String> columnNameSet) {

        List<MetaAttribute> list = new ArrayList<>(mappingPropSet.size());
        Column column;
        MetaAttribute attribute;
        String columnName;
        for (VariableElement mappingProp : mappingPropSet) {
            column = mappingProp.getAnnotation(Column.class);
            columnName = SourceCreateUtils.columnName(this.entityElement, mappingProp, column);
            attribute = new DefaultMetaAttribute(this.entityElement, mappingProp, columnNameSet.contains(columnName));
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
            if (indexNameSet.contains(index.name())) {
                throw new MetaException(ErrorCode.META_ERROR, "entity[%s] indexMap name[%s] duplication",
                        entityElement.getQualifiedName());
            }
            indexNameSet.add(index.name());
            for (String columnName : index.columnList()) {
                tokenizer = new StringTokenizer(columnName.trim(), " ", false);
                columnNameSet.add(tokenizer.nextToken());
            }
        }
        return Collections.unmodifiableSet(columnNameSet);
    }


}
