-- Tables for User Authenticator Module
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `user_id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(100) NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` ENUM('ADMIN', 'OWNER', 'SECRETARY', 'EMPLOYEE') NOT NULL,
    `created_at` DATETIME NOT NULL,
    `last_update` DATETIME NULL
);

-- Tables for Inventory Management Module
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`(
    `product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_name` VARCHAR(255) NOT NULL,
    `product_description` VARCHAR(255) NULL,
    `part_number` VARCHAR(200) NULL,
    `category_id` SMALLINT NOT NULL,
    `supplier_id` SMALLINT NOT NULL,
    `current_stock_level` SMALLINT NOT NULL,
    `storage_location` VARCHAR(255) NULL,
    `date_added` DATETIME NOT NULL,
    `last_update` DATETIME NOT NULL,
    `unit_cost` DECIMAL(12, 2) NOT NULL COMMENT 'Latest supplier price; regularly update this.',
    `retail_price` DECIMAL(12, 2) NOT NULL COMMENT 'Latest selling price; regularly update this.'
);

DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`(
    `category_id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `category_name` VARCHAR(255) NOT NULL,
    `parent_category_id` SMALLINT NULL,
    `date_added` DATETIME NOT NULL
);
ALTER TABLE 
    `product_category` ADD UNIQUE (category_name);

DROP TABLE IF EXISTS `product_images`;
CREATE TABLE product_images (
    `image_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` INT NOT NULL,
    `image_path` VARCHAR(255) NULL,
    `date_uploaded` DATETIME NOT NULL
);
ALTER TABLE 
    `product_images` ADD UNIQUE (image_path);

DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier`(
    `supplier_id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `supplier_name` VARCHAR(255) NOT NULL,
    `supplier_address` VARCHAR(255) NULL,
    `supplier_contact` BIGINT NULL,
    `date_added` DATETIME NOT NULL,
    `last_update` DATETIME NOT NULL
);
ALTER TABLE 
    `supplier` ADD UNIQUE (supplier_name);

DROP TABLE IF EXISTS `compatibility`;
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

DROP TABLE IF EXISTS `vehicles`;
CREATE TABLE `vehicles`(
    `vehicle_id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `model_name` VARCHAR(255) NOT NULL,
    `manufacturer_name` VARCHAR(255) NOT NULL
);
ALTER TABLE
    `vehicles` ADD INDEX `vehicles_manufacturer_name_index`(`manufacturer_name`);

-- Tables for Point of Sale Module
DROP TABLE IF EXISTS `transaction_log`;
CREATE TABLE `transaction_log`(
    `transaction_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `transaction_date` DATETIME NOT NULL,
    `total_amount` DECIMAL(12, 2) NOT NULL,
    `payment_method` ENUM('CASH', 'E-WALLET', 'BANK') NULL,
    `notes` VARCHAR(255) NULL
);
ALTER TABLE
    `transaction_log` ADD INDEX `transaction_log_transaction_date_index`(`transaction_date`);

DROP TABLE IF EXISTS `transaction_items`;
CREATE TABLE `transaction_items`(
    `item_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `transaction_id` BIGINT NOT NULL,
    `product_id` INT NOT NULL,
    `batch_id` BIGINT UNSIGNED NOT NULL,
    `quantity_sold` SMALLINT NOT NULL,
    `unit_selling_price` DECIMAL(12, 2) NOT NULL COMMENT 'Retail price per unit at the moment of sale',
    `unit_cost_at_sale` DECIMAL(12, 2) NOT NULL COMMENT 'Cost per unit from supplier at the moment of sale',
    `discount_applied` DECIMAL(12, 2) NOT NULL,
    `total_sale_value` DECIMAL(12, 2) NOT NULL COMMENT 'Total revenue: (quantity_sold * unit_selling_price) - (quantity_sold * discount_applied)',
    `total_cost` DECIMAL(12, 2) NOT NULL COMMENT 'Total cost of goods sold (COGS): quantity_sold * unit_cost_at_sale'
);
ALTER TABLE `transaction_items`
    ADD FOREIGN KEY (`batch_id`) REFERENCES `product_batches`(`batch_id`);

DROP TABLE IF EXISTS `pending_transactions_log`;
CREATE TABLE `pending_transactions_log`(
    `pending_transaction_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `creation_date` DATETIME NOT NULL,
    `total_amount` DECIMAL(12, 2) NOT NULL,
    `payment_method` ENUM('CASH', 'E-WALLET', 'BANK') NULL,
    `notes` VARCHAR(255) NULL
);
ALTER TABLE
    `pending_transactions_log` ADD INDEX `pending_transactions_log_creation_date_index`(`creation_date`);

DROP TABLE IF EXISTS `pending_transaction_items`;
CREATE TABLE `pending_transaction_items`(
    `pending_item_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
ALTER TABLE 
    `pending_transaction_items` ADD UNIQUE (`transaction_id`, `product_id`, `batch_id`);
ALTER TABLE
    `pending_transaction_items` ADD INDEX `idx_pending_tx`(`transaction_id`);

-- Tables for Predictive AI Module
DROP TABLE IF EXISTS `operational_costs`;
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

DROP TABLE IF EXISTS `inventory_log`;
CREATE TABLE `inventory_log`(
    `log_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` INT NOT NULL,
    `change_type` ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
    `quantity` SMALLINT NOT NULL,
    `unit_cost` DECIMAL(8, 2) NOT NULL,
    `log_date` DATETIME NOT NULL,
    `reference_id` BIGINT NULL COMMENT 'Links to transaction id/batch id',
    `reference_type` ENUM('TRANSACTION', 'PURCHASE', 'BATCH') NULL
);
ALTER TABLE
    `inventory_log` ADD INDEX `inventory_log_log_date_index`(`log_date`);

DROP TABLE IF EXISTS `investments`;
CREATE TABLE `investments`(
    `investment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `amount` DECIMAL(12, 2) NOT NULL,
    `investment_date` DATETIME NOT NULL,
    `description` VARCHAR(255) NULL
);

DROP TABLE IF EXISTS `purchase_orders`;
CREATE TABLE `purchase_orders`(
    `po_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `supplier_id` SMALLINT NOT NULL,
    `order_date` DATETIME NOT NULL,
    `total_cost` DECIMAL(12, 2) NOT NULL
);

DROP TABLE IF EXISTS `purchase_order_items`;
CREATE TABLE `purchase_order_items`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `po_id` BIGINT NOT NULL,
    `product_id` INT NOT NULL,
    `quantity` SMALLINT NOT NULL,
    `unit_cost` DECIMAL(12, 2) NOT NULL
);

DROP TABLE IF EXISTS `product_batches`;
CREATE TABLE `product_batches`(
    `batch_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` INT NOT NULL,
    `supplier_id` BIGINT NOT NULL,
    `quantity_received` SMALLINT NOT NULL,
    `quantity_remaining` SMALLINT NOT NULL,
    `unit_cost` DECIMAL(12, 2) NOT NULL,
    `date_received` DATETIME NOT NULL,
    `barcode` VARCHAR(100) NOT NULL UNIQUE 
);

DROP TABLE IF EXISTS `demand_forecasts`;
CREATE TABLE `demand_forecasts` (
    forecast_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    forecast_date DATE NOT NULL COMMENT 'Date being predicted',    
    predicted_demand DECIMAL(12,2) NOT NULL,
    model_name VARCHAR(100) COMMENT 'e.g. ARIMA, LSTM',    
    generated_at DATETIME NOT NULL COMMENT 'when prediction was made',

    FOREIGN KEY (product_id) REFERENCES product(product_id)
);
ALTER TABLE 
    `demand_forecasts` ADD UNIQUE (product_id, forecast_date, model_name);

DROP TABLE IF EXISTS `reorder_predictions`;
CREATE TABLE `reorder_predictions` (
    prediction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    current_stock DECIMAL(12,2),
    predicted_reorder_point DECIMAL(12,2),
    recommended_order_qty DECIMAL(12,2),
    model_name VARCHAR(100) COMMENT 'e.g. ARIMA, LSTM',    
    generated_at DATETIME NOT NULL,

    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

DROP TABLE IF EXISTS `profit_predictions`;
CREATE TABLE `profit_predictions` (
    prediction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    predicted_profit DECIMAL(14,2) NOT NULL,
    period_start DATETIME NOT NULL,
    period_end DATETIME NOT NULL,
    model_name VARCHAR(100),
    generated_at DATETIME NOT NULL

    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

DROP TABLE IF EXISTS `financial_predictions`;
CREATE TABLE financial_predictions (
    prediction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric_type ENUM('ROI','CAGR'),
    predicted_value DECIMAL(12,4),
    period_start DATE,
    period_end DATE,
    model_name VARCHAR(100),
    generated_at DATETIME NOT NULL
);
ALTER TABLE 
    `financial_predictions` ADD UNIQUE (period_start, period_end);



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
ALTER TABLE
    `compatibility` ADD CONSTRAINT `compatibility_vehicle_id_foreign` FOREIGN KEY(`vehicle_id`) REFERENCES `vehicles`(`vehicle_id`);
ALTER TABLE
    `compatibility` ADD CONSTRAINT `compatibility_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);
ALTER TABLE
    `product_images` ADD CONSTRAINT `product_images_product_id_foreign` FOREIGN KEY(`product_id`) REFERENCES `product`(`product_id`);


------------------------------------------------------------
------------------------------------------------------------
-- NOTE: The syntax I will be using for the naming conventions of input variables is:
-- (initial(s) of referenced table)_(optionaly clarifying info)_(referenced row)
-- e.g. v_comp_manufacturer_name = v for the 'vehicle' table, comp for compatible (for clarity), 
-- manufacturer_name is referenced row. 
-- If it's incomprehensible, bother me about it.

-- NOTE: When calling procs that edit table info, only send values that changed.

-- Procedures for Acct Management Module:
--      Register new user (Employee role ONLY)
DROP PROCEDURE IF EXISTS register_user; 

DELIMITER //

CREATE PROCEDURE register_user (
    IN u_username VARCHAR(100),
    IN u_name VARCHAR(100),
    IN u_password_hash VARCHAR(255),
    IN u_role ENUM('ADMIN', 'OWNER', 'SECRETARY', 'EMPLOYEE')
) 
BEGIN
    -- Check if username already exists
    IF EXISTS (
        SELECT 1 FROM users 
        WHERE username = u_username
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Username taken.';
    END IF;

    INSERT INTO users (
		username, 
		name, 
		password_hash, 
		role, 
		created_at
	)
	VALUES (	
		u_username, 
        u_name, 
        u_password_hash, 
        u_role, 
        NOW()
	);
END //

DELIMITER;

--      Remove existing user (For employee accts ONLY)
DROP PROCEDURE IF EXISTS remove_user; 

DELIMITER //

CREATE PROCEDURE remove_user (
    IN u_user_id BIGINT
)
BEGIN
    -- Check if user exists
    IF NOT EXISTS (
        SELECT 1
        FROM users 
        WHERE user_id = u_user_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'User not found';
    END IF;

    DELETE FROM users 
    WHERE user_id = u_user_id;
END //

DELIMITER ;

--      Change information of existing user (use NULL if no change)
DROP PROCEDURE IF EXISTS edit_user; 

DELIMITER //

CREATE PROCEDURE edit_user (
    IN u_user_id BIGINT,
    IN u_username VARCHAR(100),
    IN u_name VARCHAR(100),
    IN u_password_hash VARCHAR(255),
    IN u_role ENUM('ADMIN', 'OWNER', 'SECRETARY', 'EMPLOYEE')
)
BEGIN
    -- Check if user exists
    IF NOT EXISTS (
        SELECT 1
        FROM users
        WHERE user_id = u_user_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'User not found';
    END IF;

    UPDATE user
    SET
        username = COALESCE(u_username, username),
        name = COALESCE(u_name, name),
        password_hash = COALESCE(u_password_hash, password_hash),
        role = COALESCE(u_role, role), -- needed a comma
        last_update = NOW()
    WHERE user_id = u_user_id;

END //

DELIMITER ;

--      View user credentials (Admin & Owner ONLY can use this)
DROP PROCEDURE IF EXISTS remove_user; 

DELIMITER //

CREATE PROCEDURE remove_user (
    OUT user_id
)
BEGIN
    SELECT
        u.name,
        u.username,
        u.password_hash,    -- Use java to show unhashed pw
        u.role
    FROM users u
    GROUP BY u.role
    ORDER BY u.name;
END //

DELIMITER ;

-- Procedures for Inventory Module: 
--      Add new product record to Main Data Tables (Refer to DC)
--          * Must include all the relevant fields from all tables that references 'product' table
--          * It will only accept existing ids for category, supplier, and compatible vehicle.
--          * If desired info for any of those rows don't exist in db, call the proc to add new row 
--            to the respective table. This should be handled by java function. 
DROP PROCEDURE IF EXISTS add_product; 

DELIMITER //

CREATE PROCEDURE add_product (
    IN p_name VARCHAR(255),
    IN p_description VARCHAR(255),
    IN p_part_number VARCHAR(200),
    IN i_image_path VARCHAR(255),
    IN c_category_name VARCHAR(255),
    IN s_supplier_name VARCHAR(255),
    IN p_current_stock_level SMALLINT,
    IN p_location VARCHAR(255),
    IN p_unit_cost DECIMAL(12,2),
    IN p_retail_price DECIMAL(12,2),
    IN v_comp_manufacturer_name VARCHAR(255),
    IN v_comp_model_name VARCHAR(255),
    IN co_comp_bottom_year SMALLINT,
    IN co_comp_top_year SMALLINT
)
BEGIN
    DECLARE c_category_id, s_supplier_id, v_vehicle_id SMALLINT;

    -- Validate categorical data exists
    SELECT category_id INTO c_category_id
    FROM product_category
    WHERE LOWER(category_name) = LOWER(c_category_name)
    LIMIT 1;

    SELECT supplier_id INTO s_supplier_id
    FROM supplier
    WHERE LOWER(supplier_name) = LOWER(s_supplier_name)
    LIMIT 1;

    SELECT vehicle_id INTO v_vehicle_id
    FROM vehicles
    WHERE LOWER(model_name) = LOWER(v_comp_model_name)
    LIMIT 1;

    IF c_category_id IS NULL THEN 
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid category_id';
    END IF;

    IF s_supplier_id IS NULL THEN 
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid s_supplier_id';
    END IF;

    IF v_vehicle_id IS NULL THEN 
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid v_vehicle_id';
    END IF;

    INSERT INTO product (
        product_name,
        product_description,
        part_number,
        category_id,
        supplier_id,
        current_stock_level,
        storage_location,
        last_update,
        unit_cost,
        retail_price
    )
    VALUES (
        p_name,
        p_description,
        p_part_number,
        c_category_id,
        s_supplier_id,
        p_current_stock_level,
        p_location,
        NOW(),
        p_unit_cost,
        p_retail_price
    );

    -- Take the auto assigned product id for use in following insert statements
    SET @p_product_id = (SELECT last_insert_id());

    INSERT INTO product_images (
        product_id,
        image_path,
        date_uploaded
    )
    VALUES (
        @p_product_id,
        i_image_path,
        NOW()
    );

    INSERT INTO compatibility (
        product_id,
        bottom_year,
        top_year,
        vehicle_id
    )
    VALUES (
        @p_product_id,
        co_comp_bottom_year,
        co_comp_top_year,
        v_vehicle_id
    );
END //

DELIMITER ;

--      Edit information of product record in Main Data Tables 
--          * Edit only the info provided, and only allow editing 
--            the product info, pricing, and classification.
DROP PROCEDURE IF EXISTS edit_product; 

DELIMITER //

CREATE PROCEDURE edit_product (
    IN p_product_id INT,
    IN p_name VARCHAR(255),
    IN p_description VARCHAR(255),
    IN p_part_number VARCHAR(200),    
    IN i_image_path VARCHAR(255),
    IN c_category_id SMALLINT,
    IN c_category_name VARCHAR(255),
    IN s_supplier_id SMALLINT,
    IN s_supplier_name VARCHAR(255),
    IN p_current_stock_level SMALLINT,
    IN p_location VARCHAR(255),
    IN p_unit_cost DECIMAL(12,2),
    IN p_retail_price DECIMAL(12,2),
    IN v_comp_manufacturer_name VARCHAR(255),
    IN v_comp_vehicle_id SMALLINT,
    IN v_comp_model_name VARCHAR(255),
    IN co_comp_bottom_year SMALLINT,
    IN co_comp_top_year SMALLINT
)
BEGIN
    -- Check if product exists
    IF NOT EXISTS (
        SELECT 1
        FROM product
        WHERE product_id = p_product_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Product not found';
    END IF;

    -- Check if category exists
    IF c_category_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM product_category WHERE category_id = c_category_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid category_id';
    END IF;

    -- Check if supplier exists
        IF s_supplier_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM supplier WHERE supplier_id = s_supplier_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid supplier_id';
    END IF;

    -- Check if vehicle exists
        IF v_comp_vehicle_id IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM vehicles WHERE vehicle_id = v_comp_vehicle_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid vehicle_id';
    END IF;

    -- Update info
    UPDATE product
    SET
        product_name = COALESCE(p_name, product_name),
        product_description = COALESCE(p_description, product_description),
        part_number = COALESCE(p_part_number, part_number),
        category_id = COALESCE(c_category_id, category_id),
        supplier_id = COALESCE(s_supplier_id, supplier_id),
        current_stock_level = COALESCE(p_current_stock_level, current_stock_level),
        storage_location = COALESCE(p_location, storage_location),
        last_update = NOW(),
        unit_cost = COALESCE(p_unit_cost, unit_cost),
        retail_price = COALESCE(p_retail_price, retail_price)
    WHERE product_id = p_product_id;

    UPDATE product_images
    SET
        image_path = COALESCE(i_image_path, image_path),
        date_uploaded = COALESCE(p_description, product_description) -- took off unneeded comma
    WHERE product_id = p_product_id;

END //
    );
END$$

DELIMITER ;

--      Add new product category
DROP PROCEDURE IF EXISTS add_product_category; 

DELIMITER //

CREATE PROCEDURE add_product_category (
    IN c_category_name VARCHAR(255),
    IN c_parent_category_id VARCHAR(255)
) 
BEGIN
    INSERT INTO product_category (
        category_name,
        parent_category_id,
        date_added
    ) VALUES (
        c_category_name,
        c_parent_category_id,
        NOW()
    );
END //

DELIMITER;

--      Add new supplier
DROP PROCEDURE IF EXISTS add_supplier; 

DELIMITER //

CREATE PROCEDURE add_supplier (
    IN s_supplier_name VARCHAR(255),
    IN s_supplier_address VARCHAR(255),
    IN s_supplier_contact BIGINT
) 
BEGIN
    INSERT INTO supplier (
        supplier_name,
        supplier_address,
        supplier_contact,
        date_added,
        last_update
    ) VALUES (
        s_supplier_name,
        s_supplier_address,
        s_supplier_contact,
        NOW(),
        NOW()
    );
END //

DELIMITER;

--      Add new vehicle
DROP PROCEDURE IF EXISTS add_vehicle; 

DELIMITER //

CREATE PROCEDURE add_vehicle (
    IN v_model_name VARCHAR(255),
    IN v_manufacturer_name VARCHAR(255)
) 
BEGIN
    INSERT INTO vehicles (
        model_name,
        manufacturer_name
    ) VALUES (
        v_model_name,
        v_manufacturer_name
    );
END //

DELIMITER;

--      Remove selected existing product record(s) from Main Data Tables
--      Remove existing product category
--      Remove existing supplier
--      Remove existing vehicle

--      Edit product category
--      Edit supplier
--      Edit vehicle

--      Fetch list of product(s) & basic info based on search params
--          * This is the initial load on page
--          * Ensure to add pagination for all of these
--      Fetch expanded product information based on selection
--      Query list of product(s) based on search and/or filter + sorting


-- Procedures for Point of Sale Module:
--      Add new transaction to temporary transaction log
DROP PROCEDURE IF EXISTS add_pending_transaction; 

DELIMITER //

CREATE PROCEDURE add_pending_transaction (
    IN pt_total_amount DECIMAL(12, 2),
    IN pt_payment_method ENUM('CASH', 'E-WALLET', 'BANK'),
    IN pt_notes VARCHAR(255),
    OUT pt_transaction_id BIGINT
) 
BEGIN
    INSERT INTO pending_transactions_log (
        creation_date,
        total_amount,
        payment_method,
        notes
    ) VALUES (
        NOW(),
        pt_total_amount,        
        pt_payment_method,
        pt_notes
    );
    
    SET pt_transaction_id = LAST_INSERT_ID();
END //

DELIMITER;

--      Add items to transaction in temp transact log
DROP PROCEDURE IF EXISTS add_pending_transaction_items; 

DELIMITER //

CREATE PROCEDURE add_pending_transaction_items (
    IN pi_transaction_id BIGINT,
    IN pi_product_id INT,
    IN pi_batch_id BIGINT,
    IN pi_quantity_sold SMALLINT,
    IN pi_unit_selling_price DECIMAL(12, 2),
    IN pi_unit_cost_at_sale DECIMAL(12, 2),
    IN pi_discount_applied DECIMAL(12, 2)
) 
BEGIN
    -- Check if transaction_id is valid
    IF NOT EXISTS (
        SELECT 1 FROM pending_transactions_log 
        WHERE pending_transaction_id = pi_transaction_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid pending_transaction_id';
    END IF;

    -- Check if stock is sufficient
    IF EXISTS (
        SELECT 1
        FROM pending_transaction_items pti
        JOIN product p ON pti.product_id = p.product_id
        WHERE pti.quantity_sold > p.current_stock_level
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock';
    END IF;

    -- Compute totals
    SET var_total_sale_value = (pi_quantity_sold * pi_unit_selling_price) - pi_discount_applied;
    SET var_total_cost = pi_quantity_sold * pi_unit_cost_at_sale;

    INSERT INTO pending_transaction_items (
        transaction_id,
        product_id,
        batch_id,
        quantity_sold,
        unit_selling_price,
        unit_cost_at_sale,
        discount_applied,
        total_sale_value,
        total_cost
    ) VALUES (
        @pi_transaction_id,
        pi_product_id,
        pi_batch_id,
        pi_quantity_sold,
        pi_unit_selling_price,
        pi_unit_cost_at_sale,
        pi_discount_applied,
        var_total_sale_value,
        var_total_cost
    );
END //

DELIMITER;

--      Edit transaction info in temp log
--          * Call this proc for most UI actions in POS
DROP PROCEDURE IF EXISTS upsert_pending_transaction_item; 

DELIMITER //

CREATE PROCEDURE upsert_pending_transaction_item (
    IN p_transaction_id BIGINT,
    IN p_product_id INT,
    IN p_batch_id BIGINT,
    IN p_quantity INT,
    IN p_unit_price DECIMAL(12,2),
    IN p_discount DECIMAL(12,2)
)
BEGIN

	DECLARE var_existing_id BIGINT;
    DECLARE var_total_sale_value DECIMAL(12,2);
    DECLARE var_total_cost DECIMAL(12,2);
    
    SELECT unit_cost INTO p_unit_cost FROM product WHERE product_id = p_product_id;

    START TRANSACTION;

    -- Validate transaction
    IF NOT EXISTS (
        SELECT 1 FROM pending_transactions_log 
        WHERE pending_transaction_id = p_transaction_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid pending_transaction_id';
    END IF;

    -- Check if stock is sufficient
    IF EXISTS (
        SELECT 1
        FROM pending_transaction_items pti
        JOIN product p ON pti.product_id = p.product_id
        WHERE pti.quantity_sold > p.current_stock_level
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock';
    END IF;

    -- Compute totals
    SET var_total_sale = (p_quantity * p_unit_price) - p_discount;
    SET var_total_cost = (p_quantity * p_unit_cost);

    -- Handle negative total
    IF var_total_sale_value < 0 THEN
        SET var_total_sale_value = 0;
    END IF;

    -- Check if item already exists
    SELECT pending_item_id INTO var_existing_id
    FROM pending_transaction_items
    WHERE transaction_id = p_transaction_id
      AND product_id = p_product_id
      AND batch_id = p_batch_id
    LIMIT 1;

    -- Remove item if quantity <= 0
    IF p_quantity <= 0 THEN
        DELETE FROM pending_transaction_items
        WHERE transaction_id = p_transaction_id
          AND product_id = p_product_id
          AND batch_id = p_batch_id;

    -- Update existing item
    ELSEIF var_existing_id IS NOT NULL THEN
        UPDATE pending_transaction_items
        SET
            quantity_sold = p_quantity,
            unit_selling_price = p_unit_price,
            unit_cost_at_sale = p_unit_cost,
            discount_applied = p_discount,
            total_sale_value = var_total_sale_value,
            total_cost = var_total_cost
        WHERE item_id = var_existing_id;

    -- Insert new item
    ELSE
        INSERT INTO pending_transaction_items (
            transaction_id,
            product_id,
            batch_id,
            quantity_sold,
            unit_selling_price,
            unit_cost_at_sale,
            discount_applied,
            total_sale_value,
            total_cost
        ) VALUES (
            p_transaction_id,
            p_product_id,
            p_batch_id,
            p_quantity,
            p_unit_price,
            p_unit_cost,
            p_discount,
            var_total_sale_value,
            var_total_cost
        );
    END IF;

    -- Recalculate transaction total
    UPDATE pending_transactions_log
    SET total_amount = (
        SELECT IFNULL(SUM(total_sale_value), 0)
        FROM pending_transaction_items
        WHERE transaction_id = p_transaction_id
    )
    WHERE transaction_id = p_transaction_id;

END //

DELIMITER ;

--      Remove items from temporary transaction log
DROP PROCEDURE IF EXISTS cancel_pending_transaction; 

DELIMITER //

CREATE PROCEDURE cancel_pending_transaction (
    IN p_transaction_id BIGINT
)
BEGIN
    START TRANSACTION;

    DELETE FROM pending_transaction_items
    WHERE transaction_id = p_transaction_id;

    DELETE FROM pending_transactions_log
    WHERE pending_transaction_id = p_transaction_id;

    COMMIT;

END //

DELIMITER ;

--      Move confirmed items from temporary transaction log to transaction log, update inventory_log accordingly
DROP PROCEDURE IF EXISTS confirm_pending_transaction; 

DELIMITER //

CREATE PROCEDURE confirm_pending_transaction (
    IN p_transaction_id BIGINT
)
BEGIN
    DECLARE var_new_transaction_id BIGINT;
    DECLARE var_total DECIMAL(12,2);

    START TRANSACTION;

    -- Validate pending transaction exists
    IF NOT EXISTS (
        SELECT 1 FROM pending_transactions_log
        WHERE pending_transaction_id = p_transaction_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Pending transaction not found';
    END IF;

    -- Check if stock is sufficient
    IF EXISTS (
        SELECT 1
        FROM pending_transaction_items pti
        JOIN product p ON pti.product_id = p.product_id
        WHERE pti.quantity_sold > p.current_stock_level
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock';
    END IF;

    -- Get total
    SELECT total_amount INTO var_total
    FROM pending_transactions_log
    WHERE pending_transaction_id = p_transaction_id;

    -- Insert into final transaction table
    INSERT INTO transaction_log (
        transaction_date,
        total_amount
    )
    VALUES (
        NOW(),
        var_total
    );

    SET var_new_transaction_id = LAST_INSERT_ID();

    -- Move items
    INSERT INTO transaction_items (
        transaction_id,
        product_id,
        batch_id,
        quantity_sold,
        unit_selling_price,
        unit_cost_at_sale,
        discount_applied,
        total_sale_value,
        total_cost
    )
    SELECT
        var_new_transaction_id,
        product_id,
        batch_id,
        quantity_sold,
        unit_selling_price,
        unit_cost_at_sale,
        discount_applied,
        total_sale_value,
        total_cost
    FROM pending_transaction_items
    WHERE pending_item_id = p_transaction_id;

    -- Update inventory (OUT)
    INSERT INTO inventory_log (
        product_id,
        change_type,
        quantity,
        unit_cost,
        log_date,
        reference_id
    )
    SELECT
        product_id,
        'OUT',
        quantity_sold,
        unit_cost_at_sale,
        NOW(),
        var_new_transaction_id
    FROM pending_transaction_items
    WHERE transaction_id = p_transaction_id;

    -- Update current stock
    UPDATE product p
    JOIN pending_transaction_items pti
        ON p.product_id = pti.product_id
    SET p.current_stock_level = p.current_stock_level - pti.quantity_sold
    WHERE pti.transaction_id = p_transaction_id;

    -- Delete pending items
    DELETE FROM pending_transaction_items
    WHERE transaction_id = p_transaction_id;

    -- Delete pending transaction
    DELETE FROM pending_transactions_log
    WHERE pending_transaction_id = p_transaction_id;

    COMMIT;

END //

DELIMITER ;

--      Fetch transaction details to display on screen

--      Fetch list of product(s) & basic info based on search params
--          * This and the following procs are similar to the ones for the Inventory mod
--          * Ensure to add pagination for this and the following

--      Fetch expanded product information based on selection


-- Procedures for Business Analytics & Predictive AI
-- Note: The following may span multiple procs
--      Fetch data to calculate all the computable business metrics that don't need a trained AI here
--      Fetch data to train model for demand forecasting
--      Create training dataset for Demand Forecasting (time series per product)
DROP PROCEDURE IF EXISTS dataset_sales_timeseries; 

DELIMITER //

CREATE PROCEDURE dataset_sales_timeseries (
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    SELECT 
        p.product_id,
        p.product_name,
        DATE(t.transaction_date) AS sale_date,
        SUM(ti.quantity_sold) AS total_quantity
    FROM transaction_items ti
    JOIN transaction_log t ON ti.transaction_id = t.transaction_id
    JOIN product p ON ti.product_id = p.product_id
    WHERE t.transaction_date BETWEEN p_start_date AND p_end_date
    GROUP BY p.product_id, DATE(t.transaction_date)
    ORDER BY p.product_id, sale_date;
END //

DELIMITER ;

--      Create training dataset for Reorder Prediction (stock + demand features)
DROP PROCEDURE IF EXISTS dataset_reorder; 

DELIMITER //

CREATE PROCEDURE dataset_reorder (
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    SELECT 
        p.product_id,
        p.product_name,

        DATE(t.transaction_date) AS sale_date,

        SUM(ti.quantity_sold) AS daily_demand,

        p.current_stock_level,

        AVG(ti.unit_cost_at_sale) AS avg_cost

    FROM transaction_items ti
    JOIN transaction_log t 
        ON ti.transaction_id = t.transaction_id
    JOIN product p 
        ON ti.product_id = p.product_id

    WHERE t.transaction_date BETWEEN p_start_date AND p_end_date

    GROUP BY p.product_id, DATE(t.transaction_date)
    ORDER BY p.product_id, sale_date;
END //

DELIMITER ;

--      Create training dataset for Profit Prediction (per-product profit metrics)
DROP PROCEDURE IF EXISTS dataset_profit_analysis; 

DELIMITER //

CREATE PROCEDURE dataset_profit_analysis (
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    SELECT 
        p.product_id,

        DATE(t.transaction_date) AS sale_date,

        SUM(ti.quantity_sold) AS total_quantity,

        AVG(ti.unit_selling_price) AS avg_price,
        AVG(ti.unit_cost_at_sale) AS avg_cost,

        SUM(ti.total_sale_value) AS revenue,
        SUM(ti.total_cost) AS cost,

        (SUM(ti.total_sale_value) - SUM(ti.total_cost)) AS profit

    FROM transaction_items ti
    JOIN transaction_log t 
        ON ti.transaction_id = t.transaction_id
    JOIN product p 
        ON ti.product_id = p.product_id

    WHERE t.transaction_date BETWEEN p_start_date AND p_end_date

    GROUP BY p.product_id, DATE(t.transaction_date)
    ORDER BY p.product_id, sale_date;
END //

DELIMITER ;

--      Create training dataset for ROI (aggregated financials over time)
DROP PROCEDURE IF EXISTS calculate_roi; 
DELIMITER //

CREATE PROCEDURE calculate_roi (
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    DECLARE total_revenue DECIMAL(14,2);
    DECLARE total_cost DECIMAL(14,2);
    DECLARE total_investment DECIMAL(14,2);
    DECLARE net_profit DECIMAL(14,2);
    DECLARE roi DECIMAL(10,2);

    -- Revenue
    SELECT IFNULL(SUM(ti.total_sale_value), 0)
    INTO total_revenue
    FROM transaction_items ti
    JOIN transaction_log t 
        ON ti.transaction_id = t.transaction_id
    WHERE t.transaction_date BETWEEN p_start_date AND p_end_date;

    -- COGS
    SELECT IFNULL(SUM(ti.total_cost), 0)
    INTO total_cost
    FROM transaction_items ti
    JOIN transaction_log t 
        ON ti.transaction_id = t.transaction_id
    WHERE t.transaction_date BETWEEN p_start_date AND p_end_date;

    -- Investments
    SELECT IFNULL(SUM(amount), 0)
    INTO total_investment
    FROM investments
    WHERE investment_date BETWEEN p_start_date AND p_end_date;

    -- Operational Costs
    SELECT IFNULL(SUM(amount), 0)
    INTO operational_costs_total
    FROM operational_costs
    WHERE cost_date BETWEEN p_start_date AND p_end_date;

    -- Net Profit
    SET net_profit = total_revenue - total_cost - operational_costs_total;

    -- ROI
    IF total_investment = 0 THEN
        SET roi = NULL;
    ELSE
        SET roi = (net_profit / total_investment) * 100;
    END IF;

    -- Return result
    SELECT 
        p_start_date AS period_start,
        p_end_date AS period_end,
        total_revenue,
        total_cost,
        total_investment,
        net_profit,
        roi;
    END //
DELIMITER ;

DROP PROCEDURE IF EXISTS dataset_roi_timeseries; 
DELIMITER //
CREATE PROCEDURE dataset_roi_timeseries (
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    SELECT
        DATE(t.transaction_date) AS period_date,

        -- Revenue
        SUM(ti.total_sale_value) AS revenue,

        -- Cost
        SUM(ti.total_cost) AS cost,

        -- Operational Costs (joined per day)
        (
            SELECT IFNULL(SUM(oc.amount), 0)
            FROM operational_costs oc
            WHERE DATE(oc.cost_date) = DATE(t.transaction_date)
        ) AS operational_costs,

        -- Investments (joined per day)
        (
            SELECT IFNULL(SUM(i.amount), 0)
            FROM investments i
            WHERE DATE(i.investment_date) = DATE(t.transaction_date)
        ) AS investments,

        -- Net Profit
        (
            SUM(ti.total_sale_value) - 
            SUM(ti.total_cost)
        ) AS net_profit,

        -- ROI
        CASE 
            WHEN (
                SELECT IFNULL(SUM(i.amount), 0)
                FROM investments i
                WHERE DATE(i.investment_date) = DATE(t.transaction_date)
            ) = 0 THEN NULL
            ELSE (
                (SUM(ti.total_sale_value) - SUM(ti.total_cost)) /
                (
                    SELECT IFNULL(SUM(i.amount), 0)
                    FROM investments i
                    WHERE DATE(i.investment_date) = DATE(t.transaction_date)
                )
            ) * 100
        END AS roi

    FROM transaction_items ti
    JOIN transaction_log t 
        ON ti.transaction_id = t.transaction_id

    WHERE t.transaction_date BETWEEN p_start_date AND p_end_date

    GROUP BY YEAR(t.transaction_date), MONTH(t.transaction_date)
    ORDER BY period_date;

    END //
DELIMITER ;

--      Create training dataset for CAGR (Yearly revenue)
DELIMITER //

CREATE PROCEDURE calculate_cagr (
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    DECLARE start_value DECIMAL(14,2);
    DECLARE end_value DECIMAL(14,2);
    DECLARE years DECIMAL(10,4);
    DECLARE cagr DECIMAL(10,4);

    -- Start revenue
    SELECT SUM(ti.total_sale_value)
    INTO start_value
    FROM transaction_items ti
    JOIN transaction_log t 
        ON ti.transaction_id = t.transaction_id
    WHERE DATE(t.transaction_date) = DATE(p_start_date);

    -- End revenue
    SELECT SUM(ti.total_sale_value)
    INTO end_value
    FROM transaction_items ti
    JOIN transaction_log t 
        ON ti.transaction_id = t.transaction_id
    WHERE DATE(t.transaction_date) = DATE(p_end_date);

    -- Years difference
    SET years = TIMESTAMPDIFF(MONTH, p_start_date, p_end_date) / 12;

    IF start_value IS NULL OR start_value = 0 OR years = 0 THEN
        SET cagr = NULL;
    ELSE
        SET cagr = (POWER(end_value / start_value, 1 / years) - 1) * 100;
    END IF;

    SELECT 
        p_start_date AS period_start,
        p_end_date AS period_end,
        start_value,
        end_value,
        years,
        cagr;
END //

DELIMITER ;

DROP PROCEDURE IF EXISTS dataset_yearly_revenue; 

DELIMITER //

CREATE PROCEDURE dataset_cagr_timeseries (
    IN p_start_date DATETIME,
    IN p_end_date DATETIME
)
BEGIN
    -- Monthly revenue aggregation
    WITH monthly_data AS (
        SELECT
            DATE_FORMAT(t.transaction_date, '%Y-%m-01') AS period_month,
            SUM(ti.total_sale_value) AS revenue
        FROM transaction_items ti
        JOIN transaction_log t 
            ON ti.transaction_id = t.transaction_id
        WHERE t.transaction_date BETWEEN p_start_date AND p_end_date
        GROUP BY period_month
    ),
    -- Lagged revenue (12 months before)
    lagged AS (
        SELECT
            m1.period_month,
            m1.revenue AS current_revenue,
            m2.revenue AS past_revenue
        FROM monthly_data m1
        LEFT JOIN monthly_data m2
            ON m2.period_month = DATE_SUB(m1.period_month, INTERVAL 12 MONTH)
    )
    -- Calculate CAGR
    SELECT
        period_month,
        current_revenue,
        past_revenue,

        CASE 
            WHEN past_revenue IS NULL OR past_revenue = 0 THEN NULL
            ELSE (
                POWER(current_revenue / past_revenue, 1.0 / 1) - 1
            ) * 100
        END AS cagr

    FROM lagged
    ORDER BY period_month;
END //

DELIMITER ;

--      Add generated prediction results into respective table
    -- Demand Forecasting
DROP PROCEDURE IF EXISTS add_demand_forecast; 
DELIMITER //

CREATE PROCEDURE add_demand_forecast (
    IN df_product_id INT,
    IN df_forecast_date DATE,
    IN df_predicted_demand DECIMAL(12,2),
    IN df_model_name VARCHAR(100)
)
BEGIN
    INSERT INTO demand_forecasts (
        product_id,
        forecast_date,
        predicted_demand,
        model_name,
        generated_at
    )
    VALUES (
        df_product_id,
        df_forecast_date,
        df_predicted_demand,
        df_model_name,
        NOW()
    ) -- removed semicolon
    ON DUPLICATE KEY UPDATE
        predicted_demand = VALUES(predicted_demand),
        generated_at = NOW();
END //

DELIMITER ;

DROP PROCEDURE IF EXISTS get_demand_forecasts; 
DELIMITER //

CREATE PROCEDURE get_demand_forecasts ()
BEGIN
    SELECT *
    FROM demand_forecasts df
    WHERE generated_at = (
        SELECT MAX(generated_at)
        FROM demand_forecasts
    );
END //

DELIMITER ;

    -- Reorder Predictions
DELIMITER //
CREATE PROCEDURE dataset_lead_time ()
BEGIN
    SELECT
        po.po_id,
        poi.product_id,
        po.supplier_id,

        po.order_date,

        -- arrival date (from inventory log)
        MIN(il.log_date) AS received_date,

        DATEDIFF(MIN(il.log_date), po.order_date) AS lead_time_days,

        poi.quantity,
        poi.unit_cost

    FROM purchase_orders po
    JOIN purchase_order_items poi 
        ON po.po_id = poi.po_id

    JOIN inventory_log il 
        ON il.reference_id = po.po_id
        AND il.product_id = poi.product_id
        AND il.change_type = 'IN'

    GROUP BY po.po_id, poi.product_id

    HAVING lead_time_days IS NOT NULL
    ORDER BY po.order_date;

END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE add_reorder_prediction (
    IN rp_product_id INT,
    IN rp_predicted_reorder_point DECIMAL(12,2),
    IN rp_recommended_order_qty DECIMAL(12,2),
    IN rp_current_stock DECIMAL(12,2),
    IN rp_model_name VARCHAR(100)
)
BEGIN
    INSERT INTO reorder_predictions (
        product_id,
        current_stock,
        predicted_reorder_point,
        recommended_order_qty,
        model_name,
        generated_at
    )
    VALUES (
        rp_product_id,
        rp_current_stock,
        rp_predicted_reorder_point,
        rp_recommended_order_qty,
        rp_current_stock,
        NOW()
    );
END //

DELIMITER ;

    -- Profit Optimization
DELIMITER //

CREATE PROCEDURE add_profit_prediction (
    IN pp_product_id INT,
    IN pp_predicted_profit DECIMAL(14,2),
    IN pp_start DATETIME,
    IN pp_end DATETIME,
    IN pp_model VARCHAR(100)
)
BEGIN
    INSERT INTO profit_predictions (
        product_id,
        predicted_profit,
        period_start,
        period_end,
        model_name,
        generated_at
    )
    VALUES (
        pp_product_id,
        pp_predicted_profit,
        pp_start,
        pp_end,
        pp_model,
        NOW()
    );
END //

DELIMITER ;

    -- ROI and CAGR
DELIMITER //

CREATE PROCEDURE add_financial_prediction (
    IN fp_metric_type ENUM('ROI','CAGR'),
    IN fp_predicted_value DECIMAL(12,4),
    IN fp_period_start DATE,
    IN fp_period_end DATE,
    IN fp_model_name VARCHAR(100)
)
BEGIN
    INSERT INTO financial_predictions (
        metric_type,
        predicted_value,
        period_start,
        period_end,
        model_name,
        generated_at
    )
    VALUES (
        fp_metric_type,
        fp_predicted_value,
        fp_period_start,
        fp_period_end,
        fp_model_name,
        NOW()
    ) -- removed semicolon
    ON DUPLICATE KEY UPDATE
        predicted_value = VALUES(predicted_value),
        generated_at = NOW();
END //

DELIMITER ;

--      Add, edit, remove row in inventory_log (Should only be adjustment)
DROP PROCEDURE IF EXISTS add_inventory_log_entry; 

DELIMITER //

CREATE PROCEDURE add_inventory_log_entry (
    IN il_product_id INT,
    IN il_quantity SMALLINT,
    IN il_unit_cost DECIMAL(8, 2),
    IN reference_id BIGINT,
    IN il_reference_type ENUM('TRANSACTION', 'PURCHASE')
)
BEGIN
    -- Check if product exists
    SELECT product_id INTO temp_product_id
    FROM product_category
    WHERE product_id = il_product_id
    LIMIT 1;

    IF temp_product_id IS NULL THEN 
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid product_id';
    END IF;

    -- Proceed to insert
    INSERT INTO inventory_log (
        product_id,
        change_type,
        quantity,
        unit_cost,
        log_date,
        reference_id,
        reference_type
    )
    VALUES (
        il_product_id,
        'ADJUSTMENT',
        il_quantity,
        il_unit_cost,
        reference_id,
        il_reference_type
    );
END //

DELIMITER;

--      Add, edit, remove row in operational_costs
DROP PROCEDURE IF EXISTS add_operational_cost_entry; 

DELIMITER //

CREATE PROCEDURE add_operational_cost_entry (
    IN oc_cost_type ENUM('RENT', 'UTILITIES', 'WAGES', 'MAINTENANCE', 'OTHER'),
    IN oc_amount DECIMAL(12, 2),
    IN oc_notes VARCHAR(255)
)
BEGIN
    INSERT INTO operational_costs (
        cost_type,
        amount,
        cost_date,
        notes
    )
    VALUES (
        oc_cost_type,
        oc_amount,
        NOW(),
        oc_notes
    );
END //

DELIMITER;

--      Add, edit, remove row in investments
DROP PROCEDURE IF EXISTS add_investments_entry; 

DELIMITER //

CREATE PROCEDURE add_investments_entry (
    IN in_amount DECIMAL(12, 2),
    IN in_investment_date DATETIME,
    IN in_description VARCHAR(255)
)
BEGIN
    INSERT INTO investments (
        amount,
        investment_date,
        description
    )
    VALUES (
        in_amount,
        in_investment_date,
        in_description
    );
END //

DELIMITER;



--      Add, edit, remove row in purchase_orders & purchase_order_items
DROP PROCEDURE IF EXISTS add_purchase_order_entry; 

DELIMITER //

CREATE PROCEDURE add_purchase_order_entry (
    IN po_supplier_name VARCHAR(255),
    IN po_order_date DATETIME,
    IN po_total_cost DECIMAL(12, 2)
)
BEGIN
    -- Check if supplier id is valid
    DECLARE po_supplier_id SMALLINT;

    SELECT supplier_id INTO po_supplier_id
    FROM supplier
    WHERE LOWER(supplier_name) = LOWER(po_supplier_name)
    LIMIT 1;

    IF po_supplier_id IS NULL THEN 
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid po_supplier_id';
    END IF;

    INSERT INTO operational_costs (
        supplier_id,
        order_date,
        total_cost
    )
    VALUES (
        po_supplier_id,
        po_order_date,
        po_total_cost
    );

    
END //

DELIMITER;

--      Add, edit, remove row in product_batches
--          * Ensure to update inventory_log accordingly
--          * Barcode should be generated using python/java script
DROP PROCEDURE IF EXISTS add_product_batches_entry; 

DELIMITER //

CREATE PROCEDURE add_product_batches_entry (
    IN pb_product_name VARCHAR(255),
    IN pb_supplier_name VARCHAR(255),
    IN pb_quantity_received SMALLINT,
    IN pb_unit_cost DECIMAL(12, 2),
    IN pb_date_received DATETIME,
    IN pb_barcode VARCHAR(100)
)
BEGIN
    DECLARE pb_product_id INT;
    DECLARE pb_supplier_id SMALLINT;
    DECLARE pb_batch_id BIGINT;

    -- Validate product & supplier
    SELECT product_id INTO pb_product_id
    FROM product
    WHERE LOWER(product_name) = LOWER(pb_product_name)
    LIMIT 1;

    SELECT supplier_id INTO pb_supplier_id
    FROM supplier
    WHERE LOWER(supplier_name) = LOWER(pb_supplier_name)
    LIMIT 1;

    -- Check if product and supplier exists
    IF pb_product_id IS NULL THEN 
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid pb_product_id';
    END IF;

    IF pb_supplier_id IS NULL THEN 
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Invalid pb_supplier_id';
    END IF;

    INSERT INTO product_batches (
        product_id,
        supplier_id,
        quantity_received,
        unit_cost,
        date_received,
        barcode
    )
    VALUES (
        pb_product_id,
        pb_supplier_id,
        pb_quantity_received,
        pb_unit_cost,
        pb_date_received,
        pb_barcode  
    );

    -- Update Inventory Log
    SET pb_batch_id = LAST_INSERT_ID();

    INSERT INTO inventory_log (
        product_id,
        change_type,
        quantity,
        unit_cost,
        log_date,
        reference_id,
        reference_type
    )
    VALUES (
        pb_product_id,
        'IN',
        pb_quantity_received,
        pb_unit_cost,
        pb_date_received,
        pb_batch_id,
        'BATCH'
    );

    -- Update product stock level
    UPDATE product
    SET current_stock_level = current_stock_level + pb_quantity_received,
        last_update = NOW()
    WHERE product_id = pb_product_id;
    
END //

DELIMITER;

DROP PROCEDURE IF EXISTS edit_product_batches_entry; 
DELIMITER //

CREATE PROCEDURE edit_product_batches_entry (
    IN pb_batch_id BIGINT,
    IN pb_new_quantity SMALLINT,
    IN pb_new_unit_cost DECIMAL(12, 2),
    IN pb_new_date_received DATETIME,
    IN pb_new_barcode VARCHAR(100)
)
BEGIN
    DECLARE var_product_id INT;
    DECLARE var_old_quantity SMALLINT;
    DECLARE var_old_unit_cost DECIMAL(12,2);

    -- Check if entry exists and is valid
    SELECT 
        product_id,
        quantity_received,
        unit_cost
    INTO 
        v_product_id,
        v_old_quantity,
        v_old_unit_cost
    FROM product_batches
    WHERE batch_id = pb_batch_id;

    IF v_product_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Batch not found';
    END IF;

    -- Remove old data
    UPDATE product
    SET current_stock_level = current_stock_level - v_old_quantity,
        last_update = NOW()
    WHERE product_id = v_product_id;

    -- Update tables
    UPDATE product_batches
    SET 
        quantity_received = pb_new_quantity,
        unit_cost = pb_new_unit_cost,
        date_received = pb_new_date_received,
        barcode = pb_new_barcode
    WHERE batch_id = pb_batch_id;

    UPDATE product
    SET current_stock_level = current_stock_level + pb_new_quantity,
        last_update = NOW()
    WHERE product_id = v_product_id;

    INSERT INTO inventory_log (
        product_id,
        change_type,
        quantity,
        unit_cost,
        log_date,
        reference_id,
        reference_type
    )
    VALUES (
        v_product_id,
        'ADJUSTMENT',
        pb_new_quantity - v_old_quantity,
        pb_new_unit_cost,
        NOW(),
        pb_batch_id,
        'BATCH'
    );

END //

DELIMITER ;

DROP PROCEDURE IF EXISTS remove_product_batch_entry;
DELIMITER //

CREATE PROCEDURE remove_product_batch_entry(
    IN pb_batch_id BIGINT
)
BEGIN
    DECLARE v_product_id INT;
    DECLARE v_quantity SMALLINT;
    DECLARE v_remaining_stock SMALLINT;

    -- Fetch and validate entry
    SELECT 
        product_id,
        quantity_received
    INTO 
        v_product_id,
        v_quantity
    FROM product_batches
    WHERE batch_id = pb_batch_id;

    IF v_product_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Batch not found';
    END IF;

    -- Cancel if used in POS
    IF EXISTS (
        SELECT 1
        FROM transaction_items
        WHERE batch_id = pb_batch_id
        LIMIT 1
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot delete batch: already used in sales';
    END IF;

    -- Prevent negative stock
    SELECT current_stock_level INTO v_remaining_stock
    FROM product
    WHERE product_id = v_product_id;

    IF v_remaining_stock < v_quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock inconsistency detected';
    END IF;

    -- Remove entry across tables
    UPDATE product
    SET current_stock_level = current_stock_level - v_quantity,
        last_update = NOW()
    WHERE product_id = v_product_id;

    DELETE FROM inventory_log
    WHERE reference_id = pb_batch_id
      AND reference_type = 'BATCH';

    DELETE FROM product_batches
    WHERE batch_id = pb_batch_id;

END //

DELIMITER //

-- Miscellaneous Procedures for extraneous features that can be used in any module
--      Autocomplete query (for search bar or edit fields)
--      NOTE: This is a template only
DROP PROCEDURE IF EXISTS autocomplete_query; 

DELIMITER //

CREATE PROCEDURE autocomplete_query
    IN param1 datatype,
    IN param2 datatype
BEGIN
    SELECT column1, column2
    FROM table_name
    WHERE column1 LIKE CONCAT('%', ?, '%')
    LIMIT 10;
END //

DELIMITER;

-- Search for products
DROP PROCEDURE IF EXISTS search_products; 

DELIMITER //

CREATE PROCEDURE search_products (
    IN p_search VARCHAR(255),
    IN p_category_id SMALLINT,
    IN p_manufacturer VARCHAR(255),
    IN p_year INT,
    IN p_sort VARCHAR(50)   -- 'name', 'price', 'stock', 'newest', 'most_purchased'
)
BEGIN
    SELECT 
        p.product_id,
        p.product_name,
        p.product_description,
        p.current_stock_level,
        p.unit_cost,

        pc.category_name,
        s.supplier_name,

        IFNULL(SUM(ti.quantity_sold), 0) AS total_sold

    FROM product p
    LEFT JOIN product_category pc 
        ON p.category_id = pc.category_id
    LEFT JOIN supplier s 
        ON p.supplier_id = s.supplier_id
    LEFT JOIN transaction_items ti 
        ON p.product_id = ti.product_id
    LEFT JOIN compatibility c 
        ON p.product_id = c.product_id
    LEFT JOIN vehicles v 
        ON c.vehicle_id = v.vehicle_id

    WHERE
        -- Search name or description
        (
            p_search IS NULL OR
            p.product_name LIKE CONCAT('%', p_search, '%') OR
            p.product_description LIKE CONCAT('%', p_search, '%')
        )

        -- Category filter
        AND (
            p_category_id IS NULL OR
            p.category_id = p_category_id
        )

        -- Manufacturer filter
        AND (
            p_manufacturer IS NULL OR
            v.manufacturer_name LIKE CONCAT('%', p_manufacturer, '%')
        )

        -- Year range filter
        AND (
            p_year IS NULL OR
            c.product_id IS NULL OR
            (p_year BETWEEN c.bottom_year AND c.top_year)
        )

    GROUP BY p.product_id

    ORDER BY 
        CASE WHEN p_sort = 'name' THEN p.product_name END ASC,
        CASE WHEN p_sort = 'price' THEN p.unit_cost END ASC,
        CASE WHEN p_sort = 'stock' THEN p.current_stock_level END ASC,
        CASE WHEN p_sort = 'newest' THEN p.date_added END DESC,
        CASE WHEN p_sort = 'most_purchased' THEN total_sold END DESC;

END //

DELIMITER ;