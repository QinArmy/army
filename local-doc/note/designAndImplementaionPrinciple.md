
## 设计特性和特性的实现原则
前言 
Army 设计了一系列的特性,Army 的开发者在每次发布前要检查特性清单和特性的实现细节,也就是
1. 必须保证特性
2. 必做遵守实现原则
否则会给 Army 的使用者带来严重的数据灾难.

### Insert 部分
#### standard Insert
1. 当 ParentTableMeta primary key 有 PostFieldGenerator 时 ,
    * primary key insertable = false,
    * child primary key insertable = true,
    * child primary key ParamWrapper's paramMeta 为 FieldMeta,ParamWrapper's value 为 ReadOnlyWrapper.
    * child primary key 设置参数时 通过 ReadOnlyWrapper 获取 parent 的 primary key value.
    * InsertSQLExecutor 在设置参数时要完全实现以上特征.
    
2. 当 TableMeta  的 FieldMeta 有 FieldCodec 时 ,
    * ParamWrapper's paramMeta 为 FieldMeta ,以便 InsertSQLExecutor 能从 SessionFactory 
    中获取 FieldCodec,并将编码值作为参数设置到 PreparedStatement 中.
    * InsertSQLExecutor 在设置参数时要完全实现以上特征.

3. session 的 insert 方法不返回 插入行数,因为 判断插入行数应该由框架来做,若不符合预期必抛出异常,若有事务则设置 rollbackOnly.

4. 批量 insert 底层使用 java.sql.PreparedStatement.addBatch,
    * 只能使用 domain 插入
    * DomainBatchSQLWrapper's domainWrapperList 为 BeanWrapper 即可,不必为 DomainWrapper.
    * DomainBatchSQLWrapper's BeanWrapper 的底层对象必须为 DomainBatchSQLWrapper's tableMeta 的对应实例.

5. standard insert 不支持 字段的子查询,但支持整体的子查询.
