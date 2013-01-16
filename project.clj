(defproject clj-aws-auth "0.1.1-SNAPSHOT"
  :description "Clojure AWS Authentication library."
  :url "http://github.com/neatonk/clj-aws-auth"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.amazonaws/aws-java-sdk "1.3.18"]]
  :plugins [[codox "0.6.4"]]
  :codox {:output-dir "gh-pages"
          :src-dir-uri "http://github.com/neatonk/clj-aws-auth/blob/master"
          :src-linenum-anchor-prefix "L"})
