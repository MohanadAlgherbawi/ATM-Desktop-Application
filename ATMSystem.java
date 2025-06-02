package com.example.javabuilder;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ATMSystem extends Application {

    private FileHandler fileHandler;
    private Stage window;
    private ArrayList<Scene> scenes = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize file handler and window
        fileHandler = new FileHandler();
        window = primaryStage;

        // Create and configure the login page layout (GridPane)
        GridPane grid = createLoginPage();

        // Add elements to layout
        configureLoginPage(grid);

        // Set up the scene and show it
        Scene loginScene = new Scene(grid, 600, 400);
        scenes.add(loginScene);
        primaryStage.setScene(scenes.get(0));
        primaryStage.setTitle("ATM – Log in");
        primaryStage.show();
    }

    private GridPane createLoginPage() {
        // Create GridPane layout for login page
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));
        return grid;
    }

    private void configureLoginPage(GridPane grid) {
        // Labels and text fields for login form
        Label userIdLabel = new Label("AccountId:");
        TextField userIdField = new TextField();
        userIdField.setPromptText("Account ID:");
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        // Hyperlinks and buttons
        Hyperlink forgotPassword = createHyperlink("Forgot Password?");
        Button loginButton = createButton("Log in");
        loginButton.setPrefSize(150, 25);
        Hyperlink createAccount = createHyperlink("Don't have an account? Sign up");

        // Layout elements added to the grid
        grid.add(userIdLabel, 0, 0);
        grid.add(userIdField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(forgotPassword, 1, 2);
        grid.add(loginButton, 1, 3);
        grid.add(createAccount, 1, 4);

        // Buttons and hyperlink actions
        AtomicInteger failedAttempts = new AtomicInteger();
        loginButton.setOnAction(e -> handleLogin(userIdField, passwordField, failedAttempts));

        forgotPassword.setOnAction(e -> window.setScene(forgotPage()));
        createAccount.setOnMouseClicked(e -> createNewAccount());

        // Align buttons and labels
        GridPane.setMargin(loginButton, new Insets(10, 0, 0, 0));
        GridPane.setMargin(createAccount, new Insets(10, 0, 0, 0));
    }

    private void handleLogin(TextField userIdField, PasswordField passwordField, AtomicInteger failedAttempts) {
        try {
            int userId = Integer.parseInt(userIdField.getText());
            String password = passwordField.getText();

            if (loginValid(userId, password)) {
                showSuccessAlert("Login Successful!");
                window.setScene(getHomeScene(userId, password));
                window.show();
            } else {
                if(fileHandler.validateIUserAccount(userId)){
                    if (fileHandler.findUserAccount(userId).isLocked()) showError("Account Locked!");
                     else if (failedAttempts.get() >= 3) {
                        fileHandler.lockUserAccount(userId);
                        showError("Account locked due to multiple failed attempts");
                    }
                     else if (failedAttempts.get() < 3 ) {
                            failedAttempts.getAndIncrement();
                            showError("Login failed. Attempts left: " + (3 - failedAttempts.get()));
                    }

                }else {showError("Account Not Found!");}

            }
        } catch (Exception ex) {
            showError("Please fill in all fields");
        }
    }


    private boolean loginValid(int userId, String password) {
        return fileHandler.validateUserAccount(userId, password);
    }

    private Scene forgotPage() {
        // Create layout for forgot password page
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Create input fields and labels for forgot password page
        TextField userIdField = new TextField();
        TextField balanceField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        grid.add(new Label("AccountId:"), 0, 0);
        grid.add(new Label("Account Balance:"), 0, 1);
        grid.add(new Label("New Password:"), 0, 2);
        grid.add(new Label("Confirm Password:"), 0, 3);

        Button changeButton = createButton("Change Password");
        Button backButton = createButton("Back");

        // Set action for "Back" button
        backButton.setOnAction(e -> window.setScene(scenes.get(0)));

        // Button on action for change password
        changeButton.setOnAction(e -> handlePasswordChange(userIdField, balanceField, passwordField, confirmPasswordField));

        // Add elements to the grid
        grid.add(userIdField, 1, 0);
        grid.add(balanceField, 1, 1);
        grid.add(passwordField, 1, 2);
        grid.add(confirmPasswordField, 1, 3);
        grid.add(changeButton, 1, 4);
        grid.add(backButton, 1, 5);

        return new Scene(grid, 600, 400);
    }

    private void handlePasswordChange(TextField userIdField, TextField balanceField, PasswordField passwordField, PasswordField confirmPasswordField) {
        try {
            int userId = Integer.parseInt(userIdField.getText());
            double balance = Double.parseDouble(balanceField.getText());

            if (fileHandler.validateFUserAccount(userId, balance)) {
                if (passwordField.getText().equals(confirmPasswordField.getText()) && passwordField.getText().length() >= 8) {
                    fileHandler.findUserAccount(userId).setAccountPassword(passwordField.getText());
                    fileHandler.updateUserAccount(fileHandler.findUserAccount(userId));
                    passwordChanged();
                }
            } else {
                showError("Error found. Try again!");
            }
        } catch (Exception ex) {
            showError("Error found. Try again!");
        }
    }

    public void createNewAccount() {
        // Create layout for account creation page
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25));

        // Create input fields and labels for new account
        TextField fullNameField = new TextField();
        TextField phoneNumberField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        TextField addBalanceField = new TextField();

        gridPane.add(new Label("Full Name: "), 0, 0);
        gridPane.add(new Label("Phone Number: "), 0, 1);
        gridPane.add(new Label("Password: "), 0, 2);
        gridPane.add(new Label("Confirm Password: "), 0, 3);
        gridPane.add(new Label("Add Balance: "), 0, 4);

        Button saveButton = createButton("Save");
        Button backButton = createButton("Back");

        // Set action for "Back" button
        backButton.setOnAction(e -> window.setScene(scenes.get(0)));

        // Set action for "Save" button
        saveButton.setOnAction(e -> handleAccountSave(fullNameField, phoneNumberField, passwordField, confirmPasswordField, addBalanceField));

        // Add elements to grid
        gridPane.add(fullNameField, 1, 0);
        gridPane.add(phoneNumberField, 1, 1);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(confirmPasswordField, 1, 3);
        gridPane.add(addBalanceField, 1, 4);
        gridPane.add(saveButton, 1, 5);
        gridPane.add(backButton, 1, 6);

        // Set new scene for account creation
        Scene newAccountScene = new Scene(gridPane, 600, 400);
        scenes.add(newAccountScene);
        window.setScene(newAccountScene);
        window.show();
    }

    private void handleAccountSave(TextField fullNameField, TextField phoneNumberField, PasswordField passwordField, PasswordField confirmPasswordField, TextField addBalanceField) {
        if (isValidNewAccountInput(fullNameField, phoneNumberField, passwordField, confirmPasswordField, addBalanceField)) {
            UserAccount userAccount = new UserAccount(fullNameField.getText(), Integer.parseInt(phoneNumberField.getText()), passwordField.getText(), Double.parseDouble(addBalanceField.getText()));
            try {
                fileHandler.addUserAccount(userAccount);
                inform(userAccount.getAccountId());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            showError("Please fill all fields correctly.");
        }
    }

    private boolean isValidNewAccountInput(TextField fullNameField, TextField phoneNumberField, PasswordField passwordField, PasswordField confirmPasswordField, TextField addBalanceField) {
        return !fullNameField.getText().isEmpty() && !phoneNumberField.getText().isEmpty() && !passwordField.getText().isEmpty()
                && !confirmPasswordField.getText().isEmpty() && !addBalanceField.getText().isEmpty()
                && passwordField.getText().equals(confirmPasswordField.getText()) && passwordField.getText().length() >= 8;
    }
    public Scene getHomeScene(int userId , String userPassword){
        VBox home = new VBox();
        home.setSpacing(20);
        home.setPadding(new Insets(20));
        home.setAlignment(Pos.CENTER);
        // Add header
        Label headerLabel = new Label("Welcome "+fileHandler.findUserAccount(userId).getAccountUserFullName());
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        home.getChildren().add(headerLabel);

        // Create grid for the buttons
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(20);
        buttonGrid.setVgap(20);
        buttonGrid.setAlignment(Pos.CENTER);

        // Add buttons
        Button depositWithdrawButton = createButton("Deposit/Withdraw");
        depositWithdrawButton.setPrefSize(150,50);
        Button balanceButton = createButton("Balance");
        Button historyButton = createButton("History");
        buttonGrid.add(depositWithdrawButton, 0, 0);
        buttonGrid.add(balanceButton, 0, 1);
        buttonGrid.add(historyButton, 1, 1);
        home.getChildren().add(buttonGrid);

        //handleButtons
        depositWithdrawButton.setOnAction(e -> {
            showDepWidPop(userId);
        });
        balanceButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Balance ");
            alert.setHeaderText(null);
            alert.setContentText("Your Current Balance is: "+ fileHandler.findUserAccount(userId).getAccountBalance());
            alert.showAndWait();
        });
        historyButton.setOnAction(e -> {
            historyShow(userId,userPassword);
        });


        // Add footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));
        Label footerLabel = new Label("Made by Muhannad Elgherbawi Palestine");
        footerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        footer.getChildren().add(footerLabel);
        home.getChildren().add(footer);

        // Add logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setAlignment(Pos.TOP_RIGHT);
        logoutButton.setOnAction(e -> {window.setScene(scenes.get(0));});
        home.getChildren().add(logoutButton);


        Scene scene = new Scene(home, 600, 400);
        return scene;
    }
    public void showDepWidPop(int userId){
        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(10));

        // Transaction Type Choice
        ToggleGroup transactionTypeGroup = new ToggleGroup();
        RadioButton depositButton = new RadioButton("Deposit");
        RadioButton withdrawButton = new RadioButton("Withdraw");
        depositButton.setToggleGroup(transactionTypeGroup);
        withdrawButton.setToggleGroup(transactionTypeGroup);

        // Input field for the value
        Label valueLabel = new Label("Enter the amount:");
        TextField valueField = new TextField();
        valueField.setPromptText("Amount");

        // Confirm button
        Button confirmButton = new Button("Confirm");
        Button back = new Button("Back");
        back.setOnAction(e -> window.setScene(getHomeScene(userId, fileHandler.findUserAccount(userId).getAccountPassword())));
        confirmButton.setOnAction(e -> {
            RadioButton selectedButton = (RadioButton) transactionTypeGroup.getSelectedToggle();
            String inputValue = valueField.getText();

            if (selectedButton == null || inputValue.isEmpty()) {
                showError("Please select a transaction type and enter an amount.");
                return;
            }

            try {
                double amount = Double.parseDouble(inputValue);
                String transactionType = selectedButton.getText();
                if (transactionType.equals("Deposit")) {
                    try {
                        fileHandler.findUserAccount(userId).deposit(amount);
                        fileHandler.updateUserAccount(fileHandler.findUserAccount(userId));
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
                else try {
                    fileHandler.findUserAccount(userId).withdraw(amount);
                    fileHandler.updateUserAccount(fileHandler.findUserAccount(userId));
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Transaction Successful");
                alert.setHeaderText(null);
                alert.setContentText("You selected: " + transactionType + "\nAmount: " + amount+" Your Balance is "+ fileHandler.findUserAccount(userId).getAccountBalance());
                alert.showAndWait();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid numeric amount.");
                alert.showAndWait();
            }
        });

        dialogLayout.getChildren().addAll(depositButton, withdrawButton, valueLabel, valueField, confirmButton, back);

        Scene dialogScene = new Scene(dialogLayout, 600, 400);
        window.setScene(dialogScene);
        window.show();
    }
    public void historyShow(int userId,String userPassword){
        ListView<String> listView = new ListView<>();
        Button back = new Button("Back");
        back.setOnAction(e -> window.setScene(getHomeScene(userId, fileHandler.findUserAccount(userId).getAccountPassword())));
        listView.getItems().addAll(fileHandler.getDepositAccountHistory(userId));
        listView.getItems().addAll(fileHandler.getWithdrawAccountHistory(userId));
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(listView,back);
        Scene scene = new Scene(vBox, 600, 400);
        window.setScene(scene);
        window.show();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void passwordChanged() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Password changed successfully. Please login again.");
        alert.showAndWait();
        window.setScene(scenes.get(0));
        window.show();
    }

    private void inform(int accountId) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Account created successfully. Your account ID is: " + accountId);
        alert.showAndWait();
        window.setScene(scenes.get(0));
        window.show();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(100, 50);
        button.setStyle("-fx-font-size: 14px; -fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        return button;
    }

    private Hyperlink createHyperlink(String text) {
        Hyperlink hyperlink = new Hyperlink(text);
        hyperlink.setTextFill(Color.BLUE);
        hyperlink.setStyle("-fx-padding: 20;-fx-alignment: center;");
        return hyperlink;
    }
}
