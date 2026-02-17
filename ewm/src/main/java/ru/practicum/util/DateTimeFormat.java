package ru.practicum.util;

/**
 * Утилитарный класс для хранения констант форматирования даты и времени.
 * <p>
 * Содержит общие шаблоны форматирования, используемые во всем приложении
 * для сериализации/десериализации дат в JSON и других операциях.
 * </p>
 *
 * @see com.fasterxml.jackson.annotation.JsonFormat
 */
public final class DateTimeFormat {

    /**
     * Шаблон форматирования даты и времени.
     * <p>
     * Формат: "yyyy-MM-dd HH:mm:ss" (например, "2024-01-15 14:30:00")
     * Используется для единообразного представления дат во всем API.
     * </p>
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Приватный конструктор для предотвращения создания экземпляров утилитарного класса.
     */
    private DateTimeFormat() {
    }
}