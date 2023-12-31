package Controller;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Models.Guests;
import Models.Rooms;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;


public class DashboardController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnRoom;

    @FXML
    private Button btnGuest;

    @FXML
    private Label labelTitle;
    
    @FXML
    private GridPane paneBook;

    @FXML
    private AnchorPane addRoomModal;

    @FXML
    private TextField fieldSearchRoom;
    
    @FXML
    private TableColumn<Rooms, Integer> columnRoom_no;

    @FXML
    private TableColumn<Rooms, String> columnType;

    @FXML
    private TableColumn<Rooms, Integer> columnCapacity;

    @FXML
    private TableColumn<Rooms, String> columnStatus;
    
    @FXML
    private TableColumn<Rooms, String> columnPrice;

    @FXML
    private TableColumn<Rooms, String> columnAction;
    
    @FXML
    private TextField fieldSearchGuest;
    
    @FXML
    private TableView<Guests> guestsTable;

    @FXML
    private TableView<Rooms> tableView;
    
    @FXML
    private GridPane paneGuest;
    
    @FXML
    private TableColumn<Guests, Integer> columnBookingId;

    @FXML
    private TableColumn<Guests, String> columnName;

    @FXML
    private TableColumn<Guests, String> columnAddress;

    @FXML
    private TableColumn<Guests, String> columnPhone;

    @FXML
    private TableColumn<Guests, String> columnIdentityNumber;

    @FXML
    private TableColumn<Guests, Integer> columnRoom;

    @FXML
    private Button button;
    
    @FXML
    private Button closeButton;

    public Connection getConnection() {
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://www.papademas.net:3307/510fp", "fp510", "510");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void executeQuery(String query) {
        Connection conn = getConnection();
        Statement st;
        try {
            st = conn.createStatement();
            st.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Rooms> getRoomsList() {
        ObservableList<Rooms> roomsList = FXCollections.observableArrayList();
        Connection connection = getConnection();
        String query = "SELECT * FROM sanjanadivya_rooms";
        Statement st;
        ResultSet rs;

        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Rooms rooms;
            while (rs.next()) {
                rooms = new Rooms(rs.getInt("Room_no"), rs.getString("Type"), rs.getInt("Capacity"), rs.getString("Status"), rs.getInt("Price"));

                roomsList.add(rooms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomsList;
    }

    // I had nge ArrayList to ObservableList I didn't find another option to do this but this works :)
    public void showRooms() {
        ObservableList<Rooms> list = getRoomsList();

        columnRoom_no.setCellValueFactory(new PropertyValueFactory<Rooms, Integer>("room_no"));
        columnType.setCellValueFactory(new PropertyValueFactory<Rooms, String>("type"));
        columnCapacity.setCellValueFactory(new PropertyValueFactory<Rooms, Integer>("capacity"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<Rooms, String>("Status"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<Rooms, String>("Price"));
        columnAction.setCellValueFactory(new PropertyValueFactory<Rooms, String>("Button"));

        Callback<TableColumn<Rooms, String>, TableCell<Rooms, String>> cellFactory = (params) -> {
            //Make the tableCell containing button
            final TableCell<Rooms, String> cell = new TableCell<Rooms, String>() {
                //override updateItem method
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    //ensure that cell is create only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        //Now we can create the action button
                        Rooms room = (Rooms) getTableView().getItems().get(getIndex());
                        Button bookButton;
                        if (room.getStatus().equals("Booked")) {
                            bookButton = new Button("Deperature");
                            bookButton.setOnAction((ActionEvent event) -> {
                                 Rooms rooms = tableView.getSelectionModel().getSelectedItem();
                                 tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItem());
                                 String query = "UPDATE debayan_rooms SET Status='Available' WHERE Room_no="+rooms.getRoom_no()+"";
                                 executeQuery(query);
                                 
                                 query = "DELETE from debayan_guests WHERE room_no = "+rooms.getRoom_no();
                                 executeQuery(query);
                                 reloadTable();
                            });
                        } else {
                            bookButton = new Button("Book Now");
                            bookButton.setOnAction((ActionEvent event) -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/BookingForm.fxml"));
                                    Parent root1 = (Parent)loader.load();
                                    BookingController bookingController = loader.<BookingController>getController();
                                   
                                    /**
                                     * Get Room Number and convert to string  
                                     */
                                    Rooms rooms = tableView.getSelectionModel().getSelectedItem();
                                    bookingController.setRoomInfo(rooms.getRoom_no(), rooms.getType(), rooms.getPrice());
                                    
                                    Stage stage = new Stage();
                                    stage.initModality(Modality.APPLICATION_MODAL);
                                    stage.initStyle(StageStyle.UNDECORATED);
                                    
                                    Scene scene = new Scene(root1);

                                    stage.setScene(scene);
                                    stage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                        //Attach listener on
                        /*
                        * code
                         */
                        //remember to set the created button to cell
                        setGraphic(bookButton);
                        setText(null);
                    }
                }

            };

            return cell;
        };
        columnAction.setCellFactory(cellFactory);
//        tableView.refresh();
        tableView.setItems(list);
    }
    
    public void searchRoomTable() {
        FilteredList<Rooms> filteredData = new FilteredList<>(getRoomsList(), p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        fieldSearchRoom.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(rooms -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name field in your object with filter.
                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(rooms.getRoom_no()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches Room No.
                } else if (String.valueOf(rooms.getType()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches Type.
                } else if (String.valueOf(rooms.getStatus()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches Type.
                } 

                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        
        SortedList<Rooms> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }
    
    public ObservableList<Guests> getGuestsList() {
        ObservableList<Guests> guestsList = FXCollections.observableArrayList();
        Connection connection = getConnection();
        String query = "SELECT * FROM sanjanadivya_guests";
        Statement st;
        ResultSet rs;

        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Guests guests;
            while (rs.next()) {
                guests = new Guests(rs.getInt("booking_id"), rs.getString("name"), rs.getString("identity_number"), rs.getString("phone"), rs.getInt("room_no"));

                guestsList.add(guests);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return guestsList;
    }

    // I had nge ArrayList to ObservableList I didn't find another option to do this but this works :)
    public void showGuests() {
        ObservableList<Guests> list = getGuestsList();

        columnBookingId.setCellValueFactory(new PropertyValueFactory<Guests, Integer>("bookingId"));
        columnName.setCellValueFactory(new PropertyValueFactory<Guests, String>("name"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<Guests, String>("phone"));
        columnIdentityNumber.setCellValueFactory(new PropertyValueFactory<Guests, String>("identityNumber"));
        columnRoom.setCellValueFactory(new PropertyValueFactory<Guests, Integer>("room"));
        guestsTable.setItems(list);
    }

    public void searchGuestTable() {
        
        
        FilteredList<Guests> filteredData = new FilteredList<>(getGuestsList(), p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        fieldSearchGuest.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(guests -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name field in your object with filter.
                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(guests.getBookingId()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches BookingId.
                } else if (String.valueOf(guests.getName()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches Name.
                } else if (String.valueOf(guests.getRoom()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches room.
                }

                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        
        SortedList<Guests> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator
        sortedData.comparatorProperty().bind(guestsTable.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        guestsTable.setItems(sortedData);
    }
    
    public void reloadTable() {
        showRooms();
        showGuests();
        tableView.refresh();
        guestsTable.refresh();
    }
    
    @FXML
    private void loadStage(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeProgram() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
    
    @FXML
    private void handleNavigation(ActionEvent event) {

        if (event.getSource() == btnRoom) {
            labelTitle.setText("Room");
            paneBook.toFront();
        } else if (event.getSource() == btnGuest) {
            labelTitle.setText("Guest");
            paneGuest.toFront();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        showRooms();
        showGuests();
        
        searchGuestTable();
        searchRoomTable();
    }

}
