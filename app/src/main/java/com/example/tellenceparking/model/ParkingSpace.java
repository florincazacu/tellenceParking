package com.example.tellenceparking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

public class ParkingSpace {

    String id;
    int status;
    @JsonProperty("last_update")
    String lastUpdate;

    public ParkingSpace(String id, int status, String lastUpdate) {
        this.id = id;
        this.status = status;
        this.lastUpdate = lastUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @NotNull
    @Override
    public String toString() {
        return "ParkingSpace{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", lastUpdate='" + lastUpdate + '\'' +
                '}';
    }
}
