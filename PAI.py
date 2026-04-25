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
            row['forecast_date'],
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

def save_roi_predictions(forecast_df, forecast_date):
    cursor = conn.cursor()

    data = [
        (
            "ROI",
            float(row['predicted_roi']),
            forecast_date,
            "ARIMA_v1"
        )
        for _, row in forecast_df.iterrows()
    ]

    cursor.executemany("""
        CALL add_financial_prediction(%s, %s, %s, %s, %s)
    """, data)

    conn.commit()

# ROI Break-even Prediction (When will the user see a return on investment)
def fetch_profit_forecast(start_date, end_date):
    cursor = conn.cursor()

    cursor.callproc('dataset_cumulative_profit_forecast', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    df = pd.DataFrame(data, columns=columns)
    df['forecast_date'] = pd.to_datetime(df['forecast_date'])

    return df

def predict_break_even(df_forecast, investment_amount):
    df = df_forecast.copy()

    # cumulative profit over time
    df['cumulative_profit'] = df['total_predicted_profit'].cumsum()

    # find first date where investment is recovered
    breakeven_row = df[df['cumulative_profit'] >= investment_amount]

    if not breakeven_row.empty:
        breakeven_date = breakeven_row.iloc[0]['forecast_date']
        return breakeven_date, df
    else:
        return None, df
    
def save_break_even(investment_id, date, amount):
    cursor = conn.cursor()

    cursor.execute("""
        CALL add_break_even_prediction(%s, %s, %s, %s)
    """, (
        investment_id,
        date,
        amount,
        "ROI_FORECAST_v1"
    ))

    conn.commit()

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
            row['forecast_date'],
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

def save_cagr_prediction(forecast_df, forecast_date):
    cursor = conn.cursor()

    data = [
        (
            "CAGR",
            float(row['predicted_cagr']),
            forecast_date,
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

    for entity_id in df['entity_id'].unique():
            entity_df = df[df['entity_id'] == entity_id]
            entity_name = entity_df['entity_name'].iloc[0]

            entity_df = entity_df.set_index('sale_date').sort_index()
            entity_df = entity_df.asfreq('D', fill_value=0)

            series = entity_df['total_quantity']

            if len(series) < 20:
                continue

            try:
                model = ARIMA(series, order=(5,1,0))
                model_fit = model.fit()

                forecast = model_fit.forecast(steps=forecast_days)

                last_date = series.index[-1]
                future_dates = [last_date + timedelta(days=i+1) for i in range(forecast_days)]

                for date, value in zip(future_dates, forecast):
                    forecasts.append({
                        "entity_id": entity_id,
                        "entity_name": entity_name,
                        "forecast_date": date,
                        "predicted_demand": max(0, float(value))
                    })

            except Exception as e:
                print(f"Error forecasting {entity_name}: {e}")  
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

# Profit Prediction
    # profit = demand × (real selling price − real cost − discounts)
def fetch_dataset_profit_prediction(start_date, end_date, product_ids=None):
    cursor = conn.cursor()
    cursor.callproc('dataset_profit_prediction', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    df = pd.DataFrame(data, columns=columns)

    if product_ids is not None:
        df = df[df['product_id'].isin(product_ids)]

    df['sale_date'] = pd.to_datetime(df['sale_date'])

    return df

def preprocess_profit_analysis(df):
    df = df.sort_values(['product_id', 'sale_date'])

    # Lag features
    df['prev_profit'] = df.groupby('product_id')['profit'].shift(1)
    df['prev_quantity'] = df.groupby('product_id')['total_quantity'].shift(1)

    # Rolling features
    df['rolling_profit_7'] = df.groupby('product_id')['profit'].rolling(7).mean().reset_index(0, drop=True)
    df['rolling_qty_7'] = df.groupby('product_id')['total_quantity'].rolling(7).mean().reset_index(0, drop=True)

    # Time features
    df['month'] = df['sale_date'].dt.month
    df['day_of_week'] = df['sale_date'].dt.dayofweek

    df = df.dropna()

    features = [
        'product_id',
        'prev_profit',
        'prev_quantity',
        'rolling_profit_7',
        'rolling_qty_7',
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
        prev_profit = row['profit']
        prev_qty = row['total_quantity']
        rolling_profit = row.get('rolling_profit_7', prev_profit)
        rolling_qty = row.get('rolling_qty_7', prev_qty)

        for i in range(days):
            future_date = row['sale_date'] + pd.Timedelta(days=i+1)

            input_data = pd.DataFrame([{
                "product_id": row['product_id'],
                "prev_profit": prev_profit,
                "prev_quantity": prev_qty,
                "rolling_profit_7": rolling_profit,
                "rolling_qty_7": rolling_qty,
                "avg_price": row['avg_price'],
                "avg_cost": row['avg_cost'],
                "month": future_date.month,
                "day_of_week": future_date.dayofweek
            }])

            pred = model.predict(input_data)[0]
            pred = max(0, float(pred))  # prevent negative profit

            results.append({
                "product_id": row['product_id'],
                "forecast_date": future_date,
                "predicted_profit": pred
            })

            # Update rolling values 
            prev_profit = pred
            rolling_profit = (rolling_profit * 6 + pred) / 7

    return pd.DataFrame(results)

def compute_profit_contribution(df):
    grouped = (
        df.groupby('product_id')['predicted_profit']
        .sum()
        .reset_index()
        .sort_values(by='predicted_profit', ascending=False)
    )

    total_profit = grouped['predicted_profit'].sum()

    grouped['contribution_pct'] = (
        grouped['predicted_profit'] / total_profit * 100
    )

    return grouped

def save_profit_prediction(profit_df):
    cursor = conn.cursor()

    data = [
        (
            int(row['product_id']),
            int(row['predicted_profit']),
            row['forecast_date'],
            "GBR_v1"
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
    df = (
        df_pred
        .groupby('product_id')['predicted_profit']
        .sum()
        .reset_index()
    )

    df = df.sort_values(by='predicted_profit', ascending=False)

    total_profit = df['predicted_profit'].sum()
    df['contribution_pct'] = df['predicted_profit'] / total_profit * 100
    df['cumulative_pct'] = df['contribution_pct'].cumsum()

    return df

# EBIT (Calculated) 
def fetch_current_ebit(start_date, end_date):
    cursor = conn.cursor()

    cursor.callproc('get_current_ebit', [start_date, end_date])

    for result in cursor.stored_results():
        row = result.fetchone()
        columns = [col[0] for col in result.description]

    return dict(zip(columns, row))

# EBIT (Predicted)  
def fetch_ebit_dataset(start_date, end_date):
    cursor = conn.cursor()

    cursor.callproc('dataset_ebit', [start_date, end_date])

    for result in cursor.stored_results():
        data = result.fetchall()
        columns = [col[0] for col in result.description]

    df = pd.DataFrame(data, columns=columns)
    df['period'] = pd.to_datetime(df['period'])

    return df

def preprocess_ebit(df):
    df = df.sort_values('period')

    df['prev_ebit'] = df['ebit'].shift(1)
    df['prev_revenue'] = df['revenue'].shift(1)

    df['ebit_margin'] = df['ebit'] / df['revenue'].replace(0, 1)
    df['prev_margin'] = df['ebit_margin'].shift(1)

    df['month'] = df['period'].dt.month
    df['day_of_week'] = df['period'].dt.dayofweek

    df = df.dropna()

    X = df[['prev_ebit', 'prev_revenue', 'prev_margin', 'month', 'day_of_week']]
    y = df['ebit']

    return X, y

def train_ebit_model(X, y):
    model = GradientBoostingRegressor()
    model.fit(X, y)
    return model

def predict_ebit(model, df, days=30):
    results = []

    last_row = df.sort_values('period').iloc[-1]

    prev_ebit = last_row['ebit']
    prev_revenue = last_row['revenue']
    prev_margin = prev_ebit / prev_revenue if prev_revenue != 0 else 0

    for i in range(days):
        future_date = last_row['period'] + pd.Timedelta(days=i+1)

        input_data = pd.DataFrame([{
            "prev_ebit": prev_ebit,
            "prev_revenue": prev_revenue,
            "prev_margin": prev_margin,
            "month": future_date.month,
            "day_of_week": future_date.dayofweek
        }])

        pred = model.predict(input_data)[0]

        results.append({
            "forecast_date": future_date,
            "predicted_ebit": float(pred)
        })

        prev_ebit = pred  # rolling prediction
        prev_margin = pred / prev_revenue if prev_revenue != 0 else 0

    return pd.DataFrame(results)

def save_ebit_predictions(df):
    cursor = conn.cursor()

    data = [
        (
            row['forecast_date'],
            row['predicted_ebit'],
            "GBR_v1"
        )
        for _, row in df.iterrows()
    ]

    cursor.executemany("""
        CALL add_ebit_prediction(%s, %s, %s)
    """, data)

    conn.commit()

# EBIT Margin (Current)
def fetch_current_ebit_margin(start_date, end_date):
    cursor = conn.cursor()

    cursor.callproc('get_current_ebit_margin', [start_date, end_date])

    for result in cursor.stored_results():
        row = result.fetchone()
        columns = [col[0] for col in result.description]

    return dict(zip(columns, row))

# Net Profit (Calculated) KPI
def fetch_current_net_profit(conn, start_date, end_date):
    cursor = conn.cursor()

    cursor.execute("""
        CALL calculate_current_net_profit(%s, %s)
    """, (start_date, end_date))

    result = cursor.fetchone()

    columns = [col[0] for col in cursor.description]

    df = pd.DataFrame([result], columns=columns)

    cursor.close()
    return df

def get_net_profit_value(conn, start_date, end_date):   # Call this for dashboard
    df = fetch_current_net_profit(conn, start_date, end_date)
    return float(df['net_profit'].iloc[0])

# Net Profit (Predicted) Timeseries
def preprocess_net_profit(df):
    df = df.sort_values('period')

    df['prev_profit'] = df['net_profit'].shift(1)
    df['prev_revenue'] = df['revenue'].shift(1)

    df['month'] = df['period'].dt.month
    df['day_of_week'] = df['period'].dt.dayofweek

    df = df.dropna()

    X = df[['prev_profit', 'prev_revenue', 'month', 'day_of_week']]
    y = df['net_profit']

    return X, y

def train_net_profit_model(X, y):
    model = GradientBoostingRegressor()
    model.fit(X, y)
    return model

def predict_net_profit(model, df, days=30):
    results = []

    last_row = df.sort_values('period').iloc[-1]

    prev_profit = last_row['net_profit']
    prev_revenue = last_row['revenue']

    for i in range(days):
        future_date = last_row['period'] + pd.Timedelta(days=i+1)

        input_data = pd.DataFrame([{
            "prev_profit": prev_profit,
            "prev_revenue": prev_revenue,
            "month": future_date.month,
            "day_of_week": future_date.dayofweek
        }])

        pred = model.predict(input_data)[0]

        results.append({
            "forecast_date": future_date,
            "predicted_net_profit": float(pred)
        })

        prev_profit = pred

    return pd.DataFrame(results)

def save_net_profit_predictions(conn, df):
    cursor = conn.cursor()

    data = [
        (
            row['forecast_date'],
            row['predicted_net_profit'],
            "GBR_v1"
        )
        for _, row in df.iterrows()
    ]

    cursor.executemany("""
        CALL add_net_profit_prediction(%s, %s, %s)
    """, data)

    conn.commit()

# Net Profit Impact Factors (by product category)
def fetch_net_profit_by_category(conn, start_date, end_date):
    cursor = conn.cursor()

    cursor.execute("""
        CALL net_profit_by_category(%s, %s)
    """, (start_date, end_date))

    rows = cursor.fetchall()
    columns = [col[0] for col in cursor.description]

    df = pd.DataFrame(rows, columns=columns)

    cursor.close()
    return df

# Gross Profit (Calculated) KPI
def fetch_current_gross_profit(conn, start_date, end_date):
    cursor = conn.cursor()

    cursor.execute("""
        CALL calculate_current_gross_profit(%s, %s)
    """, (start_date, end_date))

    row = cursor.fetchone()
    columns = [col[0] for col in cursor.description]

    df = pd.DataFrame([row], columns=columns)

    cursor.close()
    return df

def get_gross_profit_value(conn, start_date, end_date):   # Call this for dashboard
    df = fetch_current_gross_profit(conn, start_date, end_date)
    return float(df['gross_profit'].iloc[0])

# Gross Profit (Predicted)
def preprocess_gross_profit(df):
    df = df.sort_values('period')

    df['prev_profit'] = df['gross_profit'].shift(1)
    df['prev_revenue'] = df['revenue'].shift(1)

    df['month'] = df['period'].dt.month
    df['day_of_week'] = df['period'].dt.dayofweek

    df = df.dropna()

    X = df[['prev_profit', 'prev_revenue', 'month', 'day_of_week']]
    y = df['gross_profit']

    return X, y

def train_gross_profit_model(X, y):
    model = GradientBoostingRegressor()
    model.fit(X, y)
    return model

def predict_gross_profit(model, df, days=30):
    results = []

    last_row = df.sort_values('period').iloc[-1]

    prev_profit = last_row['gross_profit']
    prev_revenue = last_row['revenue']

    for i in range(days):
        future_date = last_row['period'] + pd.Timedelta(days=i+1)

        input_data = pd.DataFrame([{
            "prev_profit": prev_profit,
            "prev_revenue": prev_revenue,
            "month": future_date.month,
            "day_of_week": future_date.dayofweek
        }])

        pred = model.predict(input_data)[0]

        results.append({
            "forecast_date": future_date,
            "predicted_gross_profit": float(pred)
        })

        prev_profit = pred

    return pd.DataFrame(results)

def save_gross_profit_predictions(conn, df):
    cursor = conn.cursor()

    data = [
        (
            row['forecast_date'],
            row['predicted_gross_profit'],
            "GBR_v1"
        )
        for _, row in df.iterrows()
    ]

    cursor.executemany("""
        CALL add_gross_profit_prediction(%s, %s, %s)
    """, data)

    conn.commit()

# Gross Profit Contributors (by Category)
def fetch_gross_profit_by_category(conn, start_date, end_date):
    cursor = conn.cursor()

    cursor.execute("""
        CALL gross_profit_by_category(%s, %s)
    """, (start_date, end_date))

    rows = cursor.fetchall()
    columns = [col[0] for col in cursor.description]

    df = pd.DataFrame(rows, columns=columns)

    cursor.close()
    return df

# Misc Funcs
def calculate_trend(current, previous):
    if previous == 0:
        return {"direction": "neutral", "percent_change": 0}

    change = (current - previous) / abs(previous)

    return {
        "direction": "up" if change > 0 else "down",
        "percent_change": change * 100
    }

# Main Execution (for testing)
if __name__ == "__main__":
    # NOTE: These are all example usages for both testing and reference 
    start_date = "2025-01-01"
    end_date = "2026-04-20"

    # ROI
    df = fetch_calculated_roi(start_date, end_date) # Calculated, take roi for kpi
    save_roi_calculation(df)

    df = fetch_roi_dataset(start_date, end_date)    
    df = preprocess_roi(df)
    forecast_df = train_roi_model(df, forecast_days=7)  # Predicted
    save_roi_predictions(forecast_df, start_date, end_date)

    # CAGR
    df = fetch_calculated_cagr(start_date, end_date) # Calculated, take cagr for kpi
    save_cagr_calculated(df)

    df = fetch_cagr_dataset(start_date, end_date)   
    df = preprocess_cagr(df)
    forecast_df = train_cagr_model(df)  # Predicted
    save_cagr_prediction(forecast_df, start_date, end_date)

    # Demand Forecasting
    df = fetch_sales_data(mode="PRODUCT", product_id=123)   # Forecast one product 
    df = fetch_sales_data(mode="PRODUCT", product_id=None)  # Forecast all products 
    df = fetch_sales_data(mode="CATEGORY", product_id=None) # Forecast categories

    product_forecast = forecast_demand(df)

    # Forecast products but agg by category
    category_forecast = (
        product_forecast
        .groupby(['category_name', 'forecast_date'])['predicted_demand']
        .sum()
        .reset_index()
    )

    save_forecasts(category_forecast)

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

    # Profit Prediction
    # Single product
    df = fetch_dataset_profit_prediction(start_date, end_date, product_ids=[101])
    
    X, y = preprocess_profit_analysis(df)
    model = train_profit_model(X, y)

    forecast_df = forecast_df[forecast_df['product_id'] == 101] # filter again jic

    # Multiple products
    df = fetch_dataset_profit_prediction(start_date, end_date, product_ids=[101, 205, 330])
    
    X, y = preprocess_profit_analysis(df)
    model = train_profit_model(X, y)

    forecast_df = forecast_df[
        forecast_df['product_id'].isin([101, 205, 330])
    ]

    # All products
    df = fetch_dataset_profit_prediction(start_date, end_date)

    X, y = preprocess_profit_analysis(df)
    model = train_profit_model(X, y)

    forecast_df = predict_future_profit(model, df, days=30)

    save_profit_prediction(forecast_df) # Make sure to always save predictions

    # Compute individual product profit contribution
    contribution_df = compute_profit_contribution(forecast_df)

    # Feed into pareto chart
    predicted_profit_df = aggregate_predicted_profit(forecast_df)
    pareto_df = build_profit_pareto(predicted_profit_df)

    # EBIT
    start = datetime.now().replace(day=1)  # start of month
    end = datetime.now()
    kpi = fetch_current_ebit(start, end) # Calculated

    df = fetch_ebit_dataset(start_date, end_date)   
    X, y = preprocess_ebit(df)
    model = train_ebit_model(X, y)
    forecast_df = predict_ebit(model, df)   # Predicted
    save_ebit_predictions(forecast_df)      
        # Insights:
        # EBIT rising -> invest more inventory
        # EBIT falling -> reduce costs or adjust pricing

    # EBIT MARGIN
    current = fetch_current_ebit_margin(start_current, end_current)
    previous = fetch_current_ebit_margin(start_prev, end_prev)

    trend = calculate_trend(
        current['ebit_margin'],
        previous['ebit_margin']
    )
