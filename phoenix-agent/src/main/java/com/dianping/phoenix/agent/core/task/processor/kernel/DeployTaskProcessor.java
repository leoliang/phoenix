package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.IOUtil;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.event.MessageEvent;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.agent.core.tx.TransactionManager;

public class DeployTaskProcessor extends AbstractSerialTaskProcessor<DeployTask> {

	private final static Logger logger = Logger.getLogger(DeployTaskProcessor.class);

	@Inject
	private TransactionManager txMgr;
	@Inject
	DeployStepContext ctx;
	@Inject
	Config config;

	public DeployTaskProcessor() {
	}

	@Override
	protected void doProcess(final Transaction tx) throws IOException {
		logger.info("start processing " + tx);
		try {
			tx.setStatus(Status.PROCESSING);
			txMgr.saveTransaction(tx);
			tx.setStatus(innerProcess(tx));
		} catch (Exception e) {
			tx.setStatus(Status.FAILED);
			eventTrackerChain.onEvent(new LifecycleEvent(tx.getTxId(), e.getMessage(), Status.FAILED));
		} finally {
			txMgr.saveTransaction(tx);
			logger.info("end processing " + tx);
		}
	}

	private Status innerProcess(final Transaction tx) throws IOException {

		DeployTask task = (DeployTask) tx.getTask();
		String domain = task.getDomain();

		eventTrackerChain.onEvent(new MessageEvent(tx.getTxId(), String.format("updating %s to version %s", domain,
				task.getKernelVersion())));
		OutputStream stdOut = txMgr.getLogOutputStream(tx.getTxId());
		Status exitStatus = Status.SUCCESS;
		try {
			exitStatus = updateKernel(domain, task.getKernelVersion(), stdOut);
		} catch (Exception e) {
			logger.error("error update kernel", e);
			exitStatus = Status.FAILED;
		} finally {
			IOUtil.close(stdOut);
			eventTrackerChain.onEvent(new LifecycleEvent(tx.getTxId(), "", exitStatus));
		}
		return exitStatus;
	}

	private Status updateKernel(String domain, String kernelVersion, OutputStream stdOut) throws Exception {
		ctx.setContainer(config.getContainerType().toString().toLowerCase());
		ctx.setDomain(domain);
		ctx.setDomainDocBaseFeaturePattern(config.getDomainDocBaseFeaturePattern());
		ctx.setKernelDocBasePattern(config.getKernelDocBasePattern());
		ctx.setLoaderClass(config.getLoaderClass());
		ctx.setKernelVersion(kernelVersion);
		ctx.setOut(stdOut);
		ctx.setServerXml(config.getServerXml());
		DeployStep.execute(ctx);
		return ctx.getStatus();
	}

	@Override
	public boolean cancel(TransactionId txId) {
		ctx.kill(txId);
		return true;
	}

	@Override
	public Class<DeployTask> handle() {
		return DeployTask.class;
	}

}