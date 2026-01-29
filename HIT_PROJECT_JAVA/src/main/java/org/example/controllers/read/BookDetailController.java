package org.example.controllers.read;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.api.apiAll.ApiBookItem;
import org.example.api.apiAll.ApiCategory;
import org.example.api.apiAll.ApiOneBookResponse;
import org.example.data.BookService;
import org.example.model.chapter.AllChapter;
import org.example.model.chapter.ChapterInfo;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.ArrayList;
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
    @FXML private HBox paginationContainer;

    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Label pageLabel;

    private final BookService bookService = new BookService();
    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";
    private ApiBookItem currentBook;

    private List<ChapterInfo> fullChapterList = new ArrayList<>();
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 25;
    private int totalPages = 0;

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(event ->
                    SceneUtils.switchScene(backButton, "/view/read/home_screen.fxml", "Trang chủ")
            );
        }

        if (readNowButton != null) {
            readNowButton.setOnAction(event -> {
                if (!fullChapterList.isEmpty()) {
                    openChapterReading(fullChapterList.get(0), readNowButton);
                }
            });
        }

        if (btnPrev != null) btnPrev.setOnAction(e -> showPage(currentPage - 1));
        if (btnNext != null) btnNext.setOnAction(e -> showPage(currentPage + 1));

        paginationContainer.setVisible(false);
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

        new Thread(task).start();
    }

    private void updateUI(ApiBookItem book) {
        Platform.runLater(() -> {
            if (headerTitle != null) headerTitle.setText(book.getName());
            if (bookName != null) bookName.setText(book.getName());
            if (bookStatus != null) bookStatus.setText("Trạng thái: " + book.getStatus());

            if (bookAuthor != null) {
                bookAuthor.setText("Tác giả: " + (book.getOriginName().isEmpty() ? "Đang cập nhật" : String.join(", ", book.getOriginName())));
            }

            if (bookCategory != null && book.getCategory() != null) {
                String categories = book.getCategory().stream()
                        .map(ApiCategory::getName)
                        .collect(Collectors.joining(", "));
                bookCategory.setText("Thể loại: " + categories);
            }

            if (bookImageView != null) {
                try {
                    bookImageView.setImage(new Image(IMAGE_BASE_URL + book.getThumbUrl(), true));
                } catch (Exception ignored) {}
            }

            if (bookDescription != null) bookDescription.setText("Mô tả truyện đang được cập nhật...");

            processChapters(book.getChapters());
        });
    }

    private void processChapters(List<AllChapter> apiChapters) {
        fullChapterList.clear();
        if (apiChapters != null) {
            for (AllChapter server : apiChapters) {
                if (server.getServerData() != null) {
                    fullChapterList.addAll(server.getServerData());
                }
            }
        }

        if (fullChapterList.isEmpty()) {
            chapterListContainer.getChildren().clear();
            chapterListContainer.getChildren().add(new Label("Chưa có chương nào."));
            paginationContainer.setVisible(false);
        } else {
            totalPages = (int) Math.ceil((double) fullChapterList.size() / ITEMS_PER_PAGE);
            currentPage = 1;
            paginationContainer.setVisible(true);
            showPage(1);
        }
    }

    private void showPage(int page) {
        this.currentPage = page;
        chapterListContainer.getChildren().clear();

        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, fullChapterList.size());

        for (int i = startIndex; i < endIndex; i++) {
            ChapterInfo chap = fullChapterList.get(i);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/chapter_button.fxml"));
                Button chapBtn = loader.load();
                chapBtn.setText("Chapter " + chap.getChapterName());
                chapBtn.setOnAction(e -> openChapterReading(chap, chapBtn));

                chapterListContainer.getChildren().add(chapBtn);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        updatePaginationControls();
    }

    private void updatePaginationControls() {
        if (btnPrev != null) btnPrev.setDisable(currentPage == 1);
        if (btnNext != null) btnNext.setDisable(currentPage == totalPages);
        if (pageLabel != null) pageLabel.setText("Trang " + currentPage + " / " + totalPages);
    }

    private void openChapterReading(ChapterInfo chap, Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/chapter_read.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - " + currentBook.getName() + " - Chap " + chap.getChapterName());
        } catch (IOException e) {
            System.err.println("Chưa có file chapter_read.fxml");
        }
    }
}