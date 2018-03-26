package bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.BotSession;

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

    public static void setGreetingText (String text) {
        if (isRunning()) {
            bot.greeting = text;
        }
    }
}
