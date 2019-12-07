package cn.orditech.stockanalysis.catcher.service;

import cn.orditech.stockanalysis.catcher.CatchTask;
import cn.orditech.stockanalysis.catcher.enums.TaskTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 任务队列服务
 *
 * @author kimi
 */
@Service
public class TaskQueueService {
    private final Logger LOGGER = LoggerFactory.getLogger (TaskQueueService.class);

    /**
     * FIXME 考虑将任务落入数据库，避免停机引起的任务丢失
     */
    private final Map<TaskTypeEnum, LinkedBlockingQueue<CatchTask>> queueMap = new ConcurrentHashMap<>();

    /**
     * 任务有效性验证
     * @param task
     * @return
     */
    private boolean validTask (CatchTask task) {
        if (null == task) {
            return false;
        }
        if (StringUtils.isBlank (task.getUrl ()) || StringUtils.isBlank (task.getType ())) {
            return false;
        }
        if (TaskTypeEnum.getByCode (task.getType ()) == null) {
            return false;
        }
        return true;
    }

    /**
     * 提交任务
     *
     * @param task 任务实体
     */
    public void commitTask (TaskTypeEnum type, CatchTask task) {
        // 任务校验
        assert (validTask (task));

        try {
            LinkedBlockingQueue<CatchTask> taskQueue = queueMap.get (type);
            if (taskQueue != null) {
                taskQueue.put (task);
            } else {
                synchronized (queueMap) {
                    if((taskQueue = queueMap.get(type)) == null) {
                        taskQueue = new LinkedBlockingQueue<>();
                        queueMap.put (type, taskQueue);
                    }
                }
                taskQueue.put (task);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            LOGGER.error ("taskGenerate,异常信息:{}", e.getMessage ());
        }
    }

    /**
     * 重置任务
     * @param task
     */
    public void paybackTask(CatchTask task){
        TaskTypeEnum type = TaskTypeEnum.getByCode (task.getType ());
        commitTask(type,task);
    }

    /**
     * 根据任务类型读取一条任务
     *
     * @param type
     * @return
     */
    public CatchTask getTask (TaskTypeEnum type) {
        LinkedBlockingQueue<CatchTask> taskQueue = queueMap.get (type);
        if (taskQueue != null) {
            return taskQueue.poll ();
        } else {
            return null;
        }
    }

    /**
     * 获取当前任务类型集合，返回结果中排除了没有任务的类型
     *
     * @return
     */
    public Set<TaskTypeEnum> getTypeSet () {
        Set<TaskTypeEnum> taskQueue = new HashSet<> ();
        for (TaskTypeEnum type : queueMap.keySet ()) {
            if (queueMap.get (type).size () > 0) {
                taskQueue.add (type);
            }
        }
        return taskQueue;
    }
}
