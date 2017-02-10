START TRANSACTION;

CREATE FUNCTION transformpoint(fk INTEGER) RETURNS JSONB AS $$
  DECLARE
    newpoint JSONB;
  BEGIN
    SELECT concat('{"@type":"dummy","info":',to_json(p.info),'}')::JSONB INTO newpoint FROM point p WHERE p.id = fk;
    RETURN newpoint;
  END;
$$ LANGUAGE plpgsql;

ALTER TABLE unit
  DROP CONSTRAINT unit_position_point_fk_fkey,
  ALTER COLUMN position_point_fk TYPE JSONB USING transformpoint(position_point_fk),
  RENAME COLUMN position_point_fk TO position;

ALTER TABLE unit
  DROP CONSTRAINT unit_home_point_fk_fkey,
  ALTER COLUMN home_point_fk TYPE JSONB USING transformpoint(home_point_fk),
  RENAME COLUMN home_point_fk TO home;

ALTER TABLE incident
  DROP CONSTRAINT incident_bo_point_fk_fkey,
  ALTER COLUMN bo_point_fk TYPE JSONB USING transformpoint(bo_point_fk),
  RENAME COLUMN bo_point_fk TO bo;

ALTER TABLE incident
  DROP CONSTRAINT incident_ao_point_fk_fkey,
  ALTER COLUMN ao_point_fk TYPE JSONB USING transformpoint(ao_point_fk),
  RENAME COLUMN ao_point_fk TO ao;

DROP FUNCTION transformpoint(INTEGER);

CREATE TABLE IF NOT EXISTS geocode (
  id SERIAL PRIMARY KEY,
  street VARCHAR(50),
  intersection VARCHAR(50),
  numberFrom INTEGER,
  numberTo INTEGER,
  numberLetter VARCHAR(5),
  numberBlock VARCHAR(20),
  postCode INTEGER,
  city VARCHAR(50),
  lat DOUBLE PRECISION NOT NULL,
  lng DOUBLE PRECISION NOT NULL
);
CREATE UNIQUE INDEX geocode_address ON geocode (
  LOWER(street), LOWER(intersection), numberFrom, numberTo, LOWER(numberLetter), LOWER(numberBlock), postCode, LOWER(city)
);

ALTER TABLE incident
  ADD COLUMN created TIMESTAMP NOT NULL DEFAULT '1970-01-01 00:00:00',
  ADD COLUMN arrival TIMESTAMP,
  ADD COLUMN stateChange TIMESTAMP,
  ADD COLUMN ended TIMESTAMP;
ALTER TABLE incident ALTER COLUMN created DROP DEFAULT;

COMMIT;
