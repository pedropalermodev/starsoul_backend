package br.com.itb.project.starsoul.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URL;
import java.security.interfaces.ECPublicKey;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Value("${jwt.jwks-url}")
    private String jwksUrl;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private JwkProvider jwkProvider;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        try {
            this.jwkProvider = new UrlJwkProvider(new URL(jwksUrl));
        } catch (Exception e) {
            throw new ServletException("Erro ao inicializar JWKS Provider com URL: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

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

        String token = authorizationHeader.substring("Bearer ".length()).trim();

        DecodedJWT decodedJWT = null;
        try {
            DecodedJWT jwtWithoutSignature = JWT.decode(token);
            String keyId = jwtWithoutSignature.getKeyId();
            String algorithm = jwtWithoutSignature.getAlgorithm();

            if (!"ES256".equals(algorithm) || keyId == null) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, "invalid_token", "Token inválido: Algoritmo não suportado ou Key ID ausente.");
                return;
            }

            Jwk jwk = jwkProvider.get(keyId);

            if (!(jwk.getPublicKey() instanceof ECPublicKey)) {
                throw new JwkException("Chave pública inválida para o algoritmo ES256.");
            }

            Algorithm es256Algorithm = Algorithm.ECDSA256((ECPublicKey) jwk.getPublicKey(), null);
            JWTVerifier es256Verifier = JWT.require(es256Algorithm).build();
            decodedJWT = es256Verifier.verify(token);

        } catch (JwkException e) {
            logger.error("Erro ao obter chave JWKS ou validar token ES256: " + e.getMessage(), e);
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "invalid_token", "Falha na validação da chave do token.");
            return;
        } catch (TokenExpiredException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "expired_token", "Token expirado: " + e.getMessage());
            return;
        } catch (JWTVerificationException e) {
            logger.error("Erro de verificação JWT: " + e.getMessage(), e);
            sendError(response, HttpServletResponse.SC_FORBIDDEN, "invalid_token", "Token inválido: " + e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Erro inesperado ao processar token JWT", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "server_error", "Erro interno no servidor");
            return;
        }

        try {
            String username = decodedJWT.getSubject();

            if (username == null) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, "invalid_token", "Token inválido: subject ausente");
                return;
            }

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (UsernameNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "user_not_found", "Usuário não encontrado: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao configurar autenticação após validação do token", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "server_error", "Erro interno no servidor");
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
