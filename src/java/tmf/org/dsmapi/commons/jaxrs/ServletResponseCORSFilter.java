/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.commons.jaxrs;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author maig7313
 */
public class ServletResponseCORSFilter implements Filter {

    public ServletResponseCORSFilter() {
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(
            ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {   

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader(
                "Access-Control-Allow-Origin", "*");
        httpResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String reqHead = httpRequest.getHeader("Access-Control-Request-Headers");

        if (null != reqHead && !reqHead.equals(null)) {
            httpResponse.addHeader("Access-Control-Allow-Headers", reqHead);
        }        
        
        chain.doFilter(request, response);
    }
}