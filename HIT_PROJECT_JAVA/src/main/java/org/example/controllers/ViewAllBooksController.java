package org.example.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.api.apiAll.ApiBookItem;
import org.example.data.BookService;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.List;

public class ViewAllBooksController {

    @FXML private Button backButton;
    @FXML private Label titleLabel;
    @FXML private VBox listContainer;
    @FXML private ScrollPane scrollPane;

    @FXML private Button btnPrevious;
    @FXML private Button btnNext;
    @FXML private Label pageLabel;

    private final BookService bookService = new BookService();
    private Stage loadingStage;

    private int currentPage = 1;
    private String currentType = "";

    @FXML
    public void initialize() {
        backButton.setOnAction(event ->
                SceneUtils.switchScene(backButton, "/view/home_screen.fxml", "Trang chủ")
        );

        btnPrevious.setOnAction(e -> changePage(-1));
        btnNext.setOnAction(e -> changePage(1));
    }

    public void initData(String type, String displayTitle) {
        this.currentType = type;
        this.currentPage = 1;

        titleLabel.setText(displayTitle);
        updatePaginationUI();

        Platform.runLater(() -> {
            Stage owner = (Stage) backButton.getScene().getWindow();
            loadingStage = SceneUtils.showLoading(owner);
        });

        loadData();
    }

    private void changePage(int delta) {
        currentPage += delta;
        if (currentPage < 1) currentPage = 1;

        updatePaginationUI();

        Platform.runLater(() -> {
            Stage owner = (Stage) backButton.getScene().getWindow();
            loadingStage = SceneUtils.showLoading(owner);
        });

        loadData();
    }

    private void updatePaginationUI() {
        pageLabel.setText("Trang " + currentPage);
        btnPrevious.setDisable(currentPage == 1);
    }

    private void loadData() {
        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() {
                switch (currentType) {
                    case "completed": return bookService.getCompletedBooks(currentPage);
                    case "coming_soon": return bookService.getComingSoonBooks(currentPage);
                    default: return bookService.getNewBooks(currentPage);
                }
            }
        };

        task.setOnSucceeded(event -> {
            listContainer.getChildren().clear();
            List<ApiBookItem> books = task.getValue();

            if (books == null || books.isEmpty()) {
                listContainer.getChildren().add(new Label("Không còn dữ liệu."));
                btnNext.setDisable(true);
            } else {
                btnNext.setDisable(false);

                try {
                    for (ApiBookItem book : books) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/book_list_item.fxml"));
                        HBox item = loader.load();

                        BookListItemController itemController = loader.getController();
                        itemController.setData(book);

                        listContainer.getChildren().add(item);
                    }
                } catch (IOException e) { e.printStackTrace(); }

                scrollPane.setVvalue(0.0);
            }
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));
        });

        task.setOnFailed(event -> {
            listContainer.getChildren().clear();
            listContainer.getChildren().add(new Label("Lỗi kết nối!"));
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));
        });

        new Thread(task).start();
    }
}