## valiutos-datos-ir-periodai


#### How to run:
```
1. git clone repo
2. mvn spring-boot:run
3. Go to localhost:8080

or

1. Import project to an IDE (like IntelliJ or Eclipse)
2. Run ValiutuKursaiApplication.java
3. Go to localhost:8080
```

### Functionality
* Search by date will show exchange rates of 1 EUR to all other currencies on that date
* Search by date **and** currency code, will show exchange rates of 1 EUR and specified currency on that date
* Search by Period and currency code will show what change the currency went through on that period compared to 1 EUR

### Tools Used
* Spring Boot, Maven, Thymeleaf, Bootstrap.
* JUnit 5, Mockito for tests.
