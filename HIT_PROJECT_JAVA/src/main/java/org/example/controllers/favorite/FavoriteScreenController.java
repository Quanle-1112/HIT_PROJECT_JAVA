package org.example.controllers.favorite;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.controllers.favorite.FavoriteItemController;
import org.example.dao.FavoriteDAO;
import org.example.model.user.UserFavorite;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FavoriteScreenController {

    @FXML private VBox listContainer;

    @FXML private Button btnHome;
    @FXML private Button btnHistory;
    @FXML private Button btnFavorite;
    @FXML private Button btnAccount;

    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private int currentUserId = 1;

    @FXML
    public void initialize() {
        setupBottomNavigation();
        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", "Home"));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", "History"));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", "Favorite"));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", "Account"));
        loadFavoriteData();
    }

    private void setupBottomNavigation() {
        if (btnFavorite != null) {
            btnFavorite.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 10; -fx-text-fill: #19345D; -fx-font-weight: bold;");
            btnFavorite.setDisable(true);
        }
    }

    private void loadFavoriteData() {
        listContainer.getChildren().clear();
        Label loadingLabel = new Label("Đang tải danh sách yêu thích...");
        loadingLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 20;");
        listContainer.getChildren().add(loadingLabel);

        CompletableFuture.supplyAsync(() -> favoriteDAO.getFavoritesByUserId(currentUserId))
                .thenAccept(favList -> Platform.runLater(() -> {
                    listContainer.getChildren().clear();

                    if (favList == null || favList.isEmpty()) {
                        Label emptyLabel = new Label("Bạn chưa yêu thích bộ truyện nào.");
                        emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 20;");
                        listContainer.getChildren().add(emptyLabel);
                    } else {
                        try {
                            for (UserFavorite fav : favList) {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/favorite/favorite_item.fxml"));
                                HBox node = loader.load();

                                FavoriteItemController controller = loader.getController();
                                controller.setData(fav);

                                listContainer.getChildren().add(node);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            listContainer.getChildren().add(new Label("Lỗi hiển thị dữ liệu!"));
                        }
                    }
                }));
    }
}