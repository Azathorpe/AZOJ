package org.example.azoi.judge;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class JudgeWorker {

    private static final Logger log = LoggerFactory.getLogger(JudgeWorker.class);
    private static final String QUEUE_KEY = "judge:queue";
    @Value("${azoi.data.redis.worker-count}")
    private int WORKER_COUNT;

    private final StringRedisTemplate redisTemplate;
    private final JudgeService judgeService;

    public JudgeWorker(StringRedisTemplate redisTemplate, JudgeService judgeService) {
        this.redisTemplate = redisTemplate;
        this.judgeService = judgeService;
        judgeService.onCreated();
    }

    @PostConstruct
    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(WORKER_COUNT);
        for (int i = 0; i < WORKER_COUNT; i++) {
            pool.submit(this::loop);
        }
        log.info("判题机启动，worker 数量: {}", WORKER_COUNT);
    }

    private void loop() {
        while (true) {
            try {
                // 阻塞 5 秒，没任务就继续等
                String submissionId = redisTemplate.opsForList()
                        .rightPop(QUEUE_KEY, Duration.ofSeconds(5));

                if (submissionId == null) {
                    continue;
                }

                log.info("拉取到任务: submissionId={}", submissionId);
                judgeService.judge(Long.parseLong(submissionId));

            } catch (Exception e) {
                log.error("判题出错", e);
            }
        }
    }
}