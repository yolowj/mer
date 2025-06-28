package com.zbkj.admin.task.user;

import cn.hutool.core.date.DateUtil;
import com.zbkj.service.service.PaidMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 会员日提醒
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/29
 */
@Component("MemberHappyTask")
public class MemberHappyTask {

    private static final Logger logger = LoggerFactory.getLogger(MemberHappyTask.class);

    @Autowired
    private PaidMemberService paidMemberService;

    /**
     * 每分钟执行一次
     */
    public void memberHappyTask() {
        // cron : cron : 0 */1 * * * ?
        logger.info("---MemberExpirationTask memberExpirationProcessing------produce Data with fixed rate task: Execution Time - {}", DateUtil.date());
        try {
            paidMemberService.memberHappyProcsssing();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberExpirationTask.memberExpirationProcessing" + " | msg : " + e.getMessage());
        }
    }

}
