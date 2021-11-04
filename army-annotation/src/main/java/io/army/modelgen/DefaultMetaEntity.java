package io.army.modelgen;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * this class is a implementation of  {@link MetaEntity}
 *
 * @since 1.0
 */
class DefaultMetaEntity implements MetaEntity {

    static DefaultMetaEntity create(List<TypeElement> domainMappedElementList,
                                    List<TypeElement> parentMappedElementList, AttributeMetaParser metaParser) {
        final TypeElement domainElement, parentElement;
        domainElement = MetaUtils.domainElement(domainMappedElementList);
        assert domainElement != null;
        // indexColumnNameSet help then step
        final Map<String, IndexMode> indexMetaMap;
        indexMetaMap = MetaUtils.createIndexColumnNameSet(domainElement
                , parentMappedElementList.isEmpty());

        final Collection<VariableElement> mappingProps;
        mappingProps = metaParser.generateAttributes(
                domainMappedElementList,
                parentMappedElementList,
                indexMetaMap.keySet()
        );
        parentElement = MetaUtils.domainElement(parentMappedElementList);
        final String importsBlock, classDefinition, body;

        importsBlock = SourceCreateUtils.generateImport(domainElement, parentElement, mappingProps);
        classDefinition = SourceCreateUtils.generateClassDefinition(domainElement, parentElement);
        // 3. create MetaAttributes
        final List<MetaAttribute> metaAttributes;
        metaAttributes = DefaultMetaAttribute.createMetaAttributes(domainElement, mappingProps, indexMetaMap);
        // 4. generate body of class part
        body = SourceCreateUtils.generateBody(domainElement, parentElement, metaAttributes);

        return new DefaultMetaEntity(domainElement, importsBlock, classDefinition, body);
    }

    private final TypeElement domainElement;

    private final String importsBlock;

    private final String classDefinition;

    private final String body;

    private DefaultMetaEntity(TypeElement domainElement, String importsBlock
            , String classDefinition, String body) {
        this.domainElement = domainElement;
        this.importsBlock = importsBlock;
        this.classDefinition = classDefinition;
        this.body = body;
    }

    @Override
    public String getQualifiedName() {
        return MetaUtils.domainClassName(this.domainElement) + MetaConstant.META_CLASS_NAME_SUFFIX;
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


}
