package com.yablonka.marviq.DB.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Runtime {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int runtimeId;

    @Column(name = "machine_name")
    private String machineName;

    @Column(name = "datetime")
    private String dateTime;

    @Column(name = "isrunning")
    private int isRunning;

    public Runtime() {
    }

    public Runtime(String machineName, String dateTime, int isRunning) {
        this.machineName = machineName;
        this.dateTime = dateTime;
        this.isRunning = isRunning;
    }

    public int getRuntimeId() {
        return runtimeId;
    }

    public void setRuntimeId(int runtimeId) {
        this.runtimeId = runtimeId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getIsRunning() {
        return isRunning;
    }

    public void setIsRunning(int isRunning) {
        this.isRunning = isRunning;
    }
}
