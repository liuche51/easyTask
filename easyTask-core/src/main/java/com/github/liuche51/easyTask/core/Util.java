package com.github.liuche51.easyTask.core;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Util {
    public static AtomicLong GREACE=new AtomicLong(0);
    public static String generateUniqueId(){
        StringBuilder str=new StringBuilder(UUID.randomUUID().toString().replace("-",""));
        str.append("-");
        str.append(Thread.currentThread().getId());
        return str.toString();
    }
}
