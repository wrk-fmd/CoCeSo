/**
 * @module main/viewmodels/status_changes
 * @param {module:knockout} ko
 * @param {module:data/store/units} store
 * @param {module:utils/i18n} _
 */
define(["knockout", "data/store/units", "utils/i18n", "ko/extenders/list"],
    function (ko, store, _) {
      "use strict";

      /**
       * List of status changes
       *
       * @alias module:main/viewmodels/status_changes
       * @constructor
       * @param {Object} options
       */
      return function (options) {
        this.dialogTitle = options.title || _("main.unit.status_changes");

        this.list = store.list.extend({
          list: {
            filter: {
              portable: true,
              latestState: function (unit) {
                return !!unit.latestState();
              },
            },
            sort: (a, b) => {
              return b.latestState().stateChangedAt() - a.latestState().stateChangedAt();
            },
          }
        });
      };
    }
);
