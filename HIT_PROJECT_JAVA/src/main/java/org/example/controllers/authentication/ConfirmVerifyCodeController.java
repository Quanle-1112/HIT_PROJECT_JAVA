package org.example.controllers.authentication;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.example.constant.MessageConstant;
import org.example.exception.AppException;
import org.example.exception.AuthException;
import org.example.exception.UIExceptionHandler;
import org.example.services.IForgotPasswordService;
import org.example.services.impl.IForgotPasswordServiceImpl;
import org.example.utils.SceneUtils;

public class ConfirmVerifyCodeController {
    @FXML private Button returnButton, verifyButton, resendCodeButton, sendingCodeButton;
    @FXML private TextField codeTextField;
    @FXML private Label errorLabel;

    private final IForgotPasswordService service = new IForgotPasswordServiceImpl();
    private String userEmail;

    public void setInitData(String email) { this.userEmail = email; }

    @FXML
    public void initialize() {
        UIExceptionHandler.hideError(errorLabel);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(false);

        verifyButton.setOnAction(event -> handleVerify());
        resendCodeButton.setOnAction(event -> handleResend());
        returnButton.setOnAction(event ->
                SceneUtils.switchScene(returnButton, "/view/authentication/forgot_password.fxml", MessageConstant.TITLE_FORGOT_PASS)
        );

        startTimer();
    }

    private void handleVerify() {
        UIExceptionHandler.hideError(errorLabel);
        String otp = codeTextField.getText().trim();

        if (otp.isEmpty()) {
            UIExceptionHandler.showError(errorLabel, "Vui lòng nhập mã xác thực!");
            return;
        }

        verifyButton.setDisable(true);
        verifyButton.setText("Đang kiểm tra...");

        Task<Void> verifyTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                service.verifyOtp(userEmail, otp);
                return null;
            }
        };

        verifyTask.setOnSucceeded(e -> {
            ChangePasswordToLoginController controller = SceneUtils.switchScene(
                    verifyButton,
                    "/view/authentication/change_password_to_login.fxml",
                    "Đổi mật khẩu mới"
            );
            if (controller != null) controller.setUserEmail(userEmail);
        });

        verifyTask.setOnFailed(e -> {
            verifyButton.setDisable(false);
            verifyButton.setText("Verify Code");

            Throwable ex = verifyTask.getException();
            if (ex instanceof AuthException) {
                UIExceptionHandler.showError(errorLabel, ex.getMessage());
            } else {
                UIExceptionHandler.handle(new Exception(ex), errorLabel);
            }
        });

        new Thread(verifyTask).start();
    }

    private void handleResend() {
        UIExceptionHandler.hideError(errorLabel);
        resendCodeButton.setVisible(false);
        if (sendingCodeButton != null) sendingCodeButton.setVisible(true);

        Task<Void> resendTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                service.sendOtp(userEmail);
                return null;
            }
        };

        resendTask.setOnSucceeded(e -> {
            if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
            resendCodeButton.setVisible(true);

            UIExceptionHandler.showError(errorLabel, MessageConstant.OTP_SENT_SUCCESS);
            errorLabel.setStyle("-fx-text-fill: green;");

            startTimer();
        });

        resendTask.setOnFailed(e -> {
            if (sendingCodeButton != null) sendingCodeButton.setVisible(false);
            resendCodeButton.setVisible(true);

            Throwable ex = resendTask.getException();
            UIExceptionHandler.showError(errorLabel, ex.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        });

        new Thread(resendTask).start();
    }

    private void startTimer() {
        resendCodeButton.setDisable(true);
        final int[] seconds = {30};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds[0]--;
            resendCodeButton.setText("Gửi lại mã (" + seconds[0] + "s)");
            if (seconds[0] <= 0) {
                resendCodeButton.setDisable(false);
                resendCodeButton.setText("Gửi lại mã");
            }
        }));
        timeline.setCycleCount(30);
        timeline.play();
    }
}