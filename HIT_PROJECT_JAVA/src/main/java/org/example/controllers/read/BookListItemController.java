package org.example.controllers.read;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.api.apiAll.ApiBookItem;
import org.example.api.apiAll.ApiCategory;

import java.io.IOException;
import java.util.stream.Collectors;

public class BookListItemController {

    @FXML private HBox itemContainer;
    @FXML private ImageView bookImageView;
    @FXML private Label bookNameLabel;
    @FXML private Label statusLabel;
    @FXML private Label updatedLabel;
    @FXML private Label chapterLabel;
    @FXML private Label categoryLabel;

    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";

    public void setData(ApiBookItem book) {
        bookNameLabel.setText(book.getName());
        statusLabel.setText("Trạng thái: " + book.getStatus());

        try {
            String dateStr = book.getUpdatedAt().substring(0, 10);
            updatedLabel.setText("Cập nhật lần cuối: " + dateStr);
        } catch (Exception e) {
            updatedLabel.setText("Cập nhật: N/A");
        }

        if (book.getChaptersLatest() != null && !book.getChaptersLatest().isEmpty()) {
            chapterLabel.setText("Chapter mới: " + book.getChaptersLatest().get(0).getChapter_name());
        } else {
            chapterLabel.setText("Chapter mới: Đang cập nhật");
        }

        if (book.getCategory() != null) {
            String categories = book.getCategory().stream()
                    .map(ApiCategory::getName)
                    .collect(Collectors.joining(", "));
            categoryLabel.setText(categories);
        } else {
            categoryLabel.setText("Thể loại: N/A");
        }

        String imgUrl = IMAGE_BASE_URL + book.getThumbUrl();
        try {
            Image image = new Image(imgUrl, true);
            bookImageView.setImage(image);

            Rectangle clip = new Rectangle(100, 140);
            clip.setArcWidth(10);
            clip.setArcHeight(10);
            bookImageView.setClip(clip);
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemContainer.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_detail.fxml"));
                Parent root = loader.load();

                BookDetailController detailController = loader.getController();
                detailController.setBookSlug(book.getSlug());

                Stage stage = (Stage) itemContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("WOWTruyen - " + book.getName());

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Lỗi: Không tìm thấy file view/read/book_detail.fxml");
            }
        });

        itemContainer.setOnMouseEntered(e -> {
            itemContainer.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 10; -fx-cursor: hand;");
        });

        itemContainer.setOnMouseExited(e -> {
            itemContainer.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-cursor: hand;");
        });
    }
}