CREATE TABLE `product`(
    `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_name` VARCHAR(255) NOT NULL,
    `product_description` VARCHAR(255) NULL,
    `category_id` SMALLINT NOT NULL,
    `supplier_id` SMALLINT NOT NULL,
    `storage_location` VARCHAR(255) NULL,
    `date_added` DATETIME NOT NULL,
    `last_update` DATETIME NOT NULL,
    `unit_cost` DECIMAL(12, 2) NOT NULL COMMENT 'Latest supplier price; regularly update this.'
);

CREATE TABLE `product_category`(
    `category_id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `category_name` VARCHAR(255) NOT NULL,
    `parent_category_id` SMALLINT NOT NULL,
    `date_added` DATETIME NOT NULL,
    `last_update` DATETIME NOT NULL
);

CREATE TABLE `supplier`(
    `supplier_id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `supplier_name` VARCHAR(255) NOT NULL,
    `supplier_address` VARCHAR(255) NOT NULL,
    `supplier_contact` BIGINT NOT NULL,
    `date_added` DATETIME NOT NULL,
    `last_update` DATETIME NOT NULL
);

CREATE TABLE `transaction_items`(
    `item_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `transaction_id` BIGINT NOT NULL,
    `product_id` INT NOT NULL,
    `batch_id` BIGINT NOT NULL,
    `quantity_sold` SMALLINT NOT NULL,
    `unit_selling_price` DECIMAL(12, 2) NOT NULL COMMENT 'Retail price per unit at the moment of sale',
    `unit_cost_at_sale` DECIMAL(12, 2) NOT NULL COMMENT 'Cost per unit from supplier at the moment of sale',
    `discount_applied` DECIMAL(12, 2) NOT NULL,
    `total_sale_value` DECIMAL(12, 2) NOT NULL COMMENT 'Total revenue: (quantity_sold * unit_selling_price) - (quantity_sold * discount_applied)',
    `total_cost` DECIMAL(12, 2) NOT NULL COMMENT 'Total cost of goods sold (COGS): quantity_sold * unit_cost_at_sale'
);

CREATE TABLE `transaction_log`(
    `transaction_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `transaction_date` DATETIME NOT NULL,
    `total_amount` DECIMAL(12, 2) NOT NULL,
    `payment_method` ENUM('CASH', 'E-WALLET') NULL,
    `notes` VARCHAR(255) NULL
);
ALTER TABLE
    `transaction_log` ADD INDEX `transaction_log_transaction_date_index`(`transaction_date`);

CREATE TABLE `operational_costs`(
    `cost_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `cost_type` ENUM(
        'RENT',
        'UTILITIES',
        'WAGES',
        'MAINTENANCE',
        'MARKETING',
        'OTHER'
    ) NOT NULL,
    `amount` DECIMAL(12, 2) NOT NULL,
    `cost_date` DATETIME NOT NULL,
    `notes` VARCHAR(255) NULL
);
ALTER TABLE
    `operational_costs` ADD INDEX `operational_costs_cost_date_index`(`cost_date`);
    `operational_costs` COMMENT = 'For calculating total_operating_costs & total_costs (COGS + operating_costs)';

CREATE TABLE `inventory_log`(
    `log_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` INT NOT NULL,
    `change_type` ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
    `quantity` SMALLINT NOT NULL,
    `unit_cost` DECIMAL(8, 2) NOT NULL,
    `log_date` DATETIME NOT NULL,
    `reference_id` BIGINT NULL COMMENT 'Links to transaction/purchase',
    `reference_type` ENUM('TRANSACTION', 'PURCHASE') NULL
);
ALTER TABLE
    `inventory_log` ADD INDEX `inventory_log_log_date_index`(`log_date`);

CREATE TABLE `investments`(
    `investment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `amount` DECIMAL(12, 2) NOT NULL,
    `investment_date` DATETIME NOT NULL,
    `description` VARCHAR(255) NULL
);

CREATE TABLE `purchase_orders`(
    `po_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `supplier_id` SMALLINT NOT NULL,
    `order_date` DATETIME NOT NULL,
    `total_cost` DECIMAL(12, 2) NOT NULL
);
CREATE TABLE `purchase_order_items`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `po_id` BIGINT NOT NULL,
    `product_id` INT NOT NULL,
    `quantity` SMALLINT NOT NULL,
    `unit_cost` DECIMAL(12, 2) NOT NULL
);

CREATE TABLE `pending_transaction_items`(
    `item_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `transaction_id` BIGINT NOT NULL,
    `product_id` INT NOT NULL,
    `batch_id` BIGINT NOT NULL,
    `quantity_sold` SMALLINT NOT NULL,
    `unit_selling_price` DECIMAL(12, 2) NOT NULL COMMENT 'Retail price per unit at the moment of sale',
    `unit_cost_at_sale` DECIMAL(12, 2) NOT NULL COMMENT 'Cost per unit from supplier at the moment of sale',
    `discount_applied` DECIMAL(12, 2) NOT NULL,
    `total_sale_value` DECIMAL(12, 2) NOT NULL COMMENT 'Total revenue: (quantity_sold * unit_selling_price) - (quantity_sold * discount_applied)',
    `total_cost` DECIMAL(12, 2) NOT NULL COMMENT 'Total cost of goods sold (COGS): quantity_sold * unit_cost_at_sale'
);

CREATE TABLE `pending_transactions_log`(
    `pending_transaction_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `creation_date` DATETIME NOT NULL,
    `status` ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    `total_amount` DECIMAL(12, 2) NOT NULL,
    `payment_method` ENUM('CASH', 'E-WALLET') NULL,
    `notes` VARCHAR(255) NULL
);
ALTER TABLE
    `pending_transactions_log` ADD INDEX `pending_transactions_log_creation_date_index`(`creation_date`);

CREATE TABLE `product_batches`(
    `batch_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` INT NOT NULL,
    `supplier_id` BIGINT NOT NULL,
    `quantity_received` SMALLINT NOT NULL,
    `unit_cost` DECIMAL(12, 2) NOT NULL,
    `date_received` DATETIME NOT NULL,
    `barcode` VARCHAR(100) NOT NULL
);

ALTER TABLE
    `product_batches` ADD UNIQUE `product_batches_barcode_unique`(`barcode`);
ALTER TABLE
    `purchase_orders` ADD CONSTRAINT `purchase_orders_supplier_id_foreign` FOREIGN KEY(`supplier_id`) REFERENCES `supplier`(`supplier_id`);
ALTER TABLE
    `product_category` ADD CONSTRAINT `product_category_parent_category_id_foreign` FOREIGN KEY(`parent_category_id`) REFERENCES `product_category`(`category_id`);
ALTER TABLE
    `pending_transaction_items` ADD CONSTRAINT `pending_transaction_items_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `pending_transaction_items` ADD CONSTRAINT `pending_transaction_items_batch_id_foreign` FOREIGN KEY(`batch_id`) REFERENCES `product_batches`(`batch_id`);
ALTER TABLE
    `pending_transaction_items` ADD CONSTRAINT `pending_transaction_items_transaction_id_foreign` FOREIGN KEY(`transaction_id`) REFERENCES `pending_transactions_log`(`pending_transaction_id`);
ALTER TABLE
    `purchase_order_items` ADD CONSTRAINT `purchase_order_items_po_id_foreign` FOREIGN KEY(`po_id`) REFERENCES `purchase_orders`(`po_id`);
ALTER TABLE
    `product` ADD CONSTRAINT `product_supplier_id_foreign` FOREIGN KEY(`supplier_id`) REFERENCES `supplier`(`supplier_id`);
ALTER TABLE
    `purchase_order_items` ADD CONSTRAINT `purchase_order_items_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `product` ADD CONSTRAINT `product_category_id_foreign` FOREIGN KEY(`category_id`) REFERENCES `product_category`(`category_id`);
ALTER TABLE
    `inventory_log` ADD CONSTRAINT `inventory_log_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `transaction_items` ADD CONSTRAINT `transaction_items_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `transaction_items` ADD CONSTRAINT `transaction_items_quantity_sold_foreign` FOREIGN KEY(`quantity_sold`) REFERENCES `product_batches`(`batch_id`);
ALTER TABLE
    `transaction_items` ADD CONSTRAINT `transaction_items_transaction_id_foreign` FOREIGN KEY(`transaction_id`) REFERENCES `transaction_log`(`transaction_id`);
ALTER TABLE
    `product_batches` ADD CONSTRAINT `product_batches_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);




-- Procedure format for reference
-- REMOVE BEFORE PRODUCTION!!!!

DELIMITER //

CREATE PROCEDURE procedure_name
    @param1 datatype,
    @param2 datatype
BEGIN
    -- SQL_statements to be executed
    SELECT column1, column2
    FROM table_name
    WHERE columnN = @paramN;
END //

DELIMITER;




-- Procedures for Inventory Module: 
--      Add new item to inventory


--      Remove item from inventory


--      Edit info of item 


--      Query for item(s) based on search params


-- Procedures for Point of Sale Module:
--      Add new items to temporary transaction log
DELIMITER //

CREATE PROCEDURE add_to_pending_transaction_log
    @creation_date DATETIME,
    @total_amount DECIMAL(12, 2)
BEGIN
    INSERT INTO pending_transactions_log
END //

DELIMITER;

--      Remove items from temporary transaction log


--      Edit status of items in temporary transaction log


--      Move confirmed items from temporary transaction log to transaction log



-- Procedures for Business Analytics & Predictive AI
--      Calculate all the computable business metrics that don't need a trained AI here