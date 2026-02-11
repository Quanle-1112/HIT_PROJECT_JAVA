package org.example.controllers.favorite;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.constant.MessageConstant;
import org.example.controllers.read.BookDetailController;
import org.example.dao.FavoriteDAO;
import org.example.exception.AppException;
import org.example.exception.UIExceptionHandler;
import org.example.model.user.UserFavorite;
import org.example.utils.ImageLoaderGlobal;

import java.io.IOException;

public class FavoriteItemController {

    @FXML private HBox itemContainer;
    @FXML private ImageView bookThumb;
    @FXML private Label bookTitle;
    @FXML private Label addedDate;
    @FXML private Button btnDelete;

    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private UserFavorite currentFavorite;

    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";

    public void setData(UserFavorite favorite, Runnable onDeleteCallback) {
        this.currentFavorite = favorite;

        bookTitle.setText(favorite.getBookName());

        if (addedDate != null && favorite.getAddedAt() != null) {
            addedDate.setText("Đã thích: " + favorite.getAddedAt().toString().substring(0, 10));
        }

        String imgUrl = favorite.getThumbnailUrl();
        if (imgUrl != null && !imgUrl.startsWith("http")) {
            imgUrl = IMAGE_BASE_URL + imgUrl;
        }
        ImageLoaderGlobal.setImage(imgUrl, bookThumb);

        itemContainer.setOnMouseClicked(e -> openBookDetail());

        if (btnDelete != null) {
            btnDelete.setOnAction(e -> {
                e.consume();
                handleDelete(onDeleteCallback);
            });
        }
    }

    private void handleDelete(Runnable onDeleteCallback) {
        new Thread(() -> {
            try {
                boolean success = favoriteDAO.removeFavorite(currentFavorite.getUserId(), currentFavorite.getBookSlug());

                Platform.runLater(() -> {
                    if (success) {
                        VBox parent = (VBox) itemContainer.getParent();
                        if (parent != null) {
                            parent.getChildren().remove(itemContainer);

                            if (onDeleteCallback != null) onDeleteCallback.run();
                        }
                    } else {
                        UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", MessageConstant.ERR_DB_DELETE);
                    }
                });

            } catch (AppException e) {
                Platform.runLater(() ->
                        UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi Hệ Thống", e.getMessage())
                );
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() ->
                        UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", MessageConstant.ERR_SYSTEM)
                );
            }
        }).start();
    }

    private void openBookDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_detail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookSlug(currentFavorite.getBookSlug());

            Stage stage = (Stage) itemContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - " + currentFavorite.getBookName());
        } catch (IOException e) {
            e.printStackTrace();
            UIExceptionHandler.showAlert(Alert.AlertType.ERROR, "Lỗi", MessageConstant.ERR_SYSTEM);
        }
    }
}