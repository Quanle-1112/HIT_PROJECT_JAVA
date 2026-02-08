package org.example.controllers.read;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.example.api.apiAll.ApiBookItem;
import org.example.api.apiAll.ApiCategory;
import org.example.data.BookService;
import org.example.utils.SceneUtils;
import org.example.utils.UIFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HomeScreenController {

    @FXML private HBox newBooksContainer;
    @FXML private HBox completedBooksContainer;
    @FXML private HBox comingSoonContainer;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;

    @FXML private FlowPane categoryContainer;
    @FXML private Button btnMoreCategory;

    @FXML private Button btnMoreNew;
    @FXML private Button btnMoreCompleted;
    @FXML private Button btnMoreComing;

    @FXML private Button btnHome, btnHistory, btnFavorite, btnAI, btnAccount;

    private final BookService bookService = new BookService();
    private final String[] PASTEL_COLORS = {
            "#AEEEEE", "#FFDAB9", "#E6E6FA", "#FFDEAD", "#98FB98", "#87CEFA", "#FFB6C1", "#D3D3D3"
    };

    @FXML
    public void initialize() {
        setupBottomNavigation();
        setupSearch();
        setupViewAllButtons();

        loadBooksAsync("NEW", newBooksContainer);
        loadBooksAsync("COMPLETED", completedBooksContainer);
        loadBooksAsync("COMING", comingSoonContainer);

        if (categoryContainer != null) {
            loadDefaultCategories();
        } else {
            System.err.println("LỖI: Không tìm thấy 'categoryContainer' trong FXML. Hãy kiểm tra fx:id.");
        }

        if (btnMoreCategory != null) {
            btnMoreCategory.setOnAction(e ->
                    SceneUtils.switchScene(btnMoreCategory, "/view/read/view_all_categories.fxml", "Tất cả thể loại")
            );
        }
    }

    private void loadAllCategoriesFromApi() {
        if (categoryContainer == null) return;

        categoryContainer.getChildren().clear();
        categoryContainer.getChildren().add(new Label("Đang tải danh sách..."));

        CompletableFuture.supplyAsync(() -> bookService.getAllCategories())
                .thenAccept(categories -> Platform.runLater(() -> {
                    if (categoryContainer == null) return;

                    categoryContainer.getChildren().clear();

                    if (categories == null || categories.isEmpty()) {
                        categoryContainer.getChildren().add(new Label("Lỗi tải thể loại."));
                        return;
                    }

                    int colorIndex = 0;
                    for (ApiCategory cat : categories) {
                        Button btn = createCategoryButton(cat.getName(), cat.getSlug(), colorIndex++);
                        categoryContainer.getChildren().add(btn);
                    }

                    if (btnMoreCategory != null) btnMoreCategory.setVisible(false);
                }));
    }

    private void loadDefaultCategories() {
        if (categoryContainer == null) return;

        Map<String, String> cats = Map.of(
                "Action", "action", "Comedy", "comedy", "Drama", "drama", "Fantasy", "fantasy", "Manhwa", "manhwa"
        );

        try {
            List<Button> buttons = UIFactory.createCategoryButtons(cats, event -> {
                Button btn = (Button) event.getSource();
                String slug = (String) btn.getUserData();
                handleCategoryClick(btn, slug);
            });
            categoryContainer.getChildren().setAll(buttons);
        } catch (Exception e) {
            int colorIndex = 0;
            categoryContainer.getChildren().clear();
            for (Map.Entry<String, String> entry : cats.entrySet()) {
                Button btn = createCategoryButton(entry.getKey(), entry.getValue(), colorIndex++);
                categoryContainer.getChildren().add(btn);
            }
        }
    }

    private Button createCategoryButton(String name, String slug, int index) {
        Button btn = new Button(name);
        btn.setUserData(slug);
        btn.setFont(Font.font("System", FontWeight.BOLD, 12));
        btn.setTextFill(Color.web("#333333"));
        btn.setPadding(new Insets(8, 15, 8, 15));
        btn.setPrefHeight(30);
        String colorHex = PASTEL_COLORS[index % PASTEL_COLORS.length];
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 15; -fx-cursor: hand;");

        btn.setOnAction(e -> handleCategoryClick(btn, slug));
        return btn;
    }

    private void handleCategoryClick(Button sourceBtn, String slug) {
        SearchResultController controller = SceneUtils.switchScene(searchButton, "/view/read/search_result.fxml", "Thể loại: " + sourceBtn.getText());
        if (controller != null) {
            controller.initData("CATEGORY", slug, "Thể loại: " + sourceBtn.getText());
        }
    }


    private void setupSearch() {
        if (searchButton != null) searchButton.setOnAction(e -> handleSearch());
        if (searchTextField != null) searchTextField.setOnAction(e -> handleSearch());
    }

    private void handleSearch() {
        String query = searchTextField.getText().trim();
        if (query.isEmpty()) return;
        SearchResultController controller = SceneUtils.switchScene(searchButton, "/view/read/search_result.fxml", "Tìm kiếm: " + query);
        if (controller != null) controller.initData("SEARCH", query, "Kết quả tìm kiếm: " + query);
    }


    private void setupBottomNavigation() {
        if (btnHome != null) {
            btnHome.setStyle("-fx-background-color: #F0F2F5; -fx-background-radius: 10; -fx-text-fill: #19345D; -fx-font-weight: bold;");
            btnHome.setDisable(true);
        }

        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", "Home"));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", "History"));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", "Favorite"));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", "Account"));
    }

    private void setupViewAllButtons() {
        if (btnMoreNew != null) btnMoreNew.setOnAction(e -> openViewAll("NEW", "Truyện Mới Cập Nhật"));
        if (btnMoreCompleted != null) btnMoreCompleted.setOnAction(e -> openViewAll("COMPLETED", "Truyện Đã Hoàn Thành"));
        if (btnMoreComing != null) btnMoreComing.setOnAction(e -> openViewAll("COMING", "Sắp Ra Mắt"));
    }

    private void openViewAll(String type, String title) {
        ViewAllBooksController controller = SceneUtils.switchScene(btnMoreNew, "/view/read/view_all_books.fxml", title);
        if (controller != null) controller.initData(type, title);
    }

    private void loadBooksAsync(String type, HBox container) {
        if (container == null) return;
        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() {
                if ("NEW".equals(type)) return bookService.getNewBooks(1);
                if ("COMPLETED".equals(type)) return bookService.getCompletedBooks(1);
                if ("COMING".equals(type)) return bookService.getComingSoonBooks(1);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            List<ApiBookItem> books = task.getValue();
            if (books != null) {
                Platform.runLater(() -> {
                    container.getChildren().clear();
                    try {
                        for (ApiBookItem book : books) {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_item.fxml"));
                            VBox card = loader.load();
                            BookItemController ctrl = loader.getController();
                            ctrl.setData(book);
                            container.getChildren().add(card);
                        }
                    } catch (IOException ex) { ex.printStackTrace(); }
                });
            }
        });
        new Thread(task).start();
    }
}