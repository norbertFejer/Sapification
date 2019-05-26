<?php

    header('Access-Control-Allow-Origin: *');
    header('Content-Type: application/json');
    header('Access-Control-Allow-Methods: POST');
    header('Access-Control-Allow-Headers: Access-Control-Allow-Headers,Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');
    
    include_once '../../models/Topic.php';
    include_once '../../models/FirebaseAuth.php';
    include_once '../../models/TopicDb.php';
    include_once '../../config/Database.php';
    include_once '../../models/Device.php';
    include_once '../../models/Subscribe.php';
    
    //ex: https://topicManagement.php?method=subscribe&topic=teszt
    $data = json_decode(file_get_contents("php://input"));

    $userId = "";

    if (isset($data->authorizationToken)) {
        $firebaseAuth = new FirebaseAuth();

        $userId = $firebaseAuth->isValidToken($data->authorizationToken);
        if ($userId == -1) {
            die("Invalid authorization token");
        } else {
            $database = new Database();
            $GLOBALS['db'] = $database->connect(); 
        }

    }
    
    //getting the requested method
    if (isset($_GET['method'])) {

        switch ($_GET['method']) {
            case 'subscribe':
                subscribeTopic($data);
                break;
            case 'unsubscribe':
                unsubscribeTopic($data);
                break;
            case 'delete':
                echo 'delete';
                break;
            case 'getTopics':
                getTopics($userId);
                break;
            case 'addTopic':
                addTopic();
                break;
            case 'getMyTopics':
                getTopicsByUserId();
                break;
            case 'deleteTopic':
                deleteTopic();
                break;
            default:
                echo 'Error';
        }

    }

    function subscribeTopic($data) {

        if(isset($_GET['topic'])) {

            //getting all devices for given user
            $device = new Device($GLOBALS['db']);
            $deviceArr = $device->getUserDevices($GLOBALS['userId']);

            $subscribe = new Subscribe($GLOBALS['db']);
            $ret = $subscribe->subscribeForTopic($GLOBALS['userId'], $_GET['topic']);

            if ($ret == 400) {

                echo '{"response": "400"}';
                return;
            }

            try {
                $topic = new Topic();
                foreach ($deviceArr as &$device) {
                    //subscribe all user devices for given topic
                    $deviceId = substr($device['deviceId'], 1, -1);
                    $topic->subscribeTopic($_GET['topic'], $deviceId);
                }
            } catch (Exception $e) {
                die("There's an error in the query!");
            } 

            echo '{"response": "200"}';

        }

    }

    function unsubscribeTopic($data) {

        if(isset($_GET['topic'])) {

            //storing subscription locally in database
            $subscribe = new Subscribe($GLOBALS['db']);
            $ret = $subscribe->unsubscribeFromTopic($GLOBALS['userId'], $_GET['topic']);

            if ($ret == 400) {

                echo '{"response": "400"}';
                return;
            }

            try {
                //getting all devices for given user
                $device = new Device($GLOBALS['db']);
                $deviceArr = $device->getUserDevices($GLOBALS['userId']);

                $topic = new Topic();
                foreach ($deviceArr as &$device) {
                    //subscribe all user devices for given topic
                    $deviceId = substr($device['deviceId'], 1, -1);
                    $topic->unsubscribeTopic($_GET['topic'], $deviceId);
                }
            } catch (Exception $e) {
                die("There's an error in the query!");
            }

            echo '{"response": "200"}';

        }

    }

    function responseOK() {

        header("HTTP/1.0 200 OK");
    }

    function responseError() {

        header("HTTP/1.0 200 OK");
    }

    function getTopics($userId) {

        $topicDb = new TopicDb($GLOBALS['db']);
        $topics = $topicDb->getTopics($userId);
        
        echo json_encode($topics, JSON_FORCE_OBJECT);
    }

    function addTopic() {

        if(isset($_GET['topic'])) {
            $topicDb = new TopicDb($GLOBALS['db']);
            $ret = $topicDb->addTopic($GLOBALS['userId'], $_GET['topic']);

            $response = '{"response": "' . $ret . '"}';

            echo $response;
        }
    }

    function getTopicsByUserId() {

        $topicDb = new TopicDb($GLOBALS['db']);
        $topics = $topicDb->getTopicsByUserId($GLOBALS['userId']);

        echo json_encode($topics, JSON_FORCE_OBJECT);
    }

    function deleteTopic() {

        if(isset($_GET['topic'])) {
            $topicDb = new TopicDb($GLOBALS['db']);
            $ret = $topicDb->deleteTopicById($_GET['topic']);

            $response = '{"response": "' . $ret . '"}';

            echo $response;
        }

    }
    
?> 