


import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Application {


    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(new File("../config.txt"));
        List<String> line = new ArrayList<>();
        while (scanner.hasNextLine()) {
            line.add(scanner.nextLine());

        }
        if (line.size()==2){

            String token = line.get(0).split("=")[1];
            String username = line.get(1).split("=")[1];

            System.out.println(token);
            System.out.println(username);
            Database.init();

            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

            telegramBotsApi.registerBot(new Bot(token,username));

        }



        /*;
*/


    }

    public static void stop()
    {
        System.exit(0);
    }

}
