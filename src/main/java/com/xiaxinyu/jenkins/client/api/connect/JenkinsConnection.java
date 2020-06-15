package com.xiaxinyu.jenkins.client.api.connect;


import com.offbytwo.jenkins.JenkinsServer;
import com.xiaxinyu.jenkins.client.api.JenkinsHttpClientExtend;
import com.xiaxinyu.jenkins.client.core.JenkinsClientProperties;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Jenkins Connection
 *
 * @author summer
 * @date 2020.6.16
 */
@Configuration
@Slf4j
public class JenkinsConnection {

    @Autowired
    JenkinsClientProperties properties;

    @Bean
    public JenkinsServer getJenkinsServer() throws Exception {
        JenkinsHttpClientExtend client = getJenkinsClient();
        JenkinsServer jenkinsServer = new JenkinsServer(client);

        return jenkinsServer;
    }

    @Bean
    public JenkinsHttpClientExtend getJenkinsClient() throws Exception {
        JenkinsHttpClientExtend client = new JenkinsHttpClientExtend(
                new URI(properties.getAddress()), properties.getUsername(),
                properties.getToken());
        return client;
    }
}
