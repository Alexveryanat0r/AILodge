package com.example.myapplication;

public class GenerationData {
    private String name;
    private String city;
    private String urlPic;
    private String dess;
    private String dessAi;
    private String ssilka;
    private String uid;

    // Конструктор без параметров (обязателен для Firebase)
    public GenerationData() {
    }

    // Конструктор
    public GenerationData(String name, String city, String urlPic, String dess, String dessAi, String ssilka, String uid) {
        this.name = name;
        this.city = city;
        this.urlPic = urlPic;
        this.dess = dess;
        this.dessAi = dessAi;
        this.ssilka = ssilka;
        this.uid = uid;
    }

    // Геттеры и сеттеры (обязательны для Firebase)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUrlPic() {
        return urlPic;
    }

    public void setUrlPic(String urlPic) {
        this.urlPic = urlPic;
    }

    public String getDess() {
        return dess;
    }

    public void setDess(String dess) {
        this.dess = dess;
    }

    public String getDessAi() {
        return dessAi;
    }

    public void setDessAi(String dessAi) {
        this.dessAi = dessAi;
    }

    public String getSsilka() {
        return ssilka;
    }

    public void setSsilka(String ssilka) {
        this.ssilka = ssilka;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
    public String getUid(){
        return uid;
    }
}
