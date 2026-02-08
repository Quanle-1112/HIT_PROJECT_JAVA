package org.example.controllers.account;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.dao.UserDAO;
import org.example.model.user.User;
import org.example.utils.ImageLoaderGlobal;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

import java.io.File;
import java.util.Optional;

public class AccountScreenController {

    @FXML private ImageView avatarView;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;

    @FXML private Button btnEditAvatar;
    @FXML private Button btnLogout;
    @FXML private Button btnChangeName;
    @FXML private Button btnChangePass;
    @FXML private Button btnForgotPassword;
    @FXML private Button btnDeleteAccount;

    @FXML private Button btnHome, btnHistory, btnFavorite, btnAI, btnAccount;

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            handleLogout();
            return;
        }

        loadUserInfo();

        setupEventHandlers();

        setupNavigation();
    }

    private void loadUserInfo() {
        fullNameLabel.setText(currentUser.getFullName());
        emailLabel.setText(currentUser.getEmail());

        if (avatarView != null) {
            double radius = avatarView.getFitWidth() / 2;
            Circle clip = new Circle(radius, radius, radius);
            avatarView.setClip(clip);
        }

        String avatarUrl = currentUser.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            if (avatarUrl.startsWith("http")) {
                ImageLoaderGlobal.setImage(avatarUrl, avatarView);
            } else {
                try {
                    File file = new File(avatarUrl);
                    if (file.exists()) {
                        avatarView.setImage(new Image(file.toURI().toString()));
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi load ảnh local: " + e.getMessage());
                }
            }
        }
    }

    private void setupEventHandlers() {
        if (btnEditAvatar != null) {
            btnEditAvatar.setOnAction(e -> handleChangeAvatar());
        }

        if (btnLogout != null) {
            btnLogout.setOnAction(e -> handleLogout());
        }

        if (btnChangeName != null) {
            btnChangeName.setOnAction(e -> handleUpdateName());
        }
        if (btnChangePass != null) {
            btnChangePass.setOnAction(e -> {
                SceneUtils.switchScene(btnChangePass, "/view/account/change_password.fxml", "Đổi mật khẩu");
            });
        }
        if (btnForgotPassword != null) {
            btnForgotPassword.setOnAction(e -> openForgotPasswordDialog());
        }
        if (btnDeleteAccount != null) {
            btnDeleteAccount.setOnAction(e -> openDeleteConfirmationDialog());
        }
    }

    private void handleChangeAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện mới");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(avatarView.getScene().getWindow());

        if (selectedFile != null) {
            Image newImage = new Image(selectedFile.toURI().toString());
            avatarView.setImage(newImage);

            String filePath = selectedFile.getAbsolutePath();
            currentUser.setAvatarUrl(filePath);
            saveUserChanges("Đã cập nhật ảnh đại diện.");
        }
    }

    private void handleUpdateName() {
        SceneUtils.switchScene(btnChangeName, "/view/account/change_name.fxml", "Đổi tên hiển thị");
    }

    private void saveUserChanges(String msg) {
        boolean success = userDAO.updateUserProfile(currentUser);
        if (success) {
            SessionManager.getInstance().setCurrentUser(currentUser);
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể lưu thay đổi.");
        }
    }

    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneUtils.openNewWindow("/view/read/start_screen.fxml", "Welcome", btnLogout);
    }
    private void openForgotPasswordDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/account/account_forgot_password_dialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Bảo mật tài khoản");
            stage.setScene(new Scene(root));

            Stage parentStage = (Stage) btnForgotPassword.getScene().getWindow();
            stage.initOwner(parentStage);

            stage.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi mở dialog đổi mật khẩu: " + ex.getMessage());
        }
    }

    private void openDeleteConfirmationDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/account/account_delete_confirmation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Xác nhận xóa tài khoản");
            stage.setScene(new Scene(root));

            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);


            Stage mainStage = (Stage) btnDeleteAccount.getScene().getWindow();
            stage.initOwner(mainStage);

            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi mở dialog xóa tài khoản: " + ex.getMessage());
        }
    }



    private void setupNavigation() {
        if (btnAccount != null) {
            btnAccount.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 10; -fx-text-fill: #19345D; -fx-font-weight: bold;");
            btnAccount.setDisable(true);
        }
        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", "Trang chủ"));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", "Lịch sử"));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", "Yêu thích"));
        if (btnAI != null) btnAI.setOnAction(e -> System.out.println("Tính năng AI đang phát triển"));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", "Tài khoản"));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}