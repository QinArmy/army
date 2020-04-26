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
 */
@SupportedAnnotationTypes({
        "io.army.annotation.Table",
        "io.army.annotation.MappedSuperclass",
        "io.army.annotation.Inheritance"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ArmyMetaModelEntityProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ArmyMetaModelEntityProcessor.class);

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

        //2. create source code file
        writeSources(entityList);
        LOG.info("{} cost {} ms", ArmyMetaModelEntityProcessor.class.getSimpleName(),
                System.currentTimeMillis() - startTime);
        return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    /*################################## blow private method ##################################*/

    private List<MetaEntity> createEntity(RoundEnvironment roundEnv) {

        final Map<String, TypeElement> mappedSuperMap = createClassNameToElementMap(roundEnv, MappedSuperclass.class);
        final Map<String, TypeElement> inheritanceMap = createClassNameToElementMap(roundEnv, Inheritance.class);
        final Map<String, TypeElement> domainElementMap = createClassNameToElementMap(roundEnv, Table.class);

        final List<MetaEntity> domainList = new ArrayList<>();

        List<TypeElement> domainMappedElementList, domainEntityMappedElementList;
        TypeElement parentEntityElement;
        Pair<List<TypeElement>, TypeElement> pair;

        Set<String> tableNameSet = new HashSet<>();

        for (TypeElement domainElement : domainElementMap.values()) {
            assertDomain(domainElement, tableNameSet);

            pair = createDomainMappedElementList(domainElement, mappedSuperMap, inheritanceMap, domainElementMap);

            domainMappedElementList = pair.getFirst();
            parentEntityElement = pair.getSecond();

            if (parentEntityElement == null) {
                domainEntityMappedElementList = Collections.emptyList();
            } else {
                Pair<List<TypeElement>, TypeElement> parentPair;
                // debugSQL super class(annotated by Inheritance ) 的 mapped list
                parentPair = createDomainMappedElementList(parentEntityElement, mappedSuperMap, inheritanceMap,
                        domainElementMap);
                domainEntityMappedElementList = parentPair.getFirst();
            }

            domainList.add(
                    new DefaultMetaEntity(domainMappedElementList, domainEntityMappedElementList)
            );
        }
        return Collections.unmodifiableList(domainList);
    }

    private static void assertDomain(TypeElement domainElement, Set<String> tableNameSet) throws MetaException {
        assertDomainTable(domainElement, tableNameSet);
        assertDomainInheritance(domainElement);
    }

    private static void assertDomainInheritance(TypeElement domainElement) {
        DiscriminatorValue discriminatorValue = domainElement.getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue == null) {
            return;
        }
        Inheritance inheritance = domainElement.getAnnotation(Inheritance.class);

        if (discriminatorValue.value() < 0) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "domain[%s] DiscriminatorValue.value() must great than or equals 0",
                    domainElement.getQualifiedName()
            );
        }

        if (inheritance == null) {
            if (discriminatorValue.value() == 0) {
                throw new MetaException(ErrorCode.META_ERROR,
                        "child domain[%s] DiscriminatorValue.value() cannot equals 0.",
                        domainElement.getQualifiedName()
                );
            }
        } else {
            if (discriminatorValue.value() != 0) {
                throw new MetaException(ErrorCode.META_ERROR,
                        "parentMeta domain[%s] DiscriminatorValue.value() must equals 0.",
                        domainElement.getQualifiedName()
                );
            }
        }

        if (discriminatorValue.value() % 100 != 0) {
            LOG.warn("domain[{}] DiscriminatorValue.value() isn't multiple of 100.", domainElement.getQualifiedName());
        }

    }

    private static void assertDomainTable(TypeElement domainElement, Set<String> tableNameSet) {
        Table table = domainElement.getAnnotation(Table.class);
        if (!StringUtils.hasText(table.name())) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] tableMeta name required."
                    , domainElement.getQualifiedName());
        }
        if (domainElement.getNestingKind() != NestingKind.TOP_LEVEL) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] must be top level class."
                    , domainElement.getQualifiedName());
        }

        String qualifiedTableName = table.catalog() + "." + table.schema() + "." + table.name();
        // make qualifiedTableName lower case
        qualifiedTableName = StringUtils.toLowerCase(qualifiedTableName);
        if (tableNameSet.contains(qualifiedTableName)) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] tableMeta name[%s] duplication.",
                    domainElement.getQualifiedName(),
                    table.name()
            );
        } else {
            tableNameSet.add(qualifiedTableName);
        }

        if (!StringUtils.hasText(table.comment())) {
            throw new MetaException(ErrorCode.META_ERROR, "domain[%s] tableMeta comment required."
                    , domainElement.getQualifiedName(),
                    table.comment()
            );
        }
    }

    /**
     * @return first: super class (annotated by {@link MappedSuperclass} then {@link Table}) list (asSort by extends)
     * util encounter {@link Inheritance}, second: class annotated by {@link Inheritance}
     */
    private Pair<List<TypeElement>, TypeElement> createDomainMappedElementList(
            TypeElement domainElement,
            Map<String, TypeElement> mappedSuperMap,
            Map<String, TypeElement> inheritanceMap,
            Map<String, TypeElement> domainElementMap) {

        List<TypeElement> domainMappedElementList = new ArrayList<>(6);
        // add entity class firstly
        domainMappedElementList.add(domainElement);
        TypeElement parentDomainElement = null;
        String parentClassName;

        final boolean domainAnnotatedInheritance = domainElement.getAnnotation(Inheritance.class) != null;
        int tableCount = 0;
        for (TypeElement parentMappedElement = domainElement; ; ) {
            // key is  parentMeta class name
            parentClassName = parentMappedElement.getSuperclass().toString();

            if (inheritanceMap.containsKey(parentClassName)) {
                if (domainAnnotatedInheritance) {
                    MetaUtils.throwInheritanceDuplication(domainElement);
                }
                if (tableCount > 0) {
                    MetaUtils.throwMultiLevelInheritance(domainElement);
                }
                parentDomainElement = inheritanceMap.get(parentClassName);
                break;
            }
            if (mappedSuperMap.containsKey(parentClassName)) {
                // get super class
                parentMappedElement = mappedSuperMap.get(parentClassName);
                domainMappedElementList.add(parentMappedElement);
            } else if (domainElementMap.containsKey(parentClassName)) {
                // get super class
                parentMappedElement = domainElementMap.get(parentClassName);
                domainMappedElementList.add(parentMappedElement);
                tableCount++;
            } else {
                break;
            }
        }
        // reverse mapped element list
        Collections.reverse(domainMappedElementList);

        return new Pair<>(
                Collections.unmodifiableList(domainMappedElementList)
                , parentDomainElement);
    }


    /**
     * @return <ul>
     * <li>key : className</li>
     * <li>{@link TypeElement}</li>
     * </ul>
     */
    private Map<String, TypeElement> createClassNameToElementMap(RoundEnvironment roundEnv,
                                                                 Class<? extends Annotation> annotationClass) {

        final Set<? extends Element> rootSet = roundEnv.getElementsAnnotatedWith(annotationClass);
        final Map<String, TypeElement> mappedSuperMap = new HashMap<>();

        TypeElement typeElement;
        String key;
        for (Element element : rootSet) {
            typeElement = (TypeElement) element;
            key = typeElement.getQualifiedName().toString();

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
