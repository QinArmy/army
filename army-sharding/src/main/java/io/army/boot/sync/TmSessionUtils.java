package io.army.boot.sync;

import io.army.meta.TableMeta;
import io.army.tx.TransactionOptionImpl;
import io.army.tx.XaTransactionOption;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

abstract class TmSessionUtils {

    TmSessionUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return a unmodifiable {@link XaTransactionOption} implementation.
     */
    static XaTransactionOption createXaTransactionOption(TmSessionFactoryImpl.SessionBuilderImpl builder) {
        byte[] gtrid = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
        return TransactionOptionImpl.build(builder.transactionName(), builder.readOnly()
                , builder.isolation(), builder.timeout(), gtrid);
    }

    static boolean notSupportCache(TableMeta<?> tableMeta) {
//        FieldMeta<?, ?> primaryFieldMeta;
//        if (tableMeta instanceof ChildTableMeta) {
//            primaryFieldMeta = ((ChildTableMeta<?>) tableMeta).parentMeta().id();
//        } else {
//            primaryFieldMeta = tableMeta.id();
//        }
//
//        List<FieldMeta<?, ?>> routeFieldList;
//        routeFieldList = tableMeta.routeFieldList(true);
//        boolean support = false;
//        if (routeFieldList.contains(primaryFieldMeta)) {
//            routeFieldList = tableMeta.routeFieldList(false);
//            support = routeFieldList.contains(primaryFieldMeta);
//        }
        throw new UnsupportedOperationException();
    }

    static boolean notSupportCache(TableMeta<?> tableMeta, List<String> propNameList) {
//        List<FieldMeta<?, ?>> fieldMetaList = new ArrayList<>(propNameList.size());
//
//        TableMeta<?> routeTableMeta = tableMeta;
//        if (tableMeta instanceof ChildTableMeta) {
//            routeTableMeta = ((ChildTableMeta<?>) tableMeta).parentMeta();
//        }
//        for (String propName : propNameList) {
//            fieldMetaList.add(routeTableMeta.getField(propName));
//        }
//        List<FieldMeta<?, ?>> routeFieldList;
//        routeFieldList = tableMeta.routeFieldList(true);
//
//        boolean support = false;
//        for (FieldMeta<?, ?> fieldMeta : fieldMetaList) {
//            if (routeFieldList.contains(fieldMeta)) {
//                support = true;
//                break;
//            }
//        }
//        if (support) {
//            routeFieldList = tableMeta.routeFieldList(false);
//            boolean tableSupport = false;
//            for (FieldMeta<?, ?> fieldMeta : fieldMetaList) {
//                if (routeFieldList.contains(fieldMeta)) {
//                    tableSupport = true;
//                    break;
//                }
//            }
//            support = tableSupport;
//        }
        throw new UnsupportedOperationException();
    }
}
