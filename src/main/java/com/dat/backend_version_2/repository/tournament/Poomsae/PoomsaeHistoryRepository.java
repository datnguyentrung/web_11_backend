package com.dat.backend_version_2.repository.tournament.Poomsae;

import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeHistory;
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
                SELECT DISTINCT ph
                FROM PoomsaeHistory ph
                    JOIN FETCH ph.poomsaeList pl
                    JOIN FETCH pl.student s
                    JOIN FETCH pl.poomsaeCombination pc
                WHERE pc = :poomsaeCombination
            """)
    List<PoomsaeHistory> findAllByPoomsaeCombination(@Param("poomsaeCombination") PoomsaeCombination poomsaeCombination);

    @Query("""
            SELECT DISTINCT ph
                FROM PoomsaeHistory ph
                    JOIN FETCH ph.poomsaeList pl
                    JOIN FETCH pl.student s
                    JOIN FETCH pl.poomsaeCombination pc
                WHERE pc.idPoomsaeCombination = :idPoomsaeCombination
                  AND ph.targetNode = :targetNode
            """)
    List<PoomsaeHistory> findAllByTargetNodeAndIdPoomsaeCombination(
            @Param("targetNode") Integer targetNode,
            @Param("idPoomsaeCombination") UUID idPoomsaeCombination
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
            SELECT EXISTS(SELECT 1
              FROM tournament.poomsae_history ph
                   JOIN achievement.poomsae_list pl
                        ON ph.poomsae_list = pl.id_poomsae_list
              WHERE pl.tournament = :idTournament
                AND pl.poomsae_combination = :idPoomsaeCombination
                AND (:idUser IS NULL OR pl.student_id_user = :idUser));
            """, nativeQuery = true)
    boolean existsByFilter(
            @Param("idTournament") UUID idTournament,
            @Param("idPoomsaeCombination") UUID idPoomsaeCombination,
            @Param("idUser") UUID idUser
    );
}
