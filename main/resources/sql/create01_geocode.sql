START TRANSACTION;

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
CREATE UNIQUE INDEX IF NOT EXISTS geocode_address ON geocode (
  LOWER(street), LOWER(intersection), numberFrom, numberTo, LOWER(numberLetter), LOWER(numberBlock), postCode, LOWER(city)
);

COMMIT;
