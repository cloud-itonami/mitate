(ns mitate.murakumo
  "Pure cljc actor boundary generated from manifest migration scaffold."
  (:require [clojure.string :as str]))

(def actor-did
  "did:web:etzhayyim.com:mitate")

(def common-gates
  [:council-charter-attestation
   :no-platform-held-key-baseline
   :no-probing-baseline
   :murakumo-only-inference-baseline
   :did-primary-baseline
   :append-only-gate-baseline
   :kotoba-only-substrate-baseline])

(defn collection
  [name]
  (str "com.etzhayyim.mitate." name))

(def cell-specs {
  :mitaterhinitisintakecell {:legacy-cell "MitateRhinitisIntakeCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitaterhinitisintakecell")]
     :required-gates common-gates
     :trigger "manifest cell mitaterhinitisintakecell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitaterhinitistriagecell {:legacy-cell "MitateRhinitisTriageCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitaterhinitistriagecell")]
     :required-gates common-gates
     :trigger "manifest cell mitaterhinitistriagecell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitatemedicationhistoryauditcell {:legacy-cell "MitateMedicationHistoryAuditCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitatemedicationhistoryauditcell")]
     :required-gates common-gates
     :trigger "manifest cell mitatemedicationhistoryauditcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitateemergencyscreencell {:legacy-cell "MitateEmergencyScreenCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitateemergencyscreencell")]
     :required-gates common-gates
     :trigger "manifest cell mitateemergencyscreencell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitateallergyigepanelordercell {:legacy-cell "MitateAllergyIgePanelOrderCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitateallergyigepanelordercell")]
     :required-gates common-gates
     :trigger "manifest cell mitateallergyigepanelordercell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitatenasalsmeareosinophilcell {:legacy-cell "MitateNasalSmearEosinophilCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitatenasalsmeareosinophilcell")]
     :required-gates common-gates
     :trigger "manifest cell mitatenasalsmeareosinophilcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitatenasalendoscopyacquirecell {:legacy-cell "MitateNasalEndoscopyAcquireCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitatenasalendoscopyacquirecell")]
     :required-gates common-gates
     :trigger "manifest cell mitatenasalendoscopyacquirecell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitaterhinomanometrycell {:legacy-cell "MitateRhinomanometryCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitaterhinomanometrycell")]
     :required-gates common-gates
     :trigger "manifest cell mitaterhinomanometrycell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitateparanasalctroutecell {:legacy-cell "MitateParanasalCtRouteCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitateparanasalctroutecell")]
     :required-gates common-gates
     :trigger "manifest cell mitateparanasalctroutecell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitatetreatmentroutercell {:legacy-cell "MitateTreatmentRouterCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitatetreatmentroutercell")]
     :required-gates common-gates
     :trigger "manifest cell mitatetreatmentroutercell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitateslitcohorttrackercell {:legacy-cell "MitateSlitCohortTrackerCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitateslitcohorttrackercell")]
     :required-gates common-gates
     :trigger "manifest cell mitateslitcohorttrackercell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitateesssurgeryplannercell {:legacy-cell "MitateEssSurgeryPlannerCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitateesssurgeryplannercell")]
     :required-gates common-gates
     :trigger "manifest cell mitateesssurgeryplannercell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :mitateoutcomeqolfollowupcell {:legacy-cell "MitateOutcomeQolFollowupCell"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "mitateoutcomeqolfollowupcell")]
     :required-gates common-gates
     :trigger "manifest cell mitateoutcomeqolfollowupcell"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
})

(defn safe-rkey
  [s]
  (let [clean (-> (str s)
                  (str/replace #"^did:web:" "")
                  (str/replace #"[^A-Za-z0-9._~-]" "-"))]
    (if (str/blank? clean) "unknown" clean)))

(defn gate-value
  [attestations gate]
  (or (get attestations gate)
      (get attestations (name gate))
      (when (set? attestations) (attestations gate))
      (when (set? attestations) (attestations (name gate)))))

(defn missing-gates
  [spec attestations]
  (->> (:required-gates spec)
       (remove #(boolean (gate-value attestations %)))
       vec))

(defn put-record-effect
  [collection rkey record]
  {:op :mst/put-record
   :actor actor-did
   :collection collection
   :rkey rkey
   :record record})

(defn records-for
  [spec {:keys [records record computed-at request-id]
         :as input}]
  (let [input-records (cond
                        (map? records) records
                        (some? record) {0 record}
                        :else {})
        base {:actorDid actor-did
              :computedAt computed-at
              :legacyCell (:legacy-cell spec)
              :phase (:phase spec)
              :requestId request-id
              :actorBoundary "cljc-migration-scaffold"
              :scaffold true
              :constitutionalStatus "attested-plan"}]
    (map-indexed
     (fn [idx coll]
       (let [record* (merge {:$type coll}
                            base
                            (or (get input-records coll)
                                (get input-records idx)
                                {}))
             rkey (safe-rkey (or (:rkey record*)
                                 (get record* "rkey")
                                 (:tid record*)
                                 request-id
                                 (str (:legacy-cell spec) "-" idx)))]
         {:collection coll
          :record record*
          :rkey rkey}))
     (:collections spec))))

(defn cell-plan
  [cell-key {:keys [attestations] :as input}]
  (let [spec (get cell-specs cell-key)]
    (when-not spec
      (throw (ex-info "unknown cell" {:cell cell-key})))
    (let [missing (missing-gates spec attestations)]
      (merge
       {:cell cell-key
        :legacy-cell (:legacy-cell spec)
        :actor actor-did
        :phase (:phase spec)
        :murakumo-node (:murakumo-node spec)
        :trigger (:trigger spec)
        :ceiling (:ceiling spec)
        :required-gates (:required-gates spec)
        :missing-gates missing}
       (if (seq missing)
         {:status :blocked
          :effects []}
         (let [planned-records (records-for spec input)]
           {:status :ready
            :records (vec planned-records)
            :effects (mapv (fn [{:keys [collection record rkey]}]
                             (put-record-effect collection rkey record))
                           planned-records)}))))))

(defn all-cell-plans
  [input]
  (into {}
        (map (fn [cell-key] [cell-key (cell-plan cell-key input)]))
        (keys cell-specs)))
