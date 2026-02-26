package org.example.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.constant.MessageConstant;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

public class AdminDashboardController {

    @FXML private VBox cardUserManager;
    @FXML private VBox cardHideBook;
    @FXML private VBox cardStats;
    @FXML private Button btnLogout;
    @FXML private Label lblAdminName;

    @FXML
    public void initialize() {
        if (SessionManager.getInstance().getCurrentUser() != null) {
            lblAdminName.setText(MessageConstant.HELLO + SessionManager.getInstance().getCurrentUser().getFullName());
        }

        cardUserManager.setOnMouseClicked(e ->
                SceneUtils.switchScene(cardUserManager, "/view/admin/user_manager_screen.fxml", "Quản lý người dùng")
        );

        cardHideBook.setOnMouseClicked(e ->
                SceneUtils.switchScene(cardHideBook, "/view/admin/manage_books_screen.fxml", "Quản lý truyện")
        );

        cardStats.setOnMouseClicked(e ->
                SceneUtils.switchScene(cardStats, "/view/admin/stats_screen.fxml", "Thống kê hệ thống")
        );

        if (btnLogout != null) {
            btnLogout.setOnAction(e -> {
                SessionManager.getInstance().logout();
                SceneUtils.switchScene(btnLogout, "/view/authentication/login.fxml", MessageConstant.TITLE_LOGIN);
            });
        }
    }
}