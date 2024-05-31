drop table if exists construction_work;
create table construction_work (
	id bigserial primary key,
	name varchar(255),
	data jsonb,
	spreadsheet_id bigint,
	foreign key(spreadsheet_id) references spreadsheet(id)
);