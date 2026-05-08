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
        if (usuarioRepository.findByEmail("admin@gmail.com").isEmpty()) {
            Usuario organizador = new Usuario();
            organizador.setNome("Admin Organizador");
            organizador.setEmail("admin@gmail.com");
            organizador.setSenha(passwordEncoder.encode("senha123")); 
            organizador.setPerfil(PerfilUsuario.ORGANIZADOR);
            usuarioRepository.save(organizador);
        }

        if (usuarioRepository.findByEmail("porteiro@gmail.com").isEmpty()) {
            Usuario porteiro = new Usuario();
            porteiro.setNome("João Porteiro");
            porteiro.setEmail("porteiro@gmail.com");
            porteiro.setSenha(passwordEncoder.encode("senha123"));
            porteiro.setPerfil(PerfilUsuario.PORTEIRO);
            usuarioRepository.save(porteiro);
        }

        if (usuarioRepository.findByEmail("aluno@gmail.com").isEmpty()) {
            Usuario aluno = new Usuario();
            aluno.setNome("Maria Aluna");
            aluno.setEmail("aluno@gmail.com");
            aluno.setSenha(passwordEncoder.encode("senha123"));
            aluno.setPerfil(PerfilUsuario.CLIENTE);
            usuarioRepository.save(aluno);
            
            System.out.println("Usuários de teste injetados com sucesso (ignorando os já existentes no Supabase)!");
        }

        // Se quiser injetar eventos de teste independentemente dos dados do Supabase
        // Comentado para evitar flood de eventos a cada restart. 
        // Descomente se quiser forçar a criação de eventos.
        /*
        if (eventoRepository.count() < 5) { // Só cria se tiver poucos eventos
            Evento evento1 = new Evento(
                    "Palestra de Java e Spring Boot",
                    "Auditório Principal",
                    LocalDateTime.now().plusDays(5).withHour(19).withMinute(0).withSecond(0),
                    120
            );
            eventoRepository.save(evento1);

            System.out.println("Eventos de teste criados com sucesso!");
        }
        */
    }
}