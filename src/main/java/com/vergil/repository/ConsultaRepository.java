package com.vergil.repository;
import com.vergil.domain.Consulta;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Consulta entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("select consulta from Consulta consulta where consulta.psicologo_id.login = ?#{principal.username}")
    List<Consulta> findByPsicologo_idIsCurrentUser();

    @Query("select consulta from Consulta consulta where consulta.paciente_id.login = ?#{principal.username}")
    List<Consulta> findByPaciente_idIsCurrentUser();

}
