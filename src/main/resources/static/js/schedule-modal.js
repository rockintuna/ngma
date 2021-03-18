$(document).ready(function () {
    $(document).on("click","#scheduleSubmit",function (event) {
        submitScheduleForm();
        return false;
    });
    $(document).on("click","#scheduleModify",function (event) {
        modifyScheduleForm();
        return false;
    });
    $(document).on("click","#scheduleRemove",function (event) {
        removeScheduleForm();
        return false;
    });
});

function submitScheduleForm(){
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/schedule",
        contentType: "application/json",
        cache:false,
        data: JSON.stringify($("form#scheduleForm").serializeObject()),
        success: function(response){
            $("#newScheduleModal").modal('hide');
            var pageNum = $('li.page-item.active').val();
            printSchedules(pageNum);
        },
        error: function(e){
            alert(e.responseText);
        }
    });
}

function modifyScheduleForm(){
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/schedule/modify",
        contentType: "application/json",
        cache:false,
        data: JSON.stringify($("form#modifyScheduleForm").serializeObject()),
        success: function(response){
            $("#modifyScheduleModal").modal('hide');
            var pageNum = $('li.page-item.active').val();
            printSchedules(pageNum);
        },
        error: function(e){
            alert(e.responseText);
        }
    });
}

function removeScheduleForm(){
    $.ajax({
        type: "DELETE",
        url: "http://localhost:8080/schedule",
        contentType: "application/json",
        cache:false,
        data: JSON.stringify(checkedSchedules()),
        success: function(response){
            var pageNum = $('li.page-item.active').val();
            printSchedules(pageNum);
        },
        error: function(e){
            alert(e.responseText);
        }
    });
}

function checkedSchedules() {
    var checkedSchedules = [];
    $('input[name="scheduleId"]:checked').each(function(i){
        checkedSchedules.push($(this).val());
    });

    return checkedSchedules;
}