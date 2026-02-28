package org.example.controllers.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.api.apiAll.ApiCategory;
import org.example.constant.MessageConstant;
import org.example.dao.AdminDAO;
import org.example.data.BookService;
import org.example.utils.SceneUtils;

import java.util.List;

public class StatsController {

    @FXML private Button btnBack;
    @FXML private BarChart<String, Number> chartUserGrowth;
    @FXML private PieChart chartCategory;

    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalBooks;

    private final AdminDAO adminDAO = new AdminDAO();
    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        btnBack.setOnAction(e ->
                SceneUtils.switchScene(btnBack, "/view/admin/admin_dashboard.fxml", MessageConstant.TITLE_ADMIN)
        );

        loadStatsData();
    }

    private void loadStatsData() {
        Task<Void> taskUserStats = new Task<>() {
            @Override
            protected Void call() {
                int totalUsers = adminDAO.countTotalUsers();
                int hiddenBooks = adminDAO.countHiddenBooks();

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(MessageConstant.NEW_USER);

                series.getData().add(new XYChart.Data<>("Tháng 10", totalUsers * 0.1));
                series.getData().add(new XYChart.Data<>("Tháng 11", totalUsers * 0.2));
                series.getData().add(new XYChart.Data<>("Tháng 12", totalUsers * 0.3));
                series.getData().add(new XYChart.Data<>("Hiện tại", totalUsers));

                Platform.runLater(() -> {
                    if (chartUserGrowth != null) {
                        chartUserGrowth.getData().clear();
                        chartUserGrowth.getData().add(series);
                    }

                    if (lblTotalUsers != null) {
                        lblTotalUsers.setText(String.valueOf(totalUsers));
                    }
                    if (lblTotalBooks != null) {
                        lblTotalBooks.setText(String.valueOf(hiddenBooks));
                    }
                });
                return null;
            }
        };
        new Thread(taskUserStats).start();

        Task<List<ApiCategory>> taskCategory = new Task<>() {
            @Override
            protected List<ApiCategory> call() {
                return bookService.getAllCategories();
            }
        };

        taskCategory.setOnSucceeded(e -> {
            List<ApiCategory> categories = taskCategory.getValue();
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

            if (categories != null) {
                int limit = Math.min(categories.size(), 5);
                for (int i = 0; i < limit; i++) {
                    pieData.add(new PieChart.Data(categories.get(i).getName(), 15));
                }
                pieData.add(new PieChart.Data("Khác", 25));
            }

            if (chartCategory != null) {
                chartCategory.setData(pieData);
                chartCategory.setTitle(MessageConstant.CATEGORY_COMIC);
            }
        });

        new Thread(taskCategory).start();
    }
}