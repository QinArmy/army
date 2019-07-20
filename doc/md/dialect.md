













#### oracle 
##### 标准文档 

###### https://docs.oracle.com/en/database/oracle/oracle-database/19/cncpt/sql.html#GUID-B383E550-BBF9-4449-A6B0-56B3B3C81A09


##### 数据类型 https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-7B72E154-677A-4342-A1EA-C74C1EA928E6

###### 字符型

1. CHAR(n) 
2. VARCHAR2(n)
3. NCHAR(n)
4. NVARCHAR2(n)

###### 数字型

1. NUMBER(p,s)
2. FLOAT
3. LONG

###### 时间型

1. DATE (相当于 DATETIME)
2. TIMESTAMP [(fractional_seconds_precision)] WITH TIME ZONE ,支持时区
3. TIMESTAMP [(fractional_seconds_precision)] WITH LOCAL TIME ZONE 


##### 分页方案

使用 伪列 rownum 做子查询分页
若只限定 行数 则一层子查询
若 offset 与 max 则 两层子查询


##### 排名方案

因为有 伪列 rownum ,所以使用 rownum 解决排名

##### 事务支持 https://docs.oracle.com/database/121/CNCPT/transact.htm#CNCPT016

1. 支持分布式事务
2. 事务挂起


##### 对悲观锁的支持
表级，行级，页级锁，
最有特点的是 支持 忽略已锁定行的锁


#### postgre  
##### 标准文档 

###### https://www.postgresql.org/docs/11/sql.html


##### 数据类型 https://www.postgresql.org/docs/11/datatype.html

###### 字符型

character  (变长)
varying(n), 
varchar(n)
character(n),
char(n)
text 

###### 数字型

1. NUMERIC(precision, scale)
2. FLOAT
3. LONG
4. money 

###### 时间型

timestamp [ (p) ] [ without time zone ]	
timestamp [ (p) ] with time zone
date (没有 time)
time [ (p) ] [ without time zone ]
time [ (p) ] with time zone
interval [ fields ] [ (p) ]

###### 其它
boolean 
Enumerated Types


##### 分页方案

 [ LIMIT { number | ALL } ] [ OFFSET number ]


##### 排名方案

RANK() OVER (ORDER BY score DESC) 子句实现

##### 事务支持 https://www.postgresql.org/docs/11/mvcc.html

1. 支持分布式事务



##### 对悲观锁的支持
表级，行级，页级锁



