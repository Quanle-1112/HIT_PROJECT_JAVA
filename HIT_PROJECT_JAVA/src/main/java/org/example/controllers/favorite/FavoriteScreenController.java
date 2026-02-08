package org.example.controllers.favorite;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.constant.MessageConstant;
import org.example.dao.FavoriteDAO;
import org.example.model.user.UserFavorite;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class FavoriteScreenController {

    @FXML private VBox listContainer;

    @FXML private Button btnHome;
    @FXML private Button btnAI;
    @FXML private Button btnHistory;
    @FXML private Button btnFavorite;
    @FXML private Button btnAccount;

    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private int currentUserId;

    @FXML
    public void initialize() {
        currentUserId = SessionManager.getInstance().getCurrentUserId();

        setupBottomNavigation();

        if (currentUserId != -1) {
            loadFavoriteData();
        } else {
            showEmptyMessage(MessageConstant.FAVORITE_LOGIN_REQ);
        }
    }

    private void loadFavoriteData() {
        listContainer.getChildren().clear();
        Label loadingLabel = new Label("Đang tải dữ liệu...");
        loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #19345d; -fx-padding: 20;");
        listContainer.getChildren().add(loadingLabel);

        CompletableFuture.supplyAsync(() -> favoriteDAO.getFavoritesByUserId(currentUserId))
                .thenAccept(favList -> Platform.runLater(() -> {
                    listContainer.getChildren().clear();

                    if (favList == null || favList.isEmpty()) {
                        showEmptyMessage(MessageConstant.FAVORITE_EMPTY);
                    } else {
                        try {
                            for (UserFavorite fav : favList) {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/favorite/favorite_item.fxml"));
                                HBox node = loader.load();

                                FavoriteItemController controller = loader.getController();
                                controller.setData(fav, this::checkListEmptyAfterDelete);

                                listContainer.getChildren().add(node);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            showEmptyMessage("Lỗi hiển thị dữ liệu (Kiểm tra đường dẫn FXML)!");
                        }
                    }
                }));
    }

    private void checkListEmptyAfterDelete() {
        if (listContainer.getChildren().isEmpty()) {
            showEmptyMessage(MessageConstant.FAVORITE_EMPTY);
        }
    }

    private void showEmptyMessage(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 20; -fx-font-style: italic;");
        listContainer.getChildren().add(label);
    }

    private void setupBottomNavigation() {

        if (btnFavorite != null) {
            btnFavorite.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 10; -fx-text-fill: #19345D; -fx-font-weight: bold;");
            btnFavorite.setDisable(true);
        }
        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", "Trang chủ"));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", "Lịch sử"));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", "Yêu thích"));
        if (btnAI != null) btnAI.setOnAction(e -> System.out.println("Tính năng AI đang phát triển"));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", "Tài khoản"));
    }
}