package org.example.controllers.read;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.api.apiAll.ApiBookItem;
import org.example.data.BookService;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.List;

public class SearchResultController {

    @FXML private Button backButton;
    @FXML private Label titleLabel;
    @FXML private FlowPane resultContainer;

    private final BookService bookService = new BookService();
    private Stage loadingStage;

    @FXML
    public void initialize() {
        backButton.setOnAction(event ->
                SceneUtils.switchScene(backButton, "/view/read/home_screen.fxml", "Trang chủ")
        );
    }

    public void initData(String type, String query, String displayTitle) {
        titleLabel.setText(displayTitle);

        Platform.runLater(() -> {
            Stage owner = (Stage) backButton.getScene().getWindow();
            loadingStage = SceneUtils.showLoading(owner);
        });

        loadData(type, query);
    }

    private void loadData(String type, String query) {

        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() {
                if ("SEARCH".equals(type)) {
                    return bookService.searchBooks(query, 1);
                } else {
                    return bookService.getBooksByCategory(query, 1);
                }
            }
        };

        task.setOnSucceeded(event -> {
            resultContainer.getChildren().clear();
            List<ApiBookItem> books = task.getValue();

            if (books == null || books.isEmpty()) {
                resultContainer.getChildren().add(new Label("Không tìm thấy truyện nào."));
            } else {
                try {
                    for (ApiBookItem book : books) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_item.fxml"));
                        VBox card = loader.load();
                        BookItemController itemController = loader.getController();
                        itemController.setData(book);
                        resultContainer.getChildren().add(card);
                    }
                } catch (IOException e) { e.printStackTrace(); }
            }
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));
        });

        task.setOnFailed(event -> {
            resultContainer.getChildren().clear();
            resultContainer.getChildren().add(new Label("Lỗi kết nối!"));
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));
        });

        new Thread(task).start();
    }
}