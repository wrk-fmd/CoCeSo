ALTER TYPE E_INCIDENTSTATE ADD VALUE IF NOT EXISTS 'InProgress' AFTER 'Open';

START TRANSACTION;

CREATE TYPE E_INCIDENTSTATE_1 AS ENUM ('Open', 'Demand', 'InProgress', 'Done');

UPDATE incident SET state = 'Open' WHERE state IN ('New');
UPDATE incident SET state = 'InProgress' WHERE state IN ('Dispo', 'Working');

ALTER TABLE incident ALTER COLUMN state TYPE E_INCIDENTSTATE_1 USING (state::text::E_INCIDENTSTATE_1);

DROP TYPE E_INCIDENTSTATE;
ALTER TYPE E_INCIDENTSTATE_1 RENAME TO E_INCIDENTSTATE;

COMMIT;
