/**
 * Created by Robert on 22.04.2014.
 */

if (typeof jQuery === "undefined") { throw new Error("this Script requires jQuery") }
if (typeof ko === "undefined") { throw new Error("this Script requires Knockout") }

var lockedList = ko.observableArray([]);
var baseURL = "/coceso/";
var activeConcern = 0;


EditableUnit = function(data) {
    var self = this;
    data = data || {};

    self.id = ko.observable(data.id || 0).extend({observeChanges: {}});
    self.call = ko.observable(data.call || "").extend({observeChanges: {}});
    self.ani = ko.observable(data.ani || "").extend({observeChanges: {}});
    self.withDoc = ko.observable(data.withDoc || false).extend({observeChanges: {}});
    self.transportVehicle = ko.observable(data.transportVehicle || false).extend({observeChanges: {}});
    self.portable = ko.observable(data.portable || false).extend({observeChanges: {}});
    self.info = ko.observable(data.info || "").extend({observeChanges: {}});
    self.home = {};
    self.home.info = ko.observable(data.home && data.home !== null ? data.home.info : "").extend({observeChanges: {}});

    // Dependencies: Observables to watch for Changes
    self.dependencies = ko.observableArray([self.call, self.ani, self.withDoc, self.transportVehicle,
                             self.portable, self.info, self.home_info]).extend({arrayChanges: {}});

    // Flag if error on last ajax request
    self.saveError = ko.observable(false);
    // Lock of 'Remove' button (if Unit already used in Main Program)
    self.locked = ko.computed(function() { //TODO locked doesn't work
        if(typeof lockedList === "undefined " || lockedList() === null) {
            console.debug("lockedList not found :: " + self.call());
            return true; // Locked by default
        }
        return ( $.inArray(self.id(), lockedList()) !== -1 );
    });
    // Delegate localChange
    self.localChange = self.dependencies.localChange; //TODO localChange doesn't trigger


    self.save = function() {

    };
};

EditableConcern = function(data) {
    var self = this;
    data = data || {};

    self.saveError = ko.observable(false);

    self.id = ko.observable(data.id || 0);
    self.name = ko.observable(data.name || "");
    self.pax = ko.observable(data.pax || 0);
    self.info = ko.observable(data.info || "");

    self.save = function() {
        $.ajax(baseURL + "data/concern/update", {
            data: ko.toJSON(self, function(key, value){if(key == "saveError") { return;} return value;}),
            type: "post", contentType: "application/json", success: function() {
                self.saveError(false);
            }, error: function() {
                self.saveError(true);
            }
        });
    }
};

PageViewModel = function() {
    var self = this;


    self.unitlist = ko.observableArray([]);
    self.getUnit = function(id) {
        return ko.utils.arrayFirst(self.unitlist(), function(unit) {
            return unit.id() === id;
        })
    };

    self.concern = ko.observable(new EditableConcern());

    self.newUnit = ko.observable(new EditableUnit());

    self.reload = function() {
        $.getJSON(baseURL+"data/unit/getNonDeletables", lockedList);
        $.getJSON(baseURL+"data/unit/getAll", function(units) {
            var mUnits = $.map(units, function(unit) {
                return new EditableUnit(unit);
            });
            self.unitlist(mUnits);
        });
        $.getJSON(baseURL+"data/concern/get/" + activeConcern, self.concern);
        console.info("(Re)load of data complete");
    };

    // Remove Unit
    self.remove = function(id) {
        $.ajax(baseURL + "data/unit/remove", {
            data: ko.toJSON( {id: id} ),
            type: "post", contentType: "application/json", success: function() {
                self.unitlist.remove(self.getUnit(id));
            }, error: function() {
                self.getUnit(id).saveError(true);
            }
        });
    };

    // Create new Unit
    self.create = function() {
        // Save to server
        var unit0 = $.extend(true, {concern: activeConcern}, self.newUnit());
        var unit = ko.toJSON(unit0, function(key, value){
            if($.inArray(key, ["saveError", "dependencies", "locked", "localChange"])) { return;}
            return value;
        });
        $.ajax(baseURL + "data/unit/update", {
            data: unit,
            type: "post", contentType: "application/json", success: function() {
                self.newUnit.saveError(false); // RESET Error Flag
                self.unitlist.push(self.newUnit()); //ADD TO Unitlist
                self.newUnit(new EditableUnit()); //RESET newUnit
            }, error: function() {
                self.newUnit().saveError(true); // SET Error Flag
            }
        });
    };
};


