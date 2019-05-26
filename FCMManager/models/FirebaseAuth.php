<?php

    require_once 'C:/xampp/htdocs/FCMManager/vendor/autoload.php';

    use Kreait\Firebase\Factory;
    use Kreait\Firebase\ServiceAccount;
    use Firebase\Auth\Token\Exception\InvalidToken;

    class FirebaseAuth {

        private $firebase;

        public function __construct() {

            $acc = ServiceAccount::fromJsonFile(__DIR__ . '/../secret/fcmmanager-9f357-874ee6c48cab.json');
            $this->firebase = (new Factory)->withServiceAccount($acc)->create();
        }

        public function isValidToken($idTokenString) {

            $uid = -1;

            //verify token get by client
            try {
                $verifiedIdToken = $this->firebase->getAuth()->verifyIdToken($idTokenString);
                $uid = $verifiedIdToken->getClaim('sub');
            } catch (InvalidToken $e) {
                echo $e->getMessage();
                return $uid;
            }

            return $uid;
        }

        public function getUserName($idTokenString) {

            $uName = "";
            try {
                $verifiedIdToken = $this->firebase->getAuth()->verifyIdToken($idTokenString);
                $uName = $verifiedIdToken->getClaim('name');
            } catch (InvalidToken $e) {
                echo $e->getMessage();
                return $uName;
            }

            return $uName;
        }

        public function getUserEmail($idTokenString) {

            $uEmail = "";
            try {
                $verifiedIdToken = $this->firebase->getAuth()->verifyIdToken($idTokenString);
                $uEmail = $verifiedIdToken->getClaim('email');
            } catch (InvalidToken $e) {
                echo $e->getMessage();
                return $uEmail;
            }

            return $uEmail;
        }

    }

?> 