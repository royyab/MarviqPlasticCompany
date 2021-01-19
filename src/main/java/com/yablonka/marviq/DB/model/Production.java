package com.yablonka.marviq.DB.model;

import javax.persistence.*;

@Entity
public class Production {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int productionId;

    @Column(name = "machine_name")
    private String machineName;

    @Column(name = "variable_name")
    private String variableName;

    @Column(name = "datetime_from")
    private String fromDateTime;

    @Column(name = "datetime_to")
    private String toDateTime;

    @Column(name = "value")
    private int value;

    public Production() {
    }

    public Production(String machineName, String variableName, String fromDateTime, String toDateTime, int value) {
        this.machineName = machineName;
        this.variableName = variableName;
        this.fromDateTime = fromDateTime;
        this.toDateTime = toDateTime;
        this.value = value;
    }

    public int getProductionId() {
        return productionId;
    }

    public void setProductionId(int productionId) {
        this.productionId = productionId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getFromDateTime() {
        return fromDateTime;
    }

    public void setFromDateTime(String fromDateTime) {
        this.fromDateTime = fromDateTime;
    }

    public String getToDateTime() {
        return toDateTime;
    }

    public void setToDateTime(String toDateTime) {
        this.toDateTime = toDateTime;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
