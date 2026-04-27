import pandas as pd

products_df = pd.read_csv('Mock Data/MOCK_DATA_PRODUCT.csv')
suppliers_df = pd.read_csv('Mock Data/MOCK_DATA_SUPPLIER.csv')
product_ids = products_df.index + 1  # adjust if necessary
supplier_ids = suppliers_df.index[:15] + 1 # pick 15

import random
from datetime import datetime, timedelta

NUM_WEEKS = 60    # ~15 months to spread POs over, adjust as needed
suppliers = supplier_ids.tolist()
purchase_orders = []
purchase_order_items = []
product_batches = []

po_id = 1
po_item_id = 1
batch_id = 1
start_date = datetime.strptime('2025-01-01', '%Y-%m-%d')
for supplier in suppliers:
    po_date = start_date
    for _ in range(NUM_WEEKS):
        # 1 PO per supplier every ~1-2 weeks
        po_date += timedelta(days=random.randint(7, 14))
        total_cost = 0
        items_in_po = random.randint(3, 10)
        items = []
        for _ in range(items_in_po):
            # Map which products each supplier sells
            supplier_products = {
                supplier: random.sample(list(product_ids), k=random.randint(20, min(80, len(product_ids))))
                for supplier in suppliers
            }
            product_id = int(random.choice(supplier_products[supplier]))
            quantity = random.randint(10, 300)
            unit_cost = round(random.uniform(50, 5000), 2)
            cost = quantity * unit_cost
            items.append((po_item_id, po_id, product_id, quantity, unit_cost, cost))
            # batch:
            quantity_received = quantity + random.randint(0, 200)
            quantity_remaining = random.choice([
                0,  # fully used
                random.randint(1, quantity_received - 1) if quantity_received > 1 else 1,  # partially
                quantity_received  # untouched
            ])
            product_batches.append({
                "batch_id": batch_id, "product_id": product_id, "supplier_id": supplier,
                "quantity_received": quantity_received, "quantity_remaining": quantity_remaining,
                "unit_cost": unit_cost, "date_received": po_date.strftime('%Y-%m-%d'), "barcode": f"BATCH{batch_id:06d}",
                "po_id": po_id
            })
            batch_id += 1
            po_item_id += 1
            total_cost += cost
        purchase_orders.append({
            "po_id": po_id, "supplier_id": supplier, "order_date": po_date.strftime('%Y-%m-%d'),
            "total_cost": round(total_cost, 2)
        })
        for item in items:
            # Add to PO Items
            purchase_order_items.append({
                "id": item[0], "po_id": item[1], "product_id": item[2], "quantity": item[3], "unit_cost": item[4]
            })
        po_id += 1

def get_weighted_day(date):
    # Friday-Sunday = high, Mon-Thu = low/moderate
    if date.weekday() >= 4: return 'WEEKEND'
    return 'WEEKDAY'

def payday(date):
    return date.day == 15 or date.day == 30

import numpy as np
from collections import defaultdict

batch_lookup = defaultdict(list)
for b in product_batches:
    batch_lookup[b["product_id"]].append(b)


transaction_logs = []
transaction_items = []

item_id = 1
tx_id = 1

date_range = pd.date_range(start=start_date, periods=450)  # ~15 months

for date in date_range:

    # More realistic daily volume
    base_tx = random.randint(5, 15)

    if payday(date) or get_weighted_day(date) == 'WEEKEND':
        base_tx = int(base_tx * random.uniform(1.3, 2.0))

    for _ in range(base_tx):

        tx_items = []

        num_items = random.randint(1, 5)

        # Weighted product selection (top sellers emerge naturally)
        weights = np.random.zipf(2, len(product_ids))
        chosen_products = random.choices(list(product_ids), weights=weights, k=num_items)

        for p in chosen_products:

            quantity = random.randint(1, 10)

            # ✅ FIFO batch selection
            batches = sorted(
                [b for b in batch_lookup[p] if b['quantity_remaining'] > 0],
                key=lambda x: x['date_received']
            )

            if not batches:
                continue  # simulate stockout

            remaining_qty = quantity

            for batch in batches:
                if remaining_qty <= 0:
                    break

                take_qty = min(batch['quantity_remaining'], remaining_qty)

                unit_cost = float(batch['unit_cost'])
                unit_selling = round(unit_cost * random.uniform(1.1, 2.2), 2)

                r = random.random()
                if r < 0.3: discount = 0.00
                elif r < 0.9: discount = round(random.uniform(0.01, 0.10), 2)
                else: discount = round(random.uniform(0.11, 0.30), 2)

                total_sale_value = round(unit_selling * take_qty - discount * take_qty, 2)
                total_cost = round(unit_cost * take_qty, 2)

                tx_items.append({
                    "item_id": item_id,
                    "transaction_id": tx_id,
                    "product_id": p,
                    "batch_id": batch["batch_id"],
                    "quantity_sold": take_qty,
                    "unit_selling_price": unit_selling,
                    "unit_cost_at_sale": unit_cost,
                    "discount_applied": discount,
                    "total_sale_value": total_sale_value,
                    "total_cost": total_cost
                })

                batch['quantity_remaining'] -= take_qty
                remaining_qty -= take_qty
                item_id += 1

        if not tx_items:
            continue

        # ✅ Compute total from items
        tx_total = sum(item["total_sale_value"] for item in tx_items)

        # Status logic
        status = random.choices(
            ['CONFIRMED', 'CANCELLED', 'REFUNDED', 'PENDING'],
            weights=[80, 10, 5, 5], k=1)[0]

        # ✅ Handle refunds properly
        if status == 'REFUNDED':
            for item in tx_items:
                item["total_sale_value"] = -abs(item["total_sale_value"])
                item["total_cost"] = -abs(item["total_cost"])
            tx_total = -abs(tx_total)

        transaction_logs.append({
            "transaction_id": tx_id,
            "parent_transaction_id": '',
            "transaction_date": date.strftime('%Y-%m-%d'),
            "receipt_num": random.randint(100000, 999999),
            "total_amount": round(tx_total, 2),
            "payment_method": random.choices(
                ['CASH', 'E-WALLET', 'BANK'],
                weights=[60, 25, 15], k=1
            )[0],
            "status": status,
            "notes": ''
        })

        transaction_items.extend(tx_items)

        tx_id += 1

for tx in transaction_logs:
    num_items = random.randint(2, 5)
    chosen_products = random.sample(list(batch_lookup.keys()), num_items)
    for p in chosen_products:
        quantity = random.randint(1, 10)
        # Use (one) batch available for product (simulate FIFO)
    batches = sorted(
        [b for b in batch_lookup[p] if b['quantity_remaining'] > 0],
        key=lambda x: x['date_received']
    )

    # 🚨 CRITICAL: stop if no stock
    if not batches:
        continue

    remaining_qty = quantity

    for batch in batches:
        if remaining_qty <= 0:
            break

        # 🚨 EXTRA SAFETY (prevents future bugs)
        if batch is None or batch['quantity_remaining'] <= 0:
            continue

        take_qty = min(batch['quantity_remaining'], remaining_qty)

        if take_qty <= 0:
            continue

        unit_cost = float(batch['unit_cost'])
        unit_selling = round(unit_cost * random.uniform(1.1, 2.2), 2)

        r = random.random()
        if r < 0.3: discount = 0.00
        elif r < 0.9: discount = round(random.uniform(0.01, 0.10), 2)
        else: discount = round(random.uniform(0.11, 0.30), 2)

        total_sale_value = round(unit_selling * take_qty - discount * take_qty, 2)
        total_cost = round(unit_cost * take_qty, 2)

        tx_items.append({
            "item_id": item_id,
            "transaction_id": tx_id,
            "product_id": p,
            "batch_id": batch["batch_id"],
            "quantity_sold": take_qty,
            "unit_selling_price": unit_selling,
            "unit_cost_at_sale": unit_cost,
            "discount_applied": discount,
            "total_sale_value": total_sale_value,
            "total_cost": total_cost
        })

        batch['quantity_remaining'] -= take_qty
        remaining_qty -= take_qty
        item_id += 1

inventory_log = []
log_id = 1
for batch in product_batches:
    # IN: record receipt of stock
    inventory_log.append({
        "log_id": log_id, "product_id": batch["product_id"], "change_type": "IN",
        "quantity": batch["quantity_received"], "unit_cost": batch["unit_cost"],
        "log_date": batch["date_received"], "reference_id": batch["batch_id"], "reference_type": "PURCHASE"
    })
    log_id += 1

# OUT: for each transaction_item
confirmed_tx = {
    t["transaction_id"]: t for t in transaction_logs if t["status"] == "CONFIRMED"
}

for ti in transaction_items:
    if ti["transaction_id"] not in confirmed_tx:
        continue

    tx = confirmed_tx[ti["transaction_id"]]

    inventory_log.append({
        "log_id": log_id,
        "product_id": ti["product_id"],
        "change_type": "OUT",
        "quantity": ti["quantity_sold"],
        "unit_cost": ti["unit_cost_at_sale"],
        "log_date": tx["transaction_date"],
        "reference_id": ti["transaction_id"],
        "reference_type": "SALE"
    })
    log_id += 1
    
# ADJUSTMENT: 10% of records (simulate surplus, spoilage, etc.)
for _ in range(int(0.10 * len(inventory_log))):
    ti = random.choice(transaction_items)
    adj_qty = random.choice([
        random.randint(-20, -5),  # shrinkage
        random.randint(1, 10),    # corrections
    ])
    if adj_qty == 0: adj_qty = 1
    inventory_log.append({
        "log_id": log_id, "product_id": ti["product_id"], "change_type": "ADJUSTMENT",
        "quantity": adj_qty,
        "unit_cost": ti["unit_cost_at_sale"],
        "log_date": (datetime.strptime(transaction_logs[ti['transaction_id']-1]["transaction_date"], '%Y-%m-%d') + timedelta(days=random.randint(1,14))).strftime('%Y-%m-%d'),
        "reference_id": ti["transaction_id"], "reference_type": "ADJUSTMENT"
    })
    log_id += 1

# Example for transaction_log:
pd.DataFrame(transaction_logs).to_csv('MOCK_DATA_TRANSACTION_LOG.csv', index=False)
pd.DataFrame(transaction_items).to_csv('MOCK_DATA_TRANSACTION_ITEMS.csv', index=False)
pd.DataFrame(inventory_log).to_csv('MOCK_DATA_INVENTORY_LOG.csv', index=False)
pd.DataFrame(purchase_orders).to_csv('MOCK_DATA_PURCHASE_ORDERS.csv', index=False)
pd.DataFrame(purchase_order_items).to_csv('MOCK_DATA_PURCHASE_ORDER_ITEMS.csv', index=False)
pd.DataFrame(product_batches).to_csv('MOCK_DATA_PRODUCT_BATCHES.csv', index=False)