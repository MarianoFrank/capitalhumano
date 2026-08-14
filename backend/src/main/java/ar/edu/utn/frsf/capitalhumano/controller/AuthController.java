package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.request.CandidatoLoginRequest;
import ar.edu.utn.frsf.capitalhumano.dto.response.CandidatoLoginResponse;
import ar.edu.utn.frsf.capitalhumano.model.Consultor;
import ar.edu.utn.frsf.capitalhumano.repository.ConsultorRepository;
import ar.edu.utn.frsf.capitalhumano.security.JwtService;
import ar.edu.utn.frsf.capitalhumano.security.SecurityService;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autenticacion")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SecurityService securityService;
    private final ConsultorRepository consultorRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
            ConsultorRepository consultorRepository,
            SecurityService securityService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.securityService = securityService;
        this.consultorRepository = consultorRepository;
    }

    public record IniciarSesionRequest(
            @JsonAlias({"username", "user"}) String usuario,
            @JsonAlias({"password", "clave", "pass"}) String contrasenia) {
    }

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<?> iniciarSesion(@RequestBody IniciarSesionRequest request) {
        String username = request.usuario();
        String password = request.contrasenia();

        if (username == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Debe proporcionar usuario y contraseña"));
        }

        try {
            // Validamos credenciales contra LDAP
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario o contraseña incorrectos"));
        }

        // JIT PROVISIONING: Buscamos al consultor en la BD local. Si no existe, lo creamos
        Consultor consultant = consultorRepository.findByUsername(username).orElseGet(() -> {
            Consultor newConsultant = new Consultor();
            newConsultant.setUsername(username);
            return consultorRepository.save(newConsultant);
        });

        String accessToken = jwtService.generarToken(consultant.getUsername(), "CONSULTANT");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        jwtService.generarJwtCookie(accessToken).toString())
                .body(Map.of("message", "Consultor autenticado correctamente", "user",
                        Map.of("username", consultant.getUsername(), "role", "CONSULTANT")));
    }

    @PostMapping("/cerrar-sesion")
    public ResponseEntity<?> cerrarSesion() {
        String cleanCookie = jwtService.obtenerCleanJwtCookie().toString();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cleanCookie)
                .body(Map.of("message", "Sesión cerrada"));
    }

    @GetMapping("/perfil")
    public ResponseEntity<Object> obtenerPerfil(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }

        String principalName = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        Object profile = securityService.obtenerPerfilUsuario(principalName, role);

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/candidato/iniciar-sesion")
    public ResponseEntity<CandidatoLoginResponse> iniciarSesionCandidato(@RequestBody CandidatoLoginRequest request) {
        try {
            Map<String, Object> response = securityService.autenticarCandidato(request);

            Long questionnaireId = (Long) response.get("questionnaireId");
            String token = (String) response.get("token");

            CandidatoLoginResponse candidateLoginResponse = new CandidatoLoginResponse(questionnaireId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtService.generarJwtCookie(token).toString())
                    .body(candidateLoginResponse);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
