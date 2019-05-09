package org.qinarmy.army.modelgen;

import org.qinarmy.army.ErrorCode;
import org.qinarmy.army.annotation.Inheritance;
import org.qinarmy.army.annotation.MappedSuperclass;
import org.qinarmy.army.annotation.Table;
import org.qinarmy.army.criteria.MetaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Main annotation processor.
 * <p>
 * 用于生成元数据代码
 * </p>
 * created  on 2018/9/27.
 */
@SupportedAnnotationTypes({
        "org.qinarmy.army.annotation.Table",
        "org.qinarmy.army.annotation.MappedSuperclass"
})
@SupportedOptions({
        ArmyMetaModelEntityProcessor.DEBUG_OPTION,
        ArmyMetaModelEntityProcessor.ADD_GENERATION_DATE,
        ArmyMetaModelEntityProcessor.ADD_GENERATED_ANNOTATION,
        ArmyMetaModelEntityProcessor.ADD_SUPPRESS_WARNINGS_ANNOTATION
})
public class ArmyMetaModelEntityProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ArmyMetaModelEntityProcessor.class);


    public static final String DEBUG_OPTION = "debug";
    public static final String ADD_GENERATION_DATE = "addGenerationDate";
    public static final String ADD_GENERATED_ANNOTATION = "addGeneratedAnnotation";
    public static final String ADD_SUPPRESS_WARNINGS_ANNOTATION = "addSuppressWarningsAnnotation";

    private static final boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = false;

    private ProcessingEnvironment processingEnv;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        List<MetaEntity> entityList = createEntity(roundEnv);

        // create source file
        writeSources(entityList);

        return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    private List<MetaEntity> createEntity(RoundEnvironment roundEnv) {

        final List<MetaEntity> entityList = new ArrayList<>();

        Set<? extends Element> rootSet;
        TypeElement typeElement;

        final Map<String, TypeElement> mappedSuperMap = createMappedSuperclassMap(roundEnv, MappedSuperclass.class);

        final Map<String, TypeElement> inheritanceMap = createMappedSuperclassMap(roundEnv, Inheritance.class);

        rootSet = roundEnv.getElementsAnnotatedWith(Table.class);
        List<TypeElement> supperList, inheritanceList;

        String key;

        for (Element element : rootSet) {
            typeElement = (TypeElement) element;

            supperList = new ArrayList<>(4);
            inheritanceList = new ArrayList<>(4);

            for (TypeElement superType = typeElement; ; ) {
                key = superType.getSuperclass().toString();

                if (mappedSuperMap.containsKey(key)) {
                    superType = mappedSuperMap.get(key);
                    supperList.add(superType);
                } else if (inheritanceMap.containsKey(key)) {
                    appendInheritance(superType, inheritanceList, inheritanceMap);
                    break;
                } else {
                    break;
                }
            }

            LOG.debug("Entity[{}] MappedSuperclass list:{}", typeElement.getQualifiedName(), supperList);
            LOG.debug("Entity[{}] Inheritance inheritance:{}", typeElement.getQualifiedName(), inheritanceList);

            entityList.add(new DefaultMetaEntity(typeElement, supperList, inheritanceList));

        }
        return Collections.unmodifiableList(entityList);
    }

    private void appendInheritance(TypeElement superTypeElement, List<TypeElement> inheritanceList,
                                   Map<String, TypeElement> inheritanceMap) {
        String key;
        for (TypeElement superType = superTypeElement; ; ) {
            key = superType.getSuperclass().toString();

            if (inheritanceMap.containsKey(key)) {
                superType = inheritanceMap.get(key);
                inheritanceList.add(superType);
            } else {
                break;
            }
        }

    }

    private Map<String, TypeElement> createMappedSuperclassMap(RoundEnvironment roundEnv,
                                                               Class<? extends Annotation> annotationClass)
            throws MetaException {

        Set<? extends Element> rootSet;
        final Map<String, TypeElement> mappedSuperMap = new HashMap<>();

        rootSet = roundEnv.getElementsAnnotatedWith(annotationClass);
        TypeElement typeElement;
        String key;

        for (Element element : rootSet) {
            typeElement = (TypeElement) element;
            key = typeElement.getQualifiedName().toString();

            LOG.trace("MappedSuperclass:{}", key);

            mappedSuperMap.put(key, typeElement);
        }
        return Collections.unmodifiableMap(mappedSuperMap);
    }


    private void writeSources(List<MetaEntity> entityList) {
        try {
            FileObject fo;
            for (MetaEntity metaEntity : entityList) {
                fo = processingEnv.getFiler().createSourceFile(metaEntity.getQualifiedName());
                try (PrintWriter pw = new PrintWriter(fo.openOutputStream())) {
                    doWriteSource(pw, metaEntity);
                }
            }
        } catch (Exception e) {
            throw new MetaException(ErrorCode.META_ERROR, e.getMessage(), e);
        }

    }

    private void doWriteSource(PrintWriter pw, MetaEntity metaEntity) {
        pw.println(metaEntity.getImportBlock());
        pw.println(metaEntity.getClassDefinition());
        pw.println(metaEntity.getBody());

    }


}
