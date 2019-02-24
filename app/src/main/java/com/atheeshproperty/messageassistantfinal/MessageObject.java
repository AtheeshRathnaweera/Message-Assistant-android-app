package com.atheeshproperty.messageassistantfinal;

public class MessageObject {

    private int id;
    private String title;
    private String conatactNumber;
    private String messageOne;
    private String messageTwo;
    private String messageThree;
    private String messageFour;
    private String sendTime;
    private String repeat;
    private String media;

    public MessageObject(){


    }

    MessageObject(int id,  String title, String conatctNumber, String messageOne, String messageTwo, String messageThree, String messageFour,
            String sendTime, String repeat, String media){

        this.id = id;
        this.title = title;
        this.conatactNumber = conatctNumber;
        this.messageOne = messageOne;
        this.messageTwo = messageTwo;
        this.messageThree = messageThree;
        this.messageFour = messageFour;
        this.sendTime = sendTime;
        this.repeat = repeat;
        this.media = media;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConatactNumber() {
        return conatactNumber;
    }

    public void setConatactNumber(String conatactNumber) {
        this.conatactNumber = conatactNumber;
    }

    public String getMessageOne() {
        return messageOne;
    }

    public void setMessageOne(String messageOne) {
        this.messageOne = messageOne;
    }

    public String getMessageTwo() {
        return messageTwo;
    }

    public void setMessageTwo(String messageTwo) {
        this.messageTwo = messageTwo;
    }

    public String getMessageThree() {
        return messageThree;
    }

    public void setMessageThree(String messageThree) {
        this.messageThree = messageThree;
    }

    public String getMessageFour() {
        return messageFour;
    }

    public void setMessageFour(String messageFour) {
        this.messageFour = messageFour;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }
}
