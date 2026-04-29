package com.autocompanion.pos.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Single source of truth for all orders in this JVM session.
 * Both DashboardController (writer) and InventoryController (reader)
 * reference this same static list.
 *
 * Swap-out guide: replace the ObservableList with DB calls
 * in add() and getAll() when you're ready to persist.
 */
public class LogStore {

    private static final ObservableList<OrderLog> orders =
        FXCollections.observableArrayList();

    private LogStore() {}   // utility class — no instances

    public static void add(OrderLog log) {
        orders.add(0, log);  // newest first
    }

    public static ObservableList<OrderLog> getAll() {
        return orders;
    }
}