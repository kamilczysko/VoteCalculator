create database vote_system;
use vote_system;
create table `candidate` (`id` bigint not null auto_increment, `name` varchar(255) not null, `votes` integer not null, `party_id` bigint not null, primary key (`id`)) engine=MyISAM;
create table `party` (`id` bigint not null auto_increment, `party_name` varchar(255) not null, primary key (`id`)) engine=MyISAM;
create table `roles` (`id` integer not null auto_increment, `user_role` varchar(255), primary key (`id`)) engine=MyISAM;
create table user (id bigint not null auto_increment, blocked bit not null, disallowed bit not null, first_name varchar(255) not null, password varchar(255) not null, pesel varchar(250) not null, second_name varchar(255) not null, voided_vote bit not null, roles_id integer, primary key (id)) engine=MyISAM;
alter database vote_system character set utf8 collate utf8_general_ci;
alter table user add constraint unique (pesel);
alter table candidate add constraint foreign key (party_id) references party (id);
alter table user add constraint foreign key (roles_id) references roles (id);
