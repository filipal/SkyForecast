package com.filipal.skyforecast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Main controller class for SkyForecast.
 * Handles user interactions and communicates with weather APIs.
 */
public class MainController {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String GEO_API_URL = "http://api.openweathermap.org/geo/1.0/direct";
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/3.0/onecall";

    // FXML injected fields
    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField locationField;

    @FXML
    private Button searchButton;

    @FXML
    private Button historyButton;

    @FXML
    private ComboBox<String> unitComboBox;

    @FXML
    private Label locationLabel;

    @FXML
    private ImageView weatherIcon;

    @FXML
    private Label weatherDescription;

    @FXML
    private Label currentTemp;

    @FXML
    private Label currentUnit;

    @FXML
    private Label currentDate;

    @FXML
    private Label day1, day2, day3, day4, day5, day6, day7;

    @FXML
    private ImageView weatherIcon1, weatherIcon2, weatherIcon3, weatherIcon4, weatherIcon5, weatherIcon6, weatherIcon7;

    @FXML
    private Label maxTemp1, maxTemp2, maxTemp3, maxTemp4, maxTemp5, maxTemp6, maxTemp7;

    @FXML
    private Label minTempValue, maxTempValue;

    @FXML
    private Label minTempUnit, maxTempUnit;

    @FXML
    private ImageView minTempIcon, maxTempIcon;

    @FXML
    private Label pressureValue, pressureUnit;

    @FXML
    private Label humidityValue;

    @FXML
    private Label windSpeedValue, windSpeedUnit;

    private List<HistoryRecord> searchHistory = new ArrayList<>();

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    public void initialize() {
        // Setting event handlers for buttons and combo box
        searchButton.setOnAction(event -> searchWeather());
        historyButton.setOnAction(event -> showHistory());
        unitComboBox.setOnAction(event -> changeUnit());
        unitComboBox.getItems().addAll("°C", "°F");
        unitComboBox.setValue("°C");

        // Setting initial background image
        rootPane.setStyle("-fx-background-image: url('" + getClass().getResource("/images/bg.jpg").toExternalForm() + "'); " +
                "-fx-background-size: cover;");

        // Setting icons for temperature
        minTempIcon.setImage(new Image(getClass().getResourceAsStream("/images/thermometer.png")));
        maxTempIcon.setImage(new Image(getClass().getResourceAsStream("/images/thermometer.png")));

        // Setting the current date
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE d'th'", Locale.ENGLISH);
        currentDate.setText(today.format(dateFormatter));
    }

    /**
     * Displays an alert dialog with a given title and message.
     *
     * @param title the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Searches for the weather information for the specified location.
     * Fetches coordinates from the location and then fetches weather data.
     */
    private void searchWeather() {
        String location = locationField.getText();
        if (location == null || location.trim().isEmpty()) {
            showAlert("Invalid Input", "Please enter a location.");
            return; // If location is not entered, do nothing
        }
        try {
            LocationCoordinates coordinates = fetchCoordinates(location);
            if (coordinates != null) {
                String units = unitComboBox.getValue().equals("°C") ? "metric" : "imperial";
                WeatherData weatherData = fetchWeatherData(coordinates.getLat(), coordinates.getLon(), coordinates.getLocationName(), units);
                updateWeatherUI(weatherData);
                addSearchHistory(capitalizeCityName(location), weatherData);
            } else {
                showAlert("Location Not Found", "Unable to find the specified location.");
            }
        } catch (IOException e) {
            showAlert("API Error", "Failed to fetch weather data. Please try again later.");
            e.printStackTrace();
        }
    }

    /**
     * Shows the search history in a new window.
     */
    private void showHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/filipal/skyforecast/HistoryView.fxml"));
            Parent historyRoot = loader.load();

            HistoryController historyController = loader.getController();
            historyController.setHistoryList(searchHistory);

            Stage historyStage = new Stage();
            historyStage.initModality(Modality.APPLICATION_MODAL);
            historyStage.initStyle(StageStyle.UTILITY);
            Scene historyScene = new Scene(historyRoot);
            historyStage.setScene(historyScene);
            historyStage.setTitle("Search History");
            historyStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a search record to the search history.
     *
     * @param location the location searched
     * @param weatherData the weather data for the location
     */
    private void addSearchHistory(String location, WeatherData weatherData) {
        String formattedLocation = capitalizeCityName(location);
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now());
        String temperature = String.format("%.1f", weatherData.getCurrentTemp()) + unitComboBox.getValue();
        String conditions = weatherData.getCurrentWeatherDescription();
        searchHistory.add(new HistoryRecord(formattedLocation, timestamp, temperature, conditions));
    }

    /**
     * Changes the unit of measurement for temperature and fetches weather data again.
     */
    private void changeUnit() {
        searchWeather();
    }

    /**
     * Fetches coordinates for a given location by calling the Geo API.
     *
     * @param location the name of the location
     * @return LocationCoordinates object containing latitude, longitude, and location name
     * @throws IOException if there is an issue with the API request
     */
    private LocationCoordinates fetchCoordinates(String location) throws IOException {
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String url = String.format("%s?q=%s&limit=1&appid=%s", GEO_API_URL, encodedLocation, API_KEY);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(json);

                if (rootNode.isArray() && rootNode.size() > 0) {
                    JsonNode firstResult = rootNode.get(0);
                    double lat = firstResult.path("lat").asDouble();
                    double lon = firstResult.path("lon").asDouble();
                    String name = firstResult.path("name").asText();
                    return new LocationCoordinates(lat, lon, name);
                }
            }
        }
        return null;
    }

    /**
     * Fetches weather data for the given coordinates by calling the Weather API.
     *
     * @param lat latitude of the location
     * @param lon longitude of the location
     * @param locationName name of the location
     * @param units units of measurement for temperature
     * @return WeatherData object containing weather information
     * @throws IOException if there is an issue with the API request
     */
    private WeatherData fetchWeatherData(double lat, double lon, String locationName, String units) throws IOException {
        String url = String.format("%s?lat=%f&lon=%f&units=%s&appid=%s", WEATHER_API_URL, lat, lon, units, API_KEY);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(json);

                // Parsing JSON data from the API response
                WeatherData weatherData = new WeatherData();
                weatherData.setCurrentTemp(rootNode.path("current").path("temp").asDouble());
                weatherData.setCurrentWeatherDescription(rootNode.path("current").path("weather").get(0).path("description").asText());
                weatherData.setCurrentWeatherCondition(rootNode.path("current").path("weather").get(0).path("main").asText());
                weatherData.setLocationName(locationName);

                weatherData.setMinTemp(rootNode.path("daily").get(0).path("temp").path("min").asDouble());
                weatherData.setMaxTemp(rootNode.path("daily").get(0).path("temp").path("max").asDouble());
                weatherData.setPressure(rootNode.path("current").path("pressure").asInt());
                weatherData.setHumidity(rootNode.path("current").path("humidity").asInt());
                weatherData.setWindSpeed(rootNode.path("current").path("wind_speed").asDouble());
                weatherData.setTimezone(rootNode.path("timezone").asText());

                // Filling data for the next 7 days
                for (int i = 0; i < 7; i++) {
                    weatherData.getDailyConditions()[i] = rootNode.path("daily").get(i).path("weather").get(0).path("main").asText().toLowerCase();
                    weatherData.getDailyMaxTemps()[i] = rootNode.path("daily").get(i).path("temp").path("max").asDouble();
                    weatherData.getDaysOfWeek()[i] = System.currentTimeMillis() / 1000L + (i * 86400);
                }

                return weatherData;
            }
        }
    }

    /**
     * Updates the user interface with the fetched weather data.
     *
     * @param weatherData the weather data to display
     */
    private void updateWeatherUI(WeatherData weatherData) {
        // Setting the location name
        locationLabel.setText(weatherData.getLocationName());

        // Setting the current weather information
        currentTemp.setText(String.format("%.1f", weatherData.getCurrentTemp()));
        currentUnit.setText(unitComboBox.getValue());
        weatherDescription.setText(weatherData.getCurrentWeatherDescription());

        // Setting the current weather icon
        String currentCondition = weatherData.getCurrentWeatherCondition().toLowerCase();
        weatherIcon.setImage(new Image(getClass().getResourceAsStream(getLargeIconPath(currentCondition))));

        // Setting min and max temperature values
        minTempValue.setText(String.format("%.1f", weatherData.getMinTemp()));
        minTempUnit.setText(unitComboBox.getValue());
        maxTempValue.setText(String.format("%.1f", weatherData.getMaxTemp()));
        maxTempUnit.setText(unitComboBox.getValue());

        // Setting pressure, humidity, and wind speed values
        pressureValue.setText(String.valueOf(weatherData.getPressure()));
        pressureUnit.setText("hPa");
        humidityValue.setText(String.valueOf(weatherData.getHumidity()));
        windSpeedValue.setText(String.format("%.1f", weatherData.getWindSpeed()));
        windSpeedUnit.setText(unitComboBox.getValue().equals("°C") ? "m/s" : "mph");

        // Updating daily forecasts
        LocalDate today = LocalDate.now();
        Label[] dayLabels = {day1, day2, day3, day4, day5, day6, day7};
        ImageView[] weatherIcons = {weatherIcon1, weatherIcon2, weatherIcon3, weatherIcon4, weatherIcon5, weatherIcon6, weatherIcon7};
        Label[] maxTempLabels = {maxTemp1, maxTemp2, maxTemp3, maxTemp4, maxTemp5, maxTemp6, maxTemp7};

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("E", Locale.ENGLISH);

        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i + 1);
            dayLabels[i].setText(date.format(dayFormatter).toUpperCase());
            String iconPath = getIconPath(weatherData.getDailyConditions()[i]);
            try (InputStream iconStream = getClass().getResourceAsStream(iconPath)) {
                if (iconStream != null) {
                    weatherIcons[i].setImage(new Image(iconStream));
                } else {
                    weatherIcons[i].setImage(new Image(getClass().getResourceAsStream("/images/default.png")));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            maxTempLabels[i].setText(String.format("%.1f", weatherData.getDailyMaxTemps()[i]));
        }

        // Setting the background image based on the time of day
        ZoneId zoneId = ZoneId.of(weatherData.getTimezone());
        LocalTime localTime = LocalTime.now(zoneId);
        boolean isNight = localTime.isAfter(LocalTime.of(18, 0)) || localTime.isBefore(LocalTime.of(6, 0));
        String backgroundPath = isNight ? "/images/bg_night.jpg" : "/images/bg_day.jpg";
        rootPane.setStyle("-fx-background-image: url('" + getClass().getResource(backgroundPath).toExternalForm() + "'); " +
                "-fx-background-size: cover;");

        // Applying CSS class for night mode
        if (isNight) {
            rootPane.getStyleClass().add("night-mode");
        } else {
            rootPane.getStyleClass().remove("night-mode");
        }

        // Changing text color based on the time of day
        String textColor = isNight ? "white" : "black";
        changeTextColor(textColor);
    }

    /**
     * Changes the text color of various UI components.
     *
     * @param color the color to set
     */
    private void changeTextColor(String color) {
        locationLabel.setStyle("-fx-text-fill: " + color + ";");
        weatherDescription.setStyle("-fx-text-fill: " + color + ";");
        currentTemp.setStyle("-fx-text-fill: " + color + ";");
        currentUnit.setStyle("-fx-text-fill: " + color + ";");
        currentDate.setStyle("-fx-text-fill: " + color + ";");
        day1.setStyle("-fx-text-fill: " + color + ";");
        day2.setStyle("-fx-text-fill: " + color + ";");
        day3.setStyle("-fx-text-fill: " + color + ";");
        day4.setStyle("-fx-text-fill: " + color + ";");
        day5.setStyle("-fx-text-fill: " + color + ";");
        day6.setStyle("-fx-text-fill: " + color + ";");
        day7.setStyle("-fx-text-fill: " + color + ";");
        maxTemp1.setStyle("-fx-text-fill: " + color + ";");
        maxTemp2.setStyle("-fx-text-fill: " + color + ";");
        maxTemp3.setStyle("-fx-text-fill: " + color + ";");
        maxTemp4.setStyle("-fx-text-fill: " + color + ";");
        maxTemp5.setStyle("-fx-text-fill: " + color + ";");
        maxTemp6.setStyle("-fx-text-fill: " + color + ";");
        maxTemp7.setStyle("-fx-text-fill: " + color + ";");
        minTempValue.setStyle("-fx-text-fill: " + color + ";");
        maxTempValue.setStyle("-fx-text-fill: " + color + ";");
        pressureValue.setStyle("-fx-text-fill: " + color + ";");
        pressureUnit.setStyle("-fx-text-fill: " + color + ";");
        humidityValue.setStyle("-fx-text-fill: " + color + ";");
        windSpeedValue.setStyle("-fx-text-fill: " + color + ";");
        windSpeedUnit.setStyle("-fx-text-fill: " + color + ";");
        minTempUnit.setStyle("-fx-text-fill: " + color + ";");
        maxTempUnit.setStyle("-fx-text-fill: " + color + ";");

        // Changing text color for static labels
        Label[] staticLabels = {
                new Label("Pressure:"), new Label("Humidity:"), new Label("Wind Speed:"),
                new Label("%"), new Label("Min. Temp."), new Label("Max. Temp.")
        };
        for (Label label : staticLabels) {
            label.setStyle("-fx-text-fill: " + color + ";");
        }
    }

    /**
     * Gets the path to the weather icon based on the weather condition.
     *
     * @param condition the weather condition
     * @return the path to the weather icon
     */
    private String getIconPath(String condition) {
        switch (condition) {
            case "clear":
                return "/images/sun_s.png";
            case "clouds":
                return "/images/cloud_s.png";
            case "rain":
                return "/images/rain_s.png";
            case "snow":
                return "/images/snow_s.png";
            case "thunderstorm":
                return "/images/thunderstorm_s.png";
            case "drizzle":
                return "/images/drizzle_s.png";
            case "mist":
            case "smoke":
            case "haze":
            case "dust":
            case "fog":
            case "sand":
            case "ash":
            case "squall":
            case "tornado":
                return "/images/mist_s.png";
            default:
                return "/images/default.png";  // Default icon for unknown conditions
        }
    }

    /**
     * Gets the path to the large weather icon based on the weather condition.
     *
     * @param condition the weather condition
     * @return the path to the large weather icon
     */
    private String getLargeIconPath(String condition) {
        switch (condition) {
            case "clear":
                return "/images/sun_h.png";
            case "clouds":
                return "/images/cloud_h.png";
            case "rain":
                return "/images/rain_h.png";
            case "snow":
                return "/images/snow_h.png";
            case "thunderstorm":
                return "/images/thunderstorm_h.png";
            case "drizzle":
                return "/images/drizzle_h.png";
            case "mist":
            case "smoke":
            case "haze":
            case "dust":
            case "fog":
            case "sand":
            case "ash":
            case "squall":
            case "tornado":
                return "/images/mist_h.png";
            default:
                return "/images/default.png";  // Default icon for unknown conditions
        }
    }

    /**
     * Capitalizes the first letter of each word in the city name.
     *
     * @param cityName the city name to capitalize
     * @return the capitalized city name
     */
    private String capitalizeCityName(String cityName) {
        String[] words = cityName.toLowerCase().split(" ");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    // Helper class to represent location coordinates
    private static class LocationCoordinates {
        private final double lat;
        private final double lon;
        private final String locationName;

        public LocationCoordinates(double lat, double lon, String locationName) {
            this.lat = lat;
            this.lon = lon;
            this.locationName = locationName;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        public String getLocationName() {
            return locationName;
        }
    }

    // Helper class to represent weather data
    private static class WeatherData {
        private double currentTemp;
        private double minTemp;
        private double maxTemp;
        private String currentWeatherDescription;
        private String currentWeatherCondition;
        private int pressure;
        private int humidity;
        private double windSpeed;
        private long[] daysOfWeek = new long[7];
        private String[] dailyConditions = new String[7];
        private double[] dailyMaxTemps = new double[7];
        private String timezone;
        private String locationName;

        // getters and setters
        public double getCurrentTemp() {
            return currentTemp;
        }

        public void setCurrentTemp(double currentTemp) {
            this.currentTemp = currentTemp;
        }

        public double getMinTemp() {
            return minTemp;
        }

        public void setMinTemp(double minTemp) {
            this.minTemp = minTemp;
        }

        public double getMaxTemp() {
            return maxTemp;
        }

        public void setMaxTemp(double maxTemp) {
            this.maxTemp = maxTemp;
        }

        public String getCurrentWeatherDescription() {
            return currentWeatherDescription;
        }

        public void setCurrentWeatherDescription(String currentWeatherDescription) {
            this.currentWeatherDescription = currentWeatherDescription;
        }

        public String getCurrentWeatherCondition() {
            return currentWeatherCondition;
        }

        public void setCurrentWeatherCondition(String currentWeatherCondition) {
            this.currentWeatherCondition = currentWeatherCondition;
        }

        public int getPressure() {
            return pressure;
        }

        public void setPressure(int pressure) {
            this.pressure = pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(double windSpeed) {
            this.windSpeed = windSpeed;
        }

        public long[] getDaysOfWeek() {
            return daysOfWeek;
        }

        public void setDaysOfWeek(long[] daysOfWeek) {
            this.daysOfWeek = daysOfWeek;
        }

        public String[] getDailyConditions() {
            return dailyConditions;
        }

        public void setDailyConditions(String[] dailyConditions) {
            this.dailyConditions = dailyConditions;
        }

        public double[] getDailyMaxTemps() {
            return dailyMaxTemps;
        }

        public void setDailyMaxTemps(double[] dailyMaxTemps) {
            this.dailyMaxTemps = dailyMaxTemps;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }
    }
}
