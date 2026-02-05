package com.forestplus.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Configuration
@Slf4j
@Profile("dev") // solo se aplica en development
@Order(1)      // se ejecuta antes que cualquier otra cosa
public class SimplePasswordFilter implements Filter {

    private static final String PASSWORD = "clave"; // 🔐 tu contraseña de prueba

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession();

        String path = request.getRequestURI();
        
        log.info("[doFilter] Path request: {}", path);

        // ✅ NO proteger la API
        if (path.startsWith("/api/")) {
            chain.doFilter(req, res);
            return;
        }

        // Si ya pasó el candado, deja pasar
        Boolean authorized = (Boolean) session.getAttribute("AUTHORIZED");
        if (authorized != null && authorized) {
            chain.doFilter(req, res);
            return;
        }

        // Si envían la contraseña por POST, comprueba
        if ("POST".equalsIgnoreCase(request.getMethod())
                && request.getParameter("password") != null) {

            if (PASSWORD.equals(request.getParameter("password"))) {
                session.setAttribute("AUTHORIZED", true);
                response.sendRedirect(request.getRequestURI());
                return;
            }
        }

        // Mostrar formulario
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(
            "<html><body style='font-family:sans-serif;display:flex;flex-direction:column;align-items:center;margin-top:100px'>" +
            "<h2>Entorno de pruebas</h2>" +
            "<form method='post'>" +
            "<input type='password' name='password' placeholder='Introduce la contraseña'/>" +
            "<button type='submit'>Entrar</button>" +
            "</form></body></html>"
        );
    }
}
