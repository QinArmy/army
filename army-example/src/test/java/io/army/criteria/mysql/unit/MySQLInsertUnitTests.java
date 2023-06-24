package io.army.criteria.mysql.unit;

import io.army.annotation.GeneratorType;
import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class MySQLInsertUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLInsertUnitTests.class);

    @Test
    public void domainInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .ignoreReturnIds() // due to exists ON DUPLICATE KEY, so have to ignore return ids,because database couldn't return correct ids when conflict.
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .asInsert();

        printStmt(LOG, stmt);

    }

    @Test
    public void domainInsertParentPostWithConflict() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .ignoreReturnIds() // due to exists ON DUPLICATE KEY, so have to ignore return ids,because database couldn't return correct ids when conflict.
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, SQLs.scalarSubQuery()// here test qualified field({tableName}.name) feature
                        .parens(s -> s.select(HistoryChinaRegion_.name)
                                .from(HistoryChinaRegion_.T, SQLs.AS, "t")
                                .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                                .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                                .union()
                                .select(HistoryChinaRegion_.name)
                                .from(HistoryChinaRegion_.T, SQLs.AS, "t")
                                .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                                .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                                .limit(SQLs::literal, 1)
                                .asQuery()
                        )
                        .asQuery()

                )
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);

    }

    @Test(expectedExceptions = CriteriaException.class)
    public void domainInsertParentPostAndNoIgnoreReturnIdError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        try {
            MySQLs.singleInsert()
                    .literalMode(LiteralMode.PREFERENCE)
                    .insertInto(ChinaRegion_.T)
                    .partition("p1")
                    .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                            .comma(ChinaRegion_.parentId)
                    )
                    .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                    .values(this::createReginList)
                    .onDuplicateKey()       // here ,exists ON DUPLICATE KEY and insert multi row,so database couldn't return ids.
                    .update(ChinaRegion_.name, MySQLs::values)
                    .asInsert();
        } catch (CriteriaException e) {
            LOG.debug("{}", e.getMessage());
            throw e;
        }

    }

    @Test
    public void domainInsert80ParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .ignoreReturnIds() // due to exists ON DUPLICATE KEY, so have to ignore return ids,because database couldn't return correct ids when conflict.
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .as("cr")
                .onDuplicateKey() // TODO validate version = version + persist to database result
                .update(ChinaRegion_.name, SQLs.field("cr", ChinaRegion_.name))
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs.field("cr", ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({rowAlias}.name) feature
                        .parens(s -> s.select(HistoryChinaRegion_.name)
                                .from(HistoryChinaRegion_.T, SQLs.AS, "t")
                                .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                                .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                                .union()
                                .select(HistoryChinaRegion_.name)
                                .from(HistoryChinaRegion_.T, SQLs.AS, "t")
                                .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                                .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                                .limit(SQLs::literal, 1)
                                .asQuery()

                        )
                        .asQuery()

                )
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);

    }

    @Test
    public void domainInsertSingleRowChildPostWithConflict() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final ChinaProvince province;
        province = this.createRandomProvince();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .value(province)
                .asInsert()// parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                .value(province)
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, SQLs.scalarSubQuery()
                        .parens(s -> s.select(HistoryChinaProvince_.provincialCapital)
                                .from(HistoryChinaProvince_.T, SQLs.AS, "cp")
                                .join(HistoryChinaRegion_.T, SQLs.AS, "cr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                                .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                                .union()
                                .select(HistoryChinaRegion_.name)
                                .from(HistoryChinaRegion_.T, SQLs.AS, "t")
                                .where(HistoryChinaRegion_.name::equal, SQLs::literal, "曲境")
                                .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                                .limit(SQLs::literal, 1)
                                .asQuery()
                        )
                        .asQuery()
                )
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);
    }

    @Test
    public void domainInsertSingleRow80ChildPostWithConflict() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final ChinaProvince province;
        province = this.createRandomProvince();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .value(province)
                .asInsert()// parent table insert statement end

                .child()

                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                .value(province)
                .as("cp")
                .onDuplicateKey()  // here,due to insert single row,allow ON DUPLICATE KEY clause
                .update(ChinaProvince_.governor, SQLs.field("cp", ChinaProvince_.governor))
                .comma(ChinaProvince_.provincialCapital, SQLs.scalarSubQuery()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, SQLs.AS, "hcp")
                        .join(HistoryChinaRegion_.T, SQLs.AS, "hcr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, SQLs.AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs::literal, "曲境")
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                )
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);
    }

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void domainInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        try {
            MySQLs.singleInsert()
                    .ignoreReturnIds()
                    .literalMode(LiteralMode.PREFERENCE)
                    .insertInto(ChinaRegion_.T)
                    .partition("p1")
                    .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                            .comma(ChinaRegion_.parentId)
                    )
                    .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                    .values(provinceList)
                    .onDuplicateKey() // ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                    .update(ChinaRegion_.name, MySQLs::values)
                    .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                    .asInsert()// parent table insert statement end

                    .child()

                    .insertInto(ChinaProvince_.T)
                    .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                    .values(provinceList)
                    .onDuplicateKey()
                    .update(ChinaProvince_.governor, MySQLs::values)
                    .comma(ChinaProvince_.provincialCapital, MySQLs::values)
                    .asInsert();
        } catch (ErrorChildInsertException e) {
            LOG.debug("{}", e.getMessage());
            throw e;
        }

    }


    /*-------------------below static values insert mode -------------------*/


    @Test
    public void staticValuesInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                )
                .asInsert();

        printStmt(LOG, stmt);
    }


    @Test
    public void staticValuesInsertParentPostWithConflict() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .comma()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                )
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, SQLs.scalarSubQuery()// here test qualified field({tableName}.name) feature
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, SQLs.AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .asQuery()

                )
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);

    }


    @Test
    public void staticValuesInsert80ParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T).partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .asInsert();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void staticValuesInsert80ParentPostWithConflict() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T).partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .as("cr")
                .onDuplicateKey() // TODO validate version = version + persist to database result
                .update(ChinaRegion_.name, SQLs.field("cr", ChinaRegion_.name))
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs.field("cr", ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, SQLs.scalarSubQuery()// here test qualified field({rowAlias}.name) feature
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, SQLs.AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .asQuery()

                )
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);

    }

    @Test
    public void staticValuesInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T).partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomProvince(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomProvince(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .asInsert()// parent table insert statement end

                .child()

                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::literal, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::literal, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .asInsert();

        printStmt(LOG, stmt);
    }

    @Test
    public void staticValuesInsertChildPostWithConflict() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomProvince(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomProvince(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .asInsert()// parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                .values()

                .parens(s -> s.space(ChinaProvince_.governor, SQLs::literal, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::param, randomPerson(random))
                )
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, SQLs.scalarSubQuery()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, SQLs.AS, "cp")
                        .join(HistoryChinaRegion_.T, SQLs.AS, "cr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .asQuery()
                )
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);
    }

    @Test
    public void staticValuesInsert80ChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomProvince(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomProvince(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                )
                .asInsert()// parent table insert statement end

                .child()

                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::literal, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::param, randomPerson(random))
                )
                .asInsert();

        print80Stmt(LOG, stmt);
    }

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void staticValuesInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        MySQLs.singleInsert()
                .ignoreReturnIds()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .onDuplicateKey() // ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .asInsert()// parent table insert statement end

                .child()

                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::literal, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, MySQLs::values)
                .asInsert();


    }

    /*-------------------below dynamic values insert mode -------------------*/


    @Test
    public void dynamicValuesInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.population, ChinaRegion_.parentId))
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    for (int i = 0; i < 2; i++) {
                        c.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                        );
                    }

                })
                .asInsert();

        printStmt(LOG, stmt);

    }

    @Test
    public void dynamicValuesInsertParentPostWithConflict() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.population, ChinaRegion_.parentId))
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    for (int i = 0; i < 2; i++) {
                        c.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                        );
                    }

                })
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({tableName}.name) feature
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, SQLs.AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .asQuery()

                )
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);
    }

    @Test
    public void dynamicValuesInsert80ParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    for (int i = 0; i < 2; i++) {
                        c.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                        );
                    }

                })
                .asInsert();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void dynamicValuesInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final int rowCount = 2;
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                        );
                    }

                })
                .asInsert()// parent table insert statement end

                .child()

                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                                .comma(ChinaProvince_.provincialCapital, SQLs::param, randomPerson(random))
                        );
                    }

                })
                .asInsert();

        printStmt(LOG, stmt);
    }

    @Test
    public void dynamicValuesInsertChildPostWithConflict() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final int rowCount = 2;
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                        );
                    }

                })
                .asInsert()// parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                                .comma(ChinaProvince_.provincialCapital, SQLs::param, randomPerson(random))
                        );
                    }

                })
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, SQLs.scalarSubQuery()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, SQLs.AS, "cp")
                        .join(HistoryChinaRegion_.T, SQLs.AS, "cr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .asQuery()
                )
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);
    }

    @Test//(invocationCount = 300,threadPoolSize = 2)
    public void dynamicValuesInsert80ChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final int rowCount = 4;

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                        );
                    }

                })
                .asInsert()// parent table insert statement end

                .child()

                .insertInto(ChinaCity_.T)
                .parens(s -> s.space(ChinaCity_.mayorName))
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)));
                    }

                })
                .asInsert();

        print80Stmt(LOG, stmt);
    }

    @Test
    public void dynamicValuesInsert80ChildPostWithConflict() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final int rowCount = 4;

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                        );
                    }

                })
                .asInsert()// parent table insert statement end
                .child()
                .insertInto(ChinaCity_.T)
                .parens(s -> s.space(ChinaCity_.mayorName))
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)));
                    }

                })
                .as("cp")
                .onDuplicateKey()
                .update(ChinaCity_.mayorName, SQLs.field("cp", ChinaCity_.mayorName))
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);
    }

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void dynamicValuesInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final int rowCount = 2;
        MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                        );
                    }

                })
                .onDuplicateKey() // ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .asInsert() // parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.governor, ChinaProvince_.provincialCapital))
                .values(c -> {
                    for (int i = 0; i < rowCount; i++) {
                        c.parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                                .comma(ChinaProvince_.provincialCapital, SQLs::param, randomPerson(random))
                        );
                    }

                })
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, MySQLs::values)
                .asInsert();


    }


    /*-------------------below static assignment insert mode -------------------*/

    @Test
    public void assignmentInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .set(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);

    }

    @Test
    public void assignmentInsert80ParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList).into(ChinaRegion_.T)
                .partition("p1")
                .set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .set(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .as("cr")
                .onDuplicateKey()
                .update(ChinaRegion_.name, SQLs.field("cr", ChinaRegion_.name))
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs.field("cr", ChinaRegion_.regionGdp))
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);

    }

    @Test
    public void assignmentInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .set(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .asInsert() // parent table insert statement end

                .child()

                .insertInto(ChinaProvince_.T)
                .set(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                .set(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, MySQLs::values)
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);

    }

    @Test(expectedExceptions = CriteriaException.class)
    public void assignmentInsertChildPostVisibleError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;

        try {
            stmt = MySQLs.singleInsert()
                    .literalMode(LiteralMode.PREFERENCE)
                    .insertInto(ChinaRegion_.T)
                    .partition("p1")
                    .set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                    .set(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                    .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                    .asInsert() // parent table insert statement end

                    .child()

                    .insertInto(ChinaProvince_.T)
                    .set(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                    .set(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                    .onDuplicateKey()
                    .update(ChinaProvince_.governor, MySQLs::values)
                    .comma(ChinaProvince_.provincialCapital, MySQLs::values)
                    .asInsert();
        } catch (CriteriaException e) {
            Assert.fail(e.getMessage());
            throw e;
        }


        try {
            printStmt(LOG, stmt, Visible.ONLY_VISIBLE); //here table contain visible field and Visible mode is ONLY_VISIBLE not both,so error
        } catch (CriteriaException e) {
            //test success
            LOG.debug("{}", e.getMessage());
            throw e;
        }

    }

    @Test
    public void assignmentInsert80ChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList).into(ChinaRegion_.T)
                .partition("p1")
                .set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .set(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .asInsert() // parent table insert statement end

                .child()

                .insertInto(ChinaProvince_.T)
                .set(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                .set(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                .as("cp")
                .onDuplicateKey()
                .update(ChinaProvince_.governor, SQLs.field("cp", ChinaProvince_.governor))
                .comma(ChinaProvince_.provincialCapital, SQLs.field("cp", ChinaProvince_.provincialCapital))
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);

    }

    @Test
    public void assignmentInsertChildPostWithParentConflictNoError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        // due to assignment mode insert one row only,so no error.
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .set(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .onDuplicateKey()// ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                .update(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .asInsert() // parent table insert statement end

                .child()

                .insertInto(ChinaProvince_.T)
                .set(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                .set(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, MySQLs::values)
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);

    }

    /*-------------------below dynamic assignment insert mode -------------------*/


    @Test
    public void dynamicAssignmentInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .sets(s -> s.set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .set(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                        .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);

    }

    @Test
    public void dynamicAssignmentInsert80ParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList).into(ChinaRegion_.T)
                .partition("p1")
                .sets(s -> s.set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .set(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .as("cr")
                .onDuplicateKey()
                .update(ChinaRegion_.name, SQLs.field("cr", ChinaRegion_.name))
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs.field("cr", ChinaRegion_.regionGdp))
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);

    }

    @Test
    public void dynamicAssignmentInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .sets(s -> s.set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .set(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .asInsert() // parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .set(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                .set(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                .onDuplicateKeyUpdate(s -> s.set(ChinaProvince_.governor, MySQLs::values)
                        .set(ChinaProvince_.provincialCapital, MySQLs::values)
                )
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);

    }

    @Test
    public void dynamicAssignmentInsert80ChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList).into(ChinaRegion_.T)
                .partition("p1")
                .sets(s -> s.set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .set(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .asInsert() // parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .sets(s -> s.set(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .set(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .as("cp")
                .onDuplicateKey()
                .update(ChinaProvince_.governor, SQLs.field("cp", ChinaProvince_.governor))
                .comma(ChinaProvince_.provincialCapital, SQLs.field("cp", ChinaProvince_.provincialCapital))
                .asInsert();

        print80Stmt(LOG, stmt, Visible.BOTH);

    }

    @Test
    public void dynamicAssignmentInsertChildPostWitConflictNoError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        // here ,due to assignment insert mode always insert one row,so database always could return correct id.
        MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .sets(s -> s.set(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .set(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .set(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                )
                .onDuplicateKeyUpdate(s ->
                        s.set(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                )// ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                .asInsert() // parent table insert statement end
                .child()

                .insertInto(ChinaProvince_.T)
                .set(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                .set(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))

                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, MySQLs::values)
                .asInsert();


    }


    /*-------------------below query insert tests -------------------*/

    @Test
    public void queryInsertParent() {

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .migration()
                .insertInto(ChinaRegion_.T).partition("p1")
                .parens(s -> s.space(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                        .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.regionType)
                )
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(SQLs.literalValue(RegionType.NONE)::as, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, SQLs.AS, "c")
                .limit(SQLs::param, 10)
                .asQuery()

                .asInsert();

        printStmt(LOG, stmt);

    }

    @Test
    public void queryInsert80Parent() {
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .migration()
                .insert(hintSupplier, Collections.emptyList())
                .into(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                        .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.regionType)
                )
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(SQLs.literalValue(RegionType.NONE)::as, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, SQLs.AS, "c")
                .limit(SQLs::param, 10)
                .asQuery()
                .asInsert();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void queryInsertChild() {

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .migration()
                .insertInto(ChinaRegion_.T).partition("p1")
                .parens(s -> s.space(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                        .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.regionType)
                )

                .space()

                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(SQLs.literalValue(RegionType.PROVINCE)::as, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, SQLs.AS, "c")
                .limit(SQLs::param, 10)
                .asQuery()

                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T).partition("p3")
                .parens(s -> s.space(ChinaProvince_.id, ChinaProvince_.provincialCapital)
                        .comma(ChinaProvince_.governor)
                )

                .space()

                .select(ChinaProvince_.id, ChinaProvince_.provincialCapital, ChinaProvince_.governor)
                .from(ChinaProvince_.T, SQLs.AS, "c")
                .join(ChinaRegion_.T, SQLs.AS, "p").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                .limit(SQLs::param, 10)
                .asQuery()

                .asInsert();

        printStmt(LOG, stmt);

    }


}
