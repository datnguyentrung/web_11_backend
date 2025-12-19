package com.dat.backend_version_2.domain.tournament.Poomsae;

import com.dat.backend_version_2.domain.tournament.AgeGroup;
import com.dat.backend_version_2.domain.tournament.BeltGroup;
import com.dat.backend_version_2.domain.tournament.Tournament;
import com.dat.backend_version_2.enums.tournament.PoomsaeMode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "PoomsaeCombination", schema = "tournament")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PoomsaeCombination {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(updatable = false, nullable = false)
    private UUID idPoomsaeCombination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poomsae_content")
    private PoomsaeContent poomsaeContent;

    @ManyToOne
    @JoinColumn(name = "id_tournament")
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "age_group")
    private AgeGroup ageGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belt_group")
    private BeltGroup beltGroup;

    @Enumerated(EnumType.STRING)
    private PoomsaeMode poomsaeMode = PoomsaeMode.ELIMINATION;

    @Column(name = "participants", nullable = false, columnDefinition = "int default 0")
    private Integer participants = 0;

    public PoomsaeCombination(Tournament tournament,PoomsaeContent poomsaeContent, AgeGroup ageGroup, BeltGroup beltGroup) {
        this.tournament = tournament;
        this.poomsaeContent = poomsaeContent;
        this.ageGroup = ageGroup;
        this.beltGroup = beltGroup;
    }
}
