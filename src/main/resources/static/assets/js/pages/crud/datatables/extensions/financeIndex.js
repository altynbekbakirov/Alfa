"use strict";
var KTDatatablesExtensionButtons = function () {

    var initTable = function () {

        var table = $('#kt_datatable').DataTable({
            stateSave: true,
            processing: true,
            scrollX: true,
            keys: true,
            colReorder: true,
            // responsive: true,
            // serverSide: true,
            lengthMenu: [[20, 50, 100, 200, -1], [20, 50, 100, 200, 'Все']],
            pageLength: 20,

            footerCallback: function (row, data, start, end, display) {

                var column1 = 4;
                var column2 = 5;
                var column3 = 6;
                var api = this.api(), data;

                // Remove the formatting to get integer data for summation
                var intVal = function (i) {
                    return typeof i === 'string' ? i.replace(/[\$,]/g, '') * 1 : typeof i === 'number' ? i : 0;
                };



                /* --- Purchase sum --------------------------------------------------------*/

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


                /* --- Sale Count --------------------------------------------------------*/

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
                    '$' + KTUtil.numberString(pageTotal2.toFixed(0)) + '<br/>($' + KTUtil.numberString(total2.toFixed(0)) + ')',
                );


                /* --- Sale Sum --------------------------------------------------------*/

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
                    '$' + KTUtil.numberString(pageTotal3.toFixed(2)) + '<br/>($' + KTUtil.numberString(total3.toFixed(2)) + ')',
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
                url: '/api/finance',
                type: 'POST',
                data: {
                    // parameters for custom backend script demo
                    columnsDef: [
                        'code', 'name', 'address', 'phone', 'debit', 'credit', 'balance']
                },
            },
            columns: [
                {data: 'code'},
                {data: 'name'},
                {data: 'address'},
                {data: 'phone'},
                {data: 'debit', render: $.fn.dataTable.render.number(' ', '.', 0, '$')},
                {data: 'credit', render: $.fn.dataTable.render.number(' ', '.', 0, '$')},
                {data: 'balance', render: $.fn.dataTable.render.number(' ', '.', 0, '$')}
            ],
            createdRow: function (row, data, index) {
                var cell = $('td', row).eq(6);
                if (data['balance'] > 0 ) {
                    cell.addClass('highlight').css({
                        'font-weight': 'bold',
                        color: '#716aca'
                    });
                }
                if (data['balance'] < 0) {
                    cell.addClass('highlight').css({
                        'font-weight': 'bold',
                        color: '#f4516c'
                    });
                }
                cell.html(KTUtil.numberString(data['balance']));
            },
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

    };


    return {
        //main function to initiate the module
        init: function () {
            initTable();
        }

    };

}();

jQuery(document).ready(function () {
    KTDatatablesExtensionButtons.init();

    $('#firmno').on('change', function () {

        var firmNo = $(this).val();
        var periodNo = $("#periodno");
        var begdate = $("#begdate-input");

        $.ajax({
            url: '/api/' + firmNo,
            type: 'get',
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {
                periodNo.empty();

                var items = [];

                $.each(response, function (index, element) {

                    if (element.period_active === 1) {
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
