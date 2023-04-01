(ns app.db)

(def default-db
  {:todos (sorted-map-by >)})
