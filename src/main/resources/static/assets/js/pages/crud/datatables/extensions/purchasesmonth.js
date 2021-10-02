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

                var column = 3;
                var column1 = 4;
                var column2 = 5;
                var column3 = 6;
                var column4 = 7;
                var column5 = 8;
                var column6 = 9;
                var column7 = 10;
                var column8 = 11;
                var column9 = 12;
                var column10 = 13;
                var column11 = 14;
                var column12 = 15;
                var column13 = 16;
                var column14 = 17;
                var api = this.api(), data;

                // Remove the formatting to get integer data for summation
                var intVal = function (i) {
                    return typeof i === 'string' ? i.replace(/[\$,]/g, '') * 1 : typeof i === 'number' ? i : 0;
                };


                /* --- Purchase amount --------------------------------------------------------*/

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
                    KTUtil.numberString(pageTotal1.toFixed(0)) + '<br/> (' + KTUtil.numberString(total1.toFixed(0)) + ')',
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
                    KTUtil.numberString(pageTotal2.toFixed(0)) + '<br/>(' + KTUtil.numberString(total2.toFixed(0)) + ')',
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
                    KTUtil.numberString(pageTotal3.toFixed(0)) + '<br/>(' + KTUtil.numberString(total3.toFixed(0)) + ')',
                );


                /* --- OnHand --------------------------------------------------------*/

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
                    KTUtil.numberString(pageTotal4.toFixed(0)) + '<br/>(' + KTUtil.numberString(total4.toFixed(0)) + ')',
                );


                /* --- OnHand --------------------------------------------------------*/

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
                    KTUtil.numberString(pageTotal5.toFixed(0)) + '<br/>(' + KTUtil.numberString(total5.toFixed(0)) + ')',
                );



                // Total over all pages
                var total6 = api.column(column6).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal6 = api.column(column6, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column6).footer()).html(
                    KTUtil.numberString(pageTotal6.toFixed(0)) + '<br/>(' + KTUtil.numberString(total6.toFixed(0)) + ')',
                );


                // Total over all pages
                var total7 = api.column(column7).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal7 = api.column(column7, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column7).footer()).html(
                    KTUtil.numberString(pageTotal7.toFixed(0)) + '<br/>(' + KTUtil.numberString(total7.toFixed(0)) + ')',
                );


                // Total over all pages
                var total8 = api.column(column8).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal8 = api.column(column8, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column8).footer()).html(
                    KTUtil.numberString(pageTotal8.toFixed(0)) + '<br/>(' + KTUtil.numberString(total8.toFixed(0)) + ')',
                );


                // Total over all pages
                var total9 = api.column(column9).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal9 = api.column(column9, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column9).footer()).html(
                    KTUtil.numberString(pageTotal9.toFixed(0)) + '<br/>(' + KTUtil.numberString(total9.toFixed(0)) + ')',
                );


                // Total over all pages
                var total10 = api.column(column10).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal10 = api.column(column10, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column10).footer()).html(
                    KTUtil.numberString(pageTotal10.toFixed(0)) + '<br/>(' + KTUtil.numberString(total10.toFixed(0)) + ')',
                );


                // Total over all pages
                var total11 = api.column(column11).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal11 = api.column(column11, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column11).footer()).html(
                    KTUtil.numberString(pageTotal11.toFixed(0)) + '<br/>(' + KTUtil.numberString(total11.toFixed(0)) + ')',
                );


                // Total over all pages
                var total12 = api.column(column12).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal12 = api.column(column12, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column12).footer()).html(
                    KTUtil.numberString(pageTotal12.toFixed(0)) + '<br/>(' + KTUtil.numberString(total12.toFixed(0)) + ')',
                );


                // Total over all pages
                var total13 = api.column(column13).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal13 = api.column(column13, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column13).footer()).html(
                    KTUtil.numberString(pageTotal13.toFixed(2)) + '<br/>(' + KTUtil.numberString(total13.toFixed(2)) + ')',
                );


                // Total over all pages
                var total14 = api.column(column14).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Total over this page
                var pageTotal14 = api.column(column14, {page: 'current'}).data().reduce(function (a, b) {
                    return intVal(a) + intVal(b);
                }, 0);

                // Update footer
                $(api.column(column14).footer()).html(
                    '$' +  KTUtil.numberString(pageTotal14.toFixed(2)) + '<br/>($' + KTUtil.numberString(total14.toFixed(2)) + ')',
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
                url: '/api/purchases/month',
                type: 'POST',
                data: {
                    // parameters for custom backend script demo
                    columnsDef: [
                        'code', 'name', 'groupCode', 'jan', 'feb', 'mar', 'apr', 'may', 'jun', 'jul', 'aug', 'sep', 'oct', 'nov', 'dec', 'totalCount', 'totalSum', 'totalUsd']
                },
            },
            columns: [
                {data: 'code'},
                {data: 'name'},
                {data: 'groupCode'},
                {data: 'jan', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'feb', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'mar', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'apr', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'may', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'jun', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'jul', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'aug', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'sep', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'oct', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'nov', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'dec', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'totalCount', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'totalSum', render: $.fn.dataTable.render.number(' ', '.', 2)},
                {data: 'totalUsd', render: $.fn.dataTable.render.number(' ', '.', 2, '$')}
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

jQuery(document).ready(function () {
    KTDatatablesExtensionButtons.init();

    $('#firmno').on('change', function () {

        var firmNo = $(this).val();
        var periodNo = $("#periodno");

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
        var group1 = $("#kt_select2_91");

        $.ajax({
            url: '/api/materials/itemsList/' + firmNo,
            type: 'get',
            contentType: 'application/json',
            dataType: 'json',
            success: function (response) {

                item1.empty();
                item2.empty();
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

                item1.append(items);
                item2.append(items);
                group1.append(groups1);

            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(textStatus, errorThrown);
            }

        });


    })

});
