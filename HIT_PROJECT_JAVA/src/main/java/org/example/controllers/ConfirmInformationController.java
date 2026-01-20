package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.exception.ErrorType;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.Sex;
import org.example.model.user.User;
import org.example.services.IEmailService;
import org.example.services.IUserService;
import org.example.services.impl.IEmailServiceImpl;
import org.example.services.impl.IUserServiceImpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;

public class ConfirmInformationController {

    @FXML private ImageView uploadImageButton;
    @FXML private Button btnUploadAvatar;

    @FXML private TextField hoVaTenText;
    @FXML private TextField sdtText;
    @FXML private TextField emailText;
    @FXML private TextField confirmEmailText;

    @FXML private ComboBox<Integer> yearComboBox;

    @FXML private RadioButton namRadioButton;
    @FXML private RadioButton nuRadioButton;
    @FXML private RadioButton khacRadioButton;
    @FXML private ToggleGroup genderGroup;

    @FXML private Button emailConfirmButton;
    @FXML private Button sendingCodeButton;
    @FXML private Button confirmButton;

    @FXML private CheckBox agreeCheckBox;
    @FXML private Hyperlink dieuKhoanVaChinhSachHyperlink;

    @FXML private Label pleaseCompleteAllFieldsText;
    @FXML private Label invalidCodeText;
    @FXML private Label checkTheBoxText;
    @FXML private Label failToSendEmailText;
    @FXML private Label codeSentSuccessfullyText;

    private final IEmailService emailService = new IEmailServiceImpl();
    private final IUserService userService = new IUserServiceImpl();

    private User currentUser;
    private String serverOtp = null;
    private String selectedAvatarPath = null;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            if (currentUser.getEmail() != null) {
                emailText.setText(currentUser.getEmail());
            }
            if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
                try {
                    File file = new File(currentUser.getAvatarUrl());
                    if (file.exists()) {
                        uploadImageButton.setImage(new Image(file.toURI().toString()));
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi load ảnh cũ: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(
                pleaseCompleteAllFieldsText,
                invalidCodeText,
                checkTheBoxText,
                failToSendEmailText,
                codeSentSuccessfullyText
        );

        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i >= 1950; i--) {
            yearComboBox.getItems().add(i);
        }

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
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) btnUploadAvatar.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            selectedAvatarPath = selectedFile.getAbsolutePath();
            uploadImageButton.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    private void handleSendCode() {
        UIExceptionHandler.hideError(pleaseCompleteAllFieldsText, failToSendEmailText, codeSentSuccessfullyText);
        String email = emailText.getText().trim();

        if (email.isEmpty()) {
            UIExceptionHandler.showError(pleaseCompleteAllFieldsText, ErrorType.PLEASE_COMPLETE_ALL_FIELDS);
            return;
        }

        emailConfirmButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        new Thread(() -> {
            try {
                int otpValue = 100000 + new Random().nextInt(900000);
                String tempOtp = String.valueOf(otpValue);

                String subject = "WOWTruyen - Mã xác thực thông tin";
                String body = "Mã xác thực của bạn là: " + tempOtp + "\nVui lòng nhập mã này vào phần mềm.";

                boolean isSent = emailService.sendEmail(email, subject, body);

                Platform.runLater(() -> {
                    if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                    emailConfirmButton.setVisible(true);

                    if (isSent) {
                        serverOtp = tempOtp;
                        UIExceptionHandler.showSuccess(codeSentSuccessfullyText, ErrorType.CODE_SENT_SUCCESS);
                    } else {
                        serverOtp = null;
                        UIExceptionHandler.showError(failToSendEmailText, ErrorType.FAIL_TO_SEND_EMAIL);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                    emailConfirmButton.setVisible(true);
                    UIExceptionHandler.showError(failToSendEmailText, ErrorType.FAIL_TO_SEND_EMAIL);
                });
            }
        }).start();
    }

    private void handleConfirm() {
        UIExceptionHandler.hideError(checkTheBoxText, pleaseCompleteAllFieldsText, invalidCodeText);

        if (!agreeCheckBox.isSelected()) {
            UIExceptionHandler.showError(checkTheBoxText, ErrorType.CHECK_TERMS_BOX);
            return;
        }

        if (hoVaTenText.getText().trim().isEmpty() ||
                sdtText.getText().trim().isEmpty() ||
                emailText.getText().trim().isEmpty() ||
                confirmEmailText.getText().trim().isEmpty()) {

            UIExceptionHandler.showError(pleaseCompleteAllFieldsText, ErrorType.PLEASE_COMPLETE_ALL_FIELDS);
            return;
        }

        String inputCode = confirmEmailText.getText().trim();
        if (serverOtp == null || !serverOtp.equals(inputCode)) {
            UIExceptionHandler.showError(invalidCodeText, ErrorType.INVALID_CODE);
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

        if (selectedAvatarPath != null) {
            currentUser.setAvatarUrl(selectedAvatarPath);
        } else {
            if (currentUser.getAvatarUrl() == null) {
                currentUser.setAvatarUrl("");
            }
        }

        boolean isUpdateSuccess = userService.updateUserProfile(currentUser);
        boolean isDisableFirstLoginSuccess = userService.disableFirstLogin(currentUser.getId());

        if (isUpdateSuccess && isDisableFirstLoginSuccess) {
            openHomeScreen();
        } else {
            System.err.println("Lỗi lưu dữ liệu xuống DB");
            UIExceptionHandler.showCustomError(failToSendEmailText, "Lỗi lưu dữ liệu. Vui lòng thử lại!");
        }
    }

    private void openHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home_screen.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("WOWTruyen - Trang chủ");
            stage.setScene(new Scene(root));
            stage.show();
            ((Stage) confirmButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}