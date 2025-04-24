package com.r2s.ApiWebReview.common.event;

import com.r2s.ApiWebReview.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {

    private final User user;

    public UserRegisteredEvent(User user) {
        super(user); // user là nguồn phát sự kiện
        this.user = user;
    }
}
