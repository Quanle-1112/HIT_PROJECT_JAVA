package org.example.controllers.read;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import org.example.api.apiAll.ApiChapterResponse;
import org.example.constant.MessageConstant;
import org.example.dao.HistoryDAO;
import org.example.data.ChapterService;
import org.example.exception.AppException;
import org.example.exception.DatabaseException;
import org.example.exception.NetworkException;
import org.example.model.chapter.ChapterInfo;
import org.example.model.user.UserHistory;
import org.example.utils.ImageLoaderGlobal;
import org.example.utils.MouseDragScrollHandler;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

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

    private String bookNameStr;
    private String bookSlug;
    private String currentThumbUrl;
    private List<ChapterInfo> allChapters;
    private int currentChapterIndex;

    private static final String STYLE_ACTIVE = MessageConstant.STYLE_ACTIVE;
    private static final String STYLE_DISABLED = MessageConstant.STYLE_DISABLED;

    private static final String IMAGE_DOMAIN = "https://sv1.otruyencdn.com/";

    @FXML
    public void initialize() {
        if (btnBack != null) btnBack.setOnAction(e -> handleBack());
        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", MessageConstant.TITLE_HOME));
        if (chapterInput != null) chapterInput.setOnAction(e -> handleJumpToChapter());

        if (imageContainer != null) {
            imageContainer.setFillWidth(true);

            if (imageContainer.getScene() != null) setupKeyEvents();
            else imageContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) setupKeyEvents();
            });
        }
    }

    private void setupKeyEvents() {
        imageContainer.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                if (btnPrevChapter != null && !btnPrevChapter.isDisabled()) {
                    currentChapterIndex--;
                    loadChapter(allChapters.get(currentChapterIndex));
                }
            } else if (event.getCode() == KeyCode.RIGHT) {
                if (btnNextChapter != null && !btnNextChapter.isDisabled()) {
                    currentChapterIndex++;
                    loadChapter(allChapters.get(currentChapterIndex));
                }
            }
        });
    }

    public void setInitData(String bookName, String slug, String thumbUrl, List<ChapterInfo> chapters, int startIndex) {
        this.bookNameStr = bookName;
        this.bookSlug = slug;
        this.currentThumbUrl = thumbUrl;
        this.allChapters = chapters;
        this.currentChapterIndex = startIndex;

        if (lblBookName != null) lblBookName.setText(bookName);
        if (totalChaptersLabel != null) totalChaptersLabel.setText("/ " + allChapters.size());

        loadChapter(allChapters.get(currentChapterIndex));
    }

    private void loadChapter(ChapterInfo chapter) {
        if (lblChapterName != null) lblChapterName.setText(MessageConstant.MSG_LOADING + " " + chapter.getChapterName());

        imageContainer.getChildren().clear();
        scrollPane.setVvalue(0.0);

        updatePaginationUI();

        Task<ApiChapterResponse.ChapterItem> task = new Task<>() {
            @Override
            protected ApiChapterResponse.ChapterItem call() {
                return chapterService.getChapterContent(chapter.getChapterApiData());
            }
        };

        task.setOnSucceeded(e -> {
            ApiChapterResponse.ChapterItem item = task.getValue();
            if (lblChapterName != null) lblChapterName.setText("Chương: " + chapter.getChapterName());

            if (item != null && item.getChapterImages() != null) {
                for (ApiChapterResponse.ChapterImage imgData : item.getChapterImages()) {
                    String imgUrl = IMAGE_DOMAIN + item.getChapterPath() + "/" + imgData.getImageFile();

                    ImageView imageView = new ImageView();
                    imageView.setPreserveRatio(true);

                    imageView.fitWidthProperty().bind(scrollPane.widthProperty().subtract(20));

                    ImageLoaderGlobal.setImage(imgUrl, imageView);
                    imageContainer.getChildren().add(imageView);
                }

                new MouseDragScrollHandler(scrollPane, imageContainer);
                saveHistory(chapter);
            } else {
                throw new NetworkException(MessageConstant.ERR_CHAPTER_CONTENT);
            }
        });

        task.setOnFailed(e -> {
            if (lblChapterName != null) lblChapterName.setText(MessageConstant.ERR_LOAD_CHAPTER);
            throw new AppException(MessageConstant.ERR_LOAD_CHAPTER, task.getException());
        });

        new Thread(task).start();
    }

    private void saveHistory(ChapterInfo currentChapter) {
        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        if (currentUserId == -1) return;

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() {
                UserHistory history = new UserHistory();
                history.setUserId(currentUserId);
                history.setBookSlug(bookSlug);
                history.setBookName(bookNameStr);
                history.setThumbnailUrl(currentThumbUrl);
                history.setLastChapterName(currentChapter.getChapterName());
                history.setLastChapterApiData(currentChapter.getChapterApiData());
                historyDAO.saveHistory(history);
                return null;
            }
        };

        saveTask.setOnFailed(e -> {
            throw new DatabaseException(MessageConstant.ERR_DB_SAVE, saveTask.getException());
        });

        new Thread(saveTask).start();
    }

    private void handleJumpToChapter() {
        try {
            int targetChapter = Integer.parseInt(chapterInput.getText());
            if (targetChapter >= 1 && targetChapter <= allChapters.size()) {
                int targetIndex = targetChapter - 1;
                if (targetIndex >= 0 && targetIndex < allChapters.size() && targetIndex != currentChapterIndex) {
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
        if (chapterInput != null) chapterInput.setText(String.valueOf(currentChapterIndex + 1));

        boolean hasPrev = currentChapterIndex > 0;
        boolean hasNext = currentChapterIndex < allChapters.size() - 1;

        if (btnPrevChapter != null) {
            btnPrevChapter.setDisable(!hasPrev);
            btnPrevChapter.setStyle(hasPrev ? STYLE_ACTIVE : STYLE_DISABLED);
            btnPrevChapter.setOnAction(e -> {
                if (hasPrev) {
                    currentChapterIndex--;
                    loadChapter(allChapters.get(currentChapterIndex));
                }
            });
        }

        if (btnNextChapter != null) {
            btnNextChapter.setDisable(!hasNext);
            btnNextChapter.setStyle(hasNext ? STYLE_ACTIVE : STYLE_DISABLED);
            btnNextChapter.setOnAction(e -> {
                if (hasNext) {
                    currentChapterIndex++;
                    loadChapter(allChapters.get(currentChapterIndex));
                }
            });
        }
    }

    private void handleBack() {
        BookDetailController controller = SceneUtils.switchScene(btnBack, "/view/read/book_detail.fxml", "Chi tiết truyện");
        if (controller != null) {
            controller.setBookSlug(bookSlug);
        }
    }
}