package org.example.controllers.favorite;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controllers.read.BookDetailController;
import org.example.dao.FavoriteDAO;
import org.example.model.user.UserFavorite;
import org.example.utils.ImageLoaderGlobal;

import java.io.IOException;

public class FavoriteItemController {

    @FXML private HBox itemContainer;
    @FXML private ImageView bookThumb;
    @FXML private Label bookTitle;
    @FXML private Label statusLabel;
    @FXML private Button btnRemove;

    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private UserFavorite currentFav;

    private final String IMAGE_BASE_URL = "https://img.otruyenapi.com/uploads/comics/";

    public void setData(UserFavorite fav, Runnable onDeleteCallback) {
        this.currentFav = fav;

        bookTitle.setText(fav.getBookName());

        String dateAdded = "";
        if (fav.getAddedAt() != null) {
            dateAdded = fav.getAddedAt().toString();
            if (dateAdded.length() > 10) dateAdded = dateAdded.substring(0, 10);
        }
        if (statusLabel != null) {
            statusLabel.setText("Đã thêm: " + dateAdded);
        }

        String rawThumb = fav.getThumbnailUrl();
        String finalUrl;
        if (rawThumb == null || rawThumb.isEmpty()) {
            finalUrl = "";
        } else if (rawThumb.startsWith("http")) {
            finalUrl = rawThumb;
        } else {
            finalUrl = IMAGE_BASE_URL + rawThumb;
        }
        ImageLoaderGlobal.setImage(finalUrl, bookThumb);

        itemContainer.setOnMouseClicked(e -> openBookDetail());

        if (btnRemove != null) {
            btnRemove.setOnAction(e -> {
                e.consume();
                new Thread(() -> {
                    boolean success = favoriteDAO.removeFavorite(fav.getUserId(), fav.getBookSlug());
                    Platform.runLater(() -> {
                        if (success) {
                            VBox parent = (VBox) itemContainer.getParent();
                            if (parent != null) {
                                parent.getChildren().remove(itemContainer);

                                if (onDeleteCallback != null) onDeleteCallback.run();
                            }
                        }
                    });
                }).start();
            });
        }
    }

    private void openBookDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_detail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookSlug(currentFav.getBookSlug());

            Stage stage = (Stage) itemContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - " + currentFav.getBookName());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}