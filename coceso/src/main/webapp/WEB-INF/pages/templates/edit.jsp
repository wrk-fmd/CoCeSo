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
    <p data-bind="css: call.formcss">
      <strong>
        <input type="text" maxlength="64" class="form-control" placeholder="${call}" required
               data-bind="value: call, valueUpdate: 'input', attr: {form: 'table_form_' + id}">
      </strong>
    </p>
    <p data-bind="css: ani.formcss">
      <input type="text" maxlength="64" class="form-control" placeholder="${ani}"
             data-bind="value: ani, valueUpdate: 'input', attr: {form: 'table_form_' + id}">
    </p>
  </td>
  <td>
    <p class="btn-group btn-group-sm">
      <button type="button" class="btn btn-default"
              data-bind="click: doc.toggle, css: doc.state, attr: {form: 'table_form_' + id}">
        ${doc}
      </button>
      <button type="button" class="btn btn-default"
              data-bind="click: vehicle.toggle, css: vehicle.state, attr: {form: 'table_form_' + id}">
        ${vehicle}
      </button>
      <button type="button" class="btn btn-default"
              data-bind="click: portable.toggle, css: portable.state, attr: {form: 'table_form_' + id}">
        ${portable}
      </button>
    </p>
    <!-- ko if: id -->
    <p><button type="button" class="btn btn-default btn-sm" data-bind="click: editCrew"><spring:message code="label.crew.edit"/></button></p>
    <!-- /ko -->
  </td>
  <td data-bind="css: info.formcss">
    <textarea rows="3" class="form-control" placeholder="${info}" data-bind="value: info, valueUpdate: 'input', attr: {form: 'table_form_' + id}"></textarea>
  </td>
  <td data-bind="css: home.formcss">
    <textarea rows="3" class="form-control" placeholder="${home}" data-bind="value: home, valueUpdate: 'input', attr: {form: 'table_form_' + id}"></textarea>
  </td>
</script>

<script type="text/html" id="template-container">
  <div class="panel panel-default">
    <div class="panel-heading clearfix">
      <span data-bind="text: name() || '---', click: selected.set, visible: !selected()"></span>
      <form data-bind="submit: selected.unset" style="display: inline;"><input type="text" data-bind="value: name, event: {blur: update}, visibleAndSelect: selected"/></form>

      <div class="pull-right">
        <button class="btn btn-danger btn-xs" data-bind="click: remove"><span class="glyphicon glyphicon-remove-sign"></span></button>
        <button class="btn btn-success btn-xs" data-bind="click: add"><span class="glyphicon glyphicon-plus-sign"></span></button>
      </div>
    </div>

    <div class="panel-body">
      <ul class="unit_list unit_list_edit" data-bind="sortable: {data: units, connectClass: 'unit_list_edit', afterMove: $root.container.dropUnit, options: {placeholder: 'unit-placeholder ui-corner-all'}}">
        <li>
          <a href="#" class="unit_state">
            <span class="ui-corner-all" data-bind="text: call"></span>
          </a>
        </li>
      </ul>

      <div class="unit_container_edit" data-bind="sortable: {template: 'template-container', data: subContainer, connectClass: 'unit_container_edit', afterMove: $root.container.drop, options: {placeholder: 'container-placeholder'}}"></div>
    </div>
  </div>
</script>
