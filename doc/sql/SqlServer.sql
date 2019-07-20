-- 创建数据库
create database test

-- 删除数据库
drop database test

-- 创建表
create table person (
    id int(11) primary key not null,
    name varchar(32) not null default '',
    age int(11) not null default 0,
    money decimal(14,2) not null default 0.00,
    description varchar(256) not null default ''
) go

-- 插入数据(同mysql,只要值正确,列名可选)
insert into test.person (id,name,age,money,description) values (1,'小红',18,100.00,'她是班长');

-- 更新数据
update test.person set money = 120.00 where id = 1 and age = 10;

-- 查询数据
select id,name,money from test.person order by id desc;
-- 分页查询
-- 查询第2页,每页10条
select top 10 * from person where id not in (select top (2-1)*10 id from person);

select top 10 * from person where id > (select max(id) from (select top (2-1)*10 id from person order by id asc));

select * from (select top 10 *  from (select top (2*10) * from person order by id asc) as temp order by temp.id desc) as t order by t.id asc;

select top 10 * from (select row_number() over(order by id asc) as rowNumber,* from person) as temp where temp.rowNumber > ((2-1)*10);

select * from test.person order by id offset((2-1)*10) rows fetch next 10 rows only; -- 2012及以上版本

-- 删除数据
delete from test.person where id = 1;
bb

-- data type

-- 整形
int             -- 范围: -2的31次方到2的31次方-1,每个int类型的数据按4个字节存储
smallint        -- 范围: -2的15次方到2的15次方-1,每个smallint类型的数据按2个字节存储
tinyint         -- 范围: 0-255之间的所有正整数,每个tinyint类型的数据按1个字节存储
bigint          -- 范围: -2的63次方到2的63次方-1,每个bigint类型的数据按8个字节存储

-- 浮点型
real            -- 范围: 可以精确到7位小数,从-3.40E -38到3.40E +38,每个real类型的数据按4个字节存储
float           -- 范围: 可以精确到15位小数,从-1.79E -308到1.79E +308,每个float类型的数据按8个字节存储
decimal         -- 范围: 同mysql
numeric         -- 范围: 同decimal,支持最大数据精确度为28位

-- 二进制
binary          -- 范围: 定义为binary(n),n的取值范围为1-8000,使用时必须制定其长度,n >= 1,输入数据时必须在数据前加"0X"作为二进制标识,若输入数据长度 > n,则会截掉超长部分,若输入的数据位数为奇数,则会在标识符"0X"后加"0("0x0...")
varbinary       -- 范围: 同binary相似,取值范围同binary,特点为:varbinary具有变动长度的特点,其实际长度为实际数值长度+4个字节,当binary,当binary允许为null时,则被视为varbinary,处理速度:binary > varbinary

-- 逻辑类型
bit             -- 范围: 占用1个字节,值为0或1,若输入0或1以外的值,则视为1,它不能为定义为null值

-- 字符型
char            -- 范围: 每个字符(ANSI)和符号占用1个字节的空间,定义为char(n) n的取值范围为1-8000,n的默认值为1,若输入的字符长度小于n,系统会自动在输入字符后添加空格来填满,若过长则会截掉
nchar           -- 范围: 同char类似,n的取值范围为1-4000,nchar类型为UNICODE 标准字符集(CharacterSet)
varchar         -- 范围: varchar(n),n的取值范围为1-8000,超出部分会截掉,具有变动长度的特性,若输入字符小于设定长度,不会用空格填满
nvarchar        -- 范围: 同varchar类似,n的取值范围为1-4000,nvarchar数据类型采用UNICODE 标准字符集(CharacterSet)

-- 文本
text            -- 范围: 理论容量为1到2的31次方-1
ntext           -- 范围: 采用UNICODE 标准字符集(CharacterSet)

-- 图像
image           -- 理论容量为2的31次方-1,存储数据的模式同text,用于存储大量的二进制数据

-- 时间日期
date            -- 范围: 0001-01-01 ~ 9999-12-31 占用3个字节
time            -- 范围: 00:00:00.0000000 ~ 23:59:59.9999999 占用5个字节
datetime        -- 用于存储日期和时间的结合体,范围为公元1753-01-01 00:00:00 ~ 9999-12-31 23:59:59 使用时可用"/" "." "-"作为分隔符,整个值需使用''或""括起来,占8字节
datetime2       -- datetime的扩展类型,数据范围更大,默认精度最高, 0001-01-01 00:00:00 ~ 9999-12-31 23:59:59 毫秒值精度可选
smalldatetime   -- 与datetime类型相似,范围为1900-01-01 00:00:00 ~ 2019-06-06 23:59:59 占用4个字节
datetimeoffset  -- 用于定义一个采用24小时制与日期相组合,并且可识别时区的时间.默认格式: "YYYY-MM-DD hh:mm:ss[.nnnnnnn][{+\-}hh:mm]",后边的hh和mm是时区偏移量,例:若存储北京时间为2011-11-11 12:00:00+08:00 占10字节

-- 货币型
money           -- 范围: +- 922337213685477.5808之间,数据精度为19, 占8个字节
smallmoney      -- 范围: +- 214748.3468之间,占4字节,输入数据时在数值前加货币符号,如:¥

-- 其他
rowversion      -- 每个数据的计数器,当对数据库表包含rowversion列的表执行插入或者更新操作时,该计数器值就会增加,一个表只能有一个rowversion列
timestamp       -- 时间戳数据类型,同rowversion,是一个递增计数器,使用时不需要为此类型的列指定列名
uniqueidentifier -- 16字节的GUID (Globally Unique Identifier),是Sql Server 根据网络适配器地址和主机CPU时钟产生的唯一号码,其中每个都是0-9或a-f范围内的十六进制数字,可通过newid()函数获得
cursor          -- 游标数据类型,该类型类似与数据表,其保存的数据中的包含行和列值,但是没有索引,游标用来建立一个数据的数据集,每次处理一行数据
sql_variant     -- 用于存储除文本,图形数据,timestamp数据外的其他合法的Sql Server数据,可以方便Sql Server的开发工作
table           -- 用于存储对表或视图处理后的结果集,这种新的数据类型使得变量可以存储一个表,从而使函数或过程返回查询结果更加方便,快捷
xml             -- 存储xml数据的数据类型,可以在列中或者xml类型的变量中存储xml实例,存储xml数据类型的实例大小不能超过2GB

-- 数据库事务
-- 支持ACID,Sql Server事务分三种
-- 1.自动提交,是Sql Server的默认模式,将每条单独的sql语句视为一个事务;
-- 2.显示事务,用begin transaction明确指定事务的开始,是最常用的事务类型;
-- 3.隐性事务,通过设置set implicit_transaction on语句,将隐性事务模式设置为打开,下一个语句自动启动一个新事务,不需要begin transaction,每个事务仍以commit或rollback语句显示完成.
-- 隐性事务会占用大量资源
-- 支持悲观锁