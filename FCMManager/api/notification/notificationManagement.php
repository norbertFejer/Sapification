<?php

    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: GET');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/Database.php';
    include_once '../../models/Notification.php';
    include_once '../../models/FirebaseAuth.php';

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

    //getting the requested method
    if (isset($_GET['method'])) {

        switch ($_GET['method']) {
            case 'getMyNotifications':
                getMyNotifications($data);
                break;
            case 'setNotificationToSeen':
                setNotificationToSeen($data);
                break;
            default:
                echo 'Error';
        }

    }

    function getMyNotifications($data) {

        if (isset($data->userId)){
            $notification = new Notification($GLOBALS['db']);
            $notificationList = $notification->getMyNotifications($data->userId);
            header("HTTP/1.0 200 OK");

            $json_response = json_encode($notificationList);
            echo $json_response;
        } else {
            echo '{"response": "400"}';
        }
    }

    function setNotificationToSeen($data) {

        if (isset($data->notificationId) && isset($data->userId)) {
            $notification = new Notification($GLOBALS['db']);
            $res = $notification->setNotificationToSeen($data->userId, $data->notificationId);

            echo '{"response": "' . $res . '"}';
        }
    }

?>