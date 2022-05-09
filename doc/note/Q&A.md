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
7. 为什么 io.army.criteria.Expression 要设计 ifEqual(Supplier<Object>) 和 ifEqual(Function<String, Object> function, String
   keyName) 方法, 而放弃 ifEqual(@Nullable Object) 方法?
   1. 首先在一部分场景 ifEqual(@Nullable Object) 确实能替换两者,似乎理方便,但 ifEqual(@Nullable Object) 参数是 Object, 而我们总是得设计 一个 ifEqual(
      Function<C,Object>) 方法,大多数情况下java 都能识别这两个方法,但谨慎的原则考虑还是要避免重载 Object 参数的方法.
   2. 在别一部分场景里 我们不能直接 使用 java bean 的 getXxx() 方法和 Map.get(String) 方法得到条件,而是需要一些简单的计算,在这个场景里 ifEqual(@Nullable Object)
      通过 在 语句的 之前准备好条件或者定义一个新方法,可 Supplier<Object> 可依赖于 lambda 和方法引用, 两者虽可替换,但从 sql style 和 代码相关性而言,lambda 和 方法引用 更胜一筹.
   3. equalExp(Function<C, Expression>) 和 equalExp(Supplier<Expression>) 方法其实也是为避免重载 equal(Object) 方法.其它操作符同理.

8. 为什么 io.army.criteria.Expression 总要多设计一个 equalLiteral(Object ) 方法 , equal(Object ) 不够用吗?
   1. equalLiteral(Object ) 输出的是 字面量,equal(Object ) 输出的占位任 '?' ,army 不认为在任何场景下 application 开发者都想输出占位符.
   2. 从 sql style 的视角,既能输出 '?' 也能输出字面量才够 sql style.
   3. 从 driver 的角度 没有字符串这个能产生 sql 注入的类型的场景中,没有 '?' 执行的是 database 的文本协议而不是 prepare 协议,在一部分场景来说是更快的, 尤其是网络效率高.
   4. 从jdbc 多语句的角度考虑,没有 '?' 你才能使用多语句的 jdbc api,当然如果使用的是 jdbd 则不关心这个问题.

9. 为什么 io.army.criteria.Expression 总有一个 equalNamed(String) 方法,这个做什么的?
   1. 这个方法是为批量 dml 语句设计的,这个方法能把批量语句的 代码变得简洁.
   2. io.army.criteria.TableField 的 equalNamed() 也是同理.

10. 为什么 不支持 application 开发者重写 默认 mapping type?
   1. army 的默认 mapping 已经足够满足大多数场景.
   2. army 的默认 mapping 是内置实现相对安全.
   3. 如果支持重写默认 mapping ,会给新入开发团队的的开发者造成认知混乱,对于项目管理和每一位开发者来说都是不利的.

11. 为什么 io.army.criteria.impl.JoinableUpdate 不能持有 CriteriaContext ?
   * NestedItems 不能持有 CriteriaContext,只能持有 List
    






