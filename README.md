# clj-aws-auth

A Clojure library for using Amazon Auth, based on the official AWS
Java SDK.

This library aims to provide a complete implementaion of the auth
API which complements the excellent [clj-aws-s3][0] library created by
James Reeves.

## Install

Add the following dependency to your `project.clj` file:

    [clj-aws-auth "0.1.0-SNAPSHOT"]

## Example

```clojure
(require '[aws.sdk.auth :as auth])
;; TODO: add an example
```

## Documentation

* [API docs (coming soon)][1]
* [AWS SDK Javadoc for Auth][2]

## License

Copyright © 2013 Kevin Neaton

Distributed under the Eclipse Public License, the same as Clojure.

[0]: https://github.com/weavejester/clj-aws-s3
[1]: http://neatonk.github.com/clj-aws-auth/
[2]: http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/auth/package-summary.html