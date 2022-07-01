package com.mobile.donalive.agora.openvcall.model;

public class MultiLiveModel {

    private String channleName = "";
    private String uiid = "";
    private boolean check = false;
    private String receivedGift = "";

    private int randomNumber = 0;

    public String getReceivedGift() {
        return receivedGift;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    public void setReceivedGift(String receivedGift) {
        this.receivedGift = receivedGift;
    }

    public String getChannleName() {
        return channleName;
    }

    public void setChannleName(String channleName) {
        this.channleName = channleName;
    }

    public String getUiid() {
        return uiid;
    }

    public void setUiid(String uiid) {
        this.uiid = uiid;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
