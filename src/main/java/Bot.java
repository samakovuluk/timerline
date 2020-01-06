import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    private String token;
    private String username;

    public Bot(String token, String username) {

        this.token = token;
        this.username = username;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                check();
            }
        }, 0, 5000);


    }

    public void check() {

        List<Tim> users = new ArrayList<>();

        try {
            users = Database.gett();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        users.forEach(
                e ->
                {
                    try {
                        notify(e);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
        );
    }

    public void notify(Tim tim) throws Exception {

        System.out.println(tim.getUser_id());
        SendMessage message = new SendMessage();
        message.setChatId(tim.getChat_id());
        message.setText(Database.getLine(tim.getUser_id()));
        execute(message);
        Database.setTime(tim.getUser_id());

    }


    List<String> commands = Arrays.asList("/start", "/run", "/upload", "/set", "/stop", "/getttl", "/getFile", "/help");

    private String run = EmojiParser.parseToUnicode(":smiley: Run Service");

    public String getFile(String filename, String path) {
        java.io.File localFile = new java.io.File("../documents/" + filename + ".txt");
        InputStream is = null;
        try {
            is = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + path).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.copyInputStreamToFile(is, localFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return localFile.getAbsolutePath();
    }

    public void upload(Update update) throws TelegramApiException {

        System.out.println(update.getMessage().getDocument());
        String uploadedFileId = update.getMessage().getDocument().getFileId();
        GetFile uploadedFile = new GetFile();
        uploadedFile.setFileId(uploadedFileId);

        File file = execute(uploadedFile);

        String path = getFile(uploadedFileId, file.getFilePath());

        String ms = null;
        try {
            ms = Database.setFile(path, Long.valueOf(update.getMessage().getFrom().getId()));
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(ms);
            execute(sendMessage);
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("Can not upload your file.");
            e.printStackTrace();
        }


    }


    public void onUpdateReceived(Update update) {


        if (update.getMessage().getDocument() != null) {
            try {
                upload(update);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        if (!update.getMessage().getText().equals("/run") && !update.getMessage().getText().equals("/help") && !update.getMessage().getText().equals("/start"))
            try {
                if (!Database.status(Long.valueOf(update.getMessage().getFrom().getId()))) {
                    SendMessage me = new SendMessage();
                    me.setText("Your profile not active. To start service /run");
                    me.setChatId(update.getMessage().getChatId());
                    execute(me);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        try {
            System.out.println(Database.getLastMessage(Long.valueOf(update.getMessage().getFrom().getId())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (Database.getLastMessage(Long.valueOf(update.getMessage().getFrom().getId())).equals("/set")) {
                setTtl(update);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println(update.getMessage().getText());

        if (commands.contains(update.getMessage().getText())) {
            com(update.getMessage().getText(), update);
        } else if (update.getMessage().getText().contains("/set") && update.getMessage().getText().length() >= 6) {
            System.out.println("yyyy");
            String tt = update.getMessage().getText();
            try {
                SendMessage ms = new SendMessage();
                String res = Database.set(Integer.parseInt(tt.split(" ")[1]), Long.valueOf(update.getMessage().getFrom().getId()));
                ms.setChatId(update.getMessage().getChatId());
                ms.setText(res);
                execute(ms);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        try {
            Database.setLastMessage(update.getMessage().getText(), Long.valueOf(update.getMessage().getFrom().getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void com(String command, Update update) {

        System.out.println(update.getMessage().getChat());

        System.out.println(command);

        if (command.equals("/set")) {
            SendMessage ms = new SendMessage();
            ms.setChatId(update.getMessage().getChatId());
            ms.setText("What minute do you want to receive the messages?");
            try {
                execute(ms);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


        if (command.equals("/start")) {
            try {
                start(update);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (command.contains("/run")) {
            try {
                run(update);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (command.contains("/upload")) {
            setFile(update);
        } else if (command.contains("/stop")) {
            stop(update);
        } else if (command.equals("/getttl")) {
            try {
                getTtl(update);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (command.equals("/help")) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText(EmojiParser.parseToUnicode("Hello  :bow:   I am timerLineBot which you are uploading txt" +
                    " file:page_facing_up:  and setting timer:watch:, " +
                    "and in every X minutes you will get message, message will contain random line in your txt file.\n\n" +
                    "To start service you need type command /run.\n" +
                    "For set timer you need to type command /set X minutes.\n" +
                    "And after upload your txt file.\n" +
                    "if you want to stop service type command /stop"));


            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }


    public void start(Update update) throws Exception {

        if (!Database.isExistUserId(Long.valueOf(update.getMessage().getFrom().getId()))) {


            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());
            message.setText(EmojiParser.parseToUnicode("Hello  :bow:   I am timerLineBot which you are uploading txt" +
                    " file:page_facing_up:  and setting timer:watch:, " +
                    "and in every X minutes you will get message, message will contain random line in your txt file.\n\n" +
                    "To start service you need type command /run.\n" +
                    "For set timer you need to type command /set X minutes.\n" +
                    "And after upload your txt file.\n" +
                    "if you want to stop service type command /stop"));


            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

    }

    public void run(Update update) throws Exception {

        SendMessage mes = new SendMessage();

        mes.setChatId(update.getMessage().getChatId());
        mes.setText("You profile is active now.");
        execute(mes);

        if (Database.run(Long.valueOf(update.getMessage().getFrom().getId()), update.getMessage().getChatId())) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId());

            message.setText(EmojiParser.parseToUnicode("Please upload txt file."));

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


    }

    public void setFile(Update update) {

    }

    public void setTtl(Update update) throws Exception {
        Integer mn = Integer.valueOf(update.getMessage().getText());
        String ss = Database.set(mn, Long.valueOf(update.getMessage().getFrom().getId()));
        SendMessage km = new SendMessage();
        km.setText(ss);
        km.setChatId(update.getMessage().getChatId());
        execute(km);
    }

    public void getTtl(Update update) throws Exception {

        String s = Database.getTtl(Long.valueOf(update.getMessage().getFrom().getId()));

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId());
        message.setText(s);
        execute(message);

    }

    public void stop(Update update) {

        try {
            Database.setInActive(Long.valueOf(update.getMessage().getFrom().getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SendMessage message = new SendMessage();
        message.setText("Your profile now inactive");
        message.setChatId(update.getMessage().getChatId());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return username;
    }

    public String getBotToken() {
        return token;
    }


   /* public String getBotUsername() {
        return "timerlinebot";
    }

    public String getBotToken() {
        return "849207497:AAEMjfSeiwOALS2uf5uKKel0F9rEZGdMW2c";
    }*/

    public void onClosing() {

    }
}
