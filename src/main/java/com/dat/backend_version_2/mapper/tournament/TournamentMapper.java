package com.dat.backend_version_2.mapper.tournament;

import com.dat.backend_version_2.domain.tournament.Tournament;
import com.dat.backend_version_2.dto.tournament.TournamentDTO;
import com.dat.backend_version_2.dto.training.Student.StudentRes;
import com.dat.backend_version_2.repository.tournament.HistoryInfoQuickView;
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

    public static TournamentDTO.HistoryInfo historyInfoQuickViewToHistoryInfo(
            HistoryInfoQuickView historyInfoQuickView) {
        if (historyInfoQuickView == null) return null;
        TournamentDTO.HistoryInfo historyInfo = new TournamentDTO.HistoryInfo();
        historyInfo.setIdHistory(historyInfoQuickView.getIdHistory().toString());
        historyInfo.setHasWon(historyInfoQuickView.getHasWon());

        historyInfo.setSourceNode(historyInfoQuickView.getSourceNode());
        historyInfo.setTargetNode(historyInfoQuickView.getTargetNode());
        historyInfo.setLevelNode(historyInfoQuickView.getLevelNode());

        historyInfo.setStudent(historyInfoQuickViewToPersonalInfo(historyInfoQuickView));
        return historyInfo;
    }

    public static StudentRes.PersonalInfo historyInfoQuickViewToPersonalInfo(
            HistoryInfoQuickView historyInfoQuickView) {
        if (historyInfoQuickView == null) return null;

        StudentRes.PersonalInfo personalInfo = new StudentRes.PersonalInfo();
//        personalInfo.setIdAccount(historyInfoQuickView.getAccountId());
        personalInfo.setName(historyInfoQuickView.getStudentName());
        personalInfo.setIdNational(historyInfoQuickView.getNationalId());
        personalInfo.setBirthDate(historyInfoQuickView.getBirthDate());
        personalInfo.setIsActive(historyInfoQuickView.getIsActiveStudent());
        return personalInfo;
    }
}
