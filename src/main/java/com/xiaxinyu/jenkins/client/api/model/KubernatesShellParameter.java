package com.xiaxinyu.jenkins.client.api.model;

/**
 * @author XIAXINYU3
 * @date 2019.5.20
 * @description Helm Shell实体类
 */
public class KubernatesShellParameter {
    private boolean updatedPodFlag;
    private String namespace;
    private String parameters;
    private String targetServerHost;
    private String targetServerUser;
    private String podName;
    private String orgCode;
    private String projectPath;
    private String applicationCode;
    private String chartUrl;
    private String imageUrl;
    private String shellFileName;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getTargetServerHost() {
        return targetServerHost;
    }

    public void setTargetServerHost(String targetServerHost) {
        this.targetServerHost = targetServerHost;
    }

    public String getTargetServerUser() {
        return targetServerUser;
    }

    public void setTargetServerUser(String targetServerUser) {
        this.targetServerUser = targetServerUser;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getChartUrl() {
        return chartUrl;
    }

    public void setChartUrl(String chartUrl) {
        this.chartUrl = chartUrl;
    }

    public boolean getUpdatedPodFlag() {
        return updatedPodFlag;
    }

    public void setUpdatedPodFlag(boolean updatedPodFlag) {
        this.updatedPodFlag = updatedPodFlag;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getShellFileName() {
        return shellFileName;
    }

    public void setShellFileName(String shellFileName) {
        this.shellFileName = shellFileName;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }
}
