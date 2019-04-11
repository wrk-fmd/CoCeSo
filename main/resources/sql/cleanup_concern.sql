-- This script cleans up a concern with the given concern ID. All data, including logs, will be deleted from the database.

DO $$
  DECLARE
    concern_to_delete INTEGER := 1;
  BEGIN
    DELETE FROM log WHERE concern_fk = concern_to_delete;
    DELETE FROM concern WHERE id = concern_to_delete;
  EXCEPTION
    WHEN OTHERS THEN
      RAISE NOTICE 'Transaction failed. Rolling back.';
      RAISE NOTICE '% %', SQLERRM, SQLSTATE;
  END $$;
