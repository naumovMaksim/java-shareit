# java-shareit
Это проект для шеринга вещей.

Он обеспечивает пользователям, во-первых, возможность рассказывать, какими вещами они готовы поделиться, а во-вторых, находить нужную вещь и брать её в аренду на какое-то время. 

Версия языка: 17.

Зависимости: springframework, lombok, h2database.

В этом проекте использованы: 
- Spring Boot;
- Базы данных SQL;
- MockMVC;
- Docker.

### Использование: 

 1. Запуск сервера ShareItServer через среду разработки:
 2. Запуск ShareItGateway;
 3. Обращение к серверу через Http протокол:

        3.1 http://localhost:8080;
        3.2 Используемый формат записи - Json через body;
        3.3 Пример создания пользователя
        }
        "name": "user",
        "email": "user@user.com"
        }
        3.4 Пример создания объекта: (Используется заголовок "[{"key":"X-Sharer-User-Id","value":"1","type":"text"}]" для отслеживания предметов пользователей) 
        {
        "name": "Дрель",
        "description": "Простая дрель",
        "available": true
        }
 
 
