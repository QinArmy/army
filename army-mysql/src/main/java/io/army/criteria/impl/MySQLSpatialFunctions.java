/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;


import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SimpleExpression;
import io.army.mapping.*;

import java.util.List;

/**
 * <p>
 * Package class,this class provider MySQL spatial function.
 *
 * @since 0.6.0
 */
@SuppressWarnings("unused")
abstract class MySQLSpatialFunctions extends MySQLWindowFunctions {

    MySQLSpatialFunctions() {
    }



    /*-------------------below MySQL-Specific Functions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     * @param geometryList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_geometrycollection">GeometryCollection(g [, g] ...)</a>
     */
    public static SimpleExpression geometryCollection(final List<Expression> geometryList) {
        return FunctionUtils.multiArgFunc("GeometryCollection", geometryList, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param first non-null
     * @param rest  non-null,empty or non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_geometrycollection">GeometryCollection(g [, g] ...)</a>
     */
    public static SimpleExpression geometryCollection(final Expression first, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("GeometryCollection", VarBinaryType.INSTANCE, first, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ptList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_linestring">LineString(pt [, pt] ...)</a>
     */
    public static SimpleExpression lineString(final List<Expression> ptList) {
        return FunctionUtils.multiArgFunc("LineString", ptList, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param first non-null
     * @param rest  non-null,empty or non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_linestring">LineString(pt [, pt] ...)</a>
     */
    public static SimpleExpression lineString(final Expression first, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("LineString", VarBinaryType.INSTANCE, first, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ptList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_multilinestring">MultiLineString(ls [, ls] ...)</a>
     */
    public static SimpleExpression multiLineString(final List<Expression> ptList) {
        return FunctionUtils.multiArgFunc("MultiLineString", ptList, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param first non-null
     * @param rest  non-null,empty or non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_multilinestring">MultiLineString(ls [, ls] ...)</a>
     */
    public static SimpleExpression multiLineString(final Expression first, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("MultiLineString", VarBinaryType.INSTANCE, first, rest);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ptList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_multipoint">MultiPoint(pt [, pt2] ...)</a>
     */
    public static SimpleExpression multiPoint(final List<Expression> ptList) {
        return FunctionUtils.multiArgFunc("MultiPoint", ptList, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param first non-null
     * @param rest  non-null,empty or non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_multipoint">MultiPoint(pt [, pt2] ...)</a>
     */
    public static SimpleExpression multiPoint(final Expression first, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("MultiPoint", VarBinaryType.INSTANCE, first, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ptList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_multipolygon">MultiPolygon(poly [, poly] ...)</a>
     */
    public static SimpleExpression multiPolygon(final List<Expression> ptList) {
        return FunctionUtils.multiArgFunc("MultiPolygon", ptList, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param first non-null
     * @param rest  non-null,empty or non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_multipolygon">MultiPolygon(poly [, poly] ...)</a>
     */
    public static SimpleExpression multiPolygon(final Expression first, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("MultiPolygon", VarBinaryType.INSTANCE, first, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param lsList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_polygon">Polygon(ls [, ls] ...)</a>
     */
    public static SimpleExpression polygon(final List<Expression> lsList) {
        return FunctionUtils.multiArgFunc("Polygon", lsList, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param first non-null
     * @param rest  non-null,empty or non-empty
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_polygon">Polygon(ls [, ls] ...)</a>
     */
    public static SimpleExpression polygon(final Expression first, Expression... rest) {
        return FunctionUtils.oneAndRestFunc("Polygon", VarBinaryType.INSTANCE, first, rest);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param x non-null
     * @param y non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_point">Point(x, y)</a>
     */
    public static SimpleExpression point(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("Point", x, y, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrcontains">MBRContains(g1, g2)</a>
     */
    public static IPredicate mbrContains(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRContains", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrcoveredby">MBRCoveredBy(g1, g2)</a>
     */
    public static IPredicate mbrCoveredBy(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRCoveredBy", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrcovers">MBRCovers(g1, g2)</a>
     */
    public static IPredicate mbrCovers(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRCovers", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrdisjoint">MBRDisjoint(g1, g2)</a>
     */
    public static IPredicate mbrDisjoint(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRDisjoint", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrequals">MBREquals(g1, g2)</a>
     */
    public static IPredicate mbrEquals(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBREquals", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrintersects">MBRIntersects(g1, g2)</a>
     */
    public static IPredicate mbrIntersects(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRIntersects", g1, g2);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbroverlaps">MBROverlaps(g1, g2)</a>
     */
    public static IPredicate mbrOverlaps(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBROverlaps", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrtouches">MBRTouches(g1, g2)</a>
     */
    public static IPredicate mbrTouches(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRTouches", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrwithin">MBRWithin(g1, g2)</a>
     */
    public static IPredicate mbrWithin(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRWithin", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param polyOrmpoly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-area">ST_Area({poly|mpoly})</a>
     */
    public static SimpleExpression stArea(final Expression polyOrmpoly) {
        return FunctionUtils.oneArgFunc("ST_Area", polyOrmpoly, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param polyOrmpoly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-centroid">ST_Centroid({poly|mpoly})</a>
     */
    public static SimpleExpression stCentroid(final Expression polyOrmpoly) {
        return FunctionUtils.oneArgFunc("ST_Centroid", polyOrmpoly, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param poly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-exteriorring">ST_ExteriorRing(poly)</a>
     */
    public static SimpleExpression stExteriorRing(final Expression poly) {
        return FunctionUtils.oneArgFunc("ST_ExteriorRing", poly, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param poly non-null
     * @param n    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-interiorringn">ST_InteriorRingN(poly, N)</a>
     */
    public static SimpleExpression stInteriorRingN(final Expression poly, final Expression n) {
        return FunctionUtils.twoArgFunc("ST_InteriorRingN", poly, n, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     *
     *
     * @param poly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-numinteriorrings">ST_NumInteriorRing(poly)</a>
     */
    public static SimpleExpression stNumInteriorRing(final Expression poly) {
        return FunctionUtils.oneArgFunc("ST_NumInteriorRing", poly, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     *
     *
     * @param poly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-numinteriorrings">ST_NumInteriorRings(poly)</a>
     */
    public static SimpleExpression stNumInteriorRings(final Expression poly) {
        return FunctionUtils.oneArgFunc("ST_NumInteriorRings", poly, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-asbinary">ST_AsBinary(g [, options])</a>
     */
    public static SimpleExpression stAsBinary(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_AsBinary", g, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g       non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-asbinary">ST_AsBinary(g [, options])</a>
     */
    public static SimpleExpression stAsBinary(final Expression g, final Expression options) {
        return _simpleTowArgFunc("ST_AsBinary", g, options, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-asbinary">ST_AsWKB(g [, options])</a>
     */
    public static SimpleExpression stAsWKB(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_AsWKB", g, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g       non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-asbinary">ST_AsWKB(g [, options])</a>
     */
    public static SimpleExpression stAsWKB(final Expression g, final Expression options) {
        return FunctionUtils.twoArgFunc("ST_AsWKB", g, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-astext">ST_AsText(g [, options])</a>
     */
    public static SimpleExpression stAsText(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_AsText", g, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param g       non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-astext">ST_AsText(g [, options])</a>
     */
    public static SimpleExpression stAsText(final Expression g, final Expression options) {
        return FunctionUtils.twoArgFunc("ST_AsText", g, options, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-astext">ST_AsWKT(g [, options])</a>
     */
    public static SimpleExpression stAsWKT(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_AsWKT", g, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param g       non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-astext">ST_AsWKT(g [, options])</a>
     */
    public static SimpleExpression stAsWKT(final Expression g, final Expression options) {
        return FunctionUtils.twoArgFunc("ST_AsWKT", g, options, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-swapxy">ST_SwapXY(g)</a>
     */
    public static SimpleExpression stSwapXY(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_SwapXY", g, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-asgeojson">ST_AsGeoJSON(g [, max_dec_digits [, options]])</a>
     */
    public static SimpleExpression stAsGeoJson(Expression g) {
        return FunctionUtils.oneArgFunc("ST_AsGeoJSON", g, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param g            non-null
     * @param maxDecDigits non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-asgeojson">ST_AsGeoJSON(g [, max_dec_digits [, options]])</a>
     */
    public static SimpleExpression stAsGeoJson(Expression g, Expression maxDecDigits) {
        return FunctionUtils.twoArgFunc("ST_AsGeoJSON", g, maxDecDigits, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param g            non-null
     * @param maxDecDigits non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-asgeojson">ST_AsGeoJSON(g [, max_dec_digits [, options]])</a>
     */
    public static SimpleExpression stAsGeoJson(Expression g, Expression maxDecDigits, Expression options) {
        return FunctionUtils.threeArgFunc("ST_AsGeoJSON", g, maxDecDigits, options, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-geomfromgeojson">ST_GeomFromGeoJSON(str [, options [, srid]])</a>
     */
    public static SimpleExpression stGeomFromGeoJson(Expression str) {
        return FunctionUtils.oneArgFunc("ST_GeomFromGeoJSON", str, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param str     non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-geomfromgeojson">ST_GeomFromGeoJSON(str [, options [, srid]])</a>
     */
    public static SimpleExpression stGeomFromGeoJson(Expression str, Expression options) {
        return FunctionUtils.twoArgFunc("ST_GeomFromGeoJSON", str, options, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param str     non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-geomfromgeojson">ST_GeomFromGeoJSON(str [, options [, srid]])</a>
     */
    public static SimpleExpression stGeomFromGeoJson(Expression str, Expression options, Expression srid) {
        return FunctionUtils.threeArgFunc("ST_GeomFromGeoJSON", str, options, srid, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @param d non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-buffer">ST_Buffer(g, d [, strategy1 [, strategy2 [, strategy3]]])</a>
     */
    public static SimpleExpression stBuffer(Expression g, Expression d) {
        return FunctionUtils.twoArgFunc("ST_Buffer", g, d, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @param d non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-buffer">ST_Buffer(g, d [, strategy1 [, strategy2 [, strategy3]]])</a>
     */
    public static SimpleExpression stBuffer(Expression g, Expression d, Expression strategy1) {
        return FunctionUtils.threeArgFunc("ST_Buffer", g, d, strategy1, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @param d non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-buffer">ST_Buffer(g, d [, strategy1 [, strategy2 [, strategy3]]])</a>
     */
    public static SimpleExpression stBuffer(Expression g, Expression d, Expression strategy1, Expression strategy2) {
        return FunctionUtils.multiArgFunc("ST_Buffer", VarBinaryType.INSTANCE, g, d, strategy1, strategy2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @param d non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-buffer">ST_Buffer(g, d [, strategy1 [, strategy2 [, strategy3]]])</a>
     */
    public static SimpleExpression stBuffer(Expression g, Expression d
            , Expression strategy1, Expression strategy2
            , Expression strategy3) {
        return FunctionUtils.multiArgFunc("ST_Buffer", VarBinaryType.INSTANCE
                , g, d, strategy1, strategy2, strategy3);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType}
     *
     *
     * @param expList non-null ,the list that size in [1,2].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-buffer-strategy">ST_Buffer_Strategy(strategy [, points_per_circle])</a>
     */
    public static SimpleExpression stBufferStrategy(final List<Expression> expList) {
        final String name = "ST_Buffer_Strategy";
        final SimpleExpression func;
        switch (expList.size()) {
            case 1:
                func = FunctionUtils.oneArgFunc(name, expList.get(0), VarBinaryType.INSTANCE);
                break;
            case 2:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList)
                        , VarBinaryType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType}
     *
     *
     * @param strategy non-null ,the list that size in [1,2].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-buffer-strategy">ST_Buffer_Strategy(strategy [, points_per_circle])</a>
     */
    public static SimpleExpression stBufferStrategy(Expression strategy) {
        return FunctionUtils.oneArgFunc("ST_Buffer_Strategy", strategy, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType}
     *
     *
     * @param strategy non-null ,the list that size in [1,2].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-buffer-strategy">ST_Buffer_Strategy(strategy [, points_per_circle])</a>
     */
    public static SimpleExpression stBufferStrategy(Expression strategy, Expression pointsPerCircle) {
        return FunctionUtils.twoArgFunc("ST_Buffer_Strategy", strategy, pointsPerCircle, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-convexhull">ST_ConvexHull(g)</a>
     */
    public static SimpleExpression stConvexHull(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_ConvexHull", g, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-difference">ST_Difference(g1, g2)</a>
     */
    public static SimpleExpression stDifference(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_Difference", g1, g2, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-intersection">ST_Intersection(g1, g2)</a>
     */
    public static SimpleExpression stIntersection(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_Intersection", g1, g2, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ls                 non-null
     * @param fractionalDistance non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-lineinterpolatepoint">ST_LineInterpolatePoint(ls, fractional_distance)</a>
     */
    public static SimpleExpression stLineInterpolatePoint(final Expression ls, final Expression fractionalDistance) {
        return FunctionUtils.twoArgFunc("ST_LineInterpolatePoint", ls, fractionalDistance, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ls                 non-null
     * @param fractionalDistance non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-lineinterpolatepoints">ST_LineInterpolatePoints(ls, fractional_distance)</a>
     */
    public static SimpleExpression stLineInterpolatePoints(final Expression ls, final Expression fractionalDistance) {
        return FunctionUtils.twoArgFunc("ST_LineInterpolatePoints", ls, fractionalDistance, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ls       non-null
     * @param distance non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-pointatdistance">ST_PointAtDistance(ls, distance)</a>
     */
    public static SimpleExpression stPointAtDistance(final Expression ls, final Expression distance) {
        return FunctionUtils.twoArgFunc("ST_PointAtDistance", ls, distance, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-symdifference">ST_SymDifference(g1, g2)</a>
     */
    public static SimpleExpression stSymDifference(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_SymDifference", g1, g2, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g          non-null
     * @param targetSrid non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-transform">ST_Transform(g, target_srid)</a>
     */
    public static SimpleExpression stTransform(final Expression g, final Expression targetSrid) {
        return FunctionUtils.twoArgFunc("ST_Transform", g, targetSrid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-union">ST_Union(g1, g2)</a>
     */
    public static SimpleExpression stUnion(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_Union", g1, g2, VarBinaryType.INSTANCE);
    }

    /*-------------------below Spatial Convenience Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param expList non-null,size in [2,3].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-distance-sphere">ST_Distance_Sphere(g1, g2 [, radius])</a>
     */
    public static SimpleExpression stDistanceSphere(final List<Expression> expList) {
        final String name = "ST_Distance_Sphere";
        final SimpleExpression func;
        switch (expList.size()) {
            case 2:
            case 3:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList)
                        , DoubleType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-distance-sphere">ST_Distance_Sphere(g1, g2 [, radius])</a>
     */
    public static SimpleExpression stDistanceSphere(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_Distance_Sphere", g1, g2, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-distance-sphere">ST_Distance_Sphere(g1, g2 [, radius])</a>
     */
    public static SimpleExpression stDistanceSphere(final Expression g1, final Expression g2, Expression radius) {
        return FunctionUtils.threeArgFunc("ST_Distance_Sphere", g1, g2, radius, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-isvalid">ST_IsValid(g)</a>
     */
    public static IPredicate stIsValid(final Expression g) {
        return FunctionUtils.oneArgPredicateFunc("ST_IsValid", g);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param pt1 non-null
     * @param pt2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-makeenvelope">ST_MakeEnvelope(pt1, pt2)</a>
     */
    public static SimpleExpression stMakeEnvelope(final Expression pt1, final Expression pt2) {
        return FunctionUtils.twoArgFunc("ST_MakeEnvelope", pt1, pt2, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g           non-null
     * @param maxDistance non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-simplify">ST_Simplify(g, max_distance)</a>
     */
    public static SimpleExpression stSimplify(final Expression g, final Expression maxDistance) {
        return FunctionUtils.twoArgFunc("ST_Simplify", g, maxDistance, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-validate">ST_Validate(g)</a>
     */
    public static SimpleExpression stValidate(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_Validate", g, VarBinaryType.INSTANCE);
    }


    /*-------------------below LineString and MultiLineString Property Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-endpoint">ST_EndPoint(ls)</a>
     */
    public static SimpleExpression stEndPoint(final Expression ls) {
        return FunctionUtils.oneArgFunc("ST_EndPoint", ls, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-isclosed">ST_IsClosed(ls)</a>
     */
    public static SimpleExpression stIsClosed(final Expression ls) {
        return FunctionUtils.oneArgFunc("ST_IsClosed", ls, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-length">ST_Length(ls [, unit])</a>
     */
    public static SimpleExpression stLength(Expression ls) {
        return FunctionUtils.oneArgFunc("ST_Length", ls, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param ls   non-null
     * @param unit non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-length">ST_Length(ls [, unit])</a>
     */
    public static SimpleExpression stLength(final Expression ls, Expression unit) {
        return FunctionUtils.twoArgFunc("ST_Length", ls, unit, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     *
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-numpoints">ST_NumPoints(ls)</a>
     */
    public static SimpleExpression stNumPoints(final Expression ls) {
        return FunctionUtils.oneArgFunc("ST_NumPoints", ls, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ls non-null
     * @param n  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-pointn">ST_PointN(ls, N)</a>
     */
    public static SimpleExpression stPointN(final Expression ls, final Expression n) {
        return FunctionUtils.twoArgFunc("ST_PointN", ls, n, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-startpoint">ST_StartPoint(ls)</a>
     */
    public static SimpleExpression stStartPoint(final Expression ls) {
        return FunctionUtils.oneArgFunc("ST_StartPoint", ls, VarBinaryType.INSTANCE);
    }


    /*-------------------below Spatial Relation Functions That Use Object Shapes-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-contains">ST_Contains(g1, g2)</a>
     */
    public static IPredicate stContains(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Contains", g1, g2);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-crosses">ST_Crosses(g1, g2)</a>
     */
    public static IPredicate stCrosses(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Crosses", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-disjoint">ST_Disjoint(g1, g2)</a>
     */
    public static IPredicate stDisjoint(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Disjoint", g1, g2);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #stDistance(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-distance">ST_Distance(g1, g2 [, unit])</a>
     */
    public static SimpleExpression stDistance(final Expression g1, Expression g2) {
        return FunctionUtils.twoArgFunc("ST_Distance", g1, g2, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param g1   non-null
     * @param g2   non-null
     * @param unit non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #stDistance(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-distance">ST_Distance(g1, g2 [, unit])</a>
     */
    public static SimpleExpression stDistance(final Expression g1, Expression g2, Expression unit) {
        return FunctionUtils.threeArgFunc("ST_Distance", g1, g2, unit, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-equals">ST_Equals(g1, g2)</a>
     */
    public static IPredicate stEquals(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Equals", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-frechetdistance">ST_FrechetDistance(g1, g2 [, unit])</a>
     */
    public static SimpleExpression stFrechetDistance(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_FrechetDistance", g1, g2, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param g1   non-null
     * @param g2   non-null
     * @param unit non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-frechetdistance">ST_FrechetDistance(g1, g2 [, unit])</a>
     */
    public static SimpleExpression stFrechetDistance(final Expression g1, final Expression g2, final Expression unit) {
        return FunctionUtils.threeArgFunc("ST_FrechetDistance", g1, g2, unit, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-hausdorffdistance">ST_HausdorffDistance(g1, g2 [, unit])</a>
     */
    public static SimpleExpression stHausdorffDistance(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_HausdorffDistance", g1, g2, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param g1   non-null
     * @param g2   non-null
     * @param unit non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-hausdorffdistance">ST_HausdorffDistance(g1, g2 [, unit])</a>
     */
    public static SimpleExpression stHausdorffDistance(final Expression g1, final Expression g2, final Expression unit) {
        return FunctionUtils.threeArgFunc("ST_HausdorffDistance", g1, g2, unit, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-intersects">ST_Intersects(g1, g2)</a>
     */
    public static IPredicate stIntersects(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Intersects", g1, g2);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when g1 or g2 is multi parameter or literal
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-overlaps">ST_Overlaps(g1, g2)</a>
     */
    public static IPredicate stOverlaps(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Overlaps", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when g1 or g2 is multi parameter or literal
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-touches">ST_Touches(g1, g2)</a>
     */
    public static IPredicate stTouches(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Touches", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when g1 or g2 is multi parameter or literal
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-within">ST_Within(g1, g2)</a>
     */
    public static IPredicate stWithin(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Within", g1, g2);
    }


    /*-------------------below Spatial Geohash Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param point     non-null
     * @param maxLength non-null
     * @throws CriteriaException throw when any argument is multi parameter or literal
     * @see #stGeoHash(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-geohash">ST_GeoHash(longitude, latitude, max_length), ST_GeoHash(point, max_length)</a>
     */
    public static SimpleExpression stGeoHash(final Expression point, final Expression maxLength) {
        return FunctionUtils.twoArgFunc("ST_GeoHash", point, maxLength, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param longitude non-null
     * @param latitude  non-null
     * @param maxLength non-null
     * @throws CriteriaException throw when any argument is multi parameter or literal
     * @see #stGeoHash(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-geohash">ST_GeoHash(longitude, latitude, max_length), ST_GeoHash(point, max_length)</a>
     */
    public static SimpleExpression stGeoHash(final Expression longitude, final Expression latitude, final Expression maxLength) {
        return FunctionUtils.threeArgFunc("ST_GeoHash", longitude, latitude, maxLength, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param geohashStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-latfromgeohash">ST_LatFromGeoHash(geohash_str)</a>
     */
    public static SimpleExpression stLatFromGeoHash(final Expression geohashStr) {
        return FunctionUtils.oneArgFunc("ST_LatFromGeoHash", geohashStr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param geohashStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-longfromgeohash">ST_LongFromGeoHash(geohash_str)</a>
     */
    public static SimpleExpression stLongFromGeoHash(final Expression geohashStr) {
        return FunctionUtils.oneArgFunc("ST_LongFromGeoHash", geohashStr, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType}
     *
     *
     * @param geohashStr non-null
     * @param srid       non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-pointfromgeohash">ST_PointFromGeoHash(geohash_str, srid)</a>
     */
    public static SimpleExpression stPointFromGeoHash(final Expression geohashStr, final Expression srid) {
        return FunctionUtils.twoArgFunc("ST_PointFromGeoHash", geohashStr, srid, VarBinaryType.INSTANCE);
    }

    /*-------------------below Functions That Create Geometry Values from WKT Values-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-geomcollfromtext">ST_GeomCollFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomCollFromText(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_GeomCollFromText", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-geomcollfromtext">ST_GeomCollFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomCollFromText(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_GeomCollFromText", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-geomcollfromtext">ST_GeomCollFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomCollFromText(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_GeomCollFromText", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-geomfromtext">ST_GeomFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomFromText(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_GeomFromText", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-geomfromtext">ST_GeomFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomFromText(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_GeomFromText", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-geomfromtext">ST_GeomFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomFromText(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_GeomFromText", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-linefromtext">ST_LineStringFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stLineStringFromText(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_LineStringFromText", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-linefromtext">ST_LineStringFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stLineStringFromText(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_LineStringFromText", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-linefromtext">ST_LineStringFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stLineStringFromText(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_LineStringFromText", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mlinefromtext">ST_MultiLineStringFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiLineStringFromText(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_MultiLineStringFromText", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mlinefromtext">ST_MultiLineStringFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiLineStringFromText(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_MultiLineStringFromText", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mlinefromtext">ST_MultiLineStringFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiLineStringFromText(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_MultiLineStringFromText", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mpointfromtext">ST_MultiPointFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiPointFromText(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_MultiPointFromText", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mpointfromtext">ST_MultiPointFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiPointFromText(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_MultiPointFromText", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mpointfromtext">ST_MultiPointFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiPointFromText(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_MultiPointFromText", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mpolyfromtext">ST_MultiPolygonFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiPolygonFromText(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_MultiPolygonFromText", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mpolyfromtext">ST_MultiPolygonFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiPolygonFromText(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_MultiPolygonFromText", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mpolyfromtext">ST_MultiPolygonFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiPolygonFromText(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_MultiPolygonFromText", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-pointfromtext">ST_PointFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stPointFromText(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_PointFromText", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-pointfromtext">ST_PointFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stPointFromText(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_PointFromText", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-pointfromtext">ST_PointFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stPointFromText(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_PointFromText", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-polyfromtext">ST_PolygonFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stPolygonFromText(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_PolygonFromText", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-polyfromtext">ST_PolygonFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stPolygonFromText(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_PolygonFromText", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-polyfromtext">ST_PolygonFromText(wkt [, srid [, options]])</a>
     */
    public static SimpleExpression stPolygonFromText(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_PolygonFromText", wkt, srid, options, VarBinaryType.INSTANCE);
    }

    /*-------------------below Functions That Create Geometry Values from WKB Values-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-geomcollfromwkb">ST_GeomCollFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomCollFromWKB(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_GeomCollFromWKB", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-geomcollfromwkb">ST_GeomCollFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomCollFromWKB(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_GeomCollFromWKB", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-geomcollfromwkb">ST_GeomCollFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomCollFromWKB(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_GeomCollFromWKB", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-geomfromwkb">ST_GeomFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomFromWKB(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_GeomFromWKB", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-geomfromwkb">ST_GeomFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomFromWKB(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_GeomFromWKB", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-geomfromwkb">ST_GeomFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stGeomFromWKB(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_GeomFromWKB", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-linefromwkb">ST_LineStringFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stLineStringFromWKB(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_LineStringFromWKB", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-linefromwkb">ST_LineStringFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stLineStringFromWKB(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_LineStringFromWKB", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-linefromwkb">ST_LineStringFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stLineStringFromWKB(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_LineStringFromWKB", wkt, srid, options, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-mlinefromwkb">ST_MultiLineStringFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiLineStringFromWKB(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_MultiLineStringFromWKB", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-mlinefromwkb">ST_MultiLineStringFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiLineStringFromWKB(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_MultiLineStringFromWKB", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-mlinefromwkb">ST_MultiLineStringFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiLineStringFromWKB(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_MultiLineStringFromWKB", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-mpolyfromwkb">ST_MultiPolygonFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiPolygonFromWKB(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_MultiPolygonFromWKB", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-mpolyfromwkb">ST_MultiPolygonFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiPolygonFromWKB(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_MultiPolygonFromWKB", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-mpolyfromwkb">ST_MultiPolygonFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stMultiPolygonFromWKB(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_MultiPolygonFromWKB", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-pointfromwkb">ST_PointFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stPointFromWKB(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_PointFromWKB", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-pointfromwkb">ST_PointFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stPointFromWKB(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_PointFromWKB", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-pointfromwkb">ST_PointFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stPointFromWKB(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_PointFromWKB", wkt, srid, options, VarBinaryType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-polyfromwkb">ST_PolygonFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stPolygonFromWKB(final Expression wkt) {
        return FunctionUtils.oneArgFunc("ST_PolygonFromWKB", wkt, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-polyfromwkb">ST_PolygonFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stPolygonFromWKB(final Expression wkt, Expression srid) {
        return FunctionUtils.twoArgFunc("ST_PolygonFromWKB", wkt, srid, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param wkt non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-polyfromwkb">ST_PolygonFromWKB(wkb [, srid [, options]])</a>
     */
    public static SimpleExpression stPolygonFromWKB(final Expression wkt, Expression srid, Expression options) {
        return FunctionUtils.threeArgFunc("ST_PolygonFromWKB", wkt, srid, options, VarBinaryType.INSTANCE);
    }

    /*-------------------below GeometryCollection Property Functions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param gc non-null
     * @param n  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-geometrycollection-property-functions.html#function_st-geometryn">ST_GeometryN(gc, N)</a>
     */
    public static SimpleExpression stGeometryN(final Expression gc, final Expression n) {
        return FunctionUtils.twoArgFunc("ST_GeometryN", gc, n, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     *
     *
     * @param gc non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-geometrycollection-property-functions.html#function_st-numgeometries">ST_NumGeometries(gc)</a>
     */
    public static SimpleExpression stNumGeometries(final Expression gc) {
        return FunctionUtils.oneArgFunc("ST_NumGeometries", gc, IntegerType.INSTANCE);
    }

    /*-------------------below General Geometry Property Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-dimension">ST_Dimension(g)</a>
     */
    public static SimpleExpression stDimension(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_Dimension", g, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-envelope">ST_Envelope(g)</a>
     */
    public static SimpleExpression stEnvelope(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_Envelope", g, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-geometrytype">ST_GeometryType(g)</a>
     */
    public static SimpleExpression stGeometryType(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_GeometryType", g, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-isempty">ST_IsEmpty(g)</a>
     */
    public static IPredicate stIsEmpty(final Expression g) {
        return FunctionUtils.oneArgPredicateFunc("ST_IsEmpty", g);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     *
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-issimple">ST_IsSimple(g)</a>
     */
    public static IPredicate stIsSimple(final Expression g) {
        return FunctionUtils.oneArgPredicateFunc("ST_IsSimple", g);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     *
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-srid">ST_SRID(g [, srid])</a>
     */
    public static SimpleExpression stSRID(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_SRID", p, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-srid">ST_SRID(g [, srid])</a>
     */
    public static SimpleExpression stSRID(final Expression p, final Expression srid) {
        return FunctionUtils.twoArgFunc("ST_SRID", p, srid, VarBinaryType.INSTANCE);
    }

    /*-------------------below Point Property Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-latitude">ST_Latitude(p [, new_latitude_val])</a>
     */
    public static SimpleExpression stLatitude(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_Latitude", p, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param p              non-null
     * @param newLatitudeVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-latitude">ST_Latitude(p [, new_latitude_val])</a>
     */
    public static SimpleExpression stLatitude(final Expression p, final Expression newLatitudeVal) {
        return FunctionUtils.twoArgFunc("ST_Latitude", p, newLatitudeVal, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-longitude">ST_Longitude(p [, new_longitude_val])</a>
     */
    public static SimpleExpression stLongitude(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_Longitude", p, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param p               non-null
     * @param newLongitudeVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-longitude">ST_Longitude(p [, new_longitude_val])</a>
     */
    public static SimpleExpression stLongitude(final Expression p, final Expression newLongitudeVal) {
        return FunctionUtils.twoArgFunc("ST_Longitude", p, newLongitudeVal, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-x">ST_X(p [, new_x_val])</a>
     */
    public static SimpleExpression stX(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_X", p, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param p       non-null
     * @param newXVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-x">ST_X(p [, new_x_val])</a>
     */
    public static SimpleExpression stX(final Expression p, final Expression newXVal) {
        return FunctionUtils.twoArgFunc("ST_X", p, newXVal, VarBinaryType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     *
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-y">ST_Y(p [, new_y_val])</a>
     */
    public static SimpleExpression stY(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_Y", p, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link VarBinaryType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     *
     *
     * @param p       non-null
     * @param newYVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-y">ST_Y(p [, new_y_val])</a>
     */
    public static SimpleExpression stY(final Expression p, final Expression newYVal) {
        return FunctionUtils.twoArgFunc("ST_Y", p, newYVal, VarBinaryType.INSTANCE);
    }


}
