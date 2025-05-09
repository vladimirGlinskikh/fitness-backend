package org.example.fitness_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс серверного приложения фитнес-клуба.
 * <p>
 * Этот класс является точкой входа для запуска Spring Boot приложения.
 * Аннотация {@code @SpringBootApplication} включает автоконфигурацию,
 * сканирование компонентов и другие функции Spring Boot.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@SpringBootApplication
public class FitnessServerApplication {

	/**
	 * Конструктор по умолчанию.
	 * <p>
	 * Используется Spring для создания экземпляра класса.
	 */
	public FitnessServerApplication() {
	}

	/**
	 * Точка входа для запуска приложения.
	 *
	 * @param args аргументы командной строки
	 */

	public static void main(String[] args) {
		SpringApplication.run(FitnessServerApplication.class, args);
	}
}
