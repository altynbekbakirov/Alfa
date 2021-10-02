'use strict';

// Class definition
var KTDualListbox = function () {
    // Private functions

    var demo2 = function () {
        // Dual Listbox
        var _this = document.getElementById('kt_dual_listbox_2');

        // init dual listbox
        var dualListBox = new DualListbox(_this, {
            addEvent: function (value) {
                console.log(value);
            },
            removeEvent: function (value) {
                console.log(value);
            },
            availableTitle: "Source Options",
            selectedTitle: "Destination Options",
            addButtonText: "<i class='flaticon2-next'></i>",
            removeButtonText: "<i class='flaticon2-back'></i>",
            addAllButtonText: "<i class='flaticon2-fast-next'></i>",
            removeAllButtonText: "<i class='flaticon2-fast-back'></i>"
        });
    };

    return {
        // public functions
        init: function () {
            demo2();
        },
    };
}();

window.addEventListener('load', function(){
    KTDualListbox.init();
});
