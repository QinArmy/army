## Design Philosophy

1. Don't create new world,just mapping real world.
2. We need standard,we need dialect,it's real world.
3. We need blocking way,we need reactive way,it's real world.

### How to start ?

#### Maven

```xml

<dependency>
    <groupId>io.qinarmy</groupId>
    <artifactId>army-jdbc</artifactId>
    <version>0.6.0-PREVIEW</version><!--Army maven version-->
</dependency>
```

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
    public void update() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Map<String, Object> map = _Collections.hashMap();
        map.put("firstId", (byte) 1);
        map.put("secondId", "3");

        final Update stmt;
        stmt = SQLs.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, "武侠江湖")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, addGdp)
                .where(ChinaRegion_.id::between, SQLs::literal, map.get("firstId"), AND, map.get("secondId"))
                .and(SQLs::bracket, ChinaRegion_.name.equal(SQLs::literal, "江湖"))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, addGdp, Expression::greaterEqual, BigDecimal.ZERO)
                .asUpdate();

        try (SyncLocalSession session = sessionFactory.localSession()) {
            session.update(stmt);
        }
    }


    @Test
    public void batchUpdate() {
        final BatchUpdate stmt;
        stmt = SQLs.batchSingleUpdate()
                .update(ChinaProvince_.T, AS, "p") // update only parent table field: ChinaRegion_.*
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.id::equal, SQLs::namedParam)
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, Expression::greaterEqual, BigDecimal.ZERO) // test method infer
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, BigDecimal.ZERO) // test method infer
                .ifAnd(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, BigDecimal.ZERO) // test method infer
                .and(ChinaRegion_.version::equal, SQLs::param, "0")
                .asUpdate()
                .namedParamList(this.createProvinceList());

        try (SyncLocalSession session = sessionFactory.localSession()) {
            session.batchUpdate(stmt);
        }

    }


    @Test
    public void queryFields() {
        final Select stmt;
        stmt = SQLs.query()
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
        stmt = SQLs.query()
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
        stmt = SQLs.query()
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
