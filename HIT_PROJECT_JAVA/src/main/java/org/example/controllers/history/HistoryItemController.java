package org.example.controllers.history;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.dao.HistoryDAO;
import org.example.model.user.UserHistory;
import org.example.utils.ImageLoaderGlobal;

public class HistoryItemController {
    @FXML private HBox itemContainer;
    @FXML private ImageView bookThumb;
    @FXML private Label bookTitle;
    @FXML private Label lastChapter;
    @FXML private Label readTime;
    @FXML private Button btnDelete;

    private final HistoryDAO historyDAO = new HistoryDAO();

    public void setData(UserHistory history) {
        bookTitle.setText(history.getBookName());
        lastChapter.setText("Đang đọc: " + history.getLastChapterName());
        readTime.setText(history.getReadAt() != null ? "Đọc lúc: " + history.getReadAt().toString() : "");

        String fullImgUrl = "https://img.otruyenapi.com/uploads/comics/" + history.getThumbnailUrl();
        ImageLoaderGlobal.setImage(fullImgUrl, bookThumb);

        btnDelete.setOnAction(e -> {
            new Thread(() -> {
                boolean success = historyDAO.deleteHistory(history.getUserId(), history.getBookSlug());
                javafx.application.Platform.runLater(() -> {
                    if (success) {
                        VBox parent = (VBox) itemContainer.getParent();
                        if (parent != null) {
                            parent.getChildren().remove(itemContainer);
                        }
                    }
                });
            }).start();
        });
    }
}