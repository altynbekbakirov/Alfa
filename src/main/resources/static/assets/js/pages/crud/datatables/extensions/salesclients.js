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

                var column = 5;
                var column1 = 6;
                var column2 = 7;
                var column3 = 8;
                var column4 = 9;
                var column5 = 10;
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
                    KTUtil.numberString(pageTotal.toFixed(0)) + '<br/> (' + KTUtil.numberString(total.toFixed(0)) + ')',
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
                    KTUtil.numberString(pageTotal1.toFixed()) + '<br/> (' + KTUtil.numberString(total1.toFixed(2)) + ')',
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
                    '$' + KTUtil.numberString(pageTotal2.toFixed(2)) + '<br/> ($' + KTUtil.numberString(total2.toFixed(2)) + ')',
                );


                /* --- Item Gross ---------------------------------------------------------------*/

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
                    KTUtil.numberString(pageTotal3.toFixed(0)) + '<br/> (' + KTUtil.numberString(total3.toFixed(0)) + ')',
                );


                /* --- Item Discounts --------------------------------------------------------*/

                // Total over all pages
                var total4 = api.column(column4).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal4 = api.column(column4, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column4).footer()).html(
                    KTUtil.numberString(pageTotal4.toFixed()) + '<br/> (' + KTUtil.numberString(total4.toFixed(2)) + ')',
                );


                /* --- Item Expenses --------------------------------------------------------*/

                // Total over all pages
                var total5 = api.column(column5).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal5 = api.column(column5, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column5).footer()).html(
                    '$' + KTUtil.numberString(pageTotal5.toFixed(2)) + '<br/> ($' + KTUtil.numberString(total5.toFixed(2)) + ')',
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
                url: '/api/sales/client/',
                type: 'POST',
                data: {
                    // parameters for custom backend script demo
                    columnsDef: [
                        'clientCode', 'clientName', 'itemCode', 'itemName', 'itemGroup', 'itemAmount', 'itemTotal', 'itemTotalUsd', 'itemAmountRet', 'itemTotalRet', 'itemTotalUsdRet']
                },
            },
            columns: [
                {data: 'clientCode'},
                {data: 'clientName'},
                {data: 'itemCode'},
                {data: 'itemName'},
                {data: 'itemGroup'},
                {data: 'itemAmount', render: $.fn.dataTable.render.number(',', '.', 0)},
                {data: 'itemTotal', render: $.fn.dataTable.render.number(',', '.', 0)},
                {data: 'itemTotalUsd', render: $.fn.dataTable.render.number(',', '.', 0, '$')},
                {data: 'itemAmountRet', render: $.fn.dataTable.render.number(',', '.', 0)},
                {data: 'itemTotalRet', render: $.fn.dataTable.render.number(',', '.', 0)},
                {data: 'itemTotalUsdRet', render: $.fn.dataTable.render.number(',', '.', 0, '$')}
            ],
            order: [[0, 'asc']],
            drawCallback: function(settings) {
                var api = this.api();
                var rows = api.rows({page: 'current'}).nodes();
                var last = null;

                api.column(1, {page: 'current'}).data().each(function(group, i) {
                    if (last !== group) {
                        $(rows).eq(i).before(
                            '<tr style="font-weight: bold; background-color: #E4E6EF;"><td colspan="11">' + group + '</td></tr>',
                        );
                        last = group;
                    }
                });
            },
            /*order: [[0, 'asc']],
            rowGroup: {
                startRender: null,
                endRender: function (rows, group) {

                    // Remove the formatting to get integer data for summation
                    var intVal = function (i) {
                        return typeof i === 'string' ? i.replace(/[\$,]/g, '') * 1 : typeof i === 'number' ? i : 0;
                    };

                    var salaryAvg = rows
                        .data()
                        .pluck(5)
                        .reduce( function (a, b) {
                            return a + b.replace(/[^\d]/g, '')*1;
                        }, 0) / rows.count();
                    salaryAvg = $.fn.dataTable.render.number(',', '.', 0, '$').display( salaryAvg );

                    return $('<tr/>')
                        .append('<td colspan="3">' + group + ' (' + rows.count() + ')' + '</td>')
                        .append('<td>'+salaryAvg+'</td>')
                        .append('<td/>')
                        .append('<td colspan="4"></td>');

                },
                dataSrc: 'clientName',
            },*/
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
        init: function () {
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
        var item3 = $("#kt_select2_111");
        var item4 = $("#kt_select2_119");
        var group1 = $("#kt_select2_91");

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


        $.ajax({
            url: '/api/materials/itemsList/' + firmNo,
            type: 'get',
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {

                item3.empty();
                item4.empty();
                group1.empty();

                var items = [];
                var groups = [];
                var groups1 = [];

                items.push("<option value='-'>----Все товары-------</option>");
                $.each(response, function (index, element) {
                    items.push('<option value=' + element.item_code + '>' + element.item_code + ' - ' + element.item_name + '</option>');
                });

                $.each(response, function (index, element) {
                    groups.push(element.item_group);
                });

                $.extend({
                    distinct: function (anArray) {
                        var result = [];
                        $.each(anArray, function (i, v) {
                            if ($.inArray(v, result) == -1) result.push(v);
                        });
                        return result;
                    }
                });

                groups = $.distinct(groups);

                groups1.push("<option value='-'>-----Все группы-----</option>");
                $.each(groups, function (i, deger) {
                    groups1.push("<option value=" + deger + ">" + deger + "</option>");
                });

                item3.append(items);
                item4.append(items);
                group1.append(groups1);

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus, errorThrown);
            }

        });

    });

    $("#datereset").on('click', function () {
        $("#begdate-input").val();
        $("#enddate-input").val();
    });


});
