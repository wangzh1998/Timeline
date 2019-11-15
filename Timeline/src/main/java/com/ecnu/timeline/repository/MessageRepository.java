package com.ecnu.timeline.repository;

import com.ecnu.timeline.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {

    @Query(value = "select max(ID) from message", nativeQuery = true)
    Integer getMaxID();//获取当前数据库中的最新一条纪录的ID

    //找出未被返回过的最新数据，按照降序排序
    List<Message> findByIdGreaterThanOrderByIdDesc(Integer cur_max);

    //根据起止ID来进行查找，按照降序排序
    List<Message> findByIdBetweenOrderByIdDesc(Integer start, Integer end);
}
