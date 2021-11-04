package io.army.modelgen;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Inheritance;

import javax.lang.model.element.TypeElement;
import java.util.Set;

abstract class Exceptions {

    private Exceptions() {
        throw new UnsupportedOperationException();
    }


    static AnnotationMetaException inheritanceDuplication(TypeElement entityElement) {
        String m = String.format("Domain[%s] extends link %s count great than 1 in link of extends",
                entityElement.getQualifiedName(),
                Inheritance.class.getName());
        return new AnnotationMetaException(m);
    }


    static AnnotationMetaException multiLevelInheritance(TypeElement entityElement) {
        String m = String.format("Domain[%s] inheritance level greater than 2,it's parentMeta's MappingMode is Child.",
                entityElement.getQualifiedName());
        return new AnnotationMetaException(m);
    }

    static AnnotationMetaException propNotDuplication(TypeElement mappedElement, String propName) {
        String m = String.format("Mapped class[%s] mapping property[%s] duplication"
                , mappedElement.getQualifiedName(), propName);
        return new AnnotationMetaException(m);
    }


    static AnnotationMetaException columnDuplication(TypeElement mappedElement, String columnName) {
        return new AnnotationMetaException(String.format(
                "Mapped class[%s] mapping column[%s] duplication"
                , mappedElement.getQualifiedName(), columnName));
    }

    static AnnotationMetaException discriminatorNotCodeEnum(TypeElement domainElement, String propName) {
        String m = String.format("Domain[%s] property[%s] isn't %s"
                , domainElement.getQualifiedName()
                , propName
                , MetaUtils.CODE_ENUM);
        return new AnnotationMetaException(m);
    }

    static AnnotationMetaException domainNoIdProp(TypeElement domainElement) {
        String m = String.format("Domain[%s] no property[%s]"
                , domainElement.getQualifiedName()
                , MetaConstant.ID);
        return new AnnotationMetaException(m);
    }

    static AnnotationMetaException missingProps(TypeElement domainElement, Set<String> missingProps) {
        String m = String.format("Domain[%s] missing properties[%s]"
                , domainElement.getQualifiedName()
                , missingProps);
        return new AnnotationMetaException(m);
    }

    static AnnotationMetaException childNoDiscriminatorValueAnnotation(TypeElement childElement) {
        String m = String.format("Domain[%s] missing %s annotation."
                , MetaUtils.domainClassName(childElement)
                , DiscriminatorValue.class.getName());
        return new AnnotationMetaException(m);
    }

    static AnnotationMetaException discriminatorValueDuplication(String childClassName) {
        String m = String.format("Domain[%s] missing %s value duplication."
                , childClassName
                , DiscriminatorValue.class.getName());
        return new AnnotationMetaException(m);
    }

    static AnnotationMetaException noDiscriminatorColumn(TypeElement domainElement, String discriminatorColumn) {
        String m = String.format("Domain[%s] missing discriminator column[%s]."
                , MetaUtils.domainClassName(domainElement)
                , discriminatorColumn);
        return new AnnotationMetaException(m);
    }


}
