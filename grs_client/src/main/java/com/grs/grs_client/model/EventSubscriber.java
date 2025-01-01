package com.grs.grs_client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSubscriber {

    private String beanName;
    private List<Method> methods;
}
