package io.army.criteria;

@Deprecated
public interface SortItems {

    SortItems sortItem(Expression exp);

    SortItems sortItem(Expression exp, Statement.AscDesc ascDesc);

    SortItems sortItem(Expression exp1, Expression exp2);

    SortItems sortItem(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2);

    SortItems sortItem(Expression exp1, Expression exp2, Statement.AscDesc ascDesc2);

    SortItems sortItem(Expression exp1, Statement.AscDesc ascDesc1, Expression exp2, Statement.AscDesc ascDesc2);


}
