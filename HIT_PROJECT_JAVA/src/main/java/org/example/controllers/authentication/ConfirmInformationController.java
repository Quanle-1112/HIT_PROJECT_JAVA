package org.example.controllers.authentication;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.constant.MessageConstant;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.Gender;
import org.example.model.user.OtpStatus;
import org.example.model.user.User;
import org.example.services.IUserService;
import org.example.services.impl.IUserServiceImpl;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;
import org.example.utils.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

public class ConfirmInformationController {

    @FXML private ImageView uploadImageButton;
    @FXML private Button btnUploadAvatar, emailConfirmButton, sendingCodeButton, confirmButton;
    @FXML private TextField hoVaTenText, sdtText, emailText, confirmEmailText;

    @FXML private ComboBox<Integer> yearComboBox;

    @FXML private RadioButton namRadioButton, nuRadioButton, khacRadioButton;
    @FXML private ToggleGroup genderGroup;
    @FXML private CheckBox agreeCheckBox;

    @FXML private Label errorLabel;

    @FXML private Label codeSentSuccessfullyText;

    private final IUserService userService = new IUserServiceImpl();
    private User currentUser;
    private File selectedImageFile = null;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            emailText.setText(currentUser.getEmail());
            emailText.setEditable(true);
        }
    }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel, codeSentSuccessfullyText);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        if (genderGroup == null) {
            genderGroup = new ToggleGroup();
            namRadioButton.setToggleGroup(genderGroup);
            nuRadioButton.setToggleGroup(genderGroup);
            khacRadioButton.setToggleGroup(genderGroup);
        }

        initYearComboBox();

        btnUploadAvatar.setOnAction(e -> handleUploadAvatar());
        emailConfirmButton.setOnAction(e -> handleSendCode());
        confirmButton.setOnAction(e -> handleConfirm());
    }

    private void initYearComboBox() {
        if (yearComboBox != null) {
            int currentYear = LocalDate.now().getYear();
            ObservableList<Integer> years = FXCollections.observableArrayList();
            for (int i = currentYear - 5; i >= 1950; i--) {
                years.add(i);
            }
            yearComboBox.setItems(years);
            yearComboBox.getSelectionModel().selectFirst();
        }
    }

    private void handleUploadAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(btnUploadAvatar.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            uploadImageButton.setImage(image);

            double radius = Math.min(uploadImageButton.getFitWidth(), uploadImageButton.getFitHeight()) / 2;
            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(uploadImageButton.getFitWidth() / 2, uploadImageButton.getFitHeight() / 2, radius);
            uploadImageButton.setClip(clip);
        }
    }

    private void handleSendCode() {
        UIExceptionHandler.hideError(errorLabel, codeSentSuccessfullyText);

        String emailInput = emailText.getText().trim();

        if (!ValidationUtils.isValidEmail(emailInput)) {
            showError(MessageConstant.REGISTER_EMAIL_INVALID);
            return;
        }

        emailConfirmButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        new Thread(() -> {
            boolean success = userService.sendOtp(emailInput);

            Platform.runLater(() -> {
                if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
                emailConfirmButton.setVisible(true);

                if (success) {
                    if (codeSentSuccessfullyText != null) {
                        codeSentSuccessfullyText.setVisible(true);
                    }
                } else {
                    showError(MessageConstant.FORGOT_PASS_SEND_FAIL);
                }
            });
        }).start();
    }

    private void handleConfirm() {
        UIExceptionHandler.hideError(errorLabel, codeSentSuccessfullyText);

        if (!agreeCheckBox.isSelected()) {
            showError(MessageConstant.AGREE_CHECK_BOX);
            return;
        }

        if (ValidationUtils.areFieldsEmpty(hoVaTenText, sdtText, emailText, confirmEmailText)) {
            showError(MessageConstant.LOGIN_EMPTY_FIELDS);
            return;
        }

        if (!ValidationUtils.isValidPhoneNumber(sdtText.getText().trim())) {
            showError(MessageConstant.VALIDATION_PHONE_INVALID);
            return;
        }

        String emailInput = emailText.getText().trim();
        String otp = confirmEmailText.getText().trim();

        OtpStatus status = userService.verifyOtp(emailInput, otp);

        if (status == OtpStatus.INVALID_CODE) {
            showError(MessageConstant.OTP_INVALID);
            return;
        }
        if (status == OtpStatus.EXPIRED_CODE) {
            showError(MessageConstant.OTP_EXPIRED);
            return;
        }

        confirmButton.setDisable(true);
        confirmButton.setText("Đang xử lý...");

        new Thread(() -> {
            try {
                if (currentUser != null) {
                    currentUser.setFullName(hoVaTenText.getText().trim());
                    currentUser.setPhoneNumber(sdtText.getText().trim());
                    currentUser.setEmail(emailInput);

                    Gender gender = Gender.Other;
                    if (namRadioButton.isSelected()) gender = Gender.Male;
                    else if (nuRadioButton.isSelected()) gender = Gender.Female;
                    currentUser.setGender(gender);

                    if (selectedImageFile != null) {
                        String savedPath = saveAvatarToLocal(selectedImageFile);
                        if (savedPath != null) {
                            currentUser.setAvatarUrl(savedPath);
                        }
                    }

                    boolean updateSuccess = userService.updateUserProfile(currentUser);

                    boolean disableFirstLoginSuccess = false;
                    if (updateSuccess) {
                        disableFirstLoginSuccess = userService.disableFirstLogin(currentUser.getId());
                    }

                    boolean finalResult = updateSuccess && disableFirstLoginSuccess;

                    Platform.runLater(() -> {
                        if (finalResult) {
                            SessionManager.getInstance().setCurrentUser(currentUser);
                            SceneUtils.openNewWindow("/view/read/home_screen.fxml", "Trang chủ", confirmButton);
                        } else {
                            showError(MessageConstant.UPDATE_FAIL);
                            confirmButton.setDisable(false);
                            confirmButton.setText("XÁC NHẬN");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showError("Lỗi: " + e.getMessage());
                    confirmButton.setDisable(false);
                    confirmButton.setText("XÁC NHẬN");
                });
            }
        }).start();
    }

    private String saveAvatarToLocal(File sourceFile) {
        try {
            String userDir = System.getProperty("user.dir");
            Path avatarDir = Paths.get(userDir, "user_data", "avatars");

            if (!Files.exists(avatarDir)) {
                Files.createDirectories(avatarDir);
            }

            String fileName = "avatar_" + UUID.randomUUID() + getFileExtension(sourceFile);
            Path destPath = avatarDir.resolve(fileName);

            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

            return destPath.toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        return (lastIndexOf == -1) ? "" : name.substring(lastIndexOf);
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            UIExceptionHandler.showError(errorLabel);
        }
    }
}