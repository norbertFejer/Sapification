<?php

    class TopicDb {

        private $conn;
        private $topicTbl = "topics";
        private $subscribeTbl = "subscribes";

        public function __construct($db) {

            $this->conn = $db;
        }

        public function getTopics($userId) {

            $query = 'WITH subscribedTopicIds as 
                (SELECT topicId FROM ' . $this->subscribeTbl . ' WHERE userId = :userId )
                SELECT id, topicName, 
                    CASE WHEN id IN (SELECT * FROM subscribedTopicIds) THEN "1" ELSE "0" END as subscribed
                FROM ' . $this->topicTbl . ' ORDER BY topicName ASC';
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":userId", $userId);
            $stmt->execute();
            $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

            return $result;
        }

        public function addTopic($userId, $topicName) {

            $isNewTopic = $this->isRegisteredTopic($topicName);

            if (!$isNewTopic) {
                return 406;
            }

            try {
                $query = 'INSERT INTO ' . $this->topicTbl . ' VALUES(default, :userId, :topicName)';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":userId", $userId);
                $stmt->bindParam(":topicName", $topicName);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
                return 400;
            }

            return 200;
        }

        public function isRegisteredTopic($topicName) {

            $query = 'SELECT COUNT(*) AS topicNum FROM ' . $this->topicTbl . ' WHERE topicName = :topicName';

            try{
                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":topicName", $topicName);
                $stmt->execute();
                $result = $stmt->fetchAll();
            } catch (Exception $e) {
                die("There's an error in the query!");
                return 400;
            }

            if ($result[0]['topicNum']  != 0) {
                return FALSE;
            } else {
                return TRUE;
            }
        }

        public function getTopicsByUserId($userId) {

            $query = 'SELECT id, topicName FROM ' . $this->topicTbl . ' WHERE userId = :userId ORDER BY topicName ASC';
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":userId", $userId);
            $stmt->execute();
            $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

            return $result;
        }

        public function deleteTopicById($topicId) {

            try {
                $query = "DELETE FROM " . $this->topicTbl . ' WHERE id = :topicId';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":topicId", $topicId);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
                return 400;
            }

            return 200;
        }
        
    }

?>