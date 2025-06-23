# Dokumentacja Projektu - System Zarzadzania Baza Danych Studentow

**Kurs:** Programowanie Obiektowe  
**Autor:** Mateusz Konachowicz  
**Wersja:** 1.0  
**Data:** 23.06.2025  
**Jezyk:** Java  
**GUI:** Swing  

---

## Spis tresci

1. Wprowadzenie
2. Opis zadania
3. Architektura systemu
4. Implementacja mechanizmow OOP
5. Dane testowe w systemie
6. Interfejs uzytkownika
7. Instrukcja uzytkownika
8. Podsumowanie

---

## 1. Wprowadzenie

Niniejszy dokument zawiera kompletna dokumentacje projektu "System Zarzadzania Baza Danych Studentow", zrealizowanego w ramach kursu Programowanie Obiektowe.

Projekt stanowi implementacje systemu zarzadzania informacjami o studentach, kursach i ocenach na uczelni. Glownym celem projektu bylo stworzenie funkcjonalnej aplikacji z graficznym interfejsem uzytkownika, ktora demonstruje praktyczne zastosowanie paradygmatow programowania obiektowego.

System realizuje nastepujace funkcjonalnosci:
- Zarzadzanie studentami (dodawanie, edycja, usuwanie, wyszukiwanie)
- Zarzadzanie kursami akademickimi
- System ocen z roznymi typami sprawdzianow
- Zapisywanie i wypisywanie studentow na kursy
- Obliczanie sredniej ocen (GPA)
- Persystencja danych w plikach

---

## 2. Opis zadania

### 2.1. Zalozenia funkcjonalne

System realizuje nastepujace glowne funkcjonalnosci:

**Zarzadzanie studentami:**
- Dodawanie, edycja, usuwanie i wyszukiwanie studentow
- Przechowywanie danych osobowych i akademickich
- Sortowanie i filtrowanie wedlug roznych kryteriow
- Zarzadzanie zapisami studentow na kursy

**Zarzadzanie kursami akademickimi:**
- Tworzenie i edycja kursow z punktami ECTS
- Ustawianie limitow studentow
- Przegladanie listy zapisanych studentow

**System ocen:**
- Rozne typy ocen (egzamin, kolokwium, projekt)
- Obliczanie sredniej ocen (GPA)
- Historia ocen z datami i opisami

### 2.2. Ograniczenia techniczne

Projekt zostal zrealizowany z nastepujacymi ograniczeniami:
- Implementacja w jezyku Java
- Wykorzystanie biblioteki Swing do GUI
- Persystencja danych poprzez serializacje
- Brak wykorzystania zewnetrznych bibliotek

---

## 3. Architektura systemu

System zostal zaprojektowany zgodnie z zasadami programowania obiektowego, wykorzystujac wzorzec Model-View-Controller (MVC).

### 3.1. Struktura pakietow

| Pakiet | Zawartosc | Opis |
|--------|-----------|------|
| studentdb.models | Person, Student, Course, Grade | Modele danych |
| studentdb.services | StudentService, CourseService | Logika biznesowa |
| studentdb.gui | MainFrame, panele GUI | Interfejs uzytkownika |
| studentdb.exceptions | Wlasne klasy wyjatkow | Obsluga bledow |
| studentdb.utils | FileManager | Narzedzia pomocnicze |

### 3.2. Diagram klas UML

```
Person (abstrakcyjna)
├── firstName: String
├── lastName: String
├── email: String
├── birthDate: LocalDate
├── getFullName(): String
└── getRole(): String (abstrakcyjna)
    ↑
    └── Student
        ├── studentId: String
        ├── enrolledCourses: Set<String>
        ├── yearOfStudy: int
        ├── studyProgram: String
        └── getRole(): String

Course
├── courseCode: String
├── courseName: String
├── description: String
├── ectsPoints: int
└── maxStudents: int

Grade
├── value: double
├── description: String
├── gradeType: GradeType
├── date: LocalDate
└── letterGrade: String

StudentManagementException
├── StudentNotFoundException
└── CourseNotFoundException
```

---

## 4. Implementacja mechanizmow OOP

### 4.1. Realizacja zalozen projektowych

Projekt spelnia wszystkie zalozenia i wymagania postawione w ramach kursu Programowanie Obiektowe:

| Mechanizm OOP | Implementacja | Zasluzony |
|---------------|---------------|-----------|
| Enkapsulacja | Zaimplementowana poprzez prywatne pola z publicznymi metodami dostepowymi | ✓ |
| Dziedziczenie | Zrealizowane poprzez hierarchie klas Person → Student | ✓ |
| Polimorfizm | Zastosowany w metodach toString() i getRole() | ✓ |
| Klasy abstrakcyjne | Wykorzystana klasa abstrakcyjna Person | ✓ |
| Interfejsy | Implementacja interfejsu Serializable | ✓ |
| Kolekcje | Zastosowano roznorodne kolekcje (Map, Set, List) i operacje na nich | ✓ |
| Wlasne wyjatki | Zdefiniowano hierarchie wyjatkow dla systemu | ✓ |
| Zasady SOLID | Zastosowano zasady SRP i DIP | ✓ |
| GUI Swing | Stworzono kompletny, funkcjonalny interfejs uzytkownika | ✓ |
| Persystencja danych | Zaimplementowano serializacje obiektow do plikow | ✓ |

### 4.2. Dane testowe w systemie

System zostal zaladowany realistycznymi danami testowymi, ktore obejmuja 6 studentow informatyki na roznych semestrach, z mieszanka studiow stacjonarnych i niestacjonarnych. Kazdy student ma przypisane kursy i obliczona srednia ocen.

### 4.3. Mozliwosci rozbudowy systemu

System stanowi solidna podstawe dla systemu zarzadzania baza danych studentow i moze byc rozbudowany o nastepujace funkcjonalnosci:
- Raportowanie: Generowanie raportow w formacie PDF lub CSV
- System powiadomien: Informowanie studentow o nowych ocenach
- Baza danych: Zastapienie serializacji baza danych SQL lub NoSQL
- Interfejs webowy: Dodanie interfejsu webowego dla latwiejszego dostepu
- Autentykacja i autoryzacja: Dodanie systemu logowania i rol uzytkownikow

---

## 5. Dane testowe w systemie

### 5.1. Studenci

| ID | Imie | Nazwisko | Email | Kierunek | Semestr | Typ | Srednia |
|----|------|----------|-------|----------|---------|-----|---------|
| S001 | Jan | Kowalski | j.kowalski@student.edu.pl | Informatyka | 3 | Stacjonarne | 4.2 |
| S002 | Anna | Nowak | a.nowak@student.edu.pl | Informatyka | 5 | Stacjonarne | 4.7 |
| S003 | Piotr | Wisniewski | p.wisniewski@student.edu.pl | Informatyka | 1 | Niestacjonarne | 3.8 |
| S004 | Maria | Kaminska | m.kaminska@student.edu.pl | Informatyka | 7 | Stacjonarne | 4.5 |
| S005 | Tomasz | Lewandowski | t.lewandowski@student.edu.pl | Informatyka | 2 | Niestacjonarne | 4.0 |
| S006 | Katarzyna | Dabrowska | k.dabrowska@student.edu.pl | Informatyka | 4 | Stacjonarne | 4.3 |

### 5.2. Kursy

| Kod | Nazwa | ECTS | Max studentow | Opis |
|-----|-------|------|---------------|------|
| PO001 | Programowanie Obiektowe | 6 | 50 | Podstawy programowania w Javie |
| BD001 | Bazy Danych | 5 | 45 | Projektowanie i implementacja baz danych |
| ALG001 | Algorytmy i Struktury Danych | 6 | 40 | Zaawansowane algorytmy |
| MAT001 | Matematyka Dyskretna | 4 | 60 | Podstawy matematyki dla informatykow |
| WEB001 | Technologie Webowe | 5 | 35 | HTML, CSS, JavaScript |

---

## 6. Interfejs uzytkownika

### 6.1. Glowne okno aplikacji

Glowne okno aplikacji zawiera pasek menu oraz panel z zakladkami:
- Zakladka "Studenci" - do zarzadzania studentami
- Zakladka "Kursy" - do zarzadzania kursami
- Zakladka "Oceny" - do zarzadzania ocenami

### 6.2. Panel zarzadzania studentami

Panel studentow zawiera:
- Tabele studentow wyswietlajaca dane wszystkich studentow
- Formularz do dodawania i edycji studentow
- Przyciski do zarzadzania studentami (Dodaj, Edytuj, Usun)
- Pole wyszukiwania do filtrowania studentow
- Przycisk "Zarzadzaj kursami" do zarzadzania zapisami studenta na kursy

### 6.3. Panel zarzadzania kursami

Panel kursow zawiera:
- Liste kursow
- Formularz do dodawania i edycji kursow
- Przyciski do zarzadzania kursami (Dodaj, Edytuj, Usun)
- Przycisk "Pokaz studentow" do wyswietlania listy studentow zapisanych na kurs

### 6.4. Panel zarzadzania ocenami

Panel ocen zawiera:
- Pola wyboru studenta i kursu
- Tabele ocen wyswietlajaca oceny wybranego studenta z wybranego kursu
- Formularz do dodawania nowej oceny
- Przycisk do obliczania sredniej ocen (GPA)

---

## 7. Instrukcja uzytkownika

### 7.1. Uruchomienie aplikacji

Aby uruchomic aplikacje, nalezy:
1. Upewnic sie, ze zainstalowana jest Java w wersji 8 lub nowszej
2. Uruchomic plik JAR lub skompilowac i uruchomic klase Main
3. Po uruchomieniu aplikacji zostanie wyswietlone glowne okno z trzema zakladkami

### 7.2. Zarzadzanie studentami

**Dodawanie nowego studenta:**
1. Przejdz do zakladki "Studenci"
2. Wypelnij formularz danymi studenta
3. Kliknij przycisk "Dodaj"

**Edycja danych studenta:**
1. Zaznacz studenta w tabeli
2. Kliknij przycisk "Edytuj"
3. Zmodyfikuj dane w formularzu
4. Kliknij przycisk "Zapisz"

### 7.3. Zarzadzanie kursami

**Dodawanie nowego kursu:**
1. Przejdz do zakladki "Kursy"
2. Wypelnij formularz danymi kursu
3. Kliknij przycisk "Dodaj"

**Przegladanie studentow zapisanych na kurs:**
1. Zaznacz kurs na liscie
2. Kliknij przycisk "Pokaz studentow"
3. W oknie dialogowym zostanie wyswietlona lista zapisanych studentow

### 7.4. Zarzadzanie ocenami

**Dodawanie nowej oceny:**
1. Przejdz do zakladki "Oceny"
2. Wybierz studenta z listy rozwijalnej
3. Wybierz kurs z listy rozwijalnej
4. Wypelnij formularz danymi oceny (wartosc, typ oceny, data)
5. Kliknij przycisk "Dodaj ocene"

**Obliczanie sredniej ocen:**
1. Wybierz studenta z listy rozwijalnej
2. Kliknij przycisk "Oblicz GPA"
3. Zostanie wyswietlona srednia ocen studenta ze wszystkich kursow

---

## 8. Podsumowanie

### 8.1. Realizacja zalozen projektowych

Projekt "System Zarzadzania Baza Danych Studentow" spelnia wszystkie zalozenia i wymagania postawione w ramach kursu Programowanie Obiektowe. System w pelni realizuje koncepcje programowania obiektowego, wykorzystuje biblioteke Swing do utworzenia funkcjonalnego interfejsu uzytkownika oraz implementuje persystencje danych poprzez serializacje.

### 8.2. Mozliwosci dalszego rozwoju

System moze byc rozbudowany o dodatkowe funkcjonalnosci, takie jak system raportowania, powiadomienia czy tez integracja z zewnetrzna baza danych. Obecna architektura umozliwia latwa rozbudowe i modyfikacje bez naruszania istniejacego kodu.

### 8.3. Wnioski koncowe

Projekt stanowi kompletna implementacje systemu zarzadzania danymi studenckimi z wykorzystaniem najwazniejszych mechanizmow programowania obiektowego. Wszystkie zaplanowane funkcjonalnosci zostaly zrealizowane zgodnie z wymaganiami kursu.
