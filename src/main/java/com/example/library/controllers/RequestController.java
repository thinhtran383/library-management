package com.example.library.controllers;

import com.example.library.models.Borrow;
import com.example.library.services.IBorrowService;
import com.example.library.services.impl.BorrowServiceImpl;
import com.example.library.services.impl.MailService;
import com.example.library.utils.UserContext;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RequestController implements Initializable {
    public TableColumn colSelect;

    private final IBorrowService borrowService;
    public TableColumn<Borrow, Integer> colId;
    public TableView tbRequest;
    public TableColumn colReaderName;
    public TableColumn colBookName;
    public TableColumn colBorrowDate;
    public TableColumn colReturnDate;
    public Button btnApprove;
    private List<String> selectedBorrowId;
    private final MailService mailService;

    public RequestController() {
        this.mailService = new MailService("smtp.gmail.com");
        this.borrowService = new BorrowServiceImpl();
        selectedBorrowId = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDataToTable();

        if (UserContext.getInstance().getRole().equals("Reader")) {
            setUpForReader();
        } else {
            btnApprove.setOnAction(this::onClickApprove);
        }
    }


    private void setDataToTable() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(tbRequest.getItems().indexOf(cellData.getValue()) + 1).asObject());
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colSelect.setCellFactory(tc -> new TableCell<Borrow, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Borrow borrow = getTableRow().getItem();
                    if (checkBox.isSelected()) {
                        selectedBorrowId.add(borrow.getBorrowId());
                    } else {
                        selectedBorrowId.remove(borrow.getBorrowId());
                    }

                    System.out.println(selectedBorrowId);
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(checkBox);
                }
            }
        });

        if (UserContext.getInstance().getRole().equals("Reader")) {
            tbRequest.setItems(borrowService.getAllRequestByReaderId(UserContext.getInstance().getReaderId()));
        } else {
            tbRequest.setItems(borrowService.getAllRequestBorrow());
        }
    }


    private void setUpForReader() {
        colReaderName.setVisible(false);

        btnApprove.setText("Delete Request");

        btnApprove.setOnAction(this::onClickDeleteRequest);
    }

    public void onClickApprove(ActionEvent actionEvent) {
        borrowService.approveRequest(selectedBorrowId);
        setDataToTable();


        List<String> emails = borrowService.getAllEmailByBorrowIds(selectedBorrowId);
        mailService.sendMail(
                emails,
                "Request Approved",
                "Your request has been approved please come to the library to get the book."
        );

        mailService.shutdown();



    }

    public void onClickDeleteRequest(ActionEvent actionEvent) {
        borrowService.deleteRequest(selectedBorrowId);
        setDataToTable();
    }

}
