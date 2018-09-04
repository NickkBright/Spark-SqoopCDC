//This is not complete module, but list of commands to be executed one by one
import org.apache.spark.sql.SparkSession
import spark.implicits._

val spark = SparkSession.builder().appName("Spark CDC usage").getOrCreate()
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
val df = sqlContext.read.parquet("path/to/your/parquet")
//drop table if it's already exists
df.sql("DROP TABLE if exists modified-date");
//create table for modified data
df.sql("CREATE TABLE modified-date ( cust_no int ,birth_date date ,first_name string ,last_name string ,gender string ,join_date date ,created_date timestamp ,modified_date timestamp ) STORED AS PARQUET")
//insert modified data
df.sql(" INSERT INTO TABLE customer_update_spark SELECT a.cust_no, a.birth_date, a.first_name, a.last_name, a.gender, a.join_date, a.created_date, a.modified_date FROM customer a LEFT OUTER JOIN customer_extract b ON a.cust_no = b.cust_no WHERE b.cust_no IS NULL
