package com.ecnu.timeline.service;

import com.ecnu.timeline.domain.Message;
import com.ecnu.timeline.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TimelineServiceImpl implements TimelineService{

    private static Integer min_id=0,max_id=0;//纪录返回给界面显示的纪录的起止ID
    private static Integer init_limit = 5;//首次进入页面，限制显示数据为最新的5条数据
    private static Integer showmore_limit =5;//向下更新，显示更多数据，限制为5条
    private static Integer new_record_num = 0;//纪录最新插入但是没有被显示出来的数据数目

    //private static final Logger log = LoggerFactory.getLogger(TimelineServiceImpl.class);

    @Autowired
    MessageRepository repository;

    @Autowired
    TransformService transformService;

    List<Message> lists = new ArrayList<>();//返回给前端的数据列表

    private void maintance_min_max(){//维护返回给前面的纪录列表中的最大ID和最小ID
        if(lists!=null&&lists.size()>0){
            int size = lists.size();
            min_id = lists.get(size-1).getId();
            max_id = lists.get(0).getId();
        }
    }


    @Override
    public List<Message> insert() {
        Integer latest_max_id = repository.getMaxID();
        if( latest_max_id == null)
            latest_max_id = 0;
        Message m = new Message();
        m.setName("Wangzh");
        Date cur_date = new Date();
        m.setTime(cur_date.getTime());
        m.setStr_time(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cur_date));
        //数据库的的时间字符串一定是存入时间的yyyy-MM-dd HH:mm，在后面的转换中，修改的只是取出的对象，而不是数据库内容
        m.setContent("AutoMessage"+(latest_max_id+1));
        m.setImage("/images/universe.png");
        repository.save(m);
        new_record_num++;
        transformService.transformListsTime(lists);
        return lists;//返回原来的lists
    }

    @Override
    public Integer getNewRecordNum() {
        return new_record_num;
    }


    @Override
    public List<Message> init() { //初始化函数，返回最新的五条数据
        Integer latest_max_id = repository.getMaxID();
        if(latest_max_id == null)
            latest_max_id = 0;
        Integer min = latest_max_id-init_limit+1>=0?latest_max_id-init_limit+1:0;
        List<Message> new_lists = repository.findByIdBetweenOrderByIdDesc(min,latest_max_id);
        lists.clear();//如果是第二次回到首页，仍然只显示最新的5条数据
        lists.addAll(new_lists);
        maintance_min_max();
        transformService.transformListsTime(lists);
        return lists;
    }

    @Override
    public List<Message> update() {//返回原有lists数据+所有更新后未返回过的数据
        List<Message> new_lists = repository.findByIdGreaterThanOrderByIdDesc(max_id);
        lists.addAll(0,new_lists);//从头部插入到lists
        maintance_min_max();
        new_record_num = 0;//未返回的数据归0
        transformService.transformListsTime(lists);
        return lists;
    }

    @Override
    public List<Message> showmore() {
        Integer max = min_id-1 >=0 ? min_id-1:0;
        Integer min = min_id-showmore_limit+1 >=0? min_id-showmore_limit+1:0;
        List<Message> new_lists = repository.findByIdBetweenOrderByIdDesc(min,min_id-1);
        lists.addAll(new_lists);
        maintance_min_max();
        transformService.transformListsTime(lists);
        return lists;
    }




}
