package com.atheeshproperty.messageassistantfinal;

public class BirthdayMessageObject {

    private int id;
    private String name;
    private String birthdate;
    private String contactNumber;
    private String message;
    private String sendTime;
    private String repeat;
    private String media;
    private int pause;

    public BirthdayMessageObject(){


    }

    BirthdayMessageObject(int id,  String name, String Birthdate, String contactNumber, String message,
                  String sendTime, String repeat, String media, int pause){

        this.id = id;
        this.name = name;
        this.birthdate = Birthdate;
        this.contactNumber = contactNumber;
        this.message = message;
        this.sendTime = sendTime;
        this.repeat = repeat;
        this.media = media;
        this.pause = pause;

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

    public int getPause() {
        return pause;
    }

    public void setPause(int pause) {
        this.pause = pause;
    }
}
