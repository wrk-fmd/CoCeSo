/**
 * CoCeSo
 * jQuery UI Window Manager Extension
 * Copyright (c) WRK\Daniel Rohr
 *
 * Licensed under The MIT License
 * For full copyright and license information, please see the LICENSE.txt
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright     Copyright (c) 2013 Daniel Rohr
 * @link          https://sourceforge.net/projects/coceso/
 * @package       coceso.client.winman
 * @since         Rev. 1
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 *
 * Dependencies:
 *	jquery.ui.core.js
 *	jquery.ui.widget.js
 *	jquery.ui.dialog.js
 */

//Modify behaviour of JQueryUI Dialog widget
var _init = $.ui.dialog.prototype._init;
var destroy = $.ui.dialog.prototype.destroy;

$.ui.dialog.prototype._init = function() {
  //Run the original initialization code
  _init.apply(this, arguments);

  //set some variables for later use
  var dialog_element = this;
  var dialog_id = this.uiDialogTitlebar.next().attr("id");

  //Close button completely destroys a dialog
  this.uiDialogTitlebarClose.off("click");
  this._on(this.uiDialogTitlebarClose, {
    click: function(event) {
      event.preventDefault();
      this.destroy(event);
    }
  });

  //Minimize button
  this.uiDialogTitlebarMinimize = $("<button></button>").button({
    label: this.options.minimizeText,
    icons: {
      primary: "ui-icon-minusthick"
    },
    text: false
  }).addClass("ui-dialog-titlebar-close ui-dialog-titlebar-minimize").insertBefore(this.uiDialogTitlebarClose);
  this._on(this.uiDialogTitlebarMinimize, {
    click: function(event) {
      event.preventDefault();
      this.close(event);
    }
  });

  //Create hover effect and click event for minimize button
  $("#" + dialog_id + "-minbutton").hover(function() {
    $(this).addClass("ui-state-hover");
  }, function() {
    $(this).removeClass("ui-state-hover");
  }).click(function() {
    dialog_element.close();
  });
};

$.ui.dialog.prototype.destroy = function() {
  //Run the original destruction code
  destroy.apply(this, arguments);

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
    // Add classes to taskbar root element
    this.element.addClass("ui-taskbar");

    // List of elements
    this.windows = {};
    this.buttons = {};

    this.index = 0;
  },
  // Add a window
  addWindow: function(title, src, options, create, destroy) {
    var self = this;

    var id = "ui-id-" + this.uuid + "-" + (++this.index);
    var el = $("<div class='dialog_window' id='" + id + "' title='" + title + "'></div>");
    el.load(src + " .ajax_content", function(response, status, request) {
      el.find("*").each(function(i, child) {
          // !!! Templates otherwise not usable !!!
          if($(child).is("script")) {
              return;
          }
        $(child).prependAttr("id", id + "-");
        $(child).prependAttr("for", id + "-");
        $(child).prependAttr("name", id + "-");
      });
      if (typeof create === "function") {
        create(el.get(0), id);
      }
    });

    this.windows[id] = el;
    this.buttons[id] = $("<li class='ui-taskbar-item ui-widget ui-state-default ui-corner-all' id='" + id + "_taskbar'>" + title + "</li>");
    this.buttons[id].click(function() {
      self.toggle.call(self, id);
    });

    options = $.extend(true, {}, {
      closeOnEscape: false,
      width: "auto",
      height: "auto",
      position: {my: "left top", at: "left top", of: "#dialog_container", within: "#dialog_container", collision: "fit"},
      autoOpen: true,
      focus: function(event, ui) {
        self._focus.call(self, event, ui);
      },
      close: function(event, ui) {
        self._minimize.call(self, event, ui);
      },
      destroy: function(event, ui) {
        self._close.call(self, event, ui);
        if (typeof destroy === "function") {
          destroy(el.get(0), id);
        }
      }
    }, options);

      //TODO Max-Height to option in Dialog-options?
    this.windows[id].dialog(options).css("maxHeight", window.innerHeight - 150).data("ui-dialog").uiDialog.draggable("option", "containment", $("#dialog_container"));
    this.element.append(this.buttons[id]);

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
  _focus: function(event, ui) {
    //Update the taskbar after a window is focused
    this._setFocused(event.target.id);
  },
  _minimize: function(event, ui) {
    //Update the taskbar after a window is set to invisible
    if (this.buttons[event.target.id]) {
      this.buttons[event.target.id].removeClass("ui-state-focus ui-state-open").addClass("ui-state-default");
    }
    this._setFocused($(".dialog_window:visible").last().attr("id"));
  },
  _close: function(event, ui) {
    //Update the taskbar after a window is closed
    if (this.windows[event.target.id]) {
      this.windows[event.target.id].remove();
    }
    if (this.buttons[event.target.id]) {
      this.buttons[event.target.id].remove();
    }
    this._setFocused($(".dialog_window:visible").last().attr("id"));
  },
  _setFocused: function(id) {
    $.each(this.buttons, function(index, element) {
      if (element.hasClass("ui-state-focus")) {
        element.removeClass("ui-state-focus ui-state-default").addClass("ui-state-open");
      }
    });
    if (this.buttons[id]) {
      this.buttons[id].removeClass("ui-state-open ui-state-default").addClass("ui-state-focus");
    }
  },
  _destroy: function() {
    $.each(this.windows, function(index, element) {
      element.dialog("destroy");
    });

    this.element.removeClass("ui-taskbar");
  }
});
