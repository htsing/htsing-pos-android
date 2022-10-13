package com.htsing.pos.bean;

import com.htsing.pos.easyhttp.CommonResult;

import java.io.Serializable;
import java.util.List;


public class ProductList  {

    private int total;
    private int size;
    private int current;
    private boolean searchCount;
    private int pages;
    private List<DataBean> records;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<DataBean> getRecords() {
        return records;
    }

    public void setRecords(List<DataBean> records) {
        this.records = records;
    }


    public static class DataBean implements Serializable {
        private int id;
        private String tenantId;
        private String partnerId;
        private String spuCode;
        private String scanCode;
        private String name;
        private String brief;
        private int categoryId;
        private String[] picUrls;
        private int sort;
        private String shopId;
        private double priceCost;
        private double stock = 0;
        private double priceRetail;
        private double priceVip;
        private int sales;
        private String createBy;
        private int attributeFlag;
        private int status;
        private String deliveryId;
        private String createTime;
        private String updateTime;
        private String unit;
        private String unitId;
        private String videoPath;
        private String pickupTips;
        private int exp;
        private int packages;
        private String model;
        private String standard;
        private String[] skus;

        public double getPriceRetail() {
            return priceRetail;
        }

        public void setPriceRetail(double priceRetail) {
            this.priceRetail = priceRetail;
        }

        public double getPriceVip() {
            return priceVip;
        }

        public void setPriceVip(double priceVip) {
            this.priceVip = priceVip;
        }

        private int orderNum = 1;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSpuCode() {
            return spuCode;
        }

        public void setSpuCode(String spuCode) {
            this.spuCode = spuCode;
        }

        public String[] getPicUrls() {
            return picUrls;
        }

        public void setPicUrls(String[] picUrls) {
            this.picUrls = picUrls;
        }

        public String getName() {
            return name;
        }

        public void setName(String prodName) {
            this.name = prodName;
        }

        public String getBrief() {
            return brief;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getScanCode() {
            return scanCode;
        }

        public void setScanCode(String scanCode) {
            this.scanCode = scanCode;
        }

        public int getOrderNum() {
            return orderNum;
        }

        public void setOrderNum(int orderNum) {
            this.orderNum = orderNum;
        }

        public double getStock() {
            return stock;
        }

        public void seStock(double stock) {
            this.stock = stock;
        }
    }
}
