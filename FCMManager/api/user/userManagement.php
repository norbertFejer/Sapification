<?php

    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: GET');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

    include_once '../../config/Database.php';
    include_once '../../models/User.php';
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
            case 'getUsers':
                getUsers($data);
                break;
            case 'setUserPrivilegeByEmail':
                setUserPrivilegeByEmail($data);
                break;
            default:
                echo 'Error';
        }

    }

    function getUsers($data) {

        if (isset($data->userId)){
            $user = new User($GLOBALS['db']);
            $userList = $user->getUsers($data->userId);
            header("HTTP/1.0 200 OK");

            $json_response = json_encode($userList);
            echo $json_response;
        } else {
            echo '{"response": "400"}';
        }
    }

    function setUserPrivilegeByEmail($data) {

        if (isset($data->userEmail) && isset($data->privilege)) {
            $user = new User($GLOBALS['db']);
            $result = $user->setUserPrivilegeByEmail($data->userEmail, $data->privilege);

            echo '{"response": "' . $result . '"}';
        } else {
            echo '{"response": "400"}';
        }
    }

?>