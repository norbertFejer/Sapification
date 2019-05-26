<?php

    require_once 'C:/xampp/htdocs/FCMManager/vendor/autoload.php';

    use Kreait\Firebase\Factory;
    use Kreait\Firebase\ServiceAccount;

    class Topic {

        private $firebase;

        public function __construct() {

            $acc = ServiceAccount::fromJsonFile(__DIR__ . '/../secret/fcmmanager-9f357-874ee6c48cab.json');
            $this->firebase = (new Factory)->withServiceAccount($acc)->create();
        }

        public function subscribeTopic($topic, $registrationToken) {

            if (empty($topic) || is_null($topic)){
                return false;
            }

            $this->firebase
                ->getMessaging()
                ->subscribeToTopic($topic, $registrationToken);
        }

        public function unsubscribeTopic($topic, $registrationToken) {

            $this->firebase
                ->getMessaging()
                ->unsubscribeFromTopic($topic, $registrationToken);
        }
    }

?> 