package io.army.domain;

import java.time.LocalDateTime;

public abstract class ArmyDomain {

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer version;

    private Boolean visible;

}
