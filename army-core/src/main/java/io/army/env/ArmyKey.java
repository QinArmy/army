/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.env;

import io.army.annotation.Column;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.FieldMeta;
import io.army.session.AllowMode;
import io.army.session.DdlMode;
import io.army.util.ClassUtils;
import io.army.util._Collections;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ArmyKey<T> {


    public static final ArmyKey<Boolean> READ_ONLY = new ArmyKey<>("readonly", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<DdlMode> DDL_MODE = new ArmyKey<>("ddl.mode", DdlMode.class, DdlMode.VALIDATE);

    public static final ArmyKey<Database> DATABASE = new ArmyKey<>("database", Database.class, null);

    public static final ArmyKey<Dialect> DIALECT = new ArmyKey<>("dialect", Dialect.class, null);

    public static final ArmyKey<ZoneOffset> ZONE_OFFSET = new ArmyKey<>("zone_offset", ZoneOffset.class, null);

    public static final ArmyKey<Boolean> QUALIFIED_TABLE_NAME_ENABLE = new ArmyKey<>("qualified_table_name.enable", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<NameMode> DATABASE_NAME_MODE = new ArmyKey<>("database.name.mode", NameMode.class, NameMode.DEFAULT);

    public static final ArmyKey<NameMode> TABLE_NAME_MODE = new ArmyKey<>("table.name.mode", NameMode.class, NameMode.DEFAULT);
    public static final ArmyKey<NameMode> COLUMN_NAME_MODE = new ArmyKey<>("column.name.mode", NameMode.class, NameMode.DEFAULT);

    public static final ArmyKey<AllowMode> VISIBLE_MODE = new ArmyKey<>("visible.mode", AllowMode.class, AllowMode.NEVER);

    public static final ArmyKey<String> VISIBLE_SESSION_WHITE_LIST = new ArmyKey<>("visible.session_white_list", String.class, null);

    public static final ArmyKey<AllowMode> QUERY_INSERT_MODE = new ArmyKey<>("query.insert.mode", AllowMode.class, AllowMode.WHITE_LIST);

    public static final ArmyKey<String> QUERY_INSERT_SESSION_WHITE_LIST = new ArmyKey<>("query.insert.session_white_list", String.class, null);

    public static final ArmyKey<AllowMode> DRIVER_SPI_MODE = new ArmyKey<>("driver.spi.mode", AllowMode.class, AllowMode.NEVER);

    public static final ArmyKey<String> DRIVER_SPI_SESSION_WHITE_LIST = new ArmyKey<>("driver.spi.session_white_list", String.class, null);

    public static final ArmyKey<String> DATASOURCE_CLOSE_METHOD = new ArmyKey<>("datasource.close_method", String.class, "close");

    public static final ArmyKey<Boolean> UNRECOGNIZED_TYPE_ALLOWED = new ArmyKey<>("unrecognized_type_allowed", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<EscapeMode> LITERAL_ESCAPE_MODE = new ArmyKey<>("literal.escape_mode", EscapeMode.class, EscapeMode.DEFAULT);

    public static final ArmyKey<EscapeMode> IDENTIFIER_ESCAPE_MODE = new ArmyKey<>("identifier.escape_mode", EscapeMode.class, EscapeMode.DEFAULT);

    public static final ArmyKey<Boolean> LITERAL_TYPE_NAME_ENABLE = new ArmyKey<>("literal.type_name_enable", Boolean.class, Boolean.TRUE);


    /**
     * @see Column#scale()
     * @see FieldMeta#scale()
     */
    public static final ArmyKey<Boolean> TRUNCATED_TIME_TYPE = new ArmyKey<>("truncated.time_type", Boolean.class, Boolean.TRUE);

    public static final ArmyKey<NameMode> FUNC_NAME_MODE = new ArmyKey<>("func.name.mode", NameMode.class, NameMode.DEFAULT);

    public static final ArmyKey<Boolean> SQL_LOG_DYNAMIC = new ArmyKey<>("sql.log.dynamic", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<SqlLogMode> SQL_LOG_MODE = new ArmyKey<>("sql.log.mode", SqlLogMode.class, SqlLogMode.OFF);

    /**
     * <p>Whether print meta sql or not ,when application startup.
     * <p>Currently,jdbd support only this option
     */
    public static final ArmyKey<Boolean> SQL_LOG_PRINT_META = new ArmyKey<>("sql.log.print_meta", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<Boolean> SQL_LOG_PARSING_COST_TIME = new ArmyKey<>("sql.log.sql_parsing_cost_time", Boolean.class, Boolean.FALSE);

    public static final ArmyKey<Boolean> SQL_LOG_EXECUTION_COST_TIME = new ArmyKey<>("sql.log.sql_execution_cost_time", Boolean.class, Boolean.FALSE);

    ///  public static final ArmyKey<Boolean> SQL_LOG_PARSE_PLACEHOLDER = new ArmyKey<>("sql.log.parse_placeholder", Boolean.class, Boolean.FALSE);


    public final String name;

    public final Class<T> javaType;

    public final T defaultValue;

    ArmyKey(String name, Class<T> javaType, @Nullable T defaultValue) {
        this.name = name;
        this.javaType = javaType;
        this.defaultValue = defaultValue;
    }


    @Override
    public final int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof ArmyKey) {
            match = ((ArmyKey<?>) obj).name.equals(this.name);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final String toString() {
        return _StringUtils.builder()
                .append(this.getClass().getName())
                .append("[ name : ")
                .append(this.name)
                .append(" , javaType : ")
                .append(this.javaType.getName())
                .append(" , default : ")
                .append(this.defaultValue)
                .append(" ]")
                .toString();
    }

    public static List<ArmyKey<?>> keyList() {
        return ArmyKeyListHolder.ARMY_KEY_LIST;
    }


    private static void addAllKey(final Class<?> keyClass, final List<ArmyKey<?>> list, final Map<String, Boolean> keyMap)
            throws IllegalAccessException {
        int modifier;
        ArmyKey<?> armyKey;
        for (Field field : keyClass.getDeclaredFields()) {
            modifier = field.getModifiers();
            if (ArmyKey.class.isAssignableFrom(field.getType())
                    && Modifier.isPublic(modifier)
                    && Modifier.isStatic(modifier)
                    && Modifier.isFinal(modifier)) {

                armyKey = (ArmyKey<?>) field.get(null);
                if (keyMap.put(armyKey.name, Boolean.TRUE) != null) {
                    String m = String.format("name[%s] duplication.", armyKey.name);
                    throw new IllegalArgumentException(m);
                }
                list.add(armyKey);
            }

        }
    }

    private static final class ArmyKeyListHolder {

        private static final List<ArmyKey<?>> ARMY_KEY_LIST;

        static {
            final Class<?> syncKeyClass, reactiveKeyClass;
            syncKeyClass = ClassUtils.tryLoadClass("io.army.env.SyncKey", ArmyKey.class.getClassLoader());
            reactiveKeyClass = ClassUtils.tryLoadClass("io.army.env.ReactiveKey", ArmyKey.class.getClassLoader());

            try {
                final List<ArmyKey<?>> list = _Collections.arrayList();
                final Map<String, Boolean> keyMap = _Collections.hashMap();
                addAllKey(ArmyKey.class, list, keyMap);
                if (syncKeyClass != null) {
                    addAllKey(syncKeyClass, list, keyMap);
                }
                if (reactiveKeyClass != null) {
                    addAllKey(reactiveKeyClass, list, keyMap);
                }
                ARMY_KEY_LIST = Collections.unmodifiableList(_Collections.arrayList(list));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }


    } // ArmyKeyListHolder


}
