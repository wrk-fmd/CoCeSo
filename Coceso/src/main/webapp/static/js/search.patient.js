function SearchViewModel(options) {
    var self = this;
    self.opts = $.extend(true, {
        urlprefix: "/coceso/"
        ,dataURL: "search/patient/data/"
        ,concernID: 0
        ,concerns: {
            0: "Select..."
        }
        ,initialChecked: ["sur_name","given_name","info"]
    }, options);

    self.concernID = ko.observable(this.opts.concernID);

    self.checkedFilter = ko.observableArray(this.opts.initialChecked);

    self.query = ko.observable("");

    self.patients = ko.observableArray();

    self.fetch = function() {
        if(self.concernID() === 0) {
            self.patients([]);
            return;
        }

        $.ajax(self.opts.urlprefix + self.opts.dataURL + self.concernID(), {
            success: self.patients
        })
    };

    ko.computed(self.fetch);

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

    self.tooltip = function(patient) {

        var content = "<div onmouseout=\"$('.tooltip').remove();\"><table class=\"table table-striped\">";

        var h = patient.history;
        for(var i in h) {
            if(h[i]) {
                var date = new Date(h[i].timestamp * 1000);
                var time = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
                content += "<tr><td>" + time + "</td><td>" + h[i].unit_call + "</td><td>" + h[i].state + "</td></tr>";
            }

        }
        content += "</table></div>";

        return {
            trigger: 'hover focus',
            placement: 'auto left',
            html: true,
            container: 'body',
            title: "",
            content: content
        };
    }
}
