package org.qinarmy.army.modelgen;

/**
 * created  on 2018/11/18.
 */
public interface MetaEntity {


    /**
     * @return null or package name
     */
    String getPackageName();

    String getQualifiedName();

    String getImportBlock();

    String getClassDefinition();

    String getSuperSimpleName();


    String getBody();


}
