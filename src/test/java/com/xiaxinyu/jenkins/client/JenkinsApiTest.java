package com.xiaxinyu.jenkins.client;

import com.offbytwo.jenkins.model.JobWithDetails;
import com.xiaxinyu.jenkins.client.api.JenkinsApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JenkinsClientApplication.class})
public class JenkinsApiTest {

    @Autowired
    JenkinsApi jenkinsApi;

    @Before
    public void before() {
    }

    @Test
    public void testCreateGroup() throws Exception {
        StopWatch stopWatch = new StopWatch("Starting query jenkins job");
        for (int i = 0; i < 100; i++) {
            stopWatch.start(String.format("Query jenkins job: sequence=%d", (i + 1)));
            JobWithDetails jobWithDetails = jenkinsApi.getJob("cprtri-dl-example_example1_default_sonar");
            Assert.assertNotNull(jobWithDetails);
            log.info("remoteJobName={}", jobWithDetails.getDisplayName());
            stopWatch.stop();
        }
        log.info("Finishing query jenkins job：{}", stopWatch.prettyPrint());
    }
}
