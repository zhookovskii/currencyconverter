# CurrencyConverter

### Приложение для конвертации валют

Подерживаемые валюты:
* Американские доллары (USD)
* Евро (EUR)
* Российские рубли (RUB)
* Британские фунты (GBP)
* Сербские динары (RSD)
* и другие

##### Для получения информации о текущем курсе валют используется [ExchangeRate-API](https://www.exchangerate-api.com)
Для корректной работы приложения необходимо получить ключ и вставить его в файл [Config.kt](app/src/main/java/com/zhukovskii/currencyconverter/config/Config.kt)

При написании были использованы следующие технологии:
* __Retrofit__ для получения данных от API
* __Room__ для взаимодействия с базой данных
* __Dagger Hilt__ для внедрения зависимостей
* __Navigation Component__ для навигации между фрагментами
* __Coroutines__ + __Flow__ для реализации реактивного подхода
* __AndroidX Lifecycle__ для написания lifecycle-aware асинхронного кода

Также были применены следующие архитектурные паттерны:
* __MVVM__ (Model-View-ViewModel)
* __SSOT__ (Single Source of Truth)
* __Single Activity__

