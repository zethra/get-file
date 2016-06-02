package com.zethratech.getfile;

public class IpInterface {
    private String name, address;

    public IpInterface(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return getName() + " - " + getAddress();
    }
}
