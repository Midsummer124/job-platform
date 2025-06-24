package com.example.adminservice.service.impl;

import com.example.adminservice.entity.StatisticsDTO;
import com.example.adminservice.mapper.AdminMapper;
import com.example.adminservice.service.AdminService;
import com.example.adminservice.utils.LogProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LogProducer logProducer;

    @Autowired
    private RedisTemplate<String, StatisticsDTO> redisTemplate;

    private static final String STATISTICS_KEY = "admin:statistics";
    @Override
    public StatisticsDTO getStatistics() {
        ValueOperations<String, StatisticsDTO> ops = redisTemplate.opsForValue();
        StatisticsDTO cached = ops.get(STATISTICS_KEY);
        if (cached != null) {
            logProducer.sendLog("admin-service", "INFO", "redis缓存命中，直接回退数据");
            return cached;
        }
        logProducer.sendLog("admin-service", "INFO", "redis缓存未命中，正在读入数据，该数据将存在10min");
        // 没有缓存，查询数据库
        StatisticsDTO dto = new StatisticsDTO();
        dto.setCompanyCount(adminMapper.countCompanies());
        dto.setJobSeekerCount(adminMapper.countJobSeekers());
        dto.setJobPostCount(adminMapper.countJobPosts());
        dto.setApplicationCount(adminMapper.countTotalApplications());

        // 写入 Redis，缓存10分钟
        ops.set(STATISTICS_KEY, dto, 10, TimeUnit.MINUTES);
        return dto;
    }
    @Override
    public long getCompanyCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM company", Long.class);
    }

    @Override
    public long getJobSeekerCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM job_seeker", Long.class);
    }

    @Override
    public long getJobPostCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM job_post", Long.class);
    }

    @Override
    public long getApplicationCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM job_application", Long.class);
    }
}
