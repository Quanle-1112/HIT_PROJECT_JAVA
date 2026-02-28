package org.example.controllers.read;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.api.apiAll.ApiBookItem;
import org.example.constant.MessageConstant;
import org.example.data.BookService;
import org.example.exception.AppException;
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

    @FXML private TextField pageInput;

    private final BookService bookService = new BookService();
    private Stage loadingStage;

    private int currentPage = 1;
    private String currentType = "";

    @FXML
    public void initialize() {
        backButton.setOnAction(event ->
                SceneUtils.switchScene(backButton, "/view/read/home_screen.fxml", MessageConstant.TITLE_HOME)
        );

        btnPrevious.setOnAction(e -> changePage(-1));

        btnNext.setOnAction(e -> changePage(1));

        if (pageInput != null) {
            pageInput.setOnAction(event -> handlePageInput());

            pageInput.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    pageInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            pageInput.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    handlePageInput();
                }
            });
        }
    }

    public void initData(String type, String displayTitle) {
        this.currentType = type;
        titleLabel.setText(displayTitle);
        loadData();
    }

    private void changePage(int delta) {
        if (currentPage + delta > 0) {
            currentPage += delta;
            loadData();
        }
    }

    private void handlePageInput() {
        String input = pageInput.getText();
        if (input == null || input.trim().isEmpty()) {
            pageInput.setText(String.valueOf(currentPage));
            return;
        }

        try {
            int newPage = Integer.parseInt(input);
            if (newPage < 1) newPage = 1;

            if (newPage != currentPage) {
                currentPage = newPage;
                loadData();
            } else {
                pageInput.setText(String.valueOf(currentPage));
            }
        } catch (NumberFormatException e) {
            pageInput.setText(String.valueOf(currentPage));
        }
    }

    private void loadData() {
        btnPrevious.setDisable(currentPage == 1);
        if (pageInput != null && !pageInput.isFocused()) {
            pageInput.setText(String.valueOf(currentPage));
        }

        Platform.runLater(() -> {
            Stage owner = (Stage) backButton.getScene().getWindow();
            loadingStage = SceneUtils.showLoading(owner);
        });

        Task<List<ApiBookItem>> task = new Task<>() {
            @Override
            protected List<ApiBookItem> call() {
                switch (currentType) {
                    case "COMPLETED": return bookService.getCompletedBooks(currentPage);
                    case "COMING": return bookService.getComingSoonBooks(currentPage);
                    default: return bookService.getNewBooks(currentPage);
                }
            }
        };

        task.setOnSucceeded(event -> {
            listContainer.getChildren().clear();
            List<ApiBookItem> books = task.getValue();

            if (books == null || books.isEmpty()) {
                listContainer.getChildren().add(new Label(MessageConstant.LIST_END_DATA));
                btnNext.setDisable(true);
            } else {
                btnNext.setDisable(false);

                try {
                    for (ApiBookItem book : books) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_list_item.fxml"));
                        HBox item = loader.load();

                        BookListItemController itemController = loader.getController();
                        itemController.setData(book);

                        listContainer.getChildren().add(item);
                    }
                } catch (IOException e) {
                    throw new AppException(MessageConstant.ERR_SYSTEM, e);
                }

                scrollPane.setVvalue(0.0);

                if (pageInput != null) {
                    pageInput.setText(String.valueOf(currentPage));
                }
            }
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));
        });

        task.setOnFailed(event -> {
            listContainer.getChildren().clear();
            listContainer.getChildren().add(new Label(MessageConstant.ERR_NETWORK));
            Platform.runLater(() -> SceneUtils.closeLoading(loadingStage));

            throw new AppException(MessageConstant.ERR_NETWORK, task.getException());
        });

        new Thread(task).start();
    }
}