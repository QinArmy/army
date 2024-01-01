package io.army.example.bank.domain.user;

import io.army.annotation.Index;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;

@Table(name = "history_china_region", indexes = {
        @Index(name = "uni_name_region_type", fieldList = {"name", "regionType"}, unique = true),
        @Index(name = "inx_parent_id", fieldList = "parentId")},
        comment = "china region")
@Inheritance("regionType")
public class HistoryChinaRegion<T extends HistoryChinaRegion<T>> extends AbstractChinaRegion<T> {

    public static HistoryChinaRegion<?> create() {
        return new HistoryChinaRegion<>();
    }

    @SuppressWarnings("unchecked")
    public static final Class<HistoryChinaRegion<?>> CLASS = (Class<HistoryChinaRegion<?>>) ((Class<?>) HistoryChinaRegion.class);


}
