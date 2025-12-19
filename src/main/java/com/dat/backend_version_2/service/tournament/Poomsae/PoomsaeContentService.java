package com.dat.backend_version_2.service.tournament.Poomsae;

import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeContent;
import com.dat.backend_version_2.repository.tournament.AgeGroupRepository;
import com.dat.backend_version_2.repository.tournament.BeltGroupRepository;
import com.dat.backend_version_2.repository.tournament.Poomsae.PoomsaeCombinationRepository;
import com.dat.backend_version_2.repository.tournament.Poomsae.PoomsaeContentRepository;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoomsaeContentService {
    private final PoomsaeContentRepository poomsaeContentRepository;

    public List<PoomsaeContent> getAllPoomsaeContent() {
        return poomsaeContentRepository.findAll();
    }

    public PoomsaeContent getPoomsaeContentById(Integer id) throws IdInvalidException {
        return poomsaeContentRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Poomsae Content not found with id: " + id));
    }

    public PoomsaeContent updatePoomsaeContent(PoomsaeContent poomsaeContent) {
        return poomsaeContentRepository.save(poomsaeContent);
    }

    public void deletePoomsaeContent(int id) {
        poomsaeContentRepository.deleteById(id);
    }

    public PoomsaeContent createPoomsaeContent(String contentName) {
        PoomsaeContent poomsaeContent = new PoomsaeContent();
        poomsaeContent.setContentName(contentName);
        return poomsaeContentRepository.save(poomsaeContent);
    }
}
