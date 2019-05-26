<?php

    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: GET');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/Database.php';
    include_once '../../models/User.php';
    include_once '../../models/FirebaseAuth.php';

    $data = json_decode(file_get_contents("php://input"));

    $GLOBALS['userName'] = "";
    $GLOBALS['userEmail'] = "";
    if (isset($data->authorizationToken)) {
        $firebaseAuth = new FirebaseAuth();

        if ($firebaseAuth->isValidToken($data->authorizationToken) == -1) {
            die("Invalid authorization token");
        } else {
            $GLOBALS['userName']  = $firebaseAuth->getUserName($data->authorizationToken);
            $GLOBALS['userEmail'] = $firebaseAuth->getUserEmail($data->authorizationToken);
        }
    }


    if (isset($data->userId) && isset($data->deviceId)) {
        response($data);
    }

    function response($data) {

        $database = new Database();
        $db = $database->connect(); 

        $user = new User($db);
        $userPrivilege = $user->newUserLogin($data->userId, $GLOBALS['userName'], $GLOBALS['userEmail'], $data->deviceId);
        header("HTTP/1.0 200 OK");

        $response['userPrivilege'] = $userPrivilege;
        $json_response = json_encode($response);
        echo $json_response;
    }

?>