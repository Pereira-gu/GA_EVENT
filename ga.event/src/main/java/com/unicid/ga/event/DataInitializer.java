package com.unicid.ga.event;

import com.unicid.ga.event.evento.Evento;
import com.unicid.ga.event.evento.EventoRepository;
import com.unicid.ga.event.user.PerfilUsuario;
import com.unicid.ga.event.user.Usuario;
import com.unicid.ga.event.user.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verifica especificamente se os usuários de teste existem pelo email
        if (usuarioRepository.findByEmail("admin@unicid.br").isEmpty()) {
            Usuario organizador = new Usuario();
            organizador.setNome("Admin Organizador");
            organizador.setEmail("admin@unicid.br");
            organizador.setSenha(passwordEncoder.encode("senha123")); 
            organizador.setPerfil(PerfilUsuario.ORGANIZADOR);
            usuarioRepository.save(organizador);
        }

        if (usuarioRepository.findByEmail("porteiro@unicid.br").isEmpty()) {
            Usuario porteiro = new Usuario();
            porteiro.setNome("João Porteiro");
            porteiro.setEmail("porteiro@unicid.br");
            porteiro.setSenha(passwordEncoder.encode("senha123"));
            porteiro.setPerfil(PerfilUsuario.PORTEIRO);
            usuarioRepository.save(porteiro);
        }

        if (usuarioRepository.findByEmail("aluno@unicid.br").isEmpty()) {
            Usuario aluno = new Usuario();
            aluno.setNome("Maria Aluna");
            aluno.setEmail("aluno@unicid.br");
            aluno.setSenha(passwordEncoder.encode("senha123"));
            aluno.setPerfil(PerfilUsuario.CLIENTE);
            usuarioRepository.save(aluno);
            
            System.out.println("Usuários de teste injetados com sucesso (ignorando os já existentes no Supabase)!");
        }
    }
}