package org.example.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SceneUtils {

    public static <T> T switchScene(Node sourceNode, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("WOWTruyen - " + title);
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi chuyển cảnh tới: " + fxmlPath);
            return null;
        }
    }

    public static <T> T openNewWindow(String fxmlPath, String title, Node nodeToClose) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("WOWTruyen - " + title);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            if (nodeToClose != null) {
                ((Stage) nodeToClose.getScene().getWindow()).close();
            }
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi mở cửa sổ mới: " + fxmlPath);
            return null;
        }
    }

    public static Stage showLoading(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtils.class.getResource("/view/read/loading_screen.fxml"));
            Parent root = loader.load();

            Stage loadingStage = new Stage();
            loadingStage.initStyle(StageStyle.TRANSPARENT);
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            if (owner != null) {
                loadingStage.initOwner(owner);
            }

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            loadingStage.setScene(scene);

            if (owner != null) {
                double centerX = owner.getX() + (owner.getWidth() / 2);
                double centerY = owner.getY() + (owner.getHeight() / 2);
                loadingStage.setX(centerX - 255);
                loadingStage.setY(centerY - 318);
            }

            loadingStage.show();
            return loadingStage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeLoading(Stage loadingStage) {
        if (loadingStage != null && loadingStage.isShowing()) {
            loadingStage.close();
        }
    }

    public static void switchSceneAsync(Node sourceNode, String fxmlPath, String title) {
        Stage owner = (Stage) sourceNode.getScene().getWindow();

        Stage loadingStage = showLoading(owner);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).thenAccept(v -> {
            Platform.runLater(() -> {
                closeLoading(loadingStage);
                switchScene(sourceNode, fxmlPath, title);
            });
        });
    }
}