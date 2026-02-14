package org.example.controllers.admin;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.api.apiAll.ApiBookItem;
import org.example.constant.MessageConstant;
import org.example.controllers.read.BookItemController;
import org.example.dao.AdminDAO;
import org.example.data.BookService;
import org.example.exception.AppException;
import org.example.exception.UIExceptionHandler;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ManageBooksController {

    @FXML private Button btnBack;
    @FXML private TextField txtSearch;
    @FXML private Button btnSearchBook;
    @FXML private FlowPane bookListContainer;

    private final BookService bookService = new BookService();
    private final AdminDAO adminDAO = new AdminDAO();
    private Stage loadingStage;

    @FXML
    public void initialize() {
        btnBack.setOnAction(e ->
                SceneUtils.switchScene(btnBack, "/view/admin/admin_dashboard.fxml", "Admin Dashboard")
        );

        btnSearchBook.setOnAction(e -> handleSearch());

        txtSearch.setOnAction(e -> handleSearch());

        loadBooks("", false);
    }

    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            UIExceptionHandler.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập tên truyện cần tìm!");
            return;
        }
        loadBooks(query, true);
    }

    private void loadBooks(String query, boolean isSearch) {
        Platform.runLater(() -> {
            Stage owner = (Stage) bookListContainer.getScene().getWindow();
            loadingStage = SceneUtils.showLoading(owner);
        });

        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() throws Exception {
                if (isSearch) {
                    return bookService.searchBooks(query, 1);
                } else {
                    return bookService.getNewBooks(1);
                }
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));
            List<ApiBookItem> books = task.getValue();
            renderBooks(books);
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));
            UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách truyện.");
            e.getSource().getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void renderBooks(List<ApiBookItem> books) {
        bookListContainer.getChildren().clear();
        if (books == null || books.isEmpty()) {
            bookListContainer.getChildren().add(new Label("Không tìm thấy truyện nào."));
            return;
        }

        Set<String> hiddenSlugs = adminDAO.getHiddenBookSlugs();

        try {
            for (ApiBookItem book : books) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_item.fxml"));
                VBox card = loader.load();
                BookItemController controller = loader.getController();
                controller.setData(book);

                if (hiddenSlugs.contains(book.getSlug())) {
                    card.setOpacity(0.5);
                }

                card.setOnMouseClicked(null);

                ContextMenu contextMenu = new ContextMenu();

                MenuItem itemHide = new MenuItem("Ẩn truyện này (Ban)");
                MenuItem itemUnhide = new MenuItem("Hiện truyện này (Unban)");

                itemHide.setOnAction(event -> toggleBookVisibility(book, true, card));
                itemUnhide.setOnAction(event -> toggleBookVisibility(book, false, card));

                if (hiddenSlugs.contains(book.getSlug())) {
                    contextMenu.getItems().add(itemUnhide);
                } else {
                    contextMenu.getItems().add(itemHide);
                }

                card.setOnContextMenuRequested(event ->
                        contextMenu.show(card, event.getScreenX(), event.getScreenY())
                );

                bookListContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void toggleBookVisibility(ApiBookItem book, boolean makeHidden, VBox card) {
        boolean success;
        if (makeHidden) {
            success = adminDAO.hideBook(book.getSlug(), book.getName());
        } else {
            success = adminDAO.unhideBook(book.getSlug());
        }

        if (success) {
            String msg = makeHidden ? "Đã ẩn truyện: " + book.getName() : "Đã hiển thị lại truyện: " + book.getName();
            UIExceptionHandler.showAlert(Alert.AlertType.INFORMATION, "Thành công", msg);

            if (makeHidden) {
                card.setOpacity(0.5);
            } else {
                card.setOpacity(1.0);
            }
            handleSearch();
        } else {
            UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", "Thao tác thất bại. Vui lòng thử lại.");
        }
    }
}