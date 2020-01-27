package io.army.asm;

import io.army.ErrorCode;
import io.army.annotation.Table;
import io.army.meta.TableMeta;
import io.army.modelgen.MetaConstant;
import io.army.util.ClassUtils;
import io.army.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TableMetaLoaderIml implements TableMetaLoader {

    private static final Logger LOG = LoggerFactory.getLogger(TableMetaLoaderIml.class);

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private ResourcePatternResolver patternResolver;

    private MetadataReaderFactory metadataReaderFactory;

    TableMetaLoaderIml() {
    }

    @Override
    public Map<Class<?>, TableMeta<?>> scanTableMeta(List<String> basePackages) {
        Map<Class<?>, TableMeta<?>> map = new HashMap<>();
        try {

            for (String basePackage : basePackages) {
                for (Resource resource : loadClassFiles(basePackage)) {
                    if (!resource.isReadable()) {
                        LOG.trace("Ignored because not readable: {}", resource);
                        continue;
                    }
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    if (!isDomainClass(metadataReader)) {
                        continue;
                    }
                    Class<?> domainClass = loadDomainClass(metadataReader.getClassMetadata().getClassName());
                    map.put(domainClass, loadTableMetaFor(domainClass));
                }
            }
        } catch (IOException e) {
            throw new TableMetaLoadException(ErrorCode.NONE, e, e.getMessage());
        }

        clean();
        return Collections.unmodifiableMap(map);
    }


    /*################################## blow private method ##################################*/

    private Resource[] loadClassFiles(String basePackages) throws IOException {
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackages) + '/' + DEFAULT_RESOURCE_PATTERN;
        return getPatternResolver().getResources(packageSearchPath);
    }

    private ResourcePatternResolver getPatternResolver() {
        if (patternResolver == null) {
            patternResolver = new PathMatchingResourcePatternResolver();
        }
        return patternResolver;
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    }

    /**
     * Return the MetadataReaderFactory used by this component provider.
     */
    private MetadataReaderFactory getMetadataReaderFactory() {
        if (this.metadataReaderFactory == null) {
            this.metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return this.metadataReaderFactory;
    }

    private boolean isDomainClass(MetadataReader metadataReader) {
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        return classMetadata.isIndependent()
                && classMetadata.isConcrete()
                && !classMetadata.isInterface()
                && !classMetadata.isAnnotation()
                && metadataReader.getAnnotationMetadata().hasAnnotation(Table.class.getName())
                ;
    }

    private Class<?> loadDomainClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new TableMetaLoadException(ErrorCode.NOT_FOUND_DOMAIN, e, "not found domain[%s]", className);
        }
    }

    private TableMeta<?> loadTableMetaFor(Class<?> domainClass) {
        Class<?> metaClass;
        try {
            metaClass = ClassUtils.loadDomainMetaClass(domainClass);
        } catch (ClassNotFoundException e) {
            throw new TableMetaLoadException(ErrorCode.NOT_FOUND_META_CLASS, e, e.getMessage());
        }

        if (!ClassUtils.isMatchMetaClass(domainClass, metaClass)) {
            throw new TableMetaLoadException(ErrorCode.META_CLASS_NOT_MATCH,
                    "domain[%s] not found meta class[%s%s]",
                    domainClass.getName(), domainClass.getName(), MetaConstant.META_CLASS_NAME_SUFFIX);
        }
        Field field = ReflectionUtils.findField(metaClass, MetaConstant.TABLE_META);
        if (field == null
                || !TableMeta.class.isAssignableFrom(field.getType())) {
            throw new TableMetaLoadException(ErrorCode.NOT_FOUND_META_CLASS, "not meta class,class[%s] not found static property[%s]"
                    , metaClass, MetaConstant.TABLE_META);
        }
        return (TableMeta<?>) ReflectionUtils.getField(field, null);
    }

    private void clean(){
        patternResolver = null;
        metadataReaderFactory = null;
    }


}
