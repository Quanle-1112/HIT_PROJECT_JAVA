package org.example.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.api.apiAll.ApiBookItem;
import org.example.data.BookService;

import java.io.IOException;
import java.util.List;

public class HomeScreenController {

    @FXML private HBox newBooksContainer;
    @FXML private HBox completedBooksContainer;
    @FXML private HBox comingSoonContainer;

    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        loadSection(newBooksContainer, "new");
        loadSection(completedBooksContainer, "completed");
        loadSection(comingSoonContainer, "coming_soon");
    }

    private void loadSection(HBox container, String type) {
        Label loading = new Label("Đang tải...");
        loading.setTextFill(Color.GRAY);
        container.getChildren().add(loading);

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
            container.getChildren().clear();
            List<ApiBookItem> books = task.getValue();
            if (books == null || books.isEmpty()) {
                container.getChildren().add(new Label("Không có dữ liệu"));
                return;
            }

            try {
                for (ApiBookItem book : books) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/book_item.fxml"));
                    VBox card = loader.load();

                    BookItemController itemController = loader.getController();
                    itemController.setData(book);

                    container.getChildren().add(card);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        task.setOnFailed(event -> {
            container.getChildren().clear();
            container.getChildren().add(new Label("Lỗi kết nối!"));
        });

        new Thread(task).start();
    }
}