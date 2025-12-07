package com.dat.backend_version_2.repository.achievement;

import com.dat.backend_version_2.domain.achievement.PoomsaeList;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.Tournament;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PoomsaeListRepository extends JpaRepository<PoomsaeList, UUID> {
    @Query("""
            SELECT DISTINCT p
            FROM PoomsaeList p
                JOIN FETCH p.student s
                JOIN FETCH p.tournament t
                JOIN FETCH p.branch b
                JOIN FETCH p.poomsaeCombination pc
            WHERE p.idPoomsaeList IN :ids
            """)
    List<PoomsaeList> findAllByIdPoomsaeList(@Param("ids") List<UUID> ids);

    @EntityGraph(attributePaths = {
            "student",
            "student.branch",
            "student.studentClassSessions",
            "student.studentClassSessions.classSession",
            "student.studentClassSessions.classSession.branch",
            "tournament",
            "poomsaeCombination"
    })
    @Query("SELECT DISTINCT p FROM PoomsaeList p WHERE p.tournament = :tournament")
    List<PoomsaeList> findByTournament(@Param("tournament") Tournament tournament);

    @Query(value = """
            SELECT pl
            FROM PoomsaeList pl
            JOIN FETCH pl.student s
            LEFT JOIN FETCH pl.branch b
            LEFT JOIN FETCH pl.poomsaeCombination pc
            LEFT JOIN FETCH pl.tournament t
            WHERE pl.tournament.idTournament = :tournamentId
              AND pl.poomsaeCombination.idPoomsaeCombination = :poomsaeCombinationId
              AND (:branchId IS NULL OR b.idBranch = :branchId)
              AND (:studentId IS NULL OR s.idUser = :studentId)
            """)
    List<PoomsaeList> findByFilter(
            @Param("tournamentId") UUID tournamentId,
            @Param("poomsaeCombinationId") UUID poomsaeCombination,
            @Param("studentId") UUID studentId,
            @Param("branchId") Integer branchId
    );
}
