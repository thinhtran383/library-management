package com.example.library.controllers;

import com.example.library.models.Author;
import com.example.library.services.AuthorServiceImpl;
import com.example.library.services.IAuthorService;
import com.example.library.utils.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AuthorManagementController implements Initializable {
    public TextField txtSearch;
    @FXML
    private TableView<Author> tbAuthor;
    @FXML
    private TableColumn colAuthorId;
    @FXML
    private TableColumn colAuthorName;
    @FXML
    private TableColumn colAuthorAddress;
    @FXML
    private TextField txtAuthorId;
    @FXML
    private TextField txtAuthorName;
    @FXML
    private TextField txtAddress;
    private final IAuthorService  authorService;


    public AuthorManagementController(){
        authorService = new AuthorServiceImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAuthors();
    }

    private void loadAuthors(){
        colAuthorId.setCellValueFactory(new PropertyValueFactory<>("authorId"));
        colAuthorName.setCellValueFactory(new PropertyValueFactory<>("authorName"));
        colAuthorAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        tbAuthor.setItems(authorService.getAllAuthors());
    }


    public void onSelected(MouseEvent mouseEvent) {
        Optional<Author> selectedAuthor = Optional.ofNullable(tbAuthor.getSelectionModel().getSelectedItem());

        selectedAuthor.ifPresentOrElse(
                author -> {
                    txtAuthorId.setText(author.getAuthorId());
                    txtAuthorName.setText(author.getAuthorName());
                    txtAddress.setText(author.getAddress());
                },
                () -> {
                    txtAddress.clear();
                    txtAuthorId.clear();
                    txtAuthorName.clear();
                }
        );
    }

    public void onClickAdd(ActionEvent actionEvent) {
        String authorId = txtAuthorId.getText();
        String authorName = txtAuthorName.getText();
        String address = txtAddress.getText();

        if(isNull(authorId, authorName, address)){
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", null,"Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        Author author = Author.builder()
                .authorId(authorId)
                .authorName(authorName)
                .address(address)
                .build();

        authorService.saveAuthor(author);
        tbAuthor.setItems(authorService.getAllAuthors());
    }

    public void onClickUpdate(ActionEvent actionEvent) {
        Optional<Author> selectedAuthor = Optional.ofNullable(tbAuthor.getSelectionModel().getSelectedItem());

        if(selectedAuthor.isPresent()){
            String authorId = txtAuthorId.getText();
            String authorName = txtAuthorName.getText();
            String address = txtAddress.getText();

            if(isNull(authorId, authorName, address)){
                AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", null,"Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            Author author = Author.builder()
                    .authorId(authorId)
                    .authorName(authorName)
                    .address(address)
                    .build();

            authorService.updateAuthor(author);
            tbAuthor.setItems(authorService.getAllAuthors());
        }
    }

    public void onClickDelete(ActionEvent actionEvent) {
        Optional<Author> selectedAuthor = Optional.ofNullable(tbAuthor.getSelectionModel().getSelectedItem());

        if(selectedAuthor.isPresent() && AlertUtil.showConfirmation("Bạn có chắc chắn muốn xóa tác giả này?")){
            authorService.deleteAuthor(selectedAuthor.get());
            tbAuthor.setItems(authorService.getAllAuthors());
        }
    }

    public void onClickRefresh(ActionEvent actionEvent) {
        tbAuthor.setItems(authorService.getAllAuthors());
        txtAddress.clear();
        txtAuthorId.clear();
        txtAuthorName.clear();
        tbAuthor.getSelectionModel().clearSelection();
    }

    private boolean isNull(Object ...o){
        for (Object obj : o){
            if (obj == null || obj.toString().isEmpty()){
                return true;
            }
        }
        return false;
    }


    public void onSearch(KeyEvent keyEvent) {
        String keyword = txtSearch.getText();

        if(keyword.isEmpty()){
            tbAuthor.setItems(authorService.getAllAuthors());
        }else{
            tbAuthor.setItems(authorService.getAllAuthors().filtered(author -> author.getAuthorName().toLowerCase().contains(keyword.toLowerCase())));
        }
    }
}
