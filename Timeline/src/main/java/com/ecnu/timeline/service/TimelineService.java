package com.ecnu.timeline.service;

import com.ecnu.timeline.domain.Message;

import java.util.List;

public interface TimelineService {
     List<Message> insert();

     Integer getNewRecordNum();

     List<Message> init();

     List<Message> update();

     List<Message> showmore();

}
