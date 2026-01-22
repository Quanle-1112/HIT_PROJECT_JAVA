package org.example.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.api.apiAll.ApiBookItem;
import org.example.data.BookService;

import java.util.List;

public class HomeScreenController {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane bookContainer;

    private final BookService bookService = new BookService();

    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";

    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        loadBooks();
    }

    private void loadBooks() {
        bookContainer.getChildren().clear();
        Label loadingLabel = new Label("Đang tải dữ liệu truyện...");
        bookContainer.getChildren().add(loadingLabel);

        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() throws Exception {
                return bookService.getNewBooks(1);
            }
        };

        task.setOnSucceeded(event -> {
            bookContainer.getChildren().clear();
            List<ApiBookItem> books = task.getValue();
            if (books != null && !books.isEmpty()) {
                for (ApiBookItem book : books) {
                    VBox bookCard = createBookCard(book);
                    bookContainer.getChildren().add(bookCard);
                }
            } else {
                bookContainer.getChildren().add(new Label("Không tải được dữ liệu!"));
            }
        });

        task.setOnFailed(event -> {
            bookContainer.getChildren().clear();
            bookContainer.getChildren().add(new Label("Lỗi kết nối API!"));
        });

        new Thread(task).start();
    }

    private VBox createBookCard(ApiBookItem book) {
        VBox card = new VBox();
        card.setPrefWidth(130);
        card.setSpacing(5);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setCursor(Cursor.HAND);

        String imgUrl = IMAGE_BASE_URL + book.getThumbUrl();
        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(true);

        try {
            Image image = new Image(imgUrl, true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Lỗi load ảnh: " + book.getName());
        }

        Label nameLabel = new Label(book.getName());
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(124);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        nameLabel.setTextFill(Color.web("#333333"));
        nameLabel.setMinHeight(35);

        Label chapterLabel = new Label();
        if (book.getChaptersLatest() != null && !book.getChaptersLatest().isEmpty()) {
            chapterLabel.setText("Mới: " + book.getChaptersLatest().get(0).getChapter_name());
        } else {
            chapterLabel.setText("Đang cập nhật");
        }
        chapterLabel.setFont(Font.font("Segoe UI", 10));
        chapterLabel.setTextFill(Color.web("#777777"));

        card.setOnMouseClicked(event -> {
            System.out.println("Bạn đã chọn truyện: " + book.getName() + " | Slug: " + book.getSlug());
        });

        card.getChildren().addAll(imageView, nameLabel, chapterLabel);
        return card;
    }
}