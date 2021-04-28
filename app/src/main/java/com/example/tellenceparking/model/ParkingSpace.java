package com.example.tellenceparking.model;

import org.jetbrains.annotations.NotNull;

public class ParkingSpace {

    String id;
    int status;
    String last_update;

    public ParkingSpace(String id, int status, String last_update) {
        this.id = id;
        this.status = status;
        this.last_update = last_update;
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

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    @NotNull
    @Override
    public String toString() {
        return "ParkingSpace{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", last_update='" + last_update + '\'' +
                '}';
    }
}
