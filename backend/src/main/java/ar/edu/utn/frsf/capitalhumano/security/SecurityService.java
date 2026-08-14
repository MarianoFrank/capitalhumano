package ar.edu.utn.frsf.capitalhumano.security;

import ar.edu.utn.frsf.capitalhumano.dto.request.CandidatoLoginRequest;
import ar.edu.utn.frsf.capitalhumano.dto.response.PerfilCandidatoResponse;
import ar.edu.utn.frsf.capitalhumano.dto.response.PerfilConsultorResponse;
import ar.edu.utn.frsf.capitalhumano.model.Candidato;
import ar.edu.utn.frsf.capitalhumano.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf.capitalhumano.model.cuestionario.Cuestionario;
import ar.edu.utn.frsf.capitalhumano.repository.CuestionarioRepository;
import ar.edu.utn.frsf.capitalhumano.service.CandidatoService;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("securityService")
public class SecurityService {

    private final CuestionarioRepository cuestionarioRepository;
    private final CandidatoService candidatoService;
    private final LdapService ldapService;
    private final JwtService jwtService;

    public SecurityService(CuestionarioRepository cuestionarioRepository, CandidatoService candidatoService,
            LdapService ldapService, JwtService jwtService) {
        this.cuestionarioRepository = cuestionarioRepository;
        this.candidatoService = candidatoService;
        this.ldapService = ldapService;
        this.jwtService = jwtService;
    }

    // nombrePrincipal para el candidato es su documento, para el consultor es su username de LDAP
    public Object obtenerPerfilUsuario(String nombrePrincipal, String rol) {
        String rolLimpio = rol.replace("ROLE_", "");

        if (rolLimpio.equals("CANDIDATE") || rolLimpio.equals("CANDIDATO")) {
            Candidato candidato = candidatoService.buscarPorNumeroDocumento(nombrePrincipal);

            return new PerfilCandidatoResponse(nombrePrincipal, rolLimpio, candidato.getNombre(), candidato.getApellido());
        } else {
            Map<String, Object> ldapData = ldapService.obtenerPerfilUsuario(nombrePrincipal);
            if (ldapData == null) {
                throw new RuntimeException("Usuario no encontrado en LDAP");
            }

            String nombre = (String) ldapData.get("name");
            String apellido = (String) ldapData.get("lastname");
            String legajo = (String) ldapData.get("legajo");

            return new PerfilConsultorResponse(nombrePrincipal, rolLimpio, nombre, apellido, legajo);
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
                    return candidatoDocDesdeToken.endsWith(actualDoc);
                })
                .orElse(false);
    }

    @Transactional
    public Map<String, Object> autenticarCandidato(CandidatoLoginRequest peticion) {
        // Buscamos el cuestionario por la clave
        Cuestionario cuestionario = cuestionarioRepository.findByAccessKey(peticion.accessCode())
                .orElseThrow(() -> new RuntimeException("Código de acceso inválido. Verifique e intente nuevamente."));

        if (cuestionario.getEstado() == EstadoCuestionario.COMPLETED) {
            throw new RuntimeException("Este cuestionario ya fue completado.");
        }

        if (cuestionario.getEstado() == EstadoCuestionario.INCOMPLETE ||
                cuestionario.getEstado() == EstadoCuestionario.NOT_ANSWERED) {
            throw new RuntimeException("El plazo para completar este cuestionario ha finalizado.");
        }

        if (LocalDateTime.now().isAfter(cuestionario.getEvaluacion().getFechaCierre())) {
            throw new RuntimeException("La evaluación general para este puesto ya se encuentra cerrada.");
        }

        // Generamos un JWT para el candidato
        String candidateUsername = cuestionario.getCandidato().getNumeroDocumento();
        String token = jwtService.generarToken(candidateUsername, "CANDIDATE");

        return Map.of(
                "token", token,
                "questionnaireId", cuestionario.getId());
    }
}
