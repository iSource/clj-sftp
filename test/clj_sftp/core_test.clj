(ns clj-sftp.core-test
  (:require [clojure.test :refer :all]
            [clj-sftp.core :refer :all]))

(deftest clj-sftp-test
  (testing "dir?"
    (is (= true (with-connection [channel {:user "oracle" :password "oracle" :host "10.211.55.7" :port 22}]
                                 (dir? channel "/home/oracle"))))
    (is (= false (with-connection [channel {:user "oracle" :password "oracle" :host "10.211.55.7" :port 22}]
                                  (dir? channel "/home/oracle/file.txt")))))
  (testing "file?"
    (is (= true (with-connection [channel {:user "oracle" :password "oracle" :host "10.211.55.7" :port 22}]
                                 (file? channel "/home/oracle/file.txt"))))
    (is (= false (with-connection [channel {:user "oracle" :password "oracle" :host "10.211.55.7" :port 22}]
                                  (file? channel "/home/oracle"))))))
