package com.miaoshaproject.service.model;

import java.math.BigDecimal;

//用户下单模型
public class OrderModel {

    //订单id
    private String id;

    //购买的用户id
    private Integer userId;

    //购买的商品id
    private Integer itemId;

    //购买的数量
    private Integer amount;

    //若非空，则表示是已秒杀商品方式下单
    private Integer promoId;

    //购买商品的单价，若promoId非空则表示秒杀商品价格
    private BigDecimal itemPrice;

    //购买金额，若promoId非空则表示秒杀商品价格
    private BigDecimal orderPrice;

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amoun) {
        this.amount = amoun;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderAmount) {
        this.orderPrice = orderAmount;
    }
}
