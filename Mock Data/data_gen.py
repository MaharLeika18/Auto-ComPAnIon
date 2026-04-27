import csv
import random

NUM_TRANSACTIONS = 5000
NUM_PRODUCTS = 500
AVG_ITEMS = [2, 3, 4, 5]
items_data = []

item_id_counter = 1

for tx_id in range(1, NUM_TRANSACTIONS + 1):
    num_items = random.choices(AVG_ITEMS, weights=[1, 2, 2, 1])[0]
    products_picked = random.sample(range(1, NUM_PRODUCTS + 1), num_items)
    for product_id in products_picked:
        quantity_sold = random.randint(1, 10)
        # Simulate unit cost: low range for most, high occasionally
        if random.random() < 0.7:
            unit_cost = round(random.uniform(50, 500), 2)
        elif random.random() < 0.95:
            unit_cost = round(random.uniform(500, 2000), 2)
        else:
            unit_cost = round(random.uniform(2000, 5000), 2)
        # Selling price: at least 10% more (realistic retail margin)
        margin = random.uniform(1.1, 2.5)
        unit_selling_price = round(unit_cost * margin, 2)
        # Discount: 0 for ~30%, small (0 < d < 0.1) for ~60%, rare (0.1 <= d <= 0.3) for ~10%
        r = random.random()
        if r < 0.3:
            discount_applied = 0.00
        elif r < 0.9:
            discount_applied = round(random.uniform(0.01, 0.10), 2)
        else:
            discount_applied = round(random.uniform(0.11, 0.30), 2)
        # Per-unit values
        total_sale_value = round((unit_selling_price * quantity_sold) - (discount_applied * quantity_sold), 2)
        total_cost = round(unit_cost * quantity_sold, 2)
        batch_id = random.randint(1, NUM_PRODUCTS * 10)  # Assume many batches; change as suits your setup

        items_data.append([
            item_id_counter, tx_id, product_id, batch_id,
            quantity_sold, unit_selling_price, unit_cost, discount_applied, total_sale_value, total_cost
        ])
        item_id_counter += 1

with open('MOCK_DATA_TRANSACTION_ITEMS.csv', 'w', newline='') as f:
    writer = csv.writer(f)
    writer.writerow([
        "item_id", "transaction_id", "product_id", "batch_id",
        "quantity_sold", "unit_selling_price", "unit_cost_at_sale",
        "discount_applied", "total_sale_value", "total_cost"
    ])
    writer.writerows(items_data)