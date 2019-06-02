package com.huadong.spoon.model;

import java.io.Serializable;

/**
 * @author jinjinhui
 * @date 2019/5/29
 */
public class GpsData implements Serializable {

    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
