package com.filipal.skyforecast;

/**
 * Class representing a record of a weather search history.
 */
public class HistoryRecord {
    // Private fields to store the details of a weather search history record
    private String location;
    private String timestamp;
    private String temperature;
    private String conditions;

    /**
     * Constructor to initialize a HistoryRecord object with the given details.
     *
     * @param location    the name of the location for the weather search
     * @param timestamp   the timestamp when the search was made
     * @param temperature the temperature recorded during the search
     * @param conditions  the weather conditions recorded during the search
     */
    public HistoryRecord(String location, String timestamp, String temperature, String conditions) {
        this.location = location;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.conditions = conditions;
    }

    /**
     * Gets the location of the weather search.
     *
     * @return the location of the weather search
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the weather search.
     *
     * @param location the new location of the weather search
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the timestamp of the weather search.
     *
     * @return the timestamp of the weather search
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the weather search.
     *
     * @param timestamp the new timestamp of the weather search
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the temperature recorded during the weather search.
     *
     * @return the temperature recorded during the weather search
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     * Sets the temperature recorded during the weather search.
     *
     * @param temperature the new temperature recorded during the weather search
     */
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    /**
     * Gets the weather conditions recorded during the weather search.
     *
     * @return the weather conditions recorded during the weather search
     */
    public String getConditions() {
        return conditions;
    }

    /**
     * Sets the weather conditions recorded during the weather search.
     *
     * @param conditions the new weather conditions recorded during the weather search
     */
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
}
