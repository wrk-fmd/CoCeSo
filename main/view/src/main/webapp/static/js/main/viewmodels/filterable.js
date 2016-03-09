/**
 * CoCeSo
 * Client JS - main/viewmodels/filterable
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

define(["jquery", "utils/destroy"], function($, destroy) {
  "use strict";

  /**
   * Filterable models
   *
   * @constructor
   * @param {Object} options
   */
  var Filterable = function(options) {
    options = options || {};

    this.disableFilter = {};
    for (var i in options.filter) {
      if (this.filters[options.filter[i]] && this.filters[options.filter[i]].disable) {
        this.disableFilter = $.extend(true, this.disableFilter, this.filters[options.filter[i]].disable);
      }
    }

    /**
     * Generate a list of active filters
     *
     * @type {Array}
     */
    this.activeFilters = [this.filter];

    //Filters from options
    for (var i in options.filter) {
      if (this.filters[options.filter[i]]) {
        this.activeFilters.push(this.filters[options.filter[i]].filter);
      }
    }
  };
  Filterable.prototype = Object.create({}, /** @lends Filterable.prototype */ {
    /**
     * Destroy the ViewModel
     *
     * @function
     * @return {void}
     */
    destroy: {
      value: function() {
        destroy(this);
      }
    }
  });

  return Filterable;
});
