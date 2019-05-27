<?php

    include_once 'Device.php';
    include_once 'Subscribe.php';
    include_once 'Topic.php';

    class User {

        private $conn;
        private $table = "users";

        public function __construct($db) {

            $this->conn = $db;
        }

        public function getUserPrivilege($userId) {

            $query = 'SELECT privilege FROM ' . $this->table . ' WHERE userId = :userId LIMIT 1';

            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":userId", $userId);
            $stmt->execute();
            $result = $stmt->fetchAll();

            //check if there are user with given userId
            $privilege = -1;
            foreach( $result as $row ) {
                $privilege = $row['privilege'];
            }

            return $privilege;
        }

        private function addNewUser($userId, $userName, $userEmail) {

            try {
                $query = 'INSERT INTO ' . $this->table . ' VALUES(:userId, :userName, :userEmail, 0)';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":userId", $userId);
                $stmt->bindParam(":userName", $userName);
                $stmt->bindParam(":userEmail", $userEmail);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
            }
        }

        public function setUserPrivilege($userId, $privilege) {

            try {
                $query = 'UPDATE ' . $this->table . ' SET privilege = :privilege WHERE userId = :userId';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":userId", $userId);
                $stmt->bindParam(":privilege", $privilege);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
            }
        }

        public function subscribeDeviceToAllUserTopics($userId, $deviceId) {

            $subscribe = new Subscribe($this->conn);
            $topicArr = $subscribe->getSubscribedTopics($userId);

            $topic = new Topic($this->conn);

            foreach ($topicArr as $topicName) {
                $topic->subscribeTopic($topicName['topicId'], $deviceId);
            }
        }

        public function newUserLogin($userId, $userName, $userEmail, $deviceId) {
            
            $userPrivilege = $this->getUserPrivilege($userId);

            //if user had not registered in the database before
            if ($userPrivilege == -1) {
                $this->addNewUser($userId, $userName, $userEmail);
                $userPrivilege = 0;
            }

            //check if given deviceId is registered in database
            $device = new Device($this->conn);
            //if not we register it
 
            if ($device->isRegisteredDeviceId($userId, $deviceId) == FALSE) {
                $device->registerNewDevice($userId, $deviceId);

                $deviceToken = substr($deviceId, 1, -1);
                //subscribe new registered device to all topics, for those are the user subscribed before
                $this->subscribeDeviceToAllUserTopics($userId, $deviceToken);
            }

            return $userPrivilege;
        }

        public function getUsers($userId) {

            $query = 'SELECT userName, userEmail, privilege FROM ' . $this->table . ' WHERE userId != :userId';
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":userId", $userId);
            $stmt->execute();
            $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

            return $result;
        }

        public function setUserPrivilegeByEmail($userEmail, $privilege) {

            $privilegeValue = null;
            switch ($privilege) {
                case "user":
                    $privilege = 0;
                    break;
                case "superuser":
                    $privilege = 1;
                    break;
                case "admin":
                    $privilege = 2;
                    break;
                default:
                    $privilege = -1;
            }

            if ($privilege == -1){
                //error during setting privilege value
                return 400; //400 is the error response code
            }

            try {
                $query = 'UPDATE ' . $this->table . ' SET privilege = :privilege WHERE userEmail = :userEmail';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":userEmail", $userEmail);
                $stmt->bindParam(":privilege", $privilege);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
                return 400;
            }

            return 200;
        }

        public function getUserNameById($userId) {

            $query = 'SELECT userName FROM ' . $this->table . ' WHERE userId = :userId';
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":userId", $userId);
            $stmt->execute();
            $result = $stmt->fetchALL(PDO::FETCH_ASSOC);

            return $result;
        }

    }

?>