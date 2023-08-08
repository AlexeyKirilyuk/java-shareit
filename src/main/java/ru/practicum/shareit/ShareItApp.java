package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * пароль pgAdmin будет iamroot
 */
@Slf4j
@SpringBootApplication
public class ShareItApp {
    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
        log.warn("----------------------------------   !!! Запуск !!!   ---------------------------------------");
    }
}