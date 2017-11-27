package com.example.admin.queue;

/**
 * Created by LiuLei on 2017/11/24.
 */

public class ResultFactory {

    public static Result createResult(String name){
        return new Result(name);
    }
}
