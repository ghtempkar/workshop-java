Sprawdzanie, czy użytkownik jest uwierzytelniony w oparciu o sesję, odbywa się automatycznie przez Spring Security. Oto główne elementy tej konfiguracji:

Podczas logowania:
W metodzie /login w AuthController po poprawnym uwierzytelnieniu wywołujemy request.getSession(true), co gwarantuje utworzenie sesji oraz przypisanie do niej kontekstu bezpieczeństwa poprzez ustawienie uwierzytelnienia w SecurityContextHolder.

Filtry Spring Security:
Domyślnie Spring Security używa filtru SecurityContextPersistenceFilter, który przy każdym żądaniu odczytuje sesję HTTP, aby sprawdzić, czy istnieje zapisany w niej kontekst bezpieczeństwa (czyli uwierzytelnienie użytkownika). Dzięki temu przy kolejnych żądaniach użytkownik jest walidowany na podstawie danych przechowywanych w sesji.


```
curl -X POST http://localhost:8080/login -d "username=admin&password=adminpassword" -c cookies.txt

```

```
curl -X POST http://localhost:8080/orders \
     -b cookies.txt \
     -H "Content-Type: application/json" \
     -d '{"description": "Zamówienie nr 1"}'

```

```
curl -X POST http://localhost:8080/login -d "username=user&password=userpassword" -c user_cookies.txt
curl -X GET http://localhost:8080/orders -b user_cookies.txt

```