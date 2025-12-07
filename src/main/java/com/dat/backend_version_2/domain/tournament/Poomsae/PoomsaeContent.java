package com.dat.backend_version_2.domain.tournament.Poomsae;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PoomsaeContent", schema = "tournament")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PoomsaeContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPoomsaeContent;

    private String contentName;
    private Boolean isActive = true;
}
