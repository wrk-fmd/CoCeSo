<%--
/**
 * CoCeSo
 * Client HTML edit templates
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2014 WRK\Coceso-Team
 * @link https://sourceforge.net/projects/coceso/
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */
--%>
<script type="text/html" id="template-unit-row">
  <td>
    <p>
        <input type="text" maxlength="64" class="form-control" placeholder="${call}"
               data-bind="value: call, valueUpdate: 'input', css: {'form-changed': call.localChange}, attr: {form: 'table_form_' + id}"
               style="font-weight: bold;" required>
    </p>
    <p>
        <input type="text" maxlength="64" class="form-control" placeholder="${ani}"
               data-bind="value: ani, valueUpdate: 'input', css: {'form-changed': ani.localChange}, attr: {form: 'table_form_' + id}">
    </p>
  </td>
  <td>
    <div class="btn-group btn-group-sm">
      <button type="button" class="btn btn-default"
              data-bind="click: doc.toggle, css: {active: doc, 'form-changed': doc.localChange}, attr: {form: 'table_form_' + id}">
              ${doc}
      </button>
      <button type="button" class="btn btn-default"
              data-bind="click: vehicle.toggle, css: {active: vehicle, 'form-changed': vehicle.localChange}, attr: {form: 'table_form_' + id}">
              ${vehicle}
      </button>
      <button type="button" class="btn btn-default"
              data-bind="click: portable.toggle, css: {active: portable, 'form-changed': portable.localChange}, attr: {form: 'table_form_' + id}">
              ${portable}
      </button>
    </div>
  </td>
  <td>
    <textarea rows="3" class="form-control" placeholder="${info}" data-bind="value: info, valueUpdate: 'input', css: {'form-changed': info.localChange}, attr: {form: 'table_form_' + id}"></textarea>
  </td>
  <td>
    <textarea rows="3" class="form-control" placeholder="${home}" data-bind="value: home, valueUpdate: 'input', css: {'form-changed': home.localChange}, attr: {form: 'table_form_' + id}"></textarea>
  </td>
</script>
