SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE transaction_items;
TRUNCATE TABLE transaction_log;
TRUNCATE TABLE purchase_order_items;
TRUNCATE TABLE purchase_orders;
TRUNCATE TABLE operational_costs;
TRUNCATE TABLE product_batches;
TRUNCATE TABLE inventory_log;
TRUNCATE TABLE investments;

TRUNCATE TABLE compatibility;
TRUNCATE TABLE vehicles;
TRUNCATE TABLE product;
TRUNCATE TABLE supplier;


TRUNCATE TABLE manufacturers;
-- PRODUCT
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_PRODUCT.csv'
INTO TABLE product
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(product_name,product_description,part_number,category_id,supplier_id,storage_location,date_added,last_update,status,unit_cost,retail_price);

-- SUPPLIER
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_SUPPLIER.csv'
INTO TABLE supplier
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(supplier_id,supplier_name,supplier_address,supplier_contact,@date_added,@last_update)
SET
date_added = STR_TO_DATE(@date_added, '%m/%d/%Y'),
last_update = STR_TO_DATE(@last_update, '%m/%d/%Y');

-- COMPATIBILITY
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_COMPATIBILITY.csv'
INTO TABLE compatibility
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(product_id,bottom_year,top_year,vehicle_id);

-- VEHICLES (FIXED ORDER + SAFE IMPORT)
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_VEHICLES.csv'
IGNORE
INTO TABLE vehicles
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(vehicle_id, model_name, manufacturer_id);

-- MANUFACTURERS
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_MANUFACTURER.csv'
IGNORE
INTO TABLE manufacturers
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\r\n'
IGNORE 1 LINES
(manufacturer_id, @manufacturer_name)
SET manufacturer_name = TRIM(@manufacturer_name);

-- TRANSACTION LOG
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_TRANSACTION_LOG.csv'
INTO TABLE transaction_log
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@discard,@c1,@transaction_date,@c3,@c4,@c5,@c6,@c7,@c8,@c9)
SET
parent_transaction_id = @c1,
transaction_date = 
    CASE 
        WHEN @transaction_date = '' THEN NOW()
        ELSE @transaction_date
    END,
receipt_num = @c3,
total_amount = @c4,
payment_method = NULLIF(UPPER(TRIM(REPLACE(@c5, '\r', ''))), ''),
status = NULLIF(UPPER(TRIM(REPLACE(@c6, '\r', ''))), ''),
notes = NULLIF(UPPER(TRIM(REPLACE(@c7, '\r', ''))), '');

-- TRANSACTION ITEMS
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_TRANSACTION_ITEMS.csv'
INTO TABLE transaction_items
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@c1,@c2,@c3,@c4,@c5,@c6,@c7,@c8,@c9,@c10,@c11)
SET
transaction_id = @c1,
product_id = @c2,
batch_id = @c3,
quantity_sold = @c4,
unit_selling_price = @c5,
unit_cost_at_sale = @c6,
discount_applied = @c7,
total_sale_value = @c8,
total_cost = @c9;

-- INVENTORY LOG
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_INVENTORY_LOG.csv'
INTO TABLE inventory_log
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(log_id,product_id,change_type,quantity,unit_cost,log_date,reference_id,reference_type);

-- PURCHASE ORDERS
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_PURCHASE_ORDERS.csv'
INTO TABLE purchase_orders
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(po_id, supplier_id, order_date, total_cost);

-- PURCHASE ORDER ITEMS
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_PURCHASE_ORDER_ITEMS.csv'
REPLACE
INTO TABLE purchase_order_items
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(id, po_id, product_id, quantity, unit_cost);


-- PRODUCT BATCHES
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_DATA_PRODUCT_BATCHES.csv'
INTO TABLE product_batches
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@batch_id,@product_id,@supplier_id,@quantity_received,@quantity_remaining,@unit_cost,@date_received,@barcode,@po_id)
SET
batch_id = @batch_id,
product_id = @product_id,
supplier_id = @supplier_id,
quantity_received = @quantity_received,
quantity_remaining = @quantity_remaining,
unit_cost = @unit_cost,
date_received = STR_TO_DATE(@date_received, '%Y-%m-%d'),
barcode = @barcode,
po_id = @po_id;

-- OPERATIONAL COSTS
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_OPERATIONAL_COST.csv'
INTO TABLE operational_costs
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@cost_id,@cost_type,@amount,@cost_date)
SET
cost_id = @cost_id,
cost_type = @cost_type,
amount = @amount,
cost_date = STR_TO_DATE(@cost_date, '%m/%d/%Y');

-- INVESTMENTS
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/Mock Data/MOCK_INVESTMENTS.csv'
INTO TABLE investments
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@investment_id,@amount,@investment_date)
SET
investment_id = @investment_id,
amount = @amount,
investment_date = STR_TO_DATE(@investment_date, '%m/%d/%Y');

SET FOREIGN_KEY_CHECKS = 1;