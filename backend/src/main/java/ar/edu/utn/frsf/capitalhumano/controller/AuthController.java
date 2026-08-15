package ar.edu.utn.frsf.capitalhumano.controller;

import ar.edu.utn.frsf.capitalhumano.dto.CandidatoDTO;
import ar.edu.utn.frsf.capitalhumano.model.Consultor;
import ar.edu.utn.frsf.capitalhumano.repository.ConsultorRepository;
import ar.edu.utn.frsf.capitalhumano.security.JwtService;
import ar.edu.utn.frsf.capitalhumano.security.SecurityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/autenticacion")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SecurityService securityService;
    private final ConsultorRepository consultorRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          ConsultorRepository consultorRepository,
                          SecurityService securityService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.securityService = securityService;
        this.consultorRepository = consultorRepository;
    }

    public record IniciarSesion(
            String usuario,
            String contrasenia
    ) {}

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<?> iniciarSesion(@Valid @RequestBody IniciarSesion peticion) {
        if (peticion.usuario() == null || peticion.contrasenia() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Debe proporcionar usuario y contraseña"));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(peticion.usuario(), peticion.contrasenia()));
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario o contraseña incorrectos"));
        }

        Consultor consultor = consultorRepository.findByUsername(peticion.usuario()).orElseGet(() -> {
            Consultor nuevo = new Consultor();
            nuevo.setUsername(peticion.usuario());
            return consultorRepository.save(nuevo);
        });

        String accessToken = jwtService.generarToken(consultor.getUsername(), "CONSULTANT");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtService.generarJwtCookie(accessToken).toString())
                .body(Map.of(
                        "message", "Consultor autenticado correctamente",
                        "user", Map.of("username", consultor.getUsername(), "role", "CONSULTANT")
                ));
    }

    @PostMapping("/cerrar-sesion")
    public ResponseEntity<?> cerrarSesion() {
        String cleanCookie = jwtService.obtenerCleanJwtCookie().toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie)
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
    public ResponseEntity<CandidatoDTO.SesionIniciada> iniciarSesionCandidato(@Valid @RequestBody CandidatoDTO.IniciarSesion peticion) {
        try {
            Map<String, Object> response = securityService.autenticarCandidato(peticion);
            Long idCuestionario = (Long) response.get("idCuestionario");
            String token = (String) response.get("token");

            CandidatoDTO.SesionIniciada body = new CandidatoDTO.SesionIniciada(idCuestionario);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtService.generarJwtCookie(token).toString())
                    .body(body);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
