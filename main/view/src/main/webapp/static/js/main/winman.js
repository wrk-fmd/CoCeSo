/**
 * CoCeSo
 * Client JS - main/winman
 * Copyright (c) WRK\Coceso-Team
 *
 * Licensed under the GNU General Public License, version 3 (GPL-3.0)
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright Copyright (c) 2016 WRK\Coceso-Team
 * @link https://github.com/wrk-fmd/CoCeSo
 * @license GPL-3.0 http://opensource.org/licenses/GPL-3.0
 */

/* global Function */

/**
 * @module main/winman
 * @param {module:jquery} $
 * @param {module:knockout} ko
 * @param {module:utils/conf} conf
 * @param {module:utils/i18n} _
 */
define(["jquery", "knockout", "utils/conf", "utils/i18n", "jquery-ui/widgets/dialog"], function($, ko, conf, _) {
  "use strict";

  //Modify behaviour of JQueryUI Dialog widget
  var oldTitlebar = $.ui.dialog.prototype._createTitlebar;
  var oldDestroy = $.ui.dialog.prototype.destroy;

  $.ui.dialog.prototype._createTitlebar = function() {
    //Run the original initialization code
    oldTitlebar.apply(this, arguments);

    //Close button completely destroys a dialog
    this.uiDialogTitlebarClose.off("click");
    this._on(this.uiDialogTitlebarClose, {
      click: function(event) {
        event.preventDefault();
        this.destroy(event);
      }
    });

    //Minimize button
    this.uiDialogTitlebarMinimize = $("<button type='button'></button>")
      .button({
        label: $("<a>").text("Minimize").html(),
        icon: "ui-icon-minusthick",
        showLabel: false
      })
      .insertBefore(this.uiDialogTitlebarClose);
    this._addClass(this.uiDialogTitlebarMinimize, "ui-dialog-titlebar-close ui-dialog-titlebar-minimize");
    this._on(this.uiDialogTitlebarMinimize, {
      click: function(event) {
        event.preventDefault();
        this.close(event);
      }
    });
  };

  $.ui.dialog.prototype.destroy = function() {
    //Run the original destruction code
    oldDestroy.apply(this, arguments);

    //Trigger an event
    this._trigger("destroy");
  };

  /**
   * Method to prepend an attribute
   *
   * @param {String} attrName
   * @param {String} prefix
   * @returns {$.fn}
   */
  $.fn.prependAttr = function(attrName, prefix) {
    this.attr(attrName, function(i, val) {
      return (typeof val === "undefined") ? undefined : prefix + val;
    });
    return this;
  };

  $.widget("ui.winman", {
    version: "1.0",
    options: {},
    _create: function() {
      var self = this;

      // Add classes to taskbar root element
      this._addClass("ui-taskbar");

      // List of elements
      this.windows = {};
      this.buttons = {};

      this.index = 0;

      this.tabId = 0;
      this.stored = {};

      // Find tab id
      var tabs = $.fn.getLocalStorage("tabs", []);
      while (tabs[this.tabId]) {
        this.tabId++;
      }
      tabs[this.tabId] = true;
      localStorage.setItem("tabs", JSON.stringify(tabs));

      // Set tab to inactive on close
      $(window).on("beforeunload", function() {
        self._removeTab.call(self);
      });

      // Load tab
      var defaultWindows = this.tabId === 0 ? {
        1: {
          src: conf.get("contentBase") + "unit_hierarchy.html",
          pos: "left top",
          model: "main/viewmodels/hierarchy",
          save: true
        },
        2: {
          src: conf.get("contentBase") + "incident.html",
          pos: "left+70% top",
          model: "main/viewmodels/incidents",
          options: {filter: ['overview', 'active'], title: _("main.incident.active")},
          save: true
        }
      } : {};
      var stored = $.fn.getLocalStorage("stored-" + this.tabId, defaultWindows);
      if ($.isEmptyObject(stored)) {
        stored = defaultWindows;
      }

      $.each(stored, function(key, item) {
        self.addWindow(item);
      });
    },
    // Add a window
    addWindow: function(data) {
      var self = this;

      var id = "ui-id-" + this.uuid + "-" + (++this.index),
        viewmodel = null,
        title = (data.options && data.options.title) ? data.options.title : "";

      if (data.save) {
        this.stored[id] = data;
        this._updateStore();
      }

      // Build dialog window
      var el = this.windows[id] = $("<div class='dialog_window' id='" + id + "'>");
      var options = {
        title: title.dialog || title,
        closeOnEscape: false,
        width: data.width || "auto",
        height: data.height || "auto",
        maxHeight: window.innerHeight - 150,
        appendTo: "#dialog_container",
        position: {
          my: "left top",
          at: data.pos || "left top",
          of: "#dialog_container",
          within: "#dialog_container",
          collision: "fit"
        },
        autoOpen: false,
        dragStop: function(event, ui) {
          if (self.stored[event.target.id]) {
            self.stored[event.target.id].pos = "left+" + ui.position.left + " top+" + ui.position.top;
            self._updateStore();
          }
        },
        resizeStop: function(event, ui) {
          if (self.stored[event.target.id]) {
            self.stored[event.target.id].pos = "left+" + ui.position.left + " top+" + ui.position.top;
            self.stored[event.target.id].height = ui.size.height;
            self.stored[event.target.id].width = ui.size.width;
            self._updateStore();
          }
        },
        focus: function(event) {
          self._focus.call(self, event.target.id);
        },
        close: function(event) {
          self._minimize.call(self, event.target.id);
        },
        destroy: function(event) {
          self._close.call(self, event.target.id);
          ko.cleanNode(el.get(0));
          if (viewmodel && viewmodel.destroy instanceof Function) {
            viewmodel.destroy.call(viewmodel);
          }
        }
      };
      el.dialog(options).data("ui-dialog").uiDialog.draggable("option", "containment", $("#dialog_container"));

      // Set button options
      var button = this.buttons[id] = $("<li class='ui-taskbar-item ui-widget ui-state-default ui-corner-all' id='" + id + "_taskbar'>");
      button.click(function() {
        self.toggle.call(self, id);
      });

      var contentLoaded = false, viewmodelLoaded = false;
      function afterLoad() {
        if (contentLoaded && viewmodelLoaded) {
          if (viewmodel) {
            if (viewmodel.init instanceof Function) {
              viewmodel.init.call(viewmodel);
            }
            ko.applyBindings(viewmodel, el.get(0));
          }

          button.text(title.button || title);
          self.element.append(button);

          el.dialog("option", "title", title.dialog || title);
          if (!data.minimized) {
            el.dialog("open");
          }
        }
      }

      // Load dialog content
      el.load(data.src + " .ajax_content", function() {
        el.find("*").each(function(i, child) {
          // Uncomment for usage of Templates in Windows
          // if($(child).is("script")) {
          //    return;
          // }
          $(child).prependAttr("id", id + "-");
          $(child).prependAttr("for", id + "-");
          $(child).prependAttr("name", id + "-");
        });
        contentLoaded = true;
        afterLoad();
      });

      // Load viewmodel
      if (data.model) {
        require([data.model], function(vmConstructor) {
          viewmodel = new vmConstructor(data.options);
          if (viewmodel) {
            if (typeof viewmodel.dialogTitle !== "undefined") {
              title = ko.utils.unwrapObservable(viewmodel.dialogTitle);

              if (ko.isObservable(viewmodel.dialogTitle)) {
                viewmodel.dialogTitle.subscribe(function(value) {
                  if (value) {
                    el.dialog("option", "title", value.dialog || value);
                    button.text(value.button || value);
                  }
                });
              }
              if (self.stored[id] && ko.isObservable(viewmodel.dialogState)) {
                viewmodel.dialogState.subscribe(function(value) {
                  if (value) {
                    self.stored[id].options = $.extend(true, {}, data.options, value);
                    self._updateStore();
                  }
                });
              }
            }
            viewmodel.ui = id;
          }
          viewmodelLoaded = true;
          afterLoad();
        });
      } else {
        viewmodelLoaded = true;
        afterLoad();
      }

      return id;
    },
    //Toggle a window
    toggle: function(id) {
      if (!this.windows[id].dialog("isOpen")) {
        this.windows[id].dialog("open");
      } else if (this.buttons[id].hasClass("ui-state-focus")) {
        this.windows[id].dialog("close");
      } else {
        this.windows[id].dialog("moveToTop");
      }
    },
    // Open/minimize/close a window
    open: function(id) {
      if (this.windows[id]) {
        this.windows[id].dialog("open");
      }
    },
    minimize: function(id) {
      if (this.windows[id]) {
        this.windows[id].dialog("close");
      }
    },
    close: function(id) {
      if (this.windows[id]) {
        this.windows[id].dialog("destroy");
      }
    },
    _minimize: function(id) {
      //Update the taskbar after a window is set to invisible
      if (this.buttons[id]) {
        this.buttons[id].removeClass("ui-state-focus ui-state-open").addClass("ui-state-default");
      }

      this._focusLast();

      if (this.stored[id]) {
        this.stored[id].minimized = true;
        this._updateStore();
      }
    },
    _close: function(id) {
      //Update the taskbar after a window is closed
      if (this.windows[id]) {
        this.windows[id].empty();
        this.windows[id].remove();
      }
      if (this.buttons[id]) {
        this.buttons[id].remove();
      }

      this._focusLast();

      delete this.stored[id];
      this._updateStore();
    },
    _focus: function(id) {
      $.each(this.buttons, function(index, element) {
        if (element.hasClass("ui-state-focus")) {
          element.removeClass("ui-state-focus ui-state-default").addClass("ui-state-open");
        }
      });
      if (this.buttons[id]) {
        this.buttons[id].removeClass("ui-state-open ui-state-default").addClass("ui-state-focus");
      }

      if (this.stored[id]) {
        delete this.stored[id].minimized;
        this._updateStore();
      }
    },
    _focusLast: function() {
      var instances = this.document.data("ui-dialog-instances");
      if (instances && instances[0]) {
        instances[0]._trigger("focus");
      }
    },
    _updateStore: function() {
      localStorage.setItem("stored-" + this.tabId, JSON.stringify(this.stored));
    },
    _removeTab: function() {
      var tabs = $.fn.getLocalStorage("tabs", []);
      delete tabs[this.tabId];
      localStorage.setItem("tabs", JSON.stringify(tabs));
    },
    _destroy: function() {
      this._removeTab();

      $.each(this.windows, function(index, element) {
        element.dialog("destroy");
      });

      this.element.removeClass("ui-taskbar");
    }
  });
});
