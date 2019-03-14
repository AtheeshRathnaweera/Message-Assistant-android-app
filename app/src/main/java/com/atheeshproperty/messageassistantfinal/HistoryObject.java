package com.atheeshproperty.messageassistantfinal;

public class HistoryObject {

    private int historyID;
    private String mesID;
    private String mesTitle;
    private String conatactNumber;
    private String historyContent;
    private String sendTime;
   private String status;
   private String mesType;

    public HistoryObject(){


    }

    HistoryObject(int id,  String mesID, String title, String mesType, String contactNumber, String message,
                  String sendTime, String mesStatus){

        this.historyID = id;
        this.mesID = mesID;
        this.mesTitle = title;
        this.conatactNumber = contactNumber;
        this.historyContent = message;
        this.sendTime = sendTime;
       this.status = mesStatus;
       this.mesType = mesType;

    }

    public String getMesType() {
        return mesType;
    }

    public void setMesType(String mesType) {
        this.mesType = mesType;
    }

    public int getHistoryID() {
        return historyID;
    }

    public void setHistoryID(int historyID) {
        this.historyID = historyID;
    }

    public String getMesID() {
        return mesID;
    }

    public void setMesID(String mesID) {
        this.mesID = mesID;
    }

    public String getMesTitle() {
        return mesTitle;
    }

    public void setMesTitle(String mesTitle) {
        this.mesTitle = mesTitle;
    }

    public String getConatactNumber() {
        return conatactNumber;
    }

    public void setConatactNumber(String conatactNumber) {
        this.conatactNumber = conatactNumber;
    }

    public String getHistoryContent() {
        return historyContent;
    }

    public void setHistoryContent(String historyContent) {
        this.historyContent = historyContent;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
