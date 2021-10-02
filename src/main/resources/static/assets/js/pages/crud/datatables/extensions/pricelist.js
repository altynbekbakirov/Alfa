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
            buttons: [
                'print',
                'copyHtml5',
                'excelHtml5',
                'csvHtml5',
                'pdfHtml5',
            ],
            ajax: {
                url: '/api/materials/price/',
                type: 'POST',
                data: {
                    // parameters for custom backend script demo
                    columnsDef: [
                        'item_row', 'item_code', 'item_name', 'item_groupCode', 'item_onHand', 'item_unit', 'item_price']
                },
            },
            columns: [
                {data: 'item_row'},
                {data: 'item_code'},
                {data: 'item_name'},
                {data: 'item_groupCode'},
                {data: 'item_onHand', render: $.fn.dataTable.render.number(' ', '.', 0)},
                {data: 'item_unit'},
                {data: 'item_price'}
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
                    distinct : function(anArray) {
                        var result = [];
                        $.each(anArray, function(i,v){
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
