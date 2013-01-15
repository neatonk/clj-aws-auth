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

This will attempt to retrieve credentials using the default provider chain,
which checks the following sources in order:

* `:environment`       - `AWS_ACCESS_KEY_ID` and `AWS_SECRET_KEY`
* `:system-properties` - `aws.accessKeyId` and `aws.secretKey`
* `:instance-profile`  - via the Amazon EC2 metadata service

If your credentials are available through one of these source then you're all
set. Otherwise you'll get an `com.amazonaws.AmazonClientException`.

This is equivalent to `(get-credentials :default-chain)` and the same behavior
can be achieved with.

```clojure
(get-credentials :environment :system-properties :instance-profile)
```

Ideally this is all you'll ever need.

### Other Sources

Credentials can be retrieved from a variety of sources. Implementations of
`AWSCredentialsProvider` or `AWSCredentials` from the `com.amazonaws.auth` can
be used directly or indicated as a keyword for convenience, see
[`credentials-provider`][1] for a full list of supported keywords.

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

* [API docs (coming soon)][2]
* [AWS SDK Javadoc for Auth][3]

## License

Copyright © 2013 Kevin Neaton

Distributed under the Eclipse Public License, the same as Clojure.

[0]: https://github.com/weavejester/clj-aws-s3
[1]: https://neatonk.github.com/clj-aws-auth/v0.1.0/aws.sdk.auth.html#var-credentials-provider
[2]: http://neatonk.github.com/clj-aws-auth/v0.1.0/aws.sdk.auth.html
[3]: http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/package-summary.html
