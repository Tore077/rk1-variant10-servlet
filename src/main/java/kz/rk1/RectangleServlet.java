package kz.rk1;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// РК1, 10-нұсқа: «Тікбұрыш» — ені мен биіктігі бойынша аудан мен периметрді есептеу.
@WebServlet("/rectangle")
public class RectangleServlet extends HttpServlet {

    // GET: бос форманы көрсетеді.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        render(req, resp, null, false);
    }

    // POST: формадан келген мәндерді оқып, есептейді.
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String widthText = req.getParameter("width");
        String heightText = req.getParameter("height");

        if (widthText == null || widthText.isBlank() || heightText == null || heightText.isBlank()) {
            render(req, resp, "Қате: ені мен биіктігін енгізіңіз", true);
            return;
        }

        double width;
        double height;
        try {
            width = Double.parseDouble(widthText.trim().replace(',', '.'));
            height = Double.parseDouble(heightText.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            render(req, resp, "Қате: сан енгізіңіз", true);
            return;
        }
        if (!Double.isFinite(width) || !Double.isFinite(height)) {
            render(req, resp, "Қате: сан енгізіңіз", true);
            return;
        }

        // Теріс немесе нөл мән — тапсырма шарты бойынша қате.
        if (width <= 0 || height <= 0) {
            render(req, resp, "мән оң болуы тиіс", true);
            return;
        }

        double area = width * height;
        double perimeter = 2 * (width + height);
        if (!Double.isFinite(area) || !Double.isFinite(perimeter)) {
            render(req, resp, "Қате: сан тым үлкен", true);
            return;
        }
        String result = "Ені: " + format(width) + ", биіктігі: " + format(height)
                + "<br>Ауданы: S = a × b = " + format(area)
                + "<br>Периметрі: P = 2 × (a + b) = " + format(perimeter);
        render(req, resp, result, false);
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, String message, boolean error)
            throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String ctx = req.getContextPath();
        out.println("<!doctype html><html lang='kk'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1'>"
                + "<title>Тікбұрыш</title><link rel='stylesheet' href='" + ctx + "/style.css'></head>");
        out.println("<body><main><nav><a href='" + ctx + "/'>Басты бет</a></nav>");
        out.println("<h1>Тікбұрыш: аудан және периметр</h1>");
        out.println("<form action='" + ctx + "/rectangle' method='post'>"
                + "<label>Ені (a) <input name='width' value='" + esc(req.getParameter("width")) + "'></label>"
                + "<label>Биіктігі (b) <input name='height' value='" + esc(req.getParameter("height")) + "'></label>"
                + "<button>Есептеу</button></form>");
        if (message != null) {
            // Нәтиже мәтінін Servlet өзі құрады, ал қате хабарлары тұрақты — пайдаланушы мәтіні мұнда жоқ.
            out.println("<div class='" + (error ? "error" : "result") + "'>" + message + "</div>");
        }
        out.println("</main></body></html>");
    }

    // 12.0 → "12", 2.5 → "2.5"
    private static String format(double v) {
        if (v == Math.rint(v) && Math.abs(v) < 1e15) {
            return String.valueOf((long) v);
        }
        return String.valueOf(Math.round(v * 10000) / 10000.0);
    }

    // XSS-тен қорғау: енгізілген мәнді HTML-ге қауіпсіз түрде шығару.
    private static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
