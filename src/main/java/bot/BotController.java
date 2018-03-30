package bot;

import application.TwitterController;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;

import javax.print.DocFlavor;
import java.util.Timer;
import java.util.TimerTask;

public class BotController {

    private static Bot bot;
    public static BotSession botSession;

    public static void main () {
        System.out.print("=================================================\n"
                + "============== TelegramBotCreator ===============\n"
                + "=================================================\n\n");


        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        bot = new Bot();

        try {
           botSession = botsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
            botSession = null;
        }
    }

    public static boolean isRunning() {
        return botSession != null;
    }

    public static String getChatID () {
        if (isRunning()) {
            return bot.ChatID;
        }
        return "";
    }

    public static void setCheckSpam(boolean x) {
      if (isRunning()) {
        bot.spamCheck = x;
      }
    }

    public static void setFeedback (boolean x) {
        if (isRunning()) {
            bot.giveFeedback = x;
        }
    }

    public static void setSpamOption(boolean x) {
      if (isRunning()) {
        bot.spamCheckOption = x;
      }
    }

    public static void setGreeting (boolean x) {
        if (isRunning()) {
            bot.greetings = x;
        }

    }

    public static void setForwardTweets (boolean x) {
        if (isRunning()) {
            bot.forwardTwitter = x;
        }
    }

    public static void setGreetingText (String text) {
        if (isRunning()) {
            bot.greeting = text;
        }
    }

    public static void setTwitterAccount (String account) {
        if (isRunning()) {
            bot.account = account;
        }
    }

    public static void sendLatestTweet (String ChatID, String account) {
        Integer messageID = bot.sendText("_This feature is currently unavailable_", Long.parseLong(ChatID));

        // Delete message after 10 sec
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DeleteMessage deleteMessage = new DeleteMessage()
                        .setMessageId(messageID)
                        .setChatId(ChatID);
                bot.executeMessage(deleteMessage);
            }
        }, 10000);


//        if (isRunning()) {
//            application.TwitterController twitterController = new TwitterController(account);
//            bot.sendText("*New tweet!* (@" + account + "):\n" + twitterController
//                    .getLatestTweet(), Long.parseLong(ChatID));
//        }
    }

    public static void sendTwitterError () {
        if (isRunning()) {
            bot.sendText("*ERROR:* _No proper twitter username set up!_", Long.parseLong(getChatID()));
        }
    }
}
