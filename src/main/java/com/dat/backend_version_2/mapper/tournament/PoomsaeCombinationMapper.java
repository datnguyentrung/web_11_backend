package com.dat.backend_version_2.mapper.tournament;

import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.dto.tournament.AgeGroupDTO;
import com.dat.backend_version_2.dto.tournament.BeltGroupDTO;
import com.dat.backend_version_2.dto.tournament.PoomsaeCombinationDTO;

public class PoomsaeCombinationMapper {
    public static PoomsaeCombinationDTO.CombinationDetail poomsaeCombinationToCombinationDetail(
            PoomsaeCombination poomsaeCombination) {
        if (poomsaeCombination == null) return null;

        PoomsaeCombinationDTO.CombinationDetail detail = new PoomsaeCombinationDTO.CombinationDetail();
        detail.setIdPoomsaeCombination(String.valueOf(poomsaeCombination.getIdPoomsaeCombination()));
        detail.setIdTournament(String.valueOf(poomsaeCombination.getTournament().getIdTournament()));
        detail.setPoomsaeContentName(poomsaeCombination.getPoomsaeContent().getContentName());
        detail.setBeltGroupDTO(new BeltGroupDTO(
                poomsaeCombination.getBeltGroup().getBeltGroupName(),
                poomsaeCombination.getBeltGroup().getStartBelt(),
                poomsaeCombination.getBeltGroup().getEndBelt()
        ));
        detail.setAgeGroupDTO(new AgeGroupDTO(
                poomsaeCombination.getAgeGroup().getAgeGroupName(),
                poomsaeCombination.getAgeGroup().getMinAge(),
                poomsaeCombination.getAgeGroup().getMaxAge()
        ));
        detail.setPoomsaeMode(poomsaeCombination.getPoomsaeMode());

        return detail;
    }
}
