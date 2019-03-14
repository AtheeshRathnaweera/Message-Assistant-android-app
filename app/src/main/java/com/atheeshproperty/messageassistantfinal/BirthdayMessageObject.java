package com.atheeshproperty.messageassistantfinal;

public class BirthdayMessageObject {

    private int id;
    private String name;
    private String birthdate;
    private String contactNumber;
    private String message;
    private String sendTime;
    private String media;
    private int pause;
    private String autoText;

    public BirthdayMessageObject(){


    }

    BirthdayMessageObject(int id,  String name, String Birthdate, String contactNumber, String message,
                  String sendTime, String media, int pause, String autoText){

        this.id = id;
        this.name = name;
        this.birthdate = Birthdate;
        this.contactNumber = contactNumber;
        this.message = message;
        this.sendTime = sendTime;
        this.media = media;
        this.pause = pause;
       this.autoText = autoText;

    }

    public String getAutoText() {
        return autoText;
    }

    public void setAutoText(String autoText) {
        this.autoText = autoText;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public int getPause() {
        return pause;
    }

    public void setPause(int pause) {
        this.pause = pause;
    }
}
