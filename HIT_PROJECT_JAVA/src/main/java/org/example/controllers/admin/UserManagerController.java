package org.example.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.constant.MessageConstant;
import org.example.dao.UserDAO;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.Role;
import org.example.model.user.User;
import org.example.utils.SceneUtils;
import org.example.utils.SessionManager;

import java.util.List;
import java.util.Optional;

public class UserManagerController {

    @FXML private Button btnBack;
    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail; // Đã thêm cột này để sửa lỗi
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Void> colAction;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> masterData = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadData();
        setupSearch();

        btnBack.setOnAction(e ->
                SceneUtils.switchScene(btnBack, "/view/admin/admin_dashboard.fxml", "Admin Dashboard")
        );

        btnSearch.setOnAction(e -> tableUsers.refresh());
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colRole.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole().toString())
        );

        colAction.setCellFactory(createActionCellFactory());
    }

    private void loadData() {
        Task<List<User>> task = new Task<>() {
            @Override
            protected List<User> call() {
                return userDAO.getAllUsers();
            }
        };

        task.setOnSucceeded(e -> {
            masterData.setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách người dùng.");
        });

        new Thread(task).start();
    }

    private void setupSearch() {
        filteredData = new FilteredList<>(masterData, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(user.getId()).contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableUsers.comparatorProperty());

        tableUsers.setItems(sortedData);
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button btnChangeRole = new Button();
            private final Button btnBan = new Button();
            private final HBox pane = new HBox(10, btnChangeRole, btnBan);

            {
                pane.setAlignment(Pos.CENTER);
                btnChangeRole.setStyle("-fx-font-size: 11px; -fx-cursor: hand;");
                btnBan.setStyle("-fx-font-size: 11px; -fx-cursor: hand; -fx-text-fill: white;");

                btnChangeRole.setOnAction(event -> handleChangeRole(getTableView().getItems().get(getIndex())));
                btnBan.setOnAction(event -> handleChangeStatus(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    int currentAdminId = SessionManager.getInstance().getCurrentUserId();

                    if (user.getRole() == Role.ADMIN) {
                        btnChangeRole.setText("Hạ xuống User");
                        btnChangeRole.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                    } else {
                        btnChangeRole.setText("Lên Admin");
                        btnChangeRole.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
                    }

                    if ("BAN".equalsIgnoreCase(user.getStatus())) {
                        btnBan.setText("Mở khóa");
                        btnBan.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                    } else {
                        btnBan.setText("Khóa & Cấm");
                        btnBan.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
                    }

                    if (user.getId() == currentAdminId) {
                        btnChangeRole.setDisable(true);
                        btnBan.setDisable(true);
                        btnBan.setText("Tôi");
                    } else {
                        btnChangeRole.setDisable(false);
                        btnBan.setDisable(false);
                    }

                    setGraphic(pane);
                }
            }
        };
    }

    private void handleChangeRole(User user) {
        Role newRole = (user.getRole() == Role.ADMIN) ? Role.USER : Role.ADMIN;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận thay đổi quyền");
        alert.setHeaderText(null);
        alert.setContentText("Đổi quyền của " + user.getUsername() + " thành " + newRole + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = userDAO.updateUserRole(user.getId(), newRole.toString());

            if (success) {
                user.setRole(newRole);
                tableUsers.refresh();
                UIExceptionHandler.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật quyền.");
            } else {
                UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật thất bại.");
            }
        }
    }

    private void handleChangeStatus(User user) {
        String currentStatus = user.getStatus();
        boolean isBanning = !"BAN".equalsIgnoreCase(currentStatus);

        String newStatus = isBanning ? "BAN" : "ACTIVE";
        String actionTitle = isBanning ? "KHÓA TÀI KHOẢN" : "MỞ KHÓA TÀI KHOẢN";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(actionTitle);
        alert.setHeaderText(null);

        if (isBanning) {
            alert.setContentText("Bạn có chắc muốn KHÓA tài khoản " + user.getUsername() + "?\n" +
                    "Email " + user.getEmail() + " sẽ bị thêm vào danh sách đen (Blacklist).");
        } else {
            alert.setContentText("Bạn có chắc muốn MỞ KHÓA tài khoản " + user.getUsername() + "?\n" +
                    "Email sẽ được gỡ khỏi danh sách đen.");
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            boolean statusUpdated = userDAO.updateUserStatus(user.getId(), newStatus);

            boolean blacklistUpdated;
            if (isBanning) {
                blacklistUpdated = userDAO.addEmailToBlacklist(user.getEmail());
            } else {
                blacklistUpdated = userDAO.removeEmailFromBlacklist(user.getEmail());
            }

            if (statusUpdated) {
                user.setStatus(newStatus);
                tableUsers.refresh();

                String msg = "Đã cập nhật trạng thái thành công.";
                if (isBanning && blacklistUpdated) msg += "\nEmail đã được thêm vào Blacklist.";
                if (!isBanning && blacklistUpdated) msg += "\nEmail đã được gỡ khỏi Blacklist.";

                UIExceptionHandler.showAlert(Alert.AlertType.INFORMATION, "Thành công", msg);
            } else {
                UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", "Thao tác thất bại. Vui lòng thử lại.");
            }
        }
    }
}