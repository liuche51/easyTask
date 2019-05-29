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
    private Task target;
    public ProxyFactory(Task target) {
        this.target = target;
    }

    public Object getProxyInstance() {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        log.debug("任务:{} 代理执行开始", target.getId());
                        try {
                            return method.invoke(target, args);
                        } catch (Exception e) {
                            log.error("target proxy method execute exception！task.id="+target.getId(), e);
                            throw e;
                        }finally {
                            log.debug("任务:{} 代理执行结束", target.getId());
                            boolean ret = ScheduleDao.delete(target.getId());
                            if (ret)
                                log.debug("任务:{} 执行完成，已从持久化记录中删除", target.getId());
                        }
                    }
                }
        );
    }

}
