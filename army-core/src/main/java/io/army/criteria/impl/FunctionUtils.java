package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelectionGroup;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect.*;
import io.army.function.BetweenOperator;
import io.army.function.BetweenValueOperator;
import io.army.function.ExpressionOperator;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.*;

abstract class FunctionUtils {

    FunctionUtils() {
        throw new UnsupportedOperationException();
    }


    interface ArmyFuncClause extends _SelfDescribed, Clause {

    }


    interface FunctionOuterClause {


        void appendFuncRest(StringBuilder sqlBuilder, _SqlContext context);

        void funcRestToString(StringBuilder builder);

    }


    static SQLFunction._CaseFuncWhenClause caseFunction(final @Nullable Expression caseValue) {
        if (!(caseValue == null || caseValue instanceof OperationExpression)) {
            throw CriteriaUtils.funcArgError("CASE", caseValue);
        }
        return new CaseFunction((ArmyExpression) caseValue);
    }


    static SimpleExpression oneArgFunc(String name, SQLExpression one, TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgFunction(name, one, returnType);
    }


    static SimpleExpression twoArgFunc(final String name, final SQLExpression one,
                                       final SQLExpression two, TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new TwoArgFunction(name, one, two, returnType);
    }

    static SimpleExpression threeArgFunc(final String name, final SQLExpression one, final SQLExpression two,
                                         final SQLExpression three, TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return new ThreeArgFunction(name, one, two, three, returnType);
    }

    static SimpleExpression fourArgFunc(final String name, final SQLExpression one, final SQLExpression two,
                                        final SQLExpression three, final SQLExpression four,
                                        final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        }
        return new FourArgFunction(name, one, two, three, four, returnType);
    }

    static SimpleExpression fiveArgFunc(final String name, final SQLExpression one, final SQLExpression two,
                                        final SQLExpression three, final SQLExpression four,
                                        final SQLExpression five, final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        } else if (!(five instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, five);
        }
        return new FiveArgFunction(name, one, two, three, four, five, returnType);
    }

    static SimpleExpression sixArgFunc(final String name, final SQLExpression one, final SQLExpression two,
                                       final SQLExpression three, final SQLExpression four,
                                       final SQLExpression five, final SQLExpression six,
                                       final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        } else if (!(five instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, five);
        } else if (!(six instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, six);
        }
        return new SixArgFunction(name, one, two, three, four, five, six, returnType);
    }

    static SimpleExpression sevenArgFunc(final String name, final SQLExpression one, final SQLExpression two,
                                         final SQLExpression three, final SQLExpression four,
                                         final SQLExpression five, final SQLExpression six,
                                         final SQLExpression seven, final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        } else if (!(five instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, five);
        } else if (!(six instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, six);
        } else if (!(seven instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, seven);
        }
        return new SevenArgFunction(name, one, two, three, four, five, six, seven, returnType);
    }

    static SimpleExpression oneNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                            final TypeMeta returnType) {
        assertNotation(name, null, validator, one);
        return oneArgFunc(name, one, returnType);
    }

    static SimpleExpression twoNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                            final Expression two, final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        assertNotation(name, notation, validator, two);

        return twoArgFunc(name, one, two, returnType);
    }

    static SimpleExpression threeNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                              final Expression two, final Expression three, final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        assertNotation(name, notation, validator, three);

        return threeArgFunc(name, one, two, three, returnType);
    }

    static SimpleExpression fourNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                             final Expression two, final Expression three, final Expression four,
                                             final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        notation = assertNotation(name, notation, validator, three);
        assertNotation(name, notation, validator, four);

        return fourArgFunc(name, one, two, three, four, returnType);
    }

    static SimpleExpression fiveNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                             final Expression two, final Expression three, final Expression four,
                                             final Expression five, final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        notation = assertNotation(name, notation, validator, three);
        notation = assertNotation(name, notation, validator, four);

        assertNotation(name, notation, validator, five);

        return fiveArgFunc(name, one, two, three, four, five, returnType);
    }

    static SimpleExpression sixNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                            final Expression two, final Expression three, final Expression four,
                                            final Expression five, final Expression six, final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        notation = assertNotation(name, notation, validator, three);
        notation = assertNotation(name, notation, validator, four);

        notation = assertNotation(name, notation, validator, five);
        assertNotation(name, notation, validator, six);

        return sixArgFunc(name, one, two, three, four, five, six, returnType);
    }

    static SimpleExpression sevenNotationFunc(final String name, final Predicate<String> validator, final Expression one,
                                              final Expression two, final Expression three, final Expression four,
                                              final Expression five, final Expression six, final Expression seven,
                                              final TypeMeta returnType) {
        String notation;

        notation = assertNotation(name, null, validator, one);
        notation = assertNotation(name, notation, validator, two);
        notation = assertNotation(name, notation, validator, three);
        notation = assertNotation(name, notation, validator, four);

        notation = assertNotation(name, notation, validator, five);
        notation = assertNotation(name, notation, validator, six);
        assertNotation(name, notation, validator, seven);

        return sevenArgFunc(name, one, two, three, four, five, six, seven, returnType);
    }


    static Expression namedNotation(final String name, final Expression argument) {
        if (!_DialectUtils.isSimpleIdentifier(name)) {
            String m = String.format("named notation[%s] isn't simple identifier.", name);
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        if (argument instanceof NamedNotation) {
            String m = String.format("argument[%s] of Named Notation[%s] couldn't be named Notation", name, argument);
            throw ContextStack.clearStackAndCriteriaError(m);
        } else if (argument instanceof SqlValueParam.MultiValue) {
            throw namedNotationIsMultiValue();
        }
        return new NamedNotation(name, (ArmyExpression) argument);
    }


    static SimplePredicate twoArgPredicateFunc(final String name, final Expression one, final Expression two) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new MultiArgFuncPredicate(name, true, null, twoExpList(name, one, two));
    }

    static SimplePredicate threeArgPredicateFunc(final String name, final Expression one, final Expression two,
                                                 final Expression three) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return new MultiArgFuncPredicate(name, true, null, threeExpList(name, one, two, three));
    }

    static SimplePredicate fourArgPredicateFunc(final String name, final Expression one, final Expression two,
                                                final Expression three, final Expression four) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        }
        final List<ArmyExpression> argList = _Collections.arrayList(4);

        argList.add((ArmyExpression) one);
        argList.add((ArmyExpression) two);
        argList.add((ArmyExpression) three);
        argList.add((ArmyExpression) four);
        return new MultiArgFuncPredicate(name, true, null, argList);
    }


    static SimpleExpression zeroArgFunc(String name, TypeMeta returnType) {
        return new ZeroArgFunction(name, true, returnType);
    }

    static SimpleExpression noParensFunc(String name, TypeMeta returnType) {
        return new NoParensFunctionExpression(name, returnType);
    }

    static SimpleExpression oneOrMultiArgFunc(String name, Expression exp, TypeMeta returnType) {
        if (!(exp instanceof FunctionArg)) {
            throw CriteriaUtils.funcArgError(name, exp);
        }
        return new OneArgFunction(name, exp, returnType);
    }

    static SimpleExpression oneAndMulti(String name, Expression one, Expression multi, TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(multi instanceof FunctionArg)) {
            throw CriteriaUtils.funcArgError(name, multi);
        }
        return new TwoArgFunction(name, one, multi, returnType);
    }

    static SimpleExpression twoOrMultiArgFunc(final String name, final Expression one, final Expression two,
                                              TypeMeta returnType) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        final List<ArmyExpression> argList;
        argList = Arrays.asList((ArmyExpression) one, (ArmyExpression) two);
        return new MultiArgFunctionExpression(name, argList, returnType);
    }

    static SimpleExpression oneAndMultiArgFunc(final String name, final Expression exp, final List<Expression> expList,
                                               final TypeMeta returnType) {
        if (exp instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, exp);
        }
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<ArmyExpression> argList = new ArrayList<>(1 + size);
        argList.add((ArmyExpression) exp);
        for (Expression e : expList) {
            argList.add((ArmyExpression) e);
        }
        return new MultiArgFunctionExpression(name, argList, returnType);
    }

    static SimpleExpression consumerAndFirstTypeFunc(final String name, final Consumer<Consumer<Expression>> consumer) {
        final List<ArmyExpression> argList = _Collections.arrayList();
        consumer.accept(exp -> {
            if (!(exp instanceof FunctionArg)) {
                throw CriteriaUtils.funcArgError(name, exp);
            }
            argList.add((ArmyExpression) exp);
        });
        final int argSize;
        argSize = argList.size();
        if (argSize == 0) {
            throw CriteriaUtils.dontAddAnyItem();
        }
        final TypeMeta returnType;
        returnType = Functions._returnType(argList.get(0), Expressions::identityType);
        final SimpleExpression func;
        switch (argSize) {
            case 1:
                func = new OneArgFunction(name, argList.get(0), returnType);
                break;
            case 2:
                func = new TwoArgFunction(name, argList.get(0), argList.get(1), returnType);
                break;
            case 3:
                func = new ThreeArgFunction(name, argList.get(0), argList.get(1), argList.get(2), returnType);
                break;
            case 4:
                func = new FourArgFunction(name, argList.get(0), argList.get(1), argList.get(2), argList.get(3),
                        returnType);
                break;
            default:
                func = new MultiArgFunctionExpression(name, argList, returnType);
        }
        return func;
    }

    static SimpleExpression oneAndConsumer(final String name, final boolean required, final Expression one,
                                           final Consumer<Consumer<Expression>> consumer,
                                           TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        final List<ArmyExpression> argList = _Collections.arrayList(2);
        argList.add((ArmyExpression) one);

        consumer.accept(exp -> {
            if (!(exp instanceof FunctionArg)) {
                throw CriteriaUtils.funcArgError(name, exp);
            }
            argList.add((ArmyExpression) exp);
        });

        final SimpleExpression func;
        if (argList.size() == 1) {
            if (required) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            func = new OneArgFunction(name, one, returnType);
        } else {
            func = new MultiArgFunctionExpression(name, argList, returnType);
        }
        return func;
    }

    static SimpleExpression twoAndMultiArgFunc(final String name, final Expression exp1, Expression exp2,
                                               final List<Expression> expList, final TypeMeta returnType) {
        return new MultiArgFunctionExpression(name, twoAndMultiExpList(name, exp1, exp2, expList), returnType);
    }

    static SimpleExpression multiArgFunc(final String name, final boolean required,
                                         final List<? extends SQLExpression> argList, final TypeMeta returnType) {
        final SimpleExpression func;
        if (argList.size() > 0) {
            final List<ArmySQLExpression> list = _Collections.arrayList(argList.size());
            for (SQLExpression exp : argList) {
                if (!(exp instanceof ArmySQLExpression)) {
                    throw CriteriaUtils.funcArgError(name, exp);
                }
                list.add((ArmySQLExpression) exp);
            }
            func = new MultiArgFunctionExpression(name, list, returnType);
        } else if (required) {
            throw ContextStack.clearStackAndCriteriaError("You don't add any expression.");
        } else {
            func = zeroArgFunc(name, returnType);
        }

        return func;
    }

    static SimpleExpression multiArgFunc(String name, List<Expression> argList, TypeMeta returnType) {
        return new MultiArgFunctionExpression(name, expList(name, argList), returnType);
    }


    static SimpleExpression staticStringObjectStringFunc(final String name, final boolean required,
                                                         final BiFunction<MappingType, String[], Expression> funcRef,
                                                         final MappingType paramType,
                                                         final Consumer<Statement._StringObjectSpaceClause> consumer,
                                                         final TypeMeta returnType) {

        final List<String> pairList = _Collections.arrayList();
        final CriteriaSupports.StringObjectSpaceClause clause;
        clause = CriteriaSupports.stringObjectSpace(required, pairList::add);
        consumer.accept(clause);
        clause.endClause();

        final SimpleExpression func;
        if (pairList.size() == 0) {
            func = new ZeroArgFunction(name, true, returnType);
        } else {
            final Expression arg;
            arg = funcRef.apply(paramType, pairList.toArray(new String[0]));
            if (!(arg instanceof FunctionArg.SingleFunctionArg)) {
                throw CriteriaUtils.funcArgError(name, arg);
            }
            func = new OneArgFunction(name, arg, returnType);
        }
        return func;
    }

    static SimpleExpression dynamicStringObjectStringFunc(final String name, final SqlSyntax.SymbolSpace space,
                                                          final boolean required,
                                                          final BiFunction<MappingType, String[], Expression> funcRef,
                                                          final MappingType paramType,
                                                          final Consumer<Statement._StringObjectConsumer> consumer,
                                                          final TypeMeta returnType) {
        if (space != SQLs.SPACE) {
            throw CriteriaUtils.errorSymbol(space);
        }

        final List<String> pairList = _Collections.arrayList();
        final CriteriaSupports.StringObjectConsumer clause;
        clause = CriteriaSupports.stringObjectConsumer(required, pairList::add);
        consumer.accept(clause);
        clause.endConsumer();

        final SimpleExpression func;
        if (pairList.size() == 0) {
            func = new ZeroArgFunction(name, true, returnType);
        } else {
            final Expression arg;
            arg = funcRef.apply(paramType, pairList.toArray(new String[0]));
            if (!(arg instanceof FunctionArg.SingleFunctionArg)) {
                throw CriteriaUtils.funcArgError(name, arg);
            }
            func = new OneArgFunction(name, arg, returnType);
        }
        return func;
    }


    static SimpleExpression oneAndRestFunc(String name, TypeMeta returnType, Expression first, Expression... rest) {
        if (!(first instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, first);
        }
        final SimpleExpression func;
        if (rest.length == 0) {
            func = new OneArgFunction(name, first, returnType);
        } else {
            final List<ArmyExpression> argList = _Collections.arrayList(1 + rest.length);
            argList.add((ArmyExpression) first);
            addRestExp(name, argList, rest);
            func = new MultiArgFunctionExpression(name, argList, returnType);
        }
        return func;
    }

    static SimpleExpression oneAndAtLeastFunc(String name, TypeMeta returnType, Expression one, Expression first,
                                              Expression... rest) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, first);
        } else if (!(first instanceof FunctionArg)) {
            throw CriteriaUtils.funcArgError(name, first);
        }
        final List<ArmyExpression> argList = _Collections.arrayList(2 + rest.length);
        argList.add((ArmyExpression) one);
        argList.add((ArmyExpression) first);
        addRestExp(name, argList, rest);
        return new MultiArgFunctionExpression(name, argList, returnType);
    }


    static SimpleExpression twoAndMaxRestForSingleExpFunc(String name, TypeMeta returnType
            , Expression one, Expression two, final int maxRest, Expression... rest) {
        assert maxRest > 0;
        if (rest.length > maxRest) {
            String m = String.format("function[%s] at most take %s argument", name, maxRest);
            throw ContextStack.criteriaError(ContextStack.peek(), m);
        }
        final List<ArmyExpression> argList;
        argList = new ArrayList<>(2 + rest.length);
        appendTwoSingleExp(argList, name, one, two);
        for (Expression arg : rest) {
            if (arg instanceof SqlValueParam.MultiValue) {
                throw CriteriaUtils.funcArgError(name, arg);
            }
            argList.add((ArmyExpression) arg);
        }
        return new MultiArgFunctionExpression(name, argList, returnType);
    }


    static SimpleExpression multiArgFunc(final String name, final TypeMeta returnType, final Expression firstArg,
                                         final Expression... exps) {
        final List<ArmyExpression> argList = new ArrayList<>(1 + exps.length);
        argList.add((ArmyExpression) firstArg);
        for (Expression exp : exps) {
            if (exp instanceof SqlValueParam.MultiValue) {
                throw CriteriaUtils.funcArgError(name, exp);
            }
            argList.add((ArmyExpression) exp);
        }
        return new MultiArgFunctionExpression(name, argList, returnType);
    }

    static SimpleExpression multiNotationFunc(final String name, final TypeMeta returnType, final Predicate<String> validator,
                                              final Expression firstArg, final Expression... restExp) {
        String notation = null;
        if (firstArg instanceof NamedNotation) {
            notation = ((NamedNotation) firstArg).name;
            if (validator.test(notation)) {
                throw errorNamedNotation(name, notation);
            }
        }
        for (Expression exp : restExp) {
            if (exp instanceof NamedNotation) {
                notation = ((NamedNotation) exp).name;
                if (validator.test(notation)) {
                    throw errorNamedNotation(name, notation);
                }
            } else if (notation != null) {
                throw positionalNotationAfterNamedNotation(notation);
            }
        }
        return multiArgFunc(name, returnType, firstArg, restExp);
    }


    static SimpleExpression safeMultiArgFunc(String name, List<ArmyExpression> argList, TypeMeta returnType) {
        return new MultiArgFunctionExpression(name, argList, returnType);
    }

    static SimpleExpression clauseFunc(String name, ArmyFuncClause clause, TypeMeta returnType) {
        return new ArmyFuncClauseFunction(name, clause, returnType);
    }


    static SimplePredicate zeroArgFuncPredicate(final String name) {
        return new ZeroArgFuncPredicate(name, true);
    }

    static SimplePredicate oneArgPredicateFunc(final String name, final Expression argument) {
        if (argument instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, argument);
        }
        return new OneArgFuncPredicate(name, true, argument);
    }

    static SimplePredicate multiArgFuncPredicate(String name, List<Expression> expList) {
        final int size = expList.size();
        final SimplePredicate function;
        switch (size) {
            case 0:
                throw CriteriaUtils.funcArgError(name, expList);
            case 1:
                function = new OneArgFuncPredicate(name, true, expList.get(0));
                break;
            default: {
                final List<ArmyExpression> argList = new ArrayList<>(size);
                appendExpList(argList, expList);
                function = new MultiArgFuncPredicate(name, true, null, argList);
            }
        }
        return function;
    }


    static SimplePredicate twoAndMultiArgFuncPredicate(final String name, final Expression exp1, Expression exp2
            , final List<Expression> expList) {
        return new MultiArgFuncPredicate(name, true, null, twoAndMultiExpList(name, exp1, exp2, expList));
    }


    static SimplePredicate complexArgPredicate(final String name, List<?> argList) {
        return new ComplexArgFuncPredicate(name, true, argList);
    }

    static SimplePredicate oneAndRestFuncPredicate(String name, Expression first, Expression... rest) {
        if (first instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, first);
        }
        final SimplePredicate func;
        if (rest.length == 0) {
            func = new OneArgFuncPredicate(name, true, first);
        } else {
            final List<ArmyExpression> argList = new ArrayList<>(1 + rest.length);
            argList.add((ArmyExpression) first);
            addRestExp(name, argList, rest);
            func = new MultiArgFuncPredicate(name, true, null, argList);
        }
        return func;
    }

    static SimplePredicate complexArgPredicateFrom(final String name, Object firstArg, @Nullable Object... args) {
        final List<Object> argList;
        if (args == null) {
            argList = Collections.singletonList(firstArg);
        } else {
            argList = new ArrayList<>(args.length + 1);
            argList.add(firstArg);
            for (Object arg : args) {
                if (arg != null) {
                    argList.add(arg);
                }
            }
        }
        return new ComplexArgFuncPredicate(name, true, argList);
    }


    static SimpleExpression complexArgFunc(String name, List<?> argList, TypeMeta returnType) {
        return new ComplexArgFuncExpression(name, true, argList, returnType);
    }

    static SimpleExpression complexArgFunc(String name, TypeMeta returnType, Object... args) {
        final List<Object> argList;
        if (args.length == 1) {
            argList = _Collections.singletonList(args[0]);
        } else {
            argList = _Collections.arrayList(args.length);
            Collections.addAll(argList, args);
        }
        return new ComplexArgFuncExpression(name, true, argList, returnType);
    }

    static NamedExpression namedComplexArgFunc(String name, List<?> argList, TypeMeta returnType, String expAlias) {
        return new NamedComplexArgFunc(name, true, argList, returnType, expAlias);
    }

    static SimpleExpression oneArgRowElementFunc(String name, Object one, TypeMeta returnType) {
        if (one instanceof _SelectionGroup._TableFieldGroup || one instanceof SubQuery) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new MultiArgRowElementFunc(name, true, _Collections.singletonList(one), returnType);
    }

    static SimpleExpression twoArgRowElementFunc(String name, Object one, Object two, TypeMeta returnType) {
        if (one instanceof _SelectionGroup._TableFieldGroup || one instanceof SubQuery) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof _SelectionGroup._TableFieldGroup || two instanceof SubQuery) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new MultiArgRowElementFunc(name, true, ArrayUtils.asUnmodifiableList(one, two), returnType);
    }

    static SimpleExpression threeAndRestRowElementFunc(final String name, final TypeMeta returnType, final Object one,
                                                       final Object two, final Object three, final Object... rest) {
        if (one instanceof _SelectionGroup._TableFieldGroup || one instanceof SubQuery) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof _SelectionGroup._TableFieldGroup || two instanceof SubQuery) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (three instanceof _SelectionGroup._TableFieldGroup || three instanceof SubQuery) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        final List<Object> argList = _Collections.arrayList(3 + rest.length);
        argList.add(one);
        argList.add(two);
        argList.add(three);

        for (Object arg : rest) {
            if (arg instanceof _SelectionGroup._TableFieldGroup || arg instanceof SubQuery) {
                throw CriteriaUtils.funcArgError(name, three);
            }
            argList.add(arg);
        }
        return new MultiArgRowElementFunc(name, true, argList, returnType);
    }

    static SimpleExpression rowElementFunc(final String name, final boolean required,
                                           final Consumer<Consumer<Object>> consumer, final TypeMeta returnType) {
        final List<Object> argList = _Collections.arrayList();
        consumer.accept(e -> {
            if (e instanceof _SelectionGroup._TableFieldGroup || e instanceof SubQuery) {
                throw CriteriaUtils.funcArgError(name, e);
            }
            argList.add(e);
        });
        if (argList.size() == 0 && required) {
            throw CriteriaUtils.dontAddAnyItem();
        }
        return new MultiArgRowElementFunc(name, true, argList, returnType);
    }

    static SimpleExpression objectElementFunc(final String name, final boolean required,
                                              final Consumer<Statement._StaticObjectSpaceClause> consumer,
                                              final TypeMeta returnType) {
        final List<Object> argList = _Collections.arrayList();
        final CriteriaSupports.StaticObjectConsumer objectConsumer;
        objectConsumer = CriteriaSupports.staticObjectConsumer(required, e -> {
            if (e instanceof SubQuery) {
                throw CriteriaUtils.funcArgError(name, e);
            }
            argList.add(e);
        });

        consumer.accept(objectConsumer);

        objectConsumer.endConsumer();
        return new ObjectElementFunction(name, true, argList, returnType);

    }

    static SimpleExpression oneArgObjectElementFunc(String name, RowElement one, TypeMeta returnType) {
        return new ObjectElementFunction(name, true, _Collections.singletonList(one), returnType);
    }

    static SimpleExpression objectElementFunc(SqlSyntax.SymbolSpace space, final String name, final boolean required,
                                              final Consumer<Statement._DynamicObjectConsumer> consumer,
                                              final TypeMeta returnType) {
        if (space != SQLs.SPACE) {
            throw CriteriaUtils.funcArgError(name, space);
        }
        final List<Object> argList = _Collections.arrayList();
        final CriteriaSupports.DynamicObjectConsumer objectConsumer;
        objectConsumer = CriteriaSupports.dynamicObjectConsumer(required, e -> {
            if (e instanceof SubQuery) {
                throw CriteriaUtils.funcArgError(name, e);
            }
            argList.add(e);
        });

        consumer.accept(objectConsumer);

        objectConsumer.endConsumer();
        return new ObjectElementFunction(name, true, argList, returnType);

    }


    static OrderByOptionClause orderByOptionClause() {
        return new OrderByOptionClause();
    }

    static OrderByOptionClause orderByOptionClause(CriteriaContext outerContext) {
        return new OrderByOptionClause(outerContext);
    }


    @Deprecated
    static SimpleExpression jsonObjectFunc(String name, Map<String, Expression> expMap, TypeMeta returnType) {
        throw new UnsupportedOperationException();
    }


    static void appendArguments(final @Nullable SQLWords option, final List<? extends ArmySQLExpression> argList,
                                final _SqlContext context) {

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();

        if (option != null) {
            sqlBuilder.append(option.spaceRender());
        }

        final int argSize = argList.size();
        assert argSize > 0;

        for (int i = 0; i < argSize; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }

            argList.get(i).appendSql(context);
        }//for


    }

    static void argumentsToString(final @Nullable SQLWords option, final List<? extends ArmySQLExpression> argList
            , final StringBuilder builder) {

        if (option != null) {
            builder.append(option.spaceRender());
        }

        final int argSize = argList.size();
        assert argSize > 0;
        for (int i = 0; i < argSize; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(argList.get(i));

        }//for


    }


    static void appendComplexArg(final List<?> argumentList, final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        final DialectParser parser = context.parser();
        for (Object o : argumentList) {
            if (o instanceof SQLExpression) {
                if (!(o instanceof ArmySQLExpression)) {
                    throw new CriteriaException(String.format("%s non-army row expression", o));
                }
                ((ArmySQLExpression) o).appendSql(context); // convert to ArmyExpression to avoid non-army expression
            } else if (o == Functions.FuncWord.LEFT_PAREN) {
                sqlBuilder.append(_Constant.LEFT_PAREN);
            } else if (o instanceof SQLWords) {
                if (!(o instanceof SqlSyntax.ArmyKeyWord)) {
                    throw new CriteriaException(String.format("%s non-army words", o));
                }
                sqlBuilder.append(((SQLWords) o).spaceRender());
            } else if (o instanceof String) {
                sqlBuilder.append(_Constant.SPACE);
                parser.identifier((String) o, sqlBuilder);
            } else if (o instanceof SQLIdentifier) { // sql identifier
                sqlBuilder.append(_Constant.SPACE);
                parser.identifier(((SQLIdentifier) o).render(), sqlBuilder);
            } else if (o instanceof ArmyFuncClause) {
                ((ArmyFuncClause) o).appendSql(context);
            } else if (o instanceof MappingType) {
                sqlBuilder.append(_Constant.SPACE);
                parser.typeName((MappingType) o, sqlBuilder);
            } else {
                //no bug,never here
                throw new IllegalArgumentException();
            }

        }//for

    }

    static void complexArgToString(final List<?> argumentList, final StringBuilder builder) {
        for (Object o : argumentList) {
            if (o instanceof SQLExpression) {
                builder.append(o);
            } else if (o == Functions.FuncWord.LEFT_PAREN) {
                builder.append(((SQLWords) o).spaceRender());
            } else if (o instanceof SQLWords) {
                builder.append(_Constant.SPACE)
                        .append(((SQLWords) o).spaceRender());
            } else if (o instanceof SQLIdentifier) { // sql identifier
                builder.append(((SQLIdentifier) o).render());
            } else if (o instanceof String) {
                builder.append(_Constant.SPACE)
                        .append(o);
            } else if (o instanceof ArmyFuncClause) {
                builder.append(o);
            } else if (o instanceof MappingType) {
                builder.append(_Constant.SPACE)
                        .append(o);
            } else {
                //no bug,never here
                throw new IllegalArgumentException();
            }

        }//for
    }


    static void addRestExp(final String name, final List<ArmyExpression> expList, final Expression... rest) {
        for (Expression exp : rest) {
            if (!(exp instanceof FunctionArg)) {
                throw CriteriaUtils.funcArgError(name, exp);
            }
            expList.add((ArmyExpression) exp);
        }
    }


    static List<ArmyExpression> twoExpList(final String name, Expression one, Expression two) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return Arrays.asList((ArmyExpression) one, (ArmyExpression) two);
    }

    static List<ArmyExpression> threeExpList(final String name, Expression one, Expression two, Expression three) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (three instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return Arrays.asList((ArmyExpression) one, (ArmyExpression) two, (ArmyExpression) three);
    }

    static void appendExpList(final List<ArmyExpression> argList, final List<Expression> expList) {
        for (Expression exp : expList) {
            argList.add((ArmyExpression) exp);
        }
    }

    /**
     * @see #twoAndMaxRestForSingleExpFunc(String, TypeMeta, Expression, Expression, int, Expression...)
     */
    static void appendTwoSingleExp(List<ArmyExpression> argList, String name
            , Expression one, Expression two) {
        if (one instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (two instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        argList.add((ArmyExpression) one);
        argList.add((ArmyExpression) two);
    }


    static List<ArmyExpression> twoAndMultiExpList(final String name, final Expression exp1, Expression exp2
            , final List<Expression> expList) {
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<ArmyExpression> argList;
        argList = twoExpList(name, exp1, exp2);
        for (Expression e : expList) {
            argList.add((ArmyExpression) e);
        }
        return argList;
    }

    static List<ArmyExpression> expList(final String name, final List<Expression> expList) {
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgListIsEmpty(name);
        }
        final List<ArmyExpression> argList = new ArrayList<>(expList.size());
        for (Expression exp : expList) {
            argList.add((ArmyExpression) exp);
        }
        return argList;
    }


    static Map<String, Selection> createSelectionMapFrom(final @Nullable CriteriaContext context,
                                                         final List<? extends Selection> selectionList) {
        final Map<String, Selection> map = _Collections.hashMap((int) (selectionList.size() / 0.75F));
        for (Selection s : selectionList) {
            if (map.putIfAbsent(s.alias(), s) == null) {
                continue;
            }
            String m = String.format("Tabular %s %s name[%s] duplication.", SQLFunction.class.getName(),
                    Selection.class.getName(), s.alias());
            final CriteriaException e;
            if (context == null) {
                e = ContextStack.clearStackAndCriteriaError(m);
            } else {
                e = ContextStack.criteriaError(context, m);
            }
            throw e;
        }
        return Collections.unmodifiableMap(map);
    }

    /*-------------------below private method -------------------*/

    /**
     * @see #sixNotationFunc(String, Predicate, Expression, Expression, Expression, Expression, Expression, Expression, TypeMeta)
     */
    @Nullable
    private static String assertNotation(final String funcName, @Nullable String notation, Predicate<String> validator,
                                         final Expression argument) {
        if (argument instanceof NamedNotation) {
            notation = ((NamedNotation) argument).name;
            if (validator.test(notation)) {
                throw errorNamedNotation(funcName, notation);
            }
        } else if (notation != null) {
            throw positionalNotationAfterNamedNotation(notation);
        }
        return notation;
    }


    private static CriteriaException namedNotationIsMultiValue() {
        throw ContextStack.clearStackAndCriteriaError("Named Notation couldn't be %s multi-value parameter/literal.");
    }

    private static CriteriaException errorNamedNotation(String funcName, String notation) {
        String m = String.format("Named Notation[%s] isn't %s()'s Named Notation", notation, funcName);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    private static CriteriaException positionalNotationAfterNamedNotation(String notation) {
        String m = String.format("Couldn't present Positional Notation after Named Notation[%s]", notation);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    interface NoArgFunction {

    }


    interface SimpleFunction {

    }

    interface NoParensFunction extends SimpleFunction {

    }


    enum NullTreatment implements SQLWords {

        RESPECT_NULLS(" RESPECT NULLS"),
        IGNORE_NULLS(" IGNORE NULLS");

        final String spaceWords;

        NullTreatment(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }


    }//NullTreatment

    enum FromFirstLast implements SQLWords {

        FROM_FIRST(" FROM FIRST"),
        FROM_LAST(" FROM LAST");

        final String spaceWords;

        FromFirstLast(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String spaceRender() {
            return this.spaceWords;
        }

        @Override
        public final String toString() {
            return CriteriaUtils.enumToString(this);
        }

    }//FromFirstLast


    static final class NamedNotation extends NonOperationExpression {

        final String name;

        private final ArmyExpression argument;

        /**
         * @see #namedNotation(String, Expression)
         */
        private NamedNotation(String name, ArmyExpression argument) {
            if (argument instanceof SqlValueParam.MultiValue) {
                throw namedNotationIsMultiValue();
            }
            this.name = name;
            this.argument = argument;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.argument.typeMeta();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Database database;
            database = context.parser().dialect().database();
            switch (database) {
                case PostgreSQL: {
                    context.sqlBuilder()
                            .append(_Constant.SPACE)
                            .append(this.name) // TODO validate whether import key word or not.
                            .append(" =>");
                    this.argument.appendSql(context);
                }
                break;
                case MySQL:
                case H2:
                default: {
                    String m = String.format("%s don't support named notation", database);
                    throw new CriteriaException(m);
                }

            }

        }


        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.argument);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NamedNotation) {
                final NamedNotation o = (NamedNotation) obj;
                match = o.name.equals(this.name)
                        && o.argument.equals(this.argument);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(" =>")
                    .append(this.argument)
                    .toString();
        }


    }//NamedNotation

    private static final class NoParensFunctionExpression extends OperationExpression.SqlFunctionExpression
            implements NoParensFunction {

        /**
         * @see #noParensFunc(String, TypeMeta)
         */
        private NoParensFunctionExpression(String name, TypeMeta returnType) {
            super(name, true, returnType); //no parens function must be build-in
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        void argToString(StringBuilder builder) {
            //no-op
        }


    }//NoParensFunction


    private static final class ZeroArgFunction extends OperationExpression.SqlFunctionExpression
            implements NoArgFunction, SimpleFunction {


        /**
         * @see #zeroArgFunc(String, TypeMeta)
         */
        private ZeroArgFunction(String name, boolean buildIn, TypeMeta returnType) {
            super(name, buildIn, returnType);
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        void argToString(StringBuilder builder) {
            //no-op
        }

    }//NoArgFuncExpression


    private static final class OneArgFunction extends OperationExpression.SqlFunctionExpression {

        private final ArmySQLExpression argument;

        private OneArgFunction(String name, SQLExpression argument, TypeMeta returnType) {
            super(name, returnType);
            this.argument = (ArmySQLExpression) argument;
        }


        @Override
        void appendArg(StringBuilder sqlBuilder, final _SqlContext context) {
            this.argument.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.argument);
        }


    }//OneArgFunction

    private static final class TwoArgFunction extends OperationExpression.SqlFunctionExpression {

        private final ArmySQLExpression one;

        private final ArmySQLExpression two;

        /**
         * @see #twoArgFunc(String, SQLExpression, SQLExpression, TypeMeta)
         */
        private TwoArgFunction(String name, SQLExpression one, SQLExpression two, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmySQLExpression) one;
            this.two = (ArmySQLExpression) two;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.two.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    }//TwoArgFunction


    private static final class ThreeArgFunction extends OperationExpression.SqlFunctionExpression {

        private final ArmySQLExpression one;

        private final ArmySQLExpression two;

        private final ArmySQLExpression three;

        /**
         * @see #threeArgFunc(String, SQLExpression, SQLExpression, SQLExpression, TypeMeta)
         */
        private ThreeArgFunction(String name, SQLExpression one, SQLExpression two, SQLExpression three,
                                 TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmySQLExpression) one;
            this.two = (ArmySQLExpression) two;
            this.three = (ArmySQLExpression) three;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three);
        }


    }//ThreeArgFunction


    private static final class FourArgFunction extends OperationExpression.SqlFunctionExpression {

        private final ArmySQLExpression one;

        private final ArmySQLExpression two;

        private final ArmySQLExpression three;

        private final ArmySQLExpression four;

        /**
         * @see #fourArgFunc(String, SQLExpression, SQLExpression, SQLExpression, SQLExpression, TypeMeta)
         */
        private FourArgFunction(String name, SQLExpression one, SQLExpression two, SQLExpression three,
                                SQLExpression four, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmySQLExpression) one;
            this.two = (ArmySQLExpression) two;
            this.three = (ArmySQLExpression) three;
            this.four = (ArmySQLExpression) four;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four);
        }


    }//FourArgFunction

    private static final class FiveArgFunction extends OperationExpression.SqlFunctionExpression {

        private final ArmySQLExpression one;

        private final ArmySQLExpression two;

        private final ArmySQLExpression three;

        private final ArmySQLExpression four;

        private final ArmySQLExpression five;


        /**
         * @see #fiveArgFunc(String, SQLExpression, SQLExpression, SQLExpression, SQLExpression, SQLExpression, TypeMeta)
         */
        private FiveArgFunction(String name, SQLExpression one, SQLExpression two, SQLExpression three,
                                SQLExpression four, SQLExpression five, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmySQLExpression) one;
            this.two = (ArmySQLExpression) two;
            this.three = (ArmySQLExpression) three;
            this.four = (ArmySQLExpression) four;
            this.five = (ArmySQLExpression) five;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.five.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.five);
        }


    }//FiveArgFunction


    private static final class SixArgFunction extends OperationExpression.SqlFunctionExpression {

        private final ArmySQLExpression one;

        private final ArmySQLExpression two;

        private final ArmySQLExpression three;

        private final ArmySQLExpression four;

        private final ArmySQLExpression five;

        private final ArmySQLExpression six;


        /**
         * @see #sixArgFunc(String, SQLExpression, SQLExpression, SQLExpression, SQLExpression, SQLExpression, SQLExpression, TypeMeta)
         */
        private SixArgFunction(String name, SQLExpression one, SQLExpression two, SQLExpression three,
                               SQLExpression four, SQLExpression five, SQLExpression six,
                               TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmySQLExpression) one;
            this.two = (ArmySQLExpression) two;
            this.three = (ArmySQLExpression) three;
            this.four = (ArmySQLExpression) four;

            this.five = (ArmySQLExpression) five;
            this.six = (ArmySQLExpression) six;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.five.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.six.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.five)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.six);
        }


    }//SixArgFunction


    private static final class SevenArgFunction extends OperationExpression.SqlFunctionExpression {

        private final ArmySQLExpression one;

        private final ArmySQLExpression two;

        private final ArmySQLExpression three;

        private final ArmySQLExpression four;

        private final ArmySQLExpression five;

        private final ArmySQLExpression six;

        private final ArmySQLExpression seven;


        /**
         * @see #sevenArgFunc(String, SQLExpression, SQLExpression, SQLExpression, SQLExpression, SQLExpression, SQLExpression, SQLExpression, TypeMeta)
         */
        private SevenArgFunction(String name, SQLExpression one, SQLExpression two, SQLExpression three,
                                 SQLExpression four, SQLExpression five, SQLExpression six,
                                 SQLExpression seven, TypeMeta returnType) {
            super(name, returnType);
            this.one = (ArmySQLExpression) one;
            this.two = (ArmySQLExpression) two;
            this.three = (ArmySQLExpression) three;
            this.four = (ArmySQLExpression) four;

            this.five = (ArmySQLExpression) five;
            this.six = (ArmySQLExpression) six;
            this.seven = (ArmySQLExpression) seven;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.five.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.six.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.seven.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.five)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.six)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.seven);
        }


    }//SevenArgFunction


    private static final class MultiArgFunctionExpression extends OperationExpression.SqlFunctionExpression {


        private final List<? extends ArmySQLExpression> argList;

        private MultiArgFunctionExpression(String name, List<? extends ArmySQLExpression> argList, TypeMeta returnType) {
            super(name, returnType);
            assert argList.size() > 0;
            this.argList = argList;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            FunctionUtils.appendArguments(null, this.argList, context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            FunctionUtils.argumentsToString(null, this.argList, builder);
        }


    }//MultiArgFunctionExpression


    private static final class ZeroArgFuncPredicate extends OperationPredicate.SqlFunctionPredicate
            implements NoArgFunction {

        private ZeroArgFuncPredicate(String name, boolean buildIn) {
            super(name, buildIn);
        }

        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no-op
        }

        @Override
        void argToString(StringBuilder builder) {
            //no-op
        }


    }//NoArgFuncPredicate


    private static final class OneArgFuncPredicate extends OperationPredicate.SqlFunctionPredicate {


        private final ArmyExpression one;

        private OneArgFuncPredicate(String name, final boolean buildIn, Expression one) {
            super(name, buildIn);
            this.one = (ArmyExpression) one;
        }


        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            this.one.appendSql(context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.one);
        }


    }//OneArgFuncPredicate

    /**
     * @see #threeArgPredicateFunc(String, Expression, Expression, Expression)
     */
    private static final class MultiArgFuncPredicate extends OperationPredicate.SqlFunctionPredicate {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        MultiArgFuncPredicate(String name, boolean buildIn, @Nullable SQLWords option, List<ArmyExpression> argList) {
            super(name, buildIn);
            assert argList.size() > 0;
            this.option = option;
            this.argList = argList;
        }


        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            FunctionUtils.appendArguments(this.option, this.argList, context);
        }

        @Override
        void argToString(StringBuilder builder) {
            FunctionUtils.argumentsToString(this.option, this.argList, builder);
        }


    }//MultiArgFuncPredicate


    /**
     * @see ComplexArgFuncExpression
     */
    private static final class ComplexArgFuncPredicate extends OperationPredicate.SqlFunctionPredicate {


        private final List<?> argumentList;

        /**
         * @see #complexArgPredicate(String, List)
         */
        private ComplexArgFuncPredicate(String name, boolean buildIn, List<?> argumentList) {
            super(name, buildIn);
            this.argumentList = argumentList;
        }


        @Override
        void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            FunctionUtils.appendComplexArg(this.argumentList, context);
        }

        @Override
        void argToString(StringBuilder builder) {
            FunctionUtils.complexArgToString(this.argumentList, builder);
        }


    }//ComplexArgFuncPredicate


    /**
     * @see ComplexArgFuncPredicate
     */
    private static class ComplexArgFuncExpression extends OperationExpression.SqlFunctionExpression {
        private final List<?> argList;

        /**
         * @see #complexArgFunc(String, TypeMeta, Object...)
         */
        private ComplexArgFuncExpression(String name, boolean buildIn, List<?> argList, TypeMeta returnType) {
            super(name, buildIn, returnType);
            assert argList.size() > 0;
            this.argList = argList;
        }


        @Override
        final void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            FunctionUtils.appendComplexArg(this.argList, context);
        }

        @Override
        final void argToString(StringBuilder builder) {
            FunctionUtils.complexArgToString(this.argList, builder);
        }


    }//ComplexArgFuncExpression


    private static final class NamedComplexArgFunc extends ComplexArgFuncExpression implements NamedExpression {

        private final String expAlias;

        private NamedComplexArgFunc(String name, boolean buildIn, List<?> argList, TypeMeta returnType, String expAlias) {
            super(name, buildIn, argList, returnType);
            this.expAlias = expAlias;
        }


        @Override
        public String alias() {
            return this.expAlias;
        }


    }//NamedComplexArgFunc


    private static final class ArmyFuncClauseFunction extends OperationExpression.SqlFunctionExpression {

        private final ArmyFuncClause clause;

        /**
         * @see #clauseFunc(String, ArmyFuncClause, TypeMeta)
         */
        private ArmyFuncClauseFunction(String name, ArmyFuncClause clause, TypeMeta returnType) {
            super(name, returnType);
            this.clause = clause;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, _SqlContext context) {
            this.clause.appendSql(context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.clause);
        }


    }//ArmyFuncClauseFunction


    private static final class CaseFunction extends OperationExpression.OperationSimpleExpression
            implements SQLFunction._CaseWhenSpec,
            SQLFunction._CaseFuncWhenClause,
            SQLFunction._StaticCaseThenClause,
            SQLFunction._CaseElseClause,
            CaseWhens,
            SQLFunction._DynamicCaseThenClause,
            SQLFunction._DynamicWhenSpaceClause,
            ArmySQLFunction,
            TypeInfer.DelayTypeInfer {

        private final ArmyExpression caseValue;

        private final CriteriaContext outerContext;

        private List<_Pair<ArmyExpression, ArmyExpression>> expPairList;

        private ArmyExpression whenExpression;

        private ArmyExpression elseExpression;

        private TypeMeta returnType;

        private boolean dynamicWhenSpace;

        private CaseFunction(@Nullable ArmyExpression caseValue) {
            this.caseValue = caseValue;
            this.outerContext = ContextStack.peek();
        }

        @Override
        public String name() {
            return "CASE";
        }

        @Override
        public boolean isDelay() {
            final TypeMeta returnType = this.returnType;
            return returnType instanceof TypeMeta.DelayTypeMeta && ((TypeMeta.DelayTypeMeta) returnType).isDelay();
        }

        @Override
        public MappingType typeMeta() {
            final TypeMeta returnType = this.returnType;
            final MappingType t;
            if (returnType instanceof MappingType) {
                t = (MappingType) returnType;
            } else {
                t = returnType.mappingType();
            }
            return t;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final int pairSize;
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null || (pairSize = expPairList.size()) == 0) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(" CASE");

            final ArmyExpression caseValue = this.caseValue;
            if (caseValue != null) {
                caseValue.appendSql(context);
            }
            _Pair<ArmyExpression, ArmyExpression> pair;
            for (int i = 0; i < pairSize; i++) {
                pair = expPairList.get(i);

                sqlBuilder.append(" WHEN");
                pair.first.appendSql(context);
                sqlBuilder.append(" THEN");
                pair.second.appendSql(context);

            }

            final ArmyExpression elseExpression = this.elseExpression;
            if (elseExpression != null) {
                sqlBuilder.append(" ELSE");
                elseExpression.appendSql(context);
            }

            sqlBuilder.append(" END");

        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            final int pairSize;
            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null || (pairSize = expPairList.size()) == 0) {
                return super.toString();
            }
            builder.append(" CASE");

            final ArmyExpression caseValue = this.caseValue;
            if (caseValue != null) {
                builder.append(caseValue);
            }
            _Pair<ArmyExpression, ArmyExpression> pair;
            for (int i = 0; i < pairSize; i++) {
                pair = expPairList.get(i);

                builder.append(" WHEN")
                        .append(pair.first)
                        .append(" THEN")
                        .append(pair.second);

            }

            final ArmyExpression elseExpression = this.elseExpression;
            if (elseExpression != null) {
                builder.append(" ELSE")
                        .append(elseExpression);
            }
            return builder.append(" END")
                    .toString();
        }

        @Override
        public _CaseElseClause whens(Consumer<CaseWhens> consumer) {
            consumer.accept(this);
            return this;
        }

        @Override
        public CaseFunction when(final @Nullable Expression expression) {
            if (this.whenExpression != null) {
                throw ContextStack.criteriaError(this.outerContext, "last when clause not end.");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if ((!(expression instanceof ArmyExpression))) {
                throw ContextStack.nonArmyExp(this.outerContext);
            }
            this.whenExpression = (ArmyExpression) expression;
            return this;
        }

        @Override
        public CaseFunction when(Supplier<Expression> supplier) {
            return this.when(supplier.get());
        }

        @Override
        public CaseFunction when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate) {
            return this.when(valueOperator.apply(predicate));
        }

        @Override
        public CaseFunction when(Function<Expression, Expression> valueOperator, Expression expression) {
            return this.when(valueOperator.apply(expression));
        }

        @Override
        public CaseFunction when(Function<Object, Expression> valueOperator, Object value) {
            return this.when(valueOperator.apply(value));
        }

        @Override
        public <T> CaseFunction when(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return this.when(valueOperator.apply(getter.get()));
        }

        @Override
        public <T> CaseFunction when(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> valueOperator, T value) {
            return this.when(expOperator.apply(valueOperator, value));
        }

        @Override
        public <T> CaseFunction when(BetweenValueOperator<T> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> operator, T firstValue,
                                     SqlSyntax.WordAnd and, T secondValue) {
            return this.when(expOperator.apply(operator, firstValue, and, secondValue));
        }

        @Override
        public CaseFunction when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
            return this.when(expOperator.apply(first, and, second));
        }

        @Override
        public CaseFunction ifWhen(Consumer<_DynamicWhenSpaceClause> consumer) {
            this.dynamicWhenSpace = true;
            consumer.accept(this);
            this.dynamicWhenSpace = false;
            return this;
        }

        @Override
        public _SqlCaseThenClause space(Expression expression) {
            if (!this.dynamicWhenSpace) {
                throw ContextStack.criteriaError(this.outerContext, "duplication ifWhen space.");
            }
            this.dynamicWhenSpace = false;
            return this.when(expression);
        }

        @Override
        public _SqlCaseThenClause space(Supplier<Expression> supplier) {
            return this.space(supplier.get());
        }

        @Override
        public _SqlCaseThenClause space(UnaryOperator<IPredicate> valueOperator, IPredicate predicate) {
            return this.space(valueOperator.apply(predicate));
        }

        @Override
        public _SqlCaseThenClause space(Function<Expression, Expression> valueOperator, Expression expression) {
            return this.space(valueOperator.apply(expression));
        }

        @Override
        public _SqlCaseThenClause space(Function<Object, Expression> valueOperator, Object value) {
            return this.space(valueOperator.apply(value));
        }

        @Override
        public <T> _SqlCaseThenClause space(Function<T, Expression> valueOperator, Supplier<T> getter) {
            return this.space(valueOperator.apply(getter.get()));
        }

        @Override
        public <T> _SqlCaseThenClause space(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                            BiFunction<SimpleExpression, T, Expression> valueOperator, T value) {
            return this.space(expOperator.apply(valueOperator, value));
        }

        @Override
        public <T> _SqlCaseThenClause space(BetweenValueOperator<T> expOperator,
                                            BiFunction<SimpleExpression, T, Expression> operator, T firstValue,
                                            SqlSyntax.WordAnd and, T secondValue) {
            return this.space(expOperator.apply(operator, firstValue, and, secondValue));
        }

        @Override
        public _SqlCaseThenClause space(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
            return this.space(expOperator.apply(first, and, second));
        }

        @Override
        public CaseFunction then(final @Nullable Expression expression) {
            final ArmyExpression whenExp = this.whenExpression;
            if (whenExp == null) {
                throw ContextStack.criteriaError(this.outerContext, "no when clause");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!(expression instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.outerContext);
            }
            List<_Pair<ArmyExpression, ArmyExpression>> pairList = this.expPairList;
            if (pairList == null) {
                pairList = _Collections.arrayList();
                this.expPairList = pairList;
            } else if (!(pairList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            pairList.add(_Pair.create(whenExp, (ArmyExpression) expression));
            this.whenExpression = null; // clear for next
            return this;
        }

        @Override
        public CaseFunction then(Supplier<Expression> supplier) {
            return this.then(supplier.get());
        }


        @Override
        public CaseFunction then(UnaryOperator<IPredicate> valueOperator, IPredicate value) {
            return this.then(valueOperator.apply(value));
        }

        @Override
        public CaseFunction then(Function<Expression, Expression> valueOperator, Expression value) {
            return this.then(valueOperator.apply(value));
        }

        @Override
        public CaseFunction then(Function<Object, Expression> valueOperator, Object value) {
            return this.then(valueOperator.apply(value));
        }

        @Override
        public <T> CaseFunction then(Function<T, Expression> valueOperator, Supplier<T> supplier) {
            return this.then(valueOperator.apply(supplier.get()));
        }

        @Override
        public <T> CaseFunction then(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> valueOperator, T value) {
            return this.then(expOperator.apply(valueOperator, value));
        }


        @Override
        public _CaseEndClause elseValue(final @Nullable Expression expression) {
            if (this.expPairList == null) {
                throw noWhenClause();
            } else if (this.whenExpression != null) {
                throw lastWhenClauseNotEnd();
            } else if (this.elseExpression != null) {
                throw ContextStack.criteriaError(this.outerContext, "duplicate else clause.");
            } else if (expression == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!(expression instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.outerContext);
            }
            this.elseExpression = (ArmyExpression) expression;
            return this;
        }

        @Override
        public _CaseEndClause elseValue(Supplier<Expression> supplier) {
            return this.elseValue(supplier.get());
        }


        @Override
        public _CaseEndClause elseValue(UnaryOperator<IPredicate> valueOperator, IPredicate value) {
            return this.elseValue(valueOperator.apply(value));
        }

        @Override
        public _CaseEndClause elseValue(Function<Expression, Expression> valueOperator, Expression value) {
            return this.elseValue(valueOperator.apply(value));
        }

        @Override
        public _CaseEndClause elseValue(Function<Object, Expression> valueOperator, Object value) {
            return this.elseValue(valueOperator.apply(value));
        }

        @Override
        public <T> _CaseEndClause elseValue(Function<T, Expression> valueOperator, Supplier<T> supplier) {
            return this.elseValue(valueOperator.apply(supplier.get()));
        }

        @Override
        public <T> _CaseEndClause elseValue(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                            BiFunction<SimpleExpression, T, Expression> valueOperator, T value) {
            return this.elseValue(expOperator.apply(valueOperator, value));
        }

        @Override
        public _CaseEndClause ifElse(Supplier<Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression != null) {
                this.elseValue(expression);
            }
            return this;
        }

        @Override
        public <T> _CaseEndClause ifElse(Function<T, Expression> valueOperator, Supplier<T> getter) {
            final T operand;
            operand = getter.get();
            if (operand != null) {
                this.elseValue(valueOperator.apply(operand));
            }
            return this;
        }

        @Override
        public <K, V> _CaseEndClause ifElse(Function<V, Expression> valueOperator, Function<K, V> function, K key) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.elseValue(valueOperator.apply(value));
            }
            return this;
        }

        @Override
        public <T> _CaseEndClause ifElse(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                         BiFunction<SimpleExpression, T, Expression> valueOperator, Supplier<T> getter) {
            final T value;
            if ((value = getter.get()) != null) {
                this.elseValue(expOperator.apply(valueOperator, value));
            }
            return this;
        }

        @Override
        public <K, V> _CaseEndClause ifElse(ExpressionOperator<SimpleExpression, V, Expression> expOperator,
                                            BiFunction<SimpleExpression, V, Expression> valueOperator,
                                            Function<K, V> function, K key) {
            final V value;
            if ((value = function.apply(key)) != null) {
                this.elseValue(expOperator.apply(valueOperator, value));
            }
            return this;
        }

        @Override
        public Expression end() {
            return this.endCaseFunction(TextType.INSTANCE);
        }


        @Override
        public Expression end(final @Nullable TypeInfer type) {
            if (type == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            final TypeMeta typeMeta;
            if (type instanceof MappingType) {
                typeMeta = (MappingType) type;
            } else if (type instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) type).isDelay()) {
                typeMeta = CriteriaSupports.unaryInfer((TypeInfer.DelayTypeInfer) type, Expressions::identityType);
            } else if (type instanceof TypeMeta.DelayTypeMeta) {
                typeMeta = (TypeMeta) type;
            } else {
                typeMeta = type.typeMeta().mappingType();
            }
            return this.endCaseFunction(typeMeta);
        }

        private Expression endCaseFunction(final TypeMeta type) {
            if (this.whenExpression != null) {
                throw lastWhenClauseNotEnd();
            } else if (this.returnType != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            final List<_Pair<ArmyExpression, ArmyExpression>> expPairList = this.expPairList;
            if (expPairList == null) {
                throw noWhenClause();
            } else if (expPairList instanceof ArrayList) {
                this.expPairList = _Collections.unmodifiableList(expPairList);
            } else {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            this.returnType = type;
            return this;
        }

        private CriteriaException noWhenClause() {
            return ContextStack.criteriaError(this.outerContext, "Not found any when clause.");
        }

        private CriteriaException lastWhenClauseNotEnd() {
            return ContextStack.criteriaError(this.outerContext, "current when clause not end");
        }


    }//CaseFunc


    static final class OrderByOptionClause
            extends OrderByClause.OrderByClauseClause<Statement._SimpleOrderByCommaClause, Item>
            implements ArmyFuncClause,
            Statement._SimpleOrderByClause,
            Statement._SimpleOrderByCommaClause {

        /**
         * @see #orderByOptionClause()
         */
        private OrderByOptionClause() {
            super(ContextStack.peek());
        }

        private OrderByOptionClause(CriteriaContext outerContext) {
            super(outerContext);
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final List<? extends SortItem> sortItemList;
            sortItemList = this.orderByList();
            final int itemSize;
            if ((itemSize = sortItemList.size()) == 0) {
                return;
            }
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_ORDER_BY);
            for (int i = 0; i < itemSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                ((ArmySortItem) sortItemList.get(i)).appendSql(context);

            }

        }


    }//OrderByOptionClause


    private static final class MultiArgRowElementFunc extends OperationExpression.SqlFunctionExpression {

        private final List<Object> argList;

        private MultiArgRowElementFunc(String name, boolean buildIn, List<Object> argList, TypeMeta returnType) {
            super(name, buildIn, returnType);
            this.argList = argList;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<Object> argList = this.argList;
            final int argSize;
            argSize = argList.size();

            Object arg;
            MappingType type;
            for (int i = 0; i < argSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }

                arg = argList.get(i);
                if (arg == null) {
                    sqlBuilder.append(_Constant.NULL);
                } else if (!(arg instanceof RowElement)) {
                    if (arg instanceof String) {
                        type = NoCastIntegerType.INSTANCE;
                    } else if (arg instanceof Integer) {
                        type = NoCastIntegerType.INSTANCE;
                    } else if ((type = _MappingFactory.getDefaultIfMatch(arg.getClass())) == null) {
                        throw _Exceptions.notFoundMappingType(arg);
                    }
                    context.appendLiteral(type, arg);
                } else if (arg instanceof ArmySQLExpression) {
                    ((ArmySQLExpression) arg).appendSql(context);
                } else if (arg instanceof _SelectionGroup._TableFieldGroup || arg instanceof SubQuery) {
                    String m = String.format("function[%s] don't support %s", this.name, arg);
                    throw new CriteriaException(m);
                } else if (arg instanceof SelectionGroups.RowElementGroup) {
                    ((SelectionGroups.RowElementGroup) arg).appendRowElement(sqlBuilder, context);
                } else {
                    throw _Exceptions.unknownRowElement(arg);
                }

            }//for

        }

        @Override
        void argToString(final StringBuilder builder) {
            final List<Object> argList = this.argList;
            final int argSize;
            argSize = argList.size();

            Object arg;
            for (int i = 0; i < argSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }

                arg = argList.get(i);
                if (arg == null) {
                    builder.append(_Constant.NULL);
                } else if (!(arg instanceof RowElement)) {
                    builder.append(_Constant.SPACE)
                            .append(arg);
                } else if (arg instanceof ArmySQLExpression) {
                    builder.append(arg);
                } else if (arg instanceof _SelectionGroup._TableFieldGroup || arg instanceof SubQuery) {
                    String m = String.format("function[%s] don't support %s", this.name, arg);
                    throw new CriteriaException(m);
                } else if (arg instanceof SelectionGroups.RowElementGroup) {
                    ((SelectionGroups.RowElementGroup) arg).rowElementToString(builder);
                } else {
                    throw _Exceptions.unknownRowElement(arg);
                }

            }//for
        }


    }//MultiArgRowElementFunc

    private static final class ObjectElementFunction extends OperationExpression.SqlFunctionExpression {

        private final List<Object> argList;

        private ObjectElementFunction(String name, boolean buildIn, List<Object> argList, TypeMeta returnType) {
            super(name, buildIn, returnType);
            this.argList = argList;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<Object> argList = this.argList;
            final int argSize;
            argSize = argList.size();

            Object arg;
            MappingType type;
            for (int i = 0; i < argSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                arg = argList.get(i);
                if (arg == null) {
                    sqlBuilder.append(_Constant.NULL);
                } else if (!(arg instanceof RowElement)) {
                    if (arg instanceof String) {
                        type = NoCastTextType.INSTANCE;
                    } else if (arg instanceof Integer) {
                        type = NoCastIntegerType.INSTANCE;
                    } else if ((type = _MappingFactory.getDefaultIfMatch(arg.getClass())) == null) {
                        throw _Exceptions.notFoundMappingType(arg);
                    }
                    context.appendLiteral(type, arg);
                } else if (arg instanceof ArmySQLExpression) {
                    ((ArmySQLExpression) arg).appendSql(context);
                } else if (arg instanceof SelectionGroups.ObjectElementGroup) {
                    ((SelectionGroups.ObjectElementGroup) arg).appendObjectElement(sqlBuilder, context);
                } else {
                    throw _Exceptions.unknownRowElement(arg);
                }

            }//for

        }

        @Override
        void argToString(final StringBuilder builder) {
            final List<Object> argList = this.argList;
            final int argSize;
            argSize = argList.size();

            Object arg;
            for (int i = 0; i < argSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                arg = argList.get(i);
                if (arg == null) {
                    builder.append(_Constant.NULL);
                } else if (!(arg instanceof RowElement)) {
                    builder.append(_Constant.SPACE)
                            .append(arg);
                } else if (arg instanceof ArmySQLExpression) {
                    builder.append(arg);
                } else if (arg instanceof SelectionGroups.ObjectElementGroup) {
                    ((SelectionGroups.ObjectElementGroup) arg).objectElementToString(builder);
                } else {
                    throw _Exceptions.unknownRowElement(arg);
                }

            }//for
        }


    }//JsonObjectFunction


}
