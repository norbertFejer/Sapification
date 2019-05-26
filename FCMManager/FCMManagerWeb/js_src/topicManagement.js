function getTopicsFromDB() {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken
    };

    $.ajax({
        url: 'http://localhost/FCMManager/api/topic/topicManagement.php?method=getTopics',
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            listTopics(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function listTopics(data) {
    
    listValues = "";
    for (const key of Object.keys(data)) {
        listValues += "<li class='list-group-item' value='" + data[key].id + "' >" + data[key].topicName;
        listValues += "<button type='button' class='btn btn-outline-dark float-right sbtn' value='";

        if (data[key].subscribed === "1") {
            listValues += "u" + data[key].id + "'> Unsubscribe";
        } else {
            listValues += "s" + data[key].id + "'> Subscribe";
        }

        listValues += "</button></li>";
    }

    document.getElementById("topicList").innerHTML = listValues;

    //add event listener to buttons
    addEventListenerToSbtn();
}

function addEventListenerToSbtn() {

    //get all elements with given class
    var classname = document.getElementsByClassName("sbtn");

    for (var i = 0; i < classname.length; i++) {
        classname[i].addEventListener('click', sbtnClickEvent, false);
    }
}

var sbtnClickEvent = function(e) {
    
    let answer = confirm("Do you want to subscribe for topic?");
    if (answer == false) {
        return;
    }

    if (localStorage.getItem("user") == null) {
        return;
    }

    let str = e.target.value;
    if (str.substr(0, 1) === "s") {
        subscribeUserForTopic(str.substr(1), e.target);
    } else {
        unsubscribeUserFromTopic(str.substr(1), e.target);
    }

};

function subscribeUserForTopic(topicId, btn) {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken
    };

    URL = 'http://localhost/FCMManager/api/topic/topicManagement.php?method=subscribe&topic=' + topicId;
    $.ajax({
        url: URL,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            changeSbtnValue(btn);
            getSubscriptionResponse(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
    console.log(user.stsTokenManager.accessToken);
}

function unsubscribeUserFromTopic(topicId, btn) {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken
    };

    URL = 'http://localhost/FCMManager/api/topic/topicManagement.php?method=unsubscribe&topic=' + topicId;
    $.ajax({
        url: URL,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            changeSbtnValue(btn);
            getUnsubscriptionResponse(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function changeSbtnValue(btn) {

    if (btn.value.substr(0, 1) === "s") {
        btn.innerHTML = "Unsubscribe";
        btn.value  = "u" + btn.value.substr(1);
    } else {
        btn.innerHTML = "Subscribe";
        btn.value = "s" + btn.value.substr(1);
    }
}

function addTopic() {

    let topicName = document.getElementById("newTopicName").value;
    if (topicName.trim() === "") {
        showAlert("Topic name is not defined!");
        return;
    }

    let txt = "Do you want to add '" + topicName + "' to topics?";
    let answer = confirm(txt);
    if (answer == true) {
        sendNewTopicRequest(topicName);
    }
}

function manageTopics() {

    getMyTopics();
    document.getElementById("collapsable").style.display = "block";
}

function hideTopicManagement() {
    
    document.getElementById("collapsable").style.display = "none";
}

function sendNewTopicRequest(topicName) {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken
    };

    URL = 'http://localhost/FCMManager/api/topic/topicManagement.php?method=addTopic&topic=' + topicName;
    $.ajax({
        url: URL,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            getNewTopicResponse(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function getNewTopicResponse(data) {

    document.getElementById("newTopicName").value = "";

    if (data['response'] == "406") {
        showAlert("This topic is already on the database!");
    }

    if (data['response'] == "200") {
        showAlert("Topic successfully added to database!");
        getTopicsFromDB();
    }

    if (data['response'] == "400") {
        showAlert("Something went wrong, please try again!");
    }
}

function getSubscriptionResponse(data) {

    if (data['response'] == "200") {
        showAlert("Successfully subscribed for the given topic!");
    }

    if (data['response'] == "400") {
        showAlert("Something went wrong, please try again!");
    }
}

function getUnsubscriptionResponse(data) {

    if (data['response'] == "200") {
        showAlert("Successfully unsubscribed from the given topic!");
    }

    if (data['response'] == "400") {
        showAlert("Something went wrong, please try again!");
    }
}

function getMyTopics() {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken
    };

    $.ajax({
        url: 'http://localhost/FCMManager/api/topic/topicManagement.php?method=getMyTopics',
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            listMyTopics(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function listMyTopics(data) {

    listValues = "";
    for (const key of Object.keys(data)) {
        listValues += "<li class='list-group-item' value='" + data[key].id + "' >" + data[key].topicName;
        listValues += "<button type='button' class='btn btn-outline-dark float-right deleteTopicBtn' value='" + 
                                data[key].id + "'>&times;</button></li>";
    }

    document.getElementById("topicManagementList").innerHTML = listValues;

    //setting the delete buttons click event
    addEventListenerToDeleteTopicBtn();
}

function addEventListenerToDeleteTopicBtn() {

    //get all elements with given class
    var classname = document.getElementsByClassName("deleteTopicBtn");

    for (var i = 0; i < classname.length; i++) {
        classname[i].addEventListener('click', deleteBtnClickEvent, false);
    }
}

function deleteBtnClickEvent(e) {

    let answer = confirm("Do you want to delete this topic?");
    if (answer == false) {
        return;
    }

    deleteTopicRequest(e.target.value);
}

function deleteTopicRequest(topicId) {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken
    };

    URL = 'http://localhost/FCMManager/api/topic/topicManagement.php?method=deleteTopic&topic=' + topicId;
    $.ajax({
        url: URL,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            getDeleteTopicResponse(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });

}

function getDeleteTopicResponse(data) {

    if (data['response'] == "200") {
        getTopicsFromDB();
        getMyTopics();
        showAlert("Topic successfully deleted!");
    }

    if (data['response'] == "400") {
        showAlert("Something went wrong, please try again!");
    }
}

function showTopicManagementOption() {

    document.getElementById('topicManagementOption').style.display = 'block';
}

function setTopicManagementOption() {

    if (localStorage.getItem("userPrivilege") !== null){
        let userPrivilege = JSON.parse(localStorage.getItem("userPrivilege"));
        
        if (userPrivilege > 0) {
            showTopicManagementOption()
        }
    }
}