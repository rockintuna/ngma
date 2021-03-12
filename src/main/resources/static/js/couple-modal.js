$(document).ready(function () {
    $(document).on("click","#coupleSubmit",function (event) {
        submitCoupleForm();
        return false;
    });
});

function submitCoupleForm(){
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/account/pick",
        contentType: "application/json",
        dataType: "json",
        cache:false,
        data: JSON.stringify($("form#coupleForm").serializeObject()),
        success: function(response){
            $("#coupleModal").modal('hide');
            loadCoupleState();
            alert("정상적으로 요청되었습니다.");
        },
        error: function(request){
            $(".form-control-error").empty();
            $(".form-control-error").append(request.responseText);
        }
    });
}

$(document).ready(function () {
    $(document).on("click","[data-bs-dismiss='modal']",function (event) {
        $(".form-control-error").empty();
    });
});