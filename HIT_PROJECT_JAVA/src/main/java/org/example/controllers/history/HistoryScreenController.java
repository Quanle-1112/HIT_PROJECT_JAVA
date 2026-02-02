package org.example.controllers.history;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.controllers.history.HistoryItemController;
import org.example.dao.HistoryDAO;
import org.example.model.user.UserHistory;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HistoryScreenController {

    @FXML private VBox listContainer;
    @FXML private Button btnClearAll;
    @FXML private Button btnBack;

    @FXML private Button btnHome;
    @FXML private Button btnHistory;
    @FXML private Button btnFavorite;
    @FXML private Button btnAccount;

    private final HistoryDAO historyDAO = new HistoryDAO();
    private int currentUserId = 1;

    @FXML
    public void initialize() {
        setupBottomNavigation();

        loadHistoryData();

        if (btnClearAll != null) {
            btnClearAll.setOnAction(e -> handleClearAll());
        }

        if (btnBack != null) {
            btnBack.setOnAction(e -> SceneUtils.switchScene(btnBack, "/view/read/home_screen.fxml", "Trang chủ"));
        }
    }

    private void setupBottomNavigation() {
        if (btnHistory != null) {
            btnHistory.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 10; -fx-text-fill: #19345D; -fx-font-weight: bold;");
            btnHistory.setDisable(true);
        }

        if (btnHome != null)
            btnHome.setOnAction(e -> SceneUtils.switchSceneAsync(btnHome, "/view/read/home_screen.fxml", "Trang chủ"));

        if (btnFavorite != null)
            btnFavorite.setOnAction(e -> SceneUtils.switchSceneAsync(btnFavorite, "/view/read/favorite_screen.fxml", "Truyện yêu thích"));

        if (btnAccount != null)
            btnAccount.setOnAction(e -> SceneUtils.switchSceneAsync(btnAccount, "/view/read/account_screen.fxml", "Tài khoản"));
    }

    private void loadHistoryData() {
        listContainer.getChildren().clear();
        Label loadingLabel = new Label("Đang tải lịch sử đọc...");
        loadingLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 10;");
        listContainer.getChildren().add(loadingLabel);

        CompletableFuture.supplyAsync(() -> historyDAO.getHistoryByUserId(currentUserId))
                .thenAccept(historyList -> Platform.runLater(() -> {
                    listContainer.getChildren().clear();

                    if (historyList == null || historyList.isEmpty()) {
                        Label emptyLabel = new Label("Bạn chưa đọc truyện nào gần đây.");
                        emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 20;");
                        listContainer.getChildren().add(emptyLabel);
                    } else {
                        try {
                            for (UserHistory item : historyList) {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/history_item.fxml"));
                                HBox node = loader.load();

                                HistoryItemController controller = loader.getController();
                                controller.setData(item);

                                listContainer.getChildren().add(node);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            listContainer.getChildren().add(new Label("Lỗi hiển thị dữ liệu!"));
                        }
                    }
                }));
    }

    private void handleClearAll() {
        listContainer.getChildren().clear();
        Label clearedLabel = new Label("Đã xóa toàn bộ lịch sử.");
        clearedLabel.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: bold; -fx-padding: 20;");
        listContainer.getChildren().add(clearedLabel);
    }
}