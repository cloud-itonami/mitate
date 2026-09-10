;; The test namespaces locate this repository from *file*, walking up from their
;; own path -- so they must be LOADED BY PATH. Under `require` the classpath
;; makes *file* relative, two parents up is "" and its parent is null, and the
;; namespace dies at load with "Cannot invoke Object.getClass() because target
;; is null". This runner used require, so the suite could not run as written.
(require '[clojure.test :as t])

(def ^:private test-files
  ["test/mitate/methods/test_charter_gates.cljc"
   "test/mitate/methods/test_manifest_invariants.cljc"
   "test/mitate/repository_contract_test.clj"])

(doseq [f test-files] (load-file f))

(let [result (apply t/run-tests
                    '[mitate.methods.test-charter-gates
                      mitate.methods.test-manifest-invariants
                      mitate.repository-contract-test])]
  (System/exit (if (zero? (+ (:fail result) (:error result))) 0 1)))
