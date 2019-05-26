function getNotificationsFromDB() {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken,
        'userId': user.uid
    };

    $.ajax({
        url: 'http://localhost/FCMManager/api/notification/notificationManagement.php?method=getMyNotifications',
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            listNotificationHistory(data);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function listNotificationHistory(data) {
    
    listValues = "";
    for (const key of Object.keys(data)) {  

        listValues += '<div id="n' + data[key].notificationId + '" class="notifyLst" value="' + data[key].notificationId + '">';
        
        //1 - unseen
        //0 - seen
        if ( data[key].seen == 0){
            listValues += '<a href="http://localhost/FCMManager/FCMManagerWeb/php_src/index.php#/notifications" value="1" class="list-group-item list-group-item-action flex-column align-items-start">';
        } else {
            listValues += '<a href="http://localhost/FCMManager/FCMManagerWeb/php_src/index.php#/notifications" value="0" class="list-group-item list-group-item-action flex-column align-items-start active">';
        }
        listValues +=
                    '<div class="d-flex w-100 justify-content-between">' +
                        '<h5 class="mb-1">' + data[key].notificationTitle + '</h5>'+
                        '<small>' + data[key].notificationDate + '</small>'+
                    '</div>'+
                    '<p class="mb-1">' + data[key].notificationBody + '</p>'+
                    '</a></div>';
    }

    document.getElementById("notificationList").innerHTML = listValues;

    addEventListenerToNotifyLst();
}

function addEventListenerToNotifyLst() {

    //get all elements with given class
    var classname = document.getElementsByClassName("notifyLst");

    for (var i = 0; i < classname.length; i++) {
        classname[i].addEventListener('click', notifyLstClickEvent, false);
    }

}

var notifyLstClickEvent = function(e) {
    
    let targetElem = e.currentTarget;
    //if the given notification is unseen
    if (targetElem.firstChild.attributes.value.value == 1) {
        return;
    }

    let notificationId = targetElem.attributes.value.value;
    setNotificationToSeen(notificationId);
};

function setNotificationToSeen(notificationId) {

    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken,
        'userId': user.uid,
        'notificationId': notificationId
    };

    $.ajax({
        url: 'http://localhost/FCMManager/api/notification/notificationManagement.php?method=setNotificationToSeen',
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            console.log(data);
            setNotificationToSeenResponse(data, notificationId);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function setNotificationToSeenResponse(data, notificationId) {

    //if the notification successfully set to seen
    if (data.response == 200) {
        let id = "n" + notificationId;
        let elem = document.getElementById(id);
        elem.firstChild.classList.remove("active");
    }
}