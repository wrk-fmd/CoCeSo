BEGIN;
INSERT INTO person (id, given_name, sur_name, dnr, contact) VALUES (nextval('person_id_seq'), 'Vorname','Nachname',0000,'+436640000000');
INSERT INTO operator (id, allowlogin,username,hashedpw) VALUES (currval('person_id_seq'), 't', 'v.nachname','');
INSERT INTO operator_role (operator_fk, role) VALUES (currval('person_id_seq'), 'Root');
COMMIT;
