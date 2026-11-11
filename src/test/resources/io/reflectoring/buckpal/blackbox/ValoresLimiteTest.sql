-- Datos de ValoresLimiteTest para cada caso de prueba
-- Saldos: cuenta 1 = 5000, cuenta 2 = 1000, cuenta 3 = 20000.
delete from activity;
delete from account;

insert into account (id) values (1);
insert into account (id) values (2);
insert into account (id) values (3);

-- Depósitos iniciales (fecha antigua: cuentan como saldo base de cada cuenta).
insert into activity (id, timestamp, owner_account_id, source_account_id, target_account_id, amount)
values (8001, '2020-01-01 08:00:00.0', 1, 99, 1, 5000);

insert into activity (id, timestamp, owner_account_id, source_account_id, target_account_id, amount)
values (8002, '2020-01-01 08:00:00.0', 2, 99, 2, 1000);

insert into activity (id, timestamp, owner_account_id, source_account_id, target_account_id, amount)
values (8003, '2020-01-01 08:00:00.0', 3, 99, 3, 20000);
