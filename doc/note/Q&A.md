1. 为什么不支持 sub query insert?
   1. 不能保证 child domain 的 id, parent DiscriminatorValue 和 有创建器的字段进行控制,
   2. application developer 容易犯错.
   3. 相关的功能可以用先查再插入的方式来代替.

2. 为什么 io.army.criteria.Expression 以 Function 为参数的方法都没有 ifXxx 方法?
   * 有 if 的方法都是配合 statement api 的 ifAnd 方法 ,所以如果要用 function 则直接用 statement api r ifAnd 方法即可.

3. 为什么 batch update / delete 不支持 Collection 全名参数?
   * 因为 集合的的元素不确定,无法生成固定数量的 占位符 '?'

4. 为什么所有 非 batch update 都只有 ifSet(List<FieldMeta<?,?> fieldList,List<Expression<?>> valueList);
而没有 ifSet(Function<List<FieldMeta<?,?>> fieldList,Function<List<Expression<?>>> valueList)?
   * 因为 set 子句必须一一对应而,而 有 Function 的 ifSet 方法对 field 和 value 放在两方法中 很难一一对应.
   * 两个参数都是 List 可以通过 for 循环保证 一一对应.
