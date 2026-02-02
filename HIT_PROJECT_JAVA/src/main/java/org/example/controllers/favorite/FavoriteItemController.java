package org.example.controllers.favorite;

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
    @FXML private Button btnRemove;

    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private UserFavorite currentFav;

    public void setData(UserFavorite fav) {
        this.currentFav = fav;

        bookTitle.setText(fav.getBookName());

        String fullUrl = "https://img.otruyenapi.com/uploads/comics/" + fav.getThumbnailUrl();
        ImageLoaderGlobal.setImage(fullUrl, bookThumb);

        itemContainer.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/read/book_detail.fxml"));
                Parent root = loader.load();

                BookDetailController detailController = loader.getController();
                detailController.setBookSlug(fav.getBookSlug());

                Stage stage = (Stage) itemContainer.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        if (btnRemove != null) {
            btnRemove.setOnAction(e -> {
                boolean success = favoriteDAO.removeFavorite(fav.getUserId(), fav.getBookSlug());
                if (success) {
                    VBox parent = (VBox) itemContainer.getParent();
                    if (parent != null) {
                        parent.getChildren().remove(itemContainer);
                    }
                }
            });
        }
    }
}