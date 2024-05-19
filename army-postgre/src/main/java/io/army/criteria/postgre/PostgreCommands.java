package io.army.criteria.postgre;

import io.army.criteria.Selection;
import io.army.criteria.dialect.DqlCommand;
import io.army.criteria.impl.ArmySelections;
import io.army.criteria.impl.ContextStack;
import io.army.criteria.impl.CriteriaContexts;
import io.army.criteria.impl.CriteriaSupports;
import io.army.criteria.postgre.inner._PostgreCommand;
import io.army.criteria.standard.SQLs;
import io.army.mapping.*;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;


abstract class PostgreCommands {

    private PostgreCommands() {
        throw new UnsupportedOperationException();
    }


    /**
     * <p>see {@code  io.army.robot.PostgreVariableRobotTests#variableCaseStatement()}
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-show.html">SHOW — show the value of a run-time parameter</a>
     * @see <a href="https://www.postgresql.org/docs/current/runtime-config-wal.html">runtime-config-wal</a>
     * @see <a href="https://www.postgresql.org/docs/16/runtime-config-connection.html">Connection Settings</a>
     */
    static DqlCommand show(final String name) {
        if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAnd(_Exceptions::varNameNoText);
        }
        final MappingType type;
        switch (name.toLowerCase(Locale.ROOT)) {
            case "allow_in_place_tablespaces":
            case "allow_system_table_mods":
            case "array_nulls":
            case "autovacuum":
            case "bonjour":
            case "check_function_bodies":
            case "data_checksums":
            case "data_sync_retry":
            case "db_user_namespace":
            case "debug_assertions":
            case "debug_pretty_print":
            case "debug_print_parse":
            case "debug_print_plan":
            case "debug_print_rewritten":
            case "default_transaction_deferrable":
            case "default_transaction_read_only":
            case "enable_async_append":
            case "enable_bitmapscan":
            case "enable_gathermerge":
            case "enable_hashagg":
            case "enable_hashjoin":
            case "enable_incremental_sort":
            case "enable_indexonlyscan":
            case "enable_indexscan":
            case "enable_material":
            case "enable_memoize":
            case "enable_mergejoin":
            case "enable_nestloop":
            case "enable_parallel_append":
            case "enable_parallel_hash":
            case "enable_partition_pruning":
            case "enable_partitionwise_aggregate":
            case "enable_partitionwise_join":
            case "enable_seqscan":
            case "enable_sort":
            case "enable_tidscan":
            case "escape_string_warning":
            case "exit_on_error":
            case "fsync":
            case "full_page_writes":
            case "geqo":
            case "hot_standby":
            case "hot_standby_feedback":
            case "ignore_checksum_failure":
            case "ignore_invalid_pages":
            case "ignore_system_indexes":
            case "in_hot_standby":
            case "integer_datetimes":
            case "jit":
            case "jit_debugging_support":
            case "jit_dump_bitcode":
            case "jit_expressions":
            case "jit_profiling_support":
            case "jit_tuple_deforming":
            case "krb_caseins_users":
            case "lo_compat_privileges":
            case "log_checkpoints":
            case "log_connections":
            case "log_disconnections":
            case "log_duration":
            case "log_executor_stats":
            case "log_hostname":
            case "log_lock_waits":
            case "log_parser_stats":
            case "log_planner_stats":
            case "log_recovery_conflict_waits":
            case "log_replication_commands":
            case "log_statement_stats":
            case "log_truncate_on_rotation":
            case "logging_collector":
            case "parallel_leader_participation":
            case "quote_all_identifiers":
            case "recovery_target_inclusive":
            case "remove_temp_files_after_crash":
            case "restart_after_crash":
            case "row_security":
            case "ssl":
            case "ssl_passphrase_command_supports_reload":
            case "ssl_prefer_server_ciphers":
            case "standard_conforming_strings":
            case "synchronize_seqscans":
            case "syslog_sequence_numbers":
            case "syslog_split_messages":
            case "trace_notify":
            case "trace_sort":
            case "track_activities":
            case "track_commit_timestamp":
            case "track_counts":
            case "track_io_timing":
            case "track_wal_io_timing":
            case "transaction_deferrable":
            case "transaction_read_only":
            case "transform_null_equals":
            case "update_process_title":
            case "wal_init_zero":
            case "wal_log_hints":
            case "wal_receiver_create_temp_slot":
            case "wal_recycle":
            case "zero_damaged_pages":
                type = BooleanType.INSTANCE;
                break;
            case "archive_timeout":
            case "authentication_timeout":
            case "autovacuum_analyze_threshold":
            case "autovacuum_freeze_max_age":
            case "autovacuum_max_workers":
            case "autovacuum_multixact_freeze_max_age":
            case "autovacuum_naptime":
            case "autovacuum_vacuum_cost_limit":
            case "autovacuum_vacuum_insert_threshold":
            case "autovacuum_vacuum_threshold":
            case "autovacuum_work_mem":
            case "backend_flush_after":
            case "bgwriter_delay":
            case "bgwriter_flush_after":
            case "bgwriter_lru_maxpages":
            case "block_size":
            case "checkpoint_flush_after":
            case "checkpoint_timeout":
            case "checkpoint_warning":
            case "client_connection_check_interval":
            case "commit_delay":
            case "commit_siblings":
            case "data_directory_mode":
            case "deadlock_timeout":
            case "debug_discard_caches":
            case "default_statistics_target":
            case "effective_cache_size":
            case "effective_io_concurrency":
            case "extra_float_digits":
            case "from_collapse_limit":
            case "geqo_effort":
            case "geqo_generations":
            case "geqo_pool_size":
            case "geqo_threshold":
            case "gin_fuzzy_search_limit":
            case "gin_pending_list_limit":
            case "huge_page_size":
            case "idle_in_transaction_session_timeout":
            case "idle_session_timeout":
            case "join_collapse_limit":
            case "lock_timeout":
            case "log_autovacuum_min_duration":
            case "log_file_mode":
            case "log_min_duration_sample":
            case "log_min_duration_statement":
            case "log_parameter_max_length":
            case "log_parameter_max_length_on_error":
            case "log_rotation_age":
            case "log_rotation_size":
            case "log_startup_progress_interval":
            case "log_temp_files":
            case "logical_decoding_work_mem":
            case "maintenance_io_concurrency":
            case "maintenance_work_mem":
            case "max_connections":
            case "max_files_per_process":
            case "max_function_args":
            case "max_identifier_length":
            case "max_index_keys":
            case "max_locks_per_transaction":
            case "max_logical_replication_workers":
            case "max_parallel_maintenance_workers":
            case "max_parallel_workers":
            case "max_parallel_workers_per_gather":
            case "max_pred_locks_per_page":
            case "max_pred_locks_per_relation":
            case "max_pred_locks_per_transaction":
            case "max_prepared_transactions":
            case "max_replication_slots":
            case "max_slot_wal_keep_size":
            case "max_stack_depth":
            case "max_standby_archive_delay":
            case "max_standby_streaming_delay":
            case "max_sync_workers_per_subscription":
            case "max_wal_senders":
            case "max_wal_size":
            case "max_worker_processes":
            case "min_dynamic_shared_memory":
            case "min_parallel_index_scan_size":
            case "min_parallel_table_scan_size":
            case "min_wal_size":
            case "old_snapshot_threshold":
            case "port":
            case "post_auth_delay":
            case "pre_auth_delay":
            case "recovery_min_apply_delay":
            case "segment_size":
            case "server_version_num":
            case "shared_buffers":
            case "shared_memory_size":
            case "shared_memory_size_in_huge_pages":
            case "statement_timeout":
            case "superuser_reserved_connections":
            case "tcp_keepalives_count":
            case "tcp_keepalives_idle":
            case "tcp_keepalives_interval":
            case "tcp_user_timeout":
            case "temp_buffers":
            case "temp_file_limit":
            case "track_activity_query_size":
            case "unix_socket_permissions":
            case "vacuum_cost_limit":
            case "vacuum_cost_page_dirty":
            case "vacuum_cost_page_hit":
            case "vacuum_cost_page_miss":
            case "vacuum_defer_cleanup_age":
            case "vacuum_failsafe_age":
            case "vacuum_freeze_min_age":
            case "vacuum_freeze_table_age":
            case "vacuum_multixact_failsafe_age":
            case "vacuum_multixact_freeze_min_age":
            case "vacuum_multixact_freeze_table_age":
            case "wal_block_size":
            case "wal_buffers":
            case "wal_decode_buffer_size":
            case "wal_keep_size":
            case "wal_receiver_status_interval":
            case "wal_receiver_timeout":
            case "wal_retrieve_retry_interval":
            case "wal_segment_size":
            case "wal_sender_timeout":
            case "wal_skip_threshold":
            case "wal_writer_delay":
            case "wal_writer_flush_after":
            case "work_mem":
                type = IntegerType.INSTANCE;
                break;
            case "autovacuum_analyze_scale_factor":
            case "autovacuum_vacuum_cost_delay":
            case "autovacuum_vacuum_insert_scale_factor":
            case "autovacuum_vacuum_scale_factor":
            case "bgwriter_lru_multiplier":
            case "checkpoint_completion_target":
            case "cpu_index_tuple_cost":
            case "cpu_operator_cost":
            case "cpu_tuple_cost":
            case "cursor_tuple_fraction":
            case "geqo_seed":
            case "geqo_selection_bias":
            case "hash_mem_multiplier":
            case "jit_above_cost":
            case "jit_inline_above_cost":
            case "jit_optimize_above_cost":
            case "log_statement_sample_rate":
            case "log_transaction_sample_rate":
            case "parallel_setup_cost":
            case "parallel_tuple_cost":
            case "random_page_cost":
            case "recursive_worktable_factor":
            case "seq_page_cost":
            case "vacuum_cost_delay":
                type = FloatType.INSTANCE;
                break;
            case "datestyle":
            case "intervalstyle":
            case "timezone":
            case "application_name":
            case "archive_cleanup_command":
            case "archive_command":
            case "archive_library":
            case "archive_mode":
            case "backslash_quote":
            case "backtrace_functions":
            case "bonjour_name":
            case "bytea_output":
            case "client_encoding":
            case "client_min_messages":
            case "cluster_name":
            case "compute_query_id":
            case "constraint_exclusion":
            case "default_table_access_method":
            case "default_tablespace":
            case "default_text_search_config":
            case "default_toast_compression":
            case "default_transaction_isolation":
            case "dynamic_shared_memory_type":
            case "event_source":
            case "force_parallel_mode":
            case "huge_pages":
            case "lc_collate":
            case "lc_ctype":
            case "lc_messages":
            case "lc_monetary":
            case "lc_numeric":
            case "lc_time":
            case "listen_addresses":
            case "local_preload_libraries":
            case "log_destination":
            case "log_error_verbosity":
            case "log_line_prefix":
            case "log_min_error_statement":
            case "log_min_messages":
            case "log_statement":
            case "log_timezone":
            case "password_encryption":
            case "plan_cache_mode":
            case "primary_slot_name":
            case "promote_trigger_file":
            case "recovery_end_command":
            case "recovery_init_sync_method":
            case "recovery_prefetch":
            case "recovery_target":
            case "recovery_target_action":
            case "recovery_target_lsn":
            case "recovery_target_name":
            case "recovery_target_time":
            case "recovery_target_timeline":
            case "recovery_target_xid":
            case "restore_command":
            case "search_path":
            case "server_encoding":
            case "server_version":
            case "session_replication_role":
            case "shared_memory_type":
            case "ssl_ca_file":
            case "ssl_cert_file":
            case "ssl_crl_dir":
            case "ssl_crl_file":
            case "ssl_key_file":
            case "ssl_library":
            case "stats_fetch_consistency":
            case "synchronous_commit":
            case "synchronous_standby_names":
            case "syslog_facility":
            case "syslog_ident":
            case "temp_tablespaces":
            case "timezone_abbreviations":
            case "trace_recovery_messages":
            case "track_functions":
            case "transaction_isolation":
            case "unix_socket_group":
            case "wal_compression":
            case "wal_consistency_checking":
            case "wal_level":
            case "wal_sync_method":
            case "xmlbinary":
            case "xmloption":
            default:
                type = StringType.INSTANCE;
        }
        return new ShowCommand(name, type);
    }

    static DqlCommand showAll() {
        return new ShowCommand();
    }


    static final class ShowCommand extends CriteriaSupports.StatementMockSupport implements DqlCommand,
            PostgreStatement,
            _PostgreCommand._ShowCommand {

        private final Object allOrName;

        private final List<Selection> selectionList;

        private ShowCommand(final String name, MappingType type) {
            super(CriteriaContexts.otherPrimaryContext(PostgreUtils.DIALECT));
            // here don't need to push stack
            this.allOrName = name;
            this.selectionList = Collections.singletonList(ArmySelections.forName(name.toLowerCase(Locale.ROOT), type));
        }

        private ShowCommand() {
            super(CriteriaContexts.otherPrimaryContext(PostgreUtils.DIALECT));
            // here don't need to push stack
            this.allOrName = SQLs.ALL;
            this.selectionList = ArrayUtils.of(
                    ArmySelections.forName("name", StringType.INSTANCE),
                    ArmySelections.forName("setting", StringType.INSTANCE),
                    ArmySelections.forName("description", StringType.INSTANCE)
            );
        }

        @Override
        public List<? extends Selection> selectionList() {
            return this.selectionList;
        }

        @Override
        public Object parameter() {
            return this.allOrName;
        }

        @Override
        public void prepared() {
            // no-op
        }

        @Override
        public boolean isPrepared() {
            return true;
        }

        @Override
        public void clear() {
            // no-op
        }


    } // ShowCommand

}
