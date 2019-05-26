<?php

    class Subscribe {

        private $conn;
        private $table = "subscribes";

        public function __construct($db) {

            $this->conn = $db;
        }

        public function subscribeForTopic($userId, $topicId) {

            try {
                $query = 'INSERT INTO ' . $this->table . ' VALUES(:userId, :topicId)';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":userId", $userId);
                $stmt->bindParam(":topicId", $topicId);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
                return 400;
            }

            return 200;
        }

        public function unsubscribeFromTopic($userId, $topicId) {

            try {
                $query = 'DELETE FROM ' . $this->table . ' WHERE userId = :userId AND topicId = :topicId';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":userId", $userId);
                $stmt->bindParam(":topicId", $topicId);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
                return 400;
            }

            return 200;
        }

        public function getSubscribedTopics($userId) {

            $query = 'SELECT topicId FROM ' . $this->table . ' WHERE userId = :userId';
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":userId", $userId);
            $stmt->execute();
            $result = $stmt->fetchALL(PDO::FETCH_ASSOC);

            return $result;
        }

        public function getSubscribedUsersForTopic($topicId){

            $query = 'SELECT userId FROM ' . $this->table . ' WHERE topicId = :topicId';
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":topicId", $topicId);
            $stmt->execute();
            $result = $stmt->fetchALL(PDO::FETCH_ASSOC);

            return $result;
        }
    }

?>