package org.example.controllers.history;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.constant.MessageConstant;
import org.example.dao.HistoryDAO;
import org.example.exception.AppException;
import org.example.exception.DatabaseException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.UserHistory;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

import java.io.IOException;
import java.util.List;

public class HistoryScreenController {

    @FXML private VBox listContainer;
    @FXML private Button btnClearAll;
    @FXML private Label statusLabel;
    @FXML private Button btnHome, btnHistory, btnFavorite, btnAI, btnAccount;

    private final HistoryDAO historyDAO = new HistoryDAO();

    @FXML
    public void initialize() {
        setupNavigation();
        UIExceptionHandler.hideError(statusLabel);

        if (btnClearAll != null) {
            btnClearAll.setOnAction(e -> handleClearAll());
        }

        int userId = SessionManager.getInstance().getCurrentUserId();

        if (userId != -1) {
            loadHistoryData();
        } else {
            showEmptyMessage(MessageConstant.HISTORY_LOGIN_REQ);
            if (btnClearAll != null) btnClearAll.setDisable(true);
        }
    }

    private void loadHistoryData() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) return;

        UIExceptionHandler.hideError(statusLabel);

        Task<List<UserHistory>> task = new Task<>() {
            @Override
            protected List<UserHistory> call() throws Exception {
                List<UserHistory> data = historyDAO.getHistoryByUserId(userId);
                if (data == null) {
                    throw new DatabaseException(MessageConstant.ERR_DB_QUERY);
                }
                return data;
            }
        };

        task.setOnSucceeded(e -> {
            List<UserHistory> historyList = task.getValue();
            listContainer.getChildren().clear();

            if (historyList.isEmpty()) {
                showEmptyMessage(MessageConstant.HISTORY_EMPTY);
                if (btnClearAll != null) btnClearAll.setDisable(true);
            } else {
                if (btnClearAll != null) btnClearAll.setDisable(false);
                try {
                    for (UserHistory history : historyList) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/history/history_item.fxml"));
                        HBox item = loader.load();

                        HistoryItemController itemController = loader.getController();
                        itemController.setData(history, () -> {
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

    private void handleClearAll() {
        int userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == -1) return;

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                boolean success = historyDAO.deleteAllByUserId(userId);
                if (!success) {
                    throw new DatabaseException(MessageConstant.ERR_DB_DELETE);
                }
                return true;
            }
        };

        task.setOnSucceeded(e -> {
            listContainer.getChildren().clear();
            showEmptyMessage(MessageConstant.HISTORY_EMPTY);
            if (btnClearAll != null) btnClearAll.setDisable(true);

            if (statusLabel != null) {
                statusLabel.setText(MessageConstant.DELETE_HISTORY_LIST);
                statusLabel.setStyle(MessageConstant.COLOR_DELETE_HISTORY_LIST);
                statusLabel.setVisible(true);
            }
        });

        task.setOnFailed(e -> {
            UIExceptionHandler.handle((Exception) task.getException(), statusLabel);
        });

        new Thread(task).start();
    }

    public void checkListEmptyAfterDelete() {
        if (listContainer.getChildren().isEmpty()) {
            showEmptyMessage(MessageConstant.HISTORY_EMPTY);
            if (btnClearAll != null) btnClearAll.setDisable(true);
        }
    }

    private void showEmptyMessage(String message) {
        Label label = new Label(message);
        label.setStyle(MessageConstant.COLOR_9);
        listContainer.getChildren().add(label);
    }

    private void setupNavigation() {
        if (btnHistory != null) {
            btnHistory.setStyle(MessageConstant.BUTTON_COLOR);
            btnHistory.setDisable(true);
        }

        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", MessageConstant.TITLE_HOME));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", MessageConstant.TITLE_HISTORY));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", MessageConstant.TITLE_FAVORITE));
        if (btnAI != null) btnAI.setOnAction(e -> SceneUtils.switchScene(btnAI, "/view/chatbox/chat_box.fxml", MessageConstant.CHAT_AI_TITLE));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", MessageConstant.TITLE_ACCOUNT));
    }
}