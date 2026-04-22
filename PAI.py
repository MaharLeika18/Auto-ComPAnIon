import pandas as pd
import numpy as np
from datetime import timedelta
import mysql.connector                              # pip install mysql-connector-python 
from statsmodels.tsa.arima.model import ARIMA       # pip install stasmodels  # pip install scikit-learn
from xgboost import XGBRegressor                    # pip install xgboost 

import matplotlib.pyplot as plt                     # pip install matplotlib
import seaborn as sb                                # pip install seaborn


conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="Caine",
    database="Auto-CompAnIon"
)

def generate_barcode(product_id, supplier_id, batch_id):
    return f"{product_id:03d}-{supplier_id:02d}-{batch_id:05d}"

#           Predictive AI Functions
# ROI
def fetch_dataset_financials(start_date, end_date):
    cursor = conn.cursor()
    cursor.callproc('dataset_financials', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    roi_df = pd.DataFrame(data, columns=columns)
    return roi_df

def calculate_roi(roi_df):
    # Calculate

    return pd.DataFrame(roi)

def save_roi_prediction(roi_df):
    cursor = conn.cursor()

    data = [
        (
            "ROI",
            int(row['predicted_value']),
            row['period_start'],
            row['fp_period_end'],
            "ARIMA_v1"  # TODO: Update this
        )
        for _, row in roi_df.iterrows()
    ]

    cursor.executemany("""
        CALL add_financial_prediction(%s, %s, %s, %s, %s)
    """, data)

    conn.commit()

# CAGR
def fetch_dataset_yearly_revenue(start_date, end_date):
    cursor = conn.cursor()
    cursor.callproc('dataset_yearly_revenue')

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    cagr_df = pd.DataFrame(data, columns=columns)
    return cagr_df

def calculate_cagr(cagr_df):
    # Calculate

    return pd.DataFrame(cagr)

def save_cagr_prediction(cagr_df):
    cursor = conn.cursor()

    data = [
        (
            "CAGR",
            int(row['predicted_value']),
            row['period_start'],
            row['fp_period_end'],
            "ARIMA_v1"  # TODO: Update this
        )
        for _, row in cagr_df.iterrows()
    ]

    cursor.executemany("""
        CALL add_financial_prediction(%s, %s, %s, %s, %s)
    """, data)

    conn.commit()

# Demand Forecasting
def fetch_sales_data(start_date, end_date):
    cursor = conn.cursor()
    cursor.callproc('dataset_sales_timeseries', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    demand_df = pd.DataFrame(data, columns=columns)
    return demand_df

def forecast_demand(df, forecast_days=7):
    forecasts = []

    df['sale_date'] = pd.to_datetime(df['sale_date'])

    # Group by product
    for product_id in df['product_id'].unique():
        product_df = df[df['product_id'] == product_id]

        product_df = product_df.set_index('sale_date').sort_index()

        # Fill missing dates 
        product_df = product_df.asfreq('D', fill_value=0)

        series = product_df['total_quantity']

        # Skip if too little data
        if len(series) < 20:
            continue

        try:
            # Train ARIMA model
            model = ARIMA(series, order=(5,1,0))
            model_fit = model.fit()

            forecast = model_fit.forecast(steps=forecast_days)

            # Generate future dates
            last_date = series.index[-1]
            future_dates = [last_date + timedelta(days=i+1) for i in range(forecast_days)]

            for date, value in zip(future_dates, forecast):
                forecasts.append({
                    "product_id": product_id,
                    "forecast_date": date,
                    "predicted_demand": max(0, float(value))  # prevent negative
                })

        # Catch error
        except Exception as e:
            print(f"Error forecasting product {product_id}: {e}")

    return pd.DataFrame(forecasts)

def save_forecasts(df_forecasts):
    cursor = conn.cursor()

    data = [
        (
            int(row['product_id']),
            row['forecast_date'],
            float(row['predicted_demand']),
            "ARIMA_v1"
        )
        for _, row in df_forecasts.iterrows()
    ]

    cursor.executemany("""
        CALL add_demand_forecast(%s, %s, %s, %s)
    """, data)

    conn.commit()

# Reorder Prediction
def fetch_dataset_inventory_features(days):
    cursor = conn.cursor()
    cursor.callproc('dataset_inventory_features', days)

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    reorder_df = pd.DataFrame(data, columns=columns)
    return reorder_df

def predict_reorder_point(reorder_df):
    # Calculate

    return pd.DataFrame(reorder_point)

def save_reorder_prediction(reorder_df):
    cursor = conn.cursor()

    data = [
        (
            int(row['product_id']),
            int(row['predicted_reorder_point']),
            int(row['recommended_order_qty']),
            "CAGR", #TODO: Figure out enum
        )
        for _, row in reorder_df.iterrows()
    ]

    cursor.executemany("""
        CALL add_reorder_prediction(%s, %s, %s, %s)
    """, data)

    conn.commit()

# Profit Optimization
def fetch_dataset_profit_analysis(start_date, end_date):
    cursor = conn.cursor()
    cursor.callproc('dataset_profit_analysis', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    profit_df = pd.DataFrame(data, columns=columns)
    return profit_df

def predict_reorder_point(profit_df):
    # Calculate

    return pd.DataFrame(profit)

def save_profit_prediction(profit_df):
    cursor = conn.cursor()

    data = [
        (
            int(row['product_id']),
            int(row['predicted_profit']),
            int(row['suggested_price']),
            int(row['confidence_score'])
        )
        for _, row in profit_df.iterrows()
    ]

    cursor.executemany("""
        CALL add_profit_prediction(%s, %s, %s, %s)
    """, data)

    conn.commit()

# Main Execution (for testing)
if __name__ == "__main__":
    # testing vars
    start_date = "2025-01-01"  
    end_date = "2025-12-31"

    df = fetch_sales_data(start_date, end_date)
    print("Data loaded:", df.shape)

    forecast_df = forecast_demand(df, forecast_days=7)
    print("Forecast generated:", forecast_df.shape)

    save_forecasts(forecast_df)

    print("Forecasts saved successfully!")