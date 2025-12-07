package com.dat.backend_version_2.repository.tournament.Poomsae;

import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.Tournament;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface PoomsaeCombinationRepository extends JpaRepository<PoomsaeCombination, UUID> {
    @EntityGraph(attributePaths = {
            "poomsaeContent",
            "ageGroup",
            "beltGroup"
    })
    @Query("SELECT DISTINCT pc FROM PoomsaeCombination pc")
    List<PoomsaeCombination> findAllWithGraph();

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO tournament.poomsae_combination
            (id_poomsae_combination, id_tournament, poomsae_content, belt_group, age_group, poomsae_mode)
            SELECT
                gen_random_uuid(),
                :tournamentId,
                c.id_poomsae_content,
                b.id_belt_group,
                a.id_age_group,
                'ELIMINATION'
            FROM tournament.poomsae_content c
            CROSS JOIN tournament.belt_group b
            CROSS JOIN tournament.age_group a
            WHERE c.id_poomsae_content IN (:contentIds)
              AND b.id_belt_group IN (:beltIds)
              AND a.id_age_group IN (:ageIds)
            """, nativeQuery = true)
    void bulkInsertCombinations(
            @Param("tournamentId") UUID tournamentId,
            @Param("contentIds") List<Integer> contentIds,
            @Param("beltIds") List<Integer> beltIds,
            @Param("ageIds") List<Integer> ageIds
    );

    @Query("""
            SELECT pc FROM PoomsaeCombination pc
                JOIN FETCH pc.ageGroup
                JOIN FETCH pc.beltGroup
                JOIN FETCH pc.poomsaeContent
            WHERE pc.tournament = :tournament
            """)
    List<PoomsaeCombination> findByTournament(@Param("tournament") Tournament tournament);
}
