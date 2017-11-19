(ns clj-sftp.core
  (:require [clojure.java.io :as io])
  (:import [com.jcraft.jsch JSch SftpException ChannelSftp]))

(defmacro with-connection
  "Open a sftp channel to execute some file operation and then close then channel."
  [binding & body]
  `(let [server-spec# ~(second binding)
         session# (doto (.getSession (JSch.) (:user server-spec#) (:host server-spec#) (:port server-spec#))
                    (.setConfig "StrictHostKeyChecking" "no")
                    (.setPassword (:password server-spec#))
                    (.connect))
         ~(first binding) (doto (.openChannel session# "sftp")
                            (.connect))]
     (try
       ~@body
       (finally
         (.disconnect ~(first binding))
         (.disconnect session#)))))

(defn lstat
  "Get statistics of the pointed path."
  [channel path]
  (.lstat channel path))

(defn exists?
  [channel path]
  (try
    (lstat channel path)
    true
    (catch SftpException e
      (if (= (.id e) ChannelSftp/SSH_FX_NO_SUCH_FILE)
        false
        (throw e)))))

(defn dir?
  "Given a path, return true if it is a directory."
  [channel path]
  (.isDir (lstat channel path)))

(defn file?
  "Given a path, return true if it is a file."
  [channel path]
  ((complement dir?) channel path))

(defn put
  "Transfer a file to remote server."
  [channel src dest]
  (.put channel src dest))

(defn mkdir
  "Create a directory on remote server."
  [channel dir]
  (.mkdir channel dir))

(defn mkdirs
  "Create a directory on remote server, also create all parent directory."
  [channel dir]
  (when-not (exists? channel dir)
    (mkdirs channel (.getParent (io/file dir)))
    (mkdir channel dir)))
