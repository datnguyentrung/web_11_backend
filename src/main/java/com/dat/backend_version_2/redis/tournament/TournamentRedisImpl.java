package com.dat.backend_version_2.redis.tournament;

import com.dat.backend_version_2.config.CacheTtlConfig;
import com.dat.backend_version_2.domain.tournament.Tournament;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentRedisImpl implements TournamentRedis {
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheTtlConfig cacheTtlConfig;
    private final ObjectMapper redisObjectMapper;
    private final RedisConnectionFactory connectionFactory;

    public String getKeyByIdTournament(String idTournament) {
        return "tournament:" + idTournament;
    }

    @Override
    public void deleteByKey(String key) {
        Boolean result = stringRedisTemplate.delete(key);
        if (result) {
            log.info("✅ Đã xóa cache Redis key: {}", key);
        } else {
            log.warn("⚠️ Không tìm thấy hoặc không thể xóa key: {}", key);
        }
    }

    @Override
    public Tournament getById(String idTournament) {
        String key = getKeyByIdTournament(idTournament);
        String json = stringRedisTemplate.opsForValue().get(key);
        try{
            return json != null
                    ? redisObjectMapper.readValue(json, Tournament.class)
                    : null;
        } catch (Exception e) {
            log.error("⚠️ Lỗi khi lấy Tournament từ Redis với idTournament {}: {}", idTournament, e.getMessage());
            throw new IllegalArgumentException("Lỗi khi lấy Tournament từ Redis", e);
        }
    }

    @Override
    public void save(Tournament tournament) {
        String key = getKeyByIdTournament(tournament.getIdTournament().toString());
        var ttl = cacheTtlConfig.getOneWeekSeconds();
        try {
            String json = redisObjectMapper.writeValueAsString(tournament);
            stringRedisTemplate.opsForValue().set(
                    key,
                    json,
                    Duration.ofSeconds(ttl)
            );
            log.info("✅ Đã lưu Tournament vào Redis với key: {}", key);
        } catch (Exception e) {
            log.error("⚠️ Lỗi khi lưu Tournament vào Redis với idTournament {}: {}", tournament.getIdTournament(), e.getMessage());
            throw new IllegalArgumentException("Lỗi khi lưu Tournament vào Redis", e);
        }
    }
}
