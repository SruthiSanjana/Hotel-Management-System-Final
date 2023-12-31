package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import Models.Rooms;
import Models.Users;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author akshay
 */
public class AdminController implements Initializable {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnRoom;

    @FXML
    private Button btnUsers;

    @FXML
    private Label labelTitle;

    @FXML
    private Pane paneAddNewRoom;

    @FXML
    private TextField fieldRoomNumber;

    @FXML
    private TextField fieldRoomType;

    @FXML
    private TextField fieldRoomCapacity;

    @FXML
    private Button btnSaveNewRoom;

    @FXML
    private Button btnCancelAddNewRoom;
    
    @FXML
    private Label labelErrorRoom;

    @FXML
    private GridPane paneRoom;
    
    @FXML
    private TextField fieldSearchRoom;

    @FXML
    private Button btnAddNewRoom;

    @FXML
    private TableView<Rooms> tableView;

    @FXML
    private TableColumn<Rooms, Integer> columnRoom_no;

    @FXML
    private TableColumn<Rooms, String> columnType;

    @FXML
    private TableColumn<Rooms, Integer> columnCapacity;

    @FXML
    private Pane paneAddNewUser;

    @FXML
    private TextField fieldName;

    @FXML
    private TextField fieldUsername;

    @FXML
    private TextField fieldSearchUser;
    
    @FXML
    private Button btnSaveNewUser;
    
    @FXML
    private Button btnCancelAddNewUser;

    @FXML
    private Label labelErrorUser;

    @FXML
    private PasswordField fieldPassword;

    @FXML
    private ComboBox<String> comboBoxRole;
    
    @FXML
    private GridPane paneUser;
    
    @FXML
    private TableView<Users> tableViewUser;

    @FXML
    private TableColumn<Users, Integer> columnId;

    @FXML
    private TableColumn<Users, String> columnName;

    @FXML
    private TableColumn<Users, String> columnUsername;

    @FXML
    private TableColumn<Users, Integer> columnRole;
    
    @FXML
    private TextField fieldPrice;
    
    @FXML
    private Button closeButton;
    /**
     * Initializes the controller class.
     */
    
    ObservableList<String> roleList = FXCollections.observableArrayList("User", "Admin");
    
    public Connection getConnection() {
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://www.papademas.net:3307/510fp", "fp510", "510");
            return conn;
        } catch (SQLException e) {
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
    
    public void showRooms() {
        ObservableList<Rooms> list = getRoomsList();

        columnRoom_no.setCellValueFactory(new PropertyValueFactory<Rooms, Integer>("room_no"));
        columnType.setCellValueFactory(new PropertyValueFactory<Rooms, String>("type"));
        columnCapacity.setCellValueFactory(new PropertyValueFactory<Rooms, Integer>("capacity"));

        tableView.refresh();
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
    
    @FXML
    private void deleteRoom(ActionEvent event) throws SQLException {

        Rooms rooms = tableView.getSelectionModel().getSelectedItem();
        String query = "DELETE from sanjanadivya_rooms WHERE Room_no = "+rooms.getRoom_no();
        executeQuery(query);
        
        tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void btnAddNewRoomHandle(ActionEvent event) {
        paneAddNewRoom.toFront();
        labelTitle.setText("Add New Room");
    }
    
    @FXML
    public void btnSaveNewRoom(ActionEvent event) throws IOException, SQLException {
        /*
         * Checking the new room number 
         * if that number already exist labelError will contains error message 
         */
        String query = "SELECT * from sanjanadivya_rooms WHERE Room_no = ?"; 
        
        PreparedStatement preparedStatement = getConnection().prepareStatement(query);
        preparedStatement.setString(1, fieldRoomNumber.getText());
        
        ResultSet rs = preparedStatement.executeQuery();
        
        if(!rs.next()) {
            query = "INSERT into `sanjanadivya_rooms` (`Room_no`, `Type`, `Capacity`, `Status`, Price) " + 
            "VALUES('"+fieldRoomNumber.getText()+"','"+fieldRoomType.getText()+"','"+
            fieldRoomCapacity.getText()+"','Available','"+fieldPrice.getText()+"')";
        
            executeQuery(query);
            
            showRooms();
            tableView.refresh();
            labelErrorRoom.setText("");
            paneRoom.toFront();
        } else {
            labelErrorRoom.setText("The room number already exists");
        }
      
    }
    
    public ObservableList<Users> getUsersList() {
        ObservableList<Users> usersList = FXCollections.observableArrayList();
        Connection connection = getConnection();
        String query = "SELECT id, name, username, role FROM sanjanadivya_users";
        Statement st;
        ResultSet rs;

        try {
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Users users;
            while (rs.next()) {
                users = new Users(rs.getInt("id"), rs.getString("name"), rs.getString("username"), rs.getInt("role"));

                usersList.add(users);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersList;
    }
    
    public void showUsers() {
        ObservableList<Users> list = getUsersList();

        columnId.setCellValueFactory(new PropertyValueFactory<Users, Integer>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<Users, String>("name"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<Users, String>("username"));
        columnRole.setCellValueFactory(new PropertyValueFactory<Users, Integer>("role"));

        tableViewUser.refresh();
        tableViewUser.setItems(list);
    }
    
    public void searchUserTable() {
        FilteredList<Users> filteredData = new FilteredList<>(getUsersList(), p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        fieldSearchUser.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(users -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name field in your object with filter.
                String lowerCaseFilter = newValue.toLowerCase();

                if (String.valueOf(users.getName()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches Room No.
                } else if (String.valueOf(users.getUsername()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches Type.
                } else if (String.valueOf(users.getId()).toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches Type.
                } 

                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList. 
        
        SortedList<Users> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator
        sortedData.comparatorProperty().bind(tableViewUser.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tableViewUser.setItems(sortedData);
    }
    
    @FXML
    public void btnAddNewUserHandle() {
        paneAddNewUser.toFront();
        labelTitle.setText("Add New User");
    }
    
    @FXML
    public void btnSaveNewUser() throws SQLException {
        /*
         * Checking the new user username 
         * if that username already exist labelError will contains error message 
         */
        String query = "SELECT * from sanjanadivya_users WHERE username = ?"; 
        
        PreparedStatement preparedStatement = getConnection().prepareStatement(query);
        preparedStatement.setString(1, fieldRoomNumber.getText());
        
        ResultSet rs = preparedStatement.executeQuery();
        
        int role;
        if(comboBoxRole.getSelectionModel().getSelectedItem() == "Admin" ) {
            role = 1; 
        } else {
            role = 2;
        }
        
        if(!rs.next()) {
        	Users users = tableViewUser.getItems().get(tableViewUser.getItems().size() - 1);
            query = "INSERT into `debayan_users` (`name`, `username`, `password`, `role`, `id`) " + 
            "VALUES('"+fieldName.getText()+"','"+fieldUsername.getText()+"','"+
            fieldPassword.getText()+"','"+role+"', '" + users.getId()+1 + "')";
        
            executeQuery(query);
            
            showUsers();
            tableViewUser.refresh();
            labelErrorUser.setText("");
            fieldName.setText("");
            fieldUsername.setText("");
            fieldPassword.setText("");
            paneUser.toFront();
        } else {
            labelErrorUser.setText("The username number already exists");
        }
    }
    
    @FXML
    private void deleteUser(){
        Users users = tableViewUser.getSelectionModel().getSelectedItem();
        System.out.println(users.getId());
        String query = "DELETE from sanjanadivya_users WHERE id = "+users.getId();
        executeQuery(query);
        
        tableViewUser.getItems().removeAll(tableViewUser.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    private void btnCancelHandle(ActionEvent event) {
        paneRoom.toFront();
        labelTitle.setText("Room");
    }

    @FXML
    private void closeProgram(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleNavigation(ActionEvent event) {
        if(event.getSource() == btnRoom) {
            paneRoom.toFront();
            labelTitle.setText("Room");
        } else if(event.getSource() == btnDashboard) {
            labelTitle.setText("Admin Dashboard");
        } else if(event.getSource() == btnUsers) {
            paneUser.toFront();
            labelTitle.setText("Users");
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        comboBoxRole.setValue("User");
        comboBoxRole.setItems(roleList);
        
        showRooms();
        showUsers();
        
        searchRoomTable();
        searchUserTable();
    }    
    
}
