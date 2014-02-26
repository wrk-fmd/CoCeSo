function SearchViewModel(options) {
    this.opts = $.extend(true, {
        urlprefix: "/coceso/"
        ,dataURL: "search/patient/data/"
        ,concernID: 0
        ,initialChecked: ["sur_name","given_name","info"]
    }, options);

    this.concernID = ko.observable(this.opts.concernID);

    this.checkedFilter = ko.observableArray(this.opts.initialChecked);

    this.query = ko.observable("");

    this.patients = ko.observableArray();
    ko.computed(function() {
        $.ajax(this.opts.urlprefix+this.opts.dataURL+this.concernID(), {
            success: this.patients
        })
    }, this);

    var self = this;
    this.filtered = ko.computed(function() {
        return ko.utils.arrayFilter(self.patients(), function(item) {
            if(self.query() === "") {
                return true;
            }
            var cf = self.checkedFilter();
            for(var f in cf) {
                if(item[cf[f]] && item[cf[f]] !== null && item[cf[f]].toLowerCase().indexOf(self.query().toLowerCase()) >= 0) {
                    return true;
                }
            }
            return false;
        });
    });

    self.textTooltip = ko.computed(function() {
        return "test";
    })
}
