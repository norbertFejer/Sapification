<?php

    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: POST');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/Database.php';
    include_once '../../models/Notification.php';
    include_once '../../models/User.php';
    include_once '../../models/FirebaseAuth.php';
    include_once '../../config/Database.php';

    $data = json_decode(file_get_contents("php://input"));

    if (isset($data->authorizationToken)) {
        $firebaseAuth = new FirebaseAuth();

        if ($firebaseAuth->isValidToken($data->authorizationToken) == -1) {
            die("Invalid authorization token");
        } else {
            $database = new Database();
            $GLOBALS['db'] = $database->connect(); 
        }
    }

    if(isset($data->topic) && isset($data->title) && isset($data->body) && isset($data->userId)){

        $userName = getUserNameById($data->userId);

        if ($userName[0]['userName'] != "") {
            $res = response($data, $userName[0]['userName']);
            echo '{"response": "' .$res . '"}';
        } else {
            echo '{"response": "400"}';
        }

    }

    function response($data, $userName) {

        $notification = new Notification($GLOBALS['db']);
        $title = $data->title . " - " . $userName;
        $res = $notification->sendNotification($data->topic, $title, $data->body);

        if ($res == 400) {
            return 400;
        }

        //save notification locally to database
        //return $notification->addNewNotification($data->userId, $title, $data->body);
        return storeNotificationsInDatabase($title, $data->body, $data->topic);
    }

    function getUserNameById($userId) {

        $user = new User($GLOBALS['db']);
        return $user->getUserNameById($userId);
    }

    function storeNotificationsInDatabase($title, $body, $topicId) {

        $subscribes = new Subscribe($GLOBALS['db']);
        $userIdArr = $subscribes->getSubscribedUsersForTopic($topicId);

        $notification = new Notification($GLOBALS['db']);

        foreach ($userIdArr as &$user) {
            if ($notification->addNewNotification($user['userId'], $title, $body) == 400) {
                return 400;
            }
        }

        return 200;
    }

?>