package com.vergil.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class ConsultaMapperTest {

    private ConsultaMapper consultaMapper;

    @BeforeEach
    public void setUp() {
        consultaMapper = new ConsultaMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 2L;
        assertThat(consultaMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(consultaMapper.fromId(null)).isNull();
    }
}
