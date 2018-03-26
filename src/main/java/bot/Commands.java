package bot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.ArrayList;

public interface Commands {
        Message executeMessage(SendMessage message);
        boolean executeMessage(DeleteMessage message);
        void checkForURLspam(Update update);
        void updateUserList(Update update);
        void greetNewMembers(Update update, String greeting);
        void refreshUserData(int days);
        int[] parseDate(String raw);
        void updateBotData(String BOT_TOKEN, String BOT_USERNAME);
        void addCommand(String command, String answer);
        ArrayList<String> getAdministrators();
}
