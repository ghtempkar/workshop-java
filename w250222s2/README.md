Sprawdzanie, czy użytkownik jest uwierzytelniony w oparciu o sesję, odbywa się automatycznie przez Spring Security. Oto główne elementy tej konfiguracji:

Podczas logowania:
W metodzie /login w AuthController po poprawnym uwierzytelnieniu wywołujemy request.getSession(true), co gwarantuje utworzenie sesji oraz przypisanie do niej kontekstu bezpieczeństwa poprzez ustawienie uwierzytelnienia w SecurityContextHolder.

Filtry Spring Security:
Domyślnie Spring Security używa filtru SecurityContextPersistenceFilter, który przy każdym żądaniu odczytuje sesję HTTP, aby sprawdzić, czy istnieje zapisany w niej kontekst bezpieczeństwa (czyli uwierzytelnienie użytkownika). Dzięki temu przy kolejnych żądaniach użytkownik jest walidowany na podstawie danych przechowywanych w sesji.