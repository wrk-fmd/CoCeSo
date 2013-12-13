INSERT INTO point (info) VALUES
('Mt. Scary'),
('Unknown Road 1'),
('Batman Cave'),
('Mordor');

INSERT INTO concern (info, name, pax, point_fk) VALUES
('Dr. Acula', 'Halloween', 1500, 1),
('Walter White', 'Birthday Party', 2, 2);

INSERT INTO unit
  (concern_fk, state, call, ani,   withDoc, portable, transportVehicle, info, home_point_fk) VALUES
  (1, 'AD', 'TRP-1', '',      false,  true,     false, 'INFO: n9othing', 3),
  (1, 'AD', 'TRP-2', '',      false,  true,     false, 'INFO: noth3ing', 1),
  (2, 'AD', 'TRP-3', '',      false,  true,     false, 'INFO: n6othing', 3),
  (1, 'AD', 'KTG-10', '',     false,  true,     false, 'INFO: not1hing', 3),
  (1, 'AD', 'KTG-11', '',     false,  true,     true,  'INFO: nothi7ng', 1),
  (1, 'AD', 'NA-TRP-21', '',  true,   true,     false, 'INFO: no4thing', 2);
