---
description: API работы с пользователем
---

# User

## Не требует токена

Методы указанные ниже не требуют токена \(UnauthorizedRequest в API\), однако требуют указание ключа и вида CAPTCHA. 

### Объект CAPTCHA

Объект CAPTCHA выглядит так:

```javascript
{
    "captchaRequest": "токен",
    "captchaProvider": "hcaptcha"
}
```

На данный момент ElytraHost API поддерживает только провайдер "hcaptcha", siteKey: "e5b9fdf9-38ad-4cff-97fd-d2e18755a3ee". Используйте код ниже для вставки капчи на свой сайт. 

```javascript
<script src="https://hcaptcha.com/1/api.js" async defer></script>

<div class="h-captcha" data-sitekey="e5b9fdf9-38ad-4cff-97fd-d2e18755a3ee"></div>
```

{% api-method method="post" host="https://api.elytrahost.ru" path="/v1/user/login" %}
{% api-method-summary %}
Вход в аккаунт
{% endapi-method-summary %}

{% api-method-description %}
Вход в аккаунт
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-body-parameters %}
{% api-method-parameter name="captcha" type="object" required=true %}
Объект CAPTCHA, пример выше
{% endapi-method-parameter %}

{% api-method-parameter name="email" type="string" required=true %}
Почта, на которую зарегистрирован аккаунт
{% endapi-method-parameter %}

{% api-method-parameter name="password" type="string" required=true %}
Пароль
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
Успешный вход в аккаунт
{% endapi-method-response-example-description %}

```javascript
{
    "success": true,
    "message": "OK",
    "answer": "токен аккаунта" 
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=401 %}
{% api-method-response-example-description %}
Неверный пароль
{% endapi-method-response-example-description %}

```javascript
{
    "success": false,
    "message": "Wrong password (Неверный пароль)"
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}



{% api-method method="post" host="https://api.elytrahost.ru" path="/v1/user/login\_via\_linked\_account" %}
{% api-method-summary %}
Вход в аккаунт через привязанную соц. сеть
{% endapi-method-summary %}

{% api-method-description %}
Вход в аккаунт через привязанную соц. сеть
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-body-parameters %}
{% api-method-parameter name="captcha" type="object" required=true %}
Объект CAPTCHA, пример выше
{% endapi-method-parameter %}

{% api-method-parameter name="linkedAccountType" type="string" required=true %}
Код соц. сети. \("vk", "discord", "google"\)
{% endapi-method-parameter %}

{% api-method-parameter name="token" type="string" required=true %}
OAuth или прочий токен от аккаунта
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
Успешный вход в аккаунт
{% endapi-method-response-example-description %}

```javascript
{
    "success": true,
    "message": "OK",
    "answer": "токен аккаунта" 
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=400 %}
{% api-method-response-example-description %}
Некорректно указан токен или вид социальной сети
{% endapi-method-response-example-description %}

```javascript
{
    "success": false,
    "message": "Incorrect 'user' parameter"
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="post" host="https://api.elytrahost.ru" path="/v1/user/send\_register\_link" %}
{% api-method-summary %}
Регистрация, отправить ссылку на почту
{% endapi-method-summary %}

{% api-method-description %}
Вход в аккаунт через привязанную соц. сеть
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-body-parameters %}
{% api-method-parameter name="captcha" type="object" required=true %}
Объект CAPTCHA, пример выше
{% endapi-method-parameter %}

{% api-method-parameter name="email" type="string" required=true %}
Почта для регистрации
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
Ссылка успешно отправлена
{% endapi-method-response-example-description %}

```javascript
{
    "success": true,
    "message": "OK"
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=400 %}
{% api-method-response-example-description %}
Некорректно указан токен или вид социальной сети
{% endapi-method-response-example-description %}

```javascript
{
    "success": false,
    "message": "Incorrect 'user' parameter"
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="post" host="https://api.elytrahost.ru" path="/v1/user/register" %}
{% api-method-summary %}
Регистрация, подтвердить ссылку с почты
{% endapi-method-summary %}

{% api-method-description %}
Вход в аккаунт через привязанную соц. сеть
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-body-parameters %}
{% api-method-parameter name="UUID" type="string" required=true %}
UUID с почты
{% endapi-method-parameter %}

{% api-method-parameter name="password" type="string" required=true %}
Пароль, который необходимо поставить на аккаунт
{% endapi-method-parameter %}

{% api-method-parameter name="captcha" type="object" required=true %}
Объект CAPTCHA, пример выше
{% endapi-method-parameter %}

{% api-method-parameter name="email" type="string" required=true %}
Почта для регистрации
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}
Успешный вход в аккаунт
{% endapi-method-response-example-description %}

```javascript
{
    "success": true,
    "message": "OK",
    "answer": "токен аккаунта" 
}
```
{% endapi-method-response-example %}

{% api-method-response-example httpCode=400 %}
{% api-method-response-example-description %}
Некорректно указан пользователь или капча
{% endapi-method-response-example-description %}

```javascript
{
    "success": false,
    "message": "Incorrect 'user' parameter"
}
```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

