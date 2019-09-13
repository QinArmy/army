package io.army.modelgen;

import io.army.ErrorCode;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Inheritance;
import io.army.annotation.MappedSuperclass;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.util.Pair;
import io.army.util.StringUtils;
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
 * create entity meta source code file
 * </p>
 * created  on 2018/9/27.
 */
@SupportedAnnotationTypes({
        "io.army.annotation.Table",
        "io.army.annotation.MappedSuperclass",
        "io.army.annotation.Inheritance"
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
        final long startTime = System.currentTimeMillis();
        List<MetaEntity> entityList = createEntity(roundEnv);

        // create source code file
        writeSources(entityList);
        LOG.info("{} cost {} ms", ArmyMetaModelEntityProcessor.class.getSimpleName(),
                System.currentTimeMillis() - startTime);
        return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    private List<MetaEntity> createEntity(RoundEnvironment roundEnv) {

        final Map<String, TypeElement> mappedSuperMap = createClassNameToElementMap(roundEnv, MappedSuperclass.class);
        final Map<String, TypeElement> inheritanceMap = createClassNameToElementMap(roundEnv, Inheritance.class);
        final Map<String, TypeElement> entityElementMap = createClassNameToElementMap(roundEnv, Table.class);

        final List<MetaEntity> entityList = new ArrayList<>();

        List<TypeElement> entityMappedElementList, parentEntityMappedElementList;
        TypeElement parentEntityElement;
        Pair<List<TypeElement>, TypeElement> pair;

        Set<String> tableNameSet = new HashSet<>();

        for (TypeElement entityElement : entityElementMap.values()) {
            assertEntity(entityElement, tableNameSet);

            pair = createEntityMappedElementList(entityElement, mappedSuperMap, inheritanceMap, entityElementMap);

            entityMappedElementList = pair.getFirst();
            parentEntityElement = pair.getSecond();

            if (parentEntityElement == null) {
                parentEntityMappedElementList = Collections.emptyList();
            } else {
                Pair<List<TypeElement>, TypeElement> parentPair;
                parentPair = createEntityMappedElementList(parentEntityElement, mappedSuperMap, inheritanceMap,
                        entityElementMap);
                parentEntityMappedElementList = parentPair.getFirst();
            }

            entityList.add(
                    new DefaultMetaEntity(entityMappedElementList, parentEntityMappedElementList)
            );
        }
        return Collections.unmodifiableList(entityList);
    }

    private static void assertEntity(TypeElement entityElement, Set<String> tableNameSet) throws MetaException {
        assertEntityTable(entityElement, tableNameSet);
        assertEntityInheritance(entityElement);
    }

    private static void assertEntityInheritance(TypeElement entityElement) {
        DiscriminatorValue discriminatorValue = entityElement.getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue == null) {
            return;
        }
        Inheritance inheritance = entityElement.getAnnotation(Inheritance.class);

        if (discriminatorValue.value() < 0) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "entity[%s] DiscriminatorValue.value() must great than or equals 0",
                    entityElement.getQualifiedName()
            );
        }

        if (inheritance == null) {
            if (discriminatorValue.value() == 0) {
                throw new MetaException(ErrorCode.META_ERROR,
                        "child entity[%s] DiscriminatorValue.value() cannot equals 0.",
                        entityElement.getQualifiedName()
                );
            }
        } else {
            if (discriminatorValue.value() != 0) {
                throw new MetaException(ErrorCode.META_ERROR,
                        "parent entity[%s] DiscriminatorValue.value() must equals 0.",
                        entityElement.getQualifiedName()
                );
            }
        }

        if (discriminatorValue.value() % 100 != 0) {
            LOG.warn("entity[{}] DiscriminatorValue.value() isn't multiple of 100.", entityElement.getQualifiedName());
        }

    }

    private static void assertEntityTable(TypeElement entityElement, Set<String> tableNameSet) {
        Table table = entityElement.getAnnotation(Table.class);
        if (!StringUtils.hasText(table.name())) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] table name required."
                    , entityElement.getQualifiedName());
        }
        String tableName = table.schema() + "." + table.name();
        if (tableNameSet.contains(tableName)) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] table name[%s] duplication.",
                    entityElement.getQualifiedName(),
                    table.name()
            );
        } else {
            tableNameSet.add(tableName);
        }

        if (!StringUtils.hasText(table.comment())) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] table comment required."
                    , entityElement.getQualifiedName(),
                    table.comment()
            );
        }
    }


    private Pair<List<TypeElement>, TypeElement> createEntityMappedElementList(
            TypeElement entityElement,
            Map<String, TypeElement> mappedSuperMap,
            Map<String, TypeElement> inheritanceMap,
            Map<String, TypeElement> entityElementMap) {

        List<TypeElement> entityMappedElementList = new ArrayList<>(6);
        entityMappedElementList.add(entityElement);
        TypeElement parentEntityElement = null;
        String parentClassName;

        final boolean entityAnnotatedInheritance = entityElement.getAnnotation(Inheritance.class) != null;

        for (TypeElement parentMappedElement = entityElement; ; ) {
            // key is  parent class name
            parentClassName = parentMappedElement.getSuperclass().toString();

            if (inheritanceMap.containsKey(parentClassName)) {
                if (entityAnnotatedInheritance) {
                    MetaAssert.throwInheritanceDuplication(entityElement);
                }
                parentEntityElement = inheritanceMap.get(parentClassName);
                break;
            }
            if (mappedSuperMap.containsKey(parentClassName)) {
                // get super class
                parentMappedElement = mappedSuperMap.get(parentClassName);
                entityMappedElementList.add(parentMappedElement);
            } else if (entityElementMap.containsKey(parentClassName)) {
                // get super class
                parentMappedElement = entityElementMap.get(parentClassName);
                entityMappedElementList.add(parentMappedElement);
            } else {
                break;
            }
        }
        Collections.reverse(entityMappedElementList);

        return new Pair<List<TypeElement>, TypeElement>()
                .setFirst(Collections.unmodifiableList(entityMappedElementList))
                .setSecond(parentEntityElement)
                ;
    }


    /**
     * @return <ul>
     * <li>key : className</li>
     * <li>{@link TypeElement}</li>
     * </ul>
     */
    private Map<String, TypeElement> createClassNameToElementMap(RoundEnvironment roundEnv,
                                                                 Class<? extends Annotation> annotationClass) {

        Set<? extends Element> rootSet;
        final Map<String, TypeElement> mappedSuperMap = new HashMap<>();

        rootSet = roundEnv.getElementsAnnotatedWith(annotationClass);
        TypeElement typeElement;
        String key;
        for (Element element : rootSet) {
            typeElement = (TypeElement) element;
            key = typeElement.getQualifiedName().toString();

            LOG.trace("{} :{}", annotationClass.getSimpleName(), key);

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
