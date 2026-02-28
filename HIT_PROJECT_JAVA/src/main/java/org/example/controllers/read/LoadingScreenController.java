package org.example.controllers.read;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;

public class LoadingScreenController {

    @FXML private ProgressBar progressBar;

    @FXML
    public void initialize() {
        if (progressBar != null) {
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        }
    }
}