function getUsersFromDB() {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken,
        'userId': user.uid
    };

    $.ajax({
        url: 'http://localhost/FCMManager/api/user/userManagement.php?method=getUsers',
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            listUsers(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function listUsers(data) {
    
    listValues = "";
    for (const key of Object.keys(data)) {
        listValues += "<li class='list-group-item'>" +
            data[key].userName + " | " + data[key].userEmail;
        listValues += "<select class='btn float-right' style='border-color: #e7e7e7; margin-right: 60px;'>";  

        if (data[key].privilege == 0) {
            listValues += "<option selected value='user'>User</option>";
        } else {
            listValues += "<option value='user'>User</option>";
        }

        if (data[key].privilege == 1) {
            listValues += "<option selected value='superuser'>Superuser</option>"
        } else {
            listValues += "<option value='superuser'>Superuser</option>"
        }

        if (data[key].privilege == 2){
            listValues += "<option selected value='admin'>Admin</option>"
        } else {
            listValues += "<option value='admin'>Admin</option>"
        }

        listValues += "</select>";
        listValues +=
            "<button value='" + data[key].userEmail + "' type='button' class='btn btn-outline-dark usrBtn'>" + 
                "Save</button>";
        listValues += "</li>";
    }

    document.getElementById("userList").innerHTML = listValues;

    //add event listener to save buttons
    addEventListenerToPrivilageSaveBtn();
}

function addEventListenerToPrivilageSaveBtn() {

    //get all elements with given class
    var classname = document.getElementsByClassName("usrBtn");

    for (var i = 0; i < classname.length; i++) {
        classname[i].addEventListener('click', usrBtnClickEvent, false);
    }
}

var usrBtnClickEvent = function(e) {
    
    let answer = confirm("Do you want to change this privilege?");
    if (answer == false) {
        return;
    }
    let parentElement = e.target.parentElement;
    let userPrivilege = parentElement.childNodes[1].value;
    let userEmail = e.target.value;

    setUserPrivilegeByEmail(userEmail, userPrivilege);
};

function setUserPrivilegeByEmail(userEmail, privilege) {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken,
        'userEmail': userEmail,
        'privilege': privilege
    };

    $.ajax({
        url: 'http://localhost/FCMManager/api/user/userManagement.php?method=setUserPrivilegeByEmail',
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            setUserPrivilegeResponse(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function setUserPrivilegeResponse(data) {

    if (data['response'] == "200") {
        getUsersFromDB();
        showAlert("Privilege successfully changed!");
    }

    if (data['response'] == "400") {
        showAlert("Something went wrong, please try again!");
    }
}