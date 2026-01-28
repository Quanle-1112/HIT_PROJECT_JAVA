package org.example.controllers.authentication;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.OtpStatus;
import org.example.model.user.Gender;
import org.example.model.user.User;
import org.example.services.IUserService;
import org.example.services.impl.IUserServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

import java.io.File;
import java.time.LocalDate;

public class ConfirmInformationController {
    @FXML private ImageView uploadImageButton;
    @FXML private Button btnUploadAvatar, emailConfirmButton, sendingCodeButton, confirmButton;
    @FXML private TextField hoVaTenText, sdtText, emailText, confirmEmailText;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private RadioButton namRadioButton, nuRadioButton, khacRadioButton;
    @FXML private ToggleGroup genderGroup;
    @FXML private CheckBox agreeCheckBox;
    @FXML private Label pleaseCompleteAllFieldsText, invalidCodeText, checkTheBoxText, failToSendEmailText, errorPhoneNumberText, expiredCodeText, codeSentSuccessfullyText;

    private final IUserService userService = new IUserServiceImpl();
    private User currentUser;
    private String selectedAvatarPath = null;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentUser != null && currentUser.getEmail() != null) emailText.setText(currentUser.getEmail());
    }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(pleaseCompleteAllFieldsText, invalidCodeText, checkTheBoxText, failToSendEmailText, errorPhoneNumberText, expiredCodeText, codeSentSuccessfullyText);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i >= 1950; i--) yearComboBox.getItems().add(i);

        genderGroup = new ToggleGroup();
        namRadioButton.setToggleGroup(genderGroup);
        nuRadioButton.setToggleGroup(genderGroup);
        khacRadioButton.setToggleGroup(genderGroup);

        emailConfirmButton.setOnAction(event -> handleSendCode());
        confirmButton.setOnAction(event -> handleConfirm());
        btnUploadAvatar.setOnAction(event -> handleUploadAvatar());
        uploadImageButton.setOnMouseClicked(event -> handleUploadAvatar());
    }

    private void handleUploadAvatar() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn ảnh đại diện");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File f = fc.showOpenDialog(btnUploadAvatar.getScene().getWindow());
        if (f != null) {
            selectedAvatarPath = f.getAbsolutePath();
            uploadImageButton.setImage(new Image(f.toURI().toString()));
        }
    }

    private void handleSendCode() {
        UIExceptionHandler.hideError(pleaseCompleteAllFieldsText, failToSendEmailText, codeSentSuccessfullyText);
        if (emailText.getText().trim().isEmpty()) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText); return;
        }

        emailConfirmButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        new Thread(() -> {
            boolean isSent = userService.sendOtp(emailText.getText().trim());
            Platform.runLater(() -> {
                if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                emailConfirmButton.setVisible(true);
                if (isSent) UIExceptionHandler.showSuccess(codeSentSuccessfullyText);
                else UIExceptionHandler.showError(failToSendEmailText);
            });
        }).start();
    }

    private void handleConfirm() {
        UIExceptionHandler.hideError(checkTheBoxText, pleaseCompleteAllFieldsText, invalidCodeText, errorPhoneNumberText, expiredCodeText);
        if (!agreeCheckBox.isSelected()) { UIExceptionHandler.showError(checkTheBoxText); return; }
        if (ValidationUtils.areFieldsEmpty(hoVaTenText, sdtText, emailText, confirmEmailText)) { UIExceptionHandler.showError(pleaseCompleteAllFieldsText); return; }
        if (!ValidationUtils.isValidPhoneNumber(sdtText.getText().trim())) { UIExceptionHandler.showError(errorPhoneNumberText); return; }

        OtpStatus status = userService.verifyOtp(emailText.getText().trim(), confirmEmailText.getText().trim());

        if (status == OtpStatus.INVALID_CODE) {
            UIExceptionHandler.showError(invalidCodeText); return;
        }
        if (status == OtpStatus.EXPIRED_CODE) {
            UIExceptionHandler.showError(expiredCodeText); return;
        }

        if (currentUser != null) {
            currentUser.setFullName(hoVaTenText.getText().trim());
            currentUser.setPhoneNumber(sdtText.getText().trim());
            currentUser.setGender(namRadioButton.isSelected() ? Gender.Male : (nuRadioButton.isSelected() ? Gender.Female : Gender.Other));
            if (selectedAvatarPath != null) currentUser.setAvatarUrl(selectedAvatarPath);
            else if (currentUser.getAvatarUrl() == null) currentUser.setAvatarUrl("");

            if (userService.updateUserProfile(currentUser) && userService.disableFirstLogin(currentUser.getId())) {
                SceneUtils.openNewWindow("/view/read/home_screen.fxml", "Trang chủ", confirmButton);
            } else {
                UIExceptionHandler.showError(failToSendEmailText);
            }
        }
    }
}