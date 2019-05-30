package liuche.opensource.easyTask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

class ProxyFactory {
    private static Logger log = LoggerFactory.getLogger(ProxyFactory.class);
    private Schedule target;
    public ProxyFactory(Schedule target) {
        this.target = target;
    }

    public Object getProxyInstance() {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //以下三行代码不可调换顺序，多线程环境下存在判断出错的概率
                        String id=target.getId();
                        if(target.getScheduleExt().getOldId()!=null)
                            id=target.getScheduleExt().getOldId();
                        log.debug("任务:{} 代理执行开始", id);
                        try {
                            return method.invoke(target, args);
                        } catch (Exception e) {
                            log.error("target proxy method execute exception！task.id="+id, e);
                            throw e;
                        }finally {
                            log.debug("任务:{} 代理执行结束", id);
                            boolean ret = ScheduleDao.delete(id);
                            if (ret)
                                log.debug("任务:{} 执行完成，已从持久化记录中删除", id);
                        }
                    }
                }
        );
    }

}
