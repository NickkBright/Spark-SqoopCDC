# Spark-SqoopCDC

Change data capture realization using Spark and Sqoop

## Problem
HDFS table needs to capture changes from the source RDB table. For example source RDB table has 1 million records and half of them have been changed, so we need to synchronize changed records with our HDFS database.

## Solution
1) We're going to use Sqoop in incremental mode to import only those records that were modified
2) We will store modified records in parquet file 
3) Using Spark SQL, create new tables: one for extracted data and another for updated original table.
4) Using Spark SQL, insert unchanged data merged with modified data using LEFT JOIN

## Requirements
1) Installed Spark and Sqoop
2) JDBC driver for Sqoop
3) Running hadoop cluster
4) Running spark-shell

## Instruction for Sqoop data import
Standart template for sqoop import as parquet file:

`sqoop import --connect <your jdbc driver with DB> --username <username> --password <password> --table <table name> --target-dir <path to HDFS directory> --as-parquetfile`

Sqoop incremental import can capture both new and modified records. If we need to import new records, we need to add next parameter:

`--check-column <name of column> --incremental append --last-value <value of the last record, that wasn't changed>`

If we need to capture updates:

`--check-column <name of column> --incremental lastmodified --last-value <value of the last record, that wasn't changed>`

## Using Sqoop job to simplify CDC
We can save our incremental import command for multiple usage without specifying --last-value attribute. Example: 

`sqoop job --import --connect` etc.

Sqoop will identify last-value attribute on first run and will change it if there will be larger value, so we don't need to specify it manually.

## Using Spark for synchronization
Let our original table be called Customers. After sqoop operation there will be parquet file with changed data. To make updated variant of original table, follow next steps.
1) Create temporary table called customers_extract

  `val parquetFile = sqlContext.read.parquet("path/to/your/parquet")`
  
  `parquetFile.registerTempTable("customers_extract")`

2) Create table called customers_updated, where we will merge updates and original table

  `sql = "DROP TABLE if exists customers_updated"`
  
  `sqlContext.sql(sql)`
  
  `sql = "CREATE TABLE customers_updated ( cust_no int ,birth_date date ,first_name string ,last_name string ,gender string ,join_date date ,created_date timestamp ,modified_date timestamp ) STORED AS PARQUET"`
  
  `sqlContext.sql(sql)`
  
 3) Insert data into customers_updated
 
 `sql = "INSERT INTO TABLE customer_update_spark SELECT a.cust_no, a.birth_date, a.first_name, a.last_name, a.gender, a.join_date, a.created_date, a.modified_date FROM customer a LEFT OUTER JOIN customer_extract b ON a.cust_no = b.cust_no WHERE b.cust_no IS NULL"`
 
 `sqlContext.sql(sql)`
