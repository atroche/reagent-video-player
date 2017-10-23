(ns faceviz.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [faceviz.reagent-video-test]))

(doo-tests 'faceviz.reagent-video-test)
