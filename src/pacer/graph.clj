(ns pacer.graph
    (:import
      (com.tinkerpop.blueprints.impls.tg TinkerGraph)
      (com.tinkerpop.gremlin.pipes.transform InEdgesPipe OutEdgesPipe))
    (:use [clojure.pprint :only [pprint]]
          [clojure.string :only [join]]
          pacer.step))

(defn tg []
  { :source true
    :type :graph
    :name "TinkerGraph"
    :show (fn [g] (str @(:raw-graph g)))
    :raw-graph (atom (com.tinkerpop.blueprints.impls.tg.TinkerGraph.))
    :encoder (atom (pacer/simple-encoder))})

(defn create-vertex [graph]
  { :graph graph
    :type :vertex
    :element (.addVertex @(:raw-graph graph) nil)
    })

(defn create-edge [graph label from to]
  { :graph graph
    :type :edge
    :element (.addEdge @(:raw-graph graph) nil (:element from) (:element to) (str label))
    })


(defn v []
  { :source-type :graph
    :type :vertex
    :name "V"
    :iterator (fn iterator [source]
                  (.. @(:raw-graph source) getVertices iterator)) })

(defn e []
  { :source-type :graph
    :type :edge
    :name "E"
    :iterator (fn iterator [source]
                  (.. @(:raw-graph source) getEdges iterator)) })

(defn- name+ [name labels]
       (if (empty? labels)
         name
         (str name " (" (join ", " labels) ")")))

(defn- strs [args]
       (into-array String (map str args)))

(defn out-e [& labels]
  { :source-type :vertex
    :type :edge
    :name (name+ "OutE" labels)
    :labels labels
    :pipe (fn pipe [in]
              (OutEdgesPipe. (strs labels))) })

(defn in-e [& labels]
  { :source-type :vertex
    :type :edge
    :name (name "InE" labels)
    :labels labels
    :pipe (fn pipe [in]
              (InEdgesPipe. (strs labels))) })

(defn both-e [& labels]
  { :source-type :vertex
    :type :edge
    :name (name+ "BothE" labels)
    :labels labels
    :pipe (fn pipe [in]
              (BothEdgesPipe. (strs labels))) })

