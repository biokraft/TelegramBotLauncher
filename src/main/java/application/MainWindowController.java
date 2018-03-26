package application;

import bot.BotController;
import bot.FileManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class MainWindowController {

    public Main main;

    @FXML private TextField bot_name, bot_token;
    @FXML private ProgressBar progressBar;
    @FXML public CheckBox checkSpam, greeting, spamOption, feedback;
    @FXML private Text updateWarning;
    @FXML private Tab configTab, updateTab;
    @FXML private ProgressIndicator progressCircle;
    @FXML public TextArea greetingText;

    public void setMain(Main main) {
        this.main = main;
    }

    private void updateBotData (String BOT_NAME, String BOT_TOKEN) {
        bot.FileManager botData = new FileManager("bot");
        botData.removeLineIfCointains("BT");
        botData.removeLineIfCointains("BN");
        botData.writeLine("BT: " + BOT_TOKEN);
        botData.writeLine("BN: " + BOT_NAME);
    }

    public void loadBotData (String BOT_NAME, String BOT_TOKEN) {
        bot_name.setPromptText(BOT_NAME);
        bot_token.setPromptText(BOT_TOKEN);
    }

    public void handleHyperlink () {
        try {
            Desktop.getDesktop().browse(new URL("http://copperlabs.de/").toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleStart () {
        System.out.println("START WAS PRESSED");

        // Start the bot
        BotController.main();

        if (BotController.isRunning()) {
            // Reset the warning text if it has occured
            updateWarning.setVisible(false);

            // Set progress bar to -1 aka. running and stealth Progress Circle if it has been shown
            progressCircle.setVisible(false);
            progressBar.setVisible(true);
            progressBar.setProgress(-1);

            // Unlock config tab and lock update tab
            configTab.setDisable(false);
            updateTab.setDisable(true);

            // Load settings from config file to Bot
            FileManager config = new FileManager("config");
            ArrayList<String> configData = config.getLines(false);
            BotController.setCheckSpam(configData.get(0).substring(15).equals("true"));

            BotController.setFeedback(configData.get(1).substring(15).equals("true"));

            BotController.setSpamOption(configData.get(2).substring(15).equals("true"));

            BotController.setGreeting(configData.get(3).substring(15).equals("true"));


            // Set greeting prompt text
            BotController.setGreetingText(configData.get(4).substring(15));
        } else {
            // Lock config tab and unlock update tab
            configTab.setDisable(true);
            updateTab.setDisable(false);

            // Replace progressbar with progress circle
            progressBar.setVisible(false);
            progressCircle.setVisible(true);

            // View warning message
            updateWarning.setVisible(true);
        }
    }

    public void handleStop () {
        System.out.println("STOP WAS PRESSED");
        System.exit(420);
    }

    public void handleSpamCheck() {
        if (checkSpam.isSelected()) {
            System.out.println("SPAM CHECK SELECTED");
            BotController.setCheckSpam(true);
            saveSetting(1, true);
        } else {
            BotController.setCheckSpam(false);
            saveSetting(1, false);
        }
    }

    public void handleFeedback () {
        if (feedback.isSelected()) {
            System.out.println("FEEDBACK CHECK SELECTED");
            BotController.setFeedback(true);
            saveSetting(2, true);
        } else {
            BotController.setFeedback(false);
            saveSetting(2, false);
        }
    }

    public void handleSpamOption () {
        if (spamOption.isSelected()) {
            System.out.println("SPAM OPTION SELECTED");
            BotController.setSpamOption(true);
            saveSetting(3, true);
        } else {
            BotController.setSpamOption(false);
            saveSetting(3, false);
        }
    }

    public void handleGreeting () {
        if (greeting.isSelected()) {
            System.out.println("GREETINGS SELECTED");
            BotController.setGreeting(true);
            saveSetting(4, true);
        } else {
            BotController.setGreeting(false);
            saveSetting(4, false);
        }
    }

    public void handleGreetingText () {
        if (!greetingText.getText().equals("")) {
            BotController.setGreetingText(greetingText.getText());

            // Save the newly set text
            new FileManager("config").replaceLine(5, "Greeting text: " + greetingText.getText());
        }
    }

    public void handleUpdate() {
        System.out.println("UPDATE WAS PRESSED");

        // Update Bot's internal data and also load it
        updateBotData(bot_name.getText(), bot_token.getText());
        loadBotData(bot_name.getText(), bot_token.getText());

        // Reset Control-Tab
        progressCircle.setVisible(false);
        updateWarning.setVisible(false);
        progressBar.setVisible(true);
        progressBar.setProgress(0);
    }

    private void saveSetting (int line, boolean setting) {
        FileManager config = new FileManager("config");
        ArrayList<String> data = config.getLines();
        if (data.size() > 1) {
            String text = data.get(line-1).substring(0,15);
            if (setting) {
                config.replaceLine(line, text + "true");
            } else {
                config.replaceLine(line, text + "false");
            }
        }
    }
}
