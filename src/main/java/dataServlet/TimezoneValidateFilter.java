package dataServlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = "/time")
public class TimezoneValidateFilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;

        String timezone = request.getParameter("timezone");

        if (timezone != null) {
            int zone = 0;
            String substring = timezone.substring(3);
            if (substring.startsWith(" ")) {
                substring = substring.substring(1);
            }
            try {
                zone = Integer.parseInt(substring);
            } catch (NumberFormatException ex) {
                resp.setStatus(400);
                resp.getWriter().write("Invalid timezone");
                resp.getWriter().close();
            }
            if (zone < -12 || zone > 12) {
                resp.setStatus(400);
                resp.getWriter().write("Invalid timezone");
                resp.getWriter().close();
            }
        }
        chain.doFilter(request, resp);
    }
}
