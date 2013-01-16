# clj-aws-auth

A Clojure library for Amazon's Auth API, based on the official AWS Java
SDK. Easily create and retrieve AWSCredentials from a multitude of sources.

## Install

Add the following dependency to your `project.clj` file:

```clojure
[clj-aws-auth "0.1.0"]
```

## Example

```clojure
(use 'aws.sdk.auth)
(def my-creds (get-credentials))
```

This will attempt to retrieve credentials using the [default provider chain][0],
which checks the following __sources__ in order:

* `:environment`       - `AWS_ACCESS_KEY_ID` and `AWS_SECRET_KEY`
* `:system-properties` - `aws.accessKeyId` and `aws.secretKey`
* `:instance-profile`  - via the Amazon EC2 metadata service

If your credentials are available through one of these sources then you're all
set. Otherwise you'll get an `com.amazonaws.AmazonClientException`.

This is equivalent to `(get-credentials :default-chain)` and the same behavior
can be achieved with:

```clojure
(get-credentials :environment :system-properties :instance-profile)
```

### Sources of Credentials

Credentials can be retrieved from a variety of sources and any of these sources
can be used directly with `get-credentials`, `credentials-provider`, and
`credentials-provider-chain`.

#### Provided by `com.amazonaws.auth`

Implementations of `AWSCredentialsProvider` or `AWSCredentials` from the
`com.amazonaws.auth` are all valid sources of credentials and can be used
directly or indirectly in keyword form for convenience. The example above uses
keywords to indcate a chain of three credentials sources provided by the
`com.amazonaws.auth` package. See [`credentials-provider`][1] for a list of
supported keywords.

#### Clojure Maps and Fn's

Clojure maps and functions can also be used as credentials sources, opening up
additional possibilities. Here's a simple example:

```clojure
(use 'aws.sdk.auth)
(defn creds-fn [] (credentials {:access-key "foo" :secret-key "bar"}))
(get-credentials creds-fn)
```

Here's a more realistic example, which will read your credentials from a gpg
encrypted file, assuming you already have gpg installed and setup.

```clojure
(use 'clojure.java.shell)

(defn gpg-creds [file]
  (let [{:keys [out exit]} (sh "gpg" "--quiet" "--batch" "--decrypt" (str file))]
    (when (pos? exit) (credentials (read-string out)))))

(get-credentials (partial gpg-creds "aws-credentials.clj.gpg"))
```

## Documentation

* [API docs][2]
* [AWS SDK Javadoc for Auth][3]

## License

Copyright © 2013 Kevin Neaton

Distributed under the Eclipse Public License, the same as Clojure.

[0]: http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/DefaultAWSCredentialsProviderChain.html
[1]: http://neatonk.github.com/clj-aws-auth/aws.sdk.auth.html#var-credentials-provider
[2]: http://neatonk.github.com/clj-aws-auth/aws.sdk.auth.html
[3]: http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/package-summary.html
