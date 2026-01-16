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

    // Các trường nhập liệu
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
    @FXML private Label codeSentSuccessfullyText;
    @FXML private Label failToSendEmailText;

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
        hideAllErrors();
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
        hideAllErrors();

        String email = emailText.getText().trim();

        if (email.isEmpty()) {
            if (pleaseCompleteAllFieldsText != null) pleaseCompleteAllFieldsText.setVisible(true);
            return;
        }

        emailConfirmButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        // Chạy luồng gửi mail ngầm
        new Thread(() -> {
            try {
                // Tạo OTP
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
                        if (codeSentSuccessfullyText != null) codeSentSuccessfullyText.setVisible(true);
                    } else {
                        serverOtp = null;
                        if (failToSendEmailText != null) failToSendEmailText.setVisible(true);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                    emailConfirmButton.setVisible(true);
                    if (failToSendEmailText != null) failToSendEmailText.setVisible(true);
                });
            }
        }).start();
    }

    private void handleConfirm() {
        if (pleaseCompleteAllFieldsText != null) pleaseCompleteAllFieldsText.setVisible(false);
        if (invalidCodeText != null) invalidCodeText.setVisible(false);
        if (checkTheBoxText != null) checkTheBoxText.setVisible(false);
        if (failToSendEmailText != null) failToSendEmailText.setVisible(false);

        if (!agreeCheckBox.isSelected()) {
            if (checkTheBoxText != null) checkTheBoxText.setVisible(true);
            return;
        }

        if (hoVaTenText.getText().trim().isEmpty() ||
                sdtText.getText().trim().isEmpty() ||
                emailText.getText().trim().isEmpty() ||
                confirmEmailText.getText().trim().isEmpty()) {

            if (pleaseCompleteAllFieldsText != null) pleaseCompleteAllFieldsText.setVisible(true);
            return;
        }

        String inputCode = confirmEmailText.getText().trim();
        if (serverOtp == null || !serverOtp.equals(inputCode)) {
            if (invalidCodeText != null) invalidCodeText.setVisible(true);
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
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể lưu thông tin vào CSDL.");
        }
    }


    private void hideAllErrors() {
        if (pleaseCompleteAllFieldsText != null) pleaseCompleteAllFieldsText.setVisible(false);
        if (invalidCodeText != null) invalidCodeText.setVisible(false);
        if (checkTheBoxText != null) checkTheBoxText.setVisible(false);
        if (codeSentSuccessfullyText != null) codeSentSuccessfullyText.setVisible(false);
        if (failToSendEmailText != null) failToSendEmailText.setVisible(false);
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
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở màn hình trang chủ.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}