

Army 是一个新型的持久层框架, Army 吸取了 Hibernate 和 Jooq 的优点并舍弃其缺点再加上独特的设计而成。

Army 拥有以下几个特性:

* 半 orm (仅支持单表映射和表继承,不支持关系映射) 
* 动态插入(domain)
* 只使用API更新
* 复杂静态 sql (使用 xml)
* 默认类型映射 
* code 枚举
* 乐观锁
* 逻辑删除(visible 字段)

Army 以下强制规则:
* 所有表必须有 primary key 且 必须命名为 id
* 所有表的 乐观锁的名命 必须为 version
* 所有表的必须有 逻辑删除字段的 命名必须 为 visible, 如果是层次表则在 父上有即可
* 所有表 创建时间必须命名为 create_time
* 所有表 更新时间必须命名为 update_time


Army 的起源
----
Army 吸收 Hibernate 和 Jooq 的优点去除其缺点加上新的设计和约定而成.

