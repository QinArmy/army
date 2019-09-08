package com.example.domain.product;

import com.example.domain.VersionDomain;
import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;

import java.time.LocalDateTime;

import static com.example.domain.product.Product.NONE_VALUE;


/**
 * created  on 2018/9/27.
 */
@Table(name = "p_product", comment = "产品父表")
@Inheritance("productType")
@DiscriminatorValue(NONE_VALUE)
public class Product extends VersionDomain {

    public static final int NONE_VALUE = 0;

    @Column
    private Long id;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Boolean visible;

    @Column
    private Integer version;


    public Long getId() {
        return id;
    }

    public Product setId(Long id) {
        this.id = id;
        return this;
    }


}
