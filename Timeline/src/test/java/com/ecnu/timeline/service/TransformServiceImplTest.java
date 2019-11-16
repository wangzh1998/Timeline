package com.ecnu.timeline.service;

import com.ecnu.timeline.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransformServiceImplTest {


    TransformService transformService = new TransformServiceImpl();

    @Test
    void transformListsTimeJustnow() throws ParseException {
        long one_minute = 60*1000;
        long one_hour = 60*one_minute;
        long one_day = 24*one_hour;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Long just = new Date().getTime();
        String just_str = sdf.format(just);

        List<Message> lists = new ArrayList<>();

        Message m1 = new Message(1,"Wangzh","JustNow Message", just,sdf.format(just),"imagepath");
        Long dif = one_hour-one_minute;
        Message m2 = new Message(2,"Wangzh","InnerOneHoure Message",
                just-dif,sdf.format(just-dif),"imagepath");
        long dif2 = one_day-one_hour;
        Message m3 = new Message(3,"Wangzh","Inner24Hour Message",
                just-dif2,sdf.format(just-dif2),"imagepath");
        String nowYear_str = just_str.substring(0,4)+"-01-01 13:00";
        Message m4 = new Message(4,"Wangzh","NowYear Message",
                sdf.parse(nowYear_str).getTime(),nowYear_str,"imagepath");
        String lastYear_str = "2018-01-01 13:00";
        Message m5 = new Message(5,"Wangzh","LastYear Message",
                sdf.parse(lastYear_str).getTime(),lastYear_str,"imagepath");

        lists.add(m1);
        lists.add(m2);
        lists.add(m3);
        lists.add(m4);
        lists.add(m5);

        transformService.transformListsTime(lists);

        assertAll(
                ()->assertNotNull(lists),
                ()->assertEquals(5,lists.size()),
                ()->assertEquals("刚刚",lists.get(0).getStr_time()),
                ()->assertEquals("59分钟前",lists.get(1).getStr_time()),
                ()->assertEquals("23小时前",lists.get(2).getStr_time()),
                ()->assertEquals("01-01 13:00",lists.get(3).getStr_time()),
                ()->assertEquals("2018-01-01 13:00",lists.get(4).getStr_time())
        );
    }
}