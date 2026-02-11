package org.example.controllers.authentication;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.constant.MessageConstant;
import org.example.exception.AppException;
import org.example.exception.AuthException;
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
import java.util.stream.IntStream;

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
    private File selectedAvatarFile;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (currentUser != null) {
            if (currentUser.getEmail() != null) emailText.setText(currentUser.getEmail());
            if (currentUser.getFullName() != null) hoVaTenText.setText(currentUser.getFullName());
        }
    }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel);
        if (codeSentSuccessfullyText != null) codeSentSuccessfullyText.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        setupYearComboBox();

        setupGenderGroup();

        emailText.setDisable(true);

        btnUploadAvatar.setOnAction(e -> handleUploadAvatar());
        emailConfirmButton.setOnAction(e -> handleSendOtp());
        confirmButton.setOnAction(e -> handleConfirm());
    }

    private void setupYearComboBox() {
        ObservableList<Integer> years = FXCollections.observableArrayList();
        int currentYear = LocalDate.now().getYear();
        IntStream.rangeClosed(1950, currentYear).forEach(years::add);
        FXCollections.reverse(years);
        yearComboBox.setItems(years);
        yearComboBox.setValue(2000);
    }

    private void setupGenderGroup() {
        if (genderGroup == null) {
            genderGroup = new ToggleGroup();
        }
        namRadioButton.setToggleGroup(genderGroup);
        nuRadioButton.setToggleGroup(genderGroup);
        khacRadioButton.setToggleGroup(genderGroup);
        khacRadioButton.setSelected(true);
    }

    private void handleUploadAvatar() {
        UIExceptionHandler.hideError(errorLabel);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(MessageConstant.CHOOSE_AVATAR);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(btnUploadAvatar.getScene().getWindow());

        if (file != null) {
            selectedAvatarFile = file;
            uploadImageButton.setImage(new Image(file.toURI().toString()));
        }
    }

    private void handleSendOtp() {
        UIExceptionHandler.hideError(errorLabel);
        if (codeSentSuccessfullyText != null) codeSentSuccessfullyText.setVisible(false);

        String email = emailText.getText().trim();
        if (email.isEmpty()) return;

        emailConfirmButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        Task<Boolean> sendOtpTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return userService.sendOtp(email);
            }
        };

        sendOtpTask.setOnSucceeded(e -> {
            if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
            emailConfirmButton.setVisible(true);
            emailConfirmButton.setText(MessageConstant.RESEND);

            if (sendOtpTask.getValue()) {
                if (codeSentSuccessfullyText != null) codeSentSuccessfullyText.setVisible(true);
            } else {
                UIExceptionHandler.showError(errorLabel, MessageConstant.OTP_RESEND_FAIL);
            }
        });

        sendOtpTask.setOnFailed(e -> {
            if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
            emailConfirmButton.setVisible(true);

            Throwable ex = sendOtpTask.getException();
            if (ex instanceof AppException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else {
                UIExceptionHandler.handle(new Exception(ex), errorLabel);
            }

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(sendOtpTask).start();
    }

    private void handleConfirm() {
        UIExceptionHandler.hideError(errorLabel);

        if (!agreeCheckBox.isSelected()) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.AGREE_CHECK_BOX);
            return;
        }

        if (ValidationUtils.areFieldsEmpty(hoVaTenText, sdtText)) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.LOGIN_EMPTY_FIELDS);
            return;
        }

        if (!ValidationUtils.isValidPhoneNumber(sdtText.getText().trim())) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.VALIDATION_PHONE_INVALID);
            return;
        }

        String inputOtp = confirmEmailText.getText().trim();
        if (inputOtp.isEmpty()) {
            UIExceptionHandler.showError(errorLabel, MessageConstant.OTP_EMPTY);
            return;
        }

        confirmButton.setDisable(true);
        confirmButton.setText(MessageConstant.CONFIRM_LOADING);

        Task<Void> processTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                OtpStatus status = userService.verifyOtp(currentUser.getEmail(), inputOtp);
                if (status != OtpStatus.SUCCESS) {
                    throw new AuthException(MessageConstant.OTP_INVALID);
                }

                String avatarPath = (currentUser.getAvatarUrl() != null) ? currentUser.getAvatarUrl() : "";
                if (selectedAvatarFile != null) {
                    avatarPath = saveAvatarToLocal(selectedAvatarFile);
                    if (avatarPath == null) throw new AppException(MessageConstant.ERR_DB_SAVE);
                }

                currentUser.setFullName(hoVaTenText.getText().trim());
                currentUser.setPhoneNumber(sdtText.getText().trim());
                currentUser.setAvatarUrl(avatarPath);

                if (namRadioButton.isSelected()) currentUser.setGender(Gender.Male);
                else if (nuRadioButton.isSelected()) currentUser.setGender(Gender.Female);
                else currentUser.setGender(Gender.Other);

                if (!userService.updateUserProfile(currentUser)) {
                    throw new AppException(MessageConstant.UPDATE_FAIL);
                }

                userService.disableFirstLogin(currentUser.getId());

                return null;
            }
        };

        processTask.setOnSucceeded(e -> {
            SessionManager.getInstance().setCurrentUser(currentUser);
            SceneUtils.switchScene(confirmButton, "/view/read/home_screen.fxml", MessageConstant.TITLE_HOME);
        });

        processTask.setOnFailed(e -> {
            confirmButton.setDisable(false);
            confirmButton.setText(MessageConstant.TITLE_CONFIRM_INFO);

            Throwable ex = processTask.getException();
            if (ex instanceof AuthException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else if (ex instanceof AppException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else {
                UIExceptionHandler.handle(new Exception(ex), errorLabel);
            }

            throw new AppException(MessageConstant.ERR_SYSTEM, ex);
        });

        new Thread(processTask).start();
    }

    private String saveAvatarToLocal(File sourceFile) {
        try {
            String userDir = System.getProperty("user.dir");
            Path avatarDir = Paths.get(userDir, "user_data", "avatars");
            if (!Files.exists(avatarDir)) Files.createDirectories(avatarDir);

            String fileName = "avatar_" + UUID.randomUUID() + getFileExtension(sourceFile);
            Path destPath = avatarDir.resolve(fileName);
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

            return destPath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new AppException(MessageConstant.ERR_SYSTEM, e);
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        return (lastIndexOf == -1) ? "" : name.substring(lastIndexOf);
    }
}