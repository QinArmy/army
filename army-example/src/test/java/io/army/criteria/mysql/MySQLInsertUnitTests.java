package io.army.criteria.mysql;

import io.army.annotation.GeneratorType;
import io.army.criteria.CriteriaException;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.Visible;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLFunctions;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.user.*;
import io.army.example.pill.domain.PillPerson;
import io.army.example.pill.domain.PillUser;
import io.army.example.pill.struct.IdentityType;
import io.army.example.pill.struct.PillUserType;
import io.army.stmt.GeneratedKeyStmt;
import io.army.stmt.PairStmt;
import io.army.stmt.Stmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.AS;

public class MySQLInsertUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLInsertUnitTests.class);

    @Test
    public void domainInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLFunctions::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLFunctions.values(ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({tableName}.name) feature
                        .leftParen()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLFunctions.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLFunctions.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()

                )
                .asInsert();

        printStmt(LOG, stmt);

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
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .as("cr")
                .onDuplicateKey() // TODO validate version = version + persist to database result
                .update(ChinaRegion_.name, SQLs.field("cr", ChinaRegion_.name))
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs.field("cr", ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({rowAlias}.name) feature
                        .leftParen()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()

                )
                .asInsert();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void domainInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .asInsert()
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLFunctions::values)
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
                        .leftParen()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, AS, "cp")
                        .join(HistoryChinaRegion_.T, AS, "cr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs::literal, "曲境")
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()
                )
                .asInsert();

        printStmt(LOG, stmt);
    }

    @Test
    public void domainInsert80ChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .asInsert()
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .as("cp")
                .onDuplicateKey()
                .update(ChinaProvince_.governor, SQLs.field("cp", ChinaProvince_.governor))
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
                        .leftParen()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, AS, "hcp")
                        .join(HistoryChinaRegion_.T, AS, "hcr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs::literal, "曲境")
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()
                )
                .asInsert();

        print80Stmt(LOG, stmt);
    }

    @Test(expectedExceptions = CriteriaException.class)
    public void domainInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .onDuplicateKey() // ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                .update(ChinaRegion_.name, MySQLFunctions::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLFunctions.values(ChinaRegion_.regionGdp))
                .asInsert()
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLFunctions::values)
                .comma(ChinaProvince_.provincialCapital, MySQLFunctions::values)
                .asInsert();

        printStmt(LOG, stmt);
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
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .leftParen(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLFunctions::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLFunctions.values(ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({tableName}.name) feature
                        .leftParen()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLFunctions.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLFunctions.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()

                )
                .asInsert();

        printStmt(LOG, stmt);

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
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .leftParen(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                .rightParen()
                .as("cr")
                .onDuplicateKey() // TODO validate version = version + persist to database result
                .update(ChinaRegion_.name, SQLs.field("cr", ChinaRegion_.name))
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs.field("cr", ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({rowAlias}.name) feature
                        .leftParen()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()

                )
                .asInsert();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void staticValuesInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .leftParen(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .asInsert()
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values()

                .leftParen(ChinaProvince_.governor, SQLs::literal, randomPerson(random))
                .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                .rightParen()

                .leftParen(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                .comma(ChinaProvince_.provincialCapital, SQLs::param, randomPerson(random))
                .rightParen()

                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLFunctions::values)
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
                        .leftParen()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, AS, "cp")
                        .join(HistoryChinaRegion_.T, AS, "cr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs::literal, randomPerson(random))
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()
                )
                .asInsert();

        printStmt(LOG, stmt);
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
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .leftParen(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::param, randomRegion(random))
                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values()

                .leftParen(ChinaProvince_.governor, SQLs::literal, randomPerson(random))
                .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                .rightParen()

                .leftParen(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                .comma(ChinaProvince_.provincialCapital, SQLs::param, randomPerson(random))
                .rightParen()

                .as("cp")
                .onDuplicateKey()
                .update(ChinaProvince_.governor, SQLs.field("cp", ChinaProvince_.governor))
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
                        .leftParen()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, AS, "cp")
                        .join(HistoryChinaRegion_.T, AS, "cr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs::literal, randomPerson(random))
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()
                )
                .asInsert();

        print80Stmt(LOG, stmt);
    }

    @Test(expectedExceptions = CriteriaException.class)
    public void staticValuesInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values().leftParen(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .onDuplicateKey() // ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                .update(ChinaRegion_.name, MySQLFunctions::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLFunctions.values(ChinaRegion_.regionGdp))
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values().leftParen(ChinaProvince_.governor, SQLs::literal, randomPerson(random))
                .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                .rightParen()

                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLFunctions::values)
                .comma(ChinaProvince_.provincialCapital, MySQLFunctions::values)
                .asInsert();

        printStmt(LOG, stmt);
    }

    /*-------------------below dynamic values insert mode -------------------*/


    @Test
    public void dynamicValuesInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLFunctions::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLFunctions.values(ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({tableName}.name) feature
                        .leftParen()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLFunctions.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLFunctions.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()

                )
                .asInsert();

        printStmt(LOG, stmt);

    }

    @Test
    public void dynamicValuesInsert80ParentPost() {
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
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .as("cr")
                .onDuplicateKey() // TODO validate version = version + persist to database result
                .update(ChinaRegion_.name, SQLs.field("cr", ChinaRegion_.name))
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs.field("cr", ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({rowAlias}.name) feature
                        .leftParen()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()

                )
                .asInsert();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void dynamicValuesInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .asInsert()
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLFunctions::values)
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
                        .leftParen()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, AS, "cp")
                        .join(HistoryChinaRegion_.T, AS, "cr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs::literal, "曲境")
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()
                )
                .asInsert();

        printStmt(LOG, stmt);
    }

    @Test
    public void dynamicValuesInsert80ChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };
        final List<MySQLs.Modifier> modifierList = Arrays.asList(MySQLs.HIGH_PRIORITY, MySQLs.IGNORE);

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .asInsert()
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .as("cp")
                .onDuplicateKey()
                .update(ChinaProvince_.governor, SQLs.field("cp", ChinaProvince_.governor))
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
                        .leftParen()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, AS, "hcp")
                        .join(HistoryChinaRegion_.T, AS, "hcr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs::literal, "曲境")
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
                        .asQuery()
                        .rightParen()
                        .asQuery()
                )
                .asInsert();

        print80Stmt(LOG, stmt);
    }

    @Test(expectedExceptions = CriteriaException.class)
    public void dynamicValuesInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .onDuplicateKey() // ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                .update(ChinaRegion_.name, MySQLFunctions::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLFunctions.values(ChinaRegion_.regionGdp))
                .asInsert()
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLFunctions::values)
                .comma(ChinaProvince_.provincialCapital, MySQLFunctions::values)
                .asInsert();

        printStmt(LOG, stmt);
    }


    /*-------------------below ASSIGNMENT insert mode -------------------*/

    @Test
    public void assignmentInsertParentPost() {
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, Collections.singletonList(MySQLs.HIGH_PRIORITY))
                .into(ChinaRegion_.T)
                .partition("p1")
                .set(ChinaRegion_.name, SQLs::param, "光明顶")
                .set(ChinaRegion_.regionGdp, SQLs::param, "6666.88")
                .set(ChinaRegion_.parentId, SQLs::literal, 0)
                .onDuplicateKey()
                .update(ChinaRegion_.name, SQLs::param, "光明顶")
                .comma(ChinaRegion_.regionGdp, SQLs::param, "6666.88")
                .asInsert();

        printStmt(stmt);

    }



    /*-------------------below query insert tests -------------------*/

    @Test
    public void queryInsertParent() {
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .insert(hintSupplier, Collections.emptyList())
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(ChinaRegion_.regionType)
                .rightParen()
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(SQLs.literalFrom(RegionType.NONE), AS, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, AS, "c")
                .limit(SQLs::param, 10)
                .asQuery()
                .asInsert();

        printStmt(stmt);

    }


    final List<PillPerson> createPsersonList() {
        final List<PillPerson> list = new ArrayList<>();
        PillPerson u;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < rowSize; i++) {
            u = new PillPerson();

            u.setIdentityId(i + 1L);
            u.setCreateTime(now);
            u.setUpdateTime(now);
            u.setUserType(PillUserType.NONE);

            u.setIdentityType(IdentityType.PERSON);
            u.setNickName("脉兽" + 1);
            u.setBirthday(LocalDate.now());

            list.add(u);

        }
        return list;
    }


    private List<PillUser> createUserList() {
        final List<PillUser> list = new ArrayList<>();
        PillUser u;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < rowSize; i++) {
            u = new PillUser();

            u.setIdentityId(i + 1L);
            u.setCreateTime(now);
            u.setUpdateTime(now);
            u.setUserType(PillUserType.NONE);

            u.setIdentityType(IdentityType.PERSON);
            u.setNickName("脉兽" + 1);

            list.add(u);

        }
        return list;
    }


    private void printStmt(final Insert insert) {
        String sql;
        DialectParser parser;
        Stmt stmt;
        _Insert parentStmt;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            parser = _MockDialects.from(dialect);

            stmt = parser.insert(insert, Visible.ONLY_VISIBLE);
            sql = parser.printStmt(stmt, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);


            if (insert instanceof _Insert._ChildInsert && !(insert instanceof _Insert._QueryInsert)) {
                assert stmt instanceof PairStmt;
                parentStmt = ((_Insert._ChildInsert) insert).parentStmt();
                assert parentStmt.table().id().generatorType() != GeneratorType.POST
                        || ((PairStmt) stmt).firstStmt() instanceof GeneratedKeyStmt;
            }

        }

    }


}
