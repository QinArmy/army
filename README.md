## Design Philosophy

1. Don't create new world,just mapping real world.
2. We need standard,we need dialect,it's real world.
3. We need blocking way,we need reactive way,it's real world.

### How to start ?

#### Java code

```java
import java.beans.Transient;

public class HowToStartTests {

    private static DatabaseSessionFactory sessionFactory;

    @BeforeClass
    public void createSessionFactory() {
        final Map<String, Object> envMap = new HashMap<>();
        envMap.put(ArmyKey.DATABASE.name, Database.MySQL);
        envMap.put(ArmyKey.DIALECT.name, MySQLDialect.MySQL80);

        final DataSource dataSource;
        dataSource = createDataSource();
        sessionFactory = SyncFactoryBuilder.builder()
                .name("army-test")
                .packagesToScan(Collections.singletonList("io.army.example.bank.domain"))
                .datasource(dataSource)
                .environment(StandardEnvironment.from(envMap))
                // .fieldGeneratorFactory(new SimpleFieldGeneratorFactory())
                .build();
    }

    private DataSource createDataSource() {
        // do create datasource
        throw new UnsupportedOperationException();
    }

    @Test
    public void save() {
        final ChinaRegion<?> region;
        region = new ChinaRegion<>();
        region.setName("五指礁");

        try (SyncLocalSession session = sessionFactory.localSession()) {
            session.save(region);
        }
    }

    @Test
    public void insert() {
        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();
        final Insert stmt;
        stmt = SQLs.singleInsert()  // standard criteria api
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE) // default value for each row
                .values(regionList)
                .asInsert(); // end statement

        try (SyncLocalSession session = sessionFactory.localSession()) {
            session.update(stmt);
        }
    }


    @Test
    public void queryFields() {
        final Select stmt;
        stmt = Postgres.query()
                .select(ChinaRegion_.id, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.name.equal(SQLs::param, "曲境")) // bind parameter, output '?'
                .and(ChinaRegion_.createTime::less, SQLs::literal, LocalDateTime.now().minusDays(1)) // bind literal ,output literal
                .limit(SQLs::literal, 1) // bind literal ,output literal
                .asQuery();

        try (SyncLocalSession session = sessionFactory.localSession()) {
            final Supplier<Map<String, Object>> constructor = HashMap::new;
            session.queryObject(stmt, constructor)
                    .forEach(map -> LOG.debug("{}", map));
        }
    }


    @Test
    public void querySimpleDomain() {
        final Select stmt;
        stmt = Postgres.query()
                .select("c", PERIOD, Captcha_.T)
                .from(Captcha_.T, AS, "c")
                .where(Captcha_.requestNo.equal(SQLs::param, "3423423435435")) // bind parameter, output '?'
                .and(Captcha_.createTime::less, SQLs::literal, LocalDateTime.now().minusDays(1)) // bind literal ,output literal
                .limit(SQLs::literal, 1) // bind literal ,output literal
                .asQuery();

        try (SyncLocalSession session = sessionFactory.localSession()) {
            session.query(stmt, Captcha.class)
                    .forEach(c -> LOG.debug("{}", c.getCaptcha()));
        }
    }

    @Test
    public void queryGenericsDomain() {
        final Select stmt;
        stmt = Postgres.query()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.name.equal(SQLs::param, "曲境")) // bind parameter, output '?'
                .and(ChinaRegion_.createTime::less, SQLs::literal, LocalDateTime.now().minusDays(1)) // bind literal ,output literal
                .limit(SQLs::literal, 1) // bind literal ,output literal
                .asQuery();

        try (SyncLocalSession session = sessionFactory.localSession()) {
            final Supplier<ChinaRegion<?>> supplier = ChinaRegion::new;
            session.queryObject(stmt, supplier)
                    .forEach(c -> LOG.debug("{}", c.getName()));
        }
    }


    @Test
    public void localTransaction() {
        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();
        final Insert stmt;
        stmt = SQLs.singleInsert() // standard criteria api
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE) // default value for each row
                .values(regionList)
                .asInsert(); // end statement

        try (SyncLocalSession session = sessionFactory.localSession()) {
            session.startTransaction();
            try {
                session.update(stmt);
                session.commit();
            } catch (Exception e) {
                session.rollback();
                throw e;
            }

        }
    }


}
```
