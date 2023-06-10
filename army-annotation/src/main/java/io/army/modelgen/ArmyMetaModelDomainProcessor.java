package io.army.modelgen;

import io.army.annotation.Table;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
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

        if (elementSet.size() == 0) {
            return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
        }

        final long startTime = System.currentTimeMillis();
        try {
            final AnnotationHandler handler = new AnnotationHandler(this.processingEnv);
            handler.createSourceFiles(elementSet);
            if (handler.errorMsgList.size() > 0) {
                final String m, title;
                title = "handle army annotation occur error,detail:";
                m = _MetaBridge.createErrorMessage(title, handler.errorMsgList);
                throw new AnnotationMetaException(m);
            }
        } catch (IOException e) {
            throw new AnnotationMetaException("Army create source file occur.", e);
        }
        System.out.printf("[INFO] %s cost %s ms.%n", ArmyMetaModelDomainProcessor.class.getName()
                , System.currentTimeMillis() - startTime);
        return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }


    /*################################## blow private static method ##################################*/


}
