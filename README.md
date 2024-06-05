# SkyForecast - Weather Information App

SkyForecast is a Java-based weather information application that provides real-time weather updates for a specified location. The application features a user-friendly graphical user interface (GUI) developed using JavaFX, integrates with the OpenWeatherMap API for fetching weather data, and includes features like unit conversion, error handling, search history, and dynamic backgrounds based on the time of day.

## Features

- **API Integration**: Fetches real-time weather data using the OpenWeatherMap API.
- **User-Friendly GUI**: Developed using JavaFX, allowing users to input a location and view weather details.
- **Weather Information Display**: Shows temperature, humidity, wind speed, and weather conditions for the specified location.
- **Icon Representation**: Displays appropriate icons for various weather conditions.
- **Forecast Display**: Provides a short-term weather forecast for the next 7 days.
- **Unit Conversion**: Allows switching between Celsius and Fahrenheit for temperature and different units for wind speed.
- **Error Handling**: Properly handles invalid input and API request failures.
- **Search History**: Tracks recent weather searches with timestamps and allows users to view their search history.
- **Dynamic Backgrounds**: Changes background images based on the time of day (day/night).

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven (optional, for dependency management)

### Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/SkyForecast.git
    cd SkyForecast
    ```

2. **Open the project in your preferred IDE**:
    - Ensure your IDE supports JavaFX (e.g., IntelliJ IDEA, Eclipse with e(fx)clipse plugin).

3. **Add the OpenWeatherMap API Key**:
    - Register and get your API key from [OpenWeatherMap](https://openweathermap.org/api).
    - Open `MainController.java` and replace the placeholder API key with your actual API key:
      ```java
      private static final String API_KEY = "YOUR_API_KEY_HERE";
      ```

4. **Run the application**:
    - Use your IDE's run configuration to start the `MainApp` class.

## Usage

1. **Enter Location**: Input the name of the city or location you want to get weather information for in the text field.
2. **Select Unit**: Choose the desired temperature unit (Celsius or Fahrenheit) from the drop-down menu.
3. **Search Weather**: Click the "Search" button to fetch and display weather information.
4. **View Search History**: Click the "History" button to view your recent weather searches.
5. **Dynamic Backgrounds**: Observe the background image change based on the time of day (day/night).

## Project Structure

- **src/main/java/com/filipal/skyforecast**: Contains the Java source files.
    - **MainApp.java**: The main class to start the JavaFX application.
    - **MainController.java**: The controller class for the main view, handles API requests and UI updates.
    - **HistoryController.java**: The controller class for the history view, handles the display of search history.
    - **HistoryRecord.java**: A model class representing a single record of the search history.
    - **WeatherData.java**: A model class representing the weather data fetched from the API.
    - **LocationCoordinates.java**: A model class representing the geographical coordinates of a location.
- **src/main/resources/com/filipal/skyforecast**: Contains FXML files and other resources.
    - **MainView.fxml**: FXML file defining the main view layout.
    - **HistoryView.fxml**: FXML file defining the history view layout.
- **src/main/resources/images**: Contains image files used in the application.
    - **bg.jpg, bg_day.jpg, bg_night.jpg**: Background images.
    - **weather condition icons**: Icons representing different weather conditions.
- **application.css**: CSS file defining the styles for the application.

## Error Handling

- The application displays error alerts for invalid location input and failed API requests.
- Error messages guide users to correct their input or try again later.

## License



## Acknowledgments

- [OpenWeatherMap](https://openweathermap.org/api) for providing the weather API.
- JavaFX for the GUI framework.
