package com.dat.backend_version_2.redis.tournament;

import com.dat.backend_version_2.domain.tournament.Tournament;

public interface TournamentRedis {
    void deleteByKey(String key);

    Tournament getById(String idTournament);

    void save(Tournament tournament);
}
