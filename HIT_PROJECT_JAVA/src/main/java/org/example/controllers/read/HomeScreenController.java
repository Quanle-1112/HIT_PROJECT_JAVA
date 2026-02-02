package org.example.controllers.read;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.api.apiAll.ApiBookItem;
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

    @FXML private Button btnMoreNew;
    @FXML private Button btnMoreCompleted;
    @FXML private Button btnMoreComing;

    @FXML private Button btnHome;
    @FXML private Button btnHistory;
    @FXML private Button btnFavorite;
    @FXML private Button btnAccount;

    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        CompletableFuture.runAsync(() -> loadSection(newBooksContainer, "new"));
        CompletableFuture.runAsync(() -> loadSection(completedBooksContainer, "completed"));
        CompletableFuture.runAsync(() -> loadSection(comingSoonContainer, "coming_soon"));

        loadCategories();

        searchButton.setOnAction(event -> handleSearch());
        searchTextField.setOnAction(event -> handleSearch());

        btnMoreNew.setOnAction(e -> openViewAll("NEW_BOOKS", "Truyện Mới Nhất"));
        btnMoreCompleted.setOnAction(e -> openViewAll("COMPLETED_BOOKS", "Truyện Đã Hoàn Thành"));
        btnMoreComing.setOnAction(e -> openViewAll("COMING_SOON_BOOKS", "Truyện Sắp Ra Mắt"));

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        if (btnHome != null) {
            btnHome.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #19345D; -fx-font-weight: bold;");
        }

        if (btnHistory != null) {
            btnHistory.setOnAction(e -> SceneUtils.switchSceneAsync(btnHistory, "/view/history/history_screen.fxml", "Lịch sử đọc"));
        }

        if (btnFavorite != null) {
            btnFavorite.setOnAction(e -> SceneUtils.switchSceneAsync(btnFavorite, "/view/favorite/favorite_screen.fxml", "Truyện yêu thích"));
        }

        if (btnAccount != null) {
            btnAccount.setOnAction(e -> SceneUtils.switchSceneAsync(btnAccount, "/view/read/account_screen.fxml", "Tài khoản"));
        }
    }


    private void loadSection(HBox container, String type) {
        Platform.runLater(() -> {
            container.getChildren().clear();
            container.getChildren().add(new Label("Đang tải..."));
        });

        List<ApiBookItem> books;
        try {
            switch (type) {
                case "completed": books = bookService.getCompletedBooks(1); break;
                case "coming_soon": books = bookService.getComingSoonBooks(1); break;
                default: books = bookService.getNewBooks(1); break;
            }
        } catch (Exception e) {
            books = null;
        }

        List<ApiBookItem> finalBooks = books;
        Platform.runLater(() -> {
            container.getChildren().clear();
            if (finalBooks == null || finalBooks.isEmpty()) {
                container.getChildren().add(new Label("Không có dữ liệu"));
                return;
            }

            try {
                for (ApiBookItem book : finalBooks) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_item.fxml"));
                    VBox card = loader.load();
                    BookItemController itemController = loader.getController();
                    itemController.setData(book);
                    container.getChildren().add(card);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleSearch() {
        String query = searchTextField.getText().trim();
        if (query.isEmpty()) return;

        SearchResultController controller = SceneUtils.switchScene(searchButton, "/view/read/search_result.fxml", "Tìm kiếm: " + query);
        if (controller != null) {
            controller.initData("SEARCH", query, "Kết quả tìm kiếm: " + query);
        }
    }

    private void openViewAll(String type, String title) {
        ViewAllBooksController controller = SceneUtils.switchScene(btnMoreNew, "/view/read/view_all_books.fxml", title);
        if (controller != null) {
            controller.initData(type, title);
        }
    }

    private void loadCategories() {
        Map<String, String> cats = Map.of(
                "Action", "action", "Comedy", "comedy", "Drama", "drama", "Fantasy", "fantasy"
        );
        List<Button> buttons = UIFactory.createCategoryButtons(cats, event -> {
            Button btn = (Button) event.getSource();
            String slug = (String) btn.getUserData();
            SearchResultController controller = SceneUtils.switchScene(searchButton, "/view/read/search_result.fxml", "Thể loại: " + btn.getText());
            if (controller != null) {
                controller.initData("CATEGORY", slug, "Thể loại: " + btn.getText());
            }
        });
        categoryContainer.getChildren().addAll(buttons);
    }
}