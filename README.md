## Design Philosophy

1. Don't create new world,just mapping real world.
2. We need standard,we need dialect,it's real world.
3. We need blocking way,we need reactive way,it's real world.

### How to start ?

#### Java code

```java
public class HowToStartTests {

    private static DatabaseSessionFactory sessionFactory;

    @BeforeClass
    public void createSessionFactory() {
        final Map<String, Object> envMap = new HashMap<>();
        envMap.put(ArmyKey.DATABASE.name, Database.MySQL);
        envMap.put(ArmyKey.DIALECT.name, MySQLDialect.MySQL80);

        final DataSource dataSource;
        dataSource = createDataSource();
        return SyncFactoryBuilder.builder()
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
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .asInsert();

        try (SyncLocalSession session = sessionFactory.localSession()) {
            session.update(stmt);
        }
    }


    @Test
    public void localTransaction() {
        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();
        final Insert stmt;
        stmt = SQLs.singleInsert() // standard criteria api
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T).as("c")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .asInsert();

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
