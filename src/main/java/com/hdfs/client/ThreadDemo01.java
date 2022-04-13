package com.hdfs.client;

import java.io.IOException;
import java.net.URISyntaxException;

public class ThreadDemo01 extends Thread{
    public ThreadDemo01(){
        //编写子类的构造方法，可缺省
    }
    public void run(){
        //编写自己的线程代码
        System.out.println(Thread.currentThread().getName());
        System.out.println("aaaaaa");



        HdfsClient a = new HdfsClient();
        try {
            a.before();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            a.put();
            System.out.println(Thread.currentThread().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            a.after();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws InterruptedException {
        ThreadDemo01 threadDemo01 = new ThreadDemo01();
        ThreadDemo01 threadDemo02 = new ThreadDemo01();
        ThreadDemo01 threadDemo03 = new ThreadDemo01();
        threadDemo01.setName("我是自定义的线程1");
        threadDemo01.start();
        threadDemo02.setName("我是自定义的线程2");
//        sleep(10000);
        threadDemo02.start();
        threadDemo03.setName("我是自定义的线程3");
//        sleep(10000);
        threadDemo03.start();

        System.out.println(Thread.currentThread().toString()+"xxx");
    }
}