package com.parkmecorrect;

public class Car {
    String carNo;
    String sticker;
    String email;
    String mob;
    String type;
    String checkIn;

    public Car(String carNo, String sticker, String email) {
        this.carNo = carNo;
        this.sticker = sticker;
        this.email = email;
    }

    public String getSticker() {
        return sticker;
    }

    public String getEmail() {
        return email;
    }

    public String getCarNo() {
        return carNo;

    }

    public String getMob() {
        return mob;
    }

    public String getType() {
        return type;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public Car(String carNo, String type, String mob, String checkin) {
        this.carNo = carNo;
        this.type = type;
        this.checkIn = checkin;
        this.mob = mob;

    }

    public Car(String carNo) {
        this.carNo = carNo;
    }
}
