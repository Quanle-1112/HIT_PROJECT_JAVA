package org.example.controllers.history;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controllers.read.BookDetailController;
import org.example.dao.HistoryDAO;
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

        String timeStr = "";
        if (history.getReadAt() != null) {
            String rawDate = history.getReadAt().toString();
            timeStr = rawDate.length() > 16 ? rawDate.substring(0, 16) : rawDate;
        }
        readTime.setText(timeStr);

        String rawThumb = history.getThumbnailUrl();
        String finalUrl;

        if (rawThumb == null || rawThumb.isEmpty()) {
            finalUrl = "";
        } else if (rawThumb.startsWith("http")) {
            finalUrl = rawThumb;
        } else {
            finalUrl = IMAGE_BASE_URL + rawThumb;
        }

        ImageLoaderGlobal.setImage(finalUrl, bookThumb);

        itemContainer.setOnMouseClicked(e -> openBookDetail());

        if (btnDelete != null) {
            btnDelete.setOnAction(e -> {
                e.consume();

                new Thread(() -> {
                    boolean success = historyDAO.deleteHistory(history.getUserId(), history.getBookSlug());
                    Platform.runLater(() -> {
                        if (success) {
                            VBox parent = (VBox) itemContainer.getParent();
                            if (parent != null) {
                                parent.getChildren().remove(itemContainer);

                                if (onDeleteCallback != null) onDeleteCallback.run();
                            }
                        }
                    });
                }).start();
            });
        }
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}