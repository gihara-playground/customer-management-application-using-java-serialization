package com.gihara.controller;

import com.gihara.Customer;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ManageCustomerFormController {
    private final Path dbPath = Paths.get("database/customers.data");
    public TextField txtID;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtPicture;
    public Button btnManipulate;
    public TableView<Customer> tblCustomers;

    public void initialize() {
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

//        tblCustomers.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("picture"));

        TableColumn<Customer, ImageView> fourthCol = (TableColumn<Customer, ImageView>) tblCustomers.getColumns().get(3);
        fourthCol.setCellValueFactory(param -> {
            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(param.getValue().getPictureBytes())));
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            return new ReadOnlyObjectWrapper<>(imageView);
        });


        TableColumn<Customer, Button> lastCol = (TableColumn<Customer, Button>) tblCustomers.getColumns().get(4);
        lastCol.setCellValueFactory(param -> {
            Button btnDelete = new Button("Delete");
            btnDelete.setOnAction(event -> {
                tblCustomers.getItems().remove(param.getValue());
                saveCustomers();
            });
            return new ReadOnlyObjectWrapper<>(btnDelete);
        });
        initDatabase();
    }

    public void createCustomer() throws IOException {

        if (!txtID.getText().matches("C\\d{3}") ||
                tblCustomers.getItems().stream().anyMatch(c -> c.getId().equalsIgnoreCase(txtID.getText()))) {
            txtID.requestFocus();
            txtID.selectAll();
            return;
        } else if (txtName.getText().trim().isEmpty()) {
            txtName.requestFocus();
            txtName.selectAll();
            return;
        } else if (txtAddress.getText().trim().isEmpty()) {
            txtAddress.requestFocus();
            txtAddress.selectAll();
            return;
        } else if (txtPicture.getText().trim().isEmpty()) {
            txtPicture.requestFocus();
            txtPicture.selectAll();
            return;
        }

        InputStream is = Files.newInputStream(Paths.get(txtPicture.getText()));
        byte[] picBuffer = new byte[is.available()];
        is.read(picBuffer);
        is.close();

        Customer newCustomer = new Customer(
                txtID.getText(),
                txtName.getText(),
                txtAddress.getText(),
                picBuffer);

        tblCustomers.getItems().add(newCustomer);

        boolean result = saveCustomers();

        if (!result) {
            new Alert(Alert.AlertType.ERROR, "Failed to save the customer, try again").show();
            tblCustomers.getItems().remove(newCustomer);
        } else {
            txtID.clear();
            txtName.clear();
            txtAddress.clear();
            txtPicture.clear();
        }
        txtID.requestFocus();
    }

    private void initDatabase() {
        try {
            if (!Files.exists(dbPath)) {
                Files.createDirectories(dbPath.getParent());
                Files.createFile(dbPath);
            }
            loadAllCustomers();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to initialize the database").showAndWait();
            Platform.exit();
        }
    }

    private boolean saveCustomers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(dbPath, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))) {
            oos.writeObject(new ArrayList<Customer>(tblCustomers.getItems()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadAllCustomers() {
        try (InputStream is = Files.newInputStream(dbPath, StandardOpenOption.READ);
             ObjectInputStream ois = new ObjectInputStream(is)) {
            tblCustomers.getItems().clear();
            tblCustomers.setItems(FXCollections.observableArrayList((ArrayList<Customer>) ois.readObject()));
        } catch (IOException | ClassNotFoundException e) {
            if (!(e instanceof EOFException)) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to load customers").showAndWait();
            }
        }
    }

    public void btnSearchPicture_OnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("image","*.jpeg", "*.jpg", "*.gif", "*.png", "*.bmp"));
        fileChooser.setTitle("Select an image");
        File file = fileChooser.showOpenDialog(txtID.getScene().getWindow());
        txtPicture.setText(file!=null?file.getAbsolutePath():"");
    }

    public void btnManipulateCustomer_OnAction(ActionEvent actionEvent) throws IOException {
        if (btnManipulate.getText().matches("Save Customer")){
            createCustomer();
        }
        else {

        }
    }
}