package org.example.controllers.admin;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.example.api.apiAll.ApiBookItem;
import org.example.constant.MessageConstant;
import org.example.dao.HiddenBookDAO;
import org.example.data.BookService;
import org.example.utils.ImageLoaderGlobal;
import org.example.utils.SceneUtils;

import java.util.List;
import java.util.Set;

public class ManageBooksController {

    @FXML private Button btnBack;
    @FXML private TextField txtSearch;
    @FXML private Button btnSearchBook;
    @FXML private FlowPane bookListContainer;
    @FXML private Label lblStatus;

    @FXML private Button btnPrevPage;
    @FXML private Button btnNextPage;
    @FXML private Button btnHiddenList;

    private final BookService bookService = new BookService();
    private final HiddenBookDAO hiddenBookDAO = new HiddenBookDAO();

    private int currentPage = 1;
    private String currentKeyword = "";
    private boolean isViewingHidden = false;

    @FXML
    public void initialize() {
        btnBack.setOnAction(e -> SceneUtils.switchScene(btnBack, "/view/admin/admin_dashboard.fxml", "Admin Dashboard"));

        btnSearchBook.setOnAction(e -> {
            currentKeyword = txtSearch.getText().trim();
            currentPage = 1;
            isViewingHidden = false;
            if (!currentKeyword.isEmpty()) {
                searchAndDisplayBooks();
            } else {
                loadDefaultBooks();
            }
        });

        btnPrevPage.setOnAction(e -> {
            if (currentPage > 1 && !isViewingHidden) {
                currentPage--;
                reloadCurrentState();
            }
        });

        btnNextPage.setOnAction(e -> {
            if (!isViewingHidden) {
                currentPage++;
                reloadCurrentState();
            }
        });

        btnHiddenList.setOnAction(e -> {
            isViewingHidden = true;
            txtSearch.clear();
            currentKeyword = "";
            loadHiddenBooksOnly();
        });

        loadDefaultBooks();
    }

    private void reloadCurrentState() {
        if (currentKeyword.isEmpty()) {
            loadDefaultBooks();
        } else {
            searchAndDisplayBooks();
        }
    }

    private void loadDefaultBooks() {
        updateStatusLabel(MessageConstant.LOADING_PAGE_COMIC + currentPage + ")...");
        bookListContainer.getChildren().clear();

        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() throws Exception {
                return bookService.getNewBooks(currentPage);
            }
        };

        task.setOnSucceeded(e -> displayBooks(task.getValue(), MessageConstant.LIST_COMIC + currentPage));
        task.setOnFailed(e -> showErrorMessage(MessageConstant.API_ERROR_CALL));

        new Thread(task).start();
    }

    private void searchAndDisplayBooks() {
        updateStatusLabel(MessageConstant.SEARCH_LOADING + currentKeyword + "' (Trang " + currentPage + ")...");
        bookListContainer.getChildren().clear();

        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() throws Exception {
                return bookService.searchBooks(currentKeyword, currentPage);
            }
        };

        task.setOnSucceeded(e -> displayBooks(task.getValue(), MessageConstant.SEARCH_RESULT + currentKeyword));
        task.setOnFailed(e -> showErrorMessage(MessageConstant.ERROR_SEARCH_OR_CONNECT_WIFI));

        new Thread(task).start();
    }

    private void loadHiddenBooksOnly() {
        updateStatusLabel(MessageConstant.UPDATE_STATUS);
        bookListContainer.getChildren().clear();

        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() throws Exception {
                return hiddenBookDAO.getAllHiddenBooksForAdmin();
            }
        };

        task.setOnSucceeded(e -> displayBooks(task.getValue(), MessageConstant.LIST_COMIC_HIDDEN));
        task.setOnFailed(e -> showErrorMessage(MessageConstant.CALL_DATABASE_ERROR));

        new Thread(task).start();
    }

    private void showErrorMessage(String msg) {
        bookListContainer.getChildren().clear();
        updateStatusLabel(msg);
        lblStatus.setStyle(MessageConstant.COLOR_14);
    }

    private void updateStatusLabel(String text) {
        Platform.runLater(() -> {
            if (lblStatus != null) {
                lblStatus.setText(text);
                lblStatus.setStyle(MessageConstant.COLOR_15);
            }
        });
    }

    private void displayBooks(List<ApiBookItem> books, String successMessage) {
        updateStatusLabel(successMessage);
        bookListContainer.getChildren().clear();

        if (books == null || books.isEmpty()) {
            bookListContainer.getChildren().add(new Label("Không có truyện nào trùng khớp ở khu vực này."));
            return;
        }

        Set<String> hiddenSlugs = hiddenBookDAO.getAllHiddenSlugs();

        for (ApiBookItem book : books) {
            boolean isCurrentlyHidden = hiddenSlugs.contains(book.getSlug());
            VBox card = createBookCard(book, isCurrentlyHidden);
            bookListContainer.getChildren().add(card);
        }
    }

    private VBox createBookCard(ApiBookItem book, boolean isHiddenInitial) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(120);
        card.setStyle(MessageConstant.COLOR_5);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(140);
        Rectangle clip = new Rectangle(100, 140);
        clip.setArcWidth(10); clip.setArcHeight(10);
        imageView.setClip(clip);

        if (book.getThumbUrl() != null && !book.getThumbUrl().isEmpty()) {
            String imageUrl = book.getThumbUrl().startsWith("http")
                    ? book.getThumbUrl()
                    : "https://img.otruyenapi.com/uploads/comics/" + book.getThumbUrl();
            ImageLoaderGlobal.setImage(imageUrl, imageView);
        } else {
            imageView.setStyle("-fx-background-color: #cccccc;");
        }

        Label lblName = new Label(book.getName());
        lblName.setStyle(MessageConstant.COLOR_6);
        lblName.setMaxWidth(100);
        lblName.setWrapText(true);
        lblName.setPrefHeight(40);

        Button btnToggle = new Button();
        btnToggle.setPrefWidth(100);
        updateButtonStyle(btnToggle, isHiddenInitial);

        btnToggle.setOnAction(e -> {
            boolean currentHidden = hiddenBookDAO.isHidden(book.getSlug());
            if (currentHidden) {
                if (hiddenBookDAO.showBook(book.getSlug())) {
                    updateButtonStyle(btnToggle, false);
                }
            } else {
                if (hiddenBookDAO.hideBook(book.getSlug(), book.getName(), book.getThumbUrl())) {
                    updateButtonStyle(btnToggle, true);
                }
            }
        });

        card.getChildren().addAll(imageView, lblName, btnToggle);
        return card;
    }

    private void updateButtonStyle(Button btn, boolean isHidden) {
        if (isHidden) {
            btn.setText(MessageConstant.HIDDEN);
            btn.setStyle(MessageConstant.COLOR_7);
        } else {
            btn.setText(MessageConstant.NO_HIDDEN);
            btn.setStyle(MessageConstant.COLOR_8);
        }
    }
}