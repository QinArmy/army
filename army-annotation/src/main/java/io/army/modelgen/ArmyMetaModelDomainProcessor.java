package io.army.modelgen;

import io.army.annotation.Table;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Main annotation processor.
 */
@SupportedAnnotationTypes("io.army.annotation.Table")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ArmyMetaModelDomainProcessor extends AbstractProcessor {

    private static final boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = false;

    private ProcessingEnvironment processingEnv;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Set<? extends Element> elementSet;
        elementSet = roundEnv.getElementsAnnotatedWith(Table.class);

        if (elementSet.size() > 0) {
            final long startTime = System.currentTimeMillis();
            try {
                final AnnotationHandler handler = new AnnotationHandler(this.processingEnv);
                handler.createSourceFiles(elementSet);
                if (handler.errorMsgList.size() > 0) {
                    throw createException(handler.errorMsgList);
                }
            } catch (IOException e) {
                throw new AnnotationMetaException("Army create source file occur.", e);
            }
            System.out.printf("[INFO] %s cost %s ms.%n", ArmyMetaModelDomainProcessor.class.getName()
                    , System.currentTimeMillis() - startTime);
        }
        return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }


    /*################################## blow private static method ##################################*/


    private static AnnotationMetaException createException(final List<String> errorMsgList) {
        final StringBuilder builder = new StringBuilder(errorMsgList.size() * 20)
                .append("handle army annotation occur error,detail:");
        final int size = errorMsgList.size();
        for (int i = 0; i < size; i++) {
            builder.append('\n')
                    .append(String.format("%d: ", i + 1))
                    .append(errorMsgList.get(i));
        }
        return new AnnotationMetaException(builder.toString());
    }


}
