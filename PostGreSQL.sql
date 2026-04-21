-- Tables for Inventory Management Module
DROP TABLE IF EXISTS product;
CREATE TABLE product(
    product_id INT  NOT NULL  PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_description VARCHAR(255) NULL,
    part_number VARCHAR(200) NULL,
    category_id SMALLINT NOT NULL,
    supplier_id SMALLINT NOT NULL,
    current_stock_level SMALLINT NOT NULL,
    storage_location VARCHAR(255) NULL,
    date_added TIMESTAMP NOT NULL,
    last_update TIMESTAMP NOT NULL,
    unit_cost DECIMAL(12, 2) NOT NULL COMMENT 'Latest supplier price; regularly update this.',
    retail_price DECIMAL(12, 2) NOT NULL COMMENT 'Latest selling price; regularly update this.'
);

DROP TABLE IF EXISTS product_category;
CREATE TABLE product_category(
    category_id SMALLINT  NOT NULL  PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    parent_category_id SMALLINT NOT NULL,
    date_added TIMESTAMP NOT NULL,
    last_update TIMESTAMP NOT NULL
);
ALTER TABLE 
    product_category ADD UNIQUE (category_name);

DROP TABLE IF EXISTS product_images;
CREATE TABLE product_images (
    image_id INT  NOT NULL  PRIMARY KEY,
    product_id INT NOT NULL,
    image_path VARCHAR(255) NULL,
    date_uploaded TIMESTAMP NOT NULL
);
ALTER TABLE 
    product_images ADD UNIQUE (image_path);

DROP TABLE IF EXISTS supplier;
CREATE TABLE supplier(
    supplier_id SMALLINT  NOT NULL  PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    supplier_address VARCHAR(255) NULL,
    supplier_contact BIGINT NULL,
    date_added TIMESTAMP NOT NULL,
    last_update TIMESTAMP NOT NULL
);
ALTER TABLE 
    supplier ADD UNIQUE (supplier_name);

DROP TABLE IF EXISTS compatibility;
CREATE TABLE compatibility(
    product_id BIGINT  NOT NULL,
    bottom_year SMALLINT NOT NULL,
    top_year SMALLINT NOT NULL,
    vehicle_id SMALLINT NOT NULL
);
ALTER TABLE
    compatibility ADD INDEX compatibility_product_id_index(product_id);
ALTER TABLE
    compatibility ADD INDEX compatibility_vehicle_id_index(vehicle_id);

DROP TABLE IF EXISTS vehicles;
CREATE TABLE vehicles(
    vehicle_id SMALLINT  NOT NULL  PRIMARY KEY,
    model_name VARCHAR(255) NOT NULL,
    manufacturer_name VARCHAR(255) NOT NULL
);
ALTER TABLE
    vehicles ADD INDEX vehicles_manufacturer_name_index(manufacturer_name);

-- Tables for Point of Sale Module
DROP TABLE IF EXISTS transaction_log;
CREATE TABLE transaction_log(
    transaction_id BIGINT  NOT NULL  PRIMARY KEY,
    transaction_date TIMESTAMP NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    notes VARCHAR(255) NULL
);
ALTER TABLE
    transaction_log ADD INDEX transaction_log_transaction_date_index(transaction_date);

DROP TABLE IF EXISTS transaction_items;
CREATE TABLE transaction_items(
    item_id BIGINT  NOT NULL  PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    product_id INT NOT NULL,
    batch_id BIGINT  NOT NULL,
    quantity_sold SMALLINT NOT NULL,
    unit_selling_price DECIMAL(12, 2) NOT NULL COMMENT 'Retail price per unit at the moment of sale',
    unit_cost_at_sale DECIMAL(12, 2) NOT NULL COMMENT 'Cost per unit from supplier at the moment of sale',
    discount_applied DECIMAL(12, 2) NOT NULL,
    total_sale_value DECIMAL(12, 2) NOT NULL COMMENT 'Total revenue: (quantity_sold * unit_selling_price) - (quantity_sold * discount_applied)',
    total_cost DECIMAL(12, 2) NOT NULL COMMENT 'Total cost of goods sold (COGS): quantity_sold * unit_cost_at_sale'
);
ALTER TABLE transaction_items
    ADD FOREIGN KEY (batch_id) REFERENCES product_batches(batch_id);

DROP TABLE IF EXISTS pending_transactions_log;
CREATE TABLE pending_transactions_log(
    pending_transaction_id BIGINT  NOT NULL  PRIMARY KEY,
    creation_date TIMESTAMP NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    payment_method ENUM('CASH', 'E-WALLET', 'BANK') NULL,
    notes VARCHAR(255) NULL
);
ALTER TABLE
    pending_transactions_log ADD INDEX pending_transactions_log_creation_date_index(creation_date);

DROP TABLE IF EXISTS pending_transaction_items;
CREATE TABLE pending_transaction_items(
    pending_item_id BIGINT  NOT NULL  PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    product_id INT NOT NULL,
    batch_id BIGINT NOT NULL,
    quantity_sold SMALLINT NOT NULL,
    unit_selling_price DECIMAL(12, 2) NOT NULL COMMENT 'Retail price per unit at the moment of sale',
    unit_cost_at_sale DECIMAL(12, 2) NOT NULL COMMENT 'Cost per unit from supplier at the moment of sale',
    discount_applied DECIMAL(12, 2) NOT NULL,
    total_sale_value DECIMAL(12, 2) NOT NULL COMMENT 'Total revenue: (quantity_sold * unit_selling_price) - (quantity_sold * discount_applied)',
    total_cost DECIMAL(12, 2) NOT NULL COMMENT 'Total cost of goods sold (COGS): quantity_sold * unit_cost_at_sale'
);
ALTER TABLE 
    pending_transaction_items ADD UNIQUE (transaction_id, product_id, batch_id);
ALTER TABLE
    pending_transaction_items ADD INDEX idx_pending_tx(transaction_id);

-- Tables for Predictive AI Module
DROP TABLE IF EXISTS operational_costs;
CREATE TABLE operational_costs(
    cost_id BIGINT  NOT NULL  PRIMARY KEY,
    cost_type ENUM(
        'RENT',
        'UTILITIES',
        'WAGES',
        'MAINTENANCE',
        'MARKETING',
        'OTHER'
    ) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    cost_date TIMESTAMP NOT NULL,
    notes VARCHAR(255) NULL
);
ALTER TABLE
    operational_costs ADD INDEX operational_costs_cost_date_index(cost_date);
ALTER TABLE
    operational_costs COMMENT = 'For calculating total_operating_costs & total_costs (COGS + operating_costs)';

DROP TABLE IF EXISTS inventory_log;
CREATE TABLE inventory_log(
    log_id BIGINT  NOT NULL  PRIMARY KEY,
    product_id INT NOT NULL,
    change_type ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
    quantity SMALLINT NOT NULL,
    unit_cost DECIMAL(8, 2) NOT NULL,
    log_date TIMESTAMP NOT NULL,
    reference_id BIGINT NULL COMMENT 'Links to transaction id/batch id',
    reference_type ENUM('TRANSACTION', 'PURCHASE') NULL
);
ALTER TABLE
    inventory_log ADD INDEX inventory_log_log_date_index(log_date);

DROP TABLE IF EXISTS investments;
CREATE TABLE investments(
    investment_id BIGINT  NOT NULL  PRIMARY KEY,
    amount DECIMAL(12, 2) NOT NULL,
    investment_date TIMESTAMP NOT NULL,
    description VARCHAR(255) NULL
);

DROP TABLE IF EXISTS purchase_orders;
CREATE TABLE purchase_orders(
    po_id BIGINT  NOT NULL  PRIMARY KEY,
    supplier_id SMALLINT NOT NULL,
    order_date TIMESTAMP NOT NULL,
    total_cost DECIMAL(12, 2) NOT NULL
);

DROP TABLE IF EXISTS purchase_order_items;
CREATE TABLE purchase_order_items(
    id BIGINT  NOT NULL  PRIMARY KEY,
    po_id BIGINT NOT NULL,
    product_id INT NOT NULL,
    quantity SMALLINT NOT NULL,
    unit_cost DECIMAL(12, 2) NOT NULL
);

DROP TABLE IF EXISTS product_batches;
CREATE TABLE product_batches(
    batch_id BIGINT  NOT NULL  PRIMARY KEY,
    product_id INT NOT NULL,
    supplier_id BIGINT NOT NULL,
    quantity_received SMALLINT NOT NULL,
    unit_cost DECIMAL(12, 2) NOT NULL,
    date_received TIMESTAMP NOT NULL,
    barcode VARCHAR(100) NOT NULL UNIQUE 
);

DROP TABLE IF EXISTS demand_forecasts;
CREATE TABLE demand_forecasts (
    forecast_id BIGINT  PRIMARY KEY,
    product_id INT NOT NULL,
    forecast_date DATE NOT NULL COMMENT 'Date being predicted',    
    predicted_demand DECIMAL(12,2) NOT NULL,
    model_name VARCHAR(100) COMMENT 'e.g. ARIMA, LSTM',    
    model_version VARCHAR(50),
    generated_at TIMESTAMP NOT NULL COMMENT 'when prediction was made',

    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

DROP TABLE IF EXISTS reorder_predictions;
CREATE TABLE reorder_predictions (
    prediction_id BIGINT  PRIMARY KEY,
    product_id INT NOT NULL,
    predicted_reorder_point DECIMAL(12,2),
    recommended_order_qty DECIMAL(12,2),
    risk_level ENUM('LOW','MEDIUM','HIGH'),
    generated_at TIMESTAMP NOT NULL,

    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

DROP TABLE IF EXISTS profit_predictions;
CREATE TABLE profit_predictions (
    prediction_id BIGINT  PRIMARY KEY,
    product_id INT NOT NULL,
    predicted_profit DECIMAL(12,2),
    suggested_price DECIMAL(12,2),
    confidence_score DECIMAL(5,2),
    generated_at TIMESTAMP NOT NULL,

    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

DROP TABLE IF EXISTS financial_predictions;
CREATE TABLE financial_predictions (
    prediction_id BIGINT  PRIMARY KEY,
    metric_type ENUM('ROI','CAGR'),
    predicted_value DECIMAL(12,4),
    period_start DATE,
    period_end DATE,
    model_name VARCHAR(100),
    generated_at TIMESTAMP NOT NULL
);
