package org.example.controllers.history;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.dao.HistoryDAO;
import org.example.model.user.UserHistory;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class HistoryScreenController {

    @FXML private VBox listContainer;
    @FXML private Button btnClearAll;
    @FXML private Button btnHome, btnHistory, btnFavorite, btnAccount;

    private final HistoryDAO historyDAO = new HistoryDAO();
    private int currentUserId = -1;

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadHistoryData();
    }

    @FXML
    public void initialize() {
        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", "Home"));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", "History"));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", "Favorite"));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", "Account"));
        if (btnClearAll != null) btnClearAll.setOnAction(e -> handleClearAll());
        if (btnHistory != null) {
            btnHistory.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 10; -fx-text-fill: #19345D; -fx-font-weight: bold;");
            btnHistory.setDisable(true);
        }
    }

    private void loadHistoryData() {
        if (currentUserId == -1) {
            listContainer.getChildren().add(new Label("Vui lòng đăng nhập để xem lịch sử"));
            return;
        }

        listContainer.getChildren().clear();
        listContainer.getChildren().add(new Label("Đang tải..."));

        CompletableFuture.supplyAsync(() -> historyDAO.getHistoryByUserId(currentUserId))
                .thenAccept(historyList -> Platform.runLater(() -> {
                    listContainer.getChildren().clear();

                    if (historyList == null || historyList.isEmpty()) {
                        listContainer.getChildren().add(new Label("Chưa có lịch sử đọc truyện."));
                    } else {
                        try {
                            for (UserHistory item : historyList) {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/history_item.fxml"));
                                HBox node = loader.load();

                                HistoryItemController controller = loader.getController();
                                controller.setData(item); // Item controller tự xử lý nút xóa

                                listContainer.getChildren().add(node);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
    }

    private void handleClearAll() {
        if (currentUserId == -1) return;
        CompletableFuture.runAsync(() -> {
            boolean success = historyDAO.deleteAllByUserId(currentUserId);
            Platform.runLater(() -> {
                if (success) {
                    listContainer.getChildren().clear();
                    listContainer.getChildren().add(new Label("Đã xóa toàn bộ lịch sử."));
                }
            });
        });
    }
}