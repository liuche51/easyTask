package liuche.opensource.easyTask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

 class ProxyFactory {
    private static Logger log = LoggerFactory.getLogger(ProxyFactory.class);
    private Object target;

    public ProxyFactory(Object target) {
        this.target = target;
    }

    public Object getProxyInstance() {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Task task = (Task) target;
                        log.debug("任务:{} 代理执行开始", task.getId());
                        Object returnValue = method.invoke(target, args);
                        log.debug("任务:{} 代理执行结束", task.getId());
                        boolean ret = ScheduleDao.delete(task.getId());
                        if (ret)
                            log.debug("任务:{} 执行完成，已从持久化记录中删除", task.getId());
                        return returnValue;
                    }
                }
        );
    }

}
