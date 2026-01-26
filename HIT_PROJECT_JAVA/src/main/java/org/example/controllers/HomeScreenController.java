package org.example.controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.api.apiAll.ApiBookItem;
import org.example.data.BookService;
import org.example.utils.SceneUtils;
import org.example.utils.UIFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeScreenController {

    @FXML private HBox newBooksContainer;
    @FXML private HBox completedBooksContainer;
    @FXML private HBox comingSoonContainer;

    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private FlowPane categoryContainer;

    @FXML private Button btnMoreNew;
    @FXML private Button btnMoreCompleted;
    @FXML private Button btnMoreComing;

    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        loadSection(newBooksContainer, "new");
        loadSection(completedBooksContainer, "completed");
        loadSection(comingSoonContainer, "coming_soon");

        initCategories();

        if (searchButton != null) searchButton.setOnAction(event -> handleSearch());
        if (searchTextField != null) searchTextField.setOnAction(event -> handleSearch());

        if (btnMoreNew != null) btnMoreNew.setOnAction(e -> openViewAll("new", "Truyện Mới Cập Nhật"));
        if (btnMoreCompleted != null) btnMoreCompleted.setOnAction(e -> openViewAll("completed", "Truyện Đã Hoàn Thành"));
        if (btnMoreComing != null) btnMoreComing.setOnAction(e -> openViewAll("coming_soon", "Sắp Ra Mắt"));
    }

    private void openViewAll(String type, String title) {
        ViewAllBooksController controller = SceneUtils.switchScene(
                newBooksContainer,
                "/view/view_all_books.fxml",
                title
        );
        if (controller != null) {
            controller.initData(type, title);
        }
    }

    private void initCategories() {
        Map<String, String> categories = new LinkedHashMap<>();
        categories.put("Action", "action");
        categories.put("Adventure", "adventure");
        categories.put("Comedy", "comedy");
        categories.put("Chuyển Sinh", "chuyen-sinh");
        categories.put("Cổ Đại", "co-dai");
        categories.put("Manhua", "manhua");
        categories.put("Manhwa", "manhwa");
        categories.put("Ngôn Tình", "ngon-tinh");
        categories.put("Romance", "romance");
        categories.put("Xuyên Không", "xuyen-khong");
        categories.put("Đam Mỹ", "dam-my");
        categories.put("Huyền Huyễn", "huyen-huyen");

        List<Button> buttons = UIFactory.createCategoryButtons(categories, this::handleCategoryClick);
        if (categoryContainer != null) categoryContainer.getChildren().addAll(buttons);
    }

    private void handleCategoryClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String slug = (String) clickedButton.getUserData();
        String name = clickedButton.getText();
        openSearchResult("CATEGORY", slug, "Thể loại: " + name);
    }

    private void handleSearch() {
        String keyword = searchTextField.getText().trim();
        if (keyword.isEmpty()) return;
        openSearchResult("SEARCH", keyword, "Kết quả tìm kiếm: " + keyword);
    }

    private void openSearchResult(String type, String query, String title) {
        SearchResultController controller = SceneUtils.switchScene(
                newBooksContainer,
                "/view/search_result.fxml",
                title
        );
        if (controller != null) {
            controller.initData(type, query, title);
        }
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
            } catch (IOException e) { e.printStackTrace(); }
        });
        task.setOnFailed(event -> {
            container.getChildren().clear();
            container.getChildren().add(new Label("Lỗi kết nối!"));
        });
        new Thread(task).start();
    }
}