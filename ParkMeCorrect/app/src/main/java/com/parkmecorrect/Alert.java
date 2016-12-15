package com.parkmecorrect;

public class Alert {
    Car car;
    String alertType;
    String alertDate;

    public Alert(Car car, String alertType, String alertDate) {
        this.car = car;
        this.alertType = alertType;
        this.alertDate = alertDate;
    }

    public Car getCar() {
        return car;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getAlertDate() {
        return alertDate;
    }
}
