---
description: API работы с балансом пользователя
---

# Balance

{% api-method method="get" host="https://api.elytrahost.ru" path="/balance/listMethods" %}
{% api-method-summary %}
Получить методы пополнения баланса
{% endapi-method-summary %}

{% api-method-description %}
Получение методов пополнения баланса
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
Успешно получены методы
{% endapi-method-response-example-description %}

```javascript
{
    "success": true,
    "message": "OK",
    "answer": [
        {
            "name": "qiwi",
            "displayName": "Qiwi (0%) и банковские карты (2%)"
        }
    ]
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="get" host="https://api.elytrahost.ru" path="/balance/genTopUpLink" %}
{% api-method-summary %}
Получить ссылку на пополнение баланса
{% endapi-method-summary %}

{% api-method-description %}
Получение методов пополнения баланса
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-body-parameters %}
{% api-method-parameter name="token" type="string" required=true %}
Токен пользователя
{% endapi-method-parameter %}

{% api-method-parameter name="amount" type="integer" required=true %}
Пополняемая сумма в рублях
{% endapi-method-parameter %}

{% api-method-parameter name="topUpMethod" type="string" required=true %}
Метод пополнения баланса
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
Успешно получена ссылка на пополнение баланса
{% endapi-method-response-example-description %}

```javascript
{
    "success": true,
    "message": "OK",
    "answer": "https://oplata.qiwi.com/form?invoiceUid=c28c0513-8a80-4482-a326-1dd997f8abba" 
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=400 %}
{% api-method-response-example-description %}
Неверный параметр 'amount' или 'topUpMethod'
{% endapi-method-response-example-description %}

```javascript
{
    "success": false,
    "message": "Incorrect 'amount' parameter"
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

