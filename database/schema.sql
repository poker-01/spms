create table bill_info
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    bill_no      varchar(32)                              not null comment '账单编号(唯一)',
    house_id     bigint                                   not null comment '房屋ID',
    owner_id     bigint                                   null comment '业主ID',
    fee_item_id  bigint                                   not null comment '费用项目ID',
    bill_period  varchar(16)                              not null comment '账单周期(如2026-07)',
    bill_amount  decimal(10, 2)                           not null comment '账单金额',
    paid_amount  decimal(10, 2) default 0.00              null comment '已付金额',
    status       tinyint        default 1                 null comment '状态: 1-待缴费 2-已缴费 3-逾期 4-已取消',
    pay_deadline date                                     not null comment '缴费截止日期',
    pay_time     datetime                                 null comment '缴费时间',
    pay_method   tinyint                                  null comment '支付方式: 1-现金 2-微信 3-支付宝 4-银行转账 5-其他',
    is_deleted   tinyint        default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version      int            default 0                 null comment '乐观锁版本号',
    create_time  datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user  bigint                                   null comment '创建人ID',
    update_user  bigint                                   null comment '更新人ID',
    constraint uk_bill_no
        unique (bill_no)
)
    comment '账单信息表' charset = utf8mb4;

create index idx_fee_item_id
    on bill_info (fee_item_id);

create index idx_house_id
    on bill_info (house_id);

create index idx_is_deleted
    on bill_info (is_deleted);

create index idx_owner_id
    on bill_info (owner_id);

create index idx_pay_deadline
    on bill_info (pay_deadline);

create index idx_status
    on bill_info (status);

create table building_info
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    community_id  bigint                             not null comment '所属小区ID',
    building_code varchar(32)                        not null comment '楼栋编码(如A栋、1号楼)',
    building_name varchar(64)                        null comment '楼栋名称',
    total_floors  int      default 0                 null comment '总楼层数',
    total_units   int      default 0                 null comment '总户数',
    status        tinyint  default 1                 null comment '状态: 0-停用 1-启用',
    is_deleted    tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version       int      default 0                 null comment '乐观锁版本号',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user   bigint                             null comment '创建人ID',
    update_user   bigint                             null comment '更新人ID',
    constraint uk_community_building
        unique (community_id, building_code)
)
    comment '楼栋信息表' charset = utf8mb4;

create index idx_community_id
    on building_info (community_id);

create index idx_is_deleted
    on building_info (is_deleted);

create index idx_status
    on building_info (status);

create table community_info
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    community_code   varchar(64)                        not null comment '小区编码',
    community_name   varchar(128)                       not null comment '小区名称',
    address          varchar(255)                       null comment '详细地址',
    province         varchar(32)                        null comment '省份',
    city             varchar(32)                        null comment '城市',
    district         varchar(32)                        null comment '区/县',
    total_buildings  int      default 0                 null comment '总楼栋数',
    total_units      int      default 0                 null comment '总户数',
    property_company varchar(128)                       null comment '物业公司',
    manager_name     varchar(64)                        null comment '负责人姓名',
    manager_phone    varchar(16)                        null comment '负责人电话',
    status           tinyint  default 1                 null comment '状态: 0-停用 1-启用',
    is_deleted       tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version          int      default 0                 null comment '乐观锁版本号',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user      bigint                             null comment '创建人ID',
    update_user      bigint                             null comment '更新人ID',
    constraint uk_community_code
        unique (community_code)
)
    comment '小区信息表' charset = utf8mb4;

create index idx_is_deleted
    on community_info (is_deleted);

create index idx_status
    on community_info (status);

create table complaint_suggestion
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    complaint_no  varchar(32)                        not null comment '单号(唯一)',
    house_id      bigint                             not null comment '房屋ID',
    owner_id      bigint                             null comment '业主ID',
    type          tinyint                            not null comment '类型: 1-投诉 2-建议',
    title         varchar(128)                       not null comment '标题',
    content       varchar(1000)                      not null comment '内容',
    contact_phone varchar(16)                        null comment '联系电话',
    status        tinyint  default 1                 null comment '状态: 1-待处理 2-处理中 3-已处理 4-已关闭',
    reply_content varchar(1000)                      null comment '回复内容',
    reply_time    datetime                           null comment '回复时间',
    is_deleted    tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version       int      default 0                 null comment '乐观锁版本号',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user   bigint                             null comment '创建人ID',
    update_user   bigint                             null comment '更新人ID',
    constraint uk_complaint_no
        unique (complaint_no)
)
    comment '投诉建议表' charset = utf8mb4;

create index idx_house_id
    on complaint_suggestion (house_id);

create index idx_is_deleted
    on complaint_suggestion (is_deleted);

create index idx_owner_id
    on complaint_suggestion (owner_id);

create index idx_status
    on complaint_suggestion (status);

create index idx_type
    on complaint_suggestion (type);

create table fee_item
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    item_code   varchar(32)                        not null comment '项目编码',
    item_name   varchar(128)                       not null comment '项目名称',
    item_type   tinyint                            not null comment '费用类型: 1-物业费 2-水电费 3-燃气费 4-停车费 5-其他',
    unit_price  decimal(10, 2)                     null comment '单价',
    unit        varchar(16)                        null comment '计费单位(如平方米、户)',
    calc_method tinyint  default 1                 null comment '计算方式: 1-固定金额 2-按面积 3-按户 4-按用量',
    is_default  tinyint  default 0                 null comment '是否默认项目: 0-否 1-是',
    status      tinyint  default 1                 null comment '状态: 0-停用 1-启用',
    is_deleted  tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version     int      default 0                 null comment '乐观锁版本号',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user bigint                             null comment '创建人ID',
    update_user bigint                             null comment '更新人ID',
    constraint uk_item_code
        unique (item_code)
)
    comment '费用项目表' charset = utf8mb4;

create index idx_is_deleted
    on fee_item (is_deleted);

create index idx_item_type
    on fee_item (item_type);

create index idx_status
    on fee_item (status);

create table house_info
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    building_id  bigint                             not null comment '所属楼栋ID',
    house_number varchar(32)                        not null comment '房号(如101、202)',
    floor_number int                                null comment '所在楼层',
    house_area   decimal(10, 2)                     null comment '房屋面积(平方米)',
    house_type   varchar(16)                        null comment '户型(如三室两厅)',
    owner_name   varchar(64)                        null comment '业主姓名',
    owner_phone  varchar(16)                        null comment '业主电话',
    status       tinyint  default 1                 null comment '房屋状态: 0-空置 1-已入住 2-装修中',
    is_deleted   tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version      int      default 0                 null comment '乐观锁版本号',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user  bigint                             null comment '创建人ID',
    update_user  bigint                             null comment '更新人ID',
    constraint uk_building_house
        unique (building_id, house_number)
)
    comment '房屋信息表' charset = utf8mb4;

create index idx_building_id
    on house_info (building_id);

create index idx_is_deleted
    on house_info (is_deleted);

create index idx_status
    on house_info (status);

create table owner_house_rel
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    owner_info_id bigint                             not null comment '业主ID',
    house_info_id bigint                             not null comment '房屋ID',
    relation_type tinyint  default 1                 null comment '关系类型: 1-业主 2-租客 3-家属',
    is_primary    tinyint  default 0                 null comment '是否为主业: 0-否 1-是',
    move_in_date  date                               null comment '入住日期',
    is_deleted    tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version       int      default 0                 null comment '乐观锁版本号',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user   bigint                             null comment '创建人ID',
    update_user   bigint                             null comment '更新人ID',
    constraint uk_owner_house
        unique (owner_info_id, house_info_id)
)
    comment '业主房屋关联表' charset = utf8mb4;

create index idx_house_info_id
    on owner_house_rel (house_info_id);

create index idx_owner_info_id
    on owner_house_rel (owner_info_id);

create table owner_info
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    owner_name  varchar(64)                        not null comment '业主姓名',
    owner_phone varchar(16)                        not null comment '业主手机号',
    id_card     varchar(18)                        null comment '身份证号',
    gender      tinyint                            null comment '性别: 0-女 1-男',
    email       varchar(64)                        null comment '邮箱',
    status      tinyint  default 1                 null comment '状态: 0-停用 1-启用',
    is_deleted  tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version     int      default 0                 null comment '乐观锁版本号',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user bigint                             null comment '创建人ID',
    update_user bigint                             null comment '更新人ID',
    constraint uk_owner_phone
        unique (owner_phone)
)
    comment '业主信息表' charset = utf8mb4;

create index idx_is_deleted
    on owner_info (is_deleted);

create index idx_owner_name
    on owner_info (owner_name);

create table payment_record
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    payment_no  varchar(32)                        not null comment '缴费编号(唯一)',
    bill_id     bigint                             not null comment '账单ID',
    house_id    bigint                             not null comment '房屋ID',
    owner_id    bigint                             null comment '业主ID',
    pay_amount  decimal(10, 2)                     not null comment '缴费金额',
    pay_method  tinyint                            not null comment '支付方式: 1-现金 2-微信 3-支付宝 4-银行转账 5-其他',
    pay_time    datetime                           not null comment '缴费时间',
    operator_id bigint                             null comment '操作人ID(收费员)',
    receipt_no  varchar(64)                        null comment '收据编号',
    is_deleted  tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version     int      default 0                 null comment '乐观锁版本号',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user bigint                             null comment '创建人ID',
    update_user bigint                             null comment '更新人ID',
    constraint uk_payment_no
        unique (payment_no)
)
    comment '缴费记录表' charset = utf8mb4;

create index idx_bill_id
    on payment_record (bill_id);

create index idx_house_id
    on payment_record (house_id);

create index idx_is_deleted
    on payment_record (is_deleted);

create index idx_owner_id
    on payment_record (owner_id);

create index idx_pay_time
    on payment_record (pay_time);

create table repair_order
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    order_no         varchar(32)                        not null comment '报修单号(唯一)',
    house_id         bigint                             not null comment '报修房屋ID',
    owner_id         bigint                             null comment '报修业主ID',
    repair_type      tinyint                            not null comment '报修类型: 1-水电维修 2-家电维修 3-管道疏通 4-房屋修缮 5-其他',
    repair_desc      varchar(500)                       not null comment '报修描述',
    repair_phone     varchar(16)                        not null comment '联系电话',
    priority         tinyint  default 2                 null comment '优先级: 1-紧急 2-普通 3-低',
    status           tinyint  default 1                 null comment '状态: 1-待派单 2-已派单 3-维修中 4-已完成 5-已评价 6-已取消',
    assignee_id      bigint                             null comment '派单处理人ID(维修人员)',
    assign_time      datetime                           null comment '派单时间',
    repair_time      datetime                           null comment '维修完成时间',
    repair_cost      decimal(10, 2)                     null comment '维修费用',
    evaluate_score   tinyint                            null comment '评价评分: 1-5分',
    evaluate_comment varchar(255)                       null comment '评价内容',
    is_deleted       tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version          int      default 0                 null comment '乐观锁版本号',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user      bigint                             null comment '创建人ID',
    update_user      bigint                             null comment '更新人ID',
    constraint uk_order_no
        unique (order_no)
)
    comment '报修工单表' charset = utf8mb4;

create index idx_assignee_id
    on repair_order (assignee_id);

create index idx_house_id
    on repair_order (house_id);

create index idx_is_deleted
    on repair_order (is_deleted);

create index idx_owner_id
    on repair_order (owner_id);

create index idx_status
    on repair_order (status);

create table sys_permission_info
(
    id                   bigint auto_increment comment '主键ID'
        primary key,
    parent_id            bigint   default 0                 null comment '父级节点ID (0为顶级)',
    permission_code      varchar(128)                       not null comment '权限编码(唯一)',
    permission_name      varchar(128)                       not null comment '权限名称(显示名)',
    permission_type      tinyint                            not null comment '权限类型: 1-目录 2-菜单 3-按钮',
    permission_icon      varchar(64)                        null comment '图标(Element Plus图标名)',
    permission_path      varchar(255)                       null comment '路由路径(菜单专用)',
    permission_component varchar(255)                       null comment '组件地址(菜单专用)',
    permission_str       varchar(255)                       null comment '权限标识串(按钮专用,如 sys:user:add)',
    sort_order           int      default 0                 null comment '排序号(升序)',
    visible              tinyint  default 1                 null comment '是否可见: 0-隐藏 1-显示',
    is_deleted           tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version              int      default 0                 null comment '乐观锁版本号',
    create_time          datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user          bigint                             null comment '创建人ID',
    update_user          bigint                             null comment '更新人ID',
    constraint uk_permission_code
        unique (permission_code)
)
    comment '权限信息表(菜单+按钮)' charset = utf8mb4;

create index idx_is_deleted
    on sys_permission_info (is_deleted);

create index idx_parent_id
    on sys_permission_info (parent_id);

create index idx_permission_type
    on sys_permission_info (permission_type);

create table sys_role_info
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    role_code   varchar(64)                        not null comment '角色代码(唯一)',
    role_name   varchar(128)                       not null comment '角色名称',
    role_type   tinyint  default 0                 null comment '角色类型: 0-系统角色 1-自定义角色',
    is_deleted  tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version     int      default 0                 null comment '乐观锁版本号',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user bigint                             null comment '创建人ID',
    update_user bigint                             null comment '更新人ID',
    constraint uk_role_code
        unique (role_code)
)
    comment '角色信息表' charset = utf8mb4;

create index idx_is_deleted
    on sys_role_info (is_deleted);

create table sys_role_permission
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    role_info_id       bigint                             not null comment '角色ID',
    permission_info_id bigint                             not null comment '权限ID',
    is_deleted         tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version            int      default 0                 null comment '乐观锁版本号',
    create_time        datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user        bigint                             null comment '创建人ID',
    update_user        bigint                             null comment '更新人ID',
    constraint uk_role_permission
        unique (role_info_id, permission_info_id)
)
    comment '角色权限关联表' charset = utf8mb4;

create index idx_permission_info_id
    on sys_role_permission (permission_info_id);

create index idx_role_info_id
    on sys_role_permission (role_info_id);

create table sys_user_info
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    user_name      varchar(64)                        not null comment '用户名(登录用)',
    password       varchar(128)                       not null comment '密码(加密存储)',
    full_name      varchar(128)                       null comment '真实姓名',
    phone_number   varchar(16)                        null comment '手机号码',
    email          varchar(64)                        null comment '邮箱',
    avatar_address varchar(255)                       null comment '头像地址',
    status         tinyint  default 1                 null comment '状态: 0-禁用 1-启用',
    is_deleted     tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version        int      default 0                 null comment '乐观锁版本号',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user    bigint                             null comment '创建人ID',
    update_user    bigint                             null comment '更新人ID',
    constraint uk_user_name
        unique (user_name)
)
    comment '用户信息表' charset = utf8mb4;

create index idx_is_deleted
    on sys_user_info (is_deleted);

create index idx_status
    on sys_user_info (status);

create table sys_user_role
(
    id           bigint auto_increment comment '主键ID'
        primary key,
    user_info_id bigint                             not null comment '用户ID',
    role_info_id bigint                             not null comment '角色ID',
    is_deleted   tinyint  default 0                 null comment '删除标记: 0-未删除 1-已删除',
    version      int      default 0                 null comment '乐观锁版本号',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_user  bigint                             null comment '创建人ID',
    update_user  bigint                             null comment '更新人ID',
    constraint uk_user_role
        unique (user_info_id, role_info_id)
)
    comment '用户角色关联表' charset = utf8mb4;

create index idx_role_info_id
    on sys_user_role (role_info_id);

create index idx_user_info_id
    on sys_user_role (user_info_id);

