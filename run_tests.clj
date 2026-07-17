(require '[clojure.test :as t])

(doseq [ns-sym '[mitate.methods.test-charter-gates
                  mitate.methods.test-manifest-invariants
                  mitate.repository-contract-test]]
  (require ns-sym))

(let [result (apply t/run-tests
                    '[mitate.methods.test-charter-gates
                      mitate.methods.test-manifest-invariants
                      mitate.repository-contract-test])]
  (System/exit (if (zero? (+ (:fail result) (:error result))) 0 1)))
