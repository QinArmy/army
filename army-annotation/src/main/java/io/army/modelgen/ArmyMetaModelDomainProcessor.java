package io.army.modelgen;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Inheritance;
import io.army.annotation.MappedSuperclass;
import io.army.annotation.Table;
import io.army.lang.Nullable;
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
public class ArmyMetaModelDomainProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ArmyMetaModelDomainProcessor.class);

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
        final List<MetaEntity> entityList;
        entityList = createEntityList(roundEnv);

        //2. create source code file
        writeSources(entityList);
        LOG.info("{} cost {} ms", ArmyMetaModelDomainProcessor.class.getSimpleName(),
                System.currentTimeMillis() - startTime);
        return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
    }

    /*################################## blow private method ##################################*/

    private List<MetaEntity> createEntityList(final RoundEnvironment roundEnv) {

        final Map<String, TypeElement> mappedSuperMap = createClassNameToElementMap(roundEnv, MappedSuperclass.class);
        final Map<String, TypeElement> inheritanceMap = createClassNameToElementMap(roundEnv, Inheritance.class);
        final Map<String, TypeElement> domainElementMap = createClassNameToElementMap(roundEnv, Table.class);

        final List<MetaEntity> domainList = new ArrayList<>();
        final AttributeMetaParser metaParser = new AttributeMetaParser();
        List<TypeElement> domainMappedElementList, parentMappedElementList;
        TypeElement parentElement;
        Pair pair;

        Set<String> tableNameSet = new HashSet<>();
        final Map<String, List<TypeElement>> parentMappedElementsCache = new HashMap<>();
        for (TypeElement domainElement : domainElementMap.values()) {
            assertDomainTable(domainElement, tableNameSet);
            assertDomainInheritance(domainElement);

            pair = createDomainMappedElementList(domainElement, mappedSuperMap, inheritanceMap, domainElementMap);

            domainMappedElementList = pair.mappedElementList;
            parentElement = pair.parentElement;

            if (parentElement == null) {
                parentMappedElementList = Collections.emptyList();
                if (domainElement.getAnnotation(Inheritance.class) != null) {
                    MetaUtils.assertMappingParent(domainElement);
                    // cache parent domainMappedElementList
                    parentMappedElementsCache.put(MetaUtils.domainClassName(domainElement), domainMappedElementList);
                }
            } else {
                final String parentClassName = MetaUtils.domainClassName(domainElement);
                parentMappedElementList = parentMappedElementsCache.get(parentClassName);
                if (parentMappedElementList == null) {
                    Pair parentPair;
                    parentPair = createDomainMappedElementList(parentElement, mappedSuperMap, inheritanceMap,
                            domainElementMap);
                    parentMappedElementList = parentPair.mappedElementList;
                    MetaUtils.assertMappingParent(parentElement);
                    // cache parent domainMappedElementList
                    parentMappedElementsCache.put(parentClassName, parentMappedElementList);
                }
                MetaUtils.assertMappingChild(domainElement, parentElement);
            }

            domainList.add(
                    DefaultMetaEntity.create(domainMappedElementList, parentMappedElementList, metaParser)
            );
        }
        return Collections.unmodifiableList(domainList);
    }




    /*################################## blow static method ##################################*/


    /*################################## blow private static method ##################################*/


    private static void assertDomainInheritance(TypeElement domainElement) {
        final DiscriminatorValue discriminatorValue = domainElement.getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue == null) {
            return;
        }
        if (discriminatorValue.value() < 0) {
            String m = String.format("domain[%s] DiscriminatorValue.value() must great than or equals 0",
                    domainElement.getQualifiedName());
            throw new AnnotationMetaException(m);
        }
        final Inheritance inheritance = domainElement.getAnnotation(Inheritance.class);

        if (inheritance == null) {
            if (discriminatorValue.value() == 0) {
                String m = String.format("child domain[%s] DiscriminatorValue.value() cannot equals 0.",
                        domainElement.getQualifiedName());
                throw new AnnotationMetaException(m);
            }
        } else if (discriminatorValue.value() != 0) {
            String m = String.format("parentMeta domain[%s] DiscriminatorValue.value() must equals 0.",
                    domainElement.getQualifiedName());
            throw new AnnotationMetaException(m);
        }

        if (discriminatorValue.value() % 100 != 0) {
            LOG.warn("domain[{}] DiscriminatorValue.value() isn't multiple of 100.", domainElement.getQualifiedName());
        }

    }

    private static void assertDomainTable(TypeElement domainElement, Set<String> tableNameSet) {
        final Table table = domainElement.getAnnotation(Table.class);
        if (!Strings.hasText(table.name())) {
            String m = String.format("domain[%s] tableMeta name required.", domainElement.getQualifiedName());
            throw new AnnotationMetaException(m);
        }
        if (domainElement.getNestingKind() != NestingKind.TOP_LEVEL) {
            String m = String.format("domain[%s] must be top level class.", domainElement.getQualifiedName());
            throw new AnnotationMetaException(m);
        }

        // make qualifiedTableName lower case
        final String qualifiedTableName = (table.catalog() + "." + table.schema() + "." + table.name()).toLowerCase();

        if (tableNameSet.contains(qualifiedTableName)) {
            String m = String.format("domain[%s] tableMeta name[%s] duplication.", domainElement.getQualifiedName(),
                    table.name());
            throw new AnnotationMetaException(m);
        } else {
            tableNameSet.add(qualifiedTableName);
        }

        if (!Strings.hasText(table.comment())) {
            String m = String.format("domain[%s] tableMeta comment required.", domainElement.getQualifiedName());
            throw new AnnotationMetaException(m);
        }

    }

    /**
     * @return first: super class (annotated by {@link MappedSuperclass} then {@link Table}) list (asSort by extends)
     * util encounter {@link Inheritance}, second: class annotated by {@link Inheritance}
     */
    private Pair createDomainMappedElementList(
            TypeElement domainElement,
            Map<String, TypeElement> mappedSuperMap,
            Map<String, TypeElement> inheritanceMap,
            Map<String, TypeElement> domainElementMap) {

        final List<TypeElement> domainMappedElementList = new ArrayList<>(6);
        // add entity class firstly
        domainMappedElementList.add(domainElement);
        TypeElement parentDomainElement = null;
        String parentClassName;

        final boolean domainAnnotatedInheritance = domainElement.getAnnotation(Inheritance.class) != null;
        int tableCount = 0;
        for (TypeElement parentMappedElement = domainElement; ; ) {
            // key is  parentMeta class name
            parentClassName = parentMappedElement.getSuperclass().toString();
            int index = parentClassName.indexOf('<');
            if (index > 0) {
                parentClassName = parentClassName.substring(0, index);
            }
            if (inheritanceMap.containsKey(parentClassName)) {
                if (domainAnnotatedInheritance) {
                    throw Exceptions.inheritanceDuplication(domainElement);
                }
                if (tableCount > 0) {
                    throw Exceptions.multiLevelInheritance(domainElement);
                }
                parentDomainElement = inheritanceMap.get(parentClassName);
                break;
            } else if (mappedSuperMap.containsKey(parentClassName)) {
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

        return new Pair(domainMappedElementList, parentDomainElement);
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
            throw new AnnotationMetaException(e.getMessage(), e);
        }

    }


    private static final class Pair {

        private final List<TypeElement> mappedElementList;

        private final TypeElement parentElement;

        private Pair(List<TypeElement> mappedElementList, @Nullable TypeElement parentElement) {
            this.mappedElementList = Collections.unmodifiableList(mappedElementList);
            this.parentElement = parentElement;
        }

    }


}
