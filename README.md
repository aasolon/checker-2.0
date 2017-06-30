# checker-2.0
BFT devtournament checker

Quick start:
1) gradlew build
Автоматически скачается грэдл нужной версии и сбилдит варник checker-2.0.war в build/libs. Варник является униварсальным, поэтому его можно сразу запускать с помощью java -jar (поднимется встроенный томкат) или закинуть его в любой application server на ваш вкус.
3) Положить рядом с варником application.properties (по умолчанию используется application.properties внутри варника). Для автоматического создания БД (если еще не существует) заполнить url, username и password для датасурса.
2) Этот пункт только для первого запуска для автоматического создания схемы БД и заполнения ее данными! Вытащить schema.sql из проекта и положить куда-нибудь. Создать sql-скрипт для заполнения БД (см. пример с тестовыми данными data.sql в проекте). В application.properties проставить spring.datasource.initialize=true и указать пути до sql-скриптов. После первого запуска не забыть проставить spring.datasource.initialize=false.
3) java -jar checker-2.0.war
4) Готово! Таким образом автоматически создастся и заполнится БД, и поднимется встроенный томкат (порт по умолчанию 8080)

Бонусы:
1) Есть возможность поднять вместе с веб-приложением TCP-server для подключения к БД с помощью вашего любимого клиента. См. h2.tcp.enabled=true
2) Есть возможность поднять вместе с веб-приложением web-консоль (порт по умолчанию 8082) для работы с БД прямо в браузере. См. h2.web.enabled=true
3) Есть возможность включить логирование. См. раздел # Logging в application.properties.
4) Если варник запускается из томката, то для того, чтобы указать путь до внешнего application.properties, необходимо в папке conf\Catalina\<hostname> создать файл с именем "<имя war-файла>.xml", в котором настроить контекст:
```
<?xml version="1.0" encoding="UTF-8"?>
<Context>
  <Environment name="spring.config.location" value="file:<путь до application.properties>" type="java.lang.String"/>
</Context>
```

Разаботчикам:
1) Используемый стэк технологий:
  - Spring Boot
  - Spring MVC
  - Thymeleaf
  - H2 Database
  - Apache HttpClient (+Fluent API +HttpMime)
  - Jsoup
  - Lombok
2) Т.к. в проекте исп-ся либа lombok для автогенерации геттеров, сеттеров и другого boilerplate кода, то Idea нужно настроить:
  - Подрубить галку "Enable annotation processing" в Build, Execution, Deployment -> Compiler -> Annotation Processors
  - установить Lombok плагин

TODO:
1) Exception handling
2) Украсить интерфейс
