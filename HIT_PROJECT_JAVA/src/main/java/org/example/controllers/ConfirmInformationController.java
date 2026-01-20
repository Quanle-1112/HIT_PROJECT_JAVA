package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.Sex;
import org.example.model.user.User;
import org.example.services.IEmailService;
import org.example.services.IUserService;
import org.example.services.impl.IEmailServiceImpl;
import org.example.services.impl.IUserServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.ValidationUtils;

import java.io.File;
import java.time.LocalDate;
import java.util.Random;

public class ConfirmInformationController {

    @FXML private ImageView uploadImageButton;
    @FXML private Button btnUploadAvatar;
    @FXML private TextField hoVaTenText, sdtText, emailText, confirmEmailText;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private RadioButton namRadioButton, nuRadioButton, khacRadioButton;
    @FXML private ToggleGroup genderGroup;
    @FXML private Button emailConfirmButton, sendingCodeButton, confirmButton;
    @FXML private CheckBox agreeCheckBox;

    @FXML private Label pleaseCompleteAllFieldsText, invalidCodeText, checkTheBoxText, failToSendEmailText, errorPhoneNumberText, expiredCodeText, codeSentSuccessfullyText;

    private final IEmailService emailService = new IEmailServiceImpl();
    private final IUserService userService = new IUserServiceImpl();

    private User currentUser;
    private String serverOtp = null;
    private String selectedAvatarPath = null;
    private long otpGeneratedTime = 0;
    private static final long OTP_TIMEOUT = 5 * 60 * 1000;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            if (currentUser.getEmail() != null) emailText.setText(currentUser.getEmail());
            if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
                try {
                    File file = new File(currentUser.getAvatarUrl());
                    if (file.exists()) uploadImageButton.setImage(new Image(file.toURI().toString()));
                } catch (Exception e) { /* ignore */ }
            }
        }
    }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(
                pleaseCompleteAllFieldsText, invalidCodeText, checkTheBoxText,
                failToSendEmailText, errorPhoneNumberText, expiredCodeText, codeSentSuccessfullyText
        );
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) btnUploadAvatar.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            selectedAvatarPath = selectedFile.getAbsolutePath();
            uploadImageButton.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    private void handleSendCode() {
        UIExceptionHandler.hideError(pleaseCompleteAllFieldsText, failToSendEmailText, codeSentSuccessfullyText, expiredCodeText);
        String email = emailText.getText().trim();

        if (email.isEmpty()) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText);
            return;
        }

        emailConfirmButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        new Thread(() -> {
            try {
                String tempOtp = String.valueOf(100000 + new Random().nextInt(900000));
                boolean isSent = emailService.sendEmail(email, "WOWTruyen - Mã xác thực", "Mã: " + tempOtp);

                Platform.runLater(() -> {
                    if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                    emailConfirmButton.setVisible(true);

                    if (isSent) {
                        serverOtp = tempOtp;
                        otpGeneratedTime = System.currentTimeMillis();
                        UIExceptionHandler.showSuccess(codeSentSuccessfullyText);
                    } else {
                        UIExceptionHandler.showError(failToSendEmailText);
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void handleConfirm() {
        UIExceptionHandler.hideError(checkTheBoxText, pleaseCompleteAllFieldsText, invalidCodeText, errorPhoneNumberText, expiredCodeText);

        if (!agreeCheckBox.isSelected()) {
            UIExceptionHandler.showError(checkTheBoxText);
            return;
        }

        if (ValidationUtils.areFieldsEmpty(hoVaTenText, sdtText, emailText, confirmEmailText)) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText);
            return;
        }

        if (!ValidationUtils.isValidPhoneNumber(sdtText.getText().trim())) {
            UIExceptionHandler.showError(errorPhoneNumberText);
            return;
        }

        if (System.currentTimeMillis() - otpGeneratedTime > OTP_TIMEOUT) {
            UIExceptionHandler.showError(expiredCodeText);
            return;
        }

        if (serverOtp == null || !serverOtp.equals(confirmEmailText.getText().trim())) {
            UIExceptionHandler.showError(invalidCodeText);
            return;
        }

        saveUserInformation();
    }

    private void saveUserInformation() {
        if (currentUser == null) return;
        currentUser.setFullName(hoVaTenText.getText().trim());
        currentUser.setPhoneNumber(sdtText.getText().trim());
        currentUser.setEmail(emailText.getText().trim());

        if (namRadioButton.isSelected()) currentUser.setGender(Sex.Male);
        else if (nuRadioButton.isSelected()) currentUser.setGender(Sex.Female);
        else currentUser.setGender(Sex.Other);

        if (selectedAvatarPath != null) currentUser.setAvatarUrl(selectedAvatarPath);
        else if (currentUser.getAvatarUrl() == null) currentUser.setAvatarUrl("");

        if (userService.updateUserProfile(currentUser) && userService.disableFirstLogin(currentUser.getId())) {
            // Mở màn hình Home nhưng dưới dạng Popup mới (Window mới) và đóng cửa sổ confirm
            SceneUtils.openNewWindow("/view/home_screen.fxml", "Trang chủ", confirmButton);
        } else {
            UIExceptionHandler.showError(failToSendEmailText);
        }
    }
}