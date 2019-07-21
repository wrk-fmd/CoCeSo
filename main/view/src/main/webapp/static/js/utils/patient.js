/**
 * CoCeSo
 * Client JS - utils/patient
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2019 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module utils/patient
 */
define(function () {
  "use strict";

  var utilFunctions = {};

  utilFunctions.getAgeFromBirthday = function (birthdayLocalDateString) {
    var birthday = Date.parse(birthdayLocalDateString);
    var ageInYears = null;
    if (birthday && birthday) {
      var ageInMilliseconds = Date.now() - birthday;
      var ageAsDate = new Date(ageInMilliseconds);
      var unvalidatedAgeInYears = ageAsDate.getUTCFullYear() - 1970;
      if (unvalidatedAgeInYears >= 0 && unvalidatedAgeInYears < 150) {
        ageInYears = unvalidatedAgeInYears;
      }
    }

    return ageInYears;
  };

  return utilFunctions;
});
