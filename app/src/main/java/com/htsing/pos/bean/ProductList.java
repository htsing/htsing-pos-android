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
        /**
         * prodId : 864
         * shopId : 1
         * prodName : 花生酥原味160g袋装
         * oriPrice : 19.9
         * price : 15.9
         * brief : 零添加 更营养
         * pic : https://images.51xeld.com/2020/09/59062f7d4e4748ff851bc7f8415db274.jpg
         * imgs : 2020/09/59062f7d4e4748ff851bc7f8415db274.jpg,2020/09/17eab5c778df41e896d5390c0e4c6a42.jpg,2020/09/2d0f204fb00342b5b496bab50754e75d.jpg,2020/09/c9fe1263e21b4c68ba4d321071b2db89.jpg
         * status : 1
         * categoryId : 106
         * soldNum : 10
         * totalStocks : 99989
         * deliveryMode : null
         * deliveryTemplateId : null
         * createTime : 2020-07-23 16:30:15
         * updateTime : 2020-11-02 13:35:36
         * content : <p><img src="https://images.51xeld.com/2020/07/1859c6ac0a0c4e3dbc8614cb604152d0.jpg" alt="" width="750" height="963" /><img src="https://images.51xeld.com/2020/07/16ccd7f334ed4c0aa885812c4edbe9a4.jpg" alt="" width="750" height="1038" /><img src="https://images.51xeld.com/2020/07/6e472b274eb04cdbbf978491635fe68d.jpg" alt="" /><img src="https://images.51xeld.com/2020/07/4d99dcacacdb400dbc3fbeeea7c9217c.jpg" alt="" width="750" height="1002" /><img src="https://images.51xeld.com/2020/07/42807b5fd3f540ccaaca37268f356fb5.jpg" alt="" /><img src="https://images.51xeld.com/2020/07/43cb22901e534699b9c65762f9fd54ce.jpg" alt="" width="750" height="844" /></p>
         * putawayTime : 2020-11-02 13:35:36
         * version : null
         * isSpread : 0
         * isSignboard : 0
         * skuList : null
         * categoryList : null
         * shopName : null
         * tagList : null
         * activityPrice : 0.0
         * activityId : 0
         * activityTimes : 0
         * activityTimesFlag : 0
         * activityOrderTimes : 0
         * activityOrderFlag : 0
         * isOnlySelfmention : 1
         * isHotProd : 0
         * isGroupProd : null
         * saleType : 2
         * barCode : null
         */

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
