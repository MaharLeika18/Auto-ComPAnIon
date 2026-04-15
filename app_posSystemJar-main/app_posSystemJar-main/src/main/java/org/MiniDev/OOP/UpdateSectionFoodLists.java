package org.MiniDev.OOP;

import java.util.Date;

import static UI.RoundedImagePanel.DEFAULT_IMAGE_BYTES;

public class UpdateSectionFoodLists {
    private byte[] foodPhoto;
    private int foodMainCounterID;
    private String counterName;
    private String foodCategory;
    private String foodSerialNumber;
    private String foodName;
    private double foodPrice;
    private double foodOriginalPrice;
    private double taxPercentage;
    private double promotionPercentage;
    private String foodDesc;
    private char stockCountYN;
    private int stockAvailableCnt;
    private String mainPrinterPortName;
    private String mainPrinterPortAddress;
    private Date createdDate;

    public UpdateSectionFoodLists(byte[] foodPhoto, int foodMainCounterID, String counterName, String foodCategory,
                                  String foodSerialNumber, String foodName, double foodPrice, double foodOriginalPrice,
                                  double taxPercentage, double promotionPercentage, String foodDesc, char stockCountYN,
                                  int stockAvailableCnt, String mainPrinterPortName, String mainPrinterPortAddress,
                                  Date createdDate) {
        this.foodPhoto = foodPhoto;
        this.foodMainCounterID = foodMainCounterID;
        this.counterName = counterName;
        this.foodCategory = foodCategory;
        this.foodSerialNumber = foodSerialNumber;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodOriginalPrice = foodOriginalPrice;
        this.taxPercentage = taxPercentage;
        this.promotionPercentage = promotionPercentage;
        this.foodDesc = foodDesc;
        this.stockCountYN = stockCountYN;
        this.stockAvailableCnt = stockAvailableCnt;
        this.mainPrinterPortName = mainPrinterPortName;
        this.mainPrinterPortAddress = mainPrinterPortAddress;
        this.createdDate = createdDate;
    }

    public byte[] getFoodPhoto() {
        if (foodPhoto == null) {
            return DEFAULT_IMAGE_BYTES;
        } else {
            return foodPhoto;
        }
    }

    public int getFoodMainCounterID() {
        return foodMainCounterID;
    }

    public String getCounterName() {
        return counterName;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public String getFoodSerialNumber() {
        return foodSerialNumber;
    }

    public String getFoodName() {
        return foodName;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public double getFoodOriginalPrice() {
        return foodOriginalPrice;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public double getPromotionPercentage() {
        return promotionPercentage;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public char getStockCountYN() {
        return stockCountYN;
    }

    public int getStockAvailableCnt() {
        return stockAvailableCnt;
    }

    public String getMainPrinterPortName() {
        return mainPrinterPortName;
    }

    public String getMainPrinterPortAddress() {
        return mainPrinterPortAddress;
    }

    public Date getCreatedDate() {
        return createdDate;
    }
}
