create table if not exists tw__Towar
(
    tw_Id     int not null primary key,
    tw_Rodzaj int,
    tw_Symbol varchar,
    tw_Nazwa  varchar
);

delete from tw__Towar;

INSERT INTO tw__Towar (tw_Id, tw_Rodzaj, tw_Symbol, tw_Nazwa) VALUES (5, 1, 'ACETON', 'Aceton');
INSERT INTO tw__Towar (tw_Id, tw_Rodzaj, tw_Symbol, tw_Nazwa) VALUES (6, 1, 'ACETON FARMA', 'Aceton farmaceutyczny');
INSERT INTO tw__Towar (tw_Id, tw_Rodzaj, tw_Symbol, tw_Nazwa) VALUES (7, 1, 'ALKOHOL BENZYLOWY', 'Alkohol benzylowy');
