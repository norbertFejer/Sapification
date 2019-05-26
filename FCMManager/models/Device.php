<?php

    class Device {

        private $conn;
        private $table = "devices";

        public function __construct($db) {

            $this->conn = $db;
        }

        public function isRegisteredDeviceId($userId, $deviceId) {

            //checking if deviceId is already registered in database
            $query = 'SELECT 1 FROM ' . $this->table . " WHERE userId = :userId AND deviceId = :deviceId";
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":userId", $userId);
            $stmt->bindParam(":deviceId", $deviceId);
            $stmt->execute();
            $result = $stmt->fetchAll();

            $isRegisteredId = FALSE;

            foreach( $result as $row ) {
                $isRegisteredId = TRUE;
            }

            return $isRegisteredId;
        }

        public function registerNewDevice($userId, $deviceId) {

            //add given deviceId with userId to database
            try {
                $query = 'INSERT INTO ' . $this->table . ' VALUES(default, :userId, :deviceId)';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":userId", $userId);
                $stmt->bindParam(":deviceId", $deviceId);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
            }            
        }

        public function getUserDevices($userId) {

            $query = 'SELECT deviceId FROM ' . $this->table . ' WHERE userId = :userId';
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":userId", $userId);
            $stmt->execute();
            $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

            return $result;
        }
        
    }

?>