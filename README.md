# checker-2.0
BFT devtournament checker

**Quick start:**
1) gradlew build
Автоматически скачается грэдл нужной версии и сбилдит варник checker-2.0.war в build/libs. Варник является униварсальным, поэтому его можно сразу запускать с помощью java -jar (поднимется встроенный томкат) или закинуть его в любой application server на ваш вкус.
2) Положить рядом с варником application.properties (по умолчанию используется application.properties внутри варника). Для автоматического создания БД (если еще не существует) заполнить url, username и password для датасурса.
3) Этот пункт только для первого запуска для автоматического создания схемы БД и заполнения ее данными! Вытащить schema.sql из проекта и положить куда-нибудь. Создать sql-скрипт для заполнения БД (см. пример с тестовыми данными data.sql в проекте). В application.properties проставить spring.datasource.initialize=true и указать пути до sql-скриптов. После первого запуска не забыть проставить spring.datasource.initialize=false.
4) Перед запуском необходимо установить переменную среды JAVA_HOME, указывающую на Java 8 (используется для компиляции и запуска решений в разделе тестирования). Пример можно подглядеть в start.bat.
5) java -jar checker-2.0.war
6) Готово! Таким образом автоматически создастся и заполнится БД, и поднимется встроенный томкат (порт по умолчанию 8080). Для работы в разделе тестирования рядом с варником автоматически создастся папка "compile", которая будет использоваться для компиляции и запуска решений разработчиков.

**Бонусы:**
1) Есть возможность поднять вместе с веб-приложением TCP-server для подключения к БД с помощью вашего любимого клиента. См. h2.tcp.enabled=true
2) Есть возможность поднять вместе с веб-приложением web-консоль (порт по умолчанию 8082) для работы с БД прямо в браузере. См. h2.web.enabled=true
3) Есть возможность включить логирование. См. раздел # Logging в application.properties.
4) Если варник запускается из томката, то для того, чтобы указать путь до внешнего application.properties, необходимо в папке conf/Catalina/hostname создать файл с именем "<имя war-файла>.xml", в котором настроить контекст:
```
<?xml version="1.0" encoding="UTF-8"?>
<Context>
  <Environment name="spring.config.location" value="file:<путь до application.properties>" type="java.lang.String"/>
</Context>
```

**Разаботчикам:**
1) Используемый стэк технологий:
  - Spring Boot
  - Spring MVC
  - Thymeleaf
  - H2 Database
  - Apache HttpClient (+Fluent API +HttpMime)
  - Jsoup
  - Javaparser (https://github.com/javaparser/javaparser)
  - Lombok
2) Т.к. в проекте исп-ся либа lombok для автогенерации геттеров, сеттеров и другого boilerplate кода, то IDEA нужно настроить:
  - Подрубить галку "Enable annotation processing" в Build, Execution, Deployment -> Compiler -> Annotation Processors
  - Установить Lombok плагин
3) В проект включен spring-boot-devtools, поэтому при запуске из IDEA также запускается LiverReload server, который постоянно мониторит ресурсы из build-path (т.е. html, css, js и т.д.) на изменения и позволяет передавать их браузеру с настроенной автоперезагрузкой. Для этого необходимо поставить плагин для браузера http://livereload.com/extensions/. А чтобы изменения попадали в build-path, проект постоянно нужно ребилдить в IDEA, что не всегда удобно, поэтому можно подкрутить IDEA, чтобы она автоматичски ребилдила при любом изменении кода https://www.mkyong.com/spring-boot/intellij-idea-spring-boot-template-reload-is-not-working/.

**TODO:**
1) Украсить интерфейс (Bootstrap ?)
