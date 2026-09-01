# Stellar Burgers — API-автотесты

Дипломный проект Яндекс Практикума (Diplom_2): автотесты API учебного сервиса
Stellar Burgers.

Базовый URL стенда:

`https://qa-stellarburgers.education-services.ru`

## Стек

- Java 11
- Maven
- JUnit 5 (`junit-jupiter`)
- RestAssured 5
- Allure (`allure-junit5`, `allure-rest-assured`)
- Jackson (сериализация тел запросов)

## Как запустить тесты

Из корня проекта (рядом с `pom.xml`):

```bash
mvn clean test
```

Один класс:

```bash
mvn test -Dtest=CreateOrderTest
```

Отчёт Allure (после прогона тестов):

```bash
mvn allure:serve
```

## Расхождение с документацией API: неверный хеш ингредиента

В PDF по API для `POST /api/orders` указано: невалидный хеш ингредиента → **500** Internal Server Error. Но на живом стенде `qa-stellarburgers.education-services.ru` ответ **всегда 400**.

В тесте `CreateOrderTest.shouldReturnErrorWhenIngredientHashIsInvalid` assert выставлен по документации. На стенде приходит 400, поэтому тест падает. Остальные тесты `CreateOrderTest` проходят.
