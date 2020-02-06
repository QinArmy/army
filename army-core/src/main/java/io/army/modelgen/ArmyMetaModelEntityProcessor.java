package io.army.modelgen;

import io.army.ErrorCode;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Inheritance;
import io.army.annotation.MappedSuperclass;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.util.ExceptionUtils;
import io.army.util.Pair;
import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Main annotation processor.
 * <p>
 * debugSQL entity meta source code file
 * </p>
 * created  on 2018/9/27.
 */
@SupportedAnnotationTypes({
        "io.army.annotation.Table",
        "io.army.annotation.MappedSuperclass",
        "io.army.annotation.Inheritance"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({
//        ArmyMetaModelEntityProcessor.DEBUG_OPTION,
//        ArmyMetaModelEntityProcessor.ADD_GENERATION_DATE,
//        ArmyMetaModelEntityProcessor.ADD_GENERATED_ANNOTATION,
//        ArmyMetaModelEntityProcessor.ADD_SUPPRESS_WARNINGS_ANNOTATION
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
        // 1. crate MetaEntity
        List<MetaEntity> entityList = createEntity(roundEnv);

        //2. debugSQL source code file
        writeSources(entityList);
        LOG.info("{} cost {} ms", ArmyMetaModelEntityProcessor.class.getSimpleName(),
                System.currentTimeMillis() - startTime);
        return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    /*################################## blow private method ##################################*/

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
                // debugSQL super class(annotated by Inheritance ) 的 mapped list
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
                        "parentMeta entity[%s] DiscriminatorValue.value() must equals 0.",
                        entityElement.getQualifiedName()
                );
            }
        }

        if (discriminatorValue.value() % 100 != 0) {
            LOG.warn("entity[{}] DiscriminatorValue.value() isn'table multiple of 100.", entityElement.getQualifiedName());
        }

    }

    private static void assertEntityTable(TypeElement entityElement, Set<String> tableNameSet) {
        Table table = entityElement.getAnnotation(Table.class);
        if (!StringUtils.hasText(table.name())) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] tableMeta name required."
                    , entityElement.getQualifiedName());
        }
        if (entityElement.getNestingKind() != NestingKind.TOP_LEVEL) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] must be top level class."
                    , entityElement.getQualifiedName());
        }

        String qualifiedTableName = table.catalog() + "." + table.schema() + "." + table.name();
        // make qualifiedTableName lower case
        qualifiedTableName = StringUtils.toLowerCase(qualifiedTableName);
        if (tableNameSet.contains(qualifiedTableName)) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] tableMeta name[%s] duplication.",
                    entityElement.getQualifiedName(),
                    table.name()
            );
        } else {
            tableNameSet.add(qualifiedTableName);
        }

        if (!StringUtils.hasText(table.comment())) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] tableMeta comment required."
                    , entityElement.getQualifiedName(),
                    table.comment()
            );
        }
    }

    /**
     * @return first: super class (annotated by {@link MappedSuperclass} then {@link Table}) list (order by extends)
     * util encounter {@link Inheritance}, second: class annotated by {@link Inheritance}
     */
    private Pair<List<TypeElement>, TypeElement> createEntityMappedElementList(
            TypeElement entityElement,
            Map<String, TypeElement> mappedSuperMap,
            Map<String, TypeElement> inheritanceMap,
            Map<String, TypeElement> entityElementMap) {

        List<TypeElement> entityMappedElementList = new ArrayList<>(6);
        // add entity class firstly
        entityMappedElementList.add(entityElement);
        TypeElement parentEntityElement = null;
        String parentClassName;

        final boolean entityAnnotatedInheritance = entityElement.getAnnotation(Inheritance.class) != null;
        int tableCount = 0;
        for (TypeElement parentMappedElement = entityElement; ; ) {
            // key is  parentMeta class name
            parentClassName = parentMappedElement.getSuperclass().toString();

            if (inheritanceMap.containsKey(parentClassName)) {
                if (entityAnnotatedInheritance) {
                    MetaUtils.throwInheritanceDuplication(entityElement);
                }
                if (tableCount > 0) {
                    MetaUtils.throwMultiLevelInheritance(entityElement);
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
                tableCount++;
            } else {
                break;
            }
        }
        Collections.reverse(entityMappedElementList);

        return new Pair<>(
                Collections.unmodifiableList(entityMappedElementList)
                , parentEntityElement);
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
                    pw.println(metaEntity.getImportBlock());
                    pw.println(metaEntity.getClassDefinition());
                    pw.println(metaEntity.getBody());
                }
            }
        } catch (Exception e) {
            throw new MetaException(ErrorCode.META_ERROR, e.getMessage(), e);
        }

    }


}
