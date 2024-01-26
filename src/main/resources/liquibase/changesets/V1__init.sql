create table if not exists users
(
    id            bigserial primary key,
    name          varchar(255) not null,
    username      varchar(255) not null unique,
    password      varchar(255) not null,
    mobile_number varchar(255)
);

create table if not exists users_roles
(
    user_id bigint       not null,
    role    varchar(255) not null,
    primary key (user_id, role),
    constraint fk_users_roles_user foreign key (user_id) references users (id) on delete cascade on update no action
);

create table if not exists product
(
    id             bigserial primary key,
    program_number int          not null,
    catalog_number varchar(255) null,
    name           varchar(255) not null,
    price          decimal(12, 2) check ( price > 0 ),
    model_car      varchar(255),
    amount         int check ( amount > -1 )
);

create table if not exists orders
(
    id           bigserial primary key,
    date         timestamp null,
    id_user      bigint,
    order_status varchar(255),
    constraint fk_order_user foreign key (id_user) references users (id) on delete cascade on update no action
);

create table if not exists order_products
(
    id         bigserial primary key,
    id_order   bigint,
    id_product bigint,
    amount     int check ( amount > 0 ),
    unique (id_order, id_product),
    constraint fk_order_products_order foreign key (id_order) references orders (id) on delete cascade on update no action,
    constraint fk_order_products_product foreign key (id_product) references product (id) on delete cascade on update no action
);

create table if not exists anonymous_order
(
    id      bigserial primary key,
    date    timestamp           null,
    id_user varchar(255) unique not null
);

create table if not exists anonymous_order_products
(
    id         bigserial primary key,
    id_order   bigint,
    id_product bigint,
    amount     int check ( amount > 0 ),
    unique (id_order, id_product),
    constraint fk_anonymous_order_anonymous_order foreign key (id_order) references anonymous_order (id) on delete cascade on update no action,
    constraint fk_product_order_product foreign key (id_product) references product (id) on delete cascade on update no action
);

create table if not exists password_reset_tokens
(
    id          bigserial primary key,
    token       varchar(255) not null,
    id_user     bigint       not null,
    expiry_date timestamp,
    unique (id, id_user),
    constraint fk_user_password_rest_tokens foreign key (id_user) references users (id) on delete cascade on update no action
);