package controller;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @BeforeAll
    void start(){
        usuarioRepository.deleteAll();

        usuarioService.cadastrarUsuario(new Usuario(0L,"Root","root@root.com","",null));

    }

    @Test
    @DisplayName("Vou cadastrar um usuário")
    public void deveCriarUmUsuario(){

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,"Anderson Santos","anderson@gmail.com","12345678","-"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST,corpoRequisicao,Usuario.class);

        assertEquals(HttpStatus.CREATED,corpoResposta.getStatusCode());

    }

    @Test
    @DisplayName("Verificar a duplicação do usuário")
    public void naoDeveDuplicarUsuario(){
        usuarioService.cadastrarUsuario(new Usuario(0L,"Mauricio Freire","mauriciofreire@gmail.com","12345678",null));
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,"Mauricio Freire","mauriciofreire@gmail.com","12345678",null));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST,corpoRequisicao,Usuario.class);
        assertEquals(HttpStatus.BAD_REQUEST,corpoResposta.getStatusCode());
    }

    @Test
    @DisplayName("Atualizar um Usuário")
    public void deveAtualizarUmUsuario() {

        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
                "Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));

        Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
                "Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123" , "-");

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

    }

    @Test
    @DisplayName("Listar todos os Usuários")
    public void deveMostrarTodosUsuarios() {

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));

        usuarioService.cadastrarUsuario(new Usuario(0L,
                "Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());

    }


}
