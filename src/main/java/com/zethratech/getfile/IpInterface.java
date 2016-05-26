package com.zethratech.getfile;

public class IpInterface {
    private String name, adress;

    public IpInterface(){}

    public IpInterface(String name, String adress) {
        this.name = name;
        this.adress = adress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    @Override
    public String toString() {
        return getName() + " - " + getAdress();
    }
}
