# Spark-SqoopCDC

Change data capture realization using Spark and Sqoop

## Problem
HDFS table needs to capture changes from the source RDB table. For example source RDB table has 1 million records and half of them have been changed, so we need to synchronize changed records with our HDFS database.

## Solution
1) We're going to use Sqoop in incremental mode to import only those records that were modified
2) We will store modified records in extract.parquet file 
3) Using Spark SQL, create new table called modified-data.
4) Using Spark SQL, insert unchanged data merged with modified data using LEFT JOIN

## Requirements
1) Installed Spark and Sqoop
2) JDBC driver for Sqoop
3) Running hadoop cluster
4) Running spark-shell

## Instruction for Sqoop data import
Standart template for sqoop import:

`sqoop import --connect <your jdbc driver with DB> --username <username> --password <password> -table <table name> --target-dir <path to HDFS directory>`

Sqoop incremental import can capture both new and modified records. If we need to import new records, we need to add next parameter:

`--check-column <name of column> --incremental append --last-value <value of the last record, that wasn't changed>`

If we need to capture updates:

`--check-column <name of column> --incremental lastmodified --last-value <value of the last record, that wasn't changed>`
