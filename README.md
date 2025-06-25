<h1 align="center">🚖 TaxiLog</h1>

TaxiLog is an app for taxi drivers that helps track shifts and analyze earnings.

---

## 📌 **Main Features:**

-  Shift log: track income, mileage, and working hours.  
-  Automatic statistics calculation by day, week, and month.  
-  Graphs and analytics for easy income tracking.  
-  Simple and intuitive record management.  

## 🧩 Architecture Overview

The app follows a hybrid MVVM pattern with gradual migration to Compose:

- UI is mostly built with XML Fragments and Views, some screens use Compose
- Each screen uses a `ViewModel` to manage data and state
- `LiveData` is used for reactive XML binding, while `StateFlow` is used for newer Compose elements
- Room handles local persistence of shift entries
- Shared Preferences handles user settings
- Structured domain logic with models, repositories, and use cases


## 🧱 Tech Stack

- **Language:** Kotlin  
- **UI:** Mostly XML + Jetpack Compose elements  
- **Architecture:** MVVM  
- **State Management:** LiveData & StateFlow  
- **Dependency Injection:** Hilt  
- **Database:** Room  
- **UI Charts:** [ComposeCharts](https://github.com/ehsannarmani/ComposeCharts)<br>
- **Date handling:** ThreeTenABP (`LocalDate`, `LocalDateTime`)  
- **Others:** ViewModel, Coroutines, Material Design components  

## Screenshots

<table>
  <tr>
    <td><img src="screenshots/home_screen.png" alt="Home screen" width="250"/></td>
    <td><img src="screenshots/goal_screen.png" alt="Goal screen" width="250"/></td>
  </tr>
  <tr>
    <td><img src="screenshots/stats_screen.png" alt="Stats screen" width="250"/></td>
    <td><img src="screenshots/settings_screen.png" alt="Settings" width="250"/></td>
  </tr>
</table>

📥 **Installation**  

🔹 **Via [Obtainium](https://github.com/ImranR98/Obtainium)**  
Obtainium is an app that allows automatic APK updates from GitHub. If you have Obtainium installed, add this repository to keep TaxiLog up to date.  

🔹 **Alternative method**  
1. Go to the [Releases](https://github.com/HSact/TaxiLog/releases) section.  
2. Download the latest APK version.  
3. Install it on your device.  

---

<h1 align="center">🚖 TaxiLog</h1> 

TaxiLog – это приложение для водителей такси, которое помогает отслеживать смены и анализировать заработок.  

## 📌 **Основные возможности:**  

-  Ведение журнала смен: доход, пробег, время работы.  
-  Автоматический расчет статистики по дням, неделям и месяцам.  
-  Графики и аналитика для удобного просмотра динамики доходов.  
-  Удобное управление записями.  

## 🧱 Технологии

- **Язык:** Kotlin  
- **UI:** в основном XML, частично Jetpack Compose  
- **Архитектура:** MVVM  
- **Состояния:** LiveData и StateFlow  
- **DI:** Hilt  
- **БД:** Room  
- **Графики:** [ComposeCharts](https://github.com/ehsannarmani/ComposeCharts)<br>
- **Дата и время:** ThreeTenABP (`LocalDate`, `LocalDateTime`)  

---

## 🧩 Архитектура

- Основная часть UI реализована на XML  
- Для управления состоянием используются LiveData (в XML) и StateFlow (в Compose)  
- Вся логика вынесена в ViewModel и UseCases  
- Room используется для хранения данных смен
- Shared Preferenceses используется для хранения настроек

📥 **Установка**  

🔹 **Через [Obtainium](https://github.com/ImranR98/Obtainium)**  
Obtainium — это приложение для автоматического обновления APK с GitHub. Если у вас установлен Obtainium, добавьте этот репозиторий и получайте обновления автоматически.  

🔹 **Альтернативный способ**  
1. Перейдите в [раздел Releases](https://github.com/HSact/TaxiLog/releases).  
2. Скачайте последнюю версию APK.  
3. Установите на своё устройство.
