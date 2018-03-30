package bot;

import application.TwitterController;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Bot extends TelegramLongPollingBot {
    public String BOT_USERNAME, BOT_TOKEN, greeting = "Welcome to our group chat!", account = "LeTrumperino",
            ChatID = "";

    private FileManager newUserData, botData, commandData;
    private ArrayList<Integer> userList;

    private ArrayList<Command> commands = new ArrayList<Command>();

    private final long startTime = System.nanoTime(), totalTime;
    public boolean spamCheck = false, spamCheckOption = false, greetings = false, giveFeedback = false,
            forwardTwitter = false;

    public Bot() {
        botData = new FileManager("bot");
        loadBotData();

        newUserData = new FileManager("newUser", true);
        loadUserData();

        commandData = new FileManager("command");
        loadCommands();

        // Updates newUserData in a fixed intervall
        refreshUserDataAtFixedRate(7, 1);

        // Shows elapsed time during Bot init
        totalTime = System.nanoTime() - startTime;
        System.out.println("The Bot init took: " + totalTime / 1000000 + "ms");
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) ChatID = update.getMessage().getChatId().toString();

        if (spamCheck) checkForURLspam(update, giveFeedback);

        respondToCommands(update);

        if (forwardTwitter) checkTweetRequest(update);

        if (greetings) greetNewMembers(update, greeting);


        updateUserList(update);

        // Just handy for development. Sends the current ChatID you were issuing the command from
        if (update.hasMessage() && update.getMessage().isCommand() && update.getMessage().getText().equals("/getchatid")) {
            sendText(update.getMessage().getChatId().toString(), update.getMessage().getChatId());
        }

        /*TODO - timed broadcasts (markdown-mode)
         * TODO - implement commandData & cooldown
         * TODO - implement threads for faster bot computation
         * TODO - Bot an einzelne ChatID koppeln*/
    }

    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    public Integer sendText(String text, long ChatID) {
        SendMessage message = new SendMessage()
                .enableMarkdown(true)
                .setText(text)
                .setChatId(ChatID);
        Message feedback = executeMessage(message);
        return feedback.getMessageId();
    }

    public Message executeMessage(SendMessage message) {
        try {
            return execute(message);
        } catch (TelegramApiException e) {
            System.out.println("TELEGRAM-API-EXCEPTION --- message could not be sent");
        }
        return null;
    }

    public boolean executeMessage(DeleteMessage deleteMessage) {
        try {
            return execute(deleteMessage);
        } catch (TelegramApiException e) {
            System.out.println("TELEGRAM-API-EXCEPTION --- message could not be deleted");
        }
        return false;
    }

    private void checkTweetRequest(Update update) {
        if (update.hasMessage() && update.getMessage().isCommand() && update.getMessage().getText().equals("/tweet")) {
            DeleteMessage deleteMessage = new DeleteMessage()
                    .setMessageId(update.getMessage().getMessageId())
                    .setChatId(update.getMessage().getChatId().toString());
            executeMessage(deleteMessage);

            BotController.sendLatestTweet(update.getMessage().getChatId().toString(), account);
        }
    }

    private void respondToCommands(Update update) {
        if (update.hasMessage() && update.getMessage().isCommand()) {
            for (int i = 0; i < commands.size(); i++) {
                if (update.getMessage().getText().equals("/" + commands.get(i).getTrigger()) ||
                        update.getMessage().getText().contains("/" + commands.get(i).getTrigger() + "@")) {
                    sendText(commands.get(i).getResponse(), update.getMessage().getChatId());
                }
            }
        }
    }

    public void checkForURLspam (Update update,boolean giveFeedback){
        if (update.hasMessage() && update.getMessage().hasEntities()) {
            boolean delete = false;
            for (int i = 0; i < update.getMessage().getEntities().size(); i++) {
                if (update.getMessage().getEntities().get(i).getType().equals("url")) {
                    delete = true;
                }
            }
            if (delete) {
                if (spamCheckOption) {
                    for (int i = userList.size() - 1; i >= 0; i--) {
                        if (update.getMessage().getFrom().getId().equals(userList.get(i))) {
                            DeleteMessage deleteMessage = new DeleteMessage()
                                    .setMessageId(update.getMessage().getMessageId())
                                    .setChatId(update.getMessage().getChatId().toString());
                            executeMessage(deleteMessage);
                            System.out.println(
                                    ">>> " + update.getMessage().getFrom().getFirstName() + "'s message " +
                                            "was deleted due to url detection");
                            if (giveFeedback) spamFeedback(update.getMessage().getChatId());
                            break;
                        }
                    }
                } else {
                    DeleteMessage deleteMessage = new DeleteMessage()
                            .setMessageId(update.getMessage().getMessageId())
                            .setChatId(update.getMessage().getChatId().toString());
                    executeMessage(deleteMessage);
                    System.out.println(
                            ">>> " + update.getMessage().getFrom().getFirstName() + "'s message " +
                                    "was deleted due to url detection");
                    if (giveFeedback) spamFeedback(update.getMessage().getChatId());
                }
            }
        }

    }

    public void updateUserList (Update update){
        MyDate myDate = new MyDate();
        if (update.getMessage().getNewChatMembers() != null) {
            for (int i = 0; i < update.getMessage().getNewChatMembers().size(); i++) {
                userList.add(update.getMessage().getNewChatMembers().get(i).getId());
                System.out.println(">>> " + update.getMessage().getNewChatMembers().get(i).getFirstName()
                        + " (" + update.getMessage().getNewChatMembers().get(i).getId() + ")"
                        + " just joined " + update.getMessage().getChat().getTitle());
                newUserData.writeLine("[" + update.getMessage().getNewChatMembers().get(i).getId() + ", "
                        + update.getMessage().getNewChatMembers().get(i).getFirstName() + ", "
                        + myDate.getCurrentDate() + "]");
            }
        }

        if (update.getMessage().getLeftChatMember() != null) {
            if (newUserData.removeLineIfCointains(update.getMessage().getLeftChatMember().getId().toString())) {
                System.out.println(">>> " + update.getMessage().getLeftChatMember().getFirstName() + "("
                        + update.getMessage().getLeftChatMember().getId() + ") has left the chat and thus has been "
                        + "deleted from the database");
            }
            for (int i = userList.size() - 1; i >= 0; i--) {
                if (userList.get(i).equals(update.getMessage().getLeftChatMember().getId())) {
                    userList.remove(i);
                    break;
                }
            }
        }
    }

    public void greetNewMembers (Update update, String greeting){
        if (update.getMessage().getNewChatMembers() != null) {
            if (update.getMessage().getNewChatMembers().size() == 1) {
                SendMessage message = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText("*" + update.getMessage().getNewChatMembers().get(0).getFirstName() + "* " + greeting);

                // Save message object for deleting it afterwards
                Message message1 = executeMessage(message);

                // set required ChatID
                String CurrentChatID = update.getMessage().getChatId().toString();

                // Delete greeting after 10 sec
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        DeleteMessage deleteMessage = new DeleteMessage()
                                .setChatId(CurrentChatID)
                                .setMessageId(message1.getMessageId());
                        executeMessage(deleteMessage);
                    }
                }, 10 * 1000); // 10 sec in ms

            } else {
                SendMessage message = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .enableMarkdown(true)
                        .setText("");
                for (int i = 0; i < update.getMessage().getNewChatMembers().size(); i++) {
                    if (i == update.getMessage().getNewChatMembers().size() - 1) {
                        message
                                .setText("*" + update.getMessage().getNewChatMembers().get(i).getFirstName() + "* " + greeting);
                    } else {
                        message.setText(message.getText() + " *" +
                                update.getMessage().getNewChatMembers().get(i).getUserName() + "*");
                    }
                }
                message.setText(message.getText() + " " + greeting);

                // Save message object for deleting it afterwards
                Message message1 = executeMessage(message);

                // set required ChatID
                String CurrentChatID = update.getMessage().getChatId().toString();

                // Delete greeting after 10 sec
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        DeleteMessage deleteMessage = new DeleteMessage()
                                .setChatId(CurrentChatID)
                                .setMessageId(message1.getMessageId());
                        executeMessage(deleteMessage);
                    }
                }, 10 * 1000); // 10 sec in ms
            }
        }
    }

    private void refreshUserDataAtFixedRate ( final int days, int checkAfterDays){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshUserData(days);
            }
        }, 0, checkAfterDays * 86400000);
    }

    public void refreshUserData ( int days){
        ArrayList<String> lines = newUserData.getLines(true);
        ArrayList<Integer> userIDs = newUserData.parseNewUsers();
        MyDate myDate = new MyDate();
        int[] currentDate = parseDate(myDate.getCurrentDate()); // [day, month, year]
        for (int i = 0; i < lines.size(); i++) {
            String currentLine = lines.get(i);
            for (int z = currentLine.length() / 2; z < lines.get(i).length(); z++) {
                if (currentLine.charAt(z) == ',' && currentLine.charAt(z + 1) == ' ' &&
                        (currentLine.charAt(z + 2) >= '0' && currentLine.charAt(z + 2) <= '9')) {
                    currentLine = currentLine.substring(z + 2);
                    break;
                }
            }
            int[] userJoinDate = parseDate(currentLine);
            if (userJoinDate[1] < currentDate[1]) {
                if (userJoinDate[0] < (30 - days) || currentDate[0] >= days) {
                    newUserData.removeLineIfCointains(userIDs.get(i).toString());
                }
            } else if (userJoinDate[0] <= (currentDate[0] - days)) {
                newUserData.removeLineIfCointains(userIDs.get(i).toString());
            }
        }
    }

    public int[] parseDate (String raw){
        int[] result = new int[3];
        String cache = "";
        int arrIndex = 0;
        for (int i = 0; i < raw.length(); i++) {
            if (raw.charAt(i) == '-') {
                result[arrIndex] = Integer.parseInt(cache);
                arrIndex++;
                cache = "";
                if (arrIndex > 2) break;
            } else if (raw.charAt(i) >= '0' && raw.charAt(i) <= '9') {
                cache += raw.charAt(i);
            }
            if (i == raw.length() - 1) {
                result[arrIndex] = Integer.parseInt(cache);
                break;
            }
        }
        return result;
    }

    public void updateBotData (String BOT_TOKEN, String BOT_USERNAME){
        botData.removeLineIfCointains("BT");
        botData.removeLineIfCointains("BN");
        botData.writeLine("BT: " + BOT_TOKEN);
        botData.writeLine("BN: " + BOT_USERNAME);
    }

    private void listenForCommands (Update update){
        ArrayList<String> admins = getAdministrators();
        for (int i = 0; i < admins.size(); i++) {
            if (update.getMessage().getFrom().getId().toString().equals(admins.get(i))) {
                System.out.println("Ist Admin");
                //TODO hier für commands hören
            }
        }
    }

    public ArrayList<String> getAdministrators () {
        ArrayList<ChatMember> input = new ArrayList<ChatMember>();
        ArrayList<String> result = new ArrayList<String>();
        try {
            input = execute(new GetChatAdministrators());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < input.size(); i++) {
            result.add(input.get(i).getUser().getId().toString());
        }
        return result;
    }

    private void loadBotData () {
        ArrayList<String> data = botData.getLines(false);
        if (data.size() >= 1) {
            this.BOT_TOKEN = data.get(0).substring(4);
            this.BOT_USERNAME = data.get(1).substring(4);
        } else {
            System.out.println("Cannot laod Bot_data as there are no entries yet");
        }
    }

    private void loadCommands () {
        ArrayList<String> raw = commandData.getLines();
        String TriggerChache = "", ResponseCache = "";
        if (raw.size() >= 3) {
            for (int i = 0; i < raw.size(); i++) {
                if (raw.get(i).equals("--") || i == 0) {
                    if (i == 0) TriggerChache = raw.get(i);
                    else TriggerChache = raw.get(i - 1);

                } else if (raw.get(i).equals("---")) {
                    // Adjust response cache to not include TriggerCache at the beginning
//                    ResponseCache = ResponseCache.substring(TriggerChache.length(), ResponseCache.length()-1);
                    commands.add(new Command(TriggerChache, ResponseCache));
                    TriggerChache = ""; ResponseCache = "";
                } else {
                    if (!raw.get(i-1).equals("---")) ResponseCache += raw.get(i) + "\n";
                }
            }
        }
    }

    private void loadUserData () {
        userList = newUserData.parseNewUsers();
    }

    private void spamFeedback ( final long ChatID){
        final int messageID = sendText("⛔ _This message was deleted due to possible Spam_ ⛔", ChatID);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DeleteMessage deleteMessage = new DeleteMessage()
                        .setChatId("" + ChatID)
                        .setMessageId(messageID);
                executeMessage(deleteMessage);
            }
        }, 1500);
    }
}
