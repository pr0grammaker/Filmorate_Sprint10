# 🎬 Filmorate

![Filmorate Banner](https://user-images.githubusercontent.com/yourusername/banner.png)

**Filmorate** — это современное приложение для обмена мнениями о фильмах и создания персональных рейтингов.  
Проект реализован на **Java** с использованием **Spring Boot**, **Lombok** и **Postman** для тестирования API.  

---

## 🚀 Основные возможности

- 👤 **Управление пользователями**
  - Добавление, обновление и удаление друзей
  - Просмотр списка друзей

- 🎞 **Фильмы**
  - Создание, обновление и удаление фильмов
  - Добавление лайков к фильмам
  - Просмотр популярности и рейтингов фильмов
  - Автоматическая обработка длительности фильмов в секундах (не ISO-8601)

- 💬 **Комментарии и оценки**
  - Возможность ставить оценки фильмам
  - Просмотр статистики по лайкам и популярности

---

## 🛠 Стек технологий

- **Java 21**  
- **Spring Boot** (REST API)  
- **Lombok** (для упрощения моделей и билдера)  
- **H2 / InMemory Storage** (для тестирования)  
- **Postman** (для автоматизации тестирования API)  
- **SLF4J / Logback** (логирование действий пользователей)  

---

## 📂 Структура проекта

```text
Filmorate/
├─ src/main/java/
│  ├─ controller/ # Контроллеры REST API
│  ├─ exception/  # Исключения
│  ├─ model/      # Сущности
│  ├─ serializers/# Сериализаторы  
│  ├─ services/   # Бизнес-логика
│  ├─ storage/    # Хранилища данных
├─ src/test/java/ # Юнит-тесты
├─ pom.xml        # Maven зависимости
└─ README.md      # Описание проекта

---

## ER Diagram

![Filmorate ERD](https://github.com/user-attachments/assets/0cc72915-cc46-4614-ae2a-8ca2dfdfa19a)

## Таблицы с данными и основные запросы для основных операций приложения
## 📸 Screenshots

### 🖼️ Screenshot 1
![Screenshot 1](https://github.com/user-attachments/assets/6e212d4a-485a-470b-9cbd-be6499e4f958)

### 🖼️ Screenshot 2
![Screenshot 2](https://github.com/user-attachments/assets/cab05d13-0567-4967-b7d0-33a4a5325e45)

### 🖼️ Screenshot 3
![Screenshot 3](https://github.com/user-attachments/assets/e1c0ac32-27cf-4cab-886b-236e5883e95c)

### 🖼️ Screenshot 4
![Screenshot 4](https://github.com/user-attachments/assets/47c6cba4-3f59-4c4b-82c6-155eb44dd4e5)

### 🖼️ Screenshot 5
![Screenshot 5](https://github.com/user-attachments/assets/e0b65833-f8fc-42aa-8fbc-0a8188a15fed)
