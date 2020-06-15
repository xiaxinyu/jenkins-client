package com.xiaxinyu.jenkins.client.api.model;

/**
 * @author XIAXINYU3
 */
public class JenkinsDomain {
    private String name;
    private String description;

    public JenkinsDomain() {
    }

    public JenkinsDomain(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "JenkinsDomain{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
