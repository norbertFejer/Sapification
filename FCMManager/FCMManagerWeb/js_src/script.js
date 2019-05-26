window.onload = function() {

    document.getElementById("signOutBtn").addEventListener("click", signOut);

    //initialize database
    firebase.initializeApp(config);

    //setting session datas
    loadInitialState();
}

var provider = new firebase.auth.GoogleAuthProvider();
var messaging = null;
var user = null;

//firebase configuration key
var config = {
    apiKey: "AIzaSyDN37ARaKzr6S1YTGPJvwxepN9hZTK0qpo",
    authDomain: "fcmmanager-9f357.firebaseapp.com",
    databaseURL: "https://fcmmanager-9f357.firebaseio.com",
    projectId: "fcmmanager-9f357",
    storageBucket: "fcmmanager-9f357.appspot.com",
    messagingSenderId: "333185662546"
};

function signIn() {

    firebase.auth().signInWithPopup(provider).then(function(result) {
        // This gives you a Google Access Token. You can use it to access the Google API.
        var token = result.credential.accessToken;
        // The signed-in user info.
        user = result.user;
        console.log("Login successful!");
        console.log(user);

        //saving user object
        localStorage.setItem('user', JSON.stringify(user));

        //redirecting to default home page
        home();

        setMessagingParameters();

    }).catch(function(error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        // The email of the user's account used.
        var email = error.email;
        // The firebase.auth.AuthCredential type that was used.
        var credential = error.credential;
        console.log("Error during login");
    });
}

function setMessagingParameters() {

    getMessagingToken();
    setMessageReceiverCallback();

    //saving messaging object
    localStorage.setItem('messaging', JSON.stringify(messaging));
}

function signOut() {

    let answer = confirm("Do you want to sign out?");
    if (answer == false) {
        return;
    }

    firebase.auth().signOut().then(function() {
        console.log("Signed out!");
        //getting the initial state
        setInitialState();
    }).catch(function(error) {
        console.log("Erro during sign out!");
    });      
}

function getMessagingToken() {

    messaging = firebase.messaging();
    messaging.usePublicVapidKey("BBbDBuM758a5jq8hTVg5pXBHQoN9O9-HcqiNvct-9LRKNCq1QecLDAfmo6cpcf8pSEm-VpMyhBQiPsfjzRTZH88");
    messaging.requestPermission().then(function() {

        console.log('Notification permission granted.');

        messaging.getToken().then(function(currentToken) {

            if (currentToken) {
                console.log(currentToken);
                //saving device token
                localStorage.setItem('deviceToken', JSON.stringify(currentToken));

                if (localStorage.getItem("user") !== null){
                    let user = JSON.parse(localStorage.getItem("user"));
                    
                    //saving user privilege, and setting navbar options
                    let token = '"' + currentToken + '"';
                    configureUserByPrivilege(user.uid, token);
                }

            } else {
                // Show permission request.
                console.log('No Instance ID token available. Request permission to generate one.');
                // Show permission UI.
            }
            }).catch(function(err) {
            console.log('An error occurred while retrieving token. ', err);
            });
        

    }).catch(function(err) {
    console.log('Unable to get permission to notify.', err);
    });
}

function showNewNotificationBadge() {

    badgeValue = document.getElementById("notificationBadge").innerText;

    console.log(badgeValue);
    //refreshing notification badge if notification is arrived
    if (badgeValue === "") {
        document.getElementById("notificationBadge").innerHTML = 1;
    } else {
        document.getElementById("notificationBadge").innerHTML = parseInt(badgeValue) + 1;
    }
}

function setMessageReceiverCallback() {

    messaging.onMessage(function(payload) {
        console.log('Message received. ', payload);
        showNewNotificationBadge()
    });
}

function loadInitialState() {

    if (localStorage.getItem("user") !== null) {
    
        home();

        if (localStorage.getItem("deviceToken") !== null){
            let deviceId = localStorage.getItem("deviceToken");
            let user = JSON.parse(localStorage.getItem("user"));
            
            configureUserByPrivilege(user.uid, deviceId);
        }

        //setting message handler
        if (localStorage.getItem("messaging") === null) {
            console.log("messaging set");
            getMessagingToken();
            setMessageReceiverCallback();
        }
    } else {
        $.get("login.php", function(data) {
            $("#container").html(data);
            document.getElementById("signInBtn").addEventListener("click", signIn);
        });
    }
}

function configureUserByPrivilege(userId, deviceId) {

    httpGetUserPrivilege(userId, deviceId);
}

function httpGetUserPrivilege(userId, deviceId) {
    
    let user = JSON.parse(localStorage.getItem("user"));
    let payload = {
        'authorizationToken': user.stsTokenManager.accessToken,
        'userId': userId,
        'deviceId': deviceId
    };

    $.ajax({
        url: 'http://localhost/FCMManager/api/user/getPrivilege.php',
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify( payload ),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            setUserPrivilege(data.userPrivilege);
        },
        error: function( jqXhr, textStatus, errorThrown ){
            console.log( errorThrown );
        }
    });
}

function setUserPrivilege(privilege) {

    localStorage.setItem('userPrivilege', privilege);
    showNavbarOptionsByPrivilege();
}

function setInitialState() {

    for (let el of document.querySelectorAll('.user')) el.style.visibility = 'hide';
    for (let el of document.querySelectorAll('.superuser')) el.style.visibility = 'hide';
    for (let el of document.querySelectorAll('.admin')) el.style.visibility = 'hide';

    localStorage.removeItem('user');
    localStorage.removeItem('messaging');
    localStorage.removeItem('deviceToken');
    localStorage.removeItem('userPrivilege');

    location.reload();

    loadInitialState();
}

function showNavbarOptionsByPrivilege() {

    if (localStorage.getItem("userPrivilege") !== null) {
        let privilege = localStorage.getItem("userPrivilege");
        
        if (privilege === "0") {
            console.log("user");
            for (let el of document.querySelectorAll('.user')) el.style.visibility = 'visible';
        }
    
        if (privilege === "1") {
            console.log("superuser");
            for (let el of document.querySelectorAll('.user')) el.style.visibility = 'visible';
            for (let el of document.querySelectorAll('.superuser')) el.style.visibility = 'visible';
        }
    
        if (privilege === "2") {
            console.log("admin");
            for (let el of document.querySelectorAll('.user')) el.style.visibility = 'visible';
            for (let el of document.querySelectorAll('.superuser')) el.style.visibility = 'visible';
            for (let el of document.querySelectorAll('.admin')) el.style.visibility = 'visible';
        }
    }
}

function showAlert(txt) {
    
    alert(txt);
}

function hideNotificationBadge() {

    if (document.getElementById("notificationBadge").innerText !== ""){
        document.getElementById("notificationBadge").innerText = "";
    }
       
}