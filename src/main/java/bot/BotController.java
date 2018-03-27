package bot;

import application.TwitterController;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;
import twitter4j.Twitter;

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
        if (isRunning()) {
            if (new TwitterController().getLatestTweetURL(account)  != null) {
                bot.sendText("*New tweet!* (@" + account + "):\n" + new TwitterController()
                        .getLatestTweetURL(account), Long.parseLong(ChatID));
            }
        }
    }

    public static void sendTwitterError () {
        if (isRunning()) {
            bot.sendText("*ERROR:* _No proper twitter username set up!_", Long.parseLong(getChatID()));
        }
    }
}
