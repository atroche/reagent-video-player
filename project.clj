(defproject faceviz "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-beta2"]
                 [org.clojure/clojurescript "1.9.946"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.1"]
                 [org.clojure/core.async "0.3.443"]
                 [re-com "2.1.0"]
                 [garden "1.3.2"]
                 [philoskim/debux "0.3.12"]
                 [kibu/pushy "0.3.8"]
                 [yada/bidi "1.2.6"]]

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-garden "0.2.8"]]

  :repositories {"snapshots" {:url           "s3p://thinktopic.jars/snapshots/"
                              :no-auth       true
                              :releases      false
                              :sign-releases false}
                 "releases"  {:url           "s3p://thinktopic.jars/releases/"
                              :no-auth       true
                              :snapshots     false
                              :sign-releases false}}

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"
                                    "test/js"
                                    "resources/public/css"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :garden {:builds [{:id           "screen"
                     :source-paths ["src/clj"]
                     :stylesheet   faceviz.css/screen
                     :compiler     {:output-to     "resources/public/css/screen.css"
                                    :pretty-print? true}}]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.4"]
                   [re-frisk "0.5.0"]]

    :plugins      [[lein-figwheel "0.5.13"]
                   [lein-doo "0.1.8"]]}}

  :doo {:build "test"}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "faceviz.core/mount-root"}
     :compiler     {:main                 faceviz.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload
                                           re-frisk.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}


    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            faceviz.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:main          faceviz.runner
                    :output-to     "resources/public/js/compiled/test.js"
                    :output-dir    "resources/public/js/compiled/test/out"
                    :optimizations :none}}]})

