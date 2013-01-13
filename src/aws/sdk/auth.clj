(ns aws.sdk.auth
  (:require [clojure.java.io :as io])
  (:import [com.amazonaws.auth
            AWSCredentials
            AWSSessionCredentials
            BasicAWSCredentials
            BasicSessionCredentials
            PropertiesCredentials
            AWSCredentialsProvider
            AWSCredentialsProviderChain
            ClasspathPropertiesFileCredentialsProvider
            DefaultAWSCredentialsProviderChain
            EnvironmentVariableCredentialsProvider
            InstanceProfileCredentialsProvider
            STSSessionCredentialsProvider
            SystemPropertiesCredentialsProvider]))


(defn credentials? [x] (isa? (type x) AWSCredentials))
(defn credentials-provider? [x] (isa? (type x) AWSCredentialsProvider))
(defn credentials-provider-chain? [x] (isa? (type x) AWSCredentialsProviderChain))

;; # Credentials
;;
;; Use `credentials` to create an instance of the BasicAWSCredentials
;; or BasicSessionCredentials classes from a clojure map.

(defn ^AWSCredentials
  credentials
  "Creates basic aws credentials using the provided credentials
  map. Prefer `get-credentials` when credentials need to be loaded at
  runtime.

  Supported keys:
    :access-key     requires :secret-key
    :secret-key     requires :access-key
    :session-token  requires :secret-key and :access-key"
  [{:keys [access-key secret-key session-token] :as creds}]
  (cond (and access-key secret-key session-token)
        (BasicSessionCredentials. access-key secret-key session-token)

        (and access-key secret-key)
        (BasicAWSCredentials. access-key secret-key)))


;; # Credentials Provider
;;
;; Use `credentials-provider` to create an AWSCredentialsProvider or
;; an AWSCredentialsProviderChain.

(def ^:private keyword->class
  {:classpath ClasspathPropertiesFileCredentialsProvider
   :default-chain DefaultAWSCredentialsProviderChain
   :environment EnvironmentVariableCredentialsProvider
   :instance-profile InstanceProfileCredentialsProvider
   :system-properties SystemPropertiesCredentialsProvider
   :sts-session STSSessionCredentialsProvider})

(defn- apply-constructor [class args]
  (clojure.lang.Reflector/invokeConstructor
   class (to-array args)))


(defprotocol AWSCredentialsProviderFactory
  (make-credentials-provider [x args] "Returns a credentials provider or nil."))

(extend-protocol AWSCredentialsProviderFactory
  AWSCredentialsProvider
  (make-credentials-provider [p _] p)

  AWSCredentials
  (make-credentials-provider [creds _]
    (reify AWSCredentialsProvider
      (getCredentials [_] creds)
      (refresh [_] nil)))

  clojure.lang.PersistentArrayMap
  (make-credentials-provider [m _]
    (when-let [creds (credentials m)]
      (make-credentials-provider creds _)))

  clojure.lang.Keyword
  (make-credentials-provider [k args]
    (apply-constructor (keyword->class k) args))

  clojure.lang.Fn
  (make-credentials-provider [f args]
    (let [creds (atom (apply f args))]
      (reify AWSCredentialsProvider
        (refresh [_] (reset! creds (apply f args)))
        (getCredentials [_] @creds)))))


(defn ^AWSCredentialsProvider
  credentials-provider
  "Creates a credentials provider for the source given. The source can
  be of any type the AWSCredentialsProviderFactory protocol supports,
  but should typically be a keyword indicating the provider type to
  use. Since this is based on a protocol, you are free to extend it to
  any type you like.

  Supported provider keywords and classes:
    :classpath => ClasspathPropertiesFileCredentialsProvider
    :default-chain => DefaultAWSCredentialsProviderChain
    :environment => EnvironmentVariableCredentialsProvider
    :instance-profile => InstanceProfileCredentialsProvider
    :system-properties => SystemPropertiesCredentialsProvider
    :sts-session => STSSessionCredentialsProvider"
  [source & args]
  (let [args (when-not (empty? args) args)]
    (make-credentials-provider source args)))


(defn ^AWSCredentialsProviderChain
  credentials-provider-chain
  "Creates a credentials provider chain from the sources given. See
  #'credentials-provider for supported sources."
  [sources]
  (let [providers (map credentials-provider sources)]
    (AWSCredentialsProviderChain.
     (into-array AWSCredentialsProvider providers))))


(defn ^AWSCredentials
  get-credentials
  "Loads credentials using the default aws credentials provider chain
  or from the source(s) given in the order given order. Throws an
  exception if no credentials are found."
  [& sources]
  (.getCredentials
   (if (empty? sources)
     (credentials-provider :default-chain)
     (credentials-provider-chain sources))))
