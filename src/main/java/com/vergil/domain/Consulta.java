package com.vergil.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.Duration;
import java.util.UUID;

/**
 * A Consulta.
 */
@Entity
@Table(name = "consulta")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Consulta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uuid", columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Column(name = "created_at")
    private Instant created_at;

    @Column(name = "ended_at")
    private Instant ended_at;

    @Column(name = "duration")
    private Duration duration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("consultas")
    private User psicologo_id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("consultas")
    private User paciente_id;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Consulta uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Instant getCreated_at() {
        return created_at;
    }

    public Consulta created_at(Instant created_at) {
        this.created_at = created_at;
        return this;
    }

    public void setCreated_at(Instant created_at) {
        this.created_at = created_at;
    }

    public Instant getEnded_at() {
        return ended_at;
    }

    public Consulta ended_at(Instant ended_at) {
        this.ended_at = ended_at;
        return this;
    }

    public void setEnded_at(Instant ended_at) {
        this.ended_at = ended_at;
    }

    public Duration getDuration() {
        return duration;
    }

    public Consulta duration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public User getPsicologo_id() {
        return psicologo_id;
    }

    public Consulta psicologo_id(User user) {
        this.psicologo_id = user;
        return this;
    }

    public void setPsicologo_id(User user) {
        this.psicologo_id = user;
    }

    public User getPaciente_id() {
        return paciente_id;
    }

    public Consulta paciente_id(User user) {
        this.paciente_id = user;
        return this;
    }

    public void setPaciente_id(User user) {
        this.paciente_id = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Consulta)) {
            return false;
        }
        return id != null && id.equals(((Consulta) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Consulta{" +
            "id=" + getId() +
            ", uuid='" + getUuid() + "'" +
            ", created_at='" + getCreated_at() + "'" +
            ", ended_at='" + getEnded_at() + "'" +
            ", duration='" + getDuration() + "'" +
            "}";
    }
}
