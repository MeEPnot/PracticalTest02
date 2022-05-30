package ro.pub.cs.systems.eim.practicaltest02;


import java.time.LocalDateTime;

public class CurrencyRateData {
    public String rate;
    public LocalDateTime reqTime;

    public CurrencyRateData(String rate, LocalDateTime reqTime) {
        this.rate = rate;
        this.reqTime = reqTime;
    }
}
