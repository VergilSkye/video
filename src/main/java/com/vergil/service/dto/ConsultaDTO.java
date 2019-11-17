package com.vergil.service.dto;
import java.time.Instant;
import java.time.Duration;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.vergil.domain.Consulta} entity.
 */
public class ConsultaDTO implements Serializable {

    private Long id;

    private UUID uuid;

    private Instant created_at;

    private Instant ended_at;

    private Duration duration;


    private Long psicologo_idId;

    private String psicologo_idLogin;

    private Long paciente_idId;

    private String paciente_idLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }

    public Instant getEnded_at() {
        return ended_at;
    }

    public void setEnded_at(Instant ended_at) {
        this.ended_at = ended_at;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Long getPsicologo_idId() {
        return psicologo_idId;
    }

    public void setPsicologo_idId(Long userId) {
        this.psicologo_idId = userId;
    }

    public String getPsicologo_idLogin() {
        return psicologo_idLogin;
    }

    public void setPsicologo_idLogin(String userLogin) {
        this.psicologo_idLogin = userLogin;
    }

    public Long getPaciente_idId() {
        return paciente_idId;
    }

    public void setPaciente_idId(Long userId) {
        this.paciente_idId = userId;
    }

    public String getPaciente_idLogin() {
        return paciente_idLogin;
    }

    public void setPaciente_idLogin(String userLogin) {
        this.paciente_idLogin = userLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConsultaDTO consultaDTO = (ConsultaDTO) o;
        if (consultaDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), consultaDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ConsultaDTO{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", created_at='" + getCreated_at() + "'" +
            ", ended_at='" + getEnded_at() + "'" +
            ", duration='" + getDuration() + "'" +
            ", psicologo_id=" + getPsicologo_idId() +
            ", psicologo_id='" + getPsicologo_idLogin() + "'" +
            ", paciente_id=" + getPaciente_idId() +
            ", paciente_id='" + getPaciente_idLogin() + "'" +
            "}";
    }
}
