function resetInputFields() {

    //get all elements with given class
    var classname = document.getElementsByClassName("topicInput");

    for (var i = 0; i < classname.length; i++) {
        classname[i].value= "";
    }

    document.getElementById("topicDropdown").selectedIndex = 0;
}

function sendNotification() {

    title = document.getElementById("topicTitle").value;
    body = document.getElementById("topicText").value;
    topic = document.getElementById("topicDropdown").value;

    if (title.trim() === "" || body.trim() === "") {
        showAlert("Empty fill is not allowed!");
        return;
    }

    if (topic === "default") {
        showAlert("You have to select a topic!");
        return;
    }

    sendNewNotification(title, body, topic);
}

function fillTopicDropdown(data) {

    optionValues = "<option value='default'>Select topic...</option>";
    for (const key of Object.keys(data)) {
        optionValues += "<option value='" + data[key].id + "'>" + data[key].topicName + "</option>";
    }

    document.getElementById("topicDropdown").innerHTML = optionValues;

}

function getMyTopicsForDopdown() {

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
            fillTopicDropdown(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function sendNewNotification(title, body, topic) {

    let user = JSON.parse(localStorage.getItem("user"));
    console.log('notificatio' + topic)
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken,
        'userId': user.uid,
        'title': title,
        'body': body,
        'topic': topic
    };

    $.ajax({
        url: 'http://localhost/FCMManager/api/notification/sendNotification.php',
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            getSendNotificationResponse(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function getSendNotificationResponse(data) {

    resetInputFields();

    if (data['response'] == "200") {
        showAlert("Notification sent successfully!");
    }

    if (data['response'] == "400") {
        showAlert("Something went wrong, please try again!");
    }
}