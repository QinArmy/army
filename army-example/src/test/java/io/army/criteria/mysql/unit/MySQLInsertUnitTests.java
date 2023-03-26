package io.army.criteria.mysql.unit;

import io.army.annotation.GeneratorType;
import io.army.criteria.ErrorChildInsertException;
import io.army.criteria.Expression;
import io.army.criteria.InsertStatement;
import io.army.criteria.LiteralMode;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.*;
import io.army.meta.FieldMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.AS;

public class MySQLInsertUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLInsertUnitTests.class);

    @Test
    public void domainInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({tableName}.name) feature
                        .parens(s -> s.select(HistoryChinaRegion_.name)
                                .from(HistoryChinaRegion_.T, AS, "t")
                                .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                                .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                                .union()
                                .select(HistoryChinaRegion_.name)
                                .from(HistoryChinaRegion_.T, AS, "t")
                                .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                                .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                                .limit(SQLs::literal, 1)
                                .asQuery()
                        )
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

        final InsertStatement stmt;
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
                        .parens(s -> s.select(HistoryChinaRegion_.name)
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

                        )
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
        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .asInsert()// parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
                        .parens(s -> s.select(HistoryChinaProvince_.provincialCapital)
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
                        )
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
        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .asInsert()// parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .as("cp")
                .onDuplicateKey()
                .update(ChinaProvince_.governor, SQLs.field("cp", ChinaProvince_.governor))
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
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
                )
                .asInsert();

        print80Stmt(LOG, stmt);
    }

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void domainInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .onDuplicateKey() // ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .asInsert()// parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, MySQLs::values)
                .asInsert();

    }


    /*-------------------below static values insert mode -------------------*/


    @Test
    public void staticValuesInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final InsertStatement stmt;
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
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({tableName}.name) feature
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 1)
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

        final InsertStatement stmt;
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
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .parens(s -> s.select(HistoryChinaRegion_.name)
                                .from(HistoryChinaRegion_.T, AS, "t")
                                .where(HistoryChinaRegion_.name::equal, SQLs.field("cr", ChinaRegion_.regionGdp)) // qualified field({rowAlias}.name) feature
                                .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                                .limit(SQLs::literal, 1)
                                .asQuery()
                        )
                        .asQuery()

                )
                .asInsert();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void staticValuesInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .leftParen(ChinaRegion_.name, SQLs::literal, randomProvince(random))
                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::param, randomProvince(random))
                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .asInsert()// parent table insert statement end
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
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, AS, "cp")
                        .join(HistoryChinaRegion_.T, AS, "cr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs::literal, randomPerson(random))
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 4)
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

        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()
                .leftParen(ChinaRegion_.name, SQLs::literal, randomProvince(random))
                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::param, randomProvince(random))
                .comma(ChinaRegion_.regionGdp, SQLs::param, randomDecimal(random))
                .comma(ChinaRegion_.parentId, SQLs::param, random.nextInt(Integer.MAX_VALUE))
                .rightParen()

                .asInsert()// parent table insert statement end

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
                        .select(HistoryChinaProvince_.provincialCapital)
                        .from(HistoryChinaProvince_.T, AS, "cp")
                        .join(HistoryChinaRegion_.T, AS, "cr").on(HistoryChinaProvince_.id::equal, HistoryChinaRegion_.id)
                        .where(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, SQLs::literal, randomPerson(random))
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 10)
                        .asQuery()
                )
                .asInsert();

        print80Stmt(LOG, stmt);
    }

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void staticValuesInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        MySQLs.singleInsert()
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
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .asInsert()// parent table insert statement end

                .child()

                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values().leftParen(ChinaProvince_.governor, SQLs::literal, randomPerson(random))
                .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                .rightParen()

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

        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.population, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> param = SQLs::param;
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> literal = SQLs::literal;
                    BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> function;
                    for (int i = 0; i < 2; i++) {
                        function = (i & 1) == 0 ? literal : param;
                        c.row()
                                .set(ChinaRegion_.name, function, randomRegion(random))
                                .set(ChinaRegion_.regionGdp, function, randomDecimal(random))
                                .set(ChinaRegion_.parentId, function, random.nextInt(Integer.MAX_VALUE));
                    }

                })
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({tableName}.name) feature
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.parentId::equal, SQLs::literal, 1)
                        .union()
                        .select(HistoryChinaRegion_.name)
                        .from(HistoryChinaRegion_.T, AS, "t")
                        .where(HistoryChinaRegion_.name::equal, MySQLs.values(ChinaRegion_.name)) // qualified field({tableName}.name) feature
                        .and(HistoryChinaRegion_.regionType::equal, SQLs::literal, RegionType.CITY)
                        .limit(SQLs::literal, 6)
                        .asQuery()

                )
                .asInsert();

        printStmt(LOG, stmt);

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

        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> param = SQLs::param;
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> literal = SQLs::literal;
                    BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> function;
                    for (int i = 0; i < 2; i++) {
                        function = (i & 1) == 0 ? literal : param;
                        c.row()
                                .set(ChinaRegion_.name, function, randomRegion(random))
                                .set(ChinaRegion_.regionGdp, function, randomDecimal(random))
                                .set(ChinaRegion_.parentId, function, random.nextInt(Integer.MAX_VALUE));
                    }

                })
                .as("cr")
                .onDuplicateKey() // TODO validate version = version + persist to database result
                .update(ChinaRegion_.name, SQLs.field("cr", ChinaRegion_.name))
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs.field("cr", ChinaRegion_.regionGdp))
                .comma(ChinaRegion_.name, () -> SQLs.scalarSubQuery()// here test qualified field({rowAlias}.name) feature
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

                )
                .asInsert();

        print80Stmt(LOG, stmt);

    }

    @Test
    public void dynamicValuesInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final int rowCount = 2;
        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> param = SQLs::param;
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> literal = SQLs::literal;
                    BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> function;
                    for (int i = 0; i < rowCount; i++) {
                        function = (i & 1) == 0 ? literal : param;
                        c.row()
                                .set(ChinaRegion_.name, function, randomProvince(random))
                                .set(ChinaRegion_.regionGdp, function, randomDecimal(random))
                                .set(ChinaRegion_.parentId, function, random.nextInt(Integer.MAX_VALUE));
                    }

                })
                .asInsert()// parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(c -> {
                    final BiFunction<FieldMeta<ChinaProvince>, Object, Expression> param = SQLs::param;
                    final BiFunction<FieldMeta<ChinaProvince>, Object, Expression> literal = SQLs::literal;
                    BiFunction<FieldMeta<ChinaProvince>, Object, Expression> function;
                    for (int i = 0; i < rowCount; i++) {
                        function = (i & 1) == 0 ? literal : param;
                        c.row()
                                .set(ChinaProvince_.governor, function, randomPerson(random))
                                .set(ChinaProvince_.provincialCapital, function, randomPerson(random));
                    }

                })
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLs::values)
                .comma(ChinaProvince_.provincialCapital, () -> SQLs.scalarSubQuery()
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
                )
                .asInsert();

        printStmt(LOG, stmt);
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

        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, modifierList)
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> param = SQLs::param;
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> literal = SQLs::literal;
                    BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> function;
                    for (int i = 0; i < rowCount; i++) {
                        function = (i & 1) == 0 ? literal : param;
                        c.row().set(ChinaRegion_.name, function, randomCity(random))
                                .set(ChinaRegion_.regionGdp, function, randomDecimal(random))
                                .set(ChinaRegion_.parentId, function, random.nextInt(Integer.MAX_VALUE));
                    }

                })
                .asInsert()// parent table insert statement end
                .child()
                .insertInto(ChinaCity_.T)
                .leftParen(ChinaCity_.mayorName).rightParen()
                .values(c -> {
                    final BiFunction<FieldMeta<ChinaCity>, Object, Expression> param = SQLs::param;
                    final BiFunction<FieldMeta<ChinaCity>, Object, Expression> literal = SQLs::literal;
                    BiFunction<FieldMeta<ChinaCity>, Object, Expression> function;
                    for (int i = 0; i < rowCount; i++) {
                        function = (i & 1) == 0 ? literal : param;
                        c.row().set(ChinaCity_.mayorName, function, randomPerson(random));
                    }

                })
                .as("cp")
                .onDuplicateKey()
                .update(ChinaCity_.mayorName, SQLs.field("cp", ChinaCity_.mayorName))
                .asInsert();

        print80Stmt(LOG, stmt);
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
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(c -> {
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> param = SQLs::param;
                    final BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> literal = SQLs::literal;
                    BiFunction<FieldMeta<ChinaRegion<?>>, Object, Expression> function;
                    for (int i = 0; i < rowCount; i++) {
                        function = (i & 1) == 0 ? literal : param;
                        c.row().set(ChinaRegion_.name, function, randomProvince(random))
                                .set(ChinaRegion_.regionGdp, function, randomDecimal(random))
                                .set(ChinaRegion_.parentId, function, random.nextInt(Integer.MAX_VALUE));
                    }

                })
                .onDuplicateKey() // ChinaRegion_.id.generatorType() == GeneratorType.POST, so forbid onDuplicateKey clause,must throw CriteriaException
                .update(ChinaRegion_.name, MySQLs::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLs.values(ChinaRegion_.regionGdp))
                .asInsert() // parent table insert statement end
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(c -> {
                    final BiFunction<FieldMeta<ChinaProvince>, Object, Expression> param = SQLs::param;
                    final BiFunction<FieldMeta<ChinaProvince>, Object, Expression> literal = SQLs::literal;
                    BiFunction<FieldMeta<ChinaProvince>, Object, Expression> function;
                    for (int i = 0; i < rowCount; i++) {
                        function = (i & 1) == 0 ? literal : param;
                        c.row().set(ChinaProvince_.governor, function, randomPerson(random))
                                .set(ChinaProvince_.provincialCapital, function, randomPerson(random));
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
        final InsertStatement stmt;
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

        printStmt(LOG, stmt);

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

        InsertStatement stmt;
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

        print80Stmt(LOG, stmt);

    }

    @Test
    public void assignmentInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final InsertStatement stmt;
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

        printStmt(LOG, stmt);

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

        final InsertStatement stmt;
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

        print80Stmt(LOG, stmt);

    }

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void assignmentInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

        MySQLs.singleInsert()
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


    }

    /*-------------------below dynamic assignment insert mode -------------------*/


    @Test
    public void dynamicAssignmentInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final InsertStatement stmt;
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

        printStmt(LOG, stmt);

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

        InsertStatement stmt;
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

        print80Stmt(LOG, stmt);

    }

    @Test
    public void dynamicAssignmentInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final InsertStatement stmt;
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

        printStmt(LOG, stmt);

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

        final InsertStatement stmt;
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

        print80Stmt(LOG, stmt);

    }

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void dynamicAssignmentInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();

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

    @Test(enabled = false)//TODO
    public void queryInsertParent() {

        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .migration(true)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(ChinaRegion_.regionType)
                .rightParen()
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(SQLs.literalFrom(RegionType.NONE)::as, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, AS, "c")
                .limit(SQLs::param, 10)
                .asQuery()
                .asInsert();

        printStmt(LOG, stmt);

    }

    @Test(enabled = false) //TODO
    public void queryInsert80Parent() {
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        final InsertStatement stmt;
        stmt = MySQLs.singleInsert()
                .migration(true)
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
                .comma(SQLs.literalFrom(RegionType.NONE)::as, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, AS, "c")
                .limit(SQLs::param, 10)
                .asQuery()
                .asInsert();

        print80Stmt(LOG, stmt);

    }


}
