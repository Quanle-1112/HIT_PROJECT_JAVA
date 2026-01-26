package org.example.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private final BookService bookService = new BookService();
    private Stage loadingStage;

    @FXML
    public void initialize() {
        backButton.setOnAction(event ->
                SceneUtils.switchScene(backButton, "/view/home_screen.fxml", "Trang chủ")
        );
    }

    public void initData(String type, String displayTitle) {
        titleLabel.setText(displayTitle);

        Platform.runLater(() -> {
            Stage owner = (Stage) backButton.getScene().getWindow();
            loadingStage = SceneUtils.showLoading(owner);
        });

        loadData(type);
    }

    private void loadData(String type) {
        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() {
                switch (type) {
                    case "completed": return bookService.getCompletedBooks(1);
                    case "coming_soon": return bookService.getComingSoonBooks(1);
                    default: return bookService.getNewBooks(1);
                }
            }
        };

        task.setOnSucceeded(event -> {
            listContainer.getChildren().clear();
            List<ApiBookItem> books = task.getValue();

            if (books == null || books.isEmpty()) {
                listContainer.getChildren().add(new Label("Không có dữ liệu."));
            } else {
                try {
                    for (ApiBookItem book : books) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/book_list_item.fxml"));
                        HBox item = loader.load();

                        BookListItemController itemController = loader.getController();
                        itemController.setData(book);

                        listContainer.getChildren().add(item);
                    }
                } catch (IOException e) { e.printStackTrace(); }
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