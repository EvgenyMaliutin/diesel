insert into users (name, username, password, mobile_number)
values ('Петр Иванов', 'petr@gmail.com', '$2a$10$eaL9RAsJeY95hERA/D6iUOMLfDzt7FcIqcf39ytbShEioVYm0KGLq', '8-911-555-55-55'),
       ('Сергей Сергеев','sergei@ayhoo.com','$2a$10$eaL9RAsJeY95hERA/D6iUOMLfDzt7FcIqcf39ytbShEioVYm0KGLq', '8-911-666-66-66');

insert into users_roles (user_id, role)
values (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER'),
       (2, 'ROLE_USER');

insert into product (program_number, catalog_number, name, price, model_car, amount)
values (6526, '5336-3501105р', 'Накладка тормозная (МАЗ Супер) с заклепками (к-т)  В-160', 2450.00, 'МАЗ Супер', 6),
       (6010, '236-1601180-Б2', 'Муфта выключения сцепления в сборе (МАЗ)', 1400.00, 'МАЗ', 8),
       (7307, '14.1601130', 'Диск сцепления (КамАЗ) завод', 2700.00, 'КамАЗ', 7),
       (1147, 'ФМ035-1012005', 'Элемент фильтрующий масляный Д-260 (МТЗ) металлический', 1050.00, 'МТЗ', 9);

insert into orders(date, id_user, order_status)
values ('2023-01-29 12:00:00', 1, 'CREATE');

insert into order_products(id_order, id_product, amount)
values (1, 1, 3),
       (1, 4, 2);