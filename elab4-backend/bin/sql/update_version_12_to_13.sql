alter table project_entries add short_name text not null default '';
update project_entries set short_name = substring(name from 1 for 8);

update version set version = '13' WHERE version='12';
