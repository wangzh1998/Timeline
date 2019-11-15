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

    private static final Logger log = LoggerFactory.getLogger(TimelineServiceImpl.class);

    @Autowired
    MessageRepository repository;

    List<Message> lists = new ArrayList<>();//返回给前端的数据列表

    private void maintance_min_max(){//维护返回给前面的纪录列表中的最大ID和最小ID
        if(lists!=null&&lists.size()>0){
            int size = lists.size();
            min_id = lists.get(size-1).getId();
            max_id = lists.get(0).getId();
        }
    }

    private boolean isNowYear(Message message){
        Date cur_date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String cur = sdf.format(cur_date).substring(0,3);
        return cur.equals(message.getStr_time().substring(0,3));
    }

    private void transformMessageTime(long cur,Message message){
        long one_minute = 60*1000;
        long one_hour = 60*one_minute;
        long one_day = 24*one_hour;
        long dif = cur - message.getTime();
        String str_time = new String();
        if(dif<one_minute){//1分钟内
             str_time = "刚刚";
        }else if(dif<one_hour){//1小时内
             str_time = ""+(dif/one_minute)+"分钟前";
        }else if(dif<one_day){//1天内
             str_time = ""+(dif/one_hour)+"小时前";
        }else if(isNowYear(message)){//本年度
            str_time = message.getStr_time().substring(5,15);
        }else{//几年以前 str_time不变,和数据库的str_time保持一致即可

        }
       if(str_time!=null&&str_time.length()!=0){//如果应该返回给前端的时间和数据库不同，则对该对象进行修改
           message.setStr_time(str_time);//只修改返回的对象，并不修改数据内容
           //repository.save(message);
       }


    }
    private void transformListsTime(List<Message> lists){
        Long cur_date_long = new Date().getTime();
        for (Message message: lists
             ) transformMessageTime(cur_date_long,message);
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
        transformListsTime(lists);
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
        transformListsTime(lists);
        return lists;
    }

    @Override
    public List<Message> update() {//返回原有lists数据+所有更新后未返回过的数据
        Integer latest_max_id = repository.getMaxID();
        if(latest_max_id == null)
            latest_max_id = 0;
        List<Message> new_lists = repository.findByIdGreaterThanOrderByIdDesc(max_id);
        lists.addAll(0,new_lists);//从头部插入到lists
        maintance_min_max();
        new_record_num = 0;//未返回的数据归0
        transformListsTime(lists);
        return lists;
    }

    @Override
    public List<Message> showmore() {
        Integer max = min_id-1 >=0 ? min_id-1:0;
        Integer min = min_id-showmore_limit+1 >=0? min_id-showmore_limit+1:0;
        List<Message> new_lists = repository.findByIdBetweenOrderByIdDesc(min,min_id-1);
        lists.addAll(new_lists);
        maintance_min_max();
        transformListsTime(lists);
        return lists;
    }


}
