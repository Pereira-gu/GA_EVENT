package com.unicid.ga.event.user;

import com.unicid.ga.event.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth") // Endpoint Base /auth
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;


//  Endpoint para logar ->/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(request.getEmail());

        // se email n existe ao tentar logar, mensagem credencial invalida
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        Usuario usuario = usuarioOpt.get();

        // Validação da senha
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        // caso sucesso retorna o objeto do usuário logado
        return ResponseEntity.ok(usuario);

    }// fim do endpoint login

//  Endpoint para registrar usuario -> /auth/registrar
    @PostMapping("registrar")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDTO request) {
        try {
            Optional<Usuario> usuarioExistente = usuarioService.buscarPorEmail(request.getEmail());
            // se o usuario ja existe(email) retorna mensagem generica, segurança contra brute force de password ao descobrir o email do user
            if (usuarioExistente.isPresent()) {

                // Resposta ambígua para ocultar a existência do e-mail
                return ResponseEntity.status(HttpStatus.CREATED).body("Credenciais inválidas");
            }
            // Criando novo objeto -> preenche as info...
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(request.getNome());
            novoUsuario.setEmail(request.getEmail());
            novoUsuario.setSenha(request.getSenha()); // a criptografia é tratada no service
            novoUsuario.setPerfil(request.getPerfil());
            novoUsuario.setBadgeOuro(false); // false por padrão

            usuarioService.cadastrarUsuario(novoUsuario); // Persiste o usuário no banco de dados

            return ResponseEntity.status(HttpStatus.CREATED).body("Registro solicitado com sucesso."); // Retorna o status HTTP 201 com confirmação

        } catch (Exception e) {
//          ERRORS respostas
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao processar a requisição.");
        }
    }
}

/**
 * DTO para encapsular os dados da requisição de Login.
 */
class LoginRequestDTO {
    private String email;
    private String senha;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}

/**
 * DTO para encapsular os dados da requisição de Registro.
 */
class UsuarioDTO {
    private String nome;
    private String email;
    private String senha;
    private PerfilUsuario perfil;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }
}