package com.vabz.Controller;

import com.vabz.DatabaseConnection;
import com.vabz.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainController {

    @FXML
    private TextField firstName;
    @FXML
    private TextField middleName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField address;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField email;
    @FXML
    private RadioButton male;
    @FXML
    private RadioButton female;

    @FXML
    private TableView<Student> table;
    @FXML
    private TableColumn<Student, String> colFN;
    @FXML
    private TableColumn<Student, String> colMN;
    @FXML
    private TableColumn<Student, String> colLN;
    @FXML
    private TableColumn<Student, String> colAddress;
    @FXML
    private TableColumn<Student, String> colphoneNumber;
    @FXML
    private TableColumn<Student, String> colEmail;
    @FXML
    private TableColumn<Student, String> colGender;

    private boolean isEditing = false;
    private int studentID = 0;


    private DatabaseConnection db;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    public void initialize() throws SQLException{
        db = new DatabaseConnection();

        colFN.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colMN.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        colLN.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colphoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

        loadStudents();

    }

    public void loadStudents() throws SQLException{
        studentList.clear();
        String sql = "SELECT * from students_fx";

        Statement stmt = db.getConnection().createStatement();
        ResultSet result = stmt.executeQuery(sql);

        while(result.next()){
            Student student = new Student(result.getInt("id"),
                    result.getString("first_name"),
                    result.getString("middle_name"),
                    result.getString("last_name"),
                    result.getString("address"),
                    result.getString("phone_number"),
                    result.getString("email"),
                    result.getString("gender"));
            studentList.add(student);
        }

        table.setItems(studentList);
    }

    @FXML
    private void saveData() throws SQLException {
        if(!isEditing) {
            String sql = "INSERT INTO students_fx (first_name, middle_name, last_name, address, phone_number, email, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = db.getConnection().prepareStatement(sql);
            pstmt.setString(1, firstName.getText());
            pstmt.setString(2, middleName.getText());
            pstmt.setString(3, lastName.getText());
            pstmt.setString(4, address.getText());
            pstmt.setString(5, phoneNumber.getText());
            pstmt.setString(6, email.getText());
            if (male.isSelected()) {
                pstmt.setString(7, "Male");
            } else if (female.isSelected()) {
                pstmt.setString(7, "Female");
            }
            if (pstmt.executeUpdate() == 1) {
                firstName.clear();
                middleName.clear();
                lastName.clear();
                address.clear();
                phoneNumber.clear();
                email.clear();
                male.setSelected(false);
                female.setSelected(false);
                loadStudents();
            }
        }else{
            String sql = "UPDATE students_fx SET first_name = ?, middle_name = ?, last_name = ?, address = ?, phone_number = ?, email = ?, gender =? WHERE id = ?";
            try{
                PreparedStatement pstmt = db.getConnection().prepareStatement(sql);
                pstmt.setString(1, firstName.getText());
                pstmt.setString(2, middleName.getText());
                pstmt.setString(3, lastName.getText());
                pstmt.setString(4, address.getText());
                pstmt.setString(5, phoneNumber.getText());
                pstmt.setString(6, email.getText());
                if (male.isSelected()){
                    pstmt.setString(7, "Male");
                }
                else if (female.isSelected()){
                    pstmt.setString(7,"Female");
                }
                pstmt.setInt(8, studentID);
                if (pstmt.executeUpdate() == 1) {
                    firstName.clear();
                    middleName.clear();
                    lastName.clear();
                    address.clear();
                    phoneNumber.clear();
                    email.clear();
                    male.setSelected(false);
                    female.setSelected(false);
                    loadStudents();
                }

                loadStudents();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void delete(){
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        if(selectedStudent != null){
            String sql = "DELETE from students_fx WHERE id = ?";
            try{
                PreparedStatement pstmt = db.getConnection().prepareStatement(sql);
                pstmt.setInt(1, selectedStudent.getId());
                pstmt.executeUpdate();

                studentList.remove(selectedStudent);
            }catch(SQLException e){
                e.printStackTrace();
            }

        }
    }
    @FXML
    private void edit(){
        Student selectedStudent = table.getSelectionModel().getSelectedItem();
        if(selectedStudent != null){
            firstName.setText(selectedStudent.getFirstName());
            middleName.setText(selectedStudent.getMiddleName());
            lastName.setText(selectedStudent.getLastName());
            address.setText(selectedStudent.getAddress());
            phoneNumber.setText(selectedStudent.getPhoneNumber());
            email.setText(selectedStudent.getEmail());
            male.setText(selectedStudent.getGender());
            female.setText(selectedStudent.getGender());
            isEditing = true;
            studentID = selectedStudent.getId();


        }
    }
}
