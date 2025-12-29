-- Add default user (username: user, password: password)
insert into users (username, password, email, enabled)
values('user', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'user@example.com', 1);

insert into authorities (username, authority)
values('user', 'ROLE_USER');

insert into authorities (username, authority)
values('user', 'ROLE_ADMIN');

-- Seed additional users for testing
-- alice / password
insert into users (username, password, email, enabled)
values('alice', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'alice@example.com', 1);
insert into authorities (username, authority)
values('alice', 'ROLE_USER');

-- bob / password (admin)
insert into users (username, password, email, enabled)
values('bob', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'bob@example.com', 1);
insert into authorities (username, authority)
values('bob', 'ROLE_USER');
insert into authorities (username, authority)
values('bob', 'ROLE_ADMIN');

-- Add todo entries
insert into todo (ID, USERNAME, DESCRIPTION, TARGET_DATE, DONE)
values(10001,'user', 'Get AWS Certified', date('now'), 0);

insert into todo (ID, USERNAME, DESCRIPTION, TARGET_DATE, DONE)
values(10002,'user', 'Get Azure Certified', date('now'), 0);

insert into todo (ID, USERNAME, DESCRIPTION, TARGET_DATE, DONE)
values(10003,'user', 'Get GCP Certified', date('now'), 0);

insert into todo (ID, USERNAME, DESCRIPTION, TARGET_DATE, DONE)
values(10004,'user', 'Learn DevOps', date('now'), 0);
