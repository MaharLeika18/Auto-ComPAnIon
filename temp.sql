CREATE TABLE `users` (
    `user_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` ENUM('ADMIN', 'OWNER', 'SECRETARY', 'EMPLOYEE') NOT NULL,
    `created_at` DATETIME NOT NULL,
    `last_update` DATETIME NULL
);
CREATE TABLE `product`(
    `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_name` VARCHAR(255) NOT NULL,
    `product_description` VARCHAR(255) NULL,
    `part_number` VARCHAR(200) NULL,
    `category_id` SMALLINT NOT NULL,
    `supplier_id` SMALLINT UNSIGNED NOT NULL,
    -- `current_stock_level` SMALLINT NOT NULL,
    `storage_location` VARCHAR(255) NULL,
    `date_added` DATETIME NOT NULL,
    `last_update` DATETIME NOT NULL,
    `status` ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    `unit_cost` DECIMAL(12, 2) NOT NULL COMMENT 'Latest supplier price; regularly update this.',
    `retail_price` DECIMAL(12, 2) NOT NULL COMMENT 'Latest selling price; regularly update this.'
);
ALTER TABLE
    `product` ADD INDEX `idx_product_name`(`product_name`);    
CREATE TABLE `product_category`(
    `category_id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `category_name` VARCHAR(255) NOT NULL,
    `parent_category_id` SMALLINT NULL,
    `date_added` DATETIME NOT NULL
);
ALTER TABLE 
    `product_category` ADD UNIQUE (category_name);
ALTER TABLE
    `product_category` ADD INDEX `idx_product_category`(`category_id`);    
CREATE TABLE `product_images` (
    `image_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` INT NOT NULL,
    `image_path` VARCHAR(255) NULL,
    `date_uploaded` DATETIME NOT NULL
);
ALTER TABLE 
    `product_images` ADD UNIQUE (product_id);
CREATE TABLE `supplier`(
    `supplier_id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `supplier_name` VARCHAR(255) NOT NULL,
    `supplier_address` VARCHAR(255) NULL,
    `supplier_contact` VARCHAR(20) NULL,
    `date_added` DATETIME NOT NULL,
    `last_update` DATETIME NOT NULL
);
ALTER TABLE 
    `supplier` ADD UNIQUE (supplier_name);
CREATE TABLE `compatibility`(
    `product_id` BIGINT UNSIGNED NOT NULL,
    `bottom_year` SMALLINT NOT NULL,
    `top_year` SMALLINT NOT NULL,
    `vehicle_id` SMALLINT NOT NULL
);
ALTER TABLE
    `compatibility` ADD INDEX `compatibility_product_id_index`(`product_id`);
ALTER TABLE
    `compatibility` ADD INDEX `compatibility_vehicle_id_index`(`vehicle_id`);
ALTER TABLE `compatibility`
    ADD UNIQUE (product_id, vehicle_id, bottom_year, top_year); 
CREATE TABLE `vehicles`(
    `vehicle_id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `model_name` VARCHAR(255) NOT NULL,
    `manufacturer_id` SMALLINT NOT NULL,

    FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(manufacturer_id)
);
ALTER TABLE
    `vehicles` ADD INDEX `vehicles_manufacturer_index`(`manufacturer_id`);
ALTER TABLE  
    `vehicles` ADD UNIQUE (`model_name`, `manufacturer_id`);
CREATE TABLE `manufacturers` (
    `manufacturer_id` SMALLINT PRIMARY KEY AUTO_INCREMENT,
    `manufacturer_name` VARCHAR(255) UNIQUE
);
ALTER TABLE
    `manufacturers` ADD INDEX `manufacturers_name_index`(`manufacturer_name`);
CREATE TABLE `transaction_log`(
    `transaction_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `parent_transaction_id` BIGINT UNSIGNED DEFAULT NULL,
    `transaction_date` DATETIME NOT NULL,
    `receipt_num` INT NOT NULL,
    `total_amount` DECIMAL(12, 2) NOT NULL,
    `payment_method` ENUM('CASH', 'E-WALLET', 'BANK') NULL,
    `status` ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    `notes` VARCHAR(255) NULL
);
ALTER TABLE
    `transaction_log` ADD INDEX `transaction_log_transaction_date_index`(`transaction_date`);
ALTER TABLE
    `transaction_log` ADD INDEX `transaction_log_transaction_status_date`(`status`);
ALTER TABLE 
    `transaction_log` ADD CONSTRAINT `fk_parent_transaction` FOREIGN KEY (`parent_transaction_id`) REFERENCES `transaction_log`(`transaction_id`);
CREATE TABLE `transaction_items`(
    `item_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `transaction_id` BIGINT NOT NULL,
    `product_id` INT NOT NULL,
    `batch_id` BIGINT UNSIGNED NOT NULL,
    `quantity_sold` SMALLINT NOT NULL,
    `unit_selling_price` DECIMAL(12, 2) NOT NULL COMMENT 'Retail price per unit at the moment of sale',
    `unit_cost_at_sale` DECIMAL(12, 2) NOT NULL COMMENT 'Cost per unit from supplier at the moment of sale',
    `discount_applied` DECIMAL(3, 2) NOT NULL,
    `total_sale_value` DECIMAL(12, 2) NOT NULL COMMENT 'Total revenue: (quantity_sold * unit_selling_price) - (quantity_sold * discount_applied)',
    `total_cost` DECIMAL(12, 2) NOT NULL COMMENT 'Total cost of goods sold (COGS): quantity_sold * unit_cost_at_sale'
);
ALTER TABLE 
    `transaction_items` ADD FOREIGN KEY (`batch_id`) REFERENCES `product_batches`(`batch_id`);
ALTER TABLE
    `transaction_items` ADD INDEX `transaction_items_tx`(`transaction_id`, `product_id`);
CREATE TABLE `inventory_log`(
    `log_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` INT NOT NULL,
    `change_type` ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
    `quantity` SMALLINT NOT NULL,
    `unit_cost` DECIMAL(12, 2) NOT NULL,
    `log_date` DATETIME NOT NULL,
    `reference_id` BIGINT NULL COMMENT 'Links to transaction id/batch id',
    `reference_type` ENUM('SALE','PURCHASE','REFUND','ADJUSTMENT') NULL
);
ALTER TABLE
    `inventory_log` ADD INDEX `inventory_log_log_date_index`(`log_date`);
CREATE TABLE `purchase_orders`(
    `po_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `supplier_id` SMALLINT UNSIGNED NOT NULL,
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
ALTER TABLE 
    `purchase_order_items` ADD UNIQUE (po_id, product_id);
CREATE TABLE `product_batches`(
    `batch_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `po_id` BIGINT UNSIGNED,
    `product_id` INT NOT NULL,
    `supplier_id` SMALLINT UNSIGNED NOT NULL,
    `quantity_received` SMALLINT NOT NULL,
    `quantity_remaining` SMALLINT NOT NULL,
    `unit_cost` DECIMAL(12, 2) NOT NULL,
    `date_received` DATETIME NOT NULL,
    `barcode` VARCHAR(100) NOT NULL UNIQUE 
);
ALTER TABLE `product_batches`
    ADD CONSTRAINT `chk_product_batches_quantity_remaining_nonneg` CHECK (`quantity_remaining` >= 0);
ALTER TABLE `product_batches`
    ADD CONSTRAINT `fk_product_batches_po_id` FOREIGN KEY (`po_id`) REFERENCES `purchase_orders`(`po_id`);
CREATE TABLE `operational_costs`(
    `cost_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `cost_type` ENUM(
        'RENT',
        'UTILITIES',
        'WAGES',
        'MAINTENANCE',
        'OTHER'
    ) NOT NULL,
    `amount` DECIMAL(12, 2) NOT NULL,
    `cost_date` DATETIME NOT NULL,
    `notes` VARCHAR(255) NULL
);
ALTER TABLE
    `operational_costs` ADD INDEX `operational_costs_cost_date_index`(`cost_date`);
ALTER TABLE
    `operational_costs` COMMENT = 'For calculating total_operating_costs & total_costs (COGS + operating_costs)';
CREATE TABLE `investments`(
    `investment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `amount` DECIMAL(12, 2) NOT NULL,
    `investment_date` DATETIME NOT NULL,
    `description` VARCHAR(255) NULL
);
CREATE TABLE `demand_forecasts` (
    forecast_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id INT UNSIGNED NOT NULL,
    forecast_date DATE NOT NULL,    
    predicted_demand DECIMAL(12,2) NOT NULL,
    model_name VARCHAR(100) COMMENT 'e.g. ARIMA, LSTM',    
    generated_at DATETIME NOT NULL COMMENT 'when prediction was made',

    FOREIGN KEY (product_id) REFERENCES product(product_id)
);
ALTER TABLE 
    `demand_forecasts` ADD UNIQUE (`product_id`, `forecast_date`, `model_name`);
ALTER TABLE
    `demand_forecasts` ADD INDEX `demand_forecasts_index`(`product_id`, `forecast_date`);
CREATE TABLE `reorder_predictions` (
    prediction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id INT UNSIGNED NOT NULL,
    current_stock DECIMAL(12,2),
    predicted_reorder_point DECIMAL(12,2),
    recommended_order_qty DECIMAL(12,2),
    model_name VARCHAR(100) COMMENT 'e.g. ARIMA, LSTM',    
    generated_at DATETIME NOT NULL,

    FOREIGN KEY (product_id) REFERENCES product(product_id)
);
CREATE TABLE `profit_predictions` (
    prediction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id INT UNSIGNED NOT NULL,
    predicted_profit DECIMAL(14,2) NOT NULL,
    forecast_date DATE NOT NULL,
    model_name VARCHAR(100),
    generated_at DATETIME NOT NULL,

    FOREIGN KEY (product_id) REFERENCES product(product_id)
);
CREATE TABLE financial_predictions (
    prediction_id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    metric_type ENUM('ROI','CAGR', 'NET_PROFIT', 'GROSS_PROFIT') NOT NULL,
    predicted_value DECIMAL(12,4) NOT NULL,
    forecast_date DATE NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    generated_at DATETIME NOT NULL
);
ALTER TABLE 
    `financial_predictions` ADD UNIQUE (metric_type, forecast_date);
CREATE TABLE `roi_break_even_predictions` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    investment_id BIGINT NOT NULL,
    predicted_break_even_date DATE NOT NULL,
    investment_amount DECIMAL(14,2) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    generated_at DATETIME NOT NULL
);
CREATE TABLE `ebit_predictions` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    forecast_date DATE NOT NULL,
    predicted_ebit DECIMAL(14,2) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    generated_at DATETIME NOT NULL
);
ALTER TABLE
    `product` ADD CONSTRAINT `product_supplier_id_foreign` FOREIGN KEY(`supplier_id`) REFERENCES `supplier`(`supplier_id`);
ALTER TABLE
    `product_batches` ADD UNIQUE `product_batches_barcode_unique`(`barcode`);
ALTER TABLE
    `purchase_orders` ADD CONSTRAINT `purchase_orders_supplier_id_foreign` FOREIGN KEY(`supplier_id`) REFERENCES `supplier`(`supplier_id`);
ALTER TABLE
    `product_category` ADD CONSTRAINT `product_category_parent_category_id_foreign` FOREIGN KEY(`parent_category_id`) REFERENCES `product_category`(`category_id`);
ALTER TABLE
    `purchase_order_items` ADD CONSTRAINT `purchase_order_items_po_id_foreign` FOREIGN KEY(`po_id`) REFERENCES `purchase_orders`(`po_id`);
ALTER TABLE
    `purchase_order_items` ADD CONSTRAINT `purchase_order_items_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `product` ADD CONSTRAINT `product_category_id_foreign` FOREIGN KEY(`category_id`) REFERENCES `product_category`(`category_id`);
ALTER TABLE
    `inventory_log` ADD CONSTRAINT `inventory_log_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `transaction_items` ADD CONSTRAINT `transaction_items_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `transaction_items` ADD CONSTRAINT `transaction_items_transaction_id_foreign` FOREIGN KEY(`transaction_id`) REFERENCES `transaction_log`(`transaction_id`);
ALTER TABLE
    `product_batches` ADD CONSTRAINT `product_batches_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `compatibility` ADD CONSTRAINT `compatibility_vehicle_id_foreign` FOREIGN KEY(`vehicle_id`) REFERENCES `vehicles`(`vehicle_id`);
ALTER TABLE
    `compatibility` ADD CONSTRAINT `compatibility_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `product_images` ADD CONSTRAINT `product_images_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE 
    `transaction_log`ADD CONSTRAINT `fk_parent_transaction` FOREIGN KEY (`parent_transaction_id`) REFERENCES `transaction_log`(`transaction_id`);
