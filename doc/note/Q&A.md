1. 为什么不支持 sub query insert?
    1. 不能保证 child domain 的 id, parent DiscriminatorValue 和 有创建器的字段进行控制,
    2. application developer 容易犯错.
    3. 相关的功能可以用先查再插入的方式来代替.

2. 为什么 io.army.criteria.Expression 以 Function 为参数的方法都没有 ifXxx 方法?
    * ifXxx 的方法都是配合 statement api 的 ifAnd 方法 ,所以如果要用 function 则直接用 statement api 的 ifAnd 方法即可.

3. 为什么 batch update / delete 不支持 Collection 全名参数?
    * 因为 集合的的元素不确定,无法生成固定数量的 占位符 '?'

4. 为什么所有 非 batch update 都只有 ifSet(List&lt; FieldMeta&lt; ?,?> fieldList,List&lt; Expression> valueList); 而没有 ifSet(
   Function&lt; List&lt;
   FieldMeta&lt; ?,?>> fieldList,Function&lt; List&lt; Expression>> valueList)?
    * 因为 set 子句必须一一对应而,而 有 Function 的 ifSet 方法对 field 和 value 放在两方法中 很难一一对应.
    * 两个参数都是 List 可以通过 for 循环保证 一一对应.


5. 为什么不提供一个 SQLs.optimizingParam() 方法?
    * 因为多余,如果需要这个一个方法还不如直接调用 SQLs.literal() 方法.


6. 为什么不在 语法 api 的 where 子句 提供 or 子句 而只提供 and 子句?
    * 因为输出的 sql 不会有不必要的括号,hibernate 就提供了 or 子句,输出的 sql 才会 那么多不必要的括号.
    * 因为这样设计可以把整个框架的结构变简单,比如 追加 visible 列时总是有效且不会出错.
    * or 子句只能在 io.army.criteria.IPredicate 提供,这样可以保证 or 子句始终被括号包裹成一个整体.


7. 为什么 io.army.criteria.Expression 总要多设计一个 equalLiteral(Object ) 方法 , equal(Object ) 不够用吗?
    * equalLiteral(Object ) 输出的是 字面量,equal(Object ) 输出的占位任 '?' ,army 不认为在任何场景下 application 开发者都想输出占位符.
    * 从 sql style 的视角,既能输出 '?' 也能输出字面量才够 sql style.
    * 从 driver 的角度 没有字符串这个能产生 sql 注入的类型的场景中,没有 '?' 执行的是 database 的文本协议而不是 prepare 协议,在一部分场景来说是更快的, 尤其是网络效率高.
    * 从jdbc 多语句的角度考虑,没有 '?' 你才能使用多语句的 jdbc api,当然如果使用的是 jdbd 则不关心这个问题.


8. 为什么 io.army.criteria.Expression 总有一个 equalNamed(String) 方法,这个做什么的?
    * 这个方法是为批量 dml 语句设计的,这个方法能把批量语句的 代码变得简洁.
    * io.army.criteria.TableField 的 equalNamed() 也是同理.


9. 为什么 不支持 application 开发者重写 默认 mapping type?
    * army 的默认 mapping 已经足够满足大多数场景.
    * army 的默认 mapping 是内置实现,相对安全.
    * 如果支持重写默认 mapping ,会给新入开发团队的的开发者造成认知混乱,对于项目管理和每一位开发者来说都是不利的.


10. 为什么 io.army.criteria.impl.JoinableUpdate 不能持有 CriteriaContext ?
    * NestedItems 不能持有 CriteriaContext,只能持有 List


11. 什么是 SQL style 的神(这里的神是形神兼备的神)?
    * sql style 不是 sql 本身
    * sql style 是 sql 子句的有序组合
    * sql style 是弱类型的,为什么敢于是弱类型,因为:
        * sql 是弱类型
        * java 是强类型语言,已经提供了一定程度类型安全
        * 如果你使用 Map 作为 criteria 对象,那么你已经选择放弃类型安全,不是吗?
    * sql style 追求代码的整体性,即不被 if 或 for 分割
    * sql style 追求更高的可读性
    * sql style 追求明确的语义,因此 batch update 和 update 分别由不同的方法提供.


12. 为什么 with clause 实现没有设计成 顶层 基类?
    * with clause 实现需要 CriteriaContext
    * NestedItems 没有 CriteriaContext

13. 为什么 io.army.criteria.Expression 要设计 ifEqual(Supplier&lt; Object>) 和 ifEqual(Function&lt; String, Object> function,
    String
    keyName) 方法, 而放弃 ifEqual(@Nullable Object) 方法?
    * 首先在一部分场景 ifEqual(@Nullable Object) 确实能替换两者,似乎理方便,但 ifEqual(@Nullable Object) 参数是 Object, 而我们总是得设计 一个 ifEqual(
      Function&lt; C,Object>) 方法,大多数情况下java 都能识别这两个方法,但谨慎的原则考虑还是要避免重载 Object 参数的方法.
    * 在别一部分场景里 我们不能直接使用 java bean 的 getXxx() 方法和 Map.get(String) 方法得到条件,而是需要一些简单的计算,在这个场景里 ifEqual(@Nullable Object)通过 在
      语句的 之前准备好条件或者定义一个新方法,可 Supplier 可依赖于 lambda 和方法引用, 两者虽可替换,但从 sql style 和 代码相关性而言,lambda 和 方法引用 更胜一筹.
    * equalExp(Function&lt; C, Expression>) 和 equalExp(Supplier) 方法其实也是为避免重载 equal(Object) 方法.其它操作符同理.

14. 为什么 Criteria api 不再提供 List&lt;T>, Supplier&lt;List&lt;T>> 和 Function&lt;List&lt;T>> 而是提供 Consumer&lt;Consumer&lt;
    T>>
    和 BiConsumer&lt;C,Consumer&lt; T>> ?
    * 保证运行时 ThreadLocal 的 CriteriaContext 是当前的
    * 保证运行时 SQLs.ref(derivedTable,fieldName) 能正确运行

15. 为什么提供 SQL 函数的的静态方法使用 Expression 参数 而不使用 Object 参数?
    * 使用 Expression 方便扩展重载

16. 为什么 Postgre insert 不支持 DEFAULT VALUES 子句?
    * 因为 army 管理 createTime ,updateTime,Generator field 等 field.

17. 为什么 standard insert parent 部分要预留 CT 类型变量?
    * 因为 standard 将来要支持 with cte 子句.

18. 为什么 with 子句 的 CTE 创建没有采用常规的 static 方法而采用了构造器?
    * 常规的表态方法在实现层面总是对 CTE 有外部上下文存疑.
    * 由于确定了 CTE 的外部上下文能更好的实现 RECURSIVE 引用.

19. 为什么 static with clause 只能出现在 primary statement api 中?
    * 如果 sub statement 出现 static with clause 将影响整个 sql api 代码的可读性,这违背了 army 的价值观

20. 为什么 删除 SQLs.namedParam(DataField),SQlLs.namedNullableParam(DataField),SQlLs.namedLiteral(DataField)
    ,SQlLs.namedNullableLiteral(DataField) method?
    * 因为 加上 set(F field,Function) 和 set(F field,BiFunction) 后, jvm 无法准确识别,容易出问题.

21. 为什么 where 子句 and 子句 set 子句要设计那么多的，那么长的 函数接口参数?
    * 因为只这样设计成函数类型的参数能才能将程序行为更好的组合.
    * 能消除方法的圆括号,使代码更简洁
