# Заглушки удалённого API

Приложение сейчас работает offline-first. Сеть намеренно не вызывается: вместо REST-клиента используется `RemoteApiStub`, который возвращает `ApiResult.NotConfigured`.

## Точки замены

| Назначение | Заглушка | Что подключить позже |
| --- | --- | --- |
| Регистрация | `RemoteApiStub.register` | `POST /auth/register` |
| Вход и токены | `RemoteApiStub.login` | `POST /auth/login` |
| Обновление сессии | `RemoteApiStub.refresh` | `POST /auth/refresh` |
| Синхронизация продукта | `RemoteApiStub.syncProduct` | `POST/PUT /products`, `POST /sync` |
| Штрихкод | `RemoteApiStub.findBarcode` | `GET /barcode/{barcode}` |
| Очередь изменений | `SyncOperationEntity` и `SyncOperationDao` | Worker с retry/backoff и idempotency по `operationId` |
| Семья и участники | пока отсутствует UI-клиент | `/family`, `/family/members/*` |
| Категории | локальный `CategoryDao` | `/categories` |
| История | локальная `ProductActionEntity` | `GET/POST /history` |

Заменять нужно реализацию интерфейса `RemoteApi`, а не Compose-экраны и не `ProductRepository`. Реальные URL, HTTPS, авторизацию и обработку конфликтов следует добавить в отдельную реализацию REST-клиента.