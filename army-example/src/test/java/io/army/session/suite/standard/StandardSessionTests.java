package io.army.session.suite.standard;

import io.army.criteria.*;
import io.army.criteria.dialect.BatchDqlStatement;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.lang.Nullable;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.session.AllowMode;
import io.army.session.FactoryUtils;
import io.army.session.SessionException;
import io.army.session.SessionFactoryException;
import io.army.sync.*;
import org.testng.annotations.BeforeClass;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class StandardSessionTests {


    protected static LocalSessionFactory syncSessionFactory;

    @BeforeClass
    public static void beforeClass() {
        final Database[] databaseArray = Database.values();
        final LocalSessionFactory[] factoryArray = new LocalSessionFactory[databaseArray.length];


        for (int i = 0; i < databaseArray.length; i++) {
            factoryArray[i] = FactoryUtils.createArmyBankSyncFactory(databaseArray[i]);
        }

        StandardSessionTests.syncSessionFactory = new DelegateSyncLocalFactory(factoryArray);
    }


    private static final class DelegateSyncLocalFactory implements LocalSessionFactory {

        private final LocalSessionFactory[] factoryArray;

        private boolean closed;

        private DelegateSyncLocalFactory(LocalSessionFactory[] factoryArray) {
            this.factoryArray = factoryArray;
        }

        @Override
        public String name() {
            return "standard-delegate";
        }

        @Override
        public ArmyEnvironment environment() {
            return this.factoryArray[0].environment();
        }

        @Override
        public SchemaMeta schemaMeta() {
            return this.factoryArray[0].schemaMeta();
        }

        @Override
        public Map<Class<?>, TableMeta<?>> tableMap() {
            return this.factoryArray[0].tableMap();
        }

        @Override
        public <T> TableMeta<T> getTable(Class<T> domainClass) {
            return this.factoryArray[0].getTable(domainClass);
        }

        @Override
        public AllowMode visibleMode() {
            return this.factoryArray[0].visibleMode();
        }

        @Override
        public AllowMode queryInsertMode() {
            return this.factoryArray[0].queryInsertMode();
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public boolean isReadonly() {
            return this.closed;
        }


        @Override
        public SessionContext currentSessionContext() throws SessionFactoryException {
            throw new UnsupportedOperationException();
        }

        @Override
        public SessionBuilder builder() {
            return null;
        }

        @Override
        public void close() throws SessionFactoryException {
            if (this.closed) {
                return;
            }
            synchronized (this) {
                for (LocalSessionFactory factory : factoryArray) {
                    factory.close();
                }
                this.closed = true;
            }
        }

        @Override
        public ZoneId zoneId() {
            return this.factoryArray[0].zoneId();
        }

        @Override
        public ServerMeta serverMeta() {
            return this.factoryArray[0].serverMeta();
        }

        @Override
        public boolean isSupportSavePoints() {
            boolean support = true;
            for (LocalSessionFactory factory : factoryArray) {
                if (factory.isSupportSavePoints()) {
                    continue;
                }
                support = false;
                break;
            }
            return support;
        }

        @Override
        public boolean isReactive() {
            return false;
        }


    }//DelegateSyncLocalFactory


    private static final class DelegateSessionBuilder implements LocalSessionFactory.SessionBuilder {

        private final DelegateSyncLocalFactory factory;

        private final LocalSessionFactory.SessionBuilder[] builderArray;

        private String name;

        private boolean readOnly;

        private Visible visible;

        private boolean allowQueryInsert;


        private DelegateSessionBuilder(DelegateSyncLocalFactory factory, LocalSessionFactory[] factoryArray) {
            final LocalSessionFactory.SessionBuilder[] builderArray;
            builderArray = new LocalSessionFactory.SessionBuilder[factoryArray.length];

            for (int i = 0; i < factoryArray.length; i++) {
                builderArray[i] = factoryArray[i].builder();
            }
            this.factory = factory;
            this.builderArray = builderArray;
        }

        @Override
        public LocalSessionFactory.SessionBuilder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        @Override
        public LocalSessionFactory.SessionBuilder readonly(boolean readonly) {
            this.readOnly = readonly;
            return this;
        }

        @Override
        public LocalSessionFactory.SessionBuilder allowQueryInsert(boolean allow) {
            this.allowQueryInsert = allow;
            return this;
        }

        @Override
        public LocalSessionFactory.SessionBuilder visibleMode(Visible visible) {
            this.visible = visible;
            return this;
        }

        @Override
        public LocalSession build() throws SessionException {
            final LocalSessionFactory.SessionBuilder[] builderArray = this.builderArray;
            final LocalSession[] sessionArray = new LocalSession[builderArray.length];

            String name = this.name;
            if (name == null) {
                this.name = name = "unnamed";
            }
            for (int i = 0; i < builderArray.length; i++) {
                sessionArray[i] = builderArray[i]
                        .name(name)
                        .readonly(this.readOnly)
                        .visibleMode(this.visible)
                        .allowQueryInsert(this.allowQueryInsert)
                        .build();
            }
            return null;
        }


    }//DelegateSessionBuilder


    private static final class DelegateSession extends _ArmySyncSession implements LocalSession {

        private final DelegateSyncLocalFactory factory;

        private final LocalSession[] sessionArray;


        private boolean closed;


        private DelegateSession(DelegateSessionBuilder builder, DelegateSyncLocalFactory factory,
                                LocalSession[] sessionArray) {
            super(builder.name, builder.readOnly, builder.visible, builder.allowQueryInsert);
            this.factory = factory;
            this.sessionArray = sessionArray;
        }

        @Override
        public boolean isReadOnlyStatus() {
            return false;
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public boolean hasTransaction() {
            return false;
        }

        @Override
        public <T> TableMeta<T> tableMeta(Class<T> domainClass) {
            return this.sessionArray[0].tableMeta(domainClass);
        }

        @Override
        protected String transactionName() {
            return this.sessionArray[0].currentTransaction().name();
        }

        @Override
        public LocalSessionFactory sessionFactory() {
            return this.factory;
        }

        @Override
        public LocalTransaction currentTransaction() throws SessionException {
            return this.sessionArray[0].currentTransaction();
        }

        @Override
        public TransactionBuilder builder() {
            return null;
        }

        @Override
        public void close() throws SessionException {
            if (this.closed) {
                return;
            }
            synchronized (this) {
                for (LocalSession session : sessionArray) {
                    session.close();
                }
                this.closed = true;
            }
        }

        @Override
        public <R> List<R> query(SimpleDqlStatement statement, Class<R> resultClass, Supplier<List<R>> listConstructor, Visible visible) {
            return null;
        }

        @Override
        public List<Map<String, Object>> queryAsMap(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor, Supplier<List<Map<String, Object>>> listConstructor, Visible visible) {
            return null;
        }

        @Override
        public <R> Stream<R> queryStream(SimpleDqlStatement statement, Class<R> resultClass, StreamOptions options, Visible visible) {
            return null;
        }

        @Override
        public Stream<Map<String, Object>> queryMapStream(SimpleDqlStatement statement, Supplier<Map<String, Object>> mapConstructor, StreamOptions options, Visible visible) {
            return null;
        }

        @Override
        public long update(SimpleDmlStatement statement, Visible visible) {
            return 0;
        }

        @Override
        public List<Long> batchUpdate(BatchDmlStatement statement, IntFunction<List<Long>> listConstructor, boolean useMultiStmt, Visible visible) {
            return null;
        }

        @Override
        public <R> List<R> batchQuery(BatchDqlStatement statement, Class<R> resultClass, R terminator, Supplier<List<R>> listConstructor, boolean useMultiStmt, Visible visible) {
            return null;
        }

        @Override
        public List<Map<String, Object>> batchQueryAsMap(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor, Map<String, Object> terminator, Supplier<List<Map<String, Object>>> listConstructor, boolean useMultiStmt, Visible visible) {
            return null;
        }

        @Override
        public <R> Stream<R> batchQueryStream(BatchDqlStatement statement, Class<R> resultClass, R terminator, StreamOptions options, boolean useMultiStmt, Visible visible) {
            return null;
        }

        @Override
        public Stream<Map<String, Object>> batchQueryMapStream(BatchDqlStatement statement, Supplier<Map<String, Object>> mapConstructor, Map<String, Object> terminator, StreamOptions options, boolean useMultiStmt, Visible visible) {
            return null;
        }

        @Override
        public MultiResult multiStmt(MultiResultStatement statement, StreamOptions options, Visible visible) {
            return null;
        }

        @Override
        public MultiStream multiStmtStream(MultiResultStatement statement, StreamOptions options, Visible visible) {
            return null;
        }


    }//DelegateSession


}
