import pandas as pd
import mysql.connector

conn = mysql.connector.connect(...)

df = pd.read_sql("CALL dataset_sales_timeseries('2024-01-01','2025-01-01')", conn)

def generate_barcode(product_id, supplier_id, batch_id):
    return f"{product_id:03d}-{supplier_id:02d}-{batch_id:05d}"