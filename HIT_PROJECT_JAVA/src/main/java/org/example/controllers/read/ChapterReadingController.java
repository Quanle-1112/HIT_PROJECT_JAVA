package org.example.controllers.read;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.example.api.apiAll.ApiChapterResponse;
import org.example.dao.HistoryDAO;
import org.example.data.ChapterService;
import org.example.model.chapter.ChapterInfo;
import org.example.model.user.UserHistory;
import org.example.utils.ImageLoaderGlobal;
import org.example.utils.MouseDragScrollHandler;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChapterReadingController {

    @FXML private Button btnBack;
    @FXML private Label lblChapterName;
    @FXML private Label lblBookName;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox imageContainer;

    @FXML private Button btnPrevChapter;
    @FXML private Button btnNextChapter;
    @FXML private TextField chapterInput;
    @FXML private Label totalChaptersLabel;


    private final ChapterService chapterService = new ChapterService();
    private final HistoryDAO historyDAO = new HistoryDAO();

    private List<ChapterInfo> allChapters;
    private int currentChapterIndex;
    private String currentBookName;
    private String currentBookSlug;
    private String currentThumbnailUrl;
    private int currentUserId;

    private final String STYLE_ACTIVE = "-fx-background-color: #19345D; -fx-cursor: hand; -fx-background-radius: 5; -fx-text-fill: WHITE; -fx-font-weight: bold;";
    private final String STYLE_DISABLED = "-fx-background-color: #BDC3C7; -fx-cursor: default; -fx-background-radius: 5; -fx-text-fill: #7F8C8D;";

    @FXML
    public void initialize() {
        btnBack.setOnAction(event -> handleBack());

        chapterInput.setOnAction(event -> handleChapterJump());
    }

    public void setInitData(String bookName, String bookSlug, String thumbnailUrl, List<ChapterInfo> chapters, int currentIndex, int userId) {
        this.currentBookName = bookName;
        this.currentBookSlug = bookSlug;
        this.currentThumbnailUrl = thumbnailUrl;
        this.allChapters = chapters;
        this.currentChapterIndex = currentIndex;
        this.currentUserId = userId;

        lblBookName.setText(bookName);
        if (totalChaptersLabel != null) {
            totalChaptersLabel.setText("/ " + allChapters.size());
        }

        loadChapter(allChapters.get(currentIndex));
    }

    private void loadChapter(ChapterInfo chapter) {
        Platform.runLater(() -> {
            lblChapterName.setText("Chap " + chapter.getChapterName());
            imageContainer.getChildren().clear();
            imageContainer.getChildren().add(new Label("Đang tải nội dung chương..."));
            scrollPane.setVvalue(0.0);
            updatePaginationUI();
        });

        saveReadingHistory(chapter);

        CompletableFuture.supplyAsync(() -> chapterService.getChapterContent(chapter.getChapterApiData()))
                .thenAccept(chapterData -> Platform.runLater(() -> {
                    imageContainer.getChildren().clear();

                    if (chapterData == null || chapterData.getChapterImages().isEmpty()) {
                        imageContainer.getChildren().add(new Label("Lỗi tải chương hoặc chương này chưa có ảnh."));
                        return;
                    }

                    for (ApiChapterResponse.ChapterImage img : chapterData.getChapterImages()) {
                        String imgUrl = "https://sv1.otruyencdn.com/" + chapterData.getChapterPath() + "/" + img.getImageFile();

                        ImageView imageView = new ImageView();
                        imageView.setFitWidth(430);
                        imageView.setPreserveRatio(true);

                        ImageLoaderGlobal.setImage(imgUrl, imageView);

                        imageContainer.getChildren().add(imageView);
                    }

                    new MouseDragScrollHandler(scrollPane, imageContainer);
                }));
    }

    private void saveReadingHistory(ChapterInfo chapter) {
        if (currentUserId <= 0) return;

        CompletableFuture.runAsync(() -> {
            UserHistory history = new UserHistory();
            history.setUserId(currentUserId);
            history.setBookSlug(currentBookSlug);
            history.setBookName(currentBookName);
            history.setThumbnailUrl(currentThumbnailUrl);
            history.setLastChapterName(chapter.getChapterName());
            history.setLastChapterApiData(chapter.getChapterApiData());

            historyDAO.saveHistory(history);
            System.out.println("Đã lưu lịch sử: " + currentBookName + " - Chap " + chapter.getChapterName());
        });
    }

    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_detail.fxml"));
            Parent root = loader.load();

            BookDetailController controller = loader.getController();
            controller.setBookSlug(currentBookSlug);

            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePaginationUI() {
        chapterInput.setText(String.valueOf(currentChapterIndex + 1));

        boolean hasPrev = currentChapterIndex > 0;
        boolean hasNext = currentChapterIndex < allChapters.size() - 1;

        btnPrevChapter.setDisable(!hasPrev);
        btnPrevChapter.setStyle(hasPrev ? STYLE_ACTIVE : STYLE_DISABLED);

        btnNextChapter.setDisable(!hasNext);
        btnNextChapter.setStyle(hasNext ? STYLE_ACTIVE : STYLE_DISABLED);

        btnPrevChapter.setOnAction(null);
        btnNextChapter.setOnAction(null);

        btnPrevChapter.setOnAction(e -> {
            if (hasPrev) {
                currentChapterIndex--;
                loadChapter(allChapters.get(currentChapterIndex));
            }
        });

        btnNextChapter.setOnAction(e -> {
            if (hasNext) {
                currentChapterIndex++;
                loadChapter(allChapters.get(currentChapterIndex));
            }
        });
    }

    private void handleChapterJump() {
        try {
            int targetPage = Integer.parseInt(chapterInput.getText());
            int targetIndex = targetPage - 1;
            if (targetIndex >= 0 && targetIndex < allChapters.size()) {
                if (targetIndex != currentChapterIndex) {
                    currentChapterIndex = targetIndex;
                    loadChapter(allChapters.get(currentChapterIndex));
                    imageContainer.requestFocus();
                }
            } else {
                chapterInput.setText(String.valueOf(currentChapterIndex + 1));
            }
        } catch (NumberFormatException e) {
            chapterInput.setText(String.valueOf(currentChapterIndex + 1));
        }
    }
}