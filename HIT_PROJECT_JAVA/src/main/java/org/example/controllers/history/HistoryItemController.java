package org.example.controllers.history;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.model.user.UserHistory;
import org.example.utils.ImageLoaderGlobal;
import org.example.utils.SceneUtils;

public class HistoryItemController {
    @FXML private HBox itemContainer;
    @FXML private ImageView bookThumb;
    @FXML private Label bookTitle;
    @FXML private Label lastChapter;
    @FXML private Label readTime;
    @FXML private Button btnDelete;

    public void setData(UserHistory history) {
        bookTitle.setText(history.getBookName());
        lastChapter.setText("Đang đọc: " + history.getLastChapterName());
        readTime.setText(history.getReadAt() != null ? "Đọc lúc: " + history.getReadAt().toString() : "");

        String fullImgUrl = "https://img.otruyenapi.com/uploads/comics/" + history.getThumbnailUrl();
        ImageLoaderGlobal.setImage(fullImgUrl, bookThumb);

        itemContainer.setOnMouseClicked(e -> {
            System.out.println("Tiếp tục đọc: " + history.getBookSlug());
        });

        btnDelete.setOnAction(e -> {
            ((VBox) itemContainer.getParent()).getChildren().remove(itemContainer);
        });
    }
}