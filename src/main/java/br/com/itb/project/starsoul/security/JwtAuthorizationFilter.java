package br.com.itb.project.starsoul.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Permite requisições OPTIONS para CORS pré-flight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length());

        try {
            // Validação do token
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);

            // Verificação de expiração
            if (decodedJWT.getExpiresAt().before(new Date())) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "expired_token", "Token expirado");
                return;
            }

            String username = decodedJWT.getSubject();

            if (username == null) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN,
                        "invalid_token", "Token inválido: subject ausente");
                return;
            }

            // Carrega o usuário
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (UsernameNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "user_not_found", "Usuário não encontrado: " + e.getMessage());

        } catch (com.auth0.jwt.exceptions.TokenExpiredException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "expired_token", "Token expirado: " + e.getMessage());

        } catch (com.auth0.jwt.exceptions.JWTVerificationException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "invalid_token", "Token inválido: " + e.getMessage());

        } catch (Exception e) {
            logger.error("Erro ao processar token JWT", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "server_error", "Erro interno no servidor");
        }
    }

    private void sendError(HttpServletResponse response, int status,
                           String errorCode, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format("{\"error\":\"%s\",\"message\":\"%s\"}",
                        errorCode, message));
        response.getWriter().flush();
    }
}