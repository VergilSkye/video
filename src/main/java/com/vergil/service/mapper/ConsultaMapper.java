package com.vergil.service.mapper;

import com.vergil.domain.*;
import com.vergil.service.dto.ConsultaDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Consulta} and its DTO {@link ConsultaDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ConsultaMapper extends EntityMapper<ConsultaDTO, Consulta> {

    @Mapping(source = "psicologo_id.id", target = "psicologo_idId")
    @Mapping(source = "psicologo_id.login", target = "psicologo_idLogin")
    @Mapping(source = "paciente_id.id", target = "paciente_idId")
    @Mapping(source = "paciente_id.login", target = "paciente_idLogin")
    ConsultaDTO toDto(Consulta consulta);

    @Mapping(source = "psicologo_idId", target = "psicologo_id")
    @Mapping(source = "paciente_idId", target = "paciente_id")
    Consulta toEntity(ConsultaDTO consultaDTO);

    default Consulta fromId(Long id) {
        if (id == null) {
            return null;
        }
        Consulta consulta = new Consulta();
        consulta.setId(id);
        return consulta;
    }
}
