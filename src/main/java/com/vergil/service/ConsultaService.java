package com.vergil.service;

import com.vergil.domain.Consulta;
import com.vergil.repository.ConsultaRepository;
import com.vergil.service.dto.ConsultaDTO;
import com.vergil.service.mapper.ConsultaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Consulta}.
 */
@Service
@Transactional
public class ConsultaService {

    private final Logger log = LoggerFactory.getLogger(ConsultaService.class);

    private final ConsultaRepository consultaRepository;

    private final ConsultaMapper consultaMapper;

    public ConsultaService(ConsultaRepository consultaRepository, ConsultaMapper consultaMapper) {
        this.consultaRepository = consultaRepository;
        this.consultaMapper = consultaMapper;
    }

    /**
     * Save a consulta.
     *
     * @param consultaDTO the entity to save.
     * @return the persisted entity.
     */
    public ConsultaDTO save(ConsultaDTO consultaDTO) {
        log.debug("Request to save Consulta : {}", consultaDTO);
        Consulta consulta = consultaMapper.toEntity(consultaDTO);
        consulta = consultaRepository.save(consulta);
        return consultaMapper.toDto(consulta);
    }

    /**
     * Get all the consultas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ConsultaDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Consultas");
        return consultaRepository.findAll(pageable)
            .map(consultaMapper::toDto);
    }


    /**
     * Get one consulta by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ConsultaDTO> findOne(Long id) {
        log.debug("Request to get Consulta : {}", id);
        return consultaRepository.findById(id)
            .map(consultaMapper::toDto);
    }

    /**
     * Delete the consulta by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Consulta : {}", id);
        consultaRepository.deleteById(id);
    }
}
