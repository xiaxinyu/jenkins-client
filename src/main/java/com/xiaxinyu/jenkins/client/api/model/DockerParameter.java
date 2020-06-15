package com.xiaxinyu.jenkins.client.api.model;

import lombok.Data;
import lombok.ToString;

/**
 * Docker 参数
 *
 * @author XIAXINYU3
 * @date 2019.8.13
 */
@Data
@ToString
public class DockerParameter {
    private String dockerFileDirectory;
    private String imageName;
    private Boolean pushOnSuccess;
    private String repoURL;
    private String repoCredentialId;
}
