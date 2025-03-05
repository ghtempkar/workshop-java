create table if not exists mytable2
(
    tw_Id     int not null primary key,
    tw_Rodzaj int,
    tw_symbol2 varchar,
    tw_Nazwa  varchar
);

delete from mytable2;

INSERT INTO mytable2 (tw_Id, tw_Rodzaj, tw_symbol2, tw_Nazwa) VALUES (5, 1, 'ACETON', 'Aceton');
INSERT INTO mytable2 (tw_Id, tw_Rodzaj, tw_symbol2, tw_Nazwa) VALUES (6, 1, 'ACETON FARMA', 'Aceton farmaceutyczny');
INSERT INTO mytable2 (tw_Id, tw_Rodzaj, tw_symbol2, tw_Nazwa) VALUES (7, 1, 'ALKOHOL BENZYLOWY', 'Alkohol benzylowy');
