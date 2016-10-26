-- Remember to update the version table if you change this file

drop table if exists version;
create table version (
  version integer not null primary key
);
insert into version (version) values (13);

drop table if exists sequencer cascade;
create table sequencer (
  name varchar(32) primary key,
  seed bigint not null,
  ts bigint not null
);

drop table if exists users cascade;
create table users (
  id serial primary key,
  rev bigint not null,
  username text default '',
  firstname text default '',
  lastname text default '',
  title text default '',
  email text default '',
  rolestring text default '',
  is_root boolean default false,
  encodedpassword bytea,
  unique (username)
);

drop table if exists user_settings cascade;
create table user_settings (
  id serial primary key,
  rev bigint not null,
  user_id integer not null,
  setting_key text default '',
  setting_value text default ''
);
alter table user_settings add foreign key (user_id) references users (id);

drop table if exists projects cascade;
create table projects (
  id serial primary key,
  rev bigint not null,
  name text not null,
  title text default '',
  level_1 text default '',
  level_2 text default '',
  level_3 text default '',
  project_entry_metadata_fieldnames text default '',
  project_leader_id integer default -1,
  text_layers text not null default 'Diplomatic',
  creator_id integer not null,
  modifier_id integer not null,
  created_on timestamp,
  modified_on timestamp
);
alter table projects add foreign key (creator_id) references users (id);
alter table projects add foreign key (modifier_id) references users (id);

drop table if exists log_entries cascade;
create table log_entries (
  id serial primary key,
  rev bigint not null,
  project_id integer,
  project_title text default '',
  user_name text default '',
  comment text default '',
  created_on timestamp
);
alter table log_entries add foreign key (project_id) references projects (id) on delete cascade;

drop table if exists project_metadata_fields cascade;
create table project_metadata_fields (
  id serial primary key,
  rev bigint not null,
  field_name text not null,
  value_options text default '',
  creator_id integer not null,
  modifier_id integer not null,
  created_on timestamp,
  modified_on timestamp
);
alter table project_metadata_fields add foreign key (creator_id) references users (id);
alter table project_metadata_fields add foreign key (modifier_id) references users (id);

drop table if exists project_metadata_items cascade;
create table project_metadata_items (
  id serial primary key,
  rev bigint not null,
  field text not null,
  data text not null,
  project_id integer not null,
  creator_id integer not null,
  modifier_id integer not null,
  created_on timestamp,
  modified_on timestamp
);
alter table project_metadata_items add foreign key (project_id) references projects (id) on delete cascade;
alter table project_metadata_items add foreign key (creator_id) references users (id);
alter table project_metadata_items add foreign key (modifier_id) references users (id);

drop table if exists project_entries cascade;
create table project_entries (
  id serial primary key,
  rev bigint not null,
  name text not null,
  short_name text not null,
  publishable boolean not null default false,
  project_id integer not null,
  creator_id integer not null,
  modifier_id integer not null,
  created_on timestamp,
  modified_on timestamp
);
alter table project_entries add foreign key (project_id) references projects (id) on delete cascade;
alter table project_entries add foreign key (creator_id) references users (id);
alter table project_entries add foreign key (modifier_id) references users (id);

drop table if exists project_entry_metadata_items cascade;
create table project_entry_metadata_items (
  id serial primary key,
  rev bigint not null,
  field text not null,
  data text not null,
  project_entry_id integer not null,
  creator_id integer not null,
  modifier_id integer not null,
  created_on timestamp,
  modified_on timestamp
);
alter table project_entry_metadata_items add foreign key (project_entry_id) references project_entries (id) on delete cascade;
alter table project_entry_metadata_items add foreign key (creator_id) references users (id);
alter table project_entry_metadata_items add foreign key (modifier_id) references users (id);
create index project_entry_metadata_items_index on project_entry_metadata_items (field,data); 

drop table if exists project_entry_metadata_fields cascade;
create table project_entry_metadata_fields (
  id serial primary key,
  rev bigint not null,
  field text not null
);

drop table if exists project_users cascade;
create table project_users (
  id serial primary key,
  rev bigint not null default 1,
  project_id integer not null,
  user_id integer not null,
  unique (project_id, user_id)
);
alter table project_users add foreign key (project_id) references projects (id) on delete cascade;
alter table project_users add foreign key (user_id) references users (id);

drop table if exists facsimiles cascade;
create table facsimiles (
  id serial primary key,
  rev bigint not null,
  name text not null,
  title text default '',
  filename text default '',
  zoomable_url text default '',
  thumbnail_url text default '',
  project_entry_id integer not null,
  creator_id integer not null,
  modifier_id integer not null,
  created_on timestamp,
  modified_on timestamp
);
alter table facsimiles add foreign key (project_entry_id) references project_entries (id) on delete cascade;
alter table facsimiles add foreign key (creator_id) references users (id);
alter table facsimiles add foreign key (modifier_id) references users (id);

drop table if exists transcription_types cascade;
create table transcription_types (
  id serial primary key,
  rev bigint not null,
  name text not null
);

drop table if exists transcriptions cascade;
create table transcriptions (
  id serial primary key,
  rev bigint not null,
  transcription_type_id integer not null,
  title text default '',
  text_layer text default '',
  body text default '',
  project_entry_id integer not null,
  creator_id integer not null,
  modifier_id integer not null,
  created_on timestamp,
  modified_on timestamp
);
alter table transcriptions add foreign key (transcription_type_id) references transcription_types (id);
alter table transcriptions add foreign key (project_entry_id) references project_entries (id) on delete cascade;
alter table transcriptions add foreign key (creator_id) references users (id);
alter table transcriptions add foreign key (modifier_id) references users (id);

drop table if exists annotation_types cascade;
create table annotation_types (
  id serial primary key,
  rev bigint not null default 1,

  name text not null,
  description text default '',

  creator_id integer not null,
  modifier_id integer not null,
  created_on timestamp,
  modified_on timestamp
);
alter table annotation_types add foreign key (creator_id) references users (id);
alter table annotation_types add foreign key (modifier_id) references users (id);

drop table if exists project_annotation_types cascade;
create table project_annotation_types (
  id serial primary key,
  rev bigint not null default 1,
  project_id integer not null,
  annotation_type_id integer not null,
  unique (project_id, annotation_type_id)
);
alter table project_annotation_types add foreign key (project_id) references projects (id) on delete cascade;
alter table project_annotation_types add foreign key (annotation_type_id) references annotation_types (id);

drop table if exists annotation_type_metadata_items cascade;
create table annotation_type_metadata_items (
  id serial primary key,
  rev bigint not null default 1,

  annotation_type_id integer not null,
  name text not null,
  description text default ''
);
alter table annotation_type_metadata_items add foreign key (annotation_type_id) references annotation_types (id) on delete cascade;

drop table if exists annotations cascade;
create table annotations (
  id serial primary key,
  rev bigint not null default 1,

  transcription_id integer,
  annotation_no integer,
  body text default '',
  annotation_type_id integer not null,

  creator_id integer not null,
  modifier_id integer not null,
  created_on timestamp,
  modified_on timestamp
);
alter table annotations add foreign key (transcription_id) references transcriptions (id) on delete cascade;
alter table annotations add foreign key (annotation_type_id) references annotation_types (id) on delete cascade;
alter table annotations add foreign key (creator_id) references users (id);
alter table annotations add foreign key (modifier_id) references users (id);

drop table if exists annotation_metadata_items cascade;
create table annotation_metadata_items (
  id serial primary key,
  rev bigint not null default 1,

  annotation_id integer not null,
  annotation_type_metadata_item_id integer not null,
  data text not null
);
alter table annotation_metadata_items add foreign key (annotation_id) references annotations (id) on delete cascade;
alter table annotation_metadata_items add foreign key (annotation_type_metadata_item_id) references annotation_type_metadata_items (id) on delete cascade;

drop view if exists project_facsimiles;
create view project_facsimiles as
  select facsimiles.id as facsmile_id, project_entries.project_id as project_id, project_entries.id as project_entry_id 
    from facsimiles
    join project_entries
      on facsimiles.project_entry_id = project_entries.id;

drop view if exists project_transcriptions;
create view project_transcriptions as
  select transcriptions.id as transcription_id, project_entries.project_id as project_id, project_entries.id as project_entry_id
    from transcriptions
    join project_entries
      on transcriptions.project_entry_id = project_entries.id;
    
drop view if exists project_annotations;
create view project_annotations as
  select annotations.id as annotation_id, project_entries.project_id as project_id
    from annotations
    join transcriptions 
      on annotations.transcription_id = transcriptions.id
      join project_entries
        on transcriptions.project_entry_id = project_entries.id; 

drop table if exists searchdata cascade;
create table searchdata (
  id serial primary key,
  rev bigint not null,
  created_on timestamp,
  json text not null
);
