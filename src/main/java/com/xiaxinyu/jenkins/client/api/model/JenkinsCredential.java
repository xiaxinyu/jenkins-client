package com.xiaxinyu.jenkins.client.api.model;

import com.alibaba.fastjson.JSONObject;

/**
 * @author XIAXINYU3
 */
public class JenkinsCredential {
    private String scope = "GLOBAL";
    private String username;
    private PrivateKeySource source;
    private String passphrase;
    private String redact = "passphrase";
    private Integer id;
    private String description;
    private String staplerClass = "com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey";
    private String clazz = "com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey";

    public JSONObject getJSONObject() {
        JSONObject credentialObject = new JSONObject();
        credentialObject.put("scope", this.scope);
        credentialObject.put("username", this.username);
        credentialObject.put("passphrase", passphrase);
        credentialObject.put("$redact", this.redact);
        credentialObject.put("id", this.id);
        credentialObject.put("description", this.description);
        credentialObject.put("stapler-class", this.staplerClass);
        credentialObject.put("$class", this.clazz);

        JSONObject sourceObject = new JSONObject();
        sourceObject.put("value", this.source.getValue());
        sourceObject.put("privateKey", this.source.getPrivateKey());
        sourceObject.put("stapler-class", this.source.getStaplerClass());
        credentialObject.put("privateKeySource", sourceObject);

        //创建凭据的固定格式
        JSONObject dataJson = new JSONObject();
        dataJson.put("","7");
        dataJson.put("credentials", credentialObject);

        return dataJson;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PrivateKeySource getSource() {
        return source;
    }

    public void setSource(PrivateKeySource source) {
        this.source = source;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getRedact() {
        return redact;
    }

    public void setRedact(String redact) {
        this.redact = redact;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStaplerClass() {
        return staplerClass;
    }

    public void setStaplerClass(String staplerClass) {
        this.staplerClass = staplerClass;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public static class PrivateKeySource {
        private String value = "0";
        private String privateKey;
        private String staplerClass = "com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey$DirectEntryPrivateKeySource";

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getStaplerClass() {
            return staplerClass;
        }

        public void setStaplerClass(String staplerClass) {
            this.staplerClass = staplerClass;
        }

        @Override
        public String toString() {
            return "PrivateKeySource{" +
                    "value='" + value + '\'' +
                    ", privateKey='" + privateKey + '\'' +
                    ", staplerClass='" + staplerClass + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "JenkinsCredential{" +
                "scope='" + scope + '\'' +
                ", username='" + username + '\'' +
                ", source=" + source +
                ", passphrase='" + passphrase + '\'' +
                ", redact='" + redact + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", staplerClass='" + staplerClass + '\'' +
                ", clazz='" + clazz + '\'' +
                '}';
    }
}
