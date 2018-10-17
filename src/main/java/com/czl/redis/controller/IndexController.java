package com.czl.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    StringRedisTemplate template;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @ResponseBody
    @RequestMapping("string")
    public Object index() {
        ValueOperations<String, String> operations = template.opsForValue();
        operations.set("stringKey","hello my redis demo");
        return  operations.get("stringKey");
    }
    @ResponseBody
    @RequestMapping("list")
    public Object list() {
        ListOperations<String, String> operations = template.opsForList();
        operations.leftPush("listKey","one");
        operations.leftPush("listKey","two");
        operations.leftPush("listKey","three");
        operations.rightPush("listKey","one");
        operations.rightPush("listKey","two");
        operations.rightPush("listKey","three");
        List<String> listKey = operations.range("listKey", 0, -1);
        return listKey;
    }
    @ResponseBody
    @RequestMapping("hash")
    public Object hash() {
        HashOperations<String, Object, Object> operations = template.opsForHash();
        Map<String,String> map = new HashMap<String,String>(){
            {
                put("book1","西游记");
                put("book2","三国演义");
                put("book3","红楼梦");
                put("book4","水浒传");
            }
        };
        operations.putAll("mapKey",map);
        return operations.entries("mapKey");
    }
    @ResponseBody
    @RequestMapping("set")
    public Object set() {
        SetOperations<String, String> operations = template.opsForSet();
        operations.add("setKey",new String[]{"西游记","三国演义","红楼梦","水浒传"});
        return operations.members("setKey");
    }
    @ResponseBody
    @RequestMapping("zSet")
    public Object zSet() {
        ZSetOperations<String, String> operations = template.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ZSetOperations.TypedTuple<String> tuple =
                    new DefaultTypedTuple<>("西游记"+(i+1),(i+1.0));
            tuples.add(tuple);
        }
        operations.add("zSetKey", tuples);
        return operations.rangeWithScores("zSetKey",0,-1);
    }
    @ResponseBody
    @RequestMapping("zSetRemove")
    public Object zSetRemove() {
        ZSetOperations<String, String> operations = template.opsForZSet();
        operations.removeRange("zSetKey",0,-5);
        return operations.rangeWithScores("zSetKey",0,-1);
    }

}
