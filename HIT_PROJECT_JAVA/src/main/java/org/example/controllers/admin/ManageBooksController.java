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

    private final BookService bookService = new BookService();
    private final HiddenBookDAO hiddenBookDAO = new HiddenBookDAO();

    @FXML
    public void initialize() {
        btnBack.setOnAction(e -> SceneUtils.switchScene(btnBack, "/view/admin/admin_dashboard.fxml", MessageConstant.TITLE_ADMIN));

        btnSearchBook.setOnAction(e -> {
            String query = txtSearch.getText().trim();
            if (!query.isEmpty()) {
                searchAndDisplayBooks(query);
            }
        });

        loadDefaultBooks();
    }

    private void loadDefaultBooks() {
        bookListContainer.getChildren().clear();
        bookListContainer.getChildren().add(new Label(MessageConstant.LOADING_LIST_COMIC));

        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() throws Exception {
                return bookService.getNewBooks(1);
            }
        };

        task.setOnSucceeded(e -> displayBooks(task.getValue()));
        task.setOnFailed(e -> {
            bookListContainer.getChildren().clear();
            bookListContainer.getChildren().add(new Label(MessageConstant.API_ERROR));
        });

        new Thread(task).start();
    }

    private void searchAndDisplayBooks(String keyword) {
        bookListContainer.getChildren().clear();
        bookListContainer.getChildren().add(new Label(MessageConstant.LOADING_SEARCH));

        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() throws Exception {
                return bookService.searchBooks(keyword, 1);
            }
        };

        task.setOnSucceeded(e -> displayBooks(task.getValue()));
        task.setOnFailed(e -> {
            bookListContainer.getChildren().clear();
            bookListContainer.getChildren().add(new Label(MessageConstant.CONNECT_ERROR));
        });

        new Thread(task).start();
    }

    private void displayBooks(List<ApiBookItem> books) {
        bookListContainer.getChildren().clear();

        if (books == null || books.isEmpty()) {
            bookListContainer.getChildren().add(new Label(MessageConstant.SEARCH_COMIC_ERROR));
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
        ImageLoaderGlobal.setImage("https://img.otruyenapi.com/uploads/comics/" + book.getThumbUrl(), imageView);

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
                if (hiddenBookDAO.hideBook(book.getSlug())) {
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