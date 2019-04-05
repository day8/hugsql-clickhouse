(ns hugsql.adapter.clickhouse-native-jdbc-test
  (:require [clojure.test :refer :all]
            [hugsql.core :as hugsql]
            [hugsql.adapter.clickhouse-native-jdbc :as clickhouse])
  (:import (java.sql Connection DriverManager ResultSet Statement)))

(hugsql/def-db-fns "./hugsql/adapter/fns.sql")
(hugsql/set-adapter! (clickhouse/hugsql-adapter-clickhouse-native-jdbc))

(def conn (DriverManager/getConnection "jdbc:clickhouse://127.0.0.1:9000"))

(defn database
  [tests]
  (create-test-database conn)
  (tests)
  (drop-test-database conn))

(use-fixtures :once database)

(deftest create-table-test
  (testing "Can create a table."
    (is (not (nil? (create-colors-table conn))))))

(deftest insert-row-test
  (testing "Can insert a row."
    (is (nil? (insert-color conn {:id 123 :name "ocher" :rgb [204 119 34]}))))
  (testing "Can insert another row."
    (is (nil? (insert-color conn {:id 456 :name "crimson" :rgb [220, 20, 60]}))))
  (testing "Can select a row."
    (is (= (:id (select-color-by-id conn {:id 123}))
           123)))
  (testing "Can select multiple rows."
    (is (= (count (select-all-colors conn))
           2))))

(deftest add-column-test
  (testing "Can add a column to an existing table."
    (is (not (nil? (add-column conn {:tbl :test.colors :col :hex :typ :String}))))))