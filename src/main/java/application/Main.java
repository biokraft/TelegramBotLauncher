package application;

import bot.FileManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private Stage primaryStage;
    private MainWindowController mainWindowController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainWindow();
        loadData();
    }

    public void mainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/MainWindow.fxml"));
            AnchorPane pane = loader.load();

            primaryStage.setMinHeight(300);
            primaryStage.setMinWidth(500);
            primaryStage.setTitle("TBL 0.2.0");
            primaryStage.getIcons().add(new Image("bot_logo_long.png"));
            primaryStage.setAlwaysOnTop(true);

            // If the program is closed shutdown all threads
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
              @Override
              public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
                }
            });

            mainWindowController = loader.getController();

            primaryStage.setScene(new Scene(pane));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void loadData () {
        FileManager botData = new FileManager("bot");
        ArrayList<String> data = botData.getLines(false);
        if (data.size() > 1) {
            mainWindowController.loadBotData(data.get(1).substring(4), data.get(0).substring(4));
        }

        FileManager config = new FileManager("config");
        ArrayList<String> configData = config.getLines(false);
        if (configData.size() > 1) {
            // Load saved parameters into the GUI
            if (configData.get(0).substring(15).equals("true")) mainWindowController.checkSpam.setSelected(true);

            if (configData.get(1).substring(15).equals("true")) mainWindowController.feedback.setSelected(true);

            if (configData.get(2).substring(15).equals("true")) mainWindowController.spamOption.setSelected(true);

            if (configData.get(3).substring(15).equals("true")) mainWindowController.greeting.setSelected(true);

            if (configData.get(5).substring(15).equals("true")) mainWindowController.forwardTweets.setSelected(true);


            // Set greeting text if it has been changed
            mainWindowController.greetingText.setPromptText(configData.get(4).substring(15));
            mainWindowController.twitterAccount.setPromptText(configData.get(6).substring(15));

        } else { // If this is the first time the bot launches set the parameters to default values
            config.writeLine("Anti-URL-Spam: false\n" +
                    "Give feedback: false\n" +
                    "NonMember del: false\n" +
                    "Greet new mem: false\n" +
                    "Greeting text: Welcome to our group chat!\n" +
                    "Forward tweet: false\n" +
                    "Twitter  name: LeTrumperino");
        }
    }
}
