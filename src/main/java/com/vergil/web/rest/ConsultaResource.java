package com.vergil.web.rest;

import com.vergil.service.ConsultaService;
import com.vergil.web.rest.errors.BadRequestAlertException;
import com.vergil.service.dto.ConsultaDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.vergil.domain.Consulta}.
 */
@RestController
@RequestMapping("/api")
public class ConsultaResource {

    private final Logger log = LoggerFactory.getLogger(ConsultaResource.class);

    private static final String ENTITY_NAME = "consulta";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConsultaService consultaService;

    public ConsultaResource(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    /**
     * {@code POST  /consultas} : Create a new consulta.
     *
     * @param consultaDTO the consultaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new consultaDTO, or with status {@code 400 (Bad Request)} if the consulta has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/consultas")
    public ResponseEntity<ConsultaDTO> createConsulta(@RequestBody ConsultaDTO consultaDTO) throws URISyntaxException {
        log.debug("REST request to save Consulta : {}", consultaDTO);
        if (consultaDTO.getId() != null) {
            throw new BadRequestAlertException("A new consulta cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ConsultaDTO result = consultaService.save(consultaDTO);
        return ResponseEntity.created(new URI("/api/consultas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /consultas} : Updates an existing consulta.
     *
     * @param consultaDTO the consultaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated consultaDTO,
     * or with status {@code 400 (Bad Request)} if the consultaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the consultaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/consultas")
    public ResponseEntity<ConsultaDTO> updateConsulta(@RequestBody ConsultaDTO consultaDTO) throws URISyntaxException {
        log.debug("REST request to update Consulta : {}", consultaDTO);
        if (consultaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ConsultaDTO result = consultaService.save(consultaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, consultaDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /consultas} : get all the consultas.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of consultas in body.
     */
    @GetMapping("/consultas")
    public ResponseEntity<List<ConsultaDTO>> getAllConsultas(Pageable pageable) {
        log.debug("REST request to get a page of Consultas");
        Page<ConsultaDTO> page = consultaService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /consultas/:id} : get the "id" consulta.
     *
     * @param id the id of the consultaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the consultaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/consultas/{id}")
    public ResponseEntity<ConsultaDTO> getConsulta(@PathVariable Long id) {
        log.debug("REST request to get Consulta : {}", id);
        Optional<ConsultaDTO> consultaDTO = consultaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(consultaDTO);
    }

    /**
     * {@code DELETE  /consultas/:id} : delete the "id" consulta.
     *
     * @param id the id of the consultaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/consultas/{id}")
    public ResponseEntity<Void> deleteConsulta(@PathVariable Long id) {
        log.debug("REST request to delete Consulta : {}", id);
        consultaService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
