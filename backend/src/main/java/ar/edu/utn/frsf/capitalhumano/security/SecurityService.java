package ar.edu.utn.frsf.capitalhumano.security;

import ar.edu.utn.frsf.capitalhumano.dto.CandidatoDTO;
import ar.edu.utn.frsf.capitalhumano.dto.ComunDTO;
import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import ar.edu.utn.frsf.capitalhumano.model.Consultor;
import ar.edu.utn.frsf.capitalhumano.model.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.model.enums.Rol;
import ar.edu.utn.frsf.capitalhumano.repository.ConsultorRepository;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;
import ar.edu.utn.frsf.capitalhumano.service.CandidatoService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Component("securityService")
public class SecurityService {

    private final CuestionarioRepository cuestionarioRepository;
    private final ConsultorRepository consultorRepository;
    private final CandidatoService candidatoService;
    private final LdapService ldapService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public SecurityService(CuestionarioRepository cuestionarioRepository,
                           ConsultorRepository consultorRepository,
                           CandidatoService candidatoService,
                           LdapService ldapService,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.cuestionarioRepository = cuestionarioRepository;
        this.consultorRepository = consultorRepository;
        this.candidatoService = candidatoService;
        this.ldapService = ldapService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Autentica un consultor contra LDAP y devuelve token + datos del consultor
    @Transactional
    public Map<String, Object> autenticarConsultor(String usuario, String contrasenia) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario, contrasenia));

        Consultor consultor = consultorRepository.findByUsername(usuario).orElseGet(() -> {
            Consultor nuevo = new Consultor();
            nuevo.setUsername(usuario);
            return consultorRepository.save(nuevo);
        });

        String accessToken = jwtService.generarToken(consultor.getUsername(), Rol.CONSULTANT.name());

        return Map.of(
                "token", accessToken,
                "cookie", jwtService.generarJwtCookie(accessToken).toString(),
                "username", consultor.getUsername()
        );
    }

    // nombrePrincipal para el candidato es su documento, para el consultor es su username de LDAP
    public Object obtenerPerfilUsuario(String nombrePrincipal, String rol) {
        String rolLimpio = rol.replace("ROLE_", "");

        if (rolLimpio.equals(Rol.CANDIDATE.name()) || rolLimpio.equals("CANDIDATO")) {
            Candidato candidato = candidatoService.buscarPorNumeroDocumento(nombrePrincipal);
            return CandidatoDTO.Perfil.from(candidato, nombrePrincipal, rolLimpio);
        } else {
            Map<String, Object> ldapData = ldapService.obtenerPerfilUsuario(nombrePrincipal);
            if (ldapData == null) {
                throw new RuntimeException("Usuario no encontrado en LDAP");
            }

            String nombre = (String) ldapData.get("name");
            String apellido = (String) ldapData.get("lastname");
            String legajo = (String) ldapData.get("legajo");

            return new ComunDTO.PerfilConsultor(nombrePrincipal, rolLimpio, nombre, apellido, legajo);
        }
    }

    // Validamos que el candidato que está haciendo la petición sea el dueño del cuestionario
    @Transactional(readOnly = true)
    public boolean esPropietarioCuestionario(Long idCuestionario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        // Obtenemos el documento del candidato desde el token JWT
        String candidatoDocDesdeToken = auth.getName();

        return cuestionarioRepository.findById(idCuestionario)
                .map(q -> {
                    String actualDoc = q.getCandidato().getNumeroDocumento();
                    return candidatoDocDesdeToken.equalsIgnoreCase(actualDoc) || candidatoDocDesdeToken.endsWith(actualDoc);
                })
                .orElse(false);
    }

    @Transactional
    public Map<String, Object> autenticarCandidato(CandidatoDTO.IniciarSesion peticion) {
        String claveLimpia = peticion.claveAcceso() != null ? peticion.claveAcceso().trim() : "";

        // Buscamos el cuestionario por la clave
        Cuestionario cuestionario = cuestionarioRepository.findByClaveAcceso(claveLimpia)
                .or(() -> cuestionarioRepository.findByClaveAcceso(claveLimpia.toUpperCase()))
                .orElseThrow(() -> new RuntimeException("Código de acceso inválido. Verifique e intente nuevamente."));

        if (cuestionario.getEstado() == EstadoCuestionario.COMPLETED) {
            throw new RuntimeException("Este cuestionario ya fue completado.");
        }

        if (cuestionario.getEstado() == EstadoCuestionario.INCOMPLETE ||
                cuestionario.getEstado() == EstadoCuestionario.NOT_ANSWERED) {
            throw new RuntimeException("El plazo para completar este cuestionario ha finalizado.");
        }

        if (cuestionario.getEvaluacion().getFechaCierre() != null &&
                LocalDateTime.now().isAfter(cuestionario.getEvaluacion().getFechaCierre())) {
            throw new RuntimeException("El período de evaluación ha concluido.");
        }

        // El candidato se identifica en el sistema mediante su número de documento
        String token = jwtService.generarToken(cuestionario.getCandidato().getNumeroDocumento(), Rol.CANDIDATE.name());

        return Map.of(
                "token", token,
                "idCuestionario", cuestionario.getId(),
                "nombre", cuestionario.getCandidato().getNombre(),
                "apellido", cuestionario.getCandidato().getApellido());
    }
}
