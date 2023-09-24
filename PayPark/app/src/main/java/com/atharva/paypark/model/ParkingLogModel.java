package com.atharva.paypark.model;

public class ParkingLogModel {
    private String vehicle;
    private String entry;
    private String exit;

    public ParkingLogModel() {
    }

    public ParkingLogModel(String vehicle, String entry, String exit) {
        this.vehicle = vehicle;
        this.entry = entry;
        this.exit = exit;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getEntry() {
        return entry;
    }

    public String getExit() {
        return exit;
    }
}
