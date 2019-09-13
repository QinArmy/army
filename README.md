

Army 是一个新型的持久层框架, Army 吸取了 Hibernate 和 Jooq 的优点并舍弃其缺点再加上独特的设计而成。

----

 Army 的起源    
----
Army 吸收 Hibernate 和 Jooq 的优点去除其缺点加上新的设计和约定而成.


* 使命 : 解决Java 持久化方案不够易用,简单,易于维护,高效编码的问题.
* 价值观: 简单易用,贴近 SQL ,易于阅读,高效,拥抱新技术.
* 愿景: 成为 Java 持久化方案的更好选择.

实现愿景的步骤:

1. 发布一个可在生产环境使用的 Java 持久化方案.
2. Army 成为 apache 顶级项目
3. 致力于使 Army 成为 Java 持久化方案的更好选择.

初始成员:马军玲(花名:索隆),刘彬(花名:安西教练)

新成员加入准则: 所有已在成员一致同意。


Army 拥有以下几个特性:

* 不支持缓存.
* 半 orm (仅支持单表映射和表继承,不支持关系映射) 
* 动态插入(domain)
* 只使用API更新
* 复杂静态 sql (使用 xml)
* 支持 Java 8+
* 默认类型映射 
* 不支持没有实现 CodeEnum 的枚举
* 乐观锁
* 逻辑删除(visible 字段)
* 不支持联合主键
* 不支持复杂映射



Army 以下强制规则:
* 所有表必须有 primary key 且 必须命名为 id
* 所有表的 乐观锁的名命 必须为 version
* 所有表的必须有 逻辑删除字段的 命名必须 为 visible, 如果是层次表则在 父上有即可
* 所有表 创建时间必须命名为 create_time
* 所有表 更新时间必须命名为 update_time
* 所有属性必须 not null,必须有 默认值 

支持 Java 类型

* java.lang.Boolean
* java.lang.String
* java.lang.Integer
* java.lang.Long
* java.math.BigDecimal
* io.army.struct.CodeEnum
* java.time.LocalTime
* java.time.LocalDate
* java.time.LocalDateTime


#### MySQL 默认映射
                                                                                                               
| Java Type                 |                           JDBC Type          | MySQL Data Type                |   
| :------------------------ | :--------------------------------------------|--------------------------------|   
| java.lang.Boolean         | java.sql.JDBCType#CHAR                       |  CHAR(1), [Y,N]                |   
| java.lang.String          | java.sql.JDBCType#VARCHAR                    |  VARCHAR                       |   
| java.lang.Integer         | java.sql.JDBCType#INTEGER                    |  INT                           |   
| java.lang.Long            | java.sql.JDBCType#BIGINT                     |  BIGINT                        |   
| java.math.BigDecimal      | java.sql.JDBCType#DECIMAL                    |  DECIMAL(14,2)                 |   
| io.army.struct.CodeEnum   | java.sql.JDBCType#INTEGER                    |  INT                           |      
| java.time.LocalTime       | java.sql.JDBCType#TIME                       |  TIME                          |   
| java.time.LocalDate       | java.sql.JDBCType#DATE                       |  DATE  (uuuu-MM-dd 00:00:00)   |   
| java.time.LocalDateTime   | java.sql.JDBCType#TIMESTAMP                  |  DATETIME(uuuu-MM-dd HH:mm:ss) |   
  

#### Sql Server 对应 Java 和 JDBC 的数据类型
| Java Type                 |                           JDBC Type          | Sql Server data type    |     
| :------------------------ | :--------------------------------------------|-------------------------|     
| java.lang.Boolean         | java.sql.JDBCType#CHAR                       | CHAR(1), [Y,N]          |     
| java.lang.String          | java.sql.JDBCType#VARCHAR                    | VARCHAR(n)              |     
| java.lang.Integer         | java.sql.JDBCType#INTEGER                    | INT(n)                  |     
| java.lang.Long            | java.sql.JDBCType#BIGINT                     | BIGINT(n)               |     
| java.math.BigDecimal      | java.sql.JDBCType#DECIMAL                    | DECIMAL(p,s)            | 
| io.army.struct.CodeEnum   | java.sql.JDBCType#INTEGER                    | INT(n)                  |              
| java.time.LocalTime       | java.sql.JDBCType#TIME                       | TIME                    |     
| java.time.LocalDate       | java.sql.JDBCType#DATE                       | DATE                    |     
| java.time.LocalDateTime   | java.sql.JDBCType#TIMESTAMP                  | DATETIME                |     

#### DB2 对应 JAVA 和 JDBC 的数据类型
| Java Type                 |                           JDBC Type          | DB2 data type           |     
| :------------------------ | :--------------------------------------------|-------------------------|     
| java.lang.Boolean         | java.sql.JDBCType#CHAR                       | CHAR(1), [Y,N]          |     
| java.lang.String          | java.sql.JDBCType#VARCHAR                    | VARCHAR(n)              |     
| java.lang.Integer         | java.sql.JDBCType#INTEGER                    | INT(n)                  |     
| java.lang.Long            | java.sql.JDBCType#BIGINT                     | BIGINT(n)               |     
| java.math.BigDecimal      | java.sql.JDBCType#DECIMAL                    | DECIMAL(p,s)            |
| io.army.struct.CodeEnum   | java.sql.JDBCType#INTEGER                    | INT(n)                  |             
| java.time.LocalTime       | java.sql.JDBCType#TIME                       | TIME                    |     
| java.time.LocalDate       | java.sql.JDBCType#DATE                       | DATE                    |     
| java.time.LocalDateTime   | java.sql.JDBCType#TIMESTAMP                  | TIMESTAMP               |     



#### Oracle 默认映射

| Java Type                 |                           JDBC Type          | Oracle Data Type              |  
| :------------------------ | :--------------------------------------------|-------------------------------|
| java.lang.Boolean         | java.sql.JDBCType#CHAR                       |  CHAR(1), [Y,N]               |
| java.lang.String          | java.sql.JDBCType#VARCHAR                    |  VARCHAR                      |
| java.lang.Integer         | java.sql.JDBCType#INTEGER                    |  NUMBER(11)                   |
| java.lang.Long            | java.sql.JDBCType#BIGINT                     |  NUMBER(20)                   |
| java.math.BigDecimal      | java.sql.JDBCType#DECIMAL                    |  NUMBER(14,2)                 |
| io.army.struct.CodeEnum   | java.sql.JDBCType#INTEGER                    |  NUMBER(11)                   |    
| java.time.LocalTime       | java.sql.JDBCType#TIME                       |  DATE  (1970-00-00 HH:mm:ss)  |  
| java.time.LocalDate       | java.sql.JDBCType#DATE                       |  DATE  (uuuu-MM-dd 00:00:00)  |  
| java.time.LocalDateTime   | java.sql.JDBCType#TIMESTAMP                  |  DATE  (uuuu-MM-dd HH:mm:ss)  |  


#### Postgre 默认映射

| Java Type                 |                           JDBC Type          | Postgre Data Type             |
| :------------------------ | :--------------------------------------------|-------------------------------|
| java.lang.Boolean         | java.sql.JDBCType#CHAR                       |  CHAR(1), [Y,N]               |
| java.lang.String          | java.sql.JDBCType#VARCHAR                    |  VARCHAR                      |
| java.lang.Integer         | java.sql.JDBCType#INTEGER                    |  integer                      |
| java.lang.Long            | java.sql.JDBCType#BIGINT                     |  bigint                       |
| java.math.BigDecimal      | java.sql.JDBCType#DECIMAL                    |  decimal(14,2)                |
| io.army.struct.CodeEnum   | java.sql.JDBCType#INTEGER                    |  integer                      | 
| java.time.LocalTime       | java.sql.JDBCType#TIME                       |  time                         |
| java.time.LocalDate       | java.sql.JDBCType#DATE                       |  date                         |
| java.time.LocalDateTime   | java.sql.JDBCType#TIMESTAMP                  |  timestamp  without time zone |




### 支持数据库

* MySql
* Oracle
* OceanBase
* Postgre
* Sql Server
* Db2

### terminology definition
 corresponding
* Entity : Class that annotated by io.army.annotation.Table .
* Mapping Property : Entity property that annotated by io.army.annotation.Column
* Mapped class : Class that annotated by io.army.annotation.Table or io.army.annotation.MappedSuperclass.



