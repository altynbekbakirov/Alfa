jQuery(document).ready(function () {

    $('#kt_sweetalert').on('click', function (event) {

        event.preventDefault();

        var firmNo = $('#firmno').val();

        Swal.fire({
            title: "Очистить сетевой файл?",
            text: "Настоятельно рекомендуется сохранить данные перед этой операцией!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Да, Очистить!",
            cancelButtonText: "Отмена"
        }).then(function (result) {
            if (result.value) {
                $.ajax({
                    url: '/api/network/' + firmNo,
                    type: 'get',
                    contentType: 'application/json',
                    dataType: 'json',
                    success: function (response) {
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(textStatus, errorThrown);
                    }

                });
                Swal.fire(
                    "Успешно!",
                    "Сетевой файл очищен.",
                    "success"
                )
            }

        });


    })

});
