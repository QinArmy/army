package io.army.example.bank.domain.user;

import io.army.annotation.Index;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;

@Table(name = "china_region", indexes = {
        @Index(name = "china_region_uni_name_region_type", fieldList = {"name", "regionType"}, unique = true),
        @Index(name = "china_region_inx_parent_id", fieldList = "parentId")},
        comment = "china region")
@Inheritance("regionType")
public class ChinaRegion<T extends ChinaRegion<T>> extends AbstractChinaRegion<T> {

    public static ChinaRegion<?> create() {
        return new ChinaRegion<>();
    }

}
