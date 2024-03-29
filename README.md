# easyTask

* 一个方便触发一次性或周期性任务执行的工具包，支持海量,高并发,高可用,宕机自动恢复任务
* A convenient and trigger a one-time or periodic task execution toolkit, support mass, high concurrency, high availability, downtime automatic recovery tasks
* 当前项目已经不在继续维护，后续主推easyTask-X项目，一个独立的分布式中间件实现方案
## Usage scenarios

* 乘坐网约车结单后30分钟若顾客未评价，则系统将默认提交一条评价信息
* 银行充值接口，要求5分钟后才可以查询到结果成功OR失败
* 会员登录系统30秒后自动发送一条登录短信通知
* 每个登录用户每隔10秒统计一次其某活动中获得的积分

## Features

* 使用简单
* 秒级精度任务执行计划
* 支持海量任务提交执行
* 支持高并发执行任务
* 支持任务持久化，宕机自动恢复任务计划
* 支持自定义线程池、任务持久化保存路径
* easy to use
* second precision task execution plan
* supports massive task commit execution
* supports highly concurrent execution of tasks
* supports task persistence, and automatically recovers task plan when down
* support for custom thread pool, task persistent save path

## Architecture

![Architecture](https://images.cnblogs.com/cnblogs_com/liuche/1811577/o_200722062635%E5%9B%BE%E7%89%872.png)

## Getting started

* pom添加引用
```xml
<dependency>
    <groupId>com.github.liuche51</groupId>
    <artifactId>easyTask</artifactId>
    <version>1.2.2</version>
</dependency>
```

* 定义好您要执行的任务类  Define the task class you want to perform
```java
public class CusTask1 extends Schedule implements Runnable {
    private static Logger log = LoggerFactory.getLogger(CusTask1.class);

    @Override
    public void run() {
        Map<String, String> param = getParam();
        if (param != null && param.size() > 0)
            log.info("任务1已执行!姓名:{} 生日:{} 年龄:{} 线程ID:{}", param.get("name"), param.get("birthday"), param.get("age"),param.get("threadid"));

    }
}
```
* 简单应用示例代码   Simply apply the sample code
```java
public class Main {
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    private static Object obj=new Object();
    public static void main(String[] args){
        try {
            annularQueue.start();
            CusTask1 task1 = new CusTask1();
            task1.setEndTimestamp(ZonedDateTime.now().plusSeconds(10).toInstant().toEpochMilli());
            annularQueue.submit(task1);
            obj.wait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```
* 高级应用示例代码   Advance apply the sample code
```java
public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    private static Object obj=new Object();
    public static void main(String[] args){
        allcustomSimpleSetTest();
    }
    static void allcustomSimpleSetTest(){
        try {
            annularQueue.start();
            CusTask1 task1 = new CusTask1();
            task1.setEndTimestamp(ZonedDateTime.now().plusSeconds(10).toInstant().toEpochMilli());
            Map<String,String> param=new HashMap<String,String>(){
                {
                    put("name","刘彻");
                    put("birthday","1988-1-1");
                    put("age","25");
                    put("threadid",String.valueOf(Thread.currentThread().getId()));
                }
            };
            task1.setParam(param);
            CusTask1 task2 = new CusTask1();
            task2.setPeriod(30);
            task2.setImmediateExecute(true);
            task2.setTaskType(TaskType.PERIOD);
            task2.setUnit(TimeUnit.SECONDS);
            Map<String,String> param2=new HashMap<String,String>(){
                {
                    put("name","Jack");
                    put("birthday","1986-1-1");
                    put("age","32");
                    put("threadid",String.valueOf(Thread.currentThread().getId()));
                }
            };
            task2.setParam(param2);
            annularQueue.submit(task1);
            annularQueue.submit(task2);
            obj.wait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```
* 性能监控示例 performance monitoring example
```java
log.debug("getDBScheduleCount="+Monitor.getDBScheduleCount());
log.debug("getDispatchsPoolWaiteToExecuteScheduleCount="+Monitor.getDispatchsPoolWaiteToExecuteScheduleCount());
log.debug("getWorkersPoolWaiteToExecuteScheduleCount="+Monitor.getWorkersPoolWaiteToExecuteScheduleCount());
log.debug("getScheduleInAnnularQueueCount="+Monitor.getScheduleInAnnularQueueCount());
log.debug("getDispatchsPoolExecutedScheduleCount="+Monitor.getDispatchsPoolExecutedScheduleCount());
log.debug("getWorkersPoolExecutedScheduleCount="+Monitor.getWorkersPoolExecutedScheduleCount());
```

## Notice

* 此构件已在Windows和centos下做了适当测试，如需使用，请自行测试
* 为了更好的保证系统故障自动恢复任务，请自定义程序任务持久化文件保存的路径(不同应用文件路径定义不同为好，以免被其他应用覆盖)，并确保读写权限。如果以   jar包运行,文件默认在同级目录；如果以war包在tomcat下运行，文件默认在tomcat的bin目录下。
* 如果您在使用过程中遇到问题，可以在这里提交
* this component has been properly tested with Windows and centos
* in order to better ensure the automatic recovery of the system failure task, please customize the program task persistence     file saved path (different application file path definition is different, so as not to be overwritten by other applications), and ensure read and write permissions.If run as a jar, the file defaults to the same directory.If you run as a war package under tomcat, the file defaults to the tomcat bin directory.
* if you encounter problems during use, you can submit it here
