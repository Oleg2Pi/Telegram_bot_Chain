package by.polikarpov.service;

import by.polikarpov.entity.Executor;
import by.polikarpov.entity.ImagePerson;
import by.polikarpov.entity.Person;
import by.polikarpov.repository.ExecutorDao;
import by.polikarpov.repository.ImagePersonDao;
import by.polikarpov.repository.PersonDao;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.UserProfilePhotos;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private Person.PersonBuilder person = Person.builder();
    private ImagePerson.ImagePersonBuilder image = ImagePerson.builder();
    private Executor.ExecutorBuilder executor = Executor.builder();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.hasText()) {
                handleTextMessage(message);
            } else if (message.hasContact()) {
                handleContactMessage(message);
            }
        }
    }

    private void handleTextMessage(Message message) {
        long chatId = message.getChatId();

        if ("/start".equals(message.getText())) {
            User user = message.getFrom();

            person.firstName(user.getFirstName());
            person.lastName(user.getLastName());
            person.usernameTG(user.getUserName());
            person.chatId(chatId);

            try {
                UserProfilePhotos userProfilePhotos = execute(new GetUserProfilePhotos(user.getId()));
                String photoUrl = null;
                if (userProfilePhotos.getTotalCount() > 0) {
                    String fileId = userProfilePhotos.getPhotos().get(0).get(userProfilePhotos.getPhotos().get(0).size() - 1).getFileId();
                    photoUrl = getFileUrl(fileId);
                    image.file(downloadImage(photoUrl));
                }
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

            // Отправляем кнопку для деления номером телефона
            sendCallBackMessage(chatId);
        } else if ("Исполнитель".equals(message.getText()) || "Заказчик".equals(message.getText())) {
            handleRoleSelection(message);
        }
    }

    private byte[] downloadImage(String photoUrl) {
        byte[] image = null;

        try {
            URL url = new URL(photoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            image = inputStream.readAllBytes();

            inputStream.close();
            connection.disconnect();

            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFileUrl(String fileId) {
        try {
            String path = execute(new GetFile(fileId)).getFilePath();
            return "https://api.telegram.org/file/bot" + getBotToken() + "/" + path;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRoleSelection(Message message) {

        if ("Заказчик".equals(message.getText())) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("На данный момент заказчик находиться в разработке, доступен исполнитель");

            try {
                execute(sendMessage);
                sendRoleSelectionMessage(message.getChatId());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if ("Исполнитель".equals(message.getText())) {
            saveAllObjects();

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Ваш профиль создан. Нажмите на open App, чтобы перейти в него");
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveAllObjects() {
        PersonDao personDao = new PersonDao();
        Person savedPerson = personDao.save(person.build());

        ImagePersonDao imagePersonDao = new ImagePersonDao();
        image.person(savedPerson);
        ImagePerson savedImage = imagePersonDao.save(image.build());
        savedPerson.setImage(savedImage);

        ExecutorDao executorDao = new ExecutorDao();
        executor.person(savedPerson);
        Executor savedExecutor = executorDao.save(executor.build());
        savedPerson.setExecutor(savedExecutor);
    }

    private void sendCallBackMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Нажмите кнопку ниже, чтобы поделиться своим номером телефона:");

        // Создаем клавиатуру для отправки контакта
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        // Создаем кнопку для отправки контакта
        KeyboardButton contactButton = new KeyboardButton();
        contactButton.setText("Поделиться номером");
        contactButton.setRequestContact(true); // Устанавливаем запрос контакта

        row.add(contactButton);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);

        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(sendMessage); // Отправляем сообщение с кнопкой
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleContactMessage(Message message) {
        long chatId = message.getChatId();

        if (message.hasContact()) {
            String phoneNumber = message.getContact().getPhoneNumber();
            person.phone(phoneNumber);

            sendRoleSelectionMessage(chatId);
        }
    }

    private void sendRoleSelectionMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите вашу роль");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton executorButton = new KeyboardButton("Исполнитель");
        KeyboardButton customerButton = new KeyboardButton("Заказчик");

        row.add(executorButton);
        row.add(customerButton);

        rows.add(row);
        keyboardMarkup.setKeyboard(rows);

        sendMessage.setReplyMarkup(keyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "Chain_com_bot";
    }

    @Override
    public String getBotToken() {
        return "7598478971:AAHoHXymfbVhER7jFvTkfAGFboggg196Hy8";
    }
}
