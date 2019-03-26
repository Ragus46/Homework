package com.example.efelek16.shoppinglist;

import android.location.Location;

/**
 * Created by efelek16 on 12.03.2019.
 */

public class Model {
    String geschaeft;
    String stueck;
    String eintrag;
    Location loc;

    public Model(String geschaeft, String eintrag,String stueck,Location loc) {
        this.geschaeft = geschaeft;
        this.stueck = stueck;
        this.eintrag = eintrag;
        this.loc = loc;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public String getGeschaeft() {
        return geschaeft;
    }

    public void setGeschaeft(String geschaeft) {
        this.geschaeft = geschaeft;
    }

    public String getStueck() {
        return stueck;
    }

    public void setStueck(String stueck) {
        this.stueck = stueck;
    }

    public String getEintrag() {
        return eintrag;
    }

    public void setEintrag(String eintrag) {
        this.eintrag = eintrag;
    }

    @Override
    public String toString() {
        return eintrag+" "+stueck;
    }
}
