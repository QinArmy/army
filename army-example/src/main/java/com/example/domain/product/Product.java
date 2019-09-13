package com.example.domain.product;

import com.example.domain.VersionDomain;
import io.army.annotation.Column;
import io.army.annotation.Table;


/**
 * created  on 2018/9/27.
 */
@Table(name = "p_product", comment = "产品父表")
//@Inheritance("product_type")
//@DiscriminatorValue(NONE_VALUE)
public class Product extends VersionDomain {

    public static final int NONE_VALUE = 0;

    @Column
    private Long id;


    public Long getId() {
        return id;
    }

    public Product setId(Long id) {
        this.id = id;
        return this;
    }


}
