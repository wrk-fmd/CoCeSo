BEGIN;
INSERT INTO users(id, firstname, lastname, personnelId, contact, info, allowlogin, username, hashedpw)
  VALUES (nextval('users_id_seq'), 'Vorname', 'Nachname', 0000, '+43 664 0000000', '', true, 'v.nachname', '');
INSERT INTO user_role (user_fk, urole) VALUES (currval('users_id_seq'), 'Root');
COMMIT;
