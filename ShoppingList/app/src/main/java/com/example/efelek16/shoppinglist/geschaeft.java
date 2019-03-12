package com.example.efelek16.shoppinglist;

/**
 * Created by efelek16 on 12.03.2019.
 */

public class geschaeft {
    String Name;

    public geschaeft(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString() {
        return Name;
    }
}
