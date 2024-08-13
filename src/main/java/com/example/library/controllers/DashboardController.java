package com.example.library.controllers;

import com.example.library.utils.UserContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private ListView<String> lstMenu;
    @FXML
    private AnchorPane pane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        lstMenu.getItems().addAll("Quản lý sách", "Quản lý độc giả", "Quản lý mượn trả", "Thống kê");
//        lstMenu.getSelectionModel().selectFirst();
//        loadPane(lstMenu.getSelectionModel().getSelectedItem());
        initMenuByRole(UserContext.getInstance().getRole());
    }

    public void initMenuByRole(String role){
        if(role.equalsIgnoreCase("reader")){
            lstMenu.getItems().addAll("Information", "Available book", "History borrow", "Request borrow");
        }

        if(role.equalsIgnoreCase("librarian")){
            lstMenu.getItems().addAll("Book management", "Reader management", "Borrow management", "Request management", "Statistical");
        }

        lstMenu.getSelectionModel().selectFirst();
        loadPane(lstMenu.getSelectionModel().getSelectedItem());
    }

    public void onSelected(MouseEvent mouseEvent) {
        String selectedItem = lstMenu.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            loadPane(selectedItem);
        }
    }

    private void loadPane(String selected) {
        String frm = "";

        switch (selected) {
            case "Book management":
                frm = "/com/example/library/BookManagementFrm.fxml";
                break;
            case "Reader management":
                frm = "/com/example/library/ReaderManagementFrm.fxml";
                break;
            case "Borrow management":
                frm = "/com/example/library/BorrowManagementFrm.fxml";
                break;
            case "Statistical":
                frm = "/com/example/library/StaticFrm.fxml";
                break;

                // client
            case "Available book":
                frm = "/com/example/library/BookManagementFrm.fxml";
                break;
            case "Information":
                frm = "/com/example/library/InformationFrm.fxml";
                break;
            case "History borrow":
                frm = "/com/example/library/BorrowHistoryFrm.fxml";
                BorrowHistoryController.setReaderId(UserContext.getInstance().getReaderId());
            default:
                break;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(frm));
            pane.getChildren().clear();
            AnchorPane newPane = loader.load();
            pane.getChildren().add(newPane);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
