import pandas as pd
import numpy as np
from datetime import timedelta
from datetime import datetime
import mysql.connector                              # pip install mysql-connector-python 
from statsmodels.tsa.arima.model import ARIMA       # pip install stasmodels  
from xgboost import XGBRegressor                    # pip install xgboost 
from sklearn.ensemble import RandomForestRegressor  # pip install scikit-learn
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.model_selection import train_test_split

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
# ROI (Calculated)
def fetch_calculated_roi(start_date, end_date):
    cursor = conn.cursor()

    cursor.callproc('calculate_roi', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    return pd.DataFrame(data, columns=columns)

def save_roi_calculation(roi_df):
    cursor = conn.cursor()

    data = [
        (
            "ROI",
            float(row['roi']),
            row['period_start'],
            row['period_end'],
            "CALCULATED_v1"
        )
        for _, row in roi_df.iterrows()
    ]

    cursor.executemany("""
        CALL add_financial_prediction(%s, %s, %s, %s, %s)
    """, data)

    conn.commit()

# ROI (Predicted)
def fetch_roi_dataset(start_date, end_date):
    cursor = conn.cursor()

    cursor.callproc('dataset_roi_timeseries', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    df = pd.DataFrame(data, columns=columns)
    return df

def preprocess_roi(df):
    df['period_date'] = pd.to_datetime(df['period_date'])
    df = df.sort_values('period_date')

    df = df.set_index('period_date')

    # Fill missing dates
    df = df.asfreq('D')

    # Fill missing ROI
    df['roi'] = df['roi'].fillna(method='fill').fillna(0)

    return df

def train_roi_model(df, forecast_days=7):
    series = df['roi']

    if len(series) < 20:
        raise ValueError("Not enough data for ROI forecasting")

    model = ARIMA(series, order=(3,1,1))
    model_fit = model.fit()

    forecast = model_fit.forecast(steps=forecast_days)

    last_date = df.index[-1]
    future_dates = [last_date + timedelta(days=i+1) for i in range(forecast_days)]

    result = []

    for date, value in zip(future_dates, forecast):
        result.append({
            "forecast_date": date,
            "predicted_roi": float(value)
        })

    return pd.DataFrame(result)

def save_roi_predictions(forecast_df, period_start, period_end):
    cursor = conn.cursor()

    data = [
        (
            "ROI",
            float(row['predicted_roi']),
            period_start,
            period_end,
            "ARIMA_v1"
        )
        for _, row in forecast_df.iterrows()
    ]

    cursor.executemany("""
        CALL add_financial_prediction(%s, %s, %s, %s, %s)
    """, data)

    conn.commit()

# ROI Prediction (When will the user see a return on investment)


# CAGR (Calculated)
def fetch_calculated_cagr(start_date, end_date):
    cursor = conn.cursor()

    cursor.callproc('calculate_cagr', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    return pd.DataFrame(data, columns=columns)

def save_cagr_calculated(cagr_df):
    cursor = conn.cursor()

    data = [
        (
            "CAGR",
            float(row['cagr']),
            row['period_start'],
            row['period_end'],
            "CALCULATED_v1"
        )
        for _, row in cagr_df.iterrows()
        if row['cagr'] is not None
    ]

    cursor.executemany("""
        CALL add_financial_prediction(%s, %s, %s, %s, %s)
    """, data)

    conn.commit()

# CAGR (Predicted)
def fetch_cagr_dataset(start_date, end_date):
    cursor = conn.cursor()

    cursor.callproc('dataset_cagr_timeseries', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    return pd.DataFrame(data, columns=columns)

def preprocess_cagr(df):
    df['period_month'] = pd.to_datetime(df['period_month'])
    df = df.sort_values('period_month')

    df = df.set_index('period_month')

    # Monthly frequency
    df = df.asfreq('MS')

    # Fill missing CAGR
    df['cagr'] = df['cagr'].fillna(method='ffill').fillna(0)

    return df

def train_cagr_model(df, forecast_months=3):
    series = df['cagr']

    if len(series) < 12:
        raise ValueError("Need at least 12 months of data for CAGR forecasting")

    model = ARIMA(series, order=(2,1,1))
    model_fit = model.fit()

    forecast = model_fit.forecast(steps=forecast_months)

    last_date = df.index[-1]

    future_dates = [
        last_date + pd.DateOffset(months=i+1)
        for i in range(forecast_months)
    ]

    result = []

    for date, value in zip(future_dates, forecast):
        result.append({
            "forecast_date": date,
            "predicted_cagr": float(value)
        })

    return pd.DataFrame(result)

def save_cagr_prediction(forecast_df, period_start, period_end):
    cursor = conn.cursor()

    data = [
        (
            "CAGR",
            float(row['predicted_cagr']),
            period_start,
            period_end,
            "ARIMA_v1"
        )
        for _, row in forecast_df.iterrows()
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
def fetch_reorder_dataset(start_date, end_date):
    cursor = conn.cursor()

    cursor.callproc('dataset_reorder', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    return pd.DataFrame(data, columns=columns)

def preprocess_reorder(df):
    df['sale_date'] = pd.to_datetime(df['sale_date'])

    results = []

    for product_id in df['product_id'].unique():
        p_df = df[df['product_id'] == product_id].copy()

        p_df = p_df.set_index('sale_date').sort_index()

        # fill missing days
        p_df = p_df.asfreq('D', fill_value=0)

        demand = p_df['daily_demand']

        avg_demand = demand.mean()
        std_demand = demand.std()

        current_stock = p_df['current_stock_level'].iloc[-1]
        supplier_id = p_df['supplier_id'].iloc[-1]
        avg_cost = p_df['avg_cost'].iloc[-1]

        results.append({
            "product_id": product_id,
            "supplier_id": supplier_id,
            "avg_daily_demand": avg_demand,
            "std_demand": std_demand,
            "current_stock": current_stock,
            "avg_cost": avg_cost
        })

    return pd.DataFrame(results)

def fetch_forecasts():
    cursor = conn.cursor()

    cursor.callproc('get_demand_forecasts')

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    df = pd.DataFrame(data, columns=columns)
    df['forecast_date'] = pd.to_datetime(df['forecast_date'])

    return df

def build_forecast_lookup(forecast_df):
    forecast_lookup = {}

    for product_id in forecast_df['product_id'].unique():
        p_df = forecast_df[forecast_df['product_id'] == product_id]
        p_df = p_df.sort_values('forecast_date')

        forecast_lookup[product_id] = p_df['predicted_demand'].values

    return forecast_lookup

def fetch_lead_time_dataset():
    cursor = conn.cursor()

    cursor.callproc('dataset_lead_time')

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    return pd.DataFrame(data, columns=columns)

def preprocess_lead_time(df):
    df['order_date'] = pd.to_datetime(df['order_date'])

    df['month'] = df['order_date'].dt.month

    X = df[['supplier_id', 'product_id', 'quantity', 'unit_cost', 'month']]
    y = df['lead_time_days']

    return X, y

def train_lead_time_model(X, y):
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42
    )

    model = RandomForestRegressor(n_estimators=100)
    model.fit(X_train, y_train)

    return model

def predict_lead_time(model, product_id, supplier_id, quantity, unit_cost):

    month = datetime.now().month

    input_df = pd.DataFrame([{
        "supplier_id": supplier_id,
        "product_id": product_id,
        "quantity": quantity,
        "unit_cost": unit_cost,
        "month": month
    }])

    prediction = model.predict(input_df)[0]

    return max(1, float(prediction))  # at least 1 day

def predict_reorder(df_features, lead_time_model, forecast_lookup):
    # Reorder Point = (avg_demand × lead_time) + safety_stock
    # Safety Stock = z × std_demand × sqrt(lead_time)

    results = []

    for _, row in df_features.iterrows():
        product_id = row['product_id']
        supplier_id = row['supplier_id']

        avg_demand = row['avg_daily_demand']
        std_demand = row['std_demand']
        current_stock = row['current_stock']
        unit_cost = row['avg_cost']

        assumed_quantity = max(10, avg_demand * 7)  # Assumed, can be improved in future

        lead_time_days = predict_lead_time(
            lead_time_model,
            product_id=product_id,
            supplier_id=supplier_id,
            quantity=assumed_quantity,
            unit_cost=unit_cost
        )

        # Fetch forecasted demand
        if product_id in forecast_lookup:
            future_demand = forecast_lookup[product_id]

            # Ensure enough forecast horizon
            demand_window = future_demand[:lead_time_days]

            # Fallback if forecast too short
            if len(demand_window) < lead_time_days:
                missing_days = lead_time_days - len(demand_window)
                demand_window = np.append(
                    demand_window,
                    [avg_demand] * missing_days
                )

            expected_demand = np.sum(demand_window)

        else:
            # fallback if no forecast exists
            expected_demand = avg_demand * lead_time_days

        service_level = 1.65  # 95%
        safety_stock = service_level * std_demand * np.sqrt(lead_time_days)

        reorder_point = (expected_demand * lead_time_days) + safety_stock

        reorder_quantity = max(0, reorder_point - current_stock)

        results.append({
            "product_id": product_id,
            "predicted_lead_time": float(lead_time_days),
            "reorder_point": float(reorder_point),
            "reorder_quantity": float(reorder_quantity),
            "current_stock": float(current_stock)
        })

    return pd.DataFrame(results)

def save_reorder_predictions(df_reorder):
    cursor = conn.cursor()

    data = [
        (
            int(row['product_id']),
            float(row['current_stock']),
            float(row['reorder_point']),
            float(row['reorder_quantity']),
            "RandomForestRegressor"
        )
        for _, row in df_reorder.iterrows()
    ]

    cursor.executemany("""
        CALL add_reorder_prediction(%s, %s, %s, %s, %s)
    """, data)

    conn.commit()

# Profit Optimization
def fetch_dataset_profit_analysis(start_date, end_date):
    cursor = conn.cursor()
    cursor.callproc('dataset_profit_analysis', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    df = pd.DataFrame(data, columns=columns)
    df['sale_date'] = pd.to_datetime(df['sale_date'])

    return df

def preprocess_profit_analysis(df):
    df = df.sort_values(['product_id', 'sale_date'])

    df['prev_profit'] = df.groupby('product_id')['profit'].shift(1)
    df['prev_quantity'] = df.groupby('product_id')['total_quantity'].shift(1)

    df['month'] = df['sale_date'].dt.month
    df['day_of_week'] = df['sale_date'].dt.dayofweek

    df = df.dropna()

    features = [
        'product_id',
        'prev_profit',
        'prev_quantity',
        'avg_price',
        'avg_cost',
        'month',
        'day_of_week'
    ]

    X = df[features]
    y = df['profit']

    return X, y

def train_profit_model(X, y):
    model = GradientBoostingRegressor()
    model.fit(X, y)
    
    return model

def predict_future_profit(model, df, days=30):
    results = []

    last_rows = df.sort_values('sale_date').groupby('product_id').tail(1)

    for _, row in last_rows.iterrows():
        for i in range(days):
            input_data = pd.DataFrame([{
                "product_id": row['product_id'],
                "prev_profit": row['profit'],
                "prev_quantity": row['total_quantity'],
                "avg_price": row['avg_price'],
                "avg_cost": row['avg_cost'],
                "month": (row['sale_date'] + pd.Timedelta(days=i)).month,
                "day_of_week": (row['sale_date'] + pd.Timedelta(days=i)).dayofweek
            }])

            pred = model.predict(input_data)[0]

            results.append({
                "product_id": row['product_id'],
                "predicted_profit": float(pred)
            })

    return pd.DataFrame(results)

def save_profit_prediction(profit_df):
    cursor = conn.cursor()

    data = [
        (
            int(row['product_id']),
            int(row['predicted_profit']),
            row['period_start'],
            row['period_end'],
            "GradientBoostingRegressor"
        )
        for _, row in profit_df.iterrows()
    ]

    cursor.executemany("""
        CALL add_profit_prediction(%s, %s, %s, %s)
    """, data)

    conn.commit()

def build_profit_pareto(df):
    df = df.sort_values('total_profit', ascending=False)

    total_profit_sum = df['total_profit'].sum()

    df['profit_pct'] = df['total_profit'] / total_profit_sum * 100

    df['cumulative_pct'] = df['profit_pct'].cumsum()

    # Pareto classification
    df['pareto_class'] = df['cumulative_pct'].apply(
        lambda x: 'A' if x <= 80 else ('B' if x <= 95 else 'C')
    )

    return df

def aggregate_predicted_profit(df_pred):
    return df_pred.groupby('product_id')['predicted_profit'].sum().reset_index()

# EBITDA (Calculated) KPI


# EBITDA (Predicted) Timeseries

# EBITDA Expense vs Efficiency 

# Net Profit (Calculated) KPI

# Net Profit (Predicted) Timeseries

# Net Profit Impact Factors (by product category)

# Gross Profit (Calculated)
    # KPI

    # Predicted

    # Profit Contributors


# Main Execution (for testing)
if __name__ == "__main__":
    # NOTE: These are all example usages for both testing and reference 
    start_date = "2025-01-01"
    end_date = "2026-04-20"

    # ROI
    df = fetch_roi_dataset(start_date, end_date)
    df = preprocess_roi(df)

    forecast_df = train_roi_model(df, forecast_days=7)

    save_roi_predictions(forecast_df, start_date, end_date)

    # CAGR
    df = fetch_cagr_dataset(start_date, end_date)
    df = preprocess_cagr(df)

    forecast_df = train_cagr_model(df)

    save_cagr_prediction(forecast_df, start_date, end_date)

    # Demand Forecasting
    df = fetch_sales_data(start_date, end_date)

    forecast_df = forecast_demand(df, forecast_days=7)

    save_forecasts(forecast_df)

    # Reorder Predictions
    df = fetch_reorder_dataset(start_date, end_date)
    features_df = preprocess_reorder(df)

    forecast_df = fetch_forecasts()
    forecast_lookup = build_forecast_lookup(forecast_df)

    lead_df = fetch_lead_time_dataset()
    X, y = preprocess_lead_time(lead_df)
    lead_time_model = train_lead_time_model(X, y)

    predictions_df = predict_reorder(
        features_df,
        lead_time_model,
        forecast_lookup
    )

    save_reorder_predictions(predictions_df)

    # Profit Analysis
    df = fetch_dataset_profit_analysis(start_date, end_date)
    X, y = preprocess_profit_analysis(df)
    model = train_profit_model(X, y)
    future_df = predict_future_profit(model, df, days=30)
    predicted_profit_df = aggregate_predicted_profit(future_df)
    save_profit_prediction(predicted_profit_df)

    pareto_df = build_profit_pareto(predicted_profit_df)