import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Dom on 30.10.2014.
 */
public class UserFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //filterChain.doFilter(servletRequest, servletResponse);
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        HttpSession session = ((HttpServletRequest) servletRequest).getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        /*
        TODO: FilterMapping nicht so sicher!
         */
        if (user != null && user.isLoggedIn()) {
            // User is logged in, so just continue request.
            if(user.getRole() != null && req.toString().contains(user.getRole().toString().toLowerCase())){
                filterChain.doFilter(servletRequest,servletResponse);}
            else{
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    public void destroy() {

    }
}
