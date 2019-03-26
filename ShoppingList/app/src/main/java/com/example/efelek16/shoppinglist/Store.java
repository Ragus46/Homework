package com.example.efelek16.shoppinglist;

import android.location.Location;

/**
 * Created by efelek16 on 12.03.2019.
 */

public class Store {
    String Name;
    Location location;

    public Store(String name, Location location) {
        Name = name;
        this.location = location;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return Name;
    }
}
