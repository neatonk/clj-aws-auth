(defproject clj-aws-auth "0.1.0"
  :description "Clojure AWS Authentication library."
  :url "http://github.com/neatonk/cls-aws-auth"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.amazonaws/aws-java-sdk "1.3.18"]]
  :plugins [[codox "0.6.4"]]
  :codox {:output-dir "gh-pages/v0.1.0"
          :src-dir-uri "http://github.com/neatonk/clj-aws-auth/blob/v0.1.0"
          :src-linenum-anchor-prefix "L"})
