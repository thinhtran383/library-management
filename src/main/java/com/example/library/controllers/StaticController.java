package com.example.library.controllers;

import com.example.library.services.IStaticService;
import com.example.library.services.StaticServiceImpl;
import com.example.library.utils.AlertUtil;
import com.example.library.utils.SettingUtils;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class StaticController implements Initializable {
    public Text txtNumberReader;
    public Text txtTotalBorrow;
    public Text txtTotalQuantityBook;
    public Text txtTotalLate;

    private final IStaticService staticService;
    public Text txtNumberReturn;
    private final SettingUtils settingUtils = SettingUtils.getInstance();

    public StaticController() {
        staticService = new StaticServiceImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtNumberReader.setText(String.valueOf(staticService.getTotalReader()));
        txtTotalBorrow.setText(String.valueOf(staticService.getTotalBorrow()));
        txtTotalQuantityBook.setText(String.valueOf(staticService.getTotalBook()));
        txtTotalLate.setText(String.valueOf(staticService.getTotalLate()));
        txtNumberReturn.setText(String.valueOf(staticService.getTotalReturn()));
    }

    public void onClickReturnOnTime(MouseEvent mouseEvent) {
        if(settingUtils.isHighlightReturn()){
             if(AlertUtil.showConfirmation("Bạn có muốn tắt tuỳ chọn này không?")){
                 settingUtils.setHighlightReturn(false);
             }
        } else if(AlertUtil.showConfirmation("Bạn có muốn bật tuỳ chọn này không?")){
            settingUtils.setHighlightReturn(true);
        }
    }

    public void onClickReturnLate(MouseEvent mouseEvent) {
        if(settingUtils.isHighlightLate()){
            if(AlertUtil.showConfirmation("Bạn có muốn tắt tuỳ chọn này không?")){
                settingUtils.setHighlightLate(false);
            }
        } else if(AlertUtil.showConfirmation("Bạn có muốn bật tuỳ chọn này không?")){
            settingUtils.setHighlightLate(true);
        }
    }
}
