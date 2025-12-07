package com.dat.backend_version_2.service.tournament;

import com.dat.backend_version_2.domain.tournament.AgeGroup;
import com.dat.backend_version_2.domain.tournament.BeltGroup;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeContent;
import com.dat.backend_version_2.dto.tournament.AgeGroupDTO;
import com.dat.backend_version_2.repository.tournament.AgeGroupRepository;
import com.dat.backend_version_2.repository.tournament.BeltGroupRepository;
import com.dat.backend_version_2.repository.tournament.Poomsae.PoomsaeCombinationRepository;
import com.dat.backend_version_2.service.tournament.Poomsae.PoomsaeContentService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgeGroupService {
    private final AgeGroupRepository ageGroupRepository;
    private final BeltGroupRepository beltGroupRepository;
    private final PoomsaeContentService poomsaeContentService;
    private final PoomsaeCombinationRepository poomsaeCombinationRepository;

    public List<AgeGroup> getAllAgeGroups() {
        return ageGroupRepository.findAll();
    }

    public AgeGroup getAgeGroupById(int id) throws IdInvalidException {
        return ageGroupRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Age group not found with id: " + id));
    }

    public AgeGroup update(AgeGroup ageGroup) {
        return ageGroupRepository.save(ageGroup);
    }

    public void deleteAgeGroup(int id) {
        ageGroupRepository.deleteById(id);
    }

    public AgeGroup createAgeGroup(AgeGroupDTO ageGroupDTO) {
        // 1️⃣ Tạo mới AgeGroup
        AgeGroup ageGroup = new AgeGroup();
        ageGroup.setAgeGroupName(ageGroupDTO.getAgeGroupName());
        ageGroup.setMinAge(ageGroupDTO.getMinAge());
        ageGroup.setMaxAge(ageGroupDTO.getMaxAge());
        ageGroup.setIsActive(true);
        return ageGroupRepository.save(ageGroup);
    }
}
