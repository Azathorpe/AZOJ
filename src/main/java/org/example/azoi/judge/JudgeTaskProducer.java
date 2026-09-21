package org.example.azoi.judge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class JudgeTaskProducer {

    private static final Logger log = LoggerFactory.getLogger(JudgeTaskProducer.class);
    private static final String QUEUE_KEY = "judge:queue";

    private final StringRedisTemplate redisTemplate;

    public JudgeTaskProducer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** 推 submissionId 到队列 */
    public void push(Long submissionId) {
        redisTemplate.opsForList().leftPush(QUEUE_KEY, submissionId.toString());
        log.info("推入判题队列: submissionId={}", submissionId);
    }
}