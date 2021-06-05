---
description: 'Что, как и зачем нужно ElytraHost API'
---

# Об ElytraHost API

## Общая информация

Клиент - пользователь ElytraHost

## Варианты доступа к API

ElytraHost разрешает только три варианта пользования API, список вариантов ниже:

### Реселлинг White-Label

Использовав данный вариант доступа, вы можете создать свой вебсайт, использовать стандартные методы авторизации, получать на свой счёт 9% от суммы пополнения ваших клиентов, ставить свою наценку на услуги хостинга с получением от клиента полной её суммы. 

### Управление своим аккаунтом ElytraHost

Использовав данный вариант доступа, вы cможете управлять своим аккаунтом ElytraHost на своё усмотрение.

### Получение открытой информации

Использовав данный вариант доступа, вы cможете получать метаинформацию о ElytraHost, например количество запущенных серверов

## Получение ключа доступа

Получить ключ доступа можно двумя способами:

* В личном кабинете ElytraHost, вкладка профиль
* Через методы регистрации и входа, используя ваш сайт, должна стоять CAPTCHA

## Пример запроса к API

{% api-method method="post" host="https://api.elytrahost.ru" path="/v1/:type/:method/" %}
{% api-method-summary %}
Пример
{% endapi-method-summary %}

{% api-method-description %}
Структура запроса к API
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="type" type="string" required=true %}
Тип API, например auth
{% endapi-method-parameter %}

{% api-method-parameter name="method" type="string" required=true %}
Метод в API, например login
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-headers %}
{% api-method-parameter name="Authorization" type="string" required=false %}
Токен клиента
{% endapi-method-parameter %}
{% endapi-method-headers %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
API успешно завершило работу
{% endapi-method-response-example-description %}

```javascript
{
    "success": true,
    "message": "OK",
    "answer": ...
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=400 %}
{% api-method-response-example-description %}
API не смогло обработать ваш запрос
{% endapi-method-response-example-description %}

```javascript
{
    "success": false,
    "message": "JSON Parse error",
    "answer": ...
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=401 %}
{% api-method-response-example-description %}
Токен клиента неверный
{% endapi-method-response-example-description %}

```javascript
{
    "success": false,
    "message": "Неверный токен (Wrong token)"
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=403 %}
{% api-method-response-example-description %}
Клиент не имеет доступа к объекту
{% endapi-method-response-example-description %}

```javascript
{
    "success": false,
    "message": "Клиент не имеет доступа к ModuleInstance#9c7024b2-a487-46b3-b393-4f397ae5d70f (Client hasn't access to ModuleInstance#9c7024b2-a487-46b3-b393-4f397ae5d70f)"
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=500 %}
{% api-method-response-example-description %}
API вызвало исключение \(свяжитесь с поддержкой\)
{% endapi-method-response-example-description %}

```javascript
{
    "success": false,
    "message": "NullPointerException"
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

