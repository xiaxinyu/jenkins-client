package com.xiaxinyu.jenkins.client.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 包上传
 *
 * @author XIAXINYU3
 * @date 2019.12.5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PackageUpload {
    private String replacedFileName;
    private String packagePath;
    private String uri;
    private String version;
    private String userName;
    private String password;
    private String partialUri;
    private String token;
    private String urlCi;
}
