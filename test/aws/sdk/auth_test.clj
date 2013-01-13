(ns aws.sdk.auth-test
  (:use aws.sdk.auth
        clojure.test)
  (:import java.util.Properties))


;; Helpers

(defn set-properties! [props]
  (doseq [[k v] props]
    (System/setProperty (str k) (str v))))

(defn unset-properties! [props]
  (doseq [k (keys props)]
    (System/clearProperty (str k))))

(defn with-system-properties* [props f]
  (set-properties! props)
  (try (f)
       (finally
         (unset-properties! props))))

(defmacro with-system-properties [props & body]
  `(with-system-properties* ~props (fn [] ~@body)))


;; Tests

(deftest credentials-test
  (testing "credentials"

    (testing "w/ missing keys"
      (is (nil? (credentials {}))
          "returns nil for empty map")
      (is (nil? (credentials {:access-key "access-key"}))
          "returns nil for partial map"))

    (testing "w/ :access-key and :secret-key"
      (is (-> {:access-key "access-key"
               :secret-key "secret-key"}
              credentials
              credentials?)
          "returns credentials"))

    (testing "w/ :access-key, :secret-key, and :session-token"
      (is (-> {:access-key "access-key"
               :secret-key "secret-key"
               :session-token "session-token"}
              credentials
              credentials?)
          "returns credentials"))))

(deftest get-credentials-test
  (testing "get-credentials"

    (testing "w/ default-chain"
      (is (thrown-with-msg?
            com.amazonaws.AmazonClientException
            #"Unable to load AWS credentials from any provider in the chain"
            (get-credentials))
          "throws an exception when no credentials are found in default chain")

      (with-system-properties
        {"aws.accessKeyId" "access-key"
         "aws.secretKey" "secret-key"}
        (let [creds (get-credentials)]
          (is (credentials? creds)
              "returns credentials when found in default chain")
          (is (= "access-key" (.getAWSAccessKeyId creds))
              "access-key is correct")
          (is (= "secret-key" (.getAWSSecretKey creds))
              "secret-key is correct"))))

    (testing "w/ static credentials"
      (let [creds (-> {:access-key "access-key"
                       :secret-key "secret-key"
                       :session-token "session-token"}
                      get-credentials)]
        (is (credentials? creds)
            "returns credentials")
        (is (= "access-key" (.getAWSAccessKeyId creds))
            "access-key is correct")
        (is (= "secret-key" (.getAWSSecretKey creds))
            "secret-key is correct")
        (is (= "session-token" (.getSessionToken creds))
            "session-token is correct")))

    (testing "w/ provider chain"
      (let [static-creds {:access-key "access-key" :secret-key "secret-key"}
            chain (credentials-provider-chain [:default-chain static-creds])
            creds (get-credentials chain)]
        (is (credentials? creds)
            "returns credentials")
        (is (= "access-key" (.getAWSAccessKeyId creds))
            "access-key is correct")
        (is (= "secret-key" (.getAWSSecretKey creds))
            "secret-key is correct")))

    (testing "w/ function and dynamic credentials"
      (let [creds-atom (atom {:access-key "access-key" :secret-key "secret-key"})
            creds-fn (fn [] (credentials @creds-atom))
            provider (credentials-provider creds-fn)]

        (testing "initial value"
          (let [creds (get-credentials provider)]
            (is (credentials? creds)
                "returns credentials")
            (is (= "access-key" (.getAWSAccessKeyId creds))
                "access-key is correct")
            (is (= "secret-key" (.getAWSSecretKey creds))
                "secret-key is correct")))

        (testing "refreshed value"
          (reset! creds-atom {:access-key "foo" :secret-key "bar" :session-token "baz"})
          (.refresh provider)
          (let [creds (get-credentials provider)]
            (is (credentials? creds)
                "returns credentials")
            (is (= "foo" (.getAWSAccessKeyId creds))
                "access-key is correct")
            (is (= "bar" (.getAWSSecretKey creds))
                "secret-key is correct")
            (is (= "baz" (.getSessionToken creds))
                "session-token is correct")))))))
