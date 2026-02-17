package org.example.controllers.read;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.example.api.apiAll.ApiCategory;
import org.example.api.apiAll.ApiOneBookResponse;
import org.example.constant.MessageConstant;
import org.example.dao.FavoriteDAO;
import org.example.dao.HistoryDAO;
import org.example.data.BookService;
import org.example.exception.AppException;
import org.example.exception.AuthException;
import org.example.exception.DatabaseException;
import org.example.exception.NetworkException;
import org.example.model.chapter.AllChapter;
import org.example.model.chapter.ChapterInfo;
import org.example.model.user.UserFavorite;
import org.example.model.user.UserHistory;
import org.example.utils.ImageLoaderGlobal;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

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
    @FXML private VBox chapterListContainer;

    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private TextField pageInput;
    @FXML private Label pageLabel;

    @FXML private Button readButton;
    @FXML private Button readNewestButton;
    @FXML private Button favoriteButton;

    private final BookService bookService = new BookService();
    private final HistoryDAO historyDAO = new HistoryDAO();
    private final FavoriteDAO favoriteDAO = new FavoriteDAO();

    private String bookSlug;
    private String currentThumbUrl;
    private List<ChapterInfo> allChapters = new ArrayList<>();

    private int currentPage = 1;
    private final int itemsPerPage = 50;
    private int totalPages = 1;

    private ChapterInfo historyChapter = null;
    private int historyChapterIndex = -1;

    @FXML
    public void initialize() {
        if (descriptionArea != null) {
            descriptionArea.setWrapText(true);
            descriptionArea.setEditable(false);
        }
        if (backButton != null) backButton.setOnAction(e -> SceneUtils.switchScene(backButton, "/view/read/home_screen.fxml", MessageConstant.TITLE_HOME));
        if (favoriteButton != null) favoriteButton.setOnAction(e -> handleFavorite());
        if (readButton != null) readButton.setOnAction(e -> handleReadButton());
        if (readNewestButton != null) readNewestButton.setOnAction(e -> handleReadNewest());

        setupPagination();
    }

    public void setBookSlug(String slug) {
        this.bookSlug = slug;
        loadBookDetail();
    }

    private void loadBookDetail() {
        Task<ApiOneBookResponse.ApiOneBookData> task = new Task<>() {
            @Override
            protected ApiOneBookResponse.ApiOneBookData call() {
                return bookService.getBookDetail(bookSlug);
            }
        };

        task.setOnSucceeded(e -> {
            ApiOneBookResponse.ApiOneBookData data = task.getValue();
            if (data != null && data.getItem() != null) {
                updateUI(data);
                checkHistoryStatus();
                checkFavoriteStatus();
            } else {
                throw new NetworkException(MessageConstant.ERR_API_EMPTY);
            }
        });

        task.setOnFailed(e -> {
            throw new NetworkException(MessageConstant.ERR_BOOK_NOT_FOUND, task.getException());
        });
        new Thread(task).start();
    }

    private void updateUI(ApiOneBookResponse.ApiOneBookData data) {
        var item = data.getItem();
        if (bookName != null) bookName.setText(item.getName());
        if (headerTitle != null) headerTitle.setText(item.getName());
        if (bookStatus != null) bookStatus.setText("Trạng thái: " + item.getStatus());

        if (bookAuthor != null) {
            bookAuthor.setText((item.getOriginName() != null && !item.getOriginName().isEmpty())
                    ? MessageConstant.OTHER_NAME + item.getOriginName().get(0) : MessageConstant.OTHER_AUTHOR);
        }

        if (bookCategory != null) {
            String cats = (item.getCategory() != null) ? item.getCategory().stream().map(ApiCategory::getName).collect(Collectors.joining(", ")) : "N/A";
            bookCategory.setText("Thể loại: " + cats);
        }

        if (descriptionArea != null) {
            descriptionArea.setText(item.getContent().replaceAll("<[^>]*>", ""));
        }

        if (bookImageView != null) {
            currentThumbUrl = "https://img.otruyenapi.com/uploads/comics/" + item.getThumbUrl();
            ImageLoaderGlobal.setImage(currentThumbUrl, bookImageView);
            Rectangle clip = new Rectangle(bookImageView.getFitWidth(), bookImageView.getFitHeight());
            clip.setArcWidth(20); clip.setArcHeight(20);
            bookImageView.setClip(clip);
        }

        allChapters.clear();
        if (item.getChapters() != null) {
            for (AllChapter server : item.getChapters()) {
                if (server.getServerData() != null) {
                    allChapters.addAll(server.getServerData());
                }
            }
        }

        allChapters.sort((c1, c2) -> {
            try {
                double n1 = Double.parseDouble(c1.getChapterName());
                double n2 = Double.parseDouble(c2.getChapterName());
                return Double.compare(n1, n2);
            } catch (NumberFormatException ex) {
                return 0;
            }
        });

        calculatePagination();
        renderChapterList();
    }

    private void checkHistoryStatus() {
        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        if (currentUserId == -1) {
            if (readButton != null) readButton.setText(MessageConstant.READ_FIRST);
            historyChapter = null;
            return;
        }

        Task<UserHistory> historyTask = new Task<>() {
            @Override
            protected UserHistory call() {
                return historyDAO.getHistory(currentUserId, bookSlug);
            }
        };

        historyTask.setOnSucceeded(e -> {
            UserHistory history = historyTask.getValue();
            if (history != null && readButton != null) {
                readButton.setText(MessageConstant.READ_MORE + history.getLastChapterName());
                for (int i = 0; i < allChapters.size(); i++) {
                    if (allChapters.get(i).getChapterApiData().equals(history.getLastChapterApiData())) {
                        historyChapter = allChapters.get(i);
                        historyChapterIndex = i;
                        break;
                    }
                }
            } else {
                if (readButton != null) readButton.setText(MessageConstant.READ_FIRST);
                historyChapter = null;
            }
        });

        historyTask.setOnFailed(e -> {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, historyTask.getException());
        });

        new Thread(historyTask).start();
    }

    private void checkFavoriteStatus() {
        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        if (currentUserId == -1 || favoriteButton == null) return;

        Task<Boolean> favoriteTask = new Task<>() {
            @Override
            protected Boolean call() { return favoriteDAO.isFavorite(currentUserId, bookSlug); }
        };

        favoriteTask.setOnSucceeded(e -> {
            boolean isFav = favoriteTask.getValue();
            if (isFav) {
                favoriteButton.setText("❤");
                favoriteButton.setStyle("-fx-background-color: #FADBD8; -fx-text-fill: #E74C3C; -fx-border-color: #E74C3C; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;");
            } else {
                favoriteButton.setText("♡");
                favoriteButton.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #E74C3C; -fx-border-color: #E0E0E0; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;");
            }
        });

        favoriteTask.setOnFailed(e -> {
            throw new DatabaseException(MessageConstant.ERR_DB_QUERY, favoriteTask.getException());
        });

        new Thread(favoriteTask).start();
    }

    private void handleReadButton() {
        if (allChapters.isEmpty()) {
            throw new AppException(MessageConstant.ERR_NO_CHAPTER);
        }

        if (historyChapter != null && historyChapterIndex != -1) {
            openChapterReading(historyChapter, historyChapterIndex);
        } else {
            openChapterReading(allChapters.get(0), 0);
        }
    }

    private void handleReadNewest() {
        if (allChapters.isEmpty()) {
            throw new AppException(MessageConstant.ERR_NO_CHAPTER);
        }
        int lastIndex = allChapters.size() - 1;
        openChapterReading(allChapters.get(lastIndex), lastIndex);
    }

    private void handleFavorite() {
        int currentUserId = SessionManager.getInstance().getCurrentUserId();
        if (currentUserId == -1) {
            throw new AuthException(MessageConstant.FAVORITE_LOGIN_REQ);
        }

        boolean isCurrentlyFav = favoriteButton.getText().contains("❤") || favoriteButton.getText().contains("Đã thích");
        String currentBookName = (bookName != null) ? bookName.getText() : "Unknown";

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                if (isCurrentlyFav) return favoriteDAO.removeFavorite(currentUserId, bookSlug);
                else {
                    UserFavorite fav = new UserFavorite();
                    fav.setUserId(currentUserId);
                    fav.setBookSlug(bookSlug);
                    fav.setBookName(currentBookName);
                    fav.setThumbnailUrl(currentThumbUrl);
                    return favoriteDAO.addFavorite(fav);
                }
            }
        };

        task.setOnSucceeded(e -> {
            if (task.getValue()) checkFavoriteStatus();
            else throw new DatabaseException(MessageConstant.ERR_DB_SAVE);
        });

        task.setOnFailed(e -> {
            throw new DatabaseException(MessageConstant.ERR_SYSTEM, task.getException());
        });

        new Thread(task).start();
    }

    private void openChapterReading(ChapterInfo chap, int index) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/chapter_reading.fxml"));
            Parent root = loader.load();
            ChapterReadingController controller = loader.getController();
            controller.setInitData(bookName.getText(), bookSlug, currentThumbUrl, allChapters, index);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            throw new AppException(MessageConstant.ERR_SYSTEM, e);
        }
    }

    private void calculatePagination() {
        totalPages = allChapters.isEmpty() ? 1 : (int) Math.ceil((double) allChapters.size() / itemsPerPage);
        currentPage = 1;
        if (pageLabel != null) pageLabel.setText("/ " + totalPages);
    }

    private void renderChapterList() {
        if (chapterListContainer == null) return;
        chapterListContainer.getChildren().clear();

        if (allChapters.isEmpty()) {
            chapterListContainer.getChildren().add(new Label(MessageConstant.MSG_EMPTY_DATA));
            return;
        }

        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allChapters.size());

        for (int i = start; i < end; i++) {
            ChapterInfo chap = allChapters.get(i);
            int finalIndex = i;

            Button btn = new Button(chap.getChapterName());
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
            btn.setPadding(new javafx.geometry.Insets(8, 10, 8, 10));
            btn.setOnAction(e -> openChapterReading(chap, finalIndex));
            chapterListContainer.getChildren().add(btn);
        }
        updatePaginationButtons();
    }

    private void updatePaginationButtons() {
        if (pageInput != null) pageInput.setText(String.valueOf(currentPage));
        if (btnPrev != null) btnPrev.setDisable(currentPage == 1);
        if (btnNext != null) btnNext.setDisable(currentPage == totalPages);
    }

    private void setupPagination() {
        if (btnPrev != null) btnPrev.setOnAction(e -> { if (currentPage > 1) { currentPage--; renderChapterList(); }});
        if (btnNext != null) btnNext.setOnAction(e -> { if (currentPage < totalPages) { currentPage++; renderChapterList(); }});
        if (pageInput != null) pageInput.setOnAction(e -> {
            try {
                int val = Integer.parseInt(pageInput.getText());
                if (val >= 1 && val <= totalPages) { currentPage = val; renderChapterList(); }
            } catch (Exception ex) { pageInput.setText(String.valueOf(currentPage)); }
        });
    }
}