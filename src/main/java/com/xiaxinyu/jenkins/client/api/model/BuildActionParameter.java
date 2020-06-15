package com.xiaxinyu.jenkins.client.api.model;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BuildActionParameter {
    private String buildActionType;
    private String buildActionVersion;
    private String nmpImageAddress;
}
