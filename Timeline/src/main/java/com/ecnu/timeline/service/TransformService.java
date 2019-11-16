package com.ecnu.timeline.service;

import com.ecnu.timeline.domain.Message;

import java.util.List;

public interface TransformService {
    void transformListsTime(List<Message> lists);
}
