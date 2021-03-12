$(document).ready(function () {
    $(document).on("click","#schduleSubmit",function (event) {
        submitScheduleForm();
        return false;
    });
});

function submitScheduleForm(){
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/schedule",
        contentType: "application/json",
        dataType: "json",
        cache:false,
        data: JSON.stringify($("form#scheduleForm").serializeObject()),
        success: function(response){
            $("#newScheduleModal").modal('hide');
            loadSchedules();
        },
        error: function(){
            alert("Error");
        }
    });
}