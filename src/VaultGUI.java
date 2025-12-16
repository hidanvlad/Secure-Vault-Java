import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class VaultGUI extends Application {

    private Stage window;
    private String masterKey = null;
    private ListView<String> accountList;
    private Label lblStatus;
    private static final String FILE_NAME = "vault.dat";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        window = stage;
        window.setTitle("SecureVault 🔒");

        // Start with the Login Screen
        window.setScene(createLoginScene());
        window.show();
    }

    // --- SCENE 1: LOGIN ---
    private Scene createLoginScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));

        Label title = new Label("SECURE VAULT");
        title.getStyleClass().add("header-label");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter Master Password");
        passField.setMaxWidth(300);

        Button btnLogin = new Button("UNLOCK");
        btnLogin.setPrefWidth(300);

        lblStatus = new Label("");
        lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        btnLogin.setOnAction(e -> attemptLogin(passField.getText()));
        passField.setOnAction(e -> attemptLogin(passField.getText()));

        layout.getChildren().addAll(title, passField, btnLogin, lblStatus);

        Scene scene = new Scene(layout, 450, 350);
        addStyleSheet(scene);
        return scene;
    }

    private void attemptLogin(String key) {
        if (key.isEmpty()) {
            lblStatus.setText("Password cannot be empty.");
            return;
        }
        this.masterKey = key;
        window.setScene(createVaultScene());
        loadEntries();
    }

    // --- SCENE 2: THE VAULT ---
    private Scene createVaultScene() {
        BorderPane layout = new BorderPane();

        // 1. LEFT SIDE: List of Accounts
        accountList = new ListView<>();
        // NEW LOGIC: CLICK -> COPY TO CLIPBOARD
        accountList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String decrypted = getDecryptedPassword(newVal);
                if (decrypted != null) {
                    copyToClipboard(decrypted);
                } else {
                    showAlert("Error", "Decryption failed. Check master key.");
                }
            }
        });

        Label lblListHeader = new Label("ACCOUNTS");
        lblListHeader.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        VBox leftMenu = new VBox(10, lblListHeader, accountList);
        leftMenu.setPadding(new Insets(20));
        leftMenu.setStyle("-fx-background-color: #34495e;");
        leftMenu.setPrefWidth(250);
        VBox.setVgrow(accountList, Priority.ALWAYS);

        // 2. CENTER: Add New Password
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(40));
        centerContent.setAlignment(Pos.TOP_LEFT);

        Label lblHeader = new Label("Add New Secret");
        lblHeader.getStyleClass().add("sub-header");

        TextField txtAccount = new TextField();
        txtAccount.setPromptText("Account Name (e.g. Netflix)");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Password");

        Button btnSave = new Button("ENCRYPT & SAVE");
        btnSave.setPrefWidth(200);

        Separator sep = new Separator();
        sep.setPadding(new Insets(20, 0, 10, 0));

        // Use this label to show clipboard status
        lblStatus = new Label("Select an account to copy password.");
        lblStatus.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");

        btnSave.setOnAction(e -> {
            saveEntry(txtAccount.getText(), txtPass.getText());
            txtAccount.clear();
            txtPass.clear();
        });

        centerContent.getChildren().addAll(lblHeader, txtAccount, txtPass, btnSave, sep, lblStatus);

        layout.setLeft(leftMenu);
        layout.setCenter(centerContent);

        Scene scene = new Scene(layout, 700, 500);
        addStyleSheet(scene);
        return scene;
    }

    // --- SPY FEATURE: CLIPBOARD & TIMER ---
    private void copyToClipboard(String text) {
        // 1. Copy
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);

        // 2. Notify
        lblStatus.setText("COPIED! Clearing in 10s...");
        lblStatus.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 14px;");

        // 3. Self-Destruct Timer
        new Thread(() -> {
            try {
                Thread.sleep(10000); // 10 Seconds
                Platform.runLater(() -> {
                    clipboard.setContent(null); // Wipe
                    lblStatus.setText("Clipboard Cleared.");
                    lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic;");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // --- HELPER: Decrypt Logic ---
    private String getDecryptedPassword(String accountName) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            for (String line : lines) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2 && parts[0].equals(accountName)) {
                    return CryptoUtils.decrypt(parts[1], masterKey);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    // --- OTHER HELPERS ---
    private void addStyleSheet(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("vault.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS not found!");
        }
    }

    private void saveEntry(String account, String plainPass) {
        if (account.isEmpty() || plainPass.isEmpty()) return;
        String encryptedPass = CryptoUtils.encrypt(plainPass, masterKey);
        String line = account + ":" + encryptedPass + "\n";
        try {
            Files.write(Paths.get(FILE_NAME), line.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            accountList.getItems().add(account);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadEntries() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            for (String line : lines) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) accountList.getItems().add(parts[0]);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("vault.css").toExternalForm());
        alert.showAndWait();
    }
}