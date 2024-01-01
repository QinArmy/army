# Army is a better blocking/reactive orm framework

[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.qinarmy/army.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/io/qinarmy/army/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.qinarmy/army/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.qinarmy/army)
[![Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java support](https://img.shields.io/badge/Java-8+-green?logo=java&logoColor=white)](https://openjdk.java.net/)

## Design Philosophy

1. Don't create new world,just mapping real world.
2. We need standard,we need dialect,it's real world.
3. We need blocking way,we need reactive way,it's real world.

### [Army document](https://army.qinarmy.io "Army document pages")

### How to start ?

#### Maven

```xml

<dependency>
    <groupId>io.qinarmy</groupId>
    <artifactId>army-jdbc</artifactId>
    <version>0.6.3</version><!--Army maven version-->
</dependency>

```

##### appropriate maven module that contain domain class

```xml

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessors>
                    io.army.modelgen.ArmyMetaModelDomainProcessor
                </annotationProcessors>
            </configuration>
        </plugin>
    </plugins>
</build>

```

#### Java code

```java
// mapping domain to table

@Table(name = "china_region", indexes = {
        @Index(name = "china_region_uni_name_region_type", fieldList = {"name", "regionType"}, unique = true),
        @Index(name = "china_region_inx_parent_id", fieldList = "parentId")},
        comment = "china region")
@Inheritance("regionType")
public class ChinaRegion<T extends ChinaRegion<T>> {


    @Generator(type = GeneratorType.POST) // id is generated by database
    @Column
    private Long id; // 'id' is army reserved field name,it representing primary key

    @Column(scale = 2)
    private LocalDateTime createTime;  // 'createTime' is army reserved field name,it representing table record create time

    // updateTime is optional field, if table is immutable
    @Column
    private LocalDateTime updateTime; // 'updateTime' is army reserved field name,it representing table record last update time

    // version is optional field
    @Column
    private Integer version; // 'version' is army reserved field name,it representing table record's optimistic lock

    // visible is optional field
    @Column
    private Boolean visible; // 'visible' is army reserved field name,it representing table record's logic delete

    @Column
    private RegionType regionType;

    @Column(precision = 20, nullable = false, comment = "china region name")
    private String name;

    @Column(precision = 16, scale = 2, defaultValue = "0.00", nullable = false, comment = "china region GDP")
    private BigDecimal regionGdp;


    @Column(nullable = false, defaultValue = "0", updateMode = UpdateMode.IMMUTABLE, comment = "china region parent level id")
    private Long parentId;

    @Column(defaultValue = "0", nullable = false, comment = "china region population")
    private Integer population;
}

// io.army.modelgen.ArmyMetaModelDomainProcessor generate  static metamodel class

@Generated(value = "io.army.modelgen.ArmyMetaModelDomainProcessor",
        date = "2024-01-02 07:13:54.605524+08:00",
        comments = "china region")
@SuppressWarnings("unchecked")
public abstract class ChinaRegion_ {

    private ChinaRegion_(){
        throw new UnsupportedOperationException();
    }

    public static final ParentTableMeta<ChinaRegion<?>> T;

    /** Due to ChinaRegion&lt;?> contains type parameter(s) , army generate static CLASS for army session query api. */
    public static final Class<ChinaRegion<?>> CLASS = (Class<ChinaRegion<?>>)((Class<?>)ChinaRegion.class);

    static {
        final ParentTableMeta<?> temp;
        temp = _TableMetaFactory.getParentTableMeta(ChinaRegion.class);
        T = (ParentTableMeta<ChinaRegion<?>>) temp;

        final int fieldSize = T.fieldList().size();
        if(fieldSize != 10){
            String m = String.format("Domain[%s] field count[%s] error,please check you whether create(delete) field or not,if yes then you must recompile.",
                    ChinaRegion.class.getName(),fieldSize);
            throw new IllegalStateException(m);
        }
    }

    /** Due to ChinaRegion&lt;?> contains type parameter(s) , army generate static constructor method for army session query api. */
    public static ChinaRegion<?> constructor(){
        return new ChinaRegion<>();
    }

    /*-------------------following table filed names-------------------*/

    /** {@link ChinaRegion#regionGdp } china region GDP */
    public static final String REGION_GDP = "regionGdp";

    /** {@link ChinaRegion#visible } visible for logic delete */
    public static final String VISIBLE = "visible";

    /** {@link ChinaRegion#regionType } @see io.army.example.bank.domain.user.RegionType */
    public static final String REGION_TYPE = "regionType";

    /** {@link ChinaRegion#createTime } create time */
    public static final String CREATE_TIME = "createTime";




    /** {@link ChinaRegion#name } china region name */
    public static final String NAME = "name";

    /** {@link ChinaRegion#updateTime } update time */
    public static final String UPDATE_TIME = "updateTime";

    /** {@link ChinaRegion#id } primary key */
    public static final String ID = "id";

    /** {@link ChinaRegion#version } version for optimistic lock */
    public static final String VERSION = "version";




    /** {@link ChinaRegion#parentId } china region parent level id */
    public static final String PARENT_ID = "parentId";

    /** {@link ChinaRegion#population } china region population */
    public static final String POPULATION = "population";



    /*-------------------following table filed metas-------------------*/

    /** {@link ChinaRegion#regionGdp } china region GDP */
    public static final FieldMeta<ChinaRegion<?>> regionGdp = T.getField(REGION_GDP);

    /** {@link ChinaRegion#visible } visible for logic delete */
    public static final FieldMeta<ChinaRegion<?>> visible = T.getField(VISIBLE);

    /** {@link ChinaRegion#regionType } @see io.army.example.bank.domain.user.RegionType */
    public static final FieldMeta<ChinaRegion<?>> regionType = T.getField(REGION_TYPE);

    /** {@link ChinaRegion#createTime } create time */
    public static final FieldMeta<ChinaRegion<?>> createTime = T.getField(CREATE_TIME);




    /** {@link ChinaRegion#name } china region name */
    public static final FieldMeta<ChinaRegion<?>> name = T.getField(NAME);

    /** {@link ChinaRegion#updateTime } update time */
    public static final FieldMeta<ChinaRegion<?>> updateTime = T.getField(UPDATE_TIME);

    /** {@link ChinaRegion#id } primary key */
    public static final PrimaryFieldMeta<ChinaRegion<?>> id = T.id();

    /** {@link ChinaRegion#version } version for optimistic lock */
    public static final FieldMeta<ChinaRegion<?>> version = T.getField(VERSION);




    /** {@link ChinaRegion#parentId } china region parent level id */
    public static final FieldMeta<ChinaRegion<?>> parentId = T.getField(PARENT_ID);

    /** {@link ChinaRegion#population } china region population */
    public static final FieldMeta<ChinaRegion<?>> population = T.getField(POPULATION);



} // ChinaRegion_


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
        final Map<String, Object> map = new HashMap<>();
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
            session.queryObject(stmt, RowMaps::hashMap)  // io.army.util.RowMaps
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
            session.queryObject(stmt, ChinaRegion_::constructor)
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
