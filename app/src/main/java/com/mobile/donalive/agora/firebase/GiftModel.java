package com.mobile.donalive.agora.firebase;

public class GiftModel {

    private String userId = "";
    private String userName = "";
    private String giftPath = "";
    private String giftCoin="";


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGiftPath() {
        return giftPath;
    }

    public void setGiftPath(String giftPath) {
        this.giftPath = giftPath;
    }

    public String getGiftCoin() {
        return giftCoin;
    }

    public void setGiftCoin(String giftCoin) {
        this.giftCoin = giftCoin;
    }
}
