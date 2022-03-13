package com.gihara;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

public class Customer implements Serializable {
    private String id;
    private String name;
    private String address;
    private byte[] pictureBytes;

    public Customer() {
    }

    public Customer(String id, String name, String address, byte[] pictureBytes) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.pictureBytes = pictureBytes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getPictureBytes() { return pictureBytes; }

    public void setPictureBytes(byte[] picture) { this.pictureBytes = pictureBytes; }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", pictureBytes=" + Arrays.toString(pictureBytes) +
                '}';
    }

}
