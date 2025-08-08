package cn.edu.nju.playground.event.activity;

import cn.edu.nju.playground.model.po.Registration;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationEvent {
    private Registration registration;
    private String description;
}