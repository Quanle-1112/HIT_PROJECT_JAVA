package org.example.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UIFactory {

    private static final String[] PASTEL_COLORS = {
            "#AEEEEE", "#FFDAB9", "#E6E6FA", "#FFDEAD", "#98FB98", "#87CEFA", "#FFB6C1", "#D3D3D3"
    };

    public static List<Button> createCategoryButtons(Map<String, String> categories, EventHandler<ActionEvent> handler) {
        List<Button> buttons = new ArrayList<>();
        int colorIndex = 0;

        for (Map.Entry<String, String> entry : categories.entrySet()) {
            String name = entry.getKey();
            String slug = entry.getValue();

            Button btn = new Button(name);
            btn.setUserData(slug);

            btn.setFont(Font.font("System", FontWeight.BOLD, 12));
            btn.setTextFill(Color.web("#333333"));
            btn.setPadding(new Insets(8, 15, 8, 15));
            btn.setPrefHeight(30);

            String colorHex = PASTEL_COLORS[colorIndex % PASTEL_COLORS.length];
            btn.setBackground(new Background(new BackgroundFill(Color.web(colorHex), new CornerRadii(5), Insets.EMPTY)));
            btn.setStyle("-fx-cursor: hand;");

            btn.setOnAction(handler);
            buttons.add(btn);
            colorIndex++;
        }
        return buttons;
    }
}