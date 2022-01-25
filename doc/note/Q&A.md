1. 为什么不支持 sub query insert?
   1. 不能保证 child domain 的 id, parent DiscriminatorValue 和 有创建器的字段进行控制,
   2. application developer 容易犯错.
   3. 相关的功能可以用先查再插入的方式来代替.

2. 为什么 io.army.criteria.Expression 以 Function 为参数的方法都没有 ifXxx 方法?
   * ifXxx 的方法都是配合 statement api 的 ifAnd 方法 ,所以如果要用 function 则直接用 statement api 的 ifAnd 方法即可.

3. 为什么 batch update / delete 不支持 Collection 全名参数?
   * 因为 集合的的元素不确定,无法生成固定数量的 占位符 '?'

4. 为什么所有 非 batch update 都只有 ifSet(List<FieldMeta<?,?> fieldList,List<Expression> valueList); 而没有 ifSet(Function<List<
   FieldMeta<?,?>> fieldList,Function<List<Expression>> valueList)?
   * 因为 set 子句必须一一对应而,而 有 Function 的 ifSet 方法对 field 和 value 放在两方法中 很难一一对应.
   * 两个参数都是 List 可以通过 for 循环保证 一一对应.

5. 为什么不提供一个 SQLs.optimizingParam() 方法?
   * 因为多余,如果需要这个一个方法还不如直接调用 SQLs.literal() 方法.

6. 为什么不在 语法 api 的 where 子句 提供 or 子句 而只提供 and 子句?
   * 因为输出的 sql 不会有不必要的括号,hibernate 就提供了 or 子句,输出的 sql 才会 那么多不必要的括号.
   * 因为这样设计可以把整个框架的结构变简单,比如 追加 visible 列时总是有效且不会出错.
   * or 子句只能在 io.army.criteria.IPredicate 提供,这样可以保证 or 子句始终被括号包裹成一个整体.