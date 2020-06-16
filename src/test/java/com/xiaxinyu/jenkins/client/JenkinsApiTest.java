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
        JobWithDetails jobWithDetails = jenkinsApi.getJob("test");
        Assert.assertNotNull(jobWithDetails);
        log.info("remoteJobName={}", jobWithDetails.getDisplayName());
    }
}
