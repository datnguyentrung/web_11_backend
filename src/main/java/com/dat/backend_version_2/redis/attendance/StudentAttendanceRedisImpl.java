package com.dat.backend_version_2.redis.attendance;

import com.dat.backend_version_2.config.CacheTtlConfig;
import com.dat.backend_version_2.dto.attendance.StudentAttendanceDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentAttendanceRedisImpl implements StudentAttendanceRedis {
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheTtlConfig cacheTtlConfig;
    private final ObjectMapper redisObjectMapper;
    private final RedisConnectionFactory connectionFactory;

    public String getKeyByIdClassSessionAndDate(
            String idClassSession,
            String attendanceDate
    ) {
        return "student-attendance:" + idClassSession + ":" + attendanceDate;
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
    public void clear() {
        if (connectionFactory != null) {
            try (var connection = connectionFactory.getConnection()) {
                connection.serverCommands().flushDb(); // chỉ xóa DB hiện tại
            }
        }
    }

    @Override
    public List<StudentAttendanceDTO.StudentAttendanceDetail> getAttendanceByClassSessionAndDate(
            String idClassSession,
            LocalDate attendanceDate
    ) throws JsonProcessingException{
        String key = getKeyByIdClassSessionAndDate(idClassSession, attendanceDate.toString());
        String json = stringRedisTemplate.opsForValue().get(key);

        try {
            return json != null ?
                    redisObjectMapper.readValue(json, redisObjectMapper.getTypeFactory().constructCollectionType(List.class, StudentAttendanceDTO.StudentAttendanceDetail.class))
                    : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAttendanceByClassSessionAndDate(
            String idClassSession,
            LocalDate attendanceDate,
            List<StudentAttendanceDTO.StudentAttendanceDetail> attendanceDetailList
    ) throws JsonProcessingException {
        String key = getKeyByIdClassSessionAndDate(idClassSession, attendanceDate.toString());
        try {
            String json = redisObjectMapper.writeValueAsString(attendanceDetailList);
            stringRedisTemplate.opsForValue().set(key, json, cacheTtlConfig.randomOneDay());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
