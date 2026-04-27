SET FOREIGN_KEY_CHECKS = 0;

LOAD DATA INFILE 'Mock Data/MOCK_DATA_PRODUCT.csv'
INTO TABLE product
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(product_name,product_description,part_number,category_id,supplier_id,storage_location,date_added,last_update,status,unit_cost,retail_price);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_SUPPLIER.csv'
INTO TABLE supplier
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(supplier_name,supplier_address,supplier_contact,date_added,last_update);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_COMPATIBILITY.csv'
INTO TABLE supplier
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(product_id,bottom_year,top_year,vehicle_id);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_VEHICLES.csv'
INTO TABLE vehicles
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(model_name,manufacturer_id);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_MANUFACTURER.csv'
INTO TABLE manufacturers
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(manufacturer_name);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_TRANSACTION_LOG.csv'
INTO TABLE transaction_log
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(parent_transaction_id,transaction_date,receipt_num,total_amount,payment_method,status,notes);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_TRANSACTION_ITEMS.csv'
INTO TABLE transaction_items
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(transaction_id,product_id,batch_id,quantity_sold,unit_selling_price,unit_cost_at_sale,discount_applied,total_sale_value,total_cost);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_INVENTORY_LOG.csv'
INTO TABLE inventory_log
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(product_id,change_type,quantity,unit_cost,log_date,reference_id,reference_type);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_PURCHASE_ORDERS.csv'
INTO TABLE purchase_orders
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(supplier_id,order_date,total_cost);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_PURCHASE_ORDER_ITEMS.csv'
INTO TABLE purchase_order_items
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(po_id,product_id,quantity,unit_cost);

LOAD DATA INFILE 'Mock Data/MOCK_DATA_PRODUCT_BATCHES.csv'
INTO TABLE product_batches
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(po_id,product_id,supplier_id,quantity_received,quantity_remaining,unit_cost,date_received,barcode);

LOAD DATA INFILE 'Mock Data/MOCK_OPERATIONAL_COST.csv'
INTO TABLE operational_costs
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(cost_type,amount,cost_date,notes);

LOAD DATA INFILE 'Mock Data/MOCK_INVESTMENTS.csv'
INTO TABLE investments
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(amount,investment_date,description);

SET FOREIGN_KEY_CHECKS = 1;
