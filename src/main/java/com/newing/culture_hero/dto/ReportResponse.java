package com.newing.culture_hero.dto;

public class ReportResponse {
    private String metric;
    private double value;
    public ReportResponse(String metric, double value) {
        this.metric = metric;
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
