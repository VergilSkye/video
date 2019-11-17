package com.vergil.web.rest;

import com.vergil.TestApp;
import com.vergil.domain.Consulta;
import com.vergil.repository.ConsultaRepository;
import com.vergil.service.ConsultaService;
import com.vergil.service.dto.ConsultaDTO;
import com.vergil.service.mapper.ConsultaMapper;
import com.vergil.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static com.vergil.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ConsultaResource} REST controller.
 */
@SpringBootTest(classes = TestApp.class)
public class ConsultaResourceIT {

    private static final UUID DEFAULT_UUID = UUID.randomUUID();
    private static final UUID UPDATED_UUID = UUID.randomUUID();

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ENDED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ENDED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Duration DEFAULT_DURATION = Duration.ofHours(6);
    private static final Duration UPDATED_DURATION = Duration.ofHours(12);

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ConsultaMapper consultaMapper;

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restConsultaMockMvc;

    private Consulta consulta;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ConsultaResource consultaResource = new ConsultaResource(consultaService);
        this.restConsultaMockMvc = MockMvcBuilders.standaloneSetup(consultaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Consulta createEntity(EntityManager em) {
        Consulta consulta = new Consulta()
            .uuid(DEFAULT_UUID)
            .created_at(DEFAULT_CREATED_AT)
            .ended_at(DEFAULT_ENDED_AT)
            .duration(DEFAULT_DURATION);
        return consulta;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Consulta createUpdatedEntity(EntityManager em) {
        Consulta consulta = new Consulta()
            .uuid(UPDATED_UUID)
            .created_at(UPDATED_CREATED_AT)
            .ended_at(UPDATED_ENDED_AT)
            .duration(UPDATED_DURATION);
        return consulta;
    }

    @BeforeEach
    public void initTest() {
        consulta = createEntity(em);
    }

    @Test
    @Transactional
    public void createConsulta() throws Exception {
        int databaseSizeBeforeCreate = consultaRepository.findAll().size();

        // Create the Consulta
        ConsultaDTO consultaDTO = consultaMapper.toDto(consulta);
        restConsultaMockMvc.perform(post("/api/consultas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(consultaDTO)))
            .andExpect(status().isCreated());

        // Validate the Consulta in the database
        List<Consulta> consultaList = consultaRepository.findAll();
        assertThat(consultaList).hasSize(databaseSizeBeforeCreate + 1);
        Consulta testConsulta = consultaList.get(consultaList.size() - 1);
        assertThat(testConsulta.getUuid()).isEqualTo(DEFAULT_UUID);
        assertThat(testConsulta.getCreated_at()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testConsulta.getEnded_at()).isEqualTo(DEFAULT_ENDED_AT);
        assertThat(testConsulta.getDuration()).isEqualTo(DEFAULT_DURATION);
    }

    @Test
    @Transactional
    public void createConsultaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = consultaRepository.findAll().size();

        // Create the Consulta with an existing ID
        consulta.setId(1L);
        ConsultaDTO consultaDTO = consultaMapper.toDto(consulta);

        // An entity with an existing ID cannot be created, so this API call must fail
        restConsultaMockMvc.perform(post("/api/consultas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(consultaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Consulta in the database
        List<Consulta> consultaList = consultaRepository.findAll();
        assertThat(consultaList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllConsultas() throws Exception {
        // Initialize the database
        consultaRepository.saveAndFlush(consulta);

        // Get all the consultaList
        restConsultaMockMvc.perform(get("/api/consultas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(consulta.getId().intValue())))
            .andExpect(jsonPath("$.[*].uuid").value(hasItem(DEFAULT_UUID.toString())))
            .andExpect(jsonPath("$.[*].created_at").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].ended_at").value(hasItem(DEFAULT_ENDED_AT.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.toString())));
    }
    
    @Test
    @Transactional
    public void getConsulta() throws Exception {
        // Initialize the database
        consultaRepository.saveAndFlush(consulta);

        // Get the consulta
        restConsultaMockMvc.perform(get("/api/consultas/{id}", consulta.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(consulta.getId().intValue()))
            .andExpect(jsonPath("$.uuid").value(DEFAULT_UUID.toString()))
            .andExpect(jsonPath("$.created_at").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.ended_at").value(DEFAULT_ENDED_AT.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingConsulta() throws Exception {
        // Get the consulta
        restConsultaMockMvc.perform(get("/api/consultas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateConsulta() throws Exception {
        // Initialize the database
        consultaRepository.saveAndFlush(consulta);

        int databaseSizeBeforeUpdate = consultaRepository.findAll().size();

        // Update the consulta
        Consulta updatedConsulta = consultaRepository.findById(consulta.getId()).get();
        // Disconnect from session so that the updates on updatedConsulta are not directly saved in db
        em.detach(updatedConsulta);
        updatedConsulta
            .uuid(UPDATED_UUID)
            .created_at(UPDATED_CREATED_AT)
            .ended_at(UPDATED_ENDED_AT)
            .duration(UPDATED_DURATION);
        ConsultaDTO consultaDTO = consultaMapper.toDto(updatedConsulta);

        restConsultaMockMvc.perform(put("/api/consultas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(consultaDTO)))
            .andExpect(status().isOk());

        // Validate the Consulta in the database
        List<Consulta> consultaList = consultaRepository.findAll();
        assertThat(consultaList).hasSize(databaseSizeBeforeUpdate);
        Consulta testConsulta = consultaList.get(consultaList.size() - 1);
        assertThat(testConsulta.getUuid()).isEqualTo(UPDATED_UUID);
        assertThat(testConsulta.getCreated_at()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testConsulta.getEnded_at()).isEqualTo(UPDATED_ENDED_AT);
        assertThat(testConsulta.getDuration()).isEqualTo(UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void updateNonExistingConsulta() throws Exception {
        int databaseSizeBeforeUpdate = consultaRepository.findAll().size();

        // Create the Consulta
        ConsultaDTO consultaDTO = consultaMapper.toDto(consulta);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConsultaMockMvc.perform(put("/api/consultas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(consultaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Consulta in the database
        List<Consulta> consultaList = consultaRepository.findAll();
        assertThat(consultaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteConsulta() throws Exception {
        // Initialize the database
        consultaRepository.saveAndFlush(consulta);

        int databaseSizeBeforeDelete = consultaRepository.findAll().size();

        // Delete the consulta
        restConsultaMockMvc.perform(delete("/api/consultas/{id}", consulta.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Consulta> consultaList = consultaRepository.findAll();
        assertThat(consultaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
