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
import org.example.utils.SessionManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HistoryScreenController {

    @FXML private VBox listContainer;
    @FXML private Button btnClearAll;

    @FXML private Button btnHome, btnHistory, btnFavorite,btnAI, btnAccount;

    private final HistoryDAO historyDAO = new HistoryDAO();
    private int currentUserId;

    @FXML
    public void initialize() {
        currentUserId = SessionManager.getInstance().getCurrentUserId();

        setupNavigation();

        if (btnClearAll != null) {
            btnClearAll.setOnAction(e -> handleClearAll());
        }

        if (currentUserId != -1) {
            loadHistoryData();
        } else {
            showEmptyMessage("Vui lòng đăng nhập để xem lịch sử.");
            if (btnClearAll != null) btnClearAll.setDisable(true);
        }
    }

    private void loadHistoryData() {
        listContainer.getChildren().clear();
        Label loadingLabel = new Label("Đang tải dữ liệu...");
        loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #19345d; -fx-padding: 20;");
        listContainer.getChildren().add(loadingLabel);

        CompletableFuture.supplyAsync(() -> historyDAO.getHistoryByUserId(currentUserId))
                .thenAccept(historyList -> Platform.runLater(() -> {
                    listContainer.getChildren().clear();

                    if (historyList == null || historyList.isEmpty()) {
                        showEmptyMessage("Chưa có lịch sử đọc truyện.");
                        if (btnClearAll != null) btnClearAll.setDisable(true);
                    } else {
                        if (btnClearAll != null) btnClearAll.setDisable(false);
                        try {
                            for (UserHistory item : historyList) {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/history/history_item.fxml"));
                                HBox node = loader.load();

                                HistoryItemController controller = loader.getController();

                                controller.setData(item, this::checkListEmptyAfterDelete);

                                listContainer.getChildren().add(node);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            showEmptyMessage("Lỗi hiển thị dữ liệu!");
                        }
                    }
                }));
    }

    private void handleClearAll() {
        if (currentUserId == -1) return;

        btnClearAll.setDisable(true);

        CompletableFuture.runAsync(() -> {
            boolean success = historyDAO.deleteAllByUserId(currentUserId);
            Platform.runLater(() -> {
                if (success) {
                    listContainer.getChildren().clear();
                    showEmptyMessage("Đã xóa toàn bộ lịch sử.");
                } else {
                    btnClearAll.setDisable(false);
                }
            });
        });
    }

    public void checkListEmptyAfterDelete() {
        if (listContainer.getChildren().isEmpty()) {
            showEmptyMessage("Chưa có lịch sử đọc truyện.");
            if (btnClearAll != null) btnClearAll.setDisable(true);
        }
    }

    private void showEmptyMessage(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #888; -fx-padding: 20; -fx-font-style: italic;");
        listContainer.getChildren().add(label);
    }

    private void setupNavigation() {

        if (btnHistory != null) {
            btnHistory.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 10; -fx-text-fill: #19345D; -fx-font-weight: bold;");
            btnHistory.setDisable(true);
        }
        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", "Trang chủ"));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", "Lịch sử"));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", "Yêu thích"));
        if (btnAI != null) btnAI.setOnAction(e -> System.out.println("Tính năng AI đang phát triển"));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", "Tài khoản"));
    }
}