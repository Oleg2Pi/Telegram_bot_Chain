package by.polikarpov.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private static UserData userData = new UserData();

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

        private void handleTextMessage (Message message){
            long chatId = message.getChatId();

            if ("/start".equals(message.getText())) {
                User user = message.getFrom();
                // Сохраняем данные пользователя
                userData.setFirstName(user.getFirstName());
                userData.setLastName(user.getLastName());
                userData.setUsername(user.getUserName());

                // Отправляем кнопку для деления номером телефона
                sendCallBackMessage(chatId);
            }
        }

        private String getFileUrl (String fileId){
            try {
                String path = execute(new GetFile(fileId)).getFilePath();
                return "https://api.telegram.org/file/bot" + getBotToken() + "/" + path;
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        private void sendCallBackMessage ( long chatId){
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

        private void handleContactMessage (Message message){
            long chatId = message.getChatId();

            if (message.hasContact()) {
                String phoneNumber = message.getContact().getPhoneNumber();
                userData.setPhoneNumber(phoneNumber);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(String.valueOf(chatId));
                sendMessage.setText("Ваш номер телефона получен.");

                long userId = message.getFrom().getId();
                try {
                    UserProfilePhotos userProfilePhotos = execute(new GetUserProfilePhotos(userId));
                    String photoUrl = null;
                    if (userProfilePhotos.getTotalCount() > 0) {
                        String fileId = userProfilePhotos.getPhotos().get(0).get(userProfilePhotos.getPhotos().get(0).size() - 1).getFileId();
                        photoUrl = getFileUrl(fileId);
                        System.out.println(photoUrl);
                    }
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public String getBotUsername () {
            return "Chain_com_bot";
        }

        @Override
        public String getBotToken () {
            return "7598478971:AAHoHXymfbVhER7jFvTkfAGFboggg196Hy8";
        }
    }
