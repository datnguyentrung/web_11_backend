package com.dat.backend_version_2.domain.tournament;

import com.dat.backend_version_2.enums.training.BeltLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BeltGroup", schema = "tournament")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BeltGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idBeltGroup;

    private String beltGroupName;
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    private BeltLevel startBelt;

    @Enumerated(EnumType.STRING)
    private BeltLevel endBelt;
}
