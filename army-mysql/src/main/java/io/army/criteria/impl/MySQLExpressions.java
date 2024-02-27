package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.*;
import io.army.meta.ParentTableMeta;
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
     * <p>See {@code io.army.robot.MySQLSystemVariableRobotTests#systemVariableCaseStatement()}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">SET Syntax for Variable Assignment</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/server-system-variables.html">Server System Variables</a>
     */
    static SimpleExpression systemVariable(final SQLs.VarScope varScope, final String name) {
        if (!(varScope instanceof SqlWords.KeyWordVarScope)) {
            throw CriteriaUtils.unknownWords(varScope);
        } else if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAnd(_Exceptions::varNameNoText);
        }

        final SqlWords.KeyWordVarScope scope = (SqlWords.KeyWordVarScope) varScope;
        final MappingType type;

        switch (name.toLowerCase(Locale.ROOT)) {
            case "autocommit":
            case "big_tables":
            case "default_table_encryption":
            case "end_markers_in_json":
            case "explicit_defaults_for_timestamp":
            case "foreign_key_checks":
            case "global_connection_memory_tracking":
            case "keep_files_on_create":
            case "low_priority_updates":
            case "new":
            case "old_alter_table":
            case "print_identified_with_as_hex":
            case "select_into_disk_sync":
            case "session_track_schema":
            case "session_track_state_change":
            case "show_create_table_verbosity":
            case "show_gipk_in_create_table_and_information_schema":
            case "show_old_temporals":
            case "sql_auto_is_null":
            case "sql_big_selects":
            case "sql_buffer_result":
            case "sql_generate_invisible_primary_key":
            case "sql_log_off":
            case "sql_notes":
            case "sql_quote_show_create":
            case "sql_require_primary_key":
            case "sql_safe_updates":
            case "sql_warnings":
            case "transaction_read_only":
            case "tx_read_only":
            case "unique_checks":
            case "updatable_views_with_limit":
            case "windowing_use_high_precision":
            case "xa_detach_on_prepare": {
                if (scope != SQLs.SESSION && scope != SQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = BooleanType.INSTANCE;
            }
            break;
            case "block_encryption_mode":
            case "character_set_client":
            case "character_set_connection":
            case "character_set_database":
            case "character_set_filesystem":
            case "character_set_results":
            case "character_set_server":
            case "collation_connection":
            case "collation_database":
            case "collation_server":
            case "completion_type":
            case "debug":
            case "default_collation_for_utf8mb4":
            case "default_storage_engine":
            case "default_tmp_storage_engine":
            case "explain_format":
            case "internal_tmp_mem_storage_engine":
            case "lc_messages":
            case "lc_time_names":
            case "myisam_stats_method":
            case "optimizer_switch":
            case "optimizer_trace":
            case "optimizer_trace_features":
            case "session_track_gtids":
            case "session_track_system_variables":
            case "session_track_transaction_info":
            case "sql_mode":
            case "time_zone":
            case "transaction_isolation":
            case "tx_isolation": {
                if (scope != SQLs.SESSION && scope != SQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = StringType.INSTANCE;
            }
            break;
            case "bulk_insert_buffer_size":
            case "connection_memory_chunk_size":
            case "connection_memory_limit":
            case "cte_max_recursion_depth":
            case "default_week_format":
            case "div_precision_increment":
            case "eq_range_index_dive_limit":
            case "explain_json_format_version":
            case "generated_random_password_length":
            case "group_concat_max_len":
            case "histogram_generation_max_mem_size":
            case "information_schema_stats_expiry":
            case "interactive_timeout":
            case "join_buffer_size":
            case "lock_wait_timeout":
            case "max_allowed_packet":
            case "max_delayed_threads":
            case "max_error_count":
            case "max_execution_time":
            case "max_heap_table_size":
            case "max_insert_delayed_threads":
            case "max_join_size":
            case "max_length_for_sort_data":
            case "max_points_in_geometry":
            case "max_seeks_for_key":
            case "max_sort_length":
            case "max_sp_recursion_depth":
            case "max_user_connections":
            case "min_examined_row_limit":
            case "myisam_sort_buffer_size":
            case "net_buffer_length":
            case "net_read_timeout":
            case "net_retry_count":
            case "net_write_timeout":
            case "optimizer_prune_level":
            case "optimizer_search_depth":
            case "optimizer_trace_limit":
            case "optimizer_trace_max_mem_size":
            case "optimizer_trace_offset":
            case "parser_max_mem_size":
            case "preload_buffer_size":
            case "query_alloc_block_size":
            case "query_prealloc_size":
            case "range_alloc_block_size":
            case "range_optimizer_max_mem_size":
            case "read_buffer_size":
            case "read_rnd_buffer_size":
            case "select_into_buffer_size":
            case "select_into_disk_sync_delay":
            case "set_operations_buffer_size":
            case "sort_buffer_size":
            case "sql_select_limit":
            case "thread_pool_high_priority_connection":
            case "tmp_table_size":
            case "transaction_alloc_block_size":
            case "transaction_prealloc_size":
            case "wait_timeout": {
                if (scope != SQLs.SESSION && scope != SQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = IntegerType.INSTANCE;
            }
            break;
            case "long_query_time": {
                if (scope != SQLs.SESSION && scope != SQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = BigDecimalType.INSTANCE;
            }
            break;
            case "pseudo_replica_mode":
            case "pseudo_slave_mode":
            case "require_row_format":
            case "show_create_table_skip_secondary_engine": {
                if (scope != SQLs.SESSION) {
                    throw systemVariableScopeError(scope, name);
                }
                type = BooleanType.INSTANCE;
            }
            break;
            case "debug_sync":
            case "external_user":
            case "proxy_user":
            case "rbr_exec_mode":
            case "resultset_metadata":
            case "use_secondary_engine": {
                if (scope != SQLs.SESSION) {
                    throw systemVariableScopeError(scope, name);
                }
                type = StringType.INSTANCE;
            }
            break;
            case "pseudo_thread_id":
            case "rand_seed1": {
                if (scope != SQLs.SESSION) {
                    throw systemVariableScopeError(scope, name);
                }
                type = IntegerType.INSTANCE;
            }
            break;
            case "secondary_engine_cost_threshold":
            case "timestamp": {
                if (scope != SQLs.SESSION) {
                    throw systemVariableScopeError(scope, name);
                }
                type = BigDecimalType.INSTANCE;
            }
            break;
            case "activate_all_roles_on_login":
            case "authentication_windows_use_principal_name":
            case "auto_generate_certs":
            case "automatic_sp_privileges":
            case "avoid_temporal_upgrade":
            case "caching_sha2_password_auto_generate_rsa_keys":
            case "check_proxy_users":
            case "component_scheduler.enabled":
            case "core_file":
            case "create_admin_listener_thread":
            case "disconnect_on_expired_password":
            case "enterprise_encryption.rsa_support_legacy_padding":
            case "flush":
            case "general_log":
            case "have_statement_timeout":
            case "large_files_support":
            case "large_pages":
            case "local_infile":
            case "locked_in_memory":
            case "log_queries_not_using_indexes":
            case "log_raw":
            case "log_slow_admin_statements":
            case "log_slow_extra":
            case "lower_case_file_system":
            case "myisam_use_mmap":
            case "mysql_native_password_proxy_users":
            case "named_pipe":
            case "offline_mode":
            case "old":
            case "partial_revokes":
            case "password_require_current":
            case "persist_sensitive_variables_in_plaintext":
            case "persisted_globals_load":
            case "read_only":
            case "require_secure_transport":
            case "sha256_password_auto_generate_rsa_keys":
            case "sha256_password_proxy_users":
            case "shared_memory":
            case "skip_external_locking":
            case "skip_name_resolve":
            case "skip_networking":
            case "skip_show_database":
            case "slow_query_log":
            case "ssl_session_cache_mode":
            case "super_read_only":
            case "syseventlog.include_pid":
            case "table_encryption_privilege_check":
            case "temptable_use_mmap":
            case "thread_pool_dedicated_listeners":
            case "tls_certificates_enforced_validation": {
                if (scope != SQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = BooleanType.INSTANCE;
            }
            break;
            case "admin_address":
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
            case "concurrent_insert":
            case "datadir":
            case "default_authentication_plugin":
            case "delay_key_write":
            case "disabled_storage_engines":
            case "dragnet.log_error_filter_rules":
            case "event_scheduler":
            case "ft_boolean_syntax":
            case "ft_stopword_file":
            case "general_log_file":
            case "have_ssl":
            case "hostname":
            case "init_connect":
            case "init_file":
            case "lc_messages_dir":
            case "license":
            case "log_error":
            case "log_error_services":
            case "log_error_suppression_list":
            case "log_output":
            case "log_timestamps":
            case "mandatory_roles":
            case "mecab_rc_file":
            case "myisam_recover_options":
            case "named_pipe_full_access_group":
            case "persist_only_admin_x509_subject":
            case "pid_file":
            case "plugin_dir":
            case "protocol_compression_algorithms":
            case "secure_file_priv":
            case "sha256_password_private_key_path":
            case "sha256_password_public_key_path":
            case "shared_memory_base_name":
            case "slow_query_log_file":
            case "socket":
            case "ssl_ca":
            case "ssl_capath":
            case "ssl_cert":
            case "ssl_cipher":
            case "ssl_crl":
            case "ssl_crlpath":
            case "ssl_fips_mode":
            case "ssl_key":
            case "syseventlog.facility":
            case "syseventlog.tag":
            case "system_time_zone":
            case "thread_handling":
            case "tls_ciphersuites":
            case "tls_version":
            case "tmpdir":
            case "version_comment":
            case "version_compile_machine":
            case "version_compile_os":
            case "version_compile_zlib": {
                if (scope != SQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = StringType.INSTANCE;
            }
            break;
            case "admin_port":
            case "authentication_windows_log_level":
            case "back_log":
            case "caching_sha2_password_digest_rounds":
            case "connect_timeout":
            case "default_password_lifetime":
            case "delayed_insert_limit":
            case "delayed_insert_timeout":
            case "delayed_queue_size":
            case "enterprise_encryption.maximum_rsa_key_size":
            case "flush_time":
            case "ft_max_word_len":
            case "ft_min_word_len":
            case "ft_query_expansion_limit":
            case "global_connection_memory_limit":
            case "host_cache_size":
            case "key_buffer_size":
            case "key_cache_age_threshold":
            case "key_cache_block_size":
            case "key_cache_division_limit":
            case "large_page_size":
            case "log_error_verbosity":
            case "log_throttle_queries_not_using_indexes":
            case "lower_case_table_names":
            case "max_connect_errors":
            case "max_connections":
            case "max_digest_length":
            case "max_prepared_stmt_count":
            case "max_write_lock_count":
            case "myisam_data_pointer_size":
            case "myisam_max_sort_file_size":
            case "myisam_mmap_size":
            case "ngram_token_size":
            case "open_files_limit":
            case "password_history":
            case "password_reuse_interval":
            case "port":
            case "protocol_version":
            case "regexp_stack_limit":
            case "regexp_time_limit":
            case "schema_definition_cache":
            case "slow_launch_time":
            case "ssl_session_cache_timeout":
            case "stored_program_cache":
            case "stored_program_definition_cache":
            case "table_definition_cache":
            case "table_open_cache":
            case "table_open_cache_instances":
            case "tablespace_definition_cache":
            case "temptable_max_mmap":
            case "temptable_max_ram":
            case "thread_cache_size":
            case "thread_pool_algorithm":
            case "thread_pool_max_active_query_threads":
            case "thread_pool_max_transactions_limit":
            case "thread_pool_max_unused_threads":
            case "thread_pool_prio_kickup_timer":
            case "thread_pool_query_threads_per_group":
            case "thread_pool_size":
            case "thread_pool_stall_limit":
            case "thread_pool_transaction_delay":
            case "thread_stack": {
                if (scope != SQLs.GLOBAL) {
                    throw systemVariableScopeError(scope, name);
                }
                type = IntegerType.INSTANCE;
            }
            break;
            default:
                type = StringType.INSTANCE;
        }
        return new SystemVariableExpression(scope, name, type);
    }


    /*-------------------below private static methods -------------------*/

    /**
     * @see #systemVariable(SQLs.VarScope, String)
     */
    private static CriteriaException systemVariableScopeError(SqlWords.KeyWordVarScope scope, String name) {
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

            context.appendLiteral(this.type, this.literal, true);

            final String collationName = this.collationName;
            if (collationName != null) {
                sqlBuilder.append(_Constant.SPACE_COLLATE_SPACE);
                context.identifier(collationName, sqlBuilder);
            }

        }


        @Override
        public boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
            // always false
            return false;
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

        private final SqlWords.KeyWordVarScope scope;

        private final String name;

        private final MappingType type;

        /**
         * @see #systemVariable(SQLs.VarScope, String)
         */
        private SystemVariableExpression(SqlWords.KeyWordVarScope scope, String name, MappingType type) {
            this.scope = scope;
            this.name = name;
            this.type = type;
        }

        @Override
        public MappingType typeMeta() {
            return this.type;
        }

        @Override
        public boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
            // always false
            return false;
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
            final SqlWords.KeyWordVarScope scope = this.scope;
            switch (scope) {
                case SESSION:
                case GLOBAL:
                    sqlBuilder.append(" @@")
                            .append(scope.name())
                            .append(_Constant.PERIOD);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(scope);

            }
        }


    } // VariableExpression


}
