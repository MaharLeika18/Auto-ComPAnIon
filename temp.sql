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
    `supplier_id` SMALLINT NOT NULL,
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
