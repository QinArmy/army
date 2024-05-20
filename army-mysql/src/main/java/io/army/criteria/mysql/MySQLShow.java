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

package io.army.criteria.mysql;

import io.army.criteria.DqlStatement;
import io.army.criteria.Expression;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;

import java.util.function.BiFunction;
import java.util.function.Supplier;


/**
 * <p>This interface representing MySQL SHOW statement.
 * <p>More document see {@link MySQLs#setStmt()}.
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show.html">SHOW Statements</a>
 */
public interface MySQLShow extends MySQLStatement {

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-binlog-events.html">SHOW BINLOG EVENTS Statement</a>
     */
    interface _BinLogEventsLimitSpec extends _LimitClause<_AsCommandClause<DqlStatement>>, _AsCommandClause<DqlStatement> {


    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-binlog-events.html">SHOW BINLOG EVENTS Statement</a>
     */
    interface _BinLogEventsFromSpec extends _BinLogEventsLimitSpec {

        _BinLogEventsLimitSpec from(Expression pos);

        _BinLogEventsLimitSpec from(BiFunction<MappingType, Number, Expression> func, Number pos);

        _BinLogEventsLimitSpec ifFrom(Supplier<Expression> supplier);

        _BinLogEventsLimitSpec ifFrom(BiFunction<MappingType, Number, Expression> func, Supplier<Number> supplier);

    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-binlog-events.html">SHOW BINLOG EVENTS Statement</a>
     */
    interface _BinLogEventsInSpec extends _BinLogEventsFromSpec {

        _BinLogEventsFromSpec in(Expression logName);

        _BinLogEventsFromSpec in(BiFunction<MappingType, String, Expression> func, String logName);

        _BinLogEventsFromSpec ifIn(Supplier<Expression> supplier);

        _BinLogEventsFromSpec ifIn(BiFunction<MappingType, String, Expression> func, Supplier<String> supplier);

    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-character-set.html">SHOW CHARACTER SET Statement</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-columns.html"> SHOW COLUMNS Statement</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-collation.html">SHOW COLLATION Statement</a>
     */
    interface _CharacterSetWhereAndSpec extends _WhereAndClause<_CharacterSetWhereAndSpec>,
            _AsCommandClause<DqlStatement> {

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-character-set.html">SHOW CHARACTER SET Statement</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-columns.html"> SHOW COLUMNS Statement</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-collation.html">SHOW COLLATION Statement</a>
     */
    interface _LikeWhereSpec extends _QueryWhereClause<_AsCommandClause<DqlStatement>, _CharacterSetWhereAndSpec> {

        _AsCommandClause<DqlStatement> like(Expression pattern);

        _AsCommandClause<DqlStatement> like(BiFunction<MappingType, String, Expression> func, String pattern);

        _AsCommandClause<DqlStatement> ifLike(Supplier<Expression> supplier);

        _AsCommandClause<DqlStatement> ifLike(BiFunction<MappingType, String, Expression> func, Supplier<String> supplier);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-columns.html"> SHOW COLUMNS Statement</a>
     */
    interface _ColumnsFromInDatabaseSpec extends _LikeWhereSpec {

        _LikeWhereSpec from(String databaseName);

        _LikeWhereSpec in(String databaseName);

        _LikeWhereSpec ifFrom(Supplier<String> supplier);

        _LikeWhereSpec ifIn(Supplier<String> supplier);

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-columns.html"> SHOW COLUMNS Statement</a>
     */
    interface _ColumnsFromInTableClause {

        _ColumnsFromInDatabaseSpec from(TableMeta<?> table);

        _ColumnsFromInDatabaseSpec in(TableMeta<?> table);

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-columns.html"> SHOW COLUMNS Statement</a>
     */
    interface _ColumnFieldClause {

        _ColumnsFromInTableClause columns();

        _ColumnsFromInTableClause fields();

    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-columns.html"> SHOW COLUMNS Statement</a>
     */
    interface _OptionFullSpec extends _ColumnFieldClause {

        _ColumnFieldClause full();

    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-columns.html"> SHOW COLUMNS Statement</a>
     */
    interface _OptionExtendedSpec extends _OptionFullSpec {

        _OptionFullSpec extended();

    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-database.html">SHOW CREATE DATABASE Statement</a>
     */
    interface _IfNotExistsClause {

        _AsCommandClause<DqlStatement> ifNotExists(String databaseName);


    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show.html">SHOW Statements</a>
     */
    interface _ShowCommandSpec extends _OptionExtendedSpec {

        _AsCommandClause<DqlStatement> binaryLogs();

        /**
         * @since MySQL 8.2.0
         */
        _AsCommandClause<DqlStatement> binaryLogsStatus();

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-binlog-events.html">SHOW BINLOG EVENTS Statement</a>
         */
        _BinLogEventsInSpec binLogEvents();

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-character-set.html">SHOW CHARACTER SET Statement</a>
         */
        _LikeWhereSpec characterSet();

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-collation.html">SHOW COLLATION Statement</a>
         */
        _LikeWhereSpec collation();

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-database.html">SHOW CREATE DATABASE Statement</a>
         */
        _IfNotExistsClause createDatabase();

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-database.html">SHOW CREATE DATABASE Statement</a>
         */
        _AsCommandClause<DqlStatement> createDatabase(String databaseName);

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-event.html">SHOW CREATE EVENT Statement</a>
         */
        _AsCommandClause<DqlStatement> createEvent(String eventName);

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-function.html">SHOW CREATE FUNCTION Statement</a>
         */
        _AsCommandClause<DqlStatement> createFunction(String funcName);

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-procedure.html">SHOW CREATE PROCEDURE Statement</a>
         */
        _AsCommandClause<DqlStatement> createProcedure(String procName);


        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-table.html">SHOW CREATE TABLE Statement</a>
         */
        _AsCommandClause<DqlStatement> createTable(String tableName);

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-trigger.html">SHOW CREATE TRIGGER Statement</a>
         */
        _AsCommandClause<DqlStatement> createTrigger(String triggerName);

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-user.html">SHOW CREATE USER Statement</a>
         */
        _AsCommandClause<DqlStatement> createUser(String userName);

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/show-create-view.html">SHOW CREATE VIEW Statement</a>
         */
        _AsCommandClause<DqlStatement> createView(String view);


    }

    interface _ShowClause {

        _ShowCommandSpec show();

    }


}
