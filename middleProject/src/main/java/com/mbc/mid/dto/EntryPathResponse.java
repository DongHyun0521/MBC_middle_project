package com.mbc.mid.dto;

import java.util.List;

public class EntryPathResponse {

    private int spotId;
    private int floor;
    private String zone;
    private int spotNumber;
    private List<String> path;
    private String vehicleNum;

    public EntryPathResponse() {}

    public EntryPathResponse(int spotId, int floor, String zone, int spotNumber, List<String> path) {
        this.spotId = spotId;
        this.floor = floor;
        this.zone = zone;
        this.spotNumber = spotNumber;
        this.path = path;
    }

    public int getSpotId() { return spotId; }
    public void setSpotId(int spotId) { this.spotId = spotId; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public int getSpotNumber() { return spotNumber; }
    public void setSpotNumber(int spotNumber) { this.spotNumber = spotNumber; }

    public List<String> getPath() { return path; }
    public void setPath(List<String> path) { this.path = path; }

    public String getVehicleNum() { return vehicleNum; }
    public void setVehicleNum(String vehicleNum) { this.vehicleNum = vehicleNum; }
}
