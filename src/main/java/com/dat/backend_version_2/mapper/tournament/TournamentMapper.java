package com.dat.backend_version_2.mapper.tournament;

import com.dat.backend_version_2.domain.tournament.Tournament;
import com.dat.backend_version_2.dto.tournament.TournamentDTO;
import org.springframework.beans.BeanUtils;

public class TournamentMapper {
    public static TournamentDTO.TournamentResponse toResponse(Tournament tournament) {
        if (tournament == null) return null;
        TournamentDTO.TournamentResponse res = new TournamentDTO.TournamentResponse();
        TournamentDTO.TournamentInfo info = tournamentToTournamentInfo(tournament);

        res.setIdTournament(tournament.getIdTournament().toString());
        BeanUtils.copyProperties(info, res);

        return res;
    }

    public static TournamentDTO.TournamentInfo tournamentToTournamentInfo(Tournament tournament) {
        if (tournament == null) return null;
        TournamentDTO.TournamentInfo tournamentInfo = new TournamentDTO.TournamentInfo();
        tournamentInfo.setTournamentName(tournament.getTournamentName());
        tournamentInfo.setTournamentDate(tournament.getTournamentDate());
        tournamentInfo.setLocation(tournament.getLocation());
        tournamentInfo.setTournamentScope(tournament.getTournamentScope());
        tournamentInfo.setTournamentState(tournament.getTournamentState());
        return tournamentInfo;
    }

    public static void tournamentInfoToTournament(TournamentDTO.TournamentInfo tournamentInfo, Tournament tournament) {
        if (tournamentInfo == null) return;
        tournament.setTournamentName(tournamentInfo.getTournamentName());
        tournament.setTournamentDate(tournamentInfo.getTournamentDate());
        tournament.setLocation(tournamentInfo.getLocation());
        tournament.setTournamentScope(tournamentInfo.getTournamentScope());
        tournament.setTournamentState(tournamentInfo.getTournamentState());
    }
}
