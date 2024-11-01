package by.polikarpov.service;

import by.polikarpov.entity.Company;
import by.polikarpov.entity.Employer;
import by.polikarpov.entity.Executor;
import by.polikarpov.entity.Person;
import by.polikarpov.repository.CompanyDaoImpl;
import by.polikarpov.repository.EmployerDaoImpl;
import by.polikarpov.repository.ExecutorDaoImpl;
import by.polikarpov.repository.PersonDaoImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;

public class TelegramBot extends TelegramLongPollingBot {

    private String session = null;
    private Person.PersonBuilder personBuilder = null;
    private Person person;
    private final PersonDaoImpl personDao = new PersonDaoImpl();

    private Executor.ExecutorBuilder executorBuilder = null;
    private final ExecutorDaoImpl executorDao = new ExecutorDaoImpl();

    private Employer.EmployerBuilder employerBuilder = null;
    private final EmployerDaoImpl employerDao = new EmployerDaoImpl();
    private Employer employer;

    private Company.CompanyBuilder companyBuilder = null;
    private final CompanyDaoImpl companyDao = new CompanyDaoImpl();


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleInput(update.getMessage());
        }
    }

    private void sendMessage(long chatId, String textMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textMessage);
        try {
            execute(message);
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

    private void handleInput(Message message) {
        if (message.hasText() && "/start".equals(message.getText())) session = "INIT";
        long chatId = message.getChatId();

        switch (session) {
            case "INIT":
                startConversation(chatId, message);
                break;
            case "FIRST_NAME":
                processFirstName(chatId, message);
                break;
            case "LAST_NAME":
                processLastName(chatId, message);
                break;
            case "WORK_PHONE":
                processPhone(chatId, message);
                break;
            case "EMAIL":
                processEmail(chatId, message);
                break;
            case "CHOOSE":
                processChoose(chatId, message);
                break;
            case "DESCRIPTION":
                processDescription(chatId, message);
                break;
            case "COMPANY_NAME":
                processCompanyName(chatId, message);
                break;
            case "COMPANY_ADDRESS":
                processCompanyAddress(chatId, message);
                break;
            case "COMPANY_DESCRIPTION":
                processCompanyDescription(chatId, message);
                break;
            default:
                break;
        }
    }

    private void processCompanyDescription(long chatId, Message message) {
        if (message.hasText()) {
            companyDao.save(companyBuilder.description(message.getText()).build());
            session = null;
            sendMessage(chatId, "Добро пожаловать на платформу.");
        }
    }

    private void processCompanyAddress(long chatId, Message message) {
        if (message.hasText()) {
            companyBuilder.address(message.getText());
            session = "COMPANY_DESCRIPTION";
            sendMessage(chatId, "Опишите деятельность вашей компании: ");
        }
    }

    private void processCompanyName(long chatId, Message message) {
        if (message.hasText()) {
            companyBuilder = Company.builder().name(message.getText()).employer(employer);
            session = "COMPANY_ADDRESS";
            sendMessage(chatId, "Какой адрес у вашей компании: ");
        }
    }

    private void processDescription(long chatId, Message message) {
        if (message.hasText()) {
            executorBuilder.description(message.getText());
            executorDao.save(executorBuilder.portfolio(null).build());
            session = null;
            sendMessage(chatId, "Для того, чтобы работодатели смогли оценить вас, вам нужно загрузить портфолио. Это можно будет сделать на платформе.");
        }
    }

    private void processChoose(long chatId, Message message) {
        if (message.hasText() && message.getText().equals("Исполнитель")) {
            session = "DESCRIPTION";
            executorBuilder = Executor.builder();
            executorBuilder.person(person);
            sendMessage(chatId, "Опишите вашу деятельность исполнителя: ");
        } else if (message.hasText() && message.getText().equals("Работодатель")) {
            session = "COMPANY_NAME";
            employerBuilder = Employer.builder().person(person);
            employer = employerBuilder.build();
            employerDao.save(employer);
            sendMessage(chatId, "Какое название у вашей организации: ");
        }
    }

    private void processEmail(long chatId, Message message) {
        if (message.hasText()) {
            person = personBuilder.email(message.getText()).build();
            personDao.save(person);
            session = "CHOOSE";

            SendMessage messageSend = new SendMessage();
            messageSend.setChatId(chatId);
            messageSend.setText("Выберите вашу роль: ");

            ReplyKeyboardMarkup keyboardMarkup = getReplyKeyboardMarkup();
            messageSend.setReplyMarkup(keyboardMarkup);

            try {
                execute(messageSend);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardRow row = new KeyboardRow();

        row.add(new KeyboardButton("Исполнитель"));
        row.add(new KeyboardButton("Работодатель"));

        keyboardMarkup.setKeyboard(Collections.singletonList(row));
        return keyboardMarkup;
    }

    private void processPhone(long chatId, Message message) {
        if (message.hasText()) {
            personBuilder.workPhone(message.getText());
            session = "EMAIL";
            sendMessage(chatId, "Ваш email: ");
        }
    }

    private void processLastName(long chatId, Message message) {
        if (message.hasText()) {
            personBuilder.lastName(message.getText());
            session = "WORK_PHONE";
            sendMessage(chatId, "Ваш номер телефона: ");
        }
    }

    private void processFirstName(long chatId, Message message) {
        if (message.hasText()) {
            personBuilder.firstName(message.getText());
            session = "LAST_NAME";
            sendMessage(chatId, "Ваша фамилия: ");
        }
    }

    private void startConversation(long chatId, Message message) {
        personBuilder = Person.builder();
        personBuilder.usernameTG(message.getFrom().getUserName());
        personBuilder.chatId(chatId);
        session = "FIRST_NAME";
        sendMessage(chatId, "Добро пожаловать в Chain! Это платформа для поиска работы на базе телеграмма.");
        sendMessage(chatId, "Для входа, нужно ввести некоторые данные о вас. Ваше имя: ");
    }
}
