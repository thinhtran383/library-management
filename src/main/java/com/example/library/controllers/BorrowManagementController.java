package com.example.library.controllers;

import com.example.library.models.Borrow;
import com.example.library.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class BorrowManagementController implements Initializable {
    @FXML
    private ComboBox<String> cbFilterLate;
    @FXML
    private ComboBox<String> cbReaderId;
    @FXML
    private ComboBox<String> cbBookId;
    @FXML
    private DatePicker dpReturnDate;
    @FXML
    private Label lbReaderName;
    @FXML
    private Label lbBookName;
    @FXML
    private TableView<Borrow> tbBorrows;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colBookName;
    @FXML
    private TableColumn colReaderName;
    @FXML
    private TableColumn colBorrowDate;
    @FXML
    private TableColumn colReturnDate;
    @FXML
    private TableColumn colDueDate;

    private final IBorrowService borrowService;
    private final IBookService bookService;
    private final IReaderService readerService;

    public BorrowManagementController() {
        borrowService = new BorrowServiceImpl();
        bookService = new BookServiceImpl();
        readerService = new ReaderServiceImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBorrows();
        initComboBox();
        customDatePickers();
    }

    private void initComboBox() {
        cbFilterLate.getItems().addAll("Quá hạn", "Chưa trả","Đã trả", "Không");
        cbFilterLate.getSelectionModel().selectLast();
        cbBookId.getItems().addAll(bookService.getAllBookId());
        cbReaderId.getItems().addAll(readerService.getAllReaderId());
    }

    private void loadBorrows() {
        colId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        tbBorrows.setItems(borrowService.getAllBookBorrowed());
    }

    public void onChooseReaderId(ActionEvent actionEvent) {
        String readerId = cbReaderId.getValue();
        lbReaderName.setText(readerService.getReaderNameById(readerId));
    }

    public void onChooseBookId(ActionEvent actionEvent) {
        String bookId = cbBookId.getValue();
        lbBookName.setText(bookService.getBookNameById(bookId));
    }

    private void customDatePickers() {
        dpReturnDate.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        });
    }

    public void onClickBorrow(ActionEvent actionEvent) {
        Optional<Borrow> selectedBorrow = Optional.ofNullable(tbBorrows.getSelectionModel().getSelectedItem());

        if (selectedBorrow.isPresent()) {
            borrowService.borrowBook(selectedBorrow.get());
        } else {


            Borrow borrow = Borrow.builder()
                    .readerName(cbReaderId.getValue())
                    .bookName(cbBookId.getValue())
                    .returnDate(dpReturnDate.getValue())
                    .build();
            borrowService.borrowBook(borrow);

        }
        tbBorrows.setItems(borrowService.getAllBookBorrowed());
    }

    public void onSelected(MouseEvent mouseEvent) {
    }

    public void onChooseFilter(ActionEvent actionEvent) {
        String filter = cbFilterLate.getValue();
        ObservableList<Borrow> borrows = borrowService.getAllBookBorrowed();

        switch (filter) {
            case "Chưa trả":
                borrows = borrows.filtered(borrow -> borrow.getDueDate() == null);
                break;
            case "Đã trả":
                borrows = borrows.filtered(borrow -> borrow.getDueDate() != null);
                break;
            case "Quá hạn":
                LocalDate today = LocalDate.now();
                borrows = borrows.filtered(borrow -> {
                    String returnDate = String.valueOf(borrow.getReturnDate());
                    return borrow.getDueDate() == null  && returnDate != null && !returnDate.isEmpty() && LocalDate.parse(returnDate).isBefore(today);
                });
                break;
            default:
                break;
        }

        tbBorrows.setItems(borrows);
    }
}
