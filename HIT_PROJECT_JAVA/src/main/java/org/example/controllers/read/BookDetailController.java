package org.example.controllers.read;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.example.api.apiAll.ApiOneBookResponse;
import org.example.dao.FavoriteDAO;
import org.example.dao.HistoryDAO;
import org.example.data.BookService;
import org.example.model.chapter.AllChapter;
import org.example.model.chapter.ChapterInfo;
import org.example.model.user.UserFavorite;
import org.example.model.user.UserHistory;
import org.example.utils.ImageLoaderGlobal;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private final HistoryDAO historyDAO = new HistoryDAO();

    private String bookSlug;
    private String currentThumbUrl;
    private String currentBookNameStr;

    private final int userId = 1;

    private List<ChapterInfo> allChapters = new ArrayList<>();
    private int currentPage = 1;
    private int totalPages = 1;
    private final int ITEMS_PER_PAGE = 25;
    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";

    private boolean isFavorite = false;
    private ChapterInfo lastReadChapter = null;

    @FXML
    public void initialize() {
        backButton.setOnAction(event ->
                SceneUtils.switchScene(backButton, "/view/read/home_screen.fxml", "Trang chủ")
        );

        btnPrev.setOnAction(e -> changePage(-1));
        btnNext.setOnAction(e -> changePage(1));
        pageInput.setOnAction(e -> handlePageInput());

        Rectangle clip = new Rectangle(150, 200);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        bookImageView.setClip(clip);

        if (readButton != null) readButton.setOnAction(e -> handleReadButton());
        if (readNewestButton != null) readNewestButton.setOnAction(e -> handleReadNewestButton());
        if (favoriteButton != null) favoriteButton.setOnAction(e -> handleFavoriteButton());
    }

    public void setBookSlug(String slug) {
        this.bookSlug = slug;
        loadData();
    }

    private void loadData() {
        if (bookSlug == null || bookSlug.isEmpty()) return;

        Task<ApiOneBookResponse.ApiOneBookData> task = new Task<>() {
            @Override
            protected ApiOneBookResponse.ApiOneBookData call() {
                return bookService.getBookDetail(bookSlug);
            }
        };

        task.setOnSucceeded(event -> {
            ApiOneBookResponse.ApiOneBookData data = task.getValue();
            if (data != null && data.getItem() != null) {
                updateUI(data);
                checkUserStatus();
            } else {
                headerTitle.setText("Không tải được dữ liệu");
            }
        });

        new Thread(task).start();
    }

    private void checkUserStatus() {
        new Thread(() -> {
            boolean fav = favoriteDAO.isFavorite(userId, bookSlug);
            Platform.runLater(() -> {
                this.isFavorite = fav;
                updateFavoriteButtonUI();
            });
        }).start();

        new Thread(() -> {
            List<UserHistory> historyList = historyDAO.getHistoryByUserId(userId);
            Optional<UserHistory> myHistory = historyList.stream()
                    .filter(h -> h.getBookSlug().equals(bookSlug))
                    .findFirst();

            Platform.runLater(() -> {
                if (myHistory.isPresent()) {
                    String lastApiData = myHistory.get().getLastChapterApiData();

                    for (ChapterInfo chap : allChapters) {
                        if (chap.getChapterApiData().equals(lastApiData)) {
                            this.lastReadChapter = chap;
                            break;
                        }
                    }
                    if (this.lastReadChapter != null) {
                        readButton.setText("Đọc tiếp: Chap " + lastReadChapter.getChapterName());
                    }
                }
            });
        }).start();
    }

    private void updateUI(ApiOneBookResponse.ApiOneBookData data) {
        var item = data.getItem();
        this.currentBookNameStr = item.getName();

        headerTitle.setText(item.getName());
        bookName.setText(item.getName());
        bookStatus.setText("Trạng thái: " + item.getStatus());
        bookAuthor.setText("Tác giả: Đang cập nhật");

        if (item.getCategory() != null) {
            String cats = item.getCategory().stream()
                    .map(cat -> cat.getName())
                    .collect(Collectors.joining(", "));
            bookCategory.setText("Thể loại: " + cats);
        }

        descriptionArea.setText(item.getContent().replaceAll("<[^>]*>", ""));
        this.currentThumbUrl = IMAGE_BASE_URL + item.getThumbUrl();
        ImageLoaderGlobal.setImage(currentThumbUrl, bookImageView);

        allChapters.clear();
        if (item.getChapters() != null) {
            for (AllChapter server : item.getChapters()) {
                if (server.getServerData() != null) {
                    allChapters.addAll(server.getServerData());
                }
            }
        }

        totalPages = (int) Math.ceil((double) allChapters.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        currentPage = 1;
        renderChapterList();
    }


    private void handleReadButton() {
        if (allChapters.isEmpty()) return;

        if (lastReadChapter != null) {
            openChapterReading(lastReadChapter);
        } else {

            ChapterInfo firstChapter = findFirstChapter();
            if (firstChapter != null) {
                openChapterReading(firstChapter);
            } else {
                openChapterReading(allChapters.get(allChapters.size() - 1));
            }
        }
    }

    private void handleReadNewestButton() {
        if (allChapters.isEmpty()) return;

        ChapterInfo newestChapter = findNewestChapter();
        if (newestChapter != null) {
            openChapterReading(newestChapter);
        } else {
            openChapterReading(allChapters.get(0));
        }
    }

    private void handleFavoriteButton() {
        if (isFavorite) {
            boolean success = favoriteDAO.removeFavorite(userId, bookSlug);
            if (success) {
                isFavorite = false;
                updateFavoriteButtonUI();
            }
        } else {
            UserFavorite fav = new UserFavorite();
            fav.setUserId(userId);
            fav.setBookSlug(bookSlug);
            fav.setBookName(currentBookNameStr);
            fav.setThumbnailUrl(currentThumbUrl);

            boolean success = favoriteDAO.addFavorite(fav);
            if (success) {
                isFavorite = true;
                updateFavoriteButtonUI();
            }
        }
    }

    private void updateFavoriteButtonUI() {
        if (isFavorite) {
            favoriteButton.setText("❤");
            favoriteButton.setStyle("-fx-background-color: #FADBD8; -fx-text-fill: #E74C3C; -fx-border-color: #E74C3C; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;");
        } else {
            favoriteButton.setText("♡");
            favoriteButton.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #E74C3C; -fx-border-color: #E0E0E0; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;");
        }
    }

    private ChapterInfo findFirstChapter() {
        try {
            return allChapters.stream()
                    .min(Comparator.comparingDouble(c -> parseChapterNumber(c.getChapterName())))
                    .orElse(allChapters.get(allChapters.size() - 1));
        } catch (Exception e) {
            return allChapters.get(allChapters.size() - 1);
        }
    }

    private ChapterInfo findNewestChapter() {
        try {
            return allChapters.stream()
                    .max(Comparator.comparingDouble(c -> parseChapterNumber(c.getChapterName())))
                    .orElse(allChapters.get(0));
        } catch (Exception e) {
            return allChapters.get(0);
        }
    }

    private double parseChapterNumber(String name) {
        try {
            String numStr = name.replaceAll("[^0-9.]", "");
            return Double.parseDouble(numStr);
        } catch (Exception e) {
            return 0;
        }
    }

    private void renderChapterList() {
        chapterListContainer.getChildren().clear();
        updatePaginationControls();

        if (allChapters.isEmpty()) {
            chapterListContainer.getChildren().add(new Label("Chưa có chương nào."));
            return;
        }

        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allChapters.size());
        List<ChapterInfo> pageItems = allChapters.subList(start, end);

        try {
            for (ChapterInfo chap : pageItems) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/chapter_button.fxml"));
                Button btnChapter = loader.load();

                btnChapter.setText(chap.getChapterName());
                btnChapter.setOnAction(e -> openChapterReading(chap));

                chapterListContainer.getChildren().add(btnChapter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updatePaginationControls() {
        if (btnPrev != null) btnPrev.setDisable(currentPage <= 1);
        if (btnNext != null) btnNext.setDisable(currentPage >= totalPages);
        if (pageInput != null && !pageInput.isFocused()) pageInput.setText(String.valueOf(currentPage));
        if (pageLabel != null) pageLabel.setText("/ " + totalPages);
    }

    private void changePage(int delta) {
        int newPage = currentPage + delta;
        if (newPage >= 1 && newPage <= totalPages) {
            currentPage = newPage;
            renderChapterList();
        }
    }

    private void handlePageInput() {
        try {
            int newPage = Integer.parseInt(pageInput.getText());
            if (newPage >= 1 && newPage <= totalPages) {
                currentPage = newPage;
                renderChapterList();
            } else {
                pageInput.setText(String.valueOf(currentPage));
            }
        } catch (NumberFormatException e) {
            pageInput.setText(String.valueOf(currentPage));
        }
    }

    private void openChapterReading(ChapterInfo chap) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/chapter_reading.fxml"));
            Parent root = loader.load();

            ChapterReadingController controller = loader.getController();
            int index = allChapters.indexOf(chap);

            controller.setInitData(bookName.getText(), bookSlug, currentThumbUrl, allChapters, index, userId);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}