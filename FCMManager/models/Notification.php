<?php

    require_once 'C:/xampp/htdocs/FCMManager/vendor/autoload.php';

    use Kreait\Firebase\Factory;
    use Kreait\Firebase\ServiceAccount;
    use Kreait\Firebase\Messaging\CloudMessage;

    class Notification {

        private $firebase;
        private $messaging;
        private $conn;
        private $table = "notifications";

        public function __construct($db) {

            $this->conn = $db;

            //initialize firebase
            $acc = ServiceAccount::fromJsonFile(__DIR__ . '/../secret/fcmmanager-9f357-874ee6c48cab.json');
            $this->firebase = (new Factory)->withServiceAccount($acc)->create();
            $this->messaging = $this->firebase->getMessaging();
        }

        public function sendNotification($topic, $title, $body) {

            //send notification to given topic
            $message = CloudMessage::fromArray([
                'topic' => $topic,
                'notification' => ['title' => $title, 'body' => $body]
            ]);

            $this->messaging->send($message);
        }

        public function addNewNotification($userId, $title, $body){

            try {
                $query = 'INSERT INTO ' . $this->table . 
                    ' VALUES(default, :userId, :title, :body, default, default)';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":userId", $userId);
                $stmt->bindParam(":title", $title);
                $stmt->bindParam(":body", $body);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
                return 400;
            }

            return 200;
        }

        public function getMyNotifications($userId) {

            $query = 'SELECT notificationId, notificationTitle, notificationBody, 
                DATE_FORMAT(notificationDate, "%Y/%m/%d %H:%i") AS notificationDate, seen FROM ' . 
                $this->table . ' WHERE userId = :userId ORDER BY notificationDate DESC';
            
            $stmt = $this->conn->prepare($query);
            $stmt->bindParam(":userId", $userId);
            $stmt->execute();
            $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

            return $result;
        }

        public function setNotificationToSeen($userId, $notificationId) {

            try {
                $query = 'UPDATE ' . $this->table . ' SET seen = 0 WHERE notificationId = :notificationId
                    AND :userId = userId';

                $stmt = $this->conn->prepare($query);
                $stmt->bindParam(":notificationId", $notificationId);
                $stmt->bindParam(":userId", $userId);
                $stmt->execute();
            } catch (Exception $e) {
                die("There's an error in the query!");
                return 400;
            }

            return 200;
        }

    }

?> 