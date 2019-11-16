package com.ecnu.timeline.service;

import com.ecnu.timeline.domain.Message;
import com.ecnu.timeline.repository.MessageRepository;
import javafx.beans.binding.When;
import org.hibernate.mapping.Any;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

//@SpringBootTest
@ExtendWith(SpringExtension.class)
class TimelineServiceImplTest {

    //@Autowired
    //MessageRepository testRepository;

    @Mock
    MessageRepository repository;

    @Mock
    TransformService transformService;//忘记这个MOCK对象了，一直报空浮点异常……

    @InjectMocks
    TimelineService service = new TimelineServiceImpl();
    //不能是@Autowire，因为它的内部对象repository是个mock对象,并且需要自己初始化

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        sdf = new SimpleDateFormat();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void insert() {
        //插入一条数据，验证：
        // 1.是不成功向数据库列表(return_lists)插入一条纪录
        // 2.返回给前端的(return_lists)是否为空
        // 3.插入数据库的数据格式是否正确

        List<Message> return_lists = new ArrayList<>();
        List<Message> repo_lists = new ArrayList<>();
        Long cur = new Date().getTime();

        Answer<Message> save_answer = new Answer<Message>() {
            @Override
            public Message answer(InvocationOnMock invocationOnMock) throws Throwable {
                Message message = new Message(1, "Wangzh", "AutoMessage1", cur, sdf.format(cur), "images/universe.png");
                repo_lists.add(message);
                return message;
            }
        };

        Mockito.when(repository.getMaxID()).thenReturn(0);
        Mockito.when(repository.save(any())).thenAnswer(save_answer);//answer的返回类型要和save()的返回类型一致？否则会报错？
        doNothing().when(transformService).transformListsTime(return_lists);//transformService的方法写在when().后面，而不是括号里

        service.insert();
        verify(repository, times(1)).getMaxID();
        verify(repository, times(1)).save(any());
        verify(transformService, times(1)).transformListsTime(any());

        assertAll(
                () -> assertEquals(1, repo_lists.size(), "成功向数据库插入一条纪录"),
                () -> assertEquals(0, return_lists.size(), "返回给前端的列表应该为空")
        );

        Message message = repo_lists.get(0);
        assertAll(
                () -> assertEquals(1, message.getId()),
                () -> assertEquals("Wangzh", message.getName()),
                () -> assertEquals("AutoMessage1", message.getContent()),
                () -> assertEquals(cur, message.getTime()),
                () -> assertEquals(sdf.format(cur), message.getStr_time()),
                () -> assertEquals("images/universe.png", message.getImage())
        );
    }

    @Test
    void getNewRecordNum() {
        // 1.插入之前，返回给前端的新纪录数是0
        // 2.插入之后，返回给前端的新纪录数是否为1

        List<Message> return_lists = new ArrayList<>();
        List<Message> repo_lists = new ArrayList<>();
        Long cur = new Date().getTime();

        Answer<Message> save_answer = new Answer<Message>() {
            @Override
            public Message answer(InvocationOnMock invocationOnMock) throws Throwable {
                Message message = new Message(1, "Wangzh", "AutoMessage1", cur, sdf.format(cur), "/images/universe.png");
                repo_lists.add(message);
                return message;
            }
        };

        Mockito.when(repository.getMaxID()).thenReturn(0);
        Mockito.when(repository.save(any())).thenAnswer(save_answer);//answer的返回类型要和save()的返回类型一致？否则会报错？
        doNothing().when(transformService).transformListsTime(return_lists);//transformService的方法写在when().后面，而不是括号里

        //before
        assertEquals(0, service.getNewRecordNum(), "插入之前，新纪录数为0");

        //after
        service.insert();//前面的打桩是为了这里，虽然本身service.getNewRecordNum里面没有对mock对象的方法调用
        assertEquals(1, service.getNewRecordNum(), "插入之后，新纪录数应该更新为1");

    }


    @Test
    void init() {
        //作为初始始化，先向数据库列表repo_lists当中插入10条数据，然后初始化函数中从这10条数据中取出最新的5条
        //验证:
        //1.repo_lists中数据有10条
        //2.return_lists返回最新的5条
        //3.return_lists返回的数据格式正确 此处着重倒序id和检验时间格式str_time的转换，应该全部转换为刚刚
        List<Message> return_lists = new ArrayList<>();
        List<Message> repo_lists = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Long cur = new Date().getTime();
            repo_lists.add(new Message((i + 1), "Wangzh", "AutoMessage" + (i + 1), cur, sdf.format(cur), "/images/universer.png"));
            //System.out.println("AutoMessage"+(i+1));
        }

        Answer<List<Message>> find_answer = new Answer<List<Message>>() {
            @Override
            public List<Message> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return_lists.addAll(repo_lists.subList(5, 10));//这个子串下标从1开始，太坑了！
                Collections.reverse(return_lists);
                return return_lists;
            }
        };

        Answer trans_answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                for (Message message : return_lists) {
                    message.setStr_time("刚刚");
                }
                return null;
            }
        };
        Mockito.when(repository.getMaxID()).thenReturn(repo_lists.size());//数据的最大id和数据库列表的长度一致
        Mockito.when(repository.findByIdBetweenOrderByIdDesc(anyInt(), anyInt())).thenAnswer(find_answer);
        doAnswer(trans_answer).when(transformService).transformListsTime(return_lists);//transformService的方法写在when().后面，而不是括号里
        //此处的answer没有任何返回，和transformListsTime的返回类型相匹配

        service.init();//执行初始化，不能忘记了

        verify(repository, times(1)).getMaxID();
        verify(repository, times(1)).findByIdBetweenOrderByIdDesc(anyInt(), anyInt());
        verify(transformService, times(1)).transformListsTime(any());

        assertAll(
                () -> assertNotNull(repo_lists),
                () -> assertEquals(10, repo_lists.size(), "数据库中共10条数据"),
                () -> assertNotNull(return_lists)
                //()->assertEquals(5,return_lists.size(),"返回最新的数据lists的长度应该是5")
        );

        assertAll(
                () -> assertEquals("AutoMessage10", return_lists.get(0).getContent(), "消息应为10"),
                () -> assertEquals("刚刚", return_lists.get(0).getStr_time(), "显示时间成功"),
                () -> assertEquals("AutoMessage9", return_lists.get(1).getContent(), "消息应为9"),
                () -> assertEquals("刚刚", return_lists.get(1).getStr_time(), "显示时间成功"),
                () -> assertEquals("AutoMessage8", return_lists.get(2).getContent(), "消息应为8"),
                () -> assertEquals("刚刚", return_lists.get(2).getStr_time(), "显示时间成功"),
                () -> assertEquals("AutoMessage7", return_lists.get(3).getContent(), "消息应为7"),
                () -> assertEquals("刚刚", return_lists.get(3).getStr_time(), "显示时间成功"),
                () -> assertEquals("AutoMessage6", return_lists.get(4).getContent(), "消息应为6"),
                () -> assertEquals("刚刚", return_lists.get(4).getStr_time(), "显示时间成功")
        );
    }

    @Test
    void update() {
        //首先插入10条数据，然后init()，返回消息5-10
        //立刻进行更新 检测:
        //1.新消息条数为0
        //2.return_lists不变
        //插入一条数据insert()后  检测:
        //3.新消息数目为1
        //再测进行更新update()
        //4.return_lists大小为6，第1条数据是最新插入的数据11

        List<Message> return_lists = new ArrayList<>();
        List<Message> repo_lists = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Long cur = new Date().getTime();
            repo_lists.add(new Message((i + 1), "Wangzh", "AutoMessage" + (i + 1), cur, sdf.format(cur), "/images/universe.png"));
            //System.out.println("AutoMessage"+(i+1));
        }

        Answer<List<Message>> find_answer = new Answer<List<Message>>() {//find_between的打桩方法
            @Override
            public List<Message> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return_lists.clear();
                return_lists.addAll(repo_lists.subList(5, 10));//这个子串下标从0开始，不包括结尾下标10，太坑了！
                Collections.reverse(return_lists);
                return return_lists;
            }
        };

        Answer<List<Message>> find_by_greater_answer = new Answer<List<Message>>() {
            @Override
            public List<Message> answer(InvocationOnMock invocationOnMock) throws Throwable {
                //System.out.println("Time n");
                if (service.getNewRecordNum() == 1) {
                    Message new_message = repo_lists.get(repo_lists.size() - 1);
                    return_lists.add(0, new_message);
                    //System.out.println(repo_lists.get(repo_lists.size()-1));
                }
                return return_lists;
            }
        };
        Answer<Message> save_answer = new Answer<Message>() {
            @Override
            public Message answer(InvocationOnMock invocationOnMock) throws Throwable {
                Long cur = new Date().getTime();
                Message message = new Message(11, "Wangzh", "AutoMessage11", cur, sdf.format(cur), "/images/universe.png");
                repo_lists.add(message);
                return message;
            }
        };

        Answer trans_answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                //System.out.println("Time n");
                for (Message message : return_lists) {
                    message.setStr_time("刚刚");
                    //System.out.println(message.getId());
                }
                return null;
            }
        };

        //stub 打桩
        Mockito.when(repository.getMaxID()).thenReturn(repo_lists.size());//数据的最大id和数据库列表的长度一致
        Mockito.when(repository.findByIdBetweenOrderByIdDesc(anyInt(), anyInt())).thenAnswer(find_answer);
        Mockito.when(repository.save(any())).thenAnswer(save_answer);//answer的返回类型要和save()的返回类型一致？否则会报错？
        Mockito.when(repository.findByIdGreaterThanOrderByIdDesc(anyInt())).thenAnswer(find_by_greater_answer);
        Mockito.doAnswer(trans_answer).when(transformService).transformListsTime(return_lists);//transformService的方法写在when().后面，而不是括号里

        //此处的answer没有任何返回，和transformListsTime的返回类型相匹配
        //问题是这里的方法被调用了四次，但是打桩方法只调用了一次？？

        //check 1,2
        service.init();
        service.update();
        assertAll(
                () -> assertEquals(0, service.getNewRecordNum(), "新消息数为0"),
                () -> assertNotNull(repo_lists),
                () -> assertNotNull(return_lists),
                () -> assertEquals(10, repo_lists.size()),
                () -> assertEquals(5, return_lists.size())
        );

        //check 3
        service.insert();
        assertAll(
                () -> assertEquals(1, service.getNewRecordNum(), "新消息数为1"),
                () -> assertNotNull(repo_lists),
                () -> assertNotNull(return_lists),
                () -> assertEquals(11, repo_lists.size()),
                () -> assertEquals(5, return_lists.size())
        );
        //check 4
        service.update();
        assertAll(
                () -> assertEquals(0, service.getNewRecordNum(), "新消息数为0"),
                () -> assertNotNull(repo_lists),
                () -> assertNotNull(return_lists),
                () -> assertEquals(11, repo_lists.size()),
                () -> assertEquals(6, return_lists.size())
        );
        Message message = return_lists.get(0);//最新消息
        assertAll(
                () -> assertEquals(11, message.getId(), "最新消息id应该是11"),
                () -> assertEquals("Wangzh", message.getName()),
                () -> assertEquals("AutoMessage11", message.getContent()),
                //()->assertEquals(cur,message.getTime()),//插入时间具体不可考，但是其字符串形式已经被更新成为"刚刚"
                //()->assertEquals("刚刚",message.getStr_time()),这个验证不太对……
                () -> assertEquals("/images/universe.png", message.getImage())
        );

        verify(repository, times(2)).getMaxID();
        verify(repository, times(1)).findByIdBetweenOrderByIdDesc(anyInt(), anyInt());
        verify(repository, times(2)).findByIdGreaterThanOrderByIdDesc(anyInt());
        verify(transformService, times(4)).transformListsTime(any());
    }

    @Test
    void showmore() {
        //先插入10条数据，然后init()，再来showmore
        //验证:
        //1.init()后的return_lists大小是5 消息是 10 9 8 7 6
        //2.showmore()后的return lists大小是10 消息是10 9 8 7 6  5 4 3 2 1

        List<Message> return_lists = new ArrayList<>();
        List<Message> repo_lists = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Long cur = new Date().getTime();
            repo_lists.add(new Message((i + 1), "Wangzh", "AutoMessage" + (i + 1), cur, sdf.format(cur), "/images/universe.png"));
            //System.out.println("AutoMessage"+(i+1));
        }

        Answer<List<Message>> find_answer_between_init = new Answer<List<Message>>() {//find_between的打桩方法
            @Override
            public List<Message> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return_lists.clear();
                return_lists.addAll(repo_lists.subList(5, 10));//这个子串下标从0开始，不包含结尾下标10，太坑了！
                Collections.reverse(return_lists);
                return return_lists;
            }
        };
        Answer<List<Message>> find_answer_between_showmore = new Answer<List<Message>>() {//find_between的打桩方法
            @Override
            public List<Message> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return_lists.clear();
                return_lists.addAll(repo_lists.subList(0, 10));//和前面不同，下标从1开始读取
                Collections.reverse(return_lists);
                return return_lists;
            }
        };

        Answer trans_answer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                //System.out.println("Time n");
                for (Message message : return_lists) {
                    message.setStr_time("刚刚");
                    //System.out.println(message.getId());
                }
                return null;
            }
        };

        //stub 打桩
        Mockito.when(repository.getMaxID()).thenReturn(repo_lists.size());//数据的最大id和数据库列表的长度一致
        Mockito.when(repository.findByIdBetweenOrderByIdDesc(anyInt(), anyInt())).thenAnswer(find_answer_between_init)
                .thenAnswer(find_answer_between_showmore);
        Mockito.doAnswer(trans_answer).when(transformService).transformListsTime(return_lists);//transformService的方法写在when().后面，而不是括号里

        //check 1
        service.init();
        assertAll(
                () -> assertNotNull(repo_lists),
                () -> assertNotNull(return_lists),
                () -> assertEquals(10, repo_lists.size(), "初始化时，仓库中有10条纪录"),
                () -> assertEquals(5, return_lists.size(), "初始始化时，返回5条纪录")
        );
        //check 2
        service.showmore();
        assertAll(
                () -> assertNotNull(repo_lists),
                () -> assertNotNull(return_lists),
                () -> assertEquals(10, repo_lists.size(), "显示更多纪录时，仓库中仍有10条纪录"),
                () -> assertEquals(10, return_lists.size(), "显示更多时，共返回5+5=10条纪录")
        );
        assertAll(
                () -> assertNotNull(return_lists),
                () -> assertEquals(10, return_lists.size(), "获得lists的长度应该是10"),
                () -> assertEquals("AutoMessage10", return_lists.get(0).getContent()),

                () -> assertEquals("AutoMessage9", return_lists.get(1).getContent()),

                () -> assertEquals("AutoMessage8", return_lists.get(2).getContent()),

                () -> assertEquals("AutoMessage7", return_lists.get(3).getContent()),

                () -> assertEquals("AutoMessage6", return_lists.get(4).getContent()),

                () -> assertEquals("AutoMessage5", return_lists.get(5).getContent()),

                () -> assertEquals("AutoMessage4", return_lists.get(6).getContent()),

                () -> assertEquals("AutoMessage3", return_lists.get(7).getContent()),

                () -> assertEquals("AutoMessage2", return_lists.get(8).getContent()),

                () -> assertEquals("AutoMessage1", return_lists.get(9).getContent())
        );

        verify(repository, times(2)).findByIdBetweenOrderByIdDesc(anyInt(), anyInt());
        verify(transformService, times(2)).transformListsTime(any());
    }
}






