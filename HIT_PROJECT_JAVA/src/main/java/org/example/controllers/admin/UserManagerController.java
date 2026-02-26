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
import org.example.constant.MessageConstant;
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
                SceneUtils.switchScene(btnBack, "/view/admin/admin_dashboard.fxml", MessageConstant.TITLE_ADMIN)
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
                                btnAction.setText(MessageConstant.NO_LOCK);
                                btnAction.setStyle(MessageConstant.COLOR_7);
                            } else {
                                btnAction.setText(MessageConstant.LOCK);
                                btnAction.setStyle(MessageConstant.COLOR_8);
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
            return;
        }

        boolean isBanning = "ACTIVE".equalsIgnoreCase(user.getStatus());
        String newStatus = isBanning ? "BANNED" : "ACTIVE";

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

                    String msg = MessageConstant.UPDATE_COMPLETE;
                    if (isBanning && blacklistUpdated) msg += MessageConstant.ADD_EMAIL_IN_BLACK_LIST;
                    if (!isBanning && blacklistUpdated) msg += MessageConstant.REMOVE_EMAIL_IN_BLACK_LIST;

                    UIExceptionHandler.showAlert(Alert.AlertType.INFORMATION, MessageConstant.SUCCESS, msg);
                } else {
                    UIExceptionHandler.showAlert(Alert.AlertType.ERROR, MessageConstant.ERROR, MessageConstant.OPERATION_FAILED);
                }
            });

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}