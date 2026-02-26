package org.example.controllers.account;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.AppException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.User;
import org.example.utils.ImageLoaderGlobal;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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

        setupNavigation();

        if (currentUser != null) {
            loadUserData();
        } else {
            SceneUtils.openNewWindow("/view/authentication/login.fxml", MessageConstant.TITLE_LOGIN, btnAccount);
            return;
        }

        btnEditAvatar.setOnAction(e -> handleUploadAvatar());
        btnChangeName.setOnAction(e -> openDialog("/view/account/change_name.fxml", "Đổi tên hiển thị"));
        btnChangePass.setOnAction(e -> openDialog("/view/account/change_password.fxml", "Đổi mật khẩu"));
        btnForgotPassword.setOnAction(e -> openDialog("/view/account/account_forgot_password_dialog.fxml", "Xác thực bảo mật"));
        btnDeleteAccount.setOnAction(e -> openDialog("/view/account/account_delete_confirmation.fxml", "Xóa tài khoản vĩnh viễn"));
        btnLogout.setOnAction(e -> handleLogout());
    }

    private void loadUserData() {
        fullNameLabel.setText(currentUser.getFullName());
        emailLabel.setText(currentUser.getEmail());

        double radius = avatarView.getFitWidth() / 2;
        Circle clip = new Circle(radius, radius, radius);
        avatarView.setClip(clip);

        String avatarUrl = currentUser.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            File localFile = new File(avatarUrl);
            if (localFile.exists()) {
                avatarView.setImage(new Image(localFile.toURI().toString()));
            } else {
                ImageLoaderGlobal.setImage(avatarUrl, avatarView);
            }
        }
    }

    private void handleUploadAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(MessageConstant.CHOOSE_AVATAR);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(btnEditAvatar.getScene().getWindow());

        if (file != null) {
            Task<String> uploadTask = new Task<>() {
                @Override
                protected String call() throws Exception {
                    String newPath = saveAvatarToLocal(file);
                    if (newPath == null) throw new AppException(MessageConstant.ERR_DB_SAVE);

                    if (!userDAO.updateAvatar(currentUser.getId(), newPath)) {
                        throw new AppException(MessageConstant.UPDATE_FAIL);
                    }
                    return newPath;
                }
            };

            uploadTask.setOnSucceeded(e -> {
                String newPath = uploadTask.getValue();
                currentUser.setAvatarUrl(newPath);
                SessionManager.getInstance().setCurrentUser(currentUser);
                avatarView.setImage(new Image(new File(newPath).toURI().toString()));
            });

            uploadTask.setOnFailed(e -> {
                throw new AppException(MessageConstant.ERR_SYSTEM, uploadTask.getException());
            });

            new Thread(uploadTask).start();
        }
    }

    private String saveAvatarToLocal(File sourceFile) {
        try {
            String userDir = System.getProperty("user.dir");
            Path avatarDir = Paths.get(userDir, "user_data", "avatars");
            if (!Files.exists(avatarDir)) Files.createDirectories(avatarDir);

            String fileName = "avatar_" + currentUser.getId() + "_" + UUID.randomUUID() + ".png";
            Path destPath = avatarDir.resolve(fileName);
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            return destPath.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new AppException(MessageConstant.ERR_SYSTEM, e);
        }
    }

    private void openDialog(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnAccount.getScene().getWindow());
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

            currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                loadUserData();
            }

        } catch (IOException e) {
            throw new AppException(MessageConstant.ERR_SYSTEM, e);
        }
    }

    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneUtils.openNewWindow("/view/read/start_screen.fxml", MessageConstant.TITLE_APP, btnLogout);
    }

    private void setupNavigation() {
        if (btnAccount != null) {
            btnAccount.setStyle(MessageConstant.BUTTON_COLOR);
            btnAccount.setDisable(true);
        }
        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", MessageConstant.TITLE_HOME));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", MessageConstant.TITLE_HISTORY));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", MessageConstant.TITLE_FAVORITE));
        if (btnAI != null) btnAI.setOnAction(e -> SceneUtils.switchScene(btnAI, "/view/chatbox/chat_box.fxml", MessageConstant.CHAT_AI_TITLE));
    }
}