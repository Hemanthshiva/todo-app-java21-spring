-- Add default user (username: user, password: password)
insert into users (username, password, email, enabled)
values('user', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 'user@example.com', true);

insert into authorities (username, authority)
values('user', 'ROLE_USER');

insert into authorities (username, authority)
values('user', 'ROLE_ADMIN');

-- Add todo entries
insert into todo (ID, USERNAME, DESCRIPTION, TARGET_DATE, DONE)
values(10001,'user', 'Get AWS Certified', CURRENT_DATE(), false);

insert into todo (ID, USERNAME, DESCRIPTION, TARGET_DATE, DONE)
values(10002,'user', 'Get Azure Certified', CURRENT_DATE(), false);

insert into todo (ID, USERNAME, DESCRIPTION, TARGET_DATE, DONE)
values(10003,'user', 'Get GCP Certified', CURRENT_DATE(), false);

insert into todo (ID, USERNAME, DESCRIPTION, TARGET_DATE, DONE)
values(10004,'user', 'Learn DevOps', CURRENT_DATE(), false);