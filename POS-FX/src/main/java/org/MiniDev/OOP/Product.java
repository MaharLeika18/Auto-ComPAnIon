package org.MiniDev.OOP;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Product {
    private String id; 
    private String name;
    private String serialCode;
    private double price;
    private byte[] photo;
    private String foodDesc;
    private double promotionPercentage;
    private int counterId;
    private int StockAvailableNumber;
    private String counterName;
    private String mainPrinterPortName;
    private String mainPrinterPortAddress;

    private static Map<String, Product> productMap = new HashMap<>();

    // Constructor
    public Product(String name, double price, byte[] photo) {
        this.name = name;
        this.price = price;
        this.photo = photo;
        productMap.put(name, this);
    }

    // Full constructor
    public Product(String counterName,
                   String mainPrinterPortName,
                   String mainPrinterPortAddress,
                   String name,
                   double price,
                   byte[] photo,
                   double promotionPercentage,
                   String foodDesc,
                   int counterId,
                   int StockAvailableNumber,
                   String serialCode) {
        this.counterName = counterName;
        this.mainPrinterPortName = mainPrinterPortName;
        this.mainPrinterPortAddress = mainPrinterPortAddress;
        this.name = name;
        this.price = price;
        this.photo = photo;
        this.promotionPercentage = promotionPercentage;
        this.foodDesc = foodDesc;
        this.counterId = counterId;
        this.StockAvailableNumber = StockAvailableNumber;
        this.serialCode = serialCode;
        productMap.put(name, this);
    }

    public Product() {
    }

    public String getId() { return id;
    }

    public void setId(String id) { this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockAvailableNumber() {
        return StockAvailableNumber;
    }

    public void setStockAvailableNumber(int stockAvailableNumber) {
        StockAvailableNumber = stockAvailableNumber;
    }
    // ===== END ADDED METHODS =====

    public String getSerialCode() {
        return serialCode;
    }

    public Map<String, Product> getProductMap() {
        return productMap;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public String getCounterName() {
        return counterName;
    }

    public String getMainPrinterPortName() {
        return mainPrinterPortName;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public int getCounterId() {
        return counterId;
    }

    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public String getUnitPriceString(){
        return decimalFormat.format(price);
    }

    public String getDescription() {
        return foodDesc;
    }

    public double getPromotionPercentage() {
        return promotionPercentage;
    }

    // Static method to get counter name by product name
    public static List<String> getCounterNameByProductName(String productName) {
        Product product = productMap.get(productName);
        return product != null ? Collections.singletonList(product.getCounterName()) : null;
    }

    public String getMainPrinterPortNameByProductName(String productName) {
        Product product = productMap.get(productName);
        return product != null ? product.getMainPrinterPortName() : null;
    }

    public String getMainPrinterPortAddress() {
        return mainPrinterPortAddress;
    }

    public void setMainPrinterPortAddress(String mainPrinterPortAddress) {
        this.mainPrinterPortAddress = mainPrinterPortAddress;
    }

    public static String getMainPrinterPortAddressByCounterName(String counterName) {
        for (Product product : productMap.values()) {
            if (product.getCounterName().equals(counterName)) {
                return product.getMainPrinterPortAddress();
            }
        }
        return null;
    }

    public void setSerialCode(String serialCode) {
        this.serialCode = serialCode;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void setFoodDesc(String foodDesc) {
        this.foodDesc = foodDesc;
    }

    public void setPromotionPercentage(double promotionPercentage) {
        this.promotionPercentage = promotionPercentage;
    }

    public void setCounterId(int counterId) {
        this.counterId = counterId;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public void setMainPrinterPortName(String mainPrinterPortName) {
        this.mainPrinterPortName = mainPrinterPortName;
    }

    public static void setProductMap(Map<String, Product> productMap) {
        Product.productMap = productMap;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public void setDecimalFormat(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }
}