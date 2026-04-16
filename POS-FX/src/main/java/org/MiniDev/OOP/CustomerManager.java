package org.MiniDev.OOP;

import java.util.ArrayList;
import java.util.List;

public class CustomerManager {

    private static CustomerManager instance;
    private List<Customers> customerList;

    private CustomerManager() {
        this.customerList = new ArrayList<>();
    }

    public static synchronized CustomerManager getInstance() {
        if (instance == null) {
            instance = new CustomerManager();
        }
        return instance;
    }

    public void addCustomer(Customers customer) {
        if (customer != null && getCustomerById(customer.getcID()) == null) {
            customerList.add(customer);
        }
    }

    public List<Customers> getAllCustomers() {
        return new ArrayList<>(customerList); // Return a copy to prevent external modification
    }

    public Customers getCustomerById(String cID) {
        for (Customers customer : customerList) {
            if (customer.getcID().equals(cID)) {
                return customer;
            }
        }
        return null;
    }

    public boolean removeCustomerById(String cID) {
        Customers customer = getCustomerById(cID);
        if (customer != null) {
            return customerList.remove(customer);
        }
        return false;
    }


    public void printAllCustomers() {
        for (Customers customer : customerList) {
            System.out.println(customer.getCustomerName() + " (" + customer.getcID() + ")");
        }
    }

    // Add the clearCustomers method
    public void clearCustomers() {
        customerList.clear(); // Clears all the customers from the list
    }
}
