/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-18
 * 
 */
package com.dianping.phoenix.lb.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.phoenix.lb.dao.ModelStore;
import com.dianping.phoenix.lb.dao.VirtualServerDao;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;

/**
 * @author Leo Liang
 * 
 */
@Service
public class VirtualServerDaoImpl extends AbstractDao implements VirtualServerDao {

    /**
     * @param store
     */
    @Autowired(required = true)
    public VirtualServerDaoImpl(ModelStore store) {
        super(store);
    }

    @Override
    public VirtualServer find(String virtualServerName) {
        return store.findVirtualServer(virtualServerName);
    }

    @Override
    public void add(VirtualServer virtualServer) throws BizException {
        store.addVirtualServer(virtualServer.getName(), virtualServer);
    }

    @Override
    public void update(VirtualServer virtualServer) throws BizException {
        store.updateVirtualServer(virtualServer.getName(), virtualServer);
    }

    @Override
    public List<VirtualServer> list() {
        return store.listVirtualServers();
    }

    @Override
    public void delete(String virtualServerName) throws BizException {
        store.removeVirtualServer(virtualServerName);
    }

}
