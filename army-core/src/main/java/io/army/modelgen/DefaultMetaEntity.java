package io.army.modelgen;

import io.army.util.ElementUtils;
import org.qinarmy.foundation.util.CollectionUtils;
import org.springframework.lang.NonNull;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * created  on 2018/11/18.
 */
class DefaultMetaEntity implements MetaEntity {

    private final TypeElement type;

    private final String importsBlock;

    private final String classDefinition;

    private final String body;

    private final Set<VariableElement> fieldElements;


    DefaultMetaEntity(@NonNull TypeElement type, @NonNull List<TypeElement> mappedSuperList,
                      @NonNull List<TypeElement> inheritanceList) {
        this.type = type;
        this.fieldElements = SourceCreateUtils.generateAttributes(this.type, mappedSuperList, inheritanceList);

        final TypeElement superType = CollectionUtils.isEmpty(inheritanceList) ? null : inheritanceList.get(0);

        this.importsBlock = SourceCreateUtils.generateImport(this.type, superType, fieldElements);
        this.classDefinition = SourceCreateUtils.generateClassDefinition(type, superType);

        this.body = SourceCreateUtils.generateBody(this.type, superType, generateAttributes());
    }


    @Override
    public String getPackageName() {
        return SourceCreateUtils.getPackage(type);
    }

    @Override
    public String getQualifiedName() {
        return SourceCreateUtils.getQualifiedName(type);
    }

    @Override
    public String getClassDefinition() {
        return this.classDefinition;
    }

    @Override
    public String getSuperSimpleName() {
        return ElementUtils.getSimpleName(type.getSimpleName().toString());
    }

    @Override
    public String getImportBlock() {
        return this.importsBlock;
    }


    @Override
    public String getBody() {
        return this.body;
    }


    private List<MetaAttribute> generateAttributes() {

        List<MetaAttribute> list = new ArrayList<>(this.fieldElements.size());
        for (VariableElement fieldElement : fieldElements) {
            list.add(new DefaultMetaAttribute(type, fieldElement));
        }

        return Collections.unmodifiableList(list);
    }


}
