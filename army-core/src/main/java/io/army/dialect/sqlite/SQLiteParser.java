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

package io.army.dialect.sqlite;

import io.army.criteria.CriteriaException;
import io.army.criteria.Visible;
import io.army.criteria.impl._UnionType;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.*;
import io.army.env.EscapeMode;
import io.army.mapping.MappingType;
import io.army.meta.DatabaseObject;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.sqltype.DataType;

import javax.annotation.Nullable;
import java.util.Set;

abstract class SQLiteParser extends _ArmyDialectParser {


    SQLiteParser(DialectEnv dialectEnv, Dialect dialect) {
        super(dialectEnv, dialect);
    }

    @Override
    public final void typeName(MappingType type, StringBuilder sqlBuilder) {

    }

    @Override
    protected final Set<String> createKeyWordSet() {
        return SQLiteDialectUtils.createKeyWordSet();
    }

    @Override
    protected final char identifierDelimitedQuote() {
        return _Constant.DOUBLE_QUOTE;
    }

    @Override
    protected final String defaultFuncName() {
        // SQLite don't support DEFAULT() function
        throw new UnsupportedOperationException();
    }

    @Override
    protected final boolean isSupportZone() {
        return false;
    }

    @Override
    protected final boolean isSetClauseTableAlias() {
        return false;
    }

    @Override
    protected final boolean isTableAliasAfterAs() {
        return false;
    }

    @Override
    protected final boolean isSupportOnlyDefault() {
        return false;
    }

    @Override
    protected final boolean isSupportRowAlias() {
        return false;
    }

    @Override
    protected final boolean isSupportTableOnly() {
        return false;
    }

    @Override
    protected final ChildUpdateMode childUpdateMode() {
        return null;
    }

    @Override
    protected final boolean isSupportSingleUpdateAlias() {
        return false;
    }

    @Override
    protected final boolean isSupportSingleDeleteAlias() {
        return false;
    }

    @Override
    protected final boolean isSupportWithClause() {
        return false;
    }

    @Override
    protected final boolean isSupportWithClauseInInsert() {
        return false;
    }

    @Override
    protected final boolean isSupportWindowClause() {
        return false;
    }

    @Override
    protected final boolean isSupportUpdateRow() {
        return false;
    }

    @Override
    protected final boolean isSupportUpdateDerivedField() {
        return false;
    }

    @Override
    protected final boolean isSupportReturningClause() {
        return false;
    }

    @Override
    protected final boolean isValidateUnionType() {
        return false;
    }

    @Override
    protected final void validateUnionType(_UnionType unionType) {

    }

    @Override
    protected final String qualifiedSchemaName(ServerMeta meta) {
        return null;
    }

    @Override
    protected final IdentifierMode identifierMode(String identifier) {
        return null;
    }

    @Override
    protected final void escapesIdentifier(String identifier, StringBuilder sqlBuilder) {

    }

    @Override
    protected final boolean isUseObjectNameModeMethod() {
        return false;
    }

    @Override
    protected final IdentifierMode objectNameMode(DatabaseObject object, String effectiveName) {
        return null;
    }

    @Override
    protected final void bindLiteralNull(MappingType type, DataType dataType, EscapeMode mode, StringBuilder sqlBuilder) {

    }

    @Override
    protected final boolean bindLiteral(TypeMeta typeMeta, DataType dataType, Object value, EscapeMode mode, StringBuilder sqlBuilder) {
        return false;
    }

    @Override
    protected final SQLiteDdlParser createDdlDialect() {
        return null;
    }

    @Override
    protected final boolean existsIgnoreOnConflict() {
        return false;
    }

    @Nullable
    @Override
    protected final CriteriaException supportChildInsert(_Insert._ChildInsert childStmt, Visible visible) {
        return null;
    }

    @Override
    protected final void standardLimitClause(@Nullable _Expression offset, @Nullable _Expression rowCount, _SqlContext context) {

    }


    private static class Standard extends SQLiteParser {

        private Standard(DialectEnv dialectEnv, Dialect dialect) {
            super(dialectEnv, dialect);
        }


    } // Standard


}
