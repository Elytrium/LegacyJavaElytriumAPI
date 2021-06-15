---
description: API работы с Instance (серверами)
---

# Instance

{% api-method method="get" host="https://api.elytrahost.ru" path="/instance/listAvailable" %}
{% api-method-summary %}
Доступные сервера
{% endapi-method-summary %}

{% api-method-description %}
Получить список доступных серверов
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="post" host="https://api.elytrahost.ru/" path="instance/create" %}
{% api-method-summary %}
Создание сервера
{% endapi-method-summary %}

{% api-method-description %}
Создать сервер на хостинге ElytraHost
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-body-parameters %}
{% api-method-parameter name="name" type="string" required=false %}
Никнейм владельца
{% endapi-method-parameter %}

{% api-method-parameter name="module" type="string" required=true %}

{% endapi-method-parameter %}

{% api-method-parameter name="version" type="string" required=true %}

{% endapi-method-parameter %}

{% api-method-parameter name="billingPeriod" type="string" required=true %}

{% endapi-method-parameter %}

{% api-method-parameter name="tariff" type="string" required=true %}

{% endapi-method-parameter %}

{% api-method-parameter name="token" type="string" required=true %}
Bearer токен
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="post" host="https://api.elytrahost.ru" path="/instance/remove/" %}
{% api-method-summary %}
Удаление сервера
{% endapi-method-summary %}

{% api-method-description %}
Удаляет сервер с хотинга ElytraHost
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="uuid" type="string" required=true %}
UUID Владельца сервера
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-body-parameters %}
{% api-method-parameter name="token" type="string" required=true %}
Bearer токен клиента
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="post" host="https://api.elytrahost.ru" path="/instance/update/" %}
{% api-method-summary %}
Обновление сервера
{% endapi-method-summary %}

{% api-method-description %}

{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="uuid" type="string" required=true %}
UUID Владельца сервера
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-body-parameters %}
{% api-method-parameter name="token" type="string" required=true %}
Bearer токен клиента
{% endapi-method-parameter %}

{% api-method-parameter name="newInstance" type="string" required=true %}
хевав опиши пжпж
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="get" host="https://api.elytrahost.ru" path="/instance/info/" %}
{% api-method-summary %}
Получение информации о сервере
{% endapi-method-summary %}

{% api-method-description %}
Получить информацию и вашем сервере
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="uuid" type="string" required=true %}
UUID Владельца сервера
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-body-parameters %}
{% api-method-parameter name="token" type="string" required=true %}
Bearer токен
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="post" host="https://api.elytrahost.ru" path="/instance/run/" %}
{% api-method-summary %}
Запуск сервера
{% endapi-method-summary %}

{% api-method-description %}
Запустить сервер ElytraHost
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="uuid" type="string" required=true %}
UUID Владельца сервера
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-body-parameters %}
{% api-method-parameter name="token" type="string" required=true %}
Bearer токен
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="post" host="https://api.elytrahost.ru" path="/instance/pause/" %}
{% api-method-summary %}
Приостановка сервера
{% endapi-method-summary %}

{% api-method-description %}
Приостановить сервер ElytraHost
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="uuid" type="string" required=true %}
UUID Владельца сервера
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-body-parameters %}
{% api-method-parameter name="token" type="string" required=true %}
Bearer токен
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="get" host="https://api.elytrahost.ru" path="/instance/getDownloadLink" %}
{% api-method-summary %}
Получить ссылку на загрузку файла
{% endapi-method-summary %}

{% api-method-description %}
Получить ссылку на загрузку файла с сервера по протоколу S3
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="uuid" type="string" required=true %}
UUID Владельца сервера
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-body-parameters %}
{% api-method-parameter name="token" type="string" required=false %}
Bearer токен
{% endapi-method-parameter %}

{% api-method-parameter name="filename" type="string" required=true %}
Имя целевого файла
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

{% api-method method="get" host="https://api.elytrahost.ru" path="/instance/getUploadLink/" %}
{% api-method-summary %}
Получить ссылку на изменение файла
{% endapi-method-summary %}

{% api-method-description %}
Получить ссылку на измененние файла на сервере по протоколу S3
{% endapi-method-description %}

{% api-method-spec %}
{% api-method-request %}
{% api-method-path-parameters %}
{% api-method-parameter name="uuid" type="string" required=true %}
UUID Владельца сервера
{% endapi-method-parameter %}
{% endapi-method-path-parameters %}

{% api-method-body-parameters %}
{% api-method-parameter name="token" type="string" required=false %}
Bearer токен
{% endapi-method-parameter %}

{% api-method-parameter name="filename" type="string" required=true %}
Имя целевого файла
{% endapi-method-parameter %}
{% endapi-method-body-parameters %}
{% endapi-method-request %}

{% api-method-response %}
{% api-method-response-example httpCode=200 %}
{% api-method-response-example-description %}

{% endapi-method-response-example-description %}

```

```
{% endapi-method-response-example %}
{% endapi-method-response %}
{% endapi-method-spec %}
{% endapi-method %}

