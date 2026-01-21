package org.example.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

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
}