package org.example.controllers.chatbox;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.example.constant.MessageConstant;
import org.example.exception.AppException;
import org.example.services.IChatBoxService;
import org.example.services.impl.IChatBoxServiceImpl;
import org.example.utils.SceneUtils;

public class ChatBoxController {


    @FXML private ScrollPane scrollPane;
    @FXML private VBox messageContainer;

    @FXML private TextField inputField;
    @FXML private Button btnSend;

    @FXML private Button btnHome;
    @FXML private Button btnHistory;
    @FXML private Button btnFavorite;
    @FXML private Button btnAI;
    @FXML private Button btnAccount;

    private final IChatBoxService chatService = new IChatBoxServiceImpl();

    @FXML
    public void initialize() {
        setupNavigation();
        setupChatInterface();

        addMessage(MessageConstant.CHAT_AI_TITLE, "Xin chào! Tôi có thể giúp gì cho bạn hôm nay?", false);
    }

    private void setupChatInterface() {
        if (messageContainer != null) {
            messageContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
                if (scrollPane != null) {
                    scrollPane.setVvalue(1.0);
                }
            });
        }

        if (btnSend != null) {
            btnSend.setOnAction(e -> handleSendMessage());
        }

        if (inputField != null) {
            inputField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    handleSendMessage();
                }
            });
        }
    }

    private void handleSendMessage() {
        String question = inputField.getText().trim();

        if (question.isEmpty()) {
            return;
        }

        addMessage(MessageConstant.CHAT_USER_TITLE, question, true);
        inputField.clear();

        setInputDisable(true);

        Task<String> chatTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return chatService.askGemini(question);
            }
        };

        chatTask.setOnSucceeded(e -> {
            String response = chatTask.getValue();
            addMessage(MessageConstant.CHAT_AI_TITLE, response, false);
            setInputDisable(false);
            Platform.runLater(() -> inputField.requestFocus());
        });

        chatTask.setOnFailed(e -> {
            Throwable ex = chatTask.getException();
            String errorMsg = (ex instanceof AppException) ? ex.getMessage() : MessageConstant.ERR_NETWORK;

            addSystemMessage("Lỗi: " + errorMsg);

            setInputDisable(false);
            ex.printStackTrace();
        });

        new Thread(chatTask).start();
    }

    private void setInputDisable(boolean disable) {
        if (btnSend != null) btnSend.setDisable(disable);
        if (inputField != null) inputField.setDisable(disable);
    }

    private void addMessage(String sender, String message, boolean isUser) {
        if (messageContainer == null) return;

        VBox messageBubble = new VBox(5);
        messageBubble.setMaxWidth(400);
        messageBubble.setPadding(new Insets(10));

        String style = isUser
                ? "-fx-background-color: #19345D; -fx-background-radius: 15 15 0 15;"
                : "-fx-background-color: #FFFFFF; -fx-background-radius: 15 15 15 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);";
        messageBubble.setStyle(style);

        Label lblSender = new Label(sender);
        lblSender.setFont(Font.font("System", FontWeight.BOLD, 12));
        lblSender.setTextFill(isUser ? javafx.scene.paint.Color.WHITE : javafx.scene.paint.Color.web("#19345D"));

        Text text = new Text(message);
        text.setFill(isUser ? javafx.scene.paint.Color.WHITE : javafx.scene.paint.Color.BLACK);
        text.setFont(Font.font("System", 13));

        TextFlow textFlow = new TextFlow(text);

        messageBubble.getChildren().addAll(lblSender, textFlow);

        HBox container = new HBox();
        container.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));
        container.getChildren().add(messageBubble);

        Platform.runLater(() -> messageContainer.getChildren().add(container));
    }

    private void addSystemMessage(String message) {
        if (messageContainer == null) return;

        Label lblError = new Label(message);
        lblError.setTextFill(javafx.scene.paint.Color.RED);
        lblError.setFont(Font.font("System", FontWeight.NORMAL, 11));

        HBox container = new HBox(lblError);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(5));

        Platform.runLater(() -> messageContainer.getChildren().add(container));
    }

    private void setupNavigation() {
        if (btnAI != null) {
            btnAI.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 10; -fx-text-fill: #19345D; -fx-font-weight: bold;");
            btnAI.setDisable(true);
        }

        if (btnHome != null) btnHome.setOnAction(e -> SceneUtils.switchScene(btnHome, "/view/read/home_screen.fxml", MessageConstant.TITLE_HOME));
        if (btnHistory != null) btnHistory.setOnAction(e -> SceneUtils.switchScene(btnHistory, "/view/history/history_screen.fxml", "Lịch sử"));
        if (btnFavorite != null) btnFavorite.setOnAction(e -> SceneUtils.switchScene(btnFavorite, "/view/favorite/favorite_screen.fxml", "Yêu thích"));
        if (btnAccount != null) btnAccount.setOnAction(e -> SceneUtils.switchScene(btnAccount, "/view/account/account_screen.fxml", "Tài khoản"));
    }
}