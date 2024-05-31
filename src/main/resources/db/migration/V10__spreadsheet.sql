drop table if exists spreadsheet;
create table spreadsheet(
    id bigserial primary key,
    uuid uuid,
    filename varchar(255)
);