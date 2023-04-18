package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.SimplePredicate;
import io.army.criteria.TypeInfer;
import io.army.mapping.BooleanType;
import io.army.mapping.DoubleType;
import io.army.mapping.IntegerType;
import io.army.mapping.MappingType;
import io.army.mapping.spatial.postgre.*;

import java.util.function.BiFunction;

/**
 * <p>
 * Package class,This class hold postgre geometric function methods.
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreGeometricFunctions extends PostgreDateTimeFunctions {

    /**
     * package constructor
     */
    PostgreGeometricFunctions() {
    }


    /*-------------------below Geometric Functions and Operators -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">area ( geometric_type ) → double precision<br/>
     * Computes area. Available for box, path, circle. A path input must be closed, else NULL is returned. Also, if the path is self-intersecting, the result may be meaningless.<br/>
     * area(box '(2,2),(0,0)') → 4
     * </a>
     */
    public static SimpleExpression area(Expression geometricType) {
        return FunctionUtils.oneArgFunc("AREA", geometricType, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgrePointType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">center ( geometric_type ) → point<br/>
     * Computes center point. Available for box, circle.<br/>
     * center(box '(1,2),(0,0)') → (0.5,1)
     * </a>
     */
    public static SimpleExpression center(Expression geometricType) {
        return FunctionUtils.oneArgFunc("CENTER", geometricType, PostgrePointType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgreLsegType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">diagonal ( box ) → lseg<br/>
     * Extracts box's diagonal as a line segment (same as lseg(box)).<br/>
     * diagonal(box '(1,2),(0,0)') → [(1,2),(0,0)]
     * </a>
     */
    public static SimpleExpression diagonal(Expression box) {
        return FunctionUtils.oneArgFunc("DIAGONAL", box, PostgreLsegType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">diameter ( circle ) → double precision<br/>
     * Computes diameter of circle.<br/>
     * diameter(circle '<(0,0),2>') → 4
     * </a>
     */
    public static SimpleExpression diameter(Expression circle) {
        return FunctionUtils.oneArgFunc("DIAMETER", circle, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">height ( box ) → double precision<br/>
     * Computes vertical size of box.<br/>
     * height(box '(1,2),(0,0)') → 2
     * </a>
     */
    public static SimpleExpression height(Expression box) {
        return FunctionUtils.oneArgFunc("HEIGHT", box, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: <ol>
     * <li>If geometricType is {@link MappingType.SqlGeometryType}  or {@link PostgreGeometricType},then {@link DoubleType}</li>
     * <li>Else {@link IntegerType}</li>
     * </ol>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">length ( geometric_type ) → double precision<br/>
     * Computes the total length. Available for lseg, path.<br/>
     * length(path '((-1,0),(1,0))') → 4
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-textsearch.html#TEXTSEARCH-FUNCTIONS-TABLE">length ( tsvector ) → integer<br/>
     * Returns the number of lexemes in the tsvector.<br/>
     * </a>
     */
    public static SimpleExpression length(Expression geometricType) {
        return FunctionUtils.oneArgFunc("LENGTH", geometricType, _returnType(geometricType, PostgreExpressions::lengthFuncType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">npoints ( geometric_type ) → integer<br/>
     * Returns the number of points. Available for path, polygon.<br/>
     * npoints(path '[(0,0),(1,1),(2,0)]') → 3
     * </a>
     */
    public static SimpleExpression npoints(Expression geometricType) {
        return FunctionUtils.oneArgFunc("NPOINTS", geometricType, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgrePathType}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link PostgrePathType#INSTANCE}.
     * @param path    non-null and non-empty,it will be passed to funcRef as the second argument of funcRef
     * @see #pclose(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">pclose ( path ) → path<br/>
     * Converts path to closed form.<br/>
     * pclose(path '[(0,0),(1,1),(2,0)]') → ((0,0),(1,1),(2,0))
     * </a>
     */
    public static SimpleExpression pclose(BiFunction<MappingType, String, Expression> funcRef, String path) {
        return pclose(funcRef.apply(PostgrePathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgrePathType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">pclose ( path ) → path<br/>
     * Converts path to closed form.<br/>
     * pclose(path '[(0,0),(1,1),(2,0)]') → ((0,0),(1,1),(2,0))
     * </a>
     */
    public static SimpleExpression pclose(Expression path) {
        return FunctionUtils.oneArgFunc("PCLOSE", path, PostgrePathType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgrePathType}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link PostgrePathType#INSTANCE}.
     * @param path    non-null and non-empty,it will be passed to funcRef as the second argument of funcRef
     * @see #popen(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">popen ( path ) → path<br/>
     * Converts path to open form.<br/>
     * popen(path '((0,0),(1,1),(2,0))') → [(0,0),(1,1),(2,0)]
     * </a>
     */
    public static SimpleExpression popen(BiFunction<MappingType, String, Expression> funcRef, String path) {
        return popen(funcRef.apply(PostgrePathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  PostgrePathType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">popen ( path ) → path<br/>
     * Converts path to open form.<br/>
     * popen(path '((0,0),(1,1),(2,0))') → [(0,0),(1,1),(2,0)]
     * </a>
     */
    public static SimpleExpression popen(Expression path) {
        return FunctionUtils.oneArgFunc("POPEN", path, PostgrePathType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">radius ( circle ) → double precision<br/>
     * Computes radius of circle.<br/>
     * radius(circle '&lt;(0,0),2>') → 2
     * </a>
     */
    public static SimpleExpression radius(Expression circle) {
        return FunctionUtils.oneArgFunc("RADIUS", circle, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">slope ( point, point ) → double precision<br/>
     * Computes slope of a line drawn through the two points.<br/>
     * slope(point '(0,0)', point '(2,1)') → 0.5
     * </a>
     */
    public static SimpleExpression slope(Expression point1, Expression point2) {
        return FunctionUtils.twoArgFunc("SLOPE", point1, point2, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">width ( box ) → double precision<br/>
     * Computes horizontal size of box.<br/>
     * width(box '(1,2),(0,0)') → 1
     * </a>
     */
    public static SimpleExpression width(Expression box) {
        return FunctionUtils.oneArgFunc("WIDTH", box, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreBoxType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">box ( circle ) → box<br/>
     * Computes box inscribed within the circle.<br/>
     * box(circle '&lt;(0,0),2>') → (1.414213562373095,1.414213562373095),(-1.414213562373095,-1.414213562373095)<br/>
     * box ( point ) → box<br/>
     * Converts point to empty box.<br/>
     * box(point '(1,0)') → (1,0),(1,0)<br/>
     * Converts any two corner points to box.<br/>
     * box ( polygon ) → box<br/>
     * Computes bounding box of polygon.<br/>
     * box(polygon '((0,0),(1,1),(2,0))') → (2,1),(0,0)
     * </a>
     */
    public static SimpleExpression box(Expression exp) {
        return FunctionUtils.oneArgFunc("BOX", exp, PostgreBoxType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreBoxType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">box ( point, point ) → box<br/>
     * Converts any two corner points to box.<br/>
     * box(point '(0,1)', point '(1,0)') → (1,1),(0,0)
     * </a>
     */
    public static SimpleExpression box(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("BOX", exp1, exp2, PostgreBoxType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreBoxType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">bound_box ( box, box ) → box<br/>
     * Computes bounding box of two boxes.<br/>
     * bound_box(box '(1,1),(0,0)', box '(4,4),(3,3)') → (4,4),(0,0)
     * </a>
     */
    public static SimpleExpression boundBox(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("BOUND_BOX", exp1, exp2, PostgreBoxType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreCircleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">circle ( box ) → circle<br/>
     * Computes smallest circle enclosing box.<br/>
     * circle(box '(1,1),(0,0)') → &lt;(0.5,0.5),0.7071067811865476> <br/>
     * circle ( polygon ) → circle<br/>
     * Converts polygon to circle. The circle's center is the mean of the positions of the polygon's points, and the radius is the average distance of the polygon's points from that center.<br/>
     * circle(polygon '((0,0),(1,3),(2,0))') → &lt;(1,1),1.6094757082487299>
     * </a>
     */
    public static SimpleExpression circle(Expression exp) {
        return FunctionUtils.oneArgFunc("CIRCLE", exp, PostgreCircleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreCircleType}
     * </p>
     *
     * @param funcRefForPoint  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         The first argument of funcRefForPoint always is {@link PostgrePointType#INSTANCE}.
     * @param point            it will be passed to funcRefForPoint as the second argument of funcRefForPoint
     * @param funcRefForRadius the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                         The first argument of funcRefForRadius always is {@link DoubleType#INSTANCE}.
     * @param radius           it will be passed to funcRefForRadius as the second argument of funcRefForRadius
     * @see #circle(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">circle ( point, double precision ) → circle<br/>
     * Constructs circle from center and radius.<br/>
     * circle(point '(0,0)', 2.0) → &lt;(0,0),2>
     * </a>
     */
    public static <T> SimpleExpression circle(BiFunction<MappingType, String, Expression> funcRefForPoint, String point,
                                              BiFunction<MappingType, T, Expression> funcRefForRadius, T radius) {
        return circle(funcRefForPoint.apply(PostgrePointType.INSTANCE, point),
                funcRefForRadius.apply(DoubleType.INSTANCE, radius)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreCircleType}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                         <ul>
     *                             <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                             <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                             <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                             <li>developer custom method</li>
     *                         </ul>.
     *                The first argument of funcRef always is {@link DoubleType#INSTANCE}.
     * @param radius  it will be passed to funcRef as the second argument of funcRef
     * @see #circle(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">circle ( point, double precision ) → circle<br/>
     * Constructs circle from center and radius.<br/>
     * circle(point '(0,0)', 2.0) → &lt;(0,0),2>
     * </a>
     */
    public static <T> SimpleExpression circle(Expression point, BiFunction<MappingType, T, Expression> funcRef,
                                              T radius) {
        return circle(point, funcRef.apply(DoubleType.INSTANCE, radius));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreCircleType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">circle ( point, double precision ) → circle<br/>
     * Constructs circle from center and radius.<br/>
     * circle(point '(0,0)', 2.0) → &lt;(0,0),2>
     * </a>
     */
    public static SimpleExpression circle(Expression point, Expression radius) {
        return FunctionUtils.twoArgFunc("CIRCLE", point, radius, PostgreCircleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreLineType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">line ( point, point ) → line<br/>
     * Converts two points to the line through them.<br/>
     * line(point '(-1,0)', point '(1,0)') → {0,-1,0}
     * </a>
     */
    public static SimpleExpression line(Expression point1, Expression point2) {
        return FunctionUtils.twoArgFunc("LINE", point1, point2, PostgreLineType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreLsegType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">lseg ( box ) → lseg<br/>
     * Extracts box's diagonal as a line segment.<br/>
     * lseg(box '(1,0),(-1,0)') → [(1,0),(-1,0)]
     * </a>
     */
    public static SimpleExpression lseg(Expression exp) {
        return FunctionUtils.oneArgFunc("LSEG", exp, PostgreLsegType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreLsegType}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link PostgrePointType#INSTANCE}.
     * @param point1  it will be passed to funcRef as the second argument of funcRef
     * @param point2  it will be passed to funcRef as the second argument of funcRef
     * @see #lseg(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">lseg ( point, point ) → lseg<br/>
     * Constructs line segment from two endpoints.<br/>
     * lseg(point '(-1,0)', point '(1,0)') → [(-1,0),(1,0)]
     * </a>
     */
    public static SimpleExpression lseg(BiFunction<MappingType, String, Expression> funcRef, String point1,
                                        String point2) {
        return lseg(funcRef.apply(PostgrePointType.INSTANCE, point1),
                funcRef.apply(PostgrePointType.INSTANCE, point2)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgreLsegType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">lseg ( point, point ) → lseg<br/>
     * Constructs line segment from two endpoints.<br/>
     * lseg(point '(-1,0)', point '(1,0)') → [(-1,0),(1,0)]
     * </a>
     */
    public static SimpleExpression lseg(Expression point1, Expression point2) {
        return FunctionUtils.twoArgFunc("LSEG", point1, point2, PostgreLsegType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgrePathType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">path ( polygon ) → path<br/>
     * Converts polygon to a closed path with the same list of points.<br/>
     * path(polygon '((0,0),(1,1),(2,0))') → ((0,0),(1,1),(2,0))
     * </a>
     */
    public static SimpleExpression path(Expression exp) {
        return FunctionUtils.oneArgFunc("PATH", exp, PostgrePathType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgrePointType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">point ( box ) → point<br/>
     * Computes center of box.<br/>
     * point(box '(1,0),(-1,0)') → (0,0)<br/>
     * point ( circle ) → point<br/>
     * Computes center of circle.<br/>
     * point(circle '&lt;(0,0),2>') → (0,0)<br/>
     * point ( lseg ) → point<br/>
     * Computes center of line segment. <br/>
     * point(lseg '[(-1,0),(1,0)]') → (0,0) <br/>
     * point ( polygon ) → point <br/>
     * Computes center of polygon (the mean of the positions of the polygon's points). <br/>
     * point(polygon '((0,0),(1,1),(2,0))') → (1,0.3333333333333333)
     * </a>
     */
    public static SimpleExpression point(Expression exp) {
        return FunctionUtils.oneArgFunc("POINT", exp, PostgrePointType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgrePointType}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link DoubleType#INSTANCE}.
     * @param x       it will be passed to funcRef as the second argument of funcRef
     * @param y       it will be passed to funcRef as the second argument of funcRef
     * @see #point(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">point ( double precision, double precision ) → point<br/>
     * Constructs point from its coordinates.<br/>
     * point(23.4, -44.5) → (23.4,-44.5)
     * </a>
     */
    public static <T> SimpleExpression point(BiFunction<MappingType, T, Expression> funcRef, T x, T y) {
        return point(funcRef.apply(DoubleType.INSTANCE, x),
                funcRef.apply(DoubleType.INSTANCE, y)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgrePointType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">point ( double precision, double precision ) → point<br/>
     * Constructs point from its coordinates.<br/>
     * point(23.4, -44.5) → (23.4,-44.5)
     * </a>
     */
    public static SimpleExpression point(Expression x, Expression y) {
        return FunctionUtils.twoArgFunc("POINT", x, y, PostgrePointType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgrePolygonType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">polygon ( box ) → polygon<br/>
     * Converts box to a 4-point polygon.<br/>
     * polygon(box '(1,1),(0,0)') → ((0,0),(0,1),(1,1),(1,0))<br/>
     * polygon ( circle ) → polygon<br/>
     * Converts circle to a 12-point polygon.<br/>
     * polygon(circle '&lt;(0,0),2>') → ((-2,0),(-1.7320508075688774,0.9999999999999999),(-1.0000000000000002,1.7320508075688772),<br/>
     * (-1.2246063538223773e-16,2),(0.9999999999999996,1.7320508075688774),(1.732050807568877,1.0000000000000007), <br/>
     * (2,2.4492127076447545e-16),(1.7320508075688776,-0.9999999999999994),(1.0000000000000009,-1.7320508075688767),<br/>
     * (3.673819061467132e-16,-2),(-0.9999999999999987,-1.732050807568878),(-1.7320508075688767,-1.0000000000000009))<br/>
     * polygon ( path ) → polygon<br/>
     * Converts closed path to a polygon with the same list of points.<br/>
     * polygon(path '((0,0),(1,1),(2,0))') → ((0,0),(1,1),(2,0))
     * </a>
     */
    public static SimpleExpression polygon(Expression exp) {
        return FunctionUtils.oneArgFunc("POLYGON", exp, PostgrePolygonType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link PostgrePolygonType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-CONV-TABLE">polygon ( integer, circle ) → polygon<br/>
     * Converts circle to an n-point polygon.<br/>
     * polygon(4, circle '&lt;(3,0),1>') → ((2,0),(3,1),(4,1.2246063538223773e-16),(3,-1))
     * </a>
     */
    public static SimpleExpression polygon(Expression exp1, Expression exp2) {
        return FunctionUtils.twoArgFunc("POLYGON", exp1, exp2, PostgrePolygonType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link PostgrePathType#INSTANCE}.
     * @param path    non-null and non-empty,it will be passed to funcRef as the second argument of funcRef
     * @see #isClosed(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">isclosed ( path ) → boolean<br/>
     * Is path closed?<br/>
     * isclosed(path '((0,0),(1,1),(2,0))') → t
     * </a>
     */
    public static SimplePredicate isClosed(BiFunction<MappingType, String, Expression> funcRef, String path) {
        return isClosed(funcRef.apply(PostgrePathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">isclosed ( path ) → boolean<br/>
     * Is path closed?<br/>
     * isclosed(path '((0,0),(1,1),(2,0))') → t
     * </a>
     */
    public static SimplePredicate isClosed(Expression path) {
        return FunctionUtils.oneArgFuncPredicate("ISCLOSED", path);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType}
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The first argument of funcRef always is {@link PostgrePathType#INSTANCE}.
     * @param path    non-null and non-empty,it will be passed to funcRef as the second argument of funcRef
     * @see #isOpen(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">isopen ( path ) → boolean<br/>
     * Is path open?<br/>
     * isopen(path '[(0,0),(1,1),(2,0)]') → t
     * </a>
     */
    public static SimplePredicate isOpen(BiFunction<MappingType, String, Expression> funcRef, String path) {
        return isOpen(funcRef.apply(PostgrePathType.INSTANCE, path));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-geometry.html#FUNCTIONS-GEOMETRY-FUNC-TABLE">isopen ( path ) → boolean<br/>
     * Is path open?<br/>
     * isopen(path '[(0,0),(1,1),(2,0)]') → t
     * </a>
     */
    public static SimplePredicate isOpen(Expression path) {
        return FunctionUtils.oneArgFuncPredicate("ISOPEN", path);
    }


}
