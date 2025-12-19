package com.dat.backend_version_2.repository.tournament.Poomsae;

import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeHistory;
import com.dat.backend_version_2.repository.tournament.HistoryInfoQuickView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PoomsaeHistoryRepository extends JpaRepository<PoomsaeHistory, UUID> {
    @Query("""
            SELECT ph
            FROM PoomsaeHistory ph
            JOIN FETCH ph.poomsaeList pl
            JOIN FETCH pl.poomsaeCombination pc
            JOIN FETCH pl.tournament t
            WHERE ph.idPoomsaeHistory = :idPoomsaeHistory
            """)
    Optional<PoomsaeHistory> findByIdWithCombination(@Param("idPoomsaeHistory") UUID idPoomsaeHistory);

    @Query("""
                SELECT DISTINCT ph
                FROM PoomsaeHistory ph
                    JOIN FETCH ph.poomsaeList pl
                    JOIN FETCH pl.student s
                    JOIN FETCH pl.poomsaeCombination pc
                WHERE pc = :poomsaeCombination
            """)
    List<PoomsaeHistory> findAllByPoomsaeCombination(@Param("poomsaeCombination") PoomsaeCombination poomsaeCombination);

    @Query("""
            SELECT ph
            FROM PoomsaeHistory ph
            JOIN FETCH ph.poomsaeList pl
            WHERE ph.poomsaeList.tournament.idTournament = :tournamentId
                AND ph.poomsaeList.poomsaeCombination.idPoomsaeCombination = :combinationId
                AND ph.targetNode = :targetNode
                AND ph.idPoomsaeHistory != :winnerId
            """)
    Optional<PoomsaeHistory> findOpponentByNode(
            @Param("tournamentId") UUID idTournament,
            @Param("combinationId") UUID combinationId,
            @Param("targetNode") Integer targetNode,
            @Param("winnerId") UUID winnerId
    );

    Integer countPoomsaeHistoryByLevelNode(Integer levelNode);

    @Query("""
            SELECT DISTINCT ph
                FROM PoomsaeHistory ph
                    JOIN FETCH ph.poomsaeList pl
                    JOIN FETCH pl.tournament t
                    JOIN FETCH pl.student s
                    JOIN FETCH pl.branch b
                    JOIN FETCH pl.poomsaeCombination pc
                WHERE t.idTournament = :idTournament
            """)
    List<PoomsaeHistory> findAllByIdTournament(@Param("idTournament") UUID idTournament);

    @Query("""
                SELECT DISTINCT ph
                FROM PoomsaeHistory ph
                    JOIN FETCH ph.poomsaeList pl
                    JOIN FETCH pl.student s
                    JOIN FETCH pl.poomsaeCombination pc
                WHERE pc.idPoomsaeCombination = :idPoomsaeCombination
                    AND ph.levelNode = :levelNode
                    AND ph.sourceNode = :sourceNode
            """)
    Optional<PoomsaeHistory> findByIdPoomsaeCombinationAndLevelNodeAndSourceNode(
            @Param("idPoomsaeCombination") UUID idPoomsaeCombination,
            @Param("levelNode") Integer levelNode,
            @Param("sourceNode") Integer sourceNode
    );


    @Query(value = """
            SELECT DISTINCT pc.poomsae_mode
            FROM tournament.poomsae_history ph
                 JOIN achievement.poomsae_list pl ON ph.poomsae_list = pl.id_poomsae_list
                JOIN tournament.poomsae_combination pc ON pl.poomsae_combination = pc.id_poomsae_combination
            WHERE pl.tournament = :idTournament
              AND pl.poomsae_combination = :idPoomsaeCombination
              AND (:idUser IS NULL OR pl.student_id_user = :idUser)
            LIMIT 1
            """, nativeQuery = true)
    Optional<String> findModeByFilter(
            @Param("idTournament") UUID idTournament,
            @Param("idPoomsaeCombination") UUID idPoomsaeCombination,
            @Param("idUser") UUID idUser
    );

    @Query(value = """
            SELECT ph.id_poomsae_history AS idHistory,
                ph.has_won               AS hasWon,
            
                ph.source_node           AS sourceNode,
                ph.target_node           AS targetNode,
                ph.level_node            AS levelNode,
            
                s.name                   AS studentName,
                s.id_national            AS nationalId,
                s.birth_date             AS birthDate,
                s.is_active              AS isActiveStudent
            FROM tournament.poomsae_history ph
                 JOIN achievement.poomsae_list pl on ph.poomsae_list = pl.id_poomsae_list
                 JOIN training.student s on pl.student_id_user = s.id_user
            WHERE pl.tournament = :tournamentId
              AND pl.poomsae_combination = :combinationId
              AND (:userId IS NULL OR pl.student_id_user = :userId)""", nativeQuery = true)
    List<HistoryInfoQuickView> findByFilter(
            @Param("tournamentId") UUID idTournament,
            @Param("combinationId") UUID idCombination,
            @Param("userId") UUID idUser
    );
}
