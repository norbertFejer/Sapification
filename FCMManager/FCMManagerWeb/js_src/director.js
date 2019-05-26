var topics = function () { 

    $.get("../php_src/topicManagement.php", function(data) {
        $("#container").html(data);

        //setting topic management options for given rules(user, superuser, admin)
        setTopicManagementOption();

        //getting topics from database and filling the topic list
        getTopicsFromDB();
    }); 
};

var sendNotification = function () { 

    $.get("../php_src/sendNotification.php", function(data) {
        $("#container").html(data);

        //filling the topic selection dropdown
        getMyTopicsForDopdown();
    }); 
};

var users = function () {

    $.get("../php_src/userManagement.php", function(data) {
        $("#container").html(data);

        getUsersFromDB();
    }); 
};

var notifications = function () {

    $.get("../php_src/notificationManagement.php", function(data) {
        $("#container").html(data);

        //check if notification badge is shown
        hideNotificationBadge();
        getNotificationsFromDB();
    }); 
}

var home = function () {
    
    console.log("home");
    $.get("../php_src/home.php", function(data) {
        $("#container").html(data);
    }); 
}

var routes = {
    "/" : home,
    '/topics': topics,
    '/sendNotification': sendNotification,
    '/users' : users,
    '/notifications' : notifications
    };

var router = Router(routes);

router.init();