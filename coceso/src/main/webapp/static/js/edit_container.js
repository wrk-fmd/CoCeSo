/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 15.02.14
 * Time: 15:23
 */

var jsonBase = "";
var model = new ContainerViewModel();

function SlimUnit(data) {
    this.id = data.id;
    this.ordering = ko.observable(data.ordering);
    this.call = data.call;
}

function Container(data) {
    var cont = this;
    cont.id = data.id;
    cont.name = ko.observable(data.name);
    cont.ordering = ko.observable(data.ordering);
    cont.head = ko.observable(data.head);

    cont.selected = ko.observable(false);

    cont.subContainer = ko.observableArray($.map(data.subContainer, function(u) { return new Container(u)}));
    cont.units = ko.observableArray($.map(data.units, function(u) { return new SlimUnit(u)}));

    cont.select = function() {
        cont.selected(true);
    };

    cont.triggerBlur = function() {
        cont.selected(false);
    };

    cont.updateUnit = function(arg) {
        arg.item.ordering(cont.computeOrdering(arg.targetParent(), arg.item));
        $.post(jsonBase+"unitContainer/updateUnit/"+cont.id+"/"+arg.item.id+"/"+arg.item.ordering());
    };

    cont.computeOrdering = function(array, item) {
        var i = array.indexOf(item);
        if(!array || i == -1) return 0.0;
        if(i == 0) {
            if(array.length > 1){
                return array[1].ordering()/2.0;  // At first position, and other elements in array
            }
            return 10.0; // Only element in array
        }
        if(i == array.length-1) {
            return array[array.length-2].ordering()+10.0; // At last position, other elements in array
        }
        return (array[i-1].ordering()+array[i+1].ordering())/2; // Between other elements
    };

    cont.addContainer = function()  {
        var newordering;
        if(cont.subContainer().length > 0) {
            newordering = cont.subContainer()[cont.subContainer().length-1].ordering() + 10;
        }
        else {
            newordering = 10;
        }

        var newcont = new Container({id:0, name: "New", units:[], subContainer: [], ordering: newordering, head: cont.id});
        cont.subContainer.push(newcont);
        /*$.ajax(jsonBase+"unitContainer/updateContainer", {
            data: ko.toJSON(newcont, function(key, value){if(key == "selected") { return;} return value;}),
            type: "post", contentType: "application/json"
        });*/
        newcont.select();
        $(element).find("input").focus().select();
    };

    // update name of container
    cont.update = function() {
        $.ajax(jsonBase+"unitContainer/updateContainer", {
            data: ko.toJSON(cont, function(key, value){if(key == "selected") { return;} return value;}),
            type: "post", contentType: "application/json", success: function(data, status, xhr) {
                if(data > 0) { cont.id = data; }
                else { model.load(); }
            }
        });
        cont.selected(false);
    };

    // compute new ordering and update
    cont.drop = function(arg) {
        arg.item.head(cont.id);
        arg.item.ordering(cont.computeOrdering(arg.targetParent(), arg.item));
        $.ajax(jsonBase+"unitContainer/updateContainer", {
            data: ko.toJSON(arg.item, function(key, value){if(key == "selected") { return;} return value;}),
            type: "post", contentType: "application/json"
        });
    };

    cont.remove = function() {
        cont.ordering(-2);
        $.ajax(jsonBase+"unitContainer/updateContainer", {
            data: ko.toJSON(cont, function(key, value){if(key == "selected") { return;} return value;}),
            type: "post", contentType: "application/json"
        });
        model.load();
    };
}

function ContainerViewModel() {
    var self = this;

    self.spareUnits = ko.observableArray([]);

    self.top = ko.observable(new Container({id:0, name: "Loading...", units:[], subContainer: [], ordering: -1, head: 0}));

    self.load = function() {
        $.getJSON(jsonBase+"unitContainer/get", function(topContainer) {
            self.top(new Container(topContainer));
        });
        $.getJSON(jsonBase+"unitContainer/getSpare", function(allSpare) {
            var mSpare = $.map(allSpare, function(unit) {
                return new SlimUnit(unit);
            });
            self.spareUnits(mSpare);
        });
    };

    // if dropped to this model, unit becomes "spare" -> remove
    self.updateUnit = function(arg) {
        $.post(jsonBase+"unitContainer/updateUnit/0/"+arg.item.id+"/-2");
    };
}

ko.bindingHandlers.visibleAndSelect = {
    update: function(element, valueAccessor) {
        ko.bindingHandlers.visible.update(element, valueAccessor);
        if (valueAccessor()) {
            setTimeout(function() {
                $(element).focus().select();
            }, 0);
        }
    }
};
