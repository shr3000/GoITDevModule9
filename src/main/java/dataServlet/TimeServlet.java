package dataServlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

@WebServlet(urlPatterns = "/time*")
public class TimeServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    @Override
    public void init() {
        templateEngine = new TemplateEngine();

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(templateEngine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        templateEngine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Content-Type", "text/html; charset=utf-8");
        Map<String, Object> params = new LinkedHashMap<>();
        Integer zone = getZone(req);

        String stringDateFormat = getDataTostring(zone, new Date());
        params.put("data", stringDateFormat);
        params.put("timeZone", zone);

        Context context = new Context(req.getLocale(), Map.of("queryParams", params));
        if (zone != null)
            resp.addCookie(new Cookie("lastTimezone", zone.toString()));

        context.setVariable("queryParams", params);
        templateEngine.process("timeTemplate", context, resp.getWriter());
        resp.getWriter().close();

    }

    private String getDataTostring(int zone, Date date) {
        if (zone >= -12 && zone <= 12) {
            date = new Date(date.getTime() + zone * 3600000);
        }
        DateFormat format = new SimpleDateFormat("E, dd MMMM yyyy zzzz");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.out.println(format.format(date));
        return format.format(date);
    }

    private Integer getZone(HttpServletRequest req) {
        System.out.println("getZone method");
        String timezone = req.getParameter("timezone");
        if (timezone != null) {
            String substring = timezone.substring(3);
            if (substring.startsWith(" ")) {
                substring = substring.substring(1);
                return Integer.parseInt(substring);
            } else {
                Cookie[] cookies = req.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("lastTimezone")) {
                            return Integer.parseInt(cookie.getValue());
                        }
                    }
                }

            }

        }
        return 0;
    }



}
