package org.example.controllers.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.example.dao.UserDAO;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.Role;
import org.example.model.user.User;
import org.example.utils.SceneUtils;

import java.util.List;

public class UserManagerController {

    @FXML private Button btnBack;
    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Void> colAction;

    private final UserDAO userDAO = new UserDAO();
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadUserData();
        setupSearch();

        btnBack.setOnAction(e ->
                SceneUtils.switchScene(btnBack, "/view/admin/admin_dashboard.fxml", "Admin Dashboard")
        );
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btnAction = new Button();

                    {
                        btnAction.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            toggleUserStatus(user);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            User user = getTableView().getItems().get(getIndex());

                            if ("BAN".equalsIgnoreCase(user.getStatus())) {
                                btnAction.setText("Mở khóa");
                                btnAction.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                            } else {
                                btnAction.setText("Khóa");
                                btnAction.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 5;");
                            }

                            if (user.getRole() == Role.ADMIN) {
                                btnAction.setDisable(true);
                            } else {
                                btnAction.setDisable(false);
                            }

                            HBox hbox = new HBox(btnAction);
                            hbox.setAlignment(Pos.CENTER);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };

        colAction.setCellFactory(cellFactory);
    }

    private void loadUserData() {
        Task<List<User>> loadTask = new Task<>() {
            @Override
            protected List<User> call() {
                return userDAO.getAllUsers();
            }
        };

        loadTask.setOnSucceeded(e -> {
            userList.setAll(loadTask.getValue());
            filteredData = new FilteredList<>(userList, p -> true);

            SortedList<User> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableUsers.comparatorProperty());

            tableUsers.setItems(sortedData);
        });

        loadTask.setOnFailed(e -> {
            UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách người dùng.");
        });

        new Thread(loadTask).start();
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredData != null) {
                filteredData.setPredicate(user -> {
                    if (newValue == null || newValue.trim().isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return user.getUsername().toLowerCase().contains(lowerCaseFilter) ||
                            user.getEmail().toLowerCase().contains(lowerCaseFilter);
                });
            }
        });
    }

    private void toggleUserStatus(User user) {
        if (user.getRole() == Role.ADMIN) {
            UIExceptionHandler.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không thể khóa tài khoản Admin.");
            return;
        }

        boolean isBanning = "ACTIVE".equalsIgnoreCase(user.getStatus());
        String newStatus = isBanning ? "BAN" : "ACTIVE";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin/confirm_action_dialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.initOwner(tableUsers.getScene().getWindow());

            ConfirmActionController controller = loader.getController();
            controller.initData(user, isBanning, () -> {

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
            });

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở giao diện xác nhận.");
        }
    }
}