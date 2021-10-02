"use strict";
var KTDatatablesAdvancedColumnVisibility = function () {

    var init = function () {
        var table = $('#kt_datatable');

        // begin first table
        table.DataTable({
            createdRow: function (row, data, index) {
                var cell = $('td', row).eq(3);
                if (data[3].replace(/[\$,]/g, '') * 1 > 400000 && data[3].replace(/[\$,]/g, '') * 1 < 600000) {
                    cell.addClass('highlight').css({
                        'font-weight': 'bold',
                        color: '#716aca'
                    }).attr('title', 'Over $400,000 and below $600,000');
                }
                if (data[3].replace(/[\$,]/g, '') * 1 > 600000) {
                    cell.addClass('highlight').css({
                        'font-weight': 'bold',
                        color: '#f4516c'
                    }).attr('title', 'Over $600,000');
                }
                cell.html(KTUtil.numberString(data[3]));
            },
        });
    };

    return {

        //main function to initiate the module
        init: function () {
            init();
        },

    };

}();

jQuery(document).ready(function () {
    KTDatatablesAdvancedColumnVisibility.init();
});
