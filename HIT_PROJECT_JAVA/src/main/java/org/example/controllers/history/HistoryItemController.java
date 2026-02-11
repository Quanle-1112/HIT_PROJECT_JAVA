package org.example.controllers.history;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.constant.MessageConstant;
import org.example.controllers.read.BookDetailController;
import org.example.dao.HistoryDAO;
import org.example.exception.AppException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.UserHistory;
import org.example.utils.ImageLoaderGlobal;

import java.io.IOException;

public class HistoryItemController {
    @FXML private HBox itemContainer;
    @FXML private ImageView bookThumb;
    @FXML private Label bookTitle;
    @FXML private Label lastChapter;
    @FXML private Label readTime;
    @FXML private Button btnDelete;

    private final HistoryDAO historyDAO = new HistoryDAO();
    private UserHistory currentHistory;

    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";

    public void setData(UserHistory history, Runnable onDeleteCallback) {
        this.currentHistory = history;

        bookTitle.setText(history.getBookName());
        lastChapter.setText("Đang đọc: " + history.getLastChapterName());

        if (history.getReadAt() != null) {
            readTime.setText(history.getReadAt().toString().substring(0, 16));
        } else {
            readTime.setText("");
        }

        String imgUrl = history.getThumbnailUrl();
        if (imgUrl != null && !imgUrl.startsWith("http")) {
            imgUrl = IMAGE_BASE_URL + imgUrl;
        }
        ImageLoaderGlobal.setImage(imgUrl, bookThumb);

        itemContainer.setOnMouseClicked(e -> openBookDetail());

        if (btnDelete != null) {
            btnDelete.setOnAction(e -> {
                e.consume();
                handleDelete(onDeleteCallback);
            });
        }
    }

    private void handleDelete(Runnable onDeleteCallback) {
        new Thread(() -> {
            try {
                boolean success = historyDAO.deleteHistory(currentHistory.getUserId(), currentHistory.getBookSlug());

                Platform.runLater(() -> {
                    if (success) {
                        VBox parent = (VBox) itemContainer.getParent();
                        if (parent != null) {
                            parent.getChildren().remove(itemContainer);

                            if (onDeleteCallback != null) onDeleteCallback.run();
                        }
                    } else {
                        UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", MessageConstant.ERR_DB_DELETE);
                    }
                });
            } catch (AppException e) {
                Platform.runLater(() ->
                        UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống", e.getMessage())
                );
            } catch (Exception e) {
                Platform.runLater(() ->
                        UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", MessageConstant.ERR_SYSTEM)
                );
                throw new AppException(MessageConstant.ERR_SYSTEM, e);
            }
        }).start();
    }

    private void openBookDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_detail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookSlug(currentHistory.getBookSlug());

            Stage stage = (Stage) itemContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - " + currentHistory.getBookName());
        } catch (IOException e) {
            UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", MessageConstant.ERR_SYSTEM);
            throw new AppException(MessageConstant.ERR_SYSTEM, e);
        }
    }
}