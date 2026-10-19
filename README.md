# РК1. Servlet — 10-нұсқа

Студент: ______________________   Топ: ___________   Күні: ___________

## Жоба құрылымы

```
rk1-variant10/
├─ src/main/java/kz/rk1/RectangleServlet.java   ← Servlet (@WebServlet("/rectangle"))
├─ src/main/webapp/index.html                   ← «Тікбұрыш» формасы
├─ src/main/webapp/style.css
├─ src/main/webapp/WEB-INF/web.xml              ← welcome-file
├─ pom.xml                                      ← Maven (міндетті емес)
├─ run.ps1 / stop.ps1                           ← компиляция + Tomcat 9 іске қосу/тоқтату
└─ .runtime/                                    ← portable JDK 17 және Tomcat 9
```

## Іске қосу

VS Code ішінде осы қалтаны ашыңыз → Terminal → Run Task → **Run RK1 variant 10**, немесе терминалда:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

Браузерде ашыңыз: http://localhost:8080/rk1/

Тоқтату: терминалда Ctrl+C немесе басқа терминалдан `powershell -ExecutionPolicy Bypass -File .\stop.ps1`.
Ескерту: servlet-labs-1-5 жобасы қатар іске қосулы болса, 8080 порты бос емес — алдымен оны тоқтатыңыз.

## Тексеру (скриндер үшін)

| Ені | Биіктігі | Нәтиже |
|---|---|---|
| 5 | 3 | Ауданы 15, периметрі 16 |
| 2.5 | 4 | Ауданы 10, периметрі 13 |
| 0 | 3 | мән оң болуы тиіс |
| -2 | 4 | мән оң болуы тиіс |
| abc | 3 | Қате: сан енгізіңіз |
| (бос) | 3 | Қате: ені мен биіктігін енгізіңіз |

---

## Б бөлімі. Теориялық сұрақтар

### 1. HTML формасындағы `action` және `method` атрибуттары не үшін қолданылады? Servlet-пен қалай байланысады?

- **`action`** — форма деректері **қай адреске** жіберілетінін көрсетеді. Бұл адрес Servlet-тің url-pattern-іне сәйкес келуі керек. Мысалы, `<form action="rectangle">` формасы `@WebServlet("/rectangle")` (немесе web.xml-дегі `<url-pattern>/rectangle</url-pattern>`) Servlet-іне барады.
- **`method`** — деректер **қалай** жіберілетінін анықтайды: `get` (әдепкі) немесе `post`.
  - `method="get"` → Servlet-тің **`doGet()`** әдісі шақырылады, параметрлер URL-де көрінеді: `/rectangle?width=5&height=3`.
  - `method="post"` → **`doPost()`** әдісі шақырылады, параметрлер сұраныс денесінде жіберіледі.
- Байланыс: браузер `action` адресіне `method` әдісімен сұраныс жібереді → Tomcat url-pattern бойынша керек Servlet-ті табады → `service()` әдісі HTTP әдісіне қарай `doGet()`/`doPost()`-ты шақырады → Servlet `request.getParameter("width")` арқылы өрістерді оқиды. Өрістің `name` атрибуты параметр атауы болып табылады.

Осы жұмыста: `index.html` → `<form action="rectangle" method="post">` → `RectangleServlet.doPost()`.

### 2. Сұраныс параметрі жоқ болғанда `getParameter()` не қайтарады? `null` және бос жол (`""`) айырмашылығы неде?

- Параметр сұраныста **мүлдем жоқ** болса, `getParameter()` **`null`** қайтарады. Мысалы, `/rectangle` адресін параметрсіз ашқанда `getParameter("width") == null`.
- Параметр **бар, бірақ мәні бос** болса — **бос жол `""`** қайтарады. Мысалы, форма өрісі толтырылмай жіберілсе: `/rectangle?width=&height=3` → `getParameter("width")` = `""`.
- Айырмашылығы: `null` — «мұндай өріс жіберілмеген», `""` — «өріс жіберілген, бірақ пайдаланушы ештеңе жазбаған».
- `null` мәнге әдіс шақырсақ (`width.trim()`), **NullPointerException** шығады. Сондықтан алдымен екеуін де тексеру керек:

```java
String w = request.getParameter("width");
if (w == null || w.isBlank()) {
    // өріс жоқ немесе бос — қате хабарын шығару
}
```

`RectangleServlet` дәл осылай тексереді: бос/жоқ өріске «Қате: ені мен биіктігін енгізіңіз» деп жауап береді.

---

## В бөлімі. Практикалық тапсырма — «Тікбұрыш»

Форма ені (a) мен биіктігін (b) қабылдайды. Servlet:
- ауданын **S = a × b** және периметрін **P = 2 × (a + b)** есептейді;
- теріс немесе нөл мән болса **«мән оң болуы тиіс»** деп шығарады;
- сан емес немесе бос мәнге қате хабарын көрсетеді;
- енгізілген мәндерді формада сақтайды (XSS-тен қорғалған).

Servlet коды: `src/main/java/kz/rk1/RectangleServlet.java`.
