package org.example.controllers.read;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import org.example.constant.MessageConstant;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.example.api.apiAll.ApiCategory;
import org.example.data.BookService;
import org.example.utils.SceneUtils;

import java.io.IOException;
import java.util.List;

public class ViewAllCategoriesController {

    @FXML private Button backButton;
    @FXML private FlowPane categoryContainer;
    @FXML private Label statusLabel;

    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        backButton.setOnAction(e -> SceneUtils.switchScene(backButton, "/view/read/home_screen.fxml", "Trang chủ"));

        loadCategories();
    }

    private void loadCategories() {
        Task<List<ApiCategory>> task = new Task<>() {
            @Override
            protected List<ApiCategory> call() {
                return bookService.getAllCategories();
            }
        };

        task.setOnSucceeded(e -> {
            List<ApiCategory> categories = task.getValue();
            if (categories == null || categories.isEmpty()) {
                statusLabel.setText(MessageConstant.MSG_LOADING);
            } else {
                statusLabel.setVisible(false);
                renderCategoryButtons(categories);
            }
        });

        task.setOnFailed(e -> {
            statusLabel.setText(MessageConstant.ERR_NETWORK);
            e.getSource().getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void renderCategoryButtons(List<ApiCategory> categories) {
        categoryContainer.getChildren().clear();

        for (ApiCategory cat : categories) {
            Button btn = new Button(cat.getName());

            btn.setStyle("-fx-background-color: WHITE; -fx-border-color: #19345D; -fx-border-radius: 20; -fx-background-radius: 20; -fx-cursor: hand;");
            btn.setTextFill(javafx.scene.paint.Color.web("#19345D"));
            btn.setFont(Font.font("System", FontWeight.BOLD, 13));
            btn.setPadding(new Insets(8, 15, 8, 15));

            btn.setOnMouseEntered(e -> {
                btn.setStyle("-fx-background-color: #19345D; -fx-border-color: #19345D; -fx-border-radius: 20; -fx-background-radius: 20; -fx-cursor: hand;");
                btn.setTextFill(javafx.scene.paint.Color.WHITE);
            });
            btn.setOnMouseExited(e -> {
                btn.setStyle("-fx-background-color: WHITE; -fx-border-color: #19345D; -fx-border-radius: 20; -fx-background-radius: 20; -fx-cursor: hand;");
                btn.setTextFill(javafx.scene.paint.Color.web("#19345D"));
            });

            btn.setOnAction(event -> openCategoryDetail(cat));

            categoryContainer.getChildren().add(btn);
        }
    }

    private void openCategoryDetail(ApiCategory cat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/search_result.fxml"));
            Parent root = loader.load();

            SearchResultController controller = loader.getController();
            controller.initData("CATEGORY", cat.getSlug(), "Thể loại: " + cat.getName());

            Stage stage = (Stage) categoryContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - " + cat.getName());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}