/**
 * CoCeSo
 * Client JS - main/viewmodels/alarm
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2019 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["jquery", "knockout", "data/save", "utils/errorhandling", "utils/constants", "utils/conf", "utils/i18n"],
  function($, ko, save, initErrorHandling, constants, conf, _) {
    "use strict";

    /**
     * ViewModel for Alarm Text Sending Form (Confirmation window to send an alarm text)
     *
     * @constructor
     * @param {Object} data
     */
    var AlarmTextVM = function(data) {
      var self = this;

      initErrorHandling(this);

      this.incidentId = data.incidentId;
      this.alarmType = data.alarmType === constants.AlarmText.type.casusnumberBooking
        ? constants.AlarmText.type.casusnumberBooking
        : constants.AlarmText.type.incidentInformation;

      if (this.alarmType === constants.AlarmText.type.incidentInformation) {
        this.dialogTitle = _("incident.alarm.full");
      } else {
        this.dialogTitle = _("incident.alarm.casus");
      }

      this.alarmMessageContent = ko.observable("");
      this.statusMessage = ko.observable(_("incident.alarm.text.loading"));

      (function loadAlarmTextFromServer() {
        $.post({
          contentType: "application/json",
          dataType: "json",
          url: conf.get("jsonBase") + "alarmtext/create",
          data: JSON.stringify({incidentId: self.incidentId, type: self.alarmType}),
          success: function(createAlarmTextResponse) {
            self.alarmMessageContent(createAlarmTextResponse.alarmText);
            self.statusMessage("");
          },
          error: function() {
            console.warn("Failed to create alarm text for incident.");
            self.statusMessage(_("incident.alarm.text.loading.failed"));
          }
        });
      })();

      this.ok = function() {
        save(JSON.stringify({
          incidentId: this.incidentId,
          alarmText: this.alarmMessageContent(),
          type: this.alarmType
        }), "alarmtext/send", this.afterSave, this.saveError, this.httpError);
      };

      this.afterSave = function() {
        $("#" + self.ui).dialog("destroy");
      };
    };

    return AlarmTextVM;
  });
