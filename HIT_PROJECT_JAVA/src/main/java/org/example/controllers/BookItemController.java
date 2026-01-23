package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.example.api.apiAll.ApiBookItem;

public class BookItemController {

    @FXML private VBox cardContainer;
    @FXML private ImageView bookImageView;
    @FXML private Label bookNameLabel;
    @FXML private Label chapterLabel;

    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";

    public void setData(ApiBookItem book) {
        bookNameLabel.setText(book.getName());

        if (book.getChaptersLatest() != null && !book.getChaptersLatest().isEmpty()) {
            chapterLabel.setText(book.getChaptersLatest().get(0).getChapter_name());
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
            System.out.println("Click vào truyện: " + book.getName());
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