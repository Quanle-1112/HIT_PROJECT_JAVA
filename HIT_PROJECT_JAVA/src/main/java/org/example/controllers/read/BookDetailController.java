package org.example.controllers.read;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
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
    @FXML private TextArea descriptionArea;

    @FXML private Button readFirstButton;
    @FXML private Button readNewestButton;
    @FXML private Button favoriteButton;

    @FXML private VBox chapterListContainer;

    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private TextField pageInput;
    @FXML private Label pageLabel;

    private final BookService bookService = new BookService();
    private ApiOneBookResponse.ApiOneBookData currentBookData;

    private List<ChapterInfo> fullChapterList = new ArrayList<>();

    private final int ITEMS_PER_PAGE = 25;
    private int currentPage = 1;
    private int totalPages = 1;

    private Stage loadingStage;
    private String bookSlug;

    public void setBookSlug(String slug) {
        this.bookSlug = slug;
        loadBookData();
    }

    @FXML
    public void initialize() {
        backButton.setOnAction(event ->
                SceneUtils.switchScene(backButton, "/view/read/home_screen.fxml", "Trang chủ")
        );

        if (btnPrev != null) {
            btnPrev.setOnAction(e -> {
                if (currentPage > 1) {
                    currentPage--;
                    renderChapterList();
                }
            });
        }

        if (btnNext != null) {
            btnNext.setOnAction(e -> {
                if (currentPage < totalPages) {
                    currentPage++;
                    renderChapterList();
                }
            });
        }

        if (pageInput != null) {
            pageInput.setOnAction(event -> handlePageInput());

            pageInput.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    pageInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            pageInput.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    handlePageInput();
                }
            });
        }
    }

    private void loadBookData() {
        Platform.runLater(() -> {
            Stage owner = (Stage) backButton.getScene().getWindow();
            loadingStage = SceneUtils.showLoading(owner);
        });

        Task<ApiOneBookResponse.ApiOneBookData> task = new Task<>() {
            @Override
            protected ApiOneBookResponse.ApiOneBookData call() {
                return bookService.getBookDetail(bookSlug);
            }
        };

        task.setOnSucceeded(event -> {
            currentBookData = task.getValue();
            if (currentBookData != null && currentBookData.getItem() != null) {
                updateUI();
            } else {
                System.err.println("Dữ liệu sách null hoặc lỗi API.");
            }
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));
        });

        task.setOnFailed(event -> {
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));
            System.err.println("Lỗi kết nối hoặc tải chi tiết truyện thất bại.");
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void updateUI() {
        ApiBookItem item = currentBookData.getItem();

        bookName.setText(item.getName());
        bookStatus.setText("Trạng thái: " + item.getStatus());

        if (item.getCategory() != null) {
            String cats = item.getCategory().stream()
                    .map(ApiCategory::getName)
                    .collect(Collectors.joining(", "));
            bookCategory.setText("Thể loại: " + cats);
        }

        if (item.getOriginName() != null && !item.getOriginName().isEmpty()) {
            bookAuthor.setText("Tên khác: " + item.getOriginName().get(0));
        }

        String content = item.getContent();
        if (content != null) {
            content = content.replaceAll("<br\\s*/?>", "\n").replaceAll("<[^>]*>", "");
        }
        descriptionArea.setText(content != null ? content : "Đang cập nhật...");

        String imgUrl = "https://img.otruyenapi.com/uploads/comics/" + item.getThumbUrl();
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

        processChapters();
    }

    private void processChapters() {
        fullChapterList.clear();
        if (currentBookData.getItem().getChapters() != null) {
            for (AllChapter server : currentBookData.getItem().getChapters()) {
                if (server.getServerData() != null) {
                    fullChapterList.addAll(server.getServerData());
                }
            }
        }

        if (!fullChapterList.isEmpty()) {
            totalPages = (int) Math.ceil((double) fullChapterList.size() / ITEMS_PER_PAGE);
            currentPage = 1;
            renderChapterList();
        } else {
            chapterListContainer.getChildren().clear();
            chapterListContainer.getChildren().add(new Label("Chưa có chương nào."));
            updatePaginationControls();
        }
    }

    private void renderChapterList() {
        chapterListContainer.getChildren().clear();

        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, fullChapterList.size());

        for (int i = start; i < end; i++) {
            ChapterInfo chap = fullChapterList.get(i);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/chapter_button.fxml"));
                Button chapBtn = loader.load();

                chapBtn.setText("Chapter " + chap.getChapterName());
                chapBtn.setOnAction(e -> openChapterReading(chap));

                chapterListContainer.getChildren().add(chapBtn);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updatePaginationControls();
    }

    private void updatePaginationControls() {
        if (btnPrev != null) btnPrev.setDisable(currentPage <= 1);
        if (btnNext != null) btnNext.setDisable(currentPage >= totalPages);

        if (pageInput != null) {
            if (!pageInput.isFocused()) {
                pageInput.setText(String.valueOf(currentPage));
            }
        }

        if (pageLabel != null) {
            pageLabel.setText("/ " + totalPages);
        }
    }

    private void handlePageInput() {
        if (pageInput == null) return;

        String input = pageInput.getText();

        if (input == null || input.trim().isEmpty()) {
            pageInput.setText(String.valueOf(currentPage));
            return;
        }

        try {
            int newPage = Integer.parseInt(input);

            if (newPage < 1) newPage = 1;
            if (newPage > totalPages) newPage = totalPages;

            if (newPage != currentPage) {
                currentPage = newPage;
                renderChapterList();
                pageInput.setText(String.valueOf(currentPage));
                chapterListContainer.requestFocus();
            } else {
                pageInput.setText(String.valueOf(currentPage));
            }
        } catch (NumberFormatException e) {
            pageInput.setText(String.valueOf(currentPage));
        }
    }

    private void openChapterReading(ChapterInfo chap) {
        System.out.println("Mở Chapter: " + chap.getChapterName());
        // TODO: Viết code chuyển sang màn hình đọc truyện tại đây
    }
}