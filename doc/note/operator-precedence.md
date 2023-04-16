## io.army.criteria.Expression 操作符 优先级设计

### 原则

1. () 操作符被当作 像字面量一样的 简单表达式.
2. 一元操作符的优先级高于二元操作符 . (不一定符合相应的数据库,这是由于受编程语言的限制,army 总是可以输出必要的
   括号来保证优先级)
3. 不产生 io.army.criteria.IPredicate 的 操作符优先级高于 产生 io.army.criteria.IPredicate 的操作符. (
   比较符合多数数据库)
