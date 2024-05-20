/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
@SupportedSourceVersion(SourceVersion.RELEASE_21)
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

        final int domainSetSize;
        if ((domainSetSize = elementSet.size()) == 0) {
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
        System.out.printf("[INFO] %s generate %s army static metamodel class source file, cost %s ms.%n",
                ArmyMetaModelDomainProcessor.class.getName(),
                domainSetSize,
                System.currentTimeMillis() - startTime);
        return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }


    /*################################## blow private static method ##################################*/


}
