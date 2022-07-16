package com.engine.test.itemdetail;

import java.io.Serializable;

public class ItemDetail implements Serializable {
    private final String name, address, phone, price;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getPrice() {
        return price;
    }

    public ItemDetail(String name, String address, String phone, String price) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.price = price;
    }
}
