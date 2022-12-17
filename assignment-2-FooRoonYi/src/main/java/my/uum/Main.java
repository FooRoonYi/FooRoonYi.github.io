package my.uum;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * This class is the main class for manipulating telegram bot.
 * It is used to initialize API context and instantiate telegram bot API.
 *
 * @author  Foo Roon Yi
 */
public class Main {
    /**
     * This method is the main method for handling telegram bot API.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}