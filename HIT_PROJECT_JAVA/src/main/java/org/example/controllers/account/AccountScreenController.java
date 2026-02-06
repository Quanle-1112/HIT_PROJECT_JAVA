package org.example.controllers.account;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
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
        TextInputDialog dialog = new TextInputDialog(currentUser.getFullName());
        dialog.setTitle("Đổi tên hiển thị");
        dialog.setHeaderText("Nhập tên hiển thị mới:");
        dialog.setContentText("Tên mới:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            String newName = name.trim();
            if (newName.isEmpty()) return;

            fullNameLabel.setText(newName);
            currentUser.setFullName(newName);
            saveUserChanges("Đã đổi tên thành công.");
        });
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

    private void setupNavigation() {
        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", "Trang chủ"));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", "Lịch sử"));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", "Yêu thích"));
        if (btnAI != null) btnAI.setOnAction(e -> System.out.println("Tính năng AI đang phát triển"));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}