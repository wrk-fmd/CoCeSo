/**
 * CoCeSo
 * Client JS - main/models/patient
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module main/models/patient
 * @param {module:knockout} ko
 * @param {module:main/navigation} navigation
 * @param {module:utils/constants} constants
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define(["knockout", "../navigation", "utils/constants", "utils/destroy", "utils/i18n", "ko/extenders/isvalue"],
  function(ko, navigation, constants, destroy, _) {
    "use strict";

    /**
     * Single patient
     *
     * @alias module:main/models/patient
     * @constructor
     * @param {Object} data
     */
    var Patient = function(data) {
      var self = this;

      data = data || {};

      //Create basic properties
      this.id = data.id;
      this.firstname = ko.observable("");
      this.lastname = ko.observable("");
      this.birthday = ko.observable("");
      this.insurance = ko.observable("");
      this.diagnosis = ko.observable("");
      this.ertype = ko.observable("");
      this.info = ko.observable("");
      this.externalId = ko.observable("");
      this.sex = ko.observable(null);

      /**
       * Method to set data (loaded with AJAX)
       *
       * @param {Object} data
       * @returns {void}
       */
      this.setData = function(data) {
        self.firstname(data.firstname || "");
        self.lastname(data.lastname || "");
        self.insurance(data.insurance || "");
        self.birthday(data.birthday || "");
        self.diagnosis(data.diagnosis || "");
        self.ertype(data.ertype || "");
        self.info(data.info || "");
        self.externalId(data.externalId || "");
        self.sex(data.sex || null);
      };

      //Set data
      this.setData(data);

      /**
       * Full name of the patient
       *
       * @function
       * @type ko.computed
       * @returns {String}
       */
      this.fullname = ko.computed(function() {
        return this.lastname() + " " + this.firstname();
      }, this);

      /**
       * Patient is male
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isMale = this.sex.extend({isValue: constants.Patient.sex.male});

      /**
       * Patient is female
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isFemale = this.sex.extend({isValue: constants.Patient.sex.female});

      /**
       * Patient's sex is unknown
       *
       * @function
       * @type ko.pureComputed
       * @returns {boolean}
       */
      this.isUnknown = ko.pureComputed(function() {
        return (!this.isMale() && !this.isFemale());
      }, this);

      /**
       * Set sex to undefined
       *
       * @returns {void}
       */
      this.isUnknown.set = function() {
        this.sex(null);
      };

      /**
       * State css for undefined sex
       *
       * @function
       * @type ko.pureComputed
       * @returns {String}
       */
      this.isUnknown.state = ko.pureComputed(function() {
        return this() ? "active" : "";
      }, this.isUnknown);

      this.localizedSex = ko.pureComputed(function() {
        return this.isUnknown() ? "" : _('patient.sex.' + this.sex().toLowerCase());
      }, this);

    };
    Patient.prototype = Object.create({}, /** @lends Patient.prototype */ {
      /**
       * Open the patient form
       *
       * @function
       * @returns {void}
       */
      openForm: {
        value: function() {
          navigation.openPatient({id: this.id});
        }
      },
      /**
       * Destroy the object
       *
       * @function
       * @returns {void}
       */
      destroy: {
        value: function() {
          destroy(this);
        }
      }
    });

    return Patient;
  });