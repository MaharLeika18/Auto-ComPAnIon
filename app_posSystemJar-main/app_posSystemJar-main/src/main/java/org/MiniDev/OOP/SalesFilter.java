package org.MiniDev.OOP;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SalesFilter {

    // Method to apply multiple filters to the list
    public static List<TodaySalesLists> filterSalesList(List<TodaySalesLists> salesList, Predicate<TodaySalesLists>... filters) {
        // Start with the full list and apply each filter
        List<TodaySalesLists> filteredList = salesList;
        for (Predicate<TodaySalesLists> filter : filters) {
            filteredList = filteredList.stream().filter(filter).collect(Collectors.toList());
        }
        return filteredList;
    }


    // Method to apply multiple filters to the list
    public static List<DrawerSalesLists> filterDrawerSalesList(List<DrawerSalesLists> drawerSalesList, Predicate<DrawerSalesLists>... filters) {
        // Start with the full list and apply each filter
        List<DrawerSalesLists> filteredList = drawerSalesList;
        for (Predicate<DrawerSalesLists> filter : filters) {
            filteredList = filteredList.stream().filter(filter).collect(Collectors.toList());
        }
        return filteredList;
    }
}
