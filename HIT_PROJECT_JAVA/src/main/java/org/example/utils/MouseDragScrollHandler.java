package org.example.utils;

import javafx.animation.AnimationTimer;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;

public class MouseDragScrollHandler {
    private final ScrollPane scrollPane;
    private final Region content;

    private double lastMouseY;
    private double velocityY = 0;
    private boolean isDragging = false;

    private static final double FRICTION = 0.92;
    private static final double DRAG_MULTIPLIER = 1.5;

    public MouseDragScrollHandler(ScrollPane scrollPane, Region content) {
        this.scrollPane = scrollPane;
        this.content = content;
        setupMouseEvents();
        setupInertiaTimer();
    }

    private void setupMouseEvents() {
        content.setOnMousePressed(event -> {
            isDragging = true;
            lastMouseY = event.getScreenY();
            velocityY = 0;
            scrollPane.getContent().setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });

        content.setOnMouseDragged(event -> {
            double currentMouseY = event.getScreenY();
            double deltaY = (currentMouseY - lastMouseY) * DRAG_MULTIPLIER;

            updateScroll(deltaY);

            velocityY = deltaY;
            lastMouseY = currentMouseY;
        });

        content.setOnMouseReleased(event -> {
            isDragging = false;
            scrollPane.getContent().setCursor(javafx.scene.Cursor.DEFAULT);
        });

        scrollPane.setOnScroll(event -> {
            updateScroll(event.getDeltaY() * 2);
            event.consume();
        });
    }

    private void setupInertiaTimer() {
        AnimationTimer inertiaLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isDragging && Math.abs(velocityY) > 0.1) {
                    updateScroll(velocityY);
                    velocityY *= FRICTION;
                } else if (!isDragging && Math.abs(velocityY) <= 0.1) {
                    velocityY = 0;
                }
            }
        };
        inertiaLoop.start();
    }

    private void updateScroll(double deltaPixels) {
        double contentHeight = content.getBoundsInLocal().getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();

        if (contentHeight <= viewportHeight) return;

        double scrollableHeight = contentHeight - viewportHeight;
        double currentVValue = scrollPane.getVvalue();

        double deltaVValue = -deltaPixels / scrollableHeight;

        double targetVValue = Math.min(1.0, Math.max(0.0, currentVValue + deltaVValue));
        scrollPane.setVvalue(targetVValue);
    }
}