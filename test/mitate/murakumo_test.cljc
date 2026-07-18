(ns mitate.murakumo-test
  (:require [clojure.test :refer [deftest is testing]]
            [mitate.murakumo :as mitate]))

(def full-attestations
  (into {}
        (map (fn [gate] [gate (str "attested-" (name gate))]))
        (distinct (mapcat :required-gates (vals mitate/cell-specs)))))

(deftest maps-all-legacy-mitate-cells
  (is (= #{"mitate_allergy_ige_panel_order"
           "mitate_emergency_screen"
           "mitate_ess_surgery_planner"
           "mitate_medication_history_audit"
           "mitate_nasal_endoscopy_acquire"
           "mitate_nasal_smear_eosinophil"
           "mitate_outcome_qol_followup"
           "mitate_paranasal_ct_route"
           "mitate_rhinitis_intake"
           "mitate_rhinitis_triage"
           "mitate_rhinomanometry"
           "mitate_slit_cohort_tracker"
           "mitate_treatment_router"}
         (set (map :legacy-cell (vals mitate/cell-specs))))))

(deftest r0-gates-block-effects
  (let [plan (mitate/cell-plan :rhinitis-intake
                               {:source-id "intake-001"
                                :computed-at "2026-06-29T00:00:00Z"})]
    (is (= :blocked (:status plan)))
    (is (= [:council-charter-attestation
            :silen-mitate-baseline-review
            :patient-consent-receipt-protocol
            :encrypted-envelope-recipient-registry
            :g11-intake-form-text-review]
           (:missing-gates plan)))
    (is (empty? (:effects plan)))))

(deftest attested-intake-emits-mst-effect
  (let [plan (mitate/cell-plan :rhinitis-intake
                               {:attestations full-attestations
                                :patient-pseudonym-did "did:web:etzhayyim.com:mitate:patient-pseudonym:p30"
                                :computed-at "2026-06-29T00:00:00Z"
                                :record {:tid "intake-001"
                                         :consentReceiptCid "bafy-consent"}})
        effect (first (:effects plan))]
    (is (= :ready (:status plan)))
    (is (= :mst/put-record (:op effect)))
    (is (= mitate/actor-did (:actor effect)))
    (is (= "com.etzhayyim.mitate.rhinitisIntake" (:collection effect)))
    (is (= "intake-001" (:rkey effect)))
    (is (= true (get-in effect [:record :advisoryOnly])))))

(deftest emergency-and-md-gates-remain-cell-specific
  (testing "emergency screen keeps G5 adversarial baseline"
    (let [attestations (dissoc full-attestations :g5-false-negative-adversarial-testing-baseline)
          plan (mitate/cell-plan :emergency-screen {:attestations attestations})]
      (is (= [:g5-false-negative-adversarial-testing-baseline] (:missing-gates plan)))
      (is (empty? (:effects plan)))))
  (testing "treatment router keeps licensed MD registry"
    (let [attestations (dissoc full-attestations :licensed-md-registry)
          plan (mitate/cell-plan :treatment-router {:attestations attestations})]
      (is (= [:licensed-md-registry] (:missing-gates plan)))
      (is (empty? (:effects plan))))))

(deftest all-cell-plans-ready-when-attested
  (let [plans (mitate/all-cell-plans {:attestations full-attestations
                                      :patient-pseudonym-did "patient-p30"
                                      :computed-at "2026-06-29T00:00:00Z"})]
    (is (= (set (keys mitate/cell-specs)) (set (keys plans))))
    (is (every? #(= :ready (:status %)) (vals plans)))
    (is (= (count mitate/cell-specs)
           (count (mapcat :effects (vals plans)))))))
