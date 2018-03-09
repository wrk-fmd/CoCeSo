/**
 * CoCeSo
 * Client JS - data/paginate
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
 * @module {function} data/paginate
 * @param {jquery} $
 * @param {module:utils/conf} conf
 */
define(["jquery", "utils/conf"], function($, conf) {
  "use strict";

  /**
   * @typedef {ko.observableArray<T>} PaginationObj<T>
   * @property {ko.observable<string>} field
   * @property {ko.observable<string>} filter
   * @property {ko.observable<integer>} page
   * @property {ko.observable<boolean>} asc
   * @property {ko.observable<boolean>} isFirst
   * @property {ko.observable<boolean>} isLast
   * @property {ko.observable<integer>} total
   */

  /**
   * Get paginated list
   *
   * @alias module:data/paginate
   * @param {string} url
   * @param {PaginationObj<T>} paginationObj The object containing the paginated data and pagination parameters
   * @param {Class<T>} Model
   * @param {Object<string,string>} params Additional get parameters
   */
  var paginate = function(url, paginationObj, Model, params) {
    var field = paginationObj.field(), filter = paginationObj.filter();

    params = params || {};
    params.page = paginationObj.page();
    params.sort = (field ? field + "," : "") + (paginationObj.asc() ? "asc" : "desc");
    if (filter) {
      params.filter = filter;
    }

    $.ajax({
      type: "GET",
      url: conf.get("jsonBase") + url,
      dataType: "json",
      data: params,
      success: function(data) {
        paginationObj($.map(data.content, function(item) {
          return new Model(item);
        }));
        paginationObj.isFirst(data.first);
        paginationObj.isLast(data.last);
        paginationObj.total(data.totalPages);
      }
    });
  };

  return paginate;
});
