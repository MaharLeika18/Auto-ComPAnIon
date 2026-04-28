USE autocomp;

ALTER TABLE manufacturers DROP INDEX manufacturer_name;
ALTER TABLE transaction_log MODIFY receipt_num VARCHAR(50);
ALTER TABLE transaction_log MODIFY payment_method VARCHAR(50);
ALTER TABLE transaction_log MODIFY status VARCHAR(50);
ALTER TABLE transaction_log MODIFY transaction_date DATETIME NULL;
ALTER TABLE transaction_items MODIFY discount_applied DECIMAL(10,2);

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================
-- TRANSACTION / SALES LAYER
-- =====================================
TRUNCATE TABLE transaction_items;
TRUNCATE TABLE transaction_log;
TRUNCATE TABLE pending_transaction_items;
TRUNCATE TABLE pending_transactions_log;

-- =====================================
-- INVENTORY / LOGGING
-- =====================================
TRUNCATE TABLE inventory_log;
TRUNCATE TABLE product_batches;

-- =====================================
-- PROCUREMENT
-- =====================================
TRUNCATE TABLE purchase_order_items;
TRUNCATE TABLE purchase_orders;

-- =====================================
-- PRODUCT CORE
-- =====================================
TRUNCATE TABLE compatibility;
TRUNCATE TABLE product;
TRUNCATE TABLE product_images;

-- =====================================
-- REFERENCE TABLES
-- =====================================
TRUNCATE TABLE vehicles;
TRUNCATE TABLE supplier;
TRUNCATE TABLE manufacturers;
TRUNCATE TABLE product_category;

-- =====================================
-- FINANCIAL / AI MODELS (OPTIONAL RESET)
-- =====================================
TRUNCATE TABLE operational_costs;
TRUNCATE TABLE investments;
TRUNCATE TABLE demand_forecasts;
TRUNCATE TABLE ebit_predictions;
TRUNCATE TABLE financial_predictions;
TRUNCATE TABLE profit_predictions;
TRUNCATE TABLE reorder_predictions;
TRUNCATE TABLE roi_break_even_predictions;

-- =====================================
-- USERS (OPTIONAL - only if needed)
-- =====================================
-- TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'AUTO COMP RESET COMPLETE' AS status;