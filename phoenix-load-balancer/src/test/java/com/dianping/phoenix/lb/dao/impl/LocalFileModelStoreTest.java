/**
 * Project: phoenix-load-balancer
 * 
 * File Created at 2013-10-21
 * 
 */
package com.dianping.phoenix.lb.dao.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.dianping.phoenix.lb.constant.MessageID;
import com.dianping.phoenix.lb.exception.BizException;
import com.dianping.phoenix.lb.model.Availability;
import com.dianping.phoenix.lb.model.State;
import com.dianping.phoenix.lb.model.configure.entity.Configure;
import com.dianping.phoenix.lb.model.configure.entity.Directive;
import com.dianping.phoenix.lb.model.configure.entity.Instance;
import com.dianping.phoenix.lb.model.configure.entity.Location;
import com.dianping.phoenix.lb.model.configure.entity.Member;
import com.dianping.phoenix.lb.model.configure.entity.Pool;
import com.dianping.phoenix.lb.model.configure.entity.Strategy;
import com.dianping.phoenix.lb.model.configure.entity.VirtualServer;
import com.dianping.phoenix.lb.model.configure.transform.DefaultMerger;
import com.dianping.phoenix.lb.model.configure.transform.DefaultSaxParser;

/**
 * @author Leo Liang
 * 
 */
public class LocalFileModelStoreTest {
    private LocalFileModelStoreImpl store;
    private File                    baseDir;
    private File                    tmpDir;

    @Before
    public void before() throws Exception {
        baseDir = new File(".", "src/test/resources/storeTest");
        tmpDir = new File(System.getProperty("java.io.tmpdir"), "model-test");
        if (tmpDir.exists()) {
            FileUtils.forceDelete(tmpDir);
        }
        FileUtils.copyDirectory(baseDir, tmpDir);
        store = new LocalFileModelStoreImpl();
        store.setBaseDir(tmpDir.getAbsolutePath());
        store.init();
    }

    @After
    public void after() throws Exception {
        if (tmpDir.exists()) {
            tmpDir.setWritable(true);
            FileUtils.forceDelete(tmpDir);
        }
    }

    @Test
    public void testListVirtualServers() throws Exception {
        Configure wwwConfigure = DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir,
                "configure_www.xml")));

        Configure tuangouConfigure = DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir,
                "configure_tuangou.xml")));

        List<VirtualServer> expected = new ArrayList<VirtualServer>(tuangouConfigure.getVirtualServers().values()
                .size()
                + wwwConfigure.getVirtualServers().values().size());
        expected.addAll(tuangouConfigure.getVirtualServers().values());
        expected.addAll(wwwConfigure.getVirtualServers().values());

        List<VirtualServer> actual = store.listVirtualServers();

        assertEquals(expected, actual);
    }

    @Test
    public void testListStrategies() throws Exception {
        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());
    }

    @Test
    public void testListPools() throws Exception {
        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Pool>(configure.getPools().values()), store.listPools());
    }

    @Test
    public void testFindStrategy() throws Exception {
        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        Strategy expected = configure.findStrategy("uri-hash");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, store.findStrategy("uri-hash"), true));
    }

    @Test
    public void testFindPool() throws Exception {
        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        Pool expected = configure.findPool("Web.Tuangou");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, store.findPool("Web.Tuangou"), true));
    }

    @Test
    public void testFindVirtualServer() throws Exception {
        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        VirtualServer expected = configure.findVirtualServer("www");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, store.findVirtualServer("www"), true));
    }

    @Test
    public void testAddStrategy() throws Exception {
        Strategy newStrategy = new Strategy("dper-hash");
        newStrategy.setType("hash");
        newStrategy.setDynamicAttribute("target", "dper");
        newStrategy.setDynamicAttribute("method", "crc32");
        store.updateOrCreateStrategy("dper-hash", newStrategy);

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        configure.addStrategy(newStrategy);

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());
        Assert.assertNotNull(newStrategy.getCreationDate());
        Assert.assertEquals(newStrategy.getLastModifiedDate(), newStrategy.getCreationDate());

        assertEquals(configure, "configure_base.xml");
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
    }

    @Test
    public void testAddPool() throws Exception {
        Pool pool = new Pool("TestPool");
        pool.setMinAvailableMemberPercentage(40);
        pool.setLoadbalanceStrategyName("uri-hash");
        Member member1 = new Member("test01");
        member1.setIp("10.1.1.1");
        pool.addMember(member1);
        Member member2 = new Member("test02");
        member2.setIp("10.1.1.2");
        pool.addMember(member2);
        store.updateOrCreatePool("TestPool", pool);

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        configure.addPool(pool);

        assertEquals(new ArrayList<Pool>(configure.getPools().values()), store.listPools());
        Assert.assertNotNull(pool.getCreationDate());
        Assert.assertEquals(pool.getLastModifiedDate(), pool.getCreationDate());

        assertEquals(configure, "configure_base.xml");
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
    }

    @Test
    public void testUpdateStrategy() throws Exception {
        Strategy modifiedStrategy = new Strategy("uri-hash");
        modifiedStrategy.setType("hash");
        modifiedStrategy.setDynamicAttribute("target", "$request_uri");
        modifiedStrategy.setDynamicAttribute("method", "md5");
        Date now = new Date();
        store.updateOrCreateStrategy("uri-hash", modifiedStrategy);

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        Strategy expectedStrategy = configure.findStrategy("uri-hash");
        expectedStrategy.setDynamicAttribute("method", "md5");
        expectedStrategy.setLastModifiedDate(now);

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());
        Assert.assertEquals(now, modifiedStrategy.getLastModifiedDate());
        Assert.assertEquals(expectedStrategy.getCreationDate(), modifiedStrategy.getCreationDate());

        assertEquals(configure, "configure_base.xml");
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
    }

    @Test
    public void testUpdatePool() throws Exception {
        Pool modifiedPool = new Pool("Web.Tuangou");
        modifiedPool.setMinAvailableMemberPercentage(10);
        modifiedPool.setLoadbalanceStrategyName("roundrobin");
        Member member = new Member("t1");
        member.setIp("12.12.12.12");
        modifiedPool.addMember(member);

        Date now = new Date();
        store.updateOrCreatePool("Web.Tuangou", modifiedPool);

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        Pool expectedPool = configure.findPool("Web.Tuangou");
        expectedPool.setMinAvailableMemberPercentage(10);
        expectedPool.setLoadbalanceStrategyName("roundrobin");
        Member member2 = new Member("t1");
        member2.setIp("12.12.12.12");
        expectedPool.removeMember("tuangou-web01");
        expectedPool.removeMember("tuangou-web02");
        expectedPool.addMember(member2);
        expectedPool.setLastModifiedDate(now);

        assertEquals(new ArrayList<Pool>(configure.getPools().values()), store.listPools());
        Assert.assertEquals(now, modifiedPool.getLastModifiedDate());
        Assert.assertEquals(expectedPool.getCreationDate(), modifiedPool.getCreationDate());

        assertEquals(configure, "configure_base.xml");
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
    }

    @Test
    public void testAddStrategyRollback() throws Exception {
        new File(tmpDir, "configure_base.xml").setWritable(false);

        Strategy newStrategy = new Strategy("dper-hash");
        newStrategy.setType("hash");
        newStrategy.setDynamicAttribute("target", "dper");
        newStrategy.setDynamicAttribute("method", "crc32");

        try {
            store.updateOrCreateStrategy("dper-hash", newStrategy);
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(MessageID.STRATEGY_SAVE_FAIL, e.getMessageId());
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());

        assertRawFileNotChanged("configure_base.xml");
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
    }

    @Test
    public void testUpdateStrategyRollback() throws Exception {
        new File(tmpDir, "configure_base.xml").setWritable(false);

        Strategy modifiedStrategy = new Strategy("uri-hash");
        modifiedStrategy.setType("hash");
        modifiedStrategy.setDynamicAttribute("target", "uri");
        modifiedStrategy.setDynamicAttribute("method", "md5");
        try {
            store.updateOrCreateStrategy("uri-hash", modifiedStrategy);
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(MessageID.STRATEGY_SAVE_FAIL, e.getMessageId());
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());

        assertEquals(configure, "configure_base.xml");
        assertRawFileNotChanged("configure_base.xml");
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
    }

    @Test
    public void testRemoveStrategy() throws Exception {
        store.removeStrategy("uri-hash");

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        configure.removeStrategy("uri-hash");

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());

        assertEquals(configure, "configure_base.xml");
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
    }
    
    @Test
    public void testRemovePool() throws Exception {
        store.removePool("Web.Tuangou");

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        configure.removePool("Web.Tuangou");

        assertEquals(new ArrayList<Pool>(configure.getPools().values()), store.listPools());

        assertEquals(configure, "configure_base.xml");
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
    }

    @Test
    public void testRemoveStrategyRollback() throws Exception {
        new File(tmpDir, "configure_base.xml").setWritable(false);

        try {
            store.removeStrategy("uri-hash");
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(MessageID.STRATEGY_SAVE_FAIL, e.getMessageId());
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));

        assertEquals(new ArrayList<Strategy>(configure.getStrategies().values()), store.listStrategies());

        assertEquals(configure, "configure_base.xml");
        assertRawFileNotChanged("configure_base.xml");
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
    }

    @Test
    public void testUpdateVirtualServer() throws Exception {

        VirtualServer newVirtualServer = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);

        VirtualServer originalVirtualServer = store.findVirtualServer("www");
        int originalVersion = originalVirtualServer.getVersion();
        Date originalCreationDate = originalVirtualServer.getCreationDate();

        Date now = new Date();
        store.updateVirtualServer("www", newVirtualServer);
        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));

        configure.addVirtualServer(newVirtualServer);

        Assert.assertEquals(originalVersion + 1, store.findVirtualServer("www").getVersion());
        Assert.assertEquals(originalCreationDate, store.findVirtualServer("www").getCreationDate());
        Assert.assertEquals(now, store.findVirtualServer("www").getLastModifiedDate());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(newVirtualServer, store.findVirtualServer("www"), "m_version",
                "m_creationDate", "m_lastModifiedDate"));
        // assert the whole model
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        Configure wwwConfigure = DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir,
                "configure_www.xml")));
        wwwConfigure.addVirtualServer(newVirtualServer);
        // assert www configure has updated
        assertEquals(wwwConfigure, "configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testUpdateVirtualServerConcurrentModification() throws Exception {

        VirtualServer newVirtualServer = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);

        VirtualServer originalVirtualServer = store.findVirtualServer("www");
        int originalVersion = originalVirtualServer.getVersion();
        Date originalCreationDate = originalVirtualServer.getCreationDate();

        Date now = new Date();
        store.updateVirtualServer("www", newVirtualServer);

        // modify concurrent
        VirtualServer newVirtualServer1 = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance1 = new Instance();
        newInstance1.setIp("10.1.2.5");
        newVirtualServer1.addInstance(newInstance1);
        newVirtualServer1.setAvailability(Availability.OFFLINE);
        newVirtualServer1.setState(State.DISABLED);
        newVirtualServer1.setDefaultPoolName("test-pool1");
        Location newLocation1 = new Location();
        newLocation1.setCaseSensitive(true);
        newLocation1.setMatchType("prefix");
        newLocation1.setPattern("/");
        Directive newDirective1 = new Directive();
        newDirective1.setType("static-resource1");
        newDirective1.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs1");
        newDirective1.setDynamicAttribute("expires", "300d");
        newLocation1.addDirective(newDirective1);
        newVirtualServer1.addLocation(newLocation1);
        try {
            store.updateVirtualServer("www", newVirtualServer1);
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(MessageID.VIRTUALSERVER_CONCURRENT_MOD, e.getMessageId());
        } catch (Exception e1) {
            Assert.fail();
        }
        // modify concurrent end

        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));

        configure.addVirtualServer(newVirtualServer);

        Assert.assertEquals(originalVersion + 1, store.findVirtualServer("www").getVersion());
        Assert.assertEquals(originalCreationDate, store.findVirtualServer("www").getCreationDate());
        Assert.assertEquals(now, store.findVirtualServer("www").getLastModifiedDate());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(newVirtualServer, store.findVirtualServer("www"), "m_version",
                "m_creationDate", "m_lastModifiedDate"));
        // assert the whole model
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        Configure wwwConfigure = DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir,
                "configure_www.xml")));
        wwwConfigure.addVirtualServer(newVirtualServer);
        // assert www configure has updated
        assertEquals(wwwConfigure, "configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");

    }

    @Test
    public void testUpdateVirtualServerNotExists() throws Exception {

        VirtualServer newVirtualServer = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);

        try {
            store.updateVirtualServer("test", newVirtualServer);
            Assert.fail();
        } catch (BizException e) {

        } catch (Exception e1) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));

        // assert the whole model
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");

    }

    @Test
    public void testUpdateVirtualServerRollback() throws Exception {
        new File(tmpDir, "configure_www.xml").setWritable(false);

        VirtualServer newVirtualServer = DefaultSaxParser.parse(
                FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))).findVirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);

        try {
            store.updateVirtualServer("www", newVirtualServer);
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(MessageID.VIRTUALSERVER_SAVE_FAIL, e.getMessageId());
        } catch (Exception e) {
            Assert.fail();

        }
        Configure configure = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));

        // assert the whole model
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testAddVirtualServer() throws Exception {
        VirtualServer newVirtualServer = new VirtualServer("testVs");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);

        Date now = new Date();
        store.addVirtualServer("testVs", newVirtualServer);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(newVirtualServer, store.findVirtualServer("testVs"), true));
        Assert.assertEquals(1, newVirtualServer.getVersion());
        Assert.assertEquals(now, newVirtualServer.getCreationDate());
        Assert.assertEquals(now, newVirtualServer.getLastModifiedDate());

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));
        configure.addVirtualServer(newVirtualServer);
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        // assert new file created
        Assert.assertTrue(new File(tmpDir, "configure_testVs.xml").exists());
        Assert.assertEquals(1,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, "configure_testVs.xml")))
                        .getVirtualServers().size());
        VirtualServer virtualServerFromFile = DefaultSaxParser
                .parse(FileUtils.readFileToString(new File(tmpDir, "configure_testVs.xml"))).getVirtualServers()
                .get("testVs");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assert.assertTrue(EqualsBuilder.reflectionEquals(newVirtualServer, virtualServerFromFile, "m_creationDate",
                "m_lastModifiedDate"));
        Assert.assertEquals(sdf.format(newVirtualServer.getCreationDate()),
                sdf.format(virtualServerFromFile.getCreationDate()));
        Assert.assertEquals(sdf.format(newVirtualServer.getLastModifiedDate()),
                sdf.format(virtualServerFromFile.getLastModifiedDate()));

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");

    }

    @Test
    public void testAddVirtualServerExists() throws Exception {
        VirtualServer newVirtualServer = new VirtualServer("www");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);

        try {
            store.addVirtualServer("www", newVirtualServer);
            Assert.fail();
        } catch (BizException e) {

        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        // assert new file not created
        Assert.assertFalse(new File(tmpDir, "configure_testVs.xml").exists());
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");

    }

    @Test
    public void testAddVirtualServerRollback() throws Exception {
        tmpDir.setWritable(false);

        VirtualServer newVirtualServer = new VirtualServer("test");
        Instance newInstance = new Instance();
        newInstance.setIp("10.1.2.5");
        newVirtualServer.addInstance(newInstance);
        newVirtualServer.setAvailability(Availability.OFFLINE);
        newVirtualServer.setState(State.DISABLED);
        newVirtualServer.setDefaultPoolName("test-pool");
        Location newLocation = new Location();
        newLocation.setCaseSensitive(false);
        newLocation.setMatchType("exact");
        newLocation.setPattern("/favicon.ico");
        Directive newDirective = new Directive();
        newDirective.setType("static-resource");
        newDirective.setDynamicAttribute("root-doc", "/var/www/virtual/big.server.com/htdocs");
        newDirective.setDynamicAttribute("expires", "30d");
        newLocation.addDirective(newDirective);
        newVirtualServer.addLocation(newLocation);

        try {
            store.addVirtualServer("test", newVirtualServer);
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(MessageID.VIRTUALSERVER_SAVE_FAIL, e.getMessageId());
        } catch (Exception e) {
            Assert.fail();
        }

        Configure configure = DefaultSaxParser.parse(FileUtils
                .readFileToString(new File(baseDir, "configure_base.xml")));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))));
        new DefaultMerger().merge(configure,
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_tuangou.xml"))));
        assertEquals(new ArrayList<VirtualServer>(configure.getVirtualServers().values()), store.listVirtualServers());
        // assert new file not created
        Assert.assertFalse(new File(tmpDir, "configure_testVs.xml").exists());
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");

    }

    @Test
    public void testRemoveVirtualServer() throws Exception {
        store.removeVirtualServer("www");

        Assert.assertNull(store.findVirtualServer("www"));
        // assert www configure deleted
        Assert.assertFalse(new File(tmpDir, "configure_www.xml").exists());

        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testRemoveVirtualServerRollback() throws Exception {
        tmpDir.setWritable(false);
        new File(tmpDir, "configure_www.xml").setWritable(false);
        try {
            store.removeVirtualServer("www");
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(MessageID.VIRTUALSERVER_SAVE_FAIL, e.getMessageId());
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertNotNull(store.findVirtualServer("www"));
        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testRemoveVirtualServerNotExist() throws Exception {
        try {
            store.removeVirtualServer("sss");
            Assert.fail();
        } catch (BizException e) {
        } catch (Exception e) {
            Assert.fail();
        }

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testTag() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        store.tag("www", 1);
        store.tag("www", 1);
        File tagFile = new File(tmpDir, "tag/www/" + sdf.format(new Date()) + "/configure_www.xml_1");
        File tagFile2 = new File(tmpDir, "tag/www/" + sdf.format(new Date()) + "/configure_www.xml_2");
        Assert.assertTrue(tagFile.exists());
        Assert.assertTrue(tagFile2.exists());
        Assert.assertEquals(DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))),
                DefaultSaxParser.parse(FileUtils.readFileToString(tagFile)));
        Assert.assertEquals(DefaultSaxParser.parse(FileUtils.readFileToString(new File(baseDir, "configure_www.xml"))),
                DefaultSaxParser.parse(FileUtils.readFileToString(tagFile2)));

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testTagVSNotExists() throws Exception {
        try {
            store.tag("www2", 1);
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(e.getMessageId(), MessageID.VIRTUALSERVER_NOT_EXISTS);
        } catch (Exception e) {
            Assert.fail();
        }

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testTagConcurrentMod() throws Exception {
        try {
            store.tag("www", 2);
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(e.getMessageId(), MessageID.VIRTUALSERVER_CONCURRENT_MOD);
            Assert.assertTrue(e.getCause() instanceof ConcurrentModificationException);
        } catch (Exception e) {
            Assert.fail();
        }

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testTagIOException() throws Exception {
        File tagDir = new File(tmpDir, "tag/www");
        FileUtils.forceMkdir(tagDir);
        tagDir.setWritable(false);
        try {
            store.tag("www", 1);
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(e.getMessageId(), MessageID.VIRTUALSERVER_TAG_FAIL);
            Assert.assertTrue(e.getCause() instanceof IOException);
        } catch (Exception e) {
            Assert.fail();
        }

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testListTagIds() throws Exception {
        store.tag("www", 1);
        store.tag("www", 1);
        store.tag("tuangou", 1);
        store.tag("tuangou", 1);
        store.tag("tuangou", 1);

        store = new LocalFileModelStoreImpl();
        store.setBaseDir(tmpDir.getAbsolutePath());
        store.init();

        List<String> wwwTagIds = store.listTagIds("www");
        List<String> tuangouTagIds = store.listTagIds("tuangou");

        Assert.assertArrayEquals(new String[] { "www-1", "www-2" }, wwwTagIds.toArray(new String[0]));
        Assert.assertArrayEquals(new String[] { "tuangou-1", "tuangou-2", "tuangou-3" },
                tuangouTagIds.toArray(new String[0]));

        store.tag("www", 1);
        store.tag("tuangou", 1);

        wwwTagIds = store.listTagIds("www");
        tuangouTagIds = store.listTagIds("tuangou");

        Assert.assertArrayEquals(new String[] { "www-1", "www-2", "www-3" }, wwwTagIds.toArray(new String[0]));
        Assert.assertArrayEquals(new String[] { "tuangou-1", "tuangou-2", "tuangou-3", "tuangou-4" },
                tuangouTagIds.toArray(new String[0]));

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testListTagIdsVsNotExists() throws Exception {

        try {
            store.listTagIds("test");
            Assert.fail();
        } catch (BizException e) {
            Assert.assertEquals(MessageID.VIRTUALSERVER_NOT_EXISTS, e.getMessageId());
        } catch (Exception e) {
            Assert.fail();
        }

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testListTagIdsNoTags() throws Exception {

        Assert.assertTrue(store.listTagIds("www").size() == 0);

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testGetTag() throws Exception {
        String tagId = store.tag("www", 1);

        VirtualServer tagVs = store.getTag("www", tagId);

        Assert.assertEquals(store.findVirtualServer("www").toString(), tagVs.toString());

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testFindPrevTagId() throws Exception {
        String tag1 = store.tag("www", 1);
        String tag2 = store.tag("www", 1);

        store = new LocalFileModelStoreImpl();
        store.setBaseDir(tmpDir.getAbsolutePath());
        store.init();

        Assert.assertEquals(tag1, store.findPrevTagId("www", tag2));
        Assert.assertNull(store.findPrevTagId("www", tag1));

        String tag3 = store.tag("www", 1);
        Assert.assertEquals(tag2, store.findPrevTagId("www", tag3));
        Assert.assertEquals(tag1, store.findPrevTagId("www", tag2));
        Assert.assertNull(store.findPrevTagId("www", tag1));

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testListMultiFolderTags() throws Exception {
        FileUtils.copyFile(new File(tmpDir, "configure_www.xml"), new File(tmpDir,
                "tag/www/20120101/configure_www.xml_1"));
        FileUtils.copyFile(new File(tmpDir, "configure_www.xml"), new File(tmpDir,
                "tag/www/20120102/configure_www.xml_2"));

        store = new LocalFileModelStoreImpl();
        store.setBaseDir(tmpDir.getAbsolutePath());
        store.init();

        store.tag("www", 1);
        store.tag("www", 1);
        store.tag("tuangou", 1);
        store.tag("tuangou", 1);
        store.tag("tuangou", 1);

        List<String> wwwTagIds = store.listTagIds("www");
        List<String> tuangouTagIds = store.listTagIds("tuangou");

        Assert.assertArrayEquals(new String[] { "www-1", "www-2", "www-3", "www-4" }, wwwTagIds.toArray(new String[0]));
        Assert.assertArrayEquals(new String[] { "tuangou-1", "tuangou-2", "tuangou-3" },
                tuangouTagIds.toArray(new String[0]));

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testGetMultiFolderTag() throws Exception {
        FileUtils.copyFile(new File(tmpDir, "configure_www.xml"), new File(tmpDir,
                "tag/www/20120101/configure_www.xml_1"));
        FileUtils.copyFile(new File(tmpDir, "configure_www.xml"), new File(tmpDir,
                "tag/www/20120102/configure_www.xml_2"));

        store = new LocalFileModelStoreImpl();
        store.setBaseDir(tmpDir.getAbsolutePath());
        store.init();

        Assert.assertEquals(store.findVirtualServer("www").toString(), store.getTag("www", "www-1").toString());
        Assert.assertEquals(store.findVirtualServer("www").toString(), store.getTag("www", "www-2").toString());

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    @Test
    public void testRemoveTagAndFindLatestTag() throws Exception {
        FileUtils.copyFile(new File(tmpDir, "configure_www.xml"), new File(tmpDir,
                "tag/www/20120101/configure_www.xml_1"));
        FileUtils.copyFile(new File(tmpDir, "configure_www.xml"), new File(tmpDir,
                "tag/www/20120101/configure_www.xml_2"));
        FileUtils.copyFile(new File(tmpDir, "configure_www.xml"), new File(tmpDir,
                "tag/www/20120102/configure_www.xml_3"));

        store = new LocalFileModelStoreImpl();
        store.setBaseDir(tmpDir.getAbsolutePath());
        store.init();

        store.tag("www", 1);

        store.removeTag("www", "www-2");
        store.removeTag("www", "www-4");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Assert.assertFalse(new File(tmpDir, "tag/www/20120101/configure_www.xml_2").exists());
        Assert.assertFalse(new File(tmpDir, "tag/www/" + sdf.format(new Date()) + "/configure_www.xml_4").exists());

        List<String> tagIds = store.listTagIds("www");
        Assert.assertArrayEquals(new String[] { "www-1", "www-3" }, tagIds.toArray());
        Assert.assertEquals("www-3", store.findLatestTagId("www"));

        store.removeTag("www", "www-1");
        store.removeTag("www", "www-3");
        Assert.assertFalse(new File(tmpDir, "tag/www/20120101/configure_www.xml_1").exists());
        Assert.assertFalse(new File(tmpDir, "tag/www/20120101/configure_www.xml_3").exists());

        Assert.assertEquals(0, store.listTagIds("www").size());
        Assert.assertNull(store.findLatestTagId("www"));

        assertRawFileNotChanged("configure_www.xml");
        assertRawFileNotChanged("configure_tuangou.xml");
        assertRawFileNotChanged("configure_base.xml");
    }

    private void assertRawFileNotChanged(String fileName) throws IOException {
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir, fileName)),
                FileUtils.readFileToString(new File(tmpDir, fileName)));
    }

    private void assertEquals(Configure configure, String fileName) throws SAXException, IOException {
        Assert.assertEquals(configure.toString(),
                DefaultSaxParser.parse(FileUtils.readFileToString(new File(tmpDir, fileName))).toString());
    }

    private <T> void assertEquals(List<T> expectedList, List<T> actualList) {
        Assert.assertEquals(expectedList.size(), actualList.size());
        for (T expected : expectedList) {
            Assert.assertTrue(actualList.contains(expected));
            Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, actualList.get(actualList.indexOf(expected)),
                    true));
        }
    }

}