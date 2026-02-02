package org.example.controllers.read;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.api.apiAll.ApiChapterResponse;
import org.example.dao.HistoryDAO;
import org.example.data.ChapterService;
import org.example.model.chapter.ChapterInfo;
import org.example.model.user.UserHistory;
import org.example.utils.ImageLoaderGlobal;
import org.example.utils.MouseDragScrollHandler;
import org.example.utils.SceneUtils;

import java.util.List;

public class ChapterReadingController {

    @FXML private Button btnBack;
    @FXML private Label lblChapterName;
    @FXML private Label lblBookName;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox imageContainer;

    @FXML private Button btnPrevChapter;
    @FXML private Button btnNextChapter;
    @FXML private Button btnHome;
    @FXML private TextField chapterInput;
    @FXML private Label totalChaptersLabel;

    private final ChapterService chapterService = new ChapterService();
    private final HistoryDAO historyDAO = new HistoryDAO();

    private List<ChapterInfo> allChapters;
    private int currentChapterIndex;
    private int totalChapters;

    private String bookTitle;
    private String bookSlug;
    private String bookThumb;
    private int userId = 0;

    private final String IMAGE_DOMAIN = "https://sv1.otruyencdn.com/";

    private final String STYLE_ACTIVE = "-fx-background-color: #19345D; -fx-text-fill: WHITE; -fx-cursor: hand; -fx-background-radius: 5; -fx-font-weight: bold;";
    private final String STYLE_DISABLED = "-fx-background-color: #E0E0E0; -fx-text-fill: #AAAAAA; -fx-background-radius: 5;";

    @FXML
    public void initialize() {
        new MouseDragScrollHandler(scrollPane, imageContainer);

        btnBack.setOnAction(e -> {
            BookDetailController controller = SceneUtils.switchScene(btnBack, "/view/read/book_detail.fxml", "Chi tiết truyện");
            if (controller != null) {
                controller.setBookSlug(bookSlug);
            }
        });

        if (btnHome != null) {
            btnHome.setOnAction(e ->
                    SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", "Trang chủ")
            );
        }

        chapterInput.setOnAction(e -> handleManualChapterInput());
        chapterInput.setOnMouseClicked(e -> chapterInput.selectAll());
    }

    public void setInitData(String bookName, String slug, String thumb, List<ChapterInfo> chapters, int startIndex, int userId) {
        this.bookTitle = bookName;
        this.bookSlug = slug;
        this.bookThumb = thumb;
        this.allChapters = chapters;
        this.currentChapterIndex = startIndex;
        this.userId = userId;

        if (chapters != null) {
            this.totalChapters = chapters.size();
            totalChaptersLabel.setText("/ " + totalChapters);
        }

        lblBookName.setText(bookName);
        loadChapter(allChapters.get(currentChapterIndex));
    }

    private void loadChapter(ChapterInfo chapter) {
        lblChapterName.setText("Đang tải...");
        imageContainer.getChildren().clear();
        scrollPane.setVvalue(0);

        updatePaginationUI();

        btnPrevChapter.setDisable(true);
        btnNextChapter.setDisable(true);
        chapterInput.setDisable(true);

        if (userId > 0) {
            UserHistory history = new UserHistory();
            history.setUserId(userId);
            history.setBookSlug(bookSlug);
            history.setBookName(bookTitle);
            history.setThumbnailUrl(bookThumb);
            history.setLastChapterName(chapter.getChapterName());
            history.setLastChapterApiData(chapter.getChapterApiData());
            new Thread(() -> historyDAO.saveHistory(history)).start();
        }

        Task<ApiChapterResponse.ChapterItem> task = new Task<>() {
            @Override
            protected ApiChapterResponse.ChapterItem call() {
                return chapterService.getChapterContent(chapter.getChapterApiData());
            }
        };

        task.setOnSucceeded(e -> {
            ApiChapterResponse.ChapterItem item = task.getValue();
            chapterInput.setDisable(false);

            if (item != null) {
                lblChapterName.setText("Chapter " + chapter.getChapterName());
                renderImages(item);
                updatePaginationUI();
            } else {
                lblChapterName.setText("Lỗi chương");
                Label errorLbl = new Label("Không tải được dữ liệu ảnh.");
                errorLbl.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");
                imageContainer.getChildren().add(errorLbl);
                updatePaginationUI();
            }
        });

        task.setOnFailed(e -> {
            chapterInput.setDisable(false);
            updatePaginationUI();
        });

        new Thread(task).start();
    }

    private void renderImages(ApiChapterResponse.ChapterItem item) {
        String path = item.getChapterPath();
        List<ApiChapterResponse.ChapterImage> images = item.getChapterImages();

        for (ApiChapterResponse.ChapterImage img : images) {
            String fullUrl = IMAGE_DOMAIN + path + "/" + img.getImageFile();
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(scrollPane.widthProperty());
            ImageLoaderGlobal.setImage(fullUrl, imageView);
            imageContainer.getChildren().add(imageView);
        }
    }

    private void handleManualChapterInput() {
        try {
            String input = chapterInput.getText().trim();
            int targetChapNum = Integer.parseInt(input);

            if (targetChapNum >= 1 && targetChapNum <= totalChapters) {
                int targetIndex = targetChapNum - 1;

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
}