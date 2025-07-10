/**
 * CoCeSo
 * Client JS - edit/viewmodels/user
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2020 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/**
 * @module {Class} edit/viewmodels/user
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:edit/models/user} User
 * @param {module:edit/models/editableuser} EditableUser
 * @param {module:data/paginate} paginate
 * @param {module:data/save} ajaxSave
 * @param {module:utils/conf} conf
 * @param {module:utils/errorhandling} initErrorHandling
 */
define(["jquery", "knockout", "utils/errorhandling", "utils/conf", "qrcode"],
  function ($, ko, initErrorHandling, conf, QRCode) {
    "use strict";

    return function () {
      var self = this;

      this.call = ko.observable("");
      this.label = ko.observable("");

      this.loadedGeoBrokerUnits = ko.observableArray([]);

      var createExternalUnitModel = function (unit, urlPrefix) {
        var urlParameters = {
          id: unit.externalUnitId,
          token: unit.token
        };

        var infoContent = ko.computed(function () {
          return $("#info-template").html();
        });

        var unitHeader = ko.computed(function () {
          return unit.name + (self.label() ? ' - ' + self.label() : '');
        });

        var qrCodeDivId = "qr-code-" + unit.externalUnitId;

        var generatedUrl = urlPrefix + "?" + $.param(urlParameters);

        var paintQrCodeFunction = function () {
          new QRCode(qrCodeDivId, {
            width: 300,
            height: 300,
            correctLevel: QRCode.CorrectLevel.L,
            text: generatedUrl
          });
        };

        return {
          unitId: unit.unitId,
          type: unit.type,
          name: unit.name,
          externalUnitId: unit.externalUnitId,
          token: unit.token,
          unitHeader: unitHeader,
          qrId: qrCodeDivId,
          paintQrCode: paintQrCodeFunction,
          generatedUrl: generatedUrl,
          infoContent: infoContent
        };
      };

      this.filteredUnits = ko.computed(function () {
        var urlPrefix = conf.get("publicGeobrokerUrl");
        const calls = self.call?.split(',').map(call => call.trim().toLowerCase()).filter(call => !!call);
        return $.map(self.loadedGeoBrokerUnits(), function (unit, indexOfUnit) {
          if (!calls?.length || calls.some(call => unit.name.toLowerCase().includes(call))) {
            return createExternalUnitModel(unit, urlPrefix);
          } else {
            return null;
          }
        });
      }, this);

      // Error Handling
      initErrorHandling(this);

      this.reload = function () {
        var activeConcernId = conf.get("activeConcernId");
        $.ajax({
          type: "GET",
          url: conf.get("jsonBase") + "geo/unit/allExternalUnits",
          data: {
            concernId: activeConcernId
          },
          dataType: "json",
          success: function (data) {
            self.loadedGeoBrokerUnits(data.externalUnits);
          }
        });
      };

      $("#info-template").load(conf.get("staticBase") + "geobroker_qr_info.html", function () {
        self.reload();
      });
    }
  }
);
