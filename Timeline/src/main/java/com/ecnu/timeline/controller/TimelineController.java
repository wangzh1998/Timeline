package com.ecnu.timeline.controller;

import com.ecnu.timeline.service.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


//@RestController
@Controller
public class TimelineController {

    @Autowired
    TimelineService service;

    @RequestMapping("/timeline")
    public String init(ModelMap map){
        map.addAttribute("lists",service.init());
        map.addAttribute("new_record_num",service.getNewRecordNum());
        return "timeline";
    }

    @RequestMapping("/timeline/insert")
    public String insert(ModelMap map){
        map.addAttribute("lists",service.insert());
        map.addAttribute("new_record_num",service.getNewRecordNum());
        return "timeline";
    }

    @RequestMapping("/timeline/update")
    public String update(ModelMap map){//增加显示目前更新的所有数据
        map.addAttribute("lists",service.update());
        map.addAttribute("new_record_num",service.getNewRecordNum());
        return "timeline";
    }

    @RequestMapping("/timeline/showmore")
    public String showmore(ModelMap map){//向下显示5条数据
        map.addAttribute("lists",service.showmore());
        map.addAttribute("new_record_num",service.getNewRecordNum());
        return "timeline";
    }


/*
    @RequestMapping("/init")
    public List<Message> init(){
        return service.init();
    }

    @RequestMapping("/insert")
    public void insert(){
        service.insert();
    }

    @RequestMapping("/shownew")
    public Integer shownew(){
        return service.getNewRecordNum();
    }

    @RequestMapping("/update")
    public List<Message> upload(){
        return service.update();
    }

    @RequestMapping("/showmore")
    public List<Message> showmore(){
        return service.showmore();
    }*/
}
