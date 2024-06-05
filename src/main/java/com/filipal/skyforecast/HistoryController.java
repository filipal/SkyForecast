package com.filipal.skyforecast;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * Controller class for managing the search history table view.
 */
public class HistoryController {

    @FXML
    private TableView<HistoryRecord> historyTable; // TableView to display the search history

    @FXML
    private TableColumn<HistoryRecord, String> locationColumn; // Column for location in the search history

    @FXML
    private TableColumn<HistoryRecord, String> timestampColumn; // Column for timestamp in the search history

    @FXML
    private TableColumn<HistoryRecord, String> temperatureColumn; // Column for temperature in the search history

    @FXML
    private TableColumn<HistoryRecord, String> conditionsColumn; // Column for weather conditions in the search history

    private ObservableList<HistoryRecord> historyList = FXCollections.observableArrayList(); // Observable list to store the search history records

    /**
     * Sets the search history list to be displayed in the TableView.
     *
     * @param searchHistory the list of search history records
     */
    public void setHistoryList(List<HistoryRecord> searchHistory) {
        historyList.setAll(searchHistory); // Update the observable list with the given search history
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    public void initialize() {
        // Set cell value factories for each column to map the property names of HistoryRecord
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        conditionsColumn.setCellValueFactory(new PropertyValueFactory<>("conditions"));

        // Bind the observable list to the TableView
        historyTable.setItems(historyList);
    }
}
