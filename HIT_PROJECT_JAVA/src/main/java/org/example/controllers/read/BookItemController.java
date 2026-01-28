package org.example.controllers.read;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.api.apiAll.ApiBookItem;

import java.io.IOException;

public class BookItemController {

    @FXML private VBox cardContainer;
    @FXML private ImageView bookImageView;
    @FXML private Label bookNameLabel;
    @FXML private Label chapterLabel;

    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";

    public void setData(ApiBookItem book) {
        bookNameLabel.setText(book.getName());

        if (book.getChaptersLatest() != null && !book.getChaptersLatest().isEmpty()) {
            chapterLabel.setText("Chap " + book.getChaptersLatest().get(0).getChapter_name());
        } else {
            chapterLabel.setText("Đang cập nhật");
        }

        String imgUrl = IMAGE_BASE_URL + book.getThumbUrl();
        try {
            Image image = new Image(imgUrl, true);
            bookImageView.setImage(image);

            Rectangle clip = new Rectangle(140, 190);
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            bookImageView.setClip(clip);

        } catch (Exception e) {
            e.printStackTrace();
        }

        cardContainer.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_detail.fxml"));
                Parent root = loader.load();

                BookDetailController detailController = loader.getController();
                detailController.setBookSlug(book.getSlug());

                Stage stage = (Stage) cardContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("WOWTruyen - " + book.getName());

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Không thể mở màn hình chi tiết truyện!");
            }
        });

        cardContainer.setOnMouseEntered(e -> {
            cardContainer.setScaleX(1.05);
            cardContainer.setScaleY(1.05);
        });

        cardContainer.setOnMouseExited(e -> {
            cardContainer.setScaleX(1.0);
            cardContainer.setScaleY(1.0);
        });
    }
}