package org.example.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import org.example.api.apiAll.ApiCategory;
import org.example.dao.AdminDAO;
import org.example.data.BookService;
import org.example.utils.SceneUtils;

import java.util.List;

public class StatsController {

    @FXML private Button btnBack;
    @FXML private BarChart<String, Number> chartUserGrowth;
    @FXML private PieChart chartCategory;

    private final AdminDAO adminDAO = new AdminDAO();
    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        btnBack.setOnAction(e ->
                SceneUtils.switchScene(btnBack, "/view/admin/admin_dashboard.fxml", "Admin Dashboard")
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
                series.setName("Người dùng mới đăng ký");

                series.getData().add(new XYChart.Data<>("Tháng 10", totalUsers * 0.1));
                series.getData().add(new XYChart.Data<>("Tháng 11", totalUsers * 0.2));
                series.getData().add(new XYChart.Data<>("Tháng 12", totalUsers * 0.3));
                series.getData().add(new XYChart.Data<>("Hiện tại", totalUsers));

                javafx.application.Platform.runLater(() -> {
                    chartUserGrowth.getData().clear();
                    chartUserGrowth.getData().add(series);
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

            chartCategory.setData(pieData);
            chartCategory.setTitle("Phân bố thể loại truyện");
        });

        new Thread(taskCategory).start();
    }
}