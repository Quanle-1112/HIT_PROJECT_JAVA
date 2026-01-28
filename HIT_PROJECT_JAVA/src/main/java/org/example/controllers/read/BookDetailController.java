package org.example.controllers.read;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.api.apiAll.ApiBookItem;
import org.example.api.apiAll.ApiCategory;
import org.example.api.apiAll.ApiOneBookResponse;
import org.example.data.BookService;
import org.example.model.chapter.AllChapter;
import org.example.model.chapter.ChapterInfo;
import org.example.utils.SceneUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BookDetailController {

    @FXML private Button backButton;
    @FXML private Label headerTitle;
    @FXML private ImageView bookImageView;
    @FXML private Label bookName;
    @FXML private Label bookAuthor;
    @FXML private Label bookStatus;
    @FXML private Label bookCategory;
    @FXML private Button readNowButton;
    @FXML private Button favoriteButton;
    @FXML private TextArea bookDescription;
    @FXML private VBox chapterListContainer;

    private final BookService bookService = new BookService();
    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";
    private ApiBookItem currentBook;

    @FXML
    public void initialize() {
        backButton.setOnAction(event ->
                SceneUtils.switchScene(backButton, "/view/home_screen.fxml", "Trang chủ")
        );

        readNowButton.setOnAction(event -> {
            if (currentBook != null && currentBook.getChapters() != null && !currentBook.getChapters().isEmpty()) {
                List<ChapterInfo> chapters = currentBook.getChapters().get(0).getServerData();
                if (chapters != null && !chapters.isEmpty()) {
                    System.out.println("Đọc ngay chapter: " + chapters.get(0).getChapterApiData());
                }
            }
        });

        favoriteButton.setOnAction(event -> System.out.println("Đã bấm yêu thích!"));
    }

    public void setBookSlug(String slug) {
        Task<ApiOneBookResponse.ApiOneBookData> task = new Task<>() {
            @Override
            protected ApiOneBookResponse.ApiOneBookData call() {
                return bookService.getBookDetail(slug);
            }
        };

        task.setOnSucceeded(event -> {
            ApiOneBookResponse.ApiOneBookData data = task.getValue();
            if (data != null && data.getItem() != null) {
                this.currentBook = data.getItem();
                updateUI(this.currentBook);
            }
        });

        task.setOnFailed(e -> System.err.println("Lỗi tải chi tiết truyện"));
        new Thread(task).start();
    }

    private void updateUI(ApiBookItem book) {
        Platform.runLater(() -> {
            headerTitle.setText(book.getName());
            bookName.setText(book.getName());
            bookStatus.setText("Trạng thái: " + book.getStatus());

            if (book.getOriginName() != null && !book.getOriginName().isEmpty()) {
                bookAuthor.setText("Tác giả: " + String.join(", ", book.getOriginName()));
            } else {
                bookAuthor.setText("Tác giả: Đang cập nhật");
            }

            if (book.getCategory() != null) {
                String categories = book.getCategory().stream()
                        .map(ApiCategory::getName)
                        .collect(Collectors.joining(", "));
                bookCategory.setText("Thể loại: " + categories);
            }

            try {
                String imgUrl = IMAGE_BASE_URL + book.getThumbUrl();
                bookImageView.setImage(new Image(imgUrl, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadChapterList(book.getChapters());
        });
    }

    private void loadChapterList(List<AllChapter> allChapters) {
        chapterListContainer.getChildren().clear();
        if (allChapters == null || allChapters.isEmpty()) {
            chapterListContainer.getChildren().add(new Label("Chưa có chương nào."));
            return;
        }

        for (AllChapter server : allChapters) {
            List<ChapterInfo> chapters = server.getServerData();
            if (chapters != null) {
                for (ChapterInfo chap : chapters) {
                    Button chapBtn = new Button("Chapter " + chap.getChapterName());
                    chapBtn.setMaxWidth(Double.MAX_VALUE);
                    chapBtn.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand; -fx-alignment: BASELINE_LEFT;");
                    chapBtn.setPadding(new Insets(10));
                    chapBtn.setFont(Font.font("System", FontWeight.NORMAL, 14));

                    chapBtn.setOnAction(e -> {
                        System.out.println("Click chapter api: " + chap.getChapterApiData());
                    });

                    chapterListContainer.getChildren().add(chapBtn);
                }
            }
        }
    }
}