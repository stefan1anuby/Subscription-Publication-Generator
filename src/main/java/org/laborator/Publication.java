package org.laborator;

import java.time.LocalDate;

class Publication {
    int stationId;
    String city;
    int temp;
    double rain;
    int wind;
    String direction;
    LocalDate date;

    @Override
    public String toString() {
        return String.format("{(stationid,%d);(city,\"%s\");(temp,%d);(rain,%.1f);(wind,%d);(direction,\"%s\");(date,%s)}",
                stationId, city, temp, rain, wind, direction, date);
    }
}
