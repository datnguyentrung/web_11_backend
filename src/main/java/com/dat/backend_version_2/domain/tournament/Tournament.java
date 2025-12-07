package com.dat.backend_version_2.domain.tournament;

import com.dat.backend_version_2.enums.tournament.TournamentScope;
import com.dat.backend_version_2.enums.tournament.TournamentState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "Tournament", schema = "tournament")
public class Tournament {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(updatable = false, nullable = false)
    private UUID idTournament;

    private String tournamentName;
    private LocalDate tournamentDate;
    private String location;

    @Enumerated(EnumType.STRING)
    private TournamentScope tournamentScope;

    @Enumerated(EnumType.STRING)
    private TournamentState tournamentState;
}
