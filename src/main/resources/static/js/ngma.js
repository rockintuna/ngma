function fetchPageToMain(name) {
    fetch(name).then(function(response) {
        response.text().then(function(text) {
            document.querySelector('#main').innerHTML = text;
        })
    });
}

$.fn.serializeObject =  function() {
    var obj = null;
    try {
        if(this[0].tagName && this[0].tagName.toUpperCase() === "FORM" ) {
            var arr = this.serializeArray();
            if(arr){
                obj = {};
                jQuery.each(arr, function() {
                    obj[this.name] = this.value;
                });
            }
        }
    } catch(e) {
        alert(e.message);
    } finally {}
    return obj;
}

$(document).ready(function () {
    $(document).on("click","#accountSubmit",function (event) {
        submitAccountForm();
        return false;
    });
    $(document).on("click","#accountModify",function (event) {
        modifyAccountForm();
        return false;
    });
    $(document).on("click","#accountDelete",function (event) {
        deleteAccountForm();
        return false;
    });
});

function submitAccountForm(){
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/account",
        contentType: "application/json",
        dataType: "html",
        cache:false,
        data: JSON.stringify($("form#accountForm").serializeObject()),
        success: function(response){
            window.location.href = "http://localhost:8080/login";
        },
        error: function(e){
            alert(e);
        }
    });
}

function modifyAccountForm(){
    $.ajax({
        type: "POST",
        url: "http://localhost:8080/account/update",
        contentType: "application/json",
        dataType: "html",
        cache:false,
        data: JSON.stringify($("form#accountModifyForm").serializeObject()),
        success: function(response){
            window.location.href = "http://localhost:8080/login";
        },
        error: function(e){
            alert(e.responseText);
        }
    });
}

function deleteAccountForm(){
    $.ajax({
        type: "DELETE",
        url: "http://localhost:8080/account/delete",
        cache:false,
        success: function(response){
            window.location.href = "http://localhost:8080/logout";
        },
        error: function(e){
            alert(e.responseText);
            window.location.href = "http://localhost:8080/logout";
        }
    });
}

function loadSchedules() {
    $.ajax({
        url: "http://localhost:8080/schedules",
        type: "GET",
        dataType: "json",
        success: function(data) {
            addScheduleRow(data);
        },
        error: function(e) {
            alert(e);
        }
    });
}

function addScheduleRow(schedules) {
    var tableBody = $("tbody#scheduleTable");
    $(tableBody).children("tr").remove();
    $.each(schedules, function (i, item) {
        var newRow = $("<tr></tr>").appendTo(tableBody);
        $(newRow).append('<td><input class="form-check-input" name="scheduleId" type="checkbox" value="'+item.id+'"></td>');
        $(newRow).append("<td>" + item.dateTime.substr(0, 10) + "</td>");
        $(newRow).append("<td>" + item.place + "</td>");
        $(newRow).append("<td>" + item.title + "</td>");
        if (item.personal === true) {
            $(newRow).append("<td>Alone</td>");
        } else {
            $(newRow).append("<td>Together</td>");
        }
        $(newRow).append('<td><a type="button" onclick="showModifyScheduleModal('+item.id+')"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-pencil" viewBox="0 0 16 16">\n' +
            '            <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5L13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175l-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z"/>\n' +
            '        </svg></td>');
    });
}

function showModifyScheduleModal(id) {
    $('#modifyScheduleModal').modal('show');
    $('#modifyScheduleForm > .modal-body').append("<input type=\"hidden\" name=\"id\" value=\""+id+"\">")
}

function loadCoupleState() {
    $.ajax({
        url: "http://localhost:8080/account/loverState",
        type: "GET",
        dataType: "json",
        success: function(data) {
            checkLoverState(data);
        },
        error: function(request) {
            alert(request.responseText);
        }
    });
}

function checkLoverState(loverState) {
    printWaiterTable(loverState.hasWaiters);
    loverStateResult(loverState.name);
}

function printWaiterTable(hasWaiters) {
    $("table#coupleTable").empty();
    if ( hasWaiters == true ) {
        $("table#coupleTable").load('/main-part/waiter-main.html');
        loadWaiters();
    }
}

function loadWaiters() {
    $.ajax({
        url: "http://localhost:8080/account/waiter",
        type: "GET",
        dataType: "json",
        success: function(data) {
            addWaiterRow(data);
        },
        error: function(request) {
            alert(request.responseText);
        }
    });
}

function addWaiterRow(lover) {
    var tableBody = $("tbody#coupleTableBody");
    $(tableBody).children("tr").remove();
    $.each(lover, function (i, item) {
        var newRow = $("<tr></tr>").appendTo(tableBody);
        $(newRow).append("<td>"+item.name+"</td>");
        $(newRow).append("<td>"+item.email+"</td>");
        $(newRow).append("<td><a class=\"btn btn-outline-primary btn-sm\" type=\"button\" id=\"waiterCheck\" onclick=\"confirmWaiter("+item.id+");\">확인</a></td>");
        $(newRow).append("<td><a class=\"btn btn-outline-secondary btn-sm\" type=\"button\" id=\"waiterReject\" onclick=\"rejectWaiter("+item.id+");\">거절</a></td>");
    });
}

function confirmWaiter(accountId) {
    $.ajax({
        url: "http://localhost:8080/account/waiter/"+accountId,
        type: "POST",
        data: {param: "confirm"},
        dataType: "json",
        success: function(data) {
            alert("확인되었습니다.");
            loadCoupleState();
        },
        error: function(request) {
            alert(request.responseText);
        }
    });
}

function rejectWaiter() {
    $.ajax({
        url: "http://localhost:8080/account/waiter/"+accountId,
        type: "POST",
        data: {param: "reject"},
        dataType: "json",
        success: function(data) {
            alert("거절되었습니다.");
            loadCoupleState();
        },
        error: function(request) {
            alert(request.responseText);
        }
    });
}

function loverStateResult(state) {
    $("div#loverStateResult").empty();
    if ( state == "NOTHING" ) {
        $("div#loverStateResult").append("<p class=\"text-sm-start\">아직 연결된 짝꿍이 없으시네요, 나의 짝꿍에게 신청해보세요!</p><a class=\"btn btn-outline-primary\" type=\"button\" id=\"couple\" data-bs-toggle=\"modal\" data-bs-target=\"#coupleModal\">짝꿍 요청하기</a>");
    } else if ( state == "WAITING" ) {
        loadLover(waitingComment);
    } else {
        loadLover(matchedComment);
    }
}

function loadLover(callback) {
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/account/lover",
        dataType: "json",
        success: function(data) {
            callback(data);
        },
        error: function(request) {
            alert(request.responseText);
        }
    });
}

function waitingComment(data) {
    $("div#loverStateResult")
        .append("<p class=\"text-sm-start\">아직 "+data.name.toString()+"("+data.email.toString()+")" +
            "님으로부터 승인을 대기중입니다. 상대방의 확인이 끝날 때 까지 기다려주세요.</p>" +
            "\<a class=\"btn btn-outline-primary btn-sm\" type=\"button\" id=\"cancelPick\" onclick=\"cancelPick();\">취소하기</a>");
}

function cancelPick() {
    $.ajax({
        url: "http://localhost:8080/account/pick/cancel",
        type: "POST",
        dataType: "json",
        success: function(data) {
            alert("취소되었습니다.");
            loadCoupleState();
        },
        error: function(request) {
            alert(request.responseText);
        }
    });
}

function matchedComment(data) {
    $("div#loverStateResult").append("<p class=\"text-sm-start\">"+data.name.toString()+"("+data.email.toString()+")님과 짝꿍입니다!</p>");
}