package ca.ralphsplace.djindex;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ClientFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientId = request.getHeader("X-client_id");
        if (clientId == null || clientId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            // TODO: dynamic generation of mongodb url
            String dbConnectionString = "mongodb://mongoDB:27017/"+clientId;
            ClientConnectionString.setClientConnectionStr(dbConnectionString);
            filterChain.doFilter(request,response);
            ClientConnectionString.clear();
        }
    }
}