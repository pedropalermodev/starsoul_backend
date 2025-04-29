package br.com.itb.project.starsoul.security;

import br.com.itb.project.starsoul.model.Usuario;
import br.com.itb.project.starsoul.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuário no banco de dados (com tratamento case insensitive)
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        // Retorna o UserDetails que o Spring Security precisa
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenhaHash()) // Certifique-se que a senha está codificada
                .roles(usuario.getTipoConta()) // Ex: "Administrador" vira "ROLE_ADMINISTRADOR"
                .build();
    }
}