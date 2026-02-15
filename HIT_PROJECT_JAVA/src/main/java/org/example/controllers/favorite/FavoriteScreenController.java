package org.example.controllers.favorite;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.constant.MessageConstant;
import org.example.dao.FavoriteDAO;
import org.example.exception.AppException;
import org.example.exception.DatabaseException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.UserFavorite;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.util.List;

public class FavoriteScreenController {

    @FXML private VBox listContainer;
    @FXML private Label statusLabel;

    @FXML private Button btnHome, btnHistory, btnFavorite, btnAI, btnAccount;

    private final FavoriteDAO favoriteDAO = new FavoriteDAO();

    @FXML
    public void initialize() {
        setupNavigation();
        UIExceptionHandler.hideError(statusLabel);

        int userId = SessionManager.getInstance().getCurrentUserId();

        if (userId != -1) {
            loadFavoriteData();
        } else {
            listContainer.getChildren().clear();
            showEmptyMessage(MessageConstant.FAVORITE_LOGIN_REQ);
        }
    }

    private void loadFavoriteData() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) return;

        UIExceptionHandler.hideError(statusLabel);

        Task<List<UserFavorite>> task = new Task<>() {
            @Override
            protected List<UserFavorite> call() throws Exception {
                List<UserFavorite> favorites = favoriteDAO.getFavoritesByUserId(userId);
                if (favorites == null) {
                    throw new DatabaseException(MessageConstant.ERR_DB_QUERY);
                }
                return favorites;
            }
        };

        task.setOnSucceeded(e -> {
            List<UserFavorite> favoriteList = task.getValue();
            listContainer.getChildren().clear();

            if (favoriteList.isEmpty()) {
                showEmptyMessage(MessageConstant.FAVORITE_EMPTY);
            } else {
                try {
                    for (UserFavorite favorite : favoriteList) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/favorite/favorite_item.fxml"));
                        HBox item = loader.load();

                        FavoriteItemController itemController = loader.getController();
                        itemController.setData(favorite, () -> {
                            listContainer.getChildren().remove(item);
                            checkListEmptyAfterDelete();
                        });

                        listContainer.getChildren().add(item);
                    }
                } catch (IOException ex) {
                    UIExceptionHandler.handle(new AppException(MessageConstant.ERR_SYSTEM, ex), statusLabel);
                }
            }
        });

        task.setOnFailed(e -> {
            UIExceptionHandler.handle((Exception) task.getException(), statusLabel);
        });

        new Thread(task).start();
    }

    public void checkListEmptyAfterDelete() {
        if (listContainer.getChildren().isEmpty()) {
            showEmptyMessage(MessageConstant.FAVORITE_EMPTY);
        }
    }

    private void showEmptyMessage(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 20; -fx-font-style: italic;");
        listContainer.getChildren().add(label);
    }

    private void setupNavigation() {
        if (btnFavorite != null) {
            btnFavorite.setStyle(MessageConstant.BUTTON_COLOR);
            btnFavorite.setDisable(true);
        }

        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", MessageConstant.TITLE_HOME));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", MessageConstant.TITLE_HISTORY));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", MessageConstant.TITLE_FAVORITE));
        if (btnAI != null) btnAI.setOnAction(e -> SceneUtils.switchScene(btnAI, "/view/chatbox/chat_box.fxml", MessageConstant.CHAT_AI_TITLE));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", MessageConstant.TITLE_ACCOUNT));
    }
}