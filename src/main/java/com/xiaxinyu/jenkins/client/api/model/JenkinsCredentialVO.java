package com.xiaxinyu.jenkins.client.api.model;

/**
 * @author XIAXINYU3
 */
public class JenkinsCredentialVO {
    private String name;
    private String credentialId;

    public JenkinsCredentialVO() {
    }

    public JenkinsCredentialVO(String name, String credentialId) {
        this.name = name;
        this.credentialId = credentialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    @Override
    public String toString() {
        return "JenkinsCredentialVO{" +
                "name='" + name + '\'' +
                ", credentialId='" + credentialId + '\'' +
                '}';
    }
}
