"use strict";
var KTDatatablesExtensionButtons = function () {

    var initTable = function () {

        var table = $('#kt_datatable').DataTable({
            // stateSave: true,
            processing: true,
            scrollX: true,
            keys: true,
            // serverSide: true,
            lengthMenu: [[20, 50, 100, 200, -1], [20, 50, 100, 200, 'Все']],
            pageLength: 20,

            footerCallback: function (row, data, start, end, display) {

                var column = 7;
                var column1 = 8;
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
                   '$' + KTUtil.numberString(pageTotal.toFixed(2)) + '<br/> ($' + KTUtil.numberString(total.toFixed(2)) + ')',
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
                    '$' + KTUtil.numberString(pageTotal1.toFixed(2)) + '<br/> ($' + KTUtil.numberString(total1.toFixed(2)) + ')',
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
                url: '/api/finance/extract/',
                type: 'POST',
                data: {
                    // parameters for custom backend script demo
                    columnsDef: [
                        'code', 'name', 'date', 'trcode', 'ficheno', 'description', 'balanceBefore', 'debit', 'credit', 'balance']
                },
            },
            columns: [
                {data: 'code'},
                {data: 'name'},
                {data: 'date'},
                {data: 'trcode'},
                {data: 'ficheno'},
                {data: 'description'},
                {data: 'balanceBefore', render: $.fn.dataTable.render.number(' ', '.', 2, '$')},
                {data: 'debit', render: $.fn.dataTable.render.number(' ', '.', 2, '$')},
                {data: 'credit', render: $.fn.dataTable.render.number(' ', '.', 2, '$')},
                {data: 'balance', render: $.fn.dataTable.render.number(' ', '.', 2, '$')}
            ],
            order: [[0, 'asc']],
            drawCallback: function (settings) {
                var api = this.api();
                var rows = api.rows({page: 'current'}).nodes();
                var last = null;

                api.column(1, {page: 'current'}).data().each(function (group, i) {
                    if (last !== group) {
                        $(rows).eq(i).before(
                            '<tr style="font-weight: bold; background-color: #E4E6EF;"><td colspan="11">' + group + '</td></tr>',
                        );
                        last = group;
                    }
                });
            },
            columnDefs: [
                {
                    // hide columns by index number
                    targets: [0, 1],
                    visible: false,
                }
            ],
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
        var periodNo = $('#periodno');

        $.ajax({
            url: '/api/' + firmNo,
            type: 'get',
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {

                console.log(response);

                periodNo.empty();

                var items = [];

                $.each(response, function (index, element) {

                    if (element.period_active == 1) {
                        items.push('<option value=' + element.period_nr + ' selected=selected>(' + element.period_nr + ') ' + '(' + element.period_begdate + ' - ' + element.period_enddate + ') - (*)</option>');
                    } else {
                        items.push('<option value=' + element.period_nr + '>(' + element.period_nr + ') ' + '(' + element.period_begdate + ' - ' + element.period_enddate + ')</option>');
                    }

                });

                periodNo.append(items);

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus, errorThrown);
            }

        });


        var item1 = $("#kt_select2_1");
        var item2 = $("#kt_select2_19");

        $.ajax({
            url: '/api/sales/accountsList/' + firmNo,
            type: 'get',
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {

                item1.empty();
                item2.empty();

                var items = [];

                items.push("<option value='-'> ---------- Все поставщики ---------- </option>");
                $.each(response, function (index, element) {
                    items.push('<option value=' + element.code + '>' + element.code + ' - ' + element.name + '</option>');
                });

                item1.append(items);
                item2.append(items);

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus, errorThrown);
            }

        });


    })

});
