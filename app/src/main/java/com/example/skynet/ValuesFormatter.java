package com.example.skynet;

import android.content.Context;

import com.lf.skynet_service.Units;

import java.text.NumberFormat;

public class ValuesFormatter {
    private static final double MILES_PER_KILOMETER = 0.621371;
    private static final double FEET_PER_METER = 3.28084;


    private final Context mContext;
    private final NumberFormat caloriesFormat;
    private final NumberFormat distanceFormat;
    private final NumberFormat distanceClimbedFormat;
    private final NumberFormat speedFormat;
    private final NumberFormat inclineFormat;
    private final NumberFormat heartRateFormat;

    private final String invalidDataStr;
    private Units units = Units.INVALID;
    private double distanceClimbedConversion;
    private double distanceConversion;
    private double speedConversion;
    private String distanceClimbedStrFormat;
    private String distanceStrFormat;
    private String speedStrFormat;

    public ValuesFormatter(Context context) {
        mContext = context.getApplicationContext();

        this.invalidDataStr = mContext.getString(R.string.invalid_data_value);

        caloriesFormat = NumberFormat.getIntegerInstance();

        distanceFormat = NumberFormat.getInstance();
        distanceFormat.setMinimumIntegerDigits(1);
        distanceFormat.setMinimumFractionDigits(1);
        distanceFormat.setMaximumFractionDigits(1);

        distanceClimbedFormat = NumberFormat.getIntegerInstance();

        speedFormat = NumberFormat.getInstance();
        speedFormat.setMinimumIntegerDigits(1);
        speedFormat.setMinimumFractionDigits(1);
        speedFormat.setMaximumFractionDigits(1);

        inclineFormat = NumberFormat.getPercentInstance();
        inclineFormat.setMaximumFractionDigits(1);
        inclineFormat.setMinimumFractionDigits(1);

        heartRateFormat = NumberFormat.getIntegerInstance();

        setUnits(Units.METRIC);
    }

    public void setUnits(Units units) {
        if (units != this.units && units != Units.INVALID) {
            this.units = units;
            if (units == Units.IMPERIAL) {
                distanceClimbedStrFormat = mContext.getString(R.string.distance_format_ft);
                distanceStrFormat = mContext.getString(R.string.distance_format_miles);
                speedStrFormat = mContext.getString(R.string.speed_format_mph);
                distanceClimbedConversion = FEET_PER_METER;
                distanceConversion = MILES_PER_KILOMETER;
                speedConversion = MILES_PER_KILOMETER;
            } else {
                distanceClimbedStrFormat = mContext.getString(R.string.distance_format_meters);
                distanceStrFormat = mContext.getString(R.string.distance_format_km);
                speedStrFormat = mContext.getString(R.string.speed_format_kph);
                distanceClimbedConversion = 1.0;
                distanceConversion = 1.0;
                speedConversion = 1.0;
            }
        }
    }

    public String getSpeedString(Double speedKph) {
        if (speedKph != null) {
            return String.format(speedStrFormat, speedFormat.format(speedKph * speedConversion));
        } else {
            return invalidDataStr;
        }
    }

    public String getDistanceString(Double distanceKm) {
        if (distanceKm != null) {
            return String.format(distanceStrFormat, distanceFormat.format(distanceKm * distanceConversion));
        } else {
            return invalidDataStr;
        }
    }

    public String getDistanceClimbedString(Double distanceClimbedMeters) {
        if (distanceClimbedMeters != null) {
            return String.format(distanceClimbedStrFormat, distanceClimbedFormat.format(distanceClimbedMeters * distanceClimbedConversion));
        } else {
            return invalidDataStr;
        }
    }

    public String getTimeString(Integer secondsValue) {
        if (secondsValue != null) {
            int seconds = secondsValue.intValue();
            int hours = seconds / 3600;
            seconds -= (hours * 3600);
            int minutes = (seconds / 60);
            seconds -= (minutes * 60);

            if (hours > 0) {
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format("%02d:%02d", minutes, seconds);
            }
        } else {
            return invalidDataStr;
        }
    }

    public String getCaloriesString(Integer calories) {
        if (calories != null) {
            return caloriesFormat.format(calories);
        } else {
            return invalidDataStr;
        }
    }

    public String getHeartRateString(Integer heartRateBpm) {
        if (heartRateBpm != null) {
            return heartRateFormat.format(heartRateBpm);
        } else {
            return invalidDataStr;
        }
    }

    public String getInclineString(Double inclinePercent) {
        if (inclinePercent != null) {
            return inclineFormat.format(inclinePercent / 100.0);
        } else {
            return invalidDataStr;
        }
    }
}
