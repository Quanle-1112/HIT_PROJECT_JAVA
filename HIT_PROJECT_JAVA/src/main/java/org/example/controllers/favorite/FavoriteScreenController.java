package org.example.controllers.favorite;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.constant.MessageConstant;
import org.example.dao.FavoriteDAO;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.UserFavorite;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.util.List;

public class FavoriteScreenController {

    @FXML private VBox listContainer;

    @FXML private Label statusLabel;

    @FXML private Button btnHome, btnHistory, btnFavorite, btnAccount;

    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private int currentUserId;

    @FXML
    public void initialize() {
        currentUserId = SessionManager.getInstance().getCurrentUserId();

        setupNavigation();
        UIExceptionHandler.hideError(statusLabel);

        if (currentUserId != -1) {
            loadFavoriteData();
        } else {
            showEmptyMessage(MessageConstant.FAVORITE_LOGIN_REQ);
        }
    }

    private void loadFavoriteData() {
        Task<List<UserFavorite>> task = new Task<>() {
            @Override
            protected List<UserFavorite> call() throws Exception {
                return favoriteDAO.getFavoritesByUserId(currentUserId);
            }
        };

        task.setOnSucceeded(e -> {
            List<UserFavorite> favorites = task.getValue();
            listContainer.getChildren().clear();

            if (favorites == null || favorites.isEmpty()) {
                showEmptyMessage(MessageConstant.FAVORITE_EMPTY);
            } else {
                renderFavoriteList(favorites);
            }
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex instanceof Exception) {
                UIExceptionHandler.handle((Exception) ex, statusLabel);
            } else {
                ex.printStackTrace();
            }
        });

        new Thread(task).start();
    }

    private void renderFavoriteList(List<UserFavorite> favorites) {
        try {
            for (UserFavorite fav : favorites) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/favorite/favorite_item.fxml"));
                HBox item = loader.load();

                FavoriteItemController itemController = loader.getController();

                itemController.setData(fav, this::checkListEmptyAfterDelete);

                listContainer.getChildren().add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
            UIExceptionHandler.showError(statusLabel, MessageConstant.ERR_SYSTEM);
        }
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
            btnFavorite.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 10; -fx-text-fill: #19345D; -fx-font-weight: bold;");
            btnFavorite.setDisable(true);
        }

        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", MessageConstant.TITLE_HOME));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", "Lịch sử"));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", "Yêu thích"));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", MessageConstant.TITLE_ACCOUNT));
    }
}