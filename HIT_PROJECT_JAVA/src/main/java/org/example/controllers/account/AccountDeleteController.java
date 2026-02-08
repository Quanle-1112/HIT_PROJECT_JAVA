package org.example.controllers.account;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.model.user.OtpStatus;
import org.example.model.user.User;
import org.example.services.IUserService;
import org.example.services.impl.IUserServiceImpl;
import org.example.utils.SessionManager;

import java.io.IOException;

public class AccountDeleteController {

    @FXML private Label lblStatus;
    @FXML private TextField txtOtp;
    @FXML private Button btnResendOtp;
    @FXML private Button btnConfirmDelete;
    @FXML private Button btnCancel;
    @FXML private Button btnClose;

    private final IUserService userService = new IUserServiceImpl();
    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    private Timeline timeline;
    private int countdownTime = 30;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser != null) {
            lblStatus.setText("Đang gửi mã xác thực tới: " + currentUser.getEmail());
            sendOtp();
        } else {
            lblStatus.setText("Lỗi phiên đăng nhập!");
            btnConfirmDelete.setDisable(true);
        }

        btnResendOtp.setOnAction(e -> handleResendOtp());
        btnConfirmDelete.setOnAction(e -> handleDelete());

        btnCancel.setOnAction(e -> closeDialog());
        btnClose.setOnAction(e -> closeDialog());
    }

    private void sendOtp() {
        btnResendOtp.setDisable(true);
        new Thread(() -> {
            boolean success = userService.sendOtp(currentUser.getEmail());
            Platform.runLater(() -> {
                if (success) {
                    lblStatus.setText(MessageConstant.OTP_SENT_SUCCESS);
                    lblStatus.setStyle("-fx-text-fill:  #19345D;");
                    startTimer();
                } else {
                    lblStatus.setText(MessageConstant.OTP_RESEND_FAIL);
                    lblStatus.setStyle("-fx-text-fill: red;");
                    btnResendOtp.setDisable(false);
                }
            });
        }).start();
    }

    private void handleResendOtp() {
        lblStatus.setText("Đang gửi lại mã...");
        lblStatus.setStyle("-fx-text-fill: #666;");
        sendOtp();
    }

    private void startTimer() {
        countdownTime = 30;
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            countdownTime--;
            btnResendOtp.setText("Gửi lại (" + countdownTime + "s)");
            if (countdownTime <= 0) {
                timeline.stop();
                btnResendOtp.setText("Gửi lại mã");
                btnResendOtp.setDisable(false);
            }
        }));
        timeline.setCycleCount(30);
        timeline.play();
    }

    private void handleDelete() {
        String inputOtp = txtOtp.getText().trim();
        if (inputOtp.isEmpty()) {
            lblStatus.setText(MessageConstant.OTP_EMPTY);
            lblStatus.setStyle("-fx-text-fill: red;");
            return;
        }

        OtpStatus status = userService.verifyOtp(currentUser.getEmail(), inputOtp);

        if (status == OtpStatus.SUCCESS) {
            lblStatus.setText("OTP chính xác. Đang xóa dữ liệu...");
            btnConfirmDelete.setDisable(true);

            new Thread(() -> {
                boolean deleteSuccess = userDAO.deleteUser(currentUser.getId());

                Platform.runLater(() -> {
                    if (deleteSuccess) {
                        performLogoutAndRedirect();
                    } else {
                        lblStatus.setText(MessageConstant.ACCOUNT_DELETE_FAIL);
                        lblStatus.setStyle("-fx-text-fill: red;");
                        btnConfirmDelete.setDisable(false);
                    }
                });
            }).start();

        } else if (status == OtpStatus.EXPIRED_CODE) {
            lblStatus.setText(MessageConstant.OTP_EXPIRED);
            lblStatus.setStyle("-fx-text-fill: red;");
        } else {
            lblStatus.setText(MessageConstant.OTP_INVALID);
            lblStatus.setStyle("-fx-text-fill: red;");
        }
    }

    private void performLogoutAndRedirect() {
        SessionManager.getInstance().logout();

        Stage dialogStage = (Stage) btnConfirmDelete.getScene().getWindow();
        Window owner = dialogStage.getOwner();

        if (owner instanceof Stage) {
            Stage mainStage = (Stage) owner;
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/read/start_screen.fxml"));
                mainStage.setScene(new Scene(root));
                mainStage.setTitle("WOWTruyen - Welcome");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dialogStage.close();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}