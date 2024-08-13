package com.example.library.controllers.client;

import com.example.library.models.Reader;
import com.example.library.services.IReaderService;
import com.example.library.services.ReaderServiceImpl;
import com.example.library.utils.AlertUtil;
import com.example.library.utils.UserContext;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class InformationController implements Initializable {
    public Button btnUpdate;
    public TextField txtEmail;
    public TextField txtPhoneNumber;
    public DatePicker dpDob;
    public TextField txtReaderId;
    public TextField txtFullname;
    public TextField txtAddress;

    private final IReaderService readerService;
    public Button btnSave;

    public InformationController() {
        this.readerService = new ReaderServiceImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        disableEdit();
        Reader reader = readerService.getReaderByUsername(UserContext.getInstance().getUsername());
        initInformation(reader);
    }

    private void disableEdit() {
        txtAddress.setEditable(false);
        txtEmail.setEditable(false);
        txtFullname.setEditable(false);
        txtPhoneNumber.setEditable(false);
        dpDob.setEditable(false);
        dpDob.setDisable(true);
        txtReaderId.setEditable(false);

        btnSave.setVisible(false);

    }

    private void initInformation(Reader reader) {
        txtReaderId.setText(reader.getReaderId());
        txtFullname.setText(reader.getReaderName());
        txtAddress.setText(reader.getReaderAddress());
        txtEmail.setText(reader.getReaderEmail());
        txtPhoneNumber.setText(reader.getReaderPhone());
        dpDob.setValue(reader.getReaderDOB());

        UserContext.getInstance().setReaderId(reader.getReaderId());

    }


    public void onClickUpdate(ActionEvent actionEvent) {
        txtAddress.setEditable(true);
        txtEmail.setEditable(true);
        txtFullname.setEditable(true);
        txtPhoneNumber.setEditable(true);
        dpDob.setEditable(true);
        dpDob.setDisable(false);

        btnSave.setVisible(true);
    }

    public void onClickSave(ActionEvent actionEvent) {
        Reader reader = Reader.builder()
                .readerId(txtReaderId.getText())
                .readerEmail(txtEmail.getText())
                .readerName(txtFullname.getText())
                .readerAddress(txtAddress.getText())
                .readerPhone(txtPhoneNumber.getText())
                .readerDOB(dpDob.getValue())
                .build();


        try {
            readerService.updateReader(reader);
        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, e.getMessage());
            return;
        }

        initInformation(reader);
        disableEdit();
    }
}