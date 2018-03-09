/**
 * CoCeSo
 * Client JS - main/viewmodels/patient
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
 * @module main/viewmodels/patient
 * @param {module:knockout} ko
 * @param {module:main/viewmodels/form} Form
 * @param {module:main/models/patient} Patient
 * @param {module:data/save} save
 * @param {module:data/store/patients} store
 * @param {module:utils/destroy} destroy
 * @param {module:utils/i18n} _
 */
define(["knockout", "./form", "../models/patient", "data/save", "data/store/patients", "utils/destroy", "utils/i18n",
  "ko/bindings/date", "ko/bindings/ertype", "ko/extenders/form", "ko/extenders/changes"],
  function(ko, Form, Patient, save, store, destroy, _) {
    "use strict";

    /**
     * Single Patient
     *
     * @alias module:main/viewmodels/patient
     * @constructor
     * @extends module:main/viewmodels/form
     * @extends module:main/models/patient
     * @param {Object} data
     */
    var PatientVM = function(data) {
      var self = this;

      /**
       * Observable for the ID (needed to update form after initial saving)
       *
       * @function
       * @type ko.observable
       * @returns {Integer}
       */
      this.idObs = ko.observable(data.id || null);

      /**
       * Used Model (reference)
       *
       * @function
       * @type ko.observable
       * @returns {module:main/models/patient}
       */
      this.model = ko.observable(null);

      var title = _("patient.edit");
      this.dialogTitle = ko.computed(function() {
        if (!this.model()) {
          return title;
        }
        return {dialog: title + ": " + this.model().firstname() + " " + this.model().lastname(), button: title};
      }, this);

      //Call parent constructors
      Patient.call(this, {id: data.id});
      Form.call(this);

      //Initialize change detection
      this.firstname.extend({observeChanges: {}});
      this.lastname.extend({observeChanges: {}});
      this.insurance.extend({observeChanges: {}});
      this.birthday.extend({observeChanges: {}});
      this.externalId.extend({observeChanges: {}});
      this.diagnosis.extend({observeChanges: {}});
      this.ertype.extend({observeChanges: {}});
      this.info.extend({observeChanges: {keepChanges: true}});
      this.sex.extend({observeChanges: {}});

      this.form.push(this.firstname, this.lastname, this.insurance, this.birthday,
        this.externalId, this.diagnosis, this.ertype, this.info, this.sex);

      /**
       * "Virtual" computed observable:
       * Serves as callback on changing the id or the list of models
       *
       * @function
       * @type ko.computed
       */
      this.modelChange = ko.computed(function() {
        var newModel = store.get(this.idObs()),
          oldModel = this.model.peek();

        if (newModel === null) {
          if (oldModel === null) {
            //No model exists (not loaded yet or empty form), so create a dummy one
            this.model(new Patient());
          }
        } else if (newModel !== oldModel) {
          //Model has changed
          this.model(newModel);
        }
      }, this);

      /**
       * "Virtual" computed observable:
       * Load the local data from Model
       *
       * @function
       * @type ko.computed
       */
      this.load = ko.computed(function() {
        //Subscribe to change of model
        this.model();

        //Update server reference for change detection
        this.firstname.server(this.model().firstname);
        this.lastname.server(this.model().lastname);
        this.insurance.server(this.model().insurance);
        this.birthday.server(this.model().birthday);
        this.externalId.server(this.model().externalId);
        this.diagnosis.server(this.model().diagnosis);
        this.ertype.server(this.model().ertype);
        this.info.server(this.model().info);
        this.sex.server(this.model().sex);

        //Set initial data
        if (ko.computedContext.isInitial()) {
          if (typeof data.firstname !== "undefined") {
            this.firstname(data.firstname);
          }
          if (typeof data.lastname !== "undefined") {
            this.lastname(data.lastname);
          }
          if (typeof data.insurance !== "undefined") {
            this.insurance(data.insurance);
          }
          if (typeof data.birthday !== "undefined") {
            this.birthday(data.birthday);
          }
          if (typeof data.externalId !== "undefined") {
            this.externalId(data.externalId);
          }
          if (typeof data.diagnosis !== "undefined") {
            this.diagnosis(data.diagnosis);
          }
          if (typeof data.ertype !== "undefined") {
            this.ertype(data.ertype);
          }
          if (typeof data.info !== "undefined") {
            this.info(data.info);
          }
          if (typeof data.sex !== "undefined") {
            this.sex(data.sex);
          }
        }
      }, this);


      /**
       * Callback after saving
       *
       * @param {Object} result The data returned from server
       */
      this.afterSave = function(result) {
        self.error(false);
        if (result.patient_id && result.patient_id !== self.id) {
          //ID has changed
          self.id = result.patient_id;
          self.idObs(self.id);
        }
        if (self.id && data.incident) {
          save({incident_id: data.incident, patient_id: self.id}, "incident/assignPatient.json", null, self.saveError, self.httpError);
        }
      };
    };
    PatientVM.prototype = Object.create(Patient.prototype, /** @lends PatientVM.prototype */ {
      /**
       * Save the form
       *
       * @function
       * @returns {boolean}
       */
      save: {
        value: function() {
          var data = {
            id: this.id,
            firstname: this.firstname(),
            lastname: this.lastname(),
            insurance: this.insurance(),
            birthday: this.birthday(),
            diagnosis: this.diagnosis(),
            ertype: this.ertype(),
            info: this.info(),
            externalId: this.externalId(),
            sex: this.sex()
          };

          save(JSON.stringify(data), "patient/update.json", this.afterSave, this.saveError, this.httpError, this.form.saving);
        }
      },
      /**
       * Destroy the ViewModel
       *
       * @function
       */
      destroy: {
        value: function() {
          destroy(this);
        }
      }
    });

    return PatientVM;
  }
);
