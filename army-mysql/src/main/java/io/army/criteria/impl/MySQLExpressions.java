package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.BooleanType;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.meta.TypeMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * <p>Package class.
 *
 * @since 0.6.4
 */
abstract class MySQLExpressions {

    private MySQLExpressions() {
        throw new UnsupportedOperationException();
    }

    static LiteralExpression textLiteral(final @Nullable String charsetName, final @Nullable String literal,
                                         final @Nullable String collationName) {
        if (charsetName == null) {
            throw ContextStack.clearStackAndCriteriaError("charsetName must non-null");
        } else if (literal == null) {
            throw ContextStack.clearStackAndCriteriaError("literal must non-null");
        }
        return new AnonymousTextLiteral(charsetName, StringType.INSTANCE, literal, collationName);
    }

    static LiteralExpression encodingTextLiteral(final @Nullable String charsetName, final TableField field,
                                                 final @Nullable String literal, final @Nullable String collationName) {
        if (charsetName == null) {
            throw ContextStack.clearStackAndCriteriaError("charsetName must non-null");
        } else if (!field.codec()) {
            String m = String.format("%s isn't codec filed,you should invoke %s.textLiteral() method.",
                    field, MySQLs.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        } else if (!(field.mappingType() instanceof MappingType.SqlStringType)) {
            String m = String.format("%s isn't string type", field);
            throw ContextStack.clearStackAndCriteriaError(m);
        } else if (literal == null) {
            throw ContextStack.clearStackAndCriteriaError("literal must non-null");
        }
        return new AnonymousTextLiteral(charsetName, field, literal, collationName);
    }


    /**
     * <p>Create system variable expression
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">SET Syntax for Variable Assignment</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/server-system-variables.html">Server System Variables</a>
     */
    static SimpleExpression systemVariable(final MySQLs.VarScope varScope, final String name) {
        if (!(varScope instanceof MySQLWords.KeyWordVarScope)) {
            throw CriteriaUtils.unknownWords(varScope);
        } else if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAnd(_Exceptions::varNameNoText);
        }

        final MySQLWords.KeyWordVarScope scope = (MySQLWords.KeyWordVarScope) varScope;
        final MappingType type;
        switch (name.toLowerCase(Locale.ROOT)) {

            case "debug_sync": {
                if (scope != MySQLs.SESSION) {
                    throw systemVariableScopeError(scope, name);
                }
                type = StringType.INSTANCE;
            }
            break;
            case "autocommit":
            case "big_tables":
            case "default_table_encryption": {
                if (scope != MySQLs.SESSION && scope != MySQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = BooleanType.INSTANCE;
            }
            break;
            case "default_tmp_storage_engine":
            case "default_storage_engine":
            case "default_collation_for_utf8mb4":
            case "debug":
            case "completion_type":
            case "collation_server":
            case "collation_database":
            case "collation_connection":
            case "character_set_server":
            case "character_set_results":
            case "character_set_filesystem":
            case "character_set_database":
            case "character_set_connection":
            case "character_set_client":
            case "block_encryption_mode": {
                if (scope != MySQLs.SESSION && scope != MySQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = StringType.INSTANCE;
            }
            break;
            case "default_week_format":
            case "cte_max_recursion_depth":
            case "connection_memory_limit":
            case "connection_memory_chunk_size":
            case "bulk_insert_buffer_size": {
                if (scope != MySQLs.SESSION && scope != MySQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = IntegerType.INSTANCE;
            }
            break;
            case "activate_all_roles_on_login":
            case "authentication_windows_use_principal_name":
            case "automatic_sp_privileges":
            case "auto_generate_certs":
            case "avoid_temporal_upgrade":
            case "check_proxy_users":
            case "create_admin_listener_thread":
            case "core_file":
            case "component_scheduler.enabled":
            case "caching_sha2_password_auto_generate_rsa_keys": {
                if (scope != MySQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = BooleanType.INSTANCE;
            }
            break;
            case "admin_ssl_ca":
            case "admin_ssl_capath":
            case "admin_ssl_cert":
            case "admin_ssl_cipher":
            case "admin_ssl_crl":
            case "admin_ssl_crlpath":
            case "admin_ssl_key":
            case "admin_tls_ciphersuites":
            case "admin_tls_version":
            case "authentication_policy":
            case "basedir":
            case "bind_address":
            case "build_id":
            case "caching_sha2_password_private_key_path":
            case "caching_sha2_password_public_key_path":
            case "character_set_system":
            case "character_sets_dir":

            case "disabled_storage_engines":
            case "delay_key_write":
            case "default_authentication_plugin":
            case "datadir":
            case "concurrent_insert":
            case "admin_address": {
                if (scope != MySQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = StringType.INSTANCE;
            }
            break;
            case "admin_port":
            case "authentication_windows_log_level":
            case "back_log":

            case "delayed_queue_size":
            case "delayed_insert_timeout":
            case "delayed_insert_limit":
            case "default_password_lifetime":
            case "connect_timeout":
            case "caching_sha2_password_digest_rounds": {
                if (scope != MySQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = IntegerType.INSTANCE;
            }
            break;
            default:
                type = StringType.INSTANCE;
        }
        return new SystemVariableExpression((MySQLWords.KeyWordVarScope) varScope, name, type);
    }


    /*-------------------below private static methods -------------------*/

    /**
     * @see #systemVariable(MySQLs.VarScope, String)
     */
    private static CriteriaException systemVariableScopeError(MySQLWords.KeyWordVarScope scope, String name) {
        String m = String.format("system variable[%s] isn't %s scope", name, scope.name());
        return ContextStack.clearStackAndCriteriaError(m);
    }


    private static final class AnonymousTextLiteral extends OperationExpression.OperationDefiniteExpression
            implements LiteralExpression, SqlValueParam.SingleAnonymousValue {

        private final String charsetName;

        private final TypeMeta type;


        private final String literal;


        private final String collationName;


        private AnonymousTextLiteral(String charsetName, TypeMeta type, String literal, @Nullable String collationName) {
            this.charsetName = charsetName;
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                this.type = type;
            }
            this.literal = literal;
            this.collationName = collationName;
        }

        @Override
        public TypeMeta typeMeta() {
            // here , allow FieldMeta , because this is literal
            return this.type;
        }


        @Override
        public String value() {
            return this.literal;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(" _");
            context.identifier(this.charsetName, sqlBuilder);

            context.appendLiteral(this.type, this.literal);

            final String collationName = this.collationName;
            if (collationName != null) {
                sqlBuilder.append(_Constant.SPACE_COLLATE_SPACE);
                context.identifier(collationName, sqlBuilder);
            }

        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(" _")
                    .append(this.charsetName)
                    .append(_Constant.SPACE)
                    .append(_Constant.QUOTE)
                    .append(this.literal)
                    .append(_Constant.QUOTE);

            final String collationName = this.collationName;
            if (collationName != null) {
                builder.append(_Constant.SPACE_COLLATE_SPACE)
                        .append(collationName);
            }
            return builder.toString();
        }


    } // TextLiteralExpression


    private static final class SystemVariableExpression extends OperationExpression.OperationSimpleExpression {

        private final MySQLWords.KeyWordVarScope scope;

        private final String name;

        private final MappingType type;

        /**
         * @see #systemVariable(MySQLs.VarScope, String)
         */
        private SystemVariableExpression(MySQLWords.KeyWordVarScope scope, String name, MappingType type) {
            this.scope = scope;
            this.name = name;
            this.type = type;
        }

        @Override
        public MappingType typeMeta() {
            return this.type;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            appendVarCope(sqlBuilder);
            context.identifier(this.name, sqlBuilder);
        }


        @Override
        public String toString() {
            final StringBuilder sqlBuilder = new StringBuilder(30);

            appendVarCope(sqlBuilder);

            return sqlBuilder
                    .append(this.name)
                    .toString();
        }


        private void appendVarCope(final StringBuilder sqlBuilder) {
            final MySQLWords.KeyWordVarScope scope = this.scope;
            switch (scope) {
                case SESSION:
                case GLOBAL:
                case PERSIST:
                case PERSIST_ONLY: {
                    sqlBuilder.append(scope.spaceRender())
                            .append(_Constant.PERIOD);
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(scope);

            }
        }


    } // VariableExpression


}
