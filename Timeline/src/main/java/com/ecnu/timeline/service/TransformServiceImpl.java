package com.ecnu.timeline.service;

import com.ecnu.timeline.domain.Message;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TransformServiceImpl implements TransformService{

    private boolean isNowYear(Message message){
        Date cur_date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String cur = sdf.format(cur_date).substring(0,4);//下标0-3 yyyy 下标4不取
        return cur.equals(message.getStr_time().substring(0,4));
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
            str_time = message.getStr_time().substring(5,16);
        }else{//几年以前 str_time不变,和数据库的str_time保持一致即可

        }
        if(str_time!=null&&str_time.length()!=0){//如果应该返回给前端的时间和数据库不同，则对该对象进行修改
            message.setStr_time(str_time);//只修改返回的对象，并不修改数据内容
            //repository.save(message);
        }


    }
    public void transformListsTime(List<Message> lists){
        Long cur_date_long = new Date().getTime();
        if(lists!=null&&lists.size()>0){
            for (Message message: lists
            ) transformMessageTime(cur_date_long,message);
        }
    }

}
