-- Datos iniciales para usar la interfaz web manualmente (perfil "demo").
-- Saldos resultantes: cuenta 1 = 5000, cuenta 2 = 1000, cuenta 3 = 20000.
insert into account (id) values (1);
insert into account (id) values (2);
insert into account (id) values (3);

insert into activity (id, timestamp, owner_account_id, source_account_id, target_account_id, amount)
values (9001, '2020-01-01 08:00:00.0', 1, 3, 1, 5000);

insert into activity (id, timestamp, owner_account_id, source_account_id, target_account_id, amount)
values (9002, '2020-01-01 08:00:00.0', 2, 3, 2, 1000);

insert into activity (id, timestamp, owner_account_id, source_account_id, target_account_id, amount)
values (9003, '2020-01-01 08:00:00.0', 3, 1, 3, 20000);
