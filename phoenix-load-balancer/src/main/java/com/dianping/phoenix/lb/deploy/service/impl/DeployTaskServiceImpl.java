package com.dianping.phoenix.lb.deploy.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.apache.ibatis.session.RowBounds;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.phoenix.lb.PlexusComponentContainer;
import com.dianping.phoenix.lb.action.Paginator;
import com.dianping.phoenix.lb.configure.ConfigManager;
import com.dianping.phoenix.lb.deploy.bo.DeployAgentBo;
import com.dianping.phoenix.lb.deploy.bo.DeployTaskBo;
import com.dianping.phoenix.lb.deploy.bo.DeployVsBo;
import com.dianping.phoenix.lb.deploy.bo.NewTaskInfo;
import com.dianping.phoenix.lb.deploy.bo.NewTaskInfo.VsAndTag;
import com.dianping.phoenix.lb.deploy.dao.DeployAgentMapper;
import com.dianping.phoenix.lb.deploy.dao.DeployTaskMapper;
import com.dianping.phoenix.lb.deploy.dao.DeployVsMapper;
import com.dianping.phoenix.lb.deploy.model.DeployAgent;
import com.dianping.phoenix.lb.deploy.model.DeployAgentExample;
import com.dianping.phoenix.lb.deploy.model.DeployAgentStatus;
import com.dianping.phoenix.lb.deploy.model.DeployTask;
import com.dianping.phoenix.lb.deploy.model.DeployTaskExample;
import com.dianping.phoenix.lb.deploy.model.DeployTaskStatus;
import com.dianping.phoenix.lb.deploy.model.DeployVs;
import com.dianping.phoenix.lb.deploy.model.DeployVsExample;
import com.dianping.phoenix.lb.deploy.model.DeployVsStatus;
import com.dianping.phoenix.lb.deploy.model.StateAction;
import com.dianping.phoenix.lb.deploy.service.DeployTaskService;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.entity.VirtualServer;
import com.dianping.phoenix.lb.service.model.VirtualServerService;

@Service
public class DeployTaskServiceImpl implements DeployTaskService {

    private static final int     PAGE_SIZE    = 15;

    @SuppressWarnings("unused")
    private ConfigManager        configManager;

    @Autowired
    private DeployVsMapper       deployVsMapper;

    @Autowired
    private DeployAgentMapper    deployAgentMapper;

    @Autowired
    private DeployTaskMapper     deployTaskMapper;

    @Autowired
    private VirtualServerService virtualServerService;

    private int                  MAX_PAGE_NUM = 50;

    @PostConstruct
    public void init() throws ComponentLookupException {
        configManager = PlexusComponentContainer.INSTANCE.lookup(ConfigManager.class);
    }

    @Override
    public List<DeployTask> list(Paginator paginator, int pageNum) {
        if (pageNum > MAX_PAGE_NUM || pageNum <= 0) {
            pageNum = 1;
        }

        DeployTaskExample example = new DeployTaskExample();
        example.setOrderByClause("creation_date DESC");

        int count = deployTaskMapper.countByExample(example);

        paginator.setItemsPerPage(PAGE_SIZE);
        paginator.setItems(count);
        paginator.setPage(pageNum);

        if (pageNum > paginator.getLastPage()) {
            pageNum = paginator.getLastPage();
        }
        int offset = PAGE_SIZE * (pageNum - 1);
        int limit = PAGE_SIZE;
        RowBounds rowBounds = new RowBounds(offset, limit);

        return deployTaskMapper.selectByExampleWithRowbounds(example, rowBounds);
    }

    @Override
    public DeployTaskBo getTask(long taskId) throws BizException {
        DeployTask deployTask = deployTaskMapper.selectByPrimaryKey(taskId);

        DeployTaskBo deployTaskBo = new DeployTaskBo();
        buildTaskBo(taskId, deployTaskBo);

        deployTaskBo.setTask(deployTask);

        return deployTaskBo;
    }

    @Override
    public List<DeployTaskBo> getTasksByStatus(DeployTaskStatus status) throws BizException {
        List<DeployTaskBo> re = new ArrayList<DeployTaskBo>();

        DeployTaskExample example = new DeployTaskExample();
        example.createCriteria().andStatusEqualTo(status);
        List<DeployTask> deployTasks = deployTaskMapper.selectByExample(example);

        for (DeployTask deployTask : deployTasks) {
            DeployTaskBo deployTaskBo = new DeployTaskBo();

            buildTaskBo(deployTask.getId(), deployTaskBo);

            deployTaskBo.setTask(deployTask);
            re.add(deployTaskBo);
        }

        return re;
    }

    @Override
    public List<DeployTaskBo> getTasksByStateAction(StateAction stateAction) throws BizException {
        List<DeployTaskBo> re = new ArrayList<DeployTaskBo>();

        DeployTaskExample example = new DeployTaskExample();
        example.createCriteria().andStateActionEqualTo(stateAction);
        List<DeployTask> deployTasks = deployTaskMapper.selectByExample(example);

        for (DeployTask deployTask : deployTasks) {
            DeployTaskBo deployTaskBo = new DeployTaskBo();

            buildTaskBo(deployTask.getId(), deployTaskBo);

            deployTaskBo.setTask(deployTask);
            re.add(deployTaskBo);
        }

        return re;
    }

    private void buildTaskBo(long taskId, DeployTaskBo task) throws BizException {
        DeployVsExample example = new DeployVsExample();
        example.createCriteria().andDeployTaskIdEqualTo(taskId);
        List<DeployVs> deployVsList = deployVsMapper.selectByExampleWithBLOBs(example);

        List<DeployVsBo> deployVsBos = new ArrayList<DeployVsBo>();
        for (DeployVs deployVs : deployVsList) {
            DeployVsBo deployVsBo = new DeployVsBo();
            DeployAgentExample example2 = new DeployAgentExample();
            example2.createCriteria().andDeployVsIdEqualTo(deployVs.getId());
            List<DeployAgent> deployAgents = deployAgentMapper.selectByExampleWithBLOBs(example2);
            VirtualServer vs = virtualServerService.findVirtualServer(deployVs.getVsName());

            deployVsBo.setDeployVs(deployVs);
            deployVsBo.setVs(vs);
            deployVsBo.setDeployAgentBos(convertDetailsToMap(deployAgents));

            deployVsBos.add(deployVsBo);
        }
        task.setDeployVsBos(convertDeployVssToMap(deployVsBos));
    }

    private Map<String, DeployVsBo> convertDeployVssToMap(List<DeployVsBo> deployVsBos) {
        Map<String, DeployVsBo> map = new HashMap<String, DeployVsBo>();
        if (deployVsBos != null) {
            for (DeployVsBo deployVsBo : deployVsBos) {
                String vsName = deployVsBo.getVs().getName();
                map.put(vsName, deployVsBo);
            }
        }
        return map;
    }

    private Map<String, DeployAgentBo> convertDetailsToMap(List<DeployAgent> deployAgents) {
        Map<String, DeployAgentBo> map = new HashMap<String, DeployAgentBo>();
        for (DeployAgent deployAgent : deployAgents) {
            String ip = deployAgent.getIpAddress();
            map.put(ip, new DeployAgentBo(deployAgent));
        }
        return map;
    }

    @Override
    public void addTask(NewTaskInfo newTaskInfo) {
        validate(newTaskInfo);
        DeployTask task = new DeployTask();
        task.setName(newTaskInfo.getTaskName());
        task.setLastModifiedDate(new Date());
        task.setStatus(DeployTaskStatus.CREATED);
        deployTaskMapper.insert(task);

        for (VsAndTag vsAndTag : newTaskInfo.getSelectedVsAndTags()) {
            DeployVs deployVs = new DeployVs();
            deployVs.setVsName(vsAndTag.getVsName());
            deployVs.setVsTag(vsAndTag.getTag());
            deployVs.setDeployTaskId(task.getId());
            deployVs.setStatus(DeployVsStatus.READY);
            deployVs.setLastModifiedDate(new Date());
            deployVsMapper.insertSelective(deployVs);
        }
    }

    private void validate(NewTaskInfo newTaskInfo) {
        Validate.notEmpty(newTaskInfo.getTaskName(), "Task's name can not be empty!");
        Validate.notEmpty(newTaskInfo.getSelectedVsAndTags(), "Must add one vs and tag at least !");
        Set<String> set = new HashSet<String>();
        for (VsAndTag vsAndTag : newTaskInfo.getSelectedVsAndTags()) {
            Validate.notEmpty(vsAndTag.getVsName(), "Vs's name can not be empty!");
            Validate.notEmpty(vsAndTag.getTag(), "Vs's tag can not be empty!");
            Validate.isTrue(set.add(vsAndTag.getVsName()), "Vs's name can not be duplicate！");
        }
    }

    @Override
    public void updateTask(DeployTaskBo deployTaskBo) {
        //验证
        validate(deployTaskBo);
        //更新发布策略
        DeployTask task0 = new DeployTask();
        DeployTask task = deployTaskBo.getTask();
        task0.setId(task.getId());
        task0.setAutoContinue(task.getAutoContinue());
        task0.setDeployInterval(task.getDeployInterval());
        task0.setAgentBatch(task.getAgentBatch());
        task0.setErrorPolicy(task.getErrorPolicy());
        //添加DeployAgent
        Map<String, DeployVsBo> vsBos = deployTaskBo.getDeployVsBos();
        for (DeployVsBo vsBo : vsBos.values()) {
            DeployVs vs = vsBo.getDeployVs();
            DeployVs vs0 = new DeployVs();
            vs0.setId(vs.getId());
            vs0.setStatus(DeployVsStatus.READY);
            Map<String, DeployAgentBo> agents = vsBo.getDeployAgentBos();
            if (agents != null) {
                for (DeployAgentBo agentBo : agents.values()) {
                    DeployAgent deployAgent = new DeployAgent();
                    deployAgent.setDeployVsId(vs.getId());
                    deployAgent.setStatus(DeployAgentStatus.READY);
                    deployAgent.setIpAddress(agentBo.getDeployAgent().getIpAddress());
                    deployAgent.setLastModifiedDate(new Date());
                    deployAgentMapper.insertSelective(deployAgent);
                }
            }
            deployVsMapper.updateByPrimaryKeySelective(vs0);
        }
        task0.setStatus(DeployTaskStatus.READY);
        deployTaskMapper.updateByPrimaryKeySelective(task0);
    }

    private void validate(DeployTaskBo deployTaskBo) {
        //id不为null
        Validate.notNull(deployTaskBo.getTask().getId(), "Id can not be null!");
        //策略字段不为null
        Validate.notNull(deployTaskBo.getTask().getAutoContinue(), "AutoContinue can not be null!");
        if (deployTaskBo.getTask().getAutoContinue()) {
            Validate.notNull(deployTaskBo.getTask().getDeployInterval(), "DeployInterval can not be null!");
        }
        Validate.notNull(deployTaskBo.getTask().getAgentBatch(), "DeployPolicy can not be null!");
        //agent都必选
        Map<String, DeployVsBo> vsBos = deployTaskBo.getDeployVsBos();
        Validate.notNull(vsBos, "agent host must selected!");
        Validate.notEmpty(vsBos.values(), "agent host must selected!");
        for (DeployVsBo vsBo : vsBos.values()) {
            Map<String, DeployAgentBo> agents = vsBo.getDeployAgentBos();
            Validate.notNull(agents, "agent host must selected!");
            Validate.notEmpty(agents.values(), "agent host must selected!");
        }
    }

    @Override
    public void updateDeployTaskStatus(DeployTask deployTask) {
        DeployTask deployTask0 = new DeployTask();
        deployTask0.setId(deployTask.getId());
        deployTask0.setStatus(deployTask.getStatus());

        deployTaskMapper.updateByPrimaryKeySelective(deployTask0);
    }

    @Override
    public void updateDeployVsStatus(DeployVs deployVs) {
        DeployVs deployVs0 = new DeployVs();
        deployVs0.setId(deployVs.getId());
        deployVs0.setStatus(deployVs.getStatus());

        deployVsMapper.updateByPrimaryKeySelective(deployVs0);

    }

    @Override
    public void updateDeployAgentStatusAndLog(DeployAgent deployAgent) {
        if (deployAgent.getStatus() == DeployAgentStatus.PROCESSING) {
            return;
        }
        DeployAgent deployAgent0 = new DeployAgent();
        deployAgent0.setId(deployAgent.getId());
        deployAgent0.setStatus(deployAgent.getStatus());
        if (deployAgent.getRawLog() != null) {
            deployAgent0.setRawLog(deployAgent.getRawLog());
        }

        deployAgentMapper.updateByPrimaryKeySelective(deployAgent0);
    }

    @Override
    public void updateDeployVsSummaryLog(DeployVs deployVs) {
        DeployVs deployVs0 = new DeployVs();
        deployVs0.setId(deployVs.getId());
        deployVs0.setSummaryLog(deployVs.getSummaryLog());

        deployVsMapper.updateByPrimaryKeySelective(deployVs0);

    }

    @Override
    public void updateDeployTaskStateAction(DeployTask deployTask) {
        DeployTask deployTask0 = new DeployTask();
        deployTask0.setId(deployTask.getId());
        deployTask0.setStateAction(deployTask.getStateAction());

        deployTaskMapper.updateByPrimaryKeySelective(deployTask0);
    }

}
