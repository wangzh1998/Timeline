package com.ecnu.timeline.service;

import com.ecnu.timeline.domain.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@ExtendWith(SpringExtension.class)
class TimelineServiceImplTest {

    //@Autowired
    //MessageRepository testRepository;
    @Autowired
    TimelineService testService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void insert() {
        //无返回值的函数，查看一下上节课学的东西，用一下非状态测试
        //TODO
    }

    @Test
    void getNewRecordNum() {//先用insert方法的测试可以吗？
        testService.insert();
        testService.insert();
        assertEquals(2,testService.getNewRecordNum());
    }

    @Test
    void init() {
        List<Message> lists = testService.init();
        assertEquals(5,lists.size());//这个5也不绝对，因为可能在玩家第一次进入的时候，数据库中的数据不足5条
        //TODO
        //然后可能还要测试更多的东西？
    }

    @Test
    void update() {
        testService.insert();
        testService.insert();
        List<Message> lists = testService.update();
        Integer new_recode = testService.getNewRecordNum();
        assertEquals(5+new_recode,lists.size());
        //TODO
    }

    @Test
    void showmore() {
        List<Message> lists = testService.showmore();
        Integer new_recode = testService.getNewRecordNum();
        assertEquals(5+new_recode+5,lists.size());//第二个+5也不确定，因为最后不一定有5条旧数据可以被显示出来，可能小于5
        //TODO
    }
}