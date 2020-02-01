package io.army.asm;

import io.army.ErrorCode;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.criteria.impl.TableMetaFactory;
import io.army.meta.SchemaMeta;
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
    public Map<Class<?>, TableMeta<?>> scanTableMeta(SchemaMeta schemaMeta, List<String> basePackages) {
        Map<Class<?>, TableMeta<?>> map = new HashMap<>();
        try {
            TableMeta<?> tableMeta;
            for (String basePackage : basePackages) {
                for (Resource resource : loadClassFiles(basePackage)) {
                    if (!resource.isReadable()) {
                        LOG.trace("Ignored because not readable: {}", resource);
                        continue;
                    }
                    MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                    if (!isDomainClass(schemaMeta, metadataReader)) {
                        continue;
                    }
                    Class<?> domainClass = loadDomainClass(metadataReader.getClassMetadata().getClassName());
                    tableMeta = loadTableMetaFor(domainClass);
                    if (tableMeta.schema() != schemaMeta) {
                        throw new MetaException(ErrorCode.META_ERROR, String.format(
                                "Entity[%s] schema error.", domainClass.getName()));
                    }
                    map.put(domainClass, tableMeta);
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

    private boolean isDomainClass(SchemaMeta schemaMeta, MetadataReader metadataReader) {
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        Map<String, Object> valueMap;
        valueMap = metadataReader.getAnnotationMetadata().getAnnotationAttributes(Table.class.getName());

        return classMetadata.isIndependent()
                && classMetadata.isConcrete()
                && valueMap != null
                && schemaMeta.catalog().equals(valueMap.get("catalog"))
                && schemaMeta.schema().equals(valueMap.get("schema"))
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
            throw new TableMetaLoadException(ErrorCode.NOT_FOUND_META_CLASS
                    , "not meta class,class[%s] not found static property[%s]"
                    , metaClass, MetaConstant.TABLE_META);
        }
        try {
            TableMeta<?> tableMeta = (TableMeta<?>) ReflectionUtils.getField(field, null);
            if (tableMeta == null) {
                throw new MetaException(ErrorCode.META_ERROR, String.format("Entity[%s] meta class error"
                        , domainClass.getName()));
            }
            return tableMeta;
        } catch (Throwable e) {
            throw new TableMetaLoadException(ErrorCode.META_ERROR
                    , e
                    , "meta class[%s] error"
                    , metaClass);
        }
    }


/*
    @Nullable
    private Class<?> loadParentEntityClass(Class<?> entityClass) {
        Class<?> superClass = entityClass.getSuperclass();
        Class<?> parentEntityClass = null;
        Inheritance inheritance;
        for (; superClass != Object.class; superClass = superClass.getSuperclass()) {
            inheritance = AnnotationUtils.getAnnotation(superClass, Inheritance.class);
            if (inheritance != null && AnnotationUtils.getAnnotation(superClass, Table.class) != null) {
                parentEntityClass = superClass;
                break;
            }
        }
        return parentEntityClass;
    }*/

    private void clean() {
        patternResolver = null;
        metadataReaderFactory = null;
        TableMetaFactory.cleanCache();
    }


}
