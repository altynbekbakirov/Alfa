"use strict";
var KTDatatablesExtensionButtons = function () {

    var initTable = function () {

        var table = $('#kt_datatable').DataTable({
            stateSave: true,
            processing: true,
            scrollX: true,
            keys: true,
            // responsive: true,
            // serverSide: true,
            lengthMenu: [[20, 50, 100, 200, -1], [20, 50, 100, 200, 'Все']],
            pageLength: 20,

            footerCallback: function (row, data, start, end, display) {

                var column = 4;
                var column1 = 5;
                var column2 = 6;
                var column3 = 7;
                var api = this.api(), data;

                // Remove the formatting to get integer data for summation
                var intVal = function (i) {
                    return typeof i === 'string' ? i.replace(/[\$,]/g, '') * 1 : typeof i === 'number' ? i : 0;
                };


                /* --- Item Gross ---------------------------------------------------------------*/

                // Total over all pages
                var total = api.column(column).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal = api.column(column, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column).footer()).html(
                    KTUtil.numberString(pageTotal.toFixed(2)) + '<br/> (' + KTUtil.numberString(total.toFixed(2)) + ')',
                );


                /* --- Item Discounts --------------------------------------------------------*/

                // Total over all pages
                var total1 = api.column(column1).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal1 = api.column(column1, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column1).footer()).html(
                    KTUtil.numberString(pageTotal1.toFixed(2)) + '<br/> (' + KTUtil.numberString(total1.toFixed(2)) + ')',
                );


                /* --- Item Expenses --------------------------------------------------------*/

                // Total over all pages
                var total2 = api.column(column2).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal2 = api.column(column2, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column2).footer()).html(
                    KTUtil.numberString(pageTotal2.toFixed(2)) + '<br/> (' + KTUtil.numberString(total2.toFixed(2)) + ')',
                );


                /* --- Item Net --------------------------------------------------------*/

                // Total over all pages
                var total3 = api.column(column3).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal3 = api.column(column3, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column3).footer()).html(
                    KTUtil.numberString(pageTotal3.toFixed(2)) + '<br/> (' + KTUtil.numberString(total3.toFixed(2)) + ')',
                );

            },
            buttons: [
                'print',
                'copyHtml5',
                'excelHtml5',
                'csvHtml5',
                'pdfHtml5',
            ],
            ajax: {
                url: '/api/materials/fiche/',
                type: 'POST',
                data: {
                    // parameters for custom backend script demo
                    columnsDef: [
                        'item_date', 'item_trCode', 'item_ficheNo', 'item_clientName', 'item_gross', 'item_discounts', 'item_expenses', 'item_net', 'item_type']
                },
            },
            columns: [
                {data: 'item_date'},
                {data: 'item_trCode'},
                {data: 'item_ficheNo'},
                {data: 'item_clientName'},
                {data: 'item_gross', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'item_discounts', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'item_expenses', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'item_net', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'item_type'}
            ]
        });

        $('#export_print').on('click', function (e) {
            e.preventDefault();
            table.button(0).trigger();
        });

        $('#export_copy').on('click', function (e) {
            e.preventDefault();
            table.button(1).trigger();
        });

        $('#export_excel').on('click', function (e) {
            e.preventDefault();
            table.button(2).trigger();
        });

        $('#export_csv').on('click', function (e) {
            e.preventDefault();
            table.button(3).trigger();
        });

        $('#export_pdf').on('click', function (e) {
            e.preventDefault();
            table.button(4).trigger();
        });

        // Private functions
        $('#kt_multipleselectsplitter_1, #kt_multipleselectsplitter_2').multiselectsplitter();

    };


    return {
        //main function to initiate the module
        init: function () {
            initTable();
        }

    };

}();

var KTBootstrapSelect = function () {

    // Private functions
    var demos = function () {
        // minimum setup
        $('.kt-selectpicker').selectpicker();
    }

    return {
        // public functions
        init: function() {
            demos();
        }
    };
}();

jQuery(document).ready(function () {
    KTBootstrapSelect.init();
    KTDatatablesExtensionButtons.init();

    $('#firmno').on('change', function () {

        var firmNo = $(this).val();

        $.ajax({
            url: '/api/' + firmNo,
            type: 'get',
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {

                console.log(response);

                $('#periodno').empty();

                var items = [];

                $.each(response, function (index, element) {

                    if (element.period_active == 1) {
                        items.push('<option value=' + element.period_nr + ' selected=selected>(' + element.period_nr + ') ' + '(' + element.period_begdate + ' - ' + element.period_enddate + ') - (*)</option>');
                    } else {
                        items.push('<option value=' + element.period_nr + '>(' + element.period_nr + ') ' + '(' + element.period_begdate + ' - ' + element.period_enddate + ')</option>');
                    }

                });

                $('#periodno').append(items);

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus, errorThrown);
            }

        });


    })

});
