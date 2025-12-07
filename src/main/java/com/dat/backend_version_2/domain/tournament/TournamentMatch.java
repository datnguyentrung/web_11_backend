package com.dat.backend_version_2.domain.tournament;

import com.dat.backend_version_2.dto.tournament.TournamentMatchId;
import com.dat.backend_version_2.enums.tournament.TournamentType;
import com.dat.backend_version_2.enums.training.ClassSession.Session;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TournamentMatch", schema = "tournament")
@IdClass(TournamentMatchId.class)
public class TournamentMatch {
    @Id
    @ManyToOne
    @JoinColumn(name = "tournament")
    private Tournament tournament;

    @Id
    @Column(nullable = false)
    private UUID idCombination;

    @Id
    @Column(nullable = false)
    private Integer targetNode;

    private boolean isFirstNode = false;

    // ---- Self-referencing relations ----
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "leftMatch_tournament", referencedColumnName = "tournament"),
            @JoinColumn(name = "leftMatch_idCombination", referencedColumnName = "idCombination"),
            @JoinColumn(name = "leftMatch_targetNode", referencedColumnName = "targetNode")
    })
    private TournamentMatch leftMatch;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "rightMatch_tournament", referencedColumnName = "tournament"),
            @JoinColumn(name = "rightMatch_idCombination", referencedColumnName = "idCombination"),
            @JoinColumn(name = "rightMatch_targetNode", referencedColumnName = "targetNode")
    })
    private TournamentMatch rightMatch;

    // ---- Match properties ----
    @Enumerated(EnumType.STRING)
    private TournamentType tournamentType;

    @Enumerated(EnumType.STRING)
    private Session session;

    private Duration duration;
    private Integer participants;
}
