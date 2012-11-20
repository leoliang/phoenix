package com.dianping.phoenix.service;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;

public class DefaultGitService implements GitService {
	@Inject
	private ConfigManager m_configManager;

	@Inject
	private StatusReporter m_reporter;

	@Inject
	private ProgressMonitor m_monitor;

	private File m_workingDir = new File("target/gitrepo");

	private Git m_git;

	@Override
	public void clearWorkingDir() throws Exception {
		if (m_git == null) {
			throw new IllegalStateException("Please call setup() to initiailize git first!");
		}

		m_reporter.log("Clearing git ... ");

		String[] names = m_workingDir.list();

		if (names != null) {
			for (String name : names) {
				if (".git".equals(name)) {
					continue;
				}

				Files.forDir().delete(new File(m_workingDir, name), true);
			}
		}
		m_reporter.log("Cleared git for ... ");
	}

	@Override
	public void commit(String tag, String description) throws Exception {
		if (m_git == null) {
			throw new IllegalStateException("Please call setup() to initiailize git first!");
		}

		// Add
		m_reporter.log(String.format("Adding to git for tag(%s) ... ", tag));
		m_git.add().addFilepattern(".").call();
		m_reporter.log(String.format("Adding to git for tag(%s) ... DONE", tag));

		// Commit
		m_reporter.log(String.format("Commiting to git for tag(%s) ... ", tag));
		m_git.commit().setAll(true).setMessage(description).call();
		m_reporter.log(String.format("Commited to git for tag(%s) ... ", tag));

		// Tag
		m_reporter.log(String.format("Taging to git for tag(%s) ... ", tag));
		m_git.tag().setName(tag).setMessage(description).call();
		m_reporter.log(String.format("Taged to git for tag(%s) ... ", tag));
	}

	@Override
	public File getWorkingDir() {
		return m_workingDir;
	}

	@Override
	public void pull() throws Exception {
		if (m_git == null) {
			throw new IllegalStateException("Please call setup() to initiailize git first!");
		}

		m_reporter.log("Pulling from git ... ");
		m_git.pull().setProgressMonitor(m_monitor).call();
		m_reporter.log("Pulled from git ... ");
	}

	@Override
	public void push() throws Exception {
		if (m_git == null) {
			throw new IllegalStateException("Please call setup() to initiailize git first!");
		}

		// Push heads
		m_reporter.log("Pushing to git heads ... ");
		m_git.push().setProgressMonitor(m_monitor).setPushAll().call();
		m_reporter.log("Pushed to git heads ... ");

		// Push heads
		m_reporter.log("Pushing to git tags ... ");
		m_git.push().setPushTags().setProgressMonitor(m_monitor).call();
		m_reporter.log("Pushed to git tags ... ");
	}

	@Override
	public synchronized void setup() throws Exception {
		if (m_git == null) {
			m_workingDir = new File(m_configManager.getGitWorkingDir());
			m_workingDir.mkdirs();

			File gitRepo = new File(m_workingDir, ".git");

			if (!gitRepo.exists()) {
				String gitURL = m_configManager.getGitOriginUrl();
				m_reporter.log(String.format("Cloning repo from %s ... ", gitURL));
				FileRepositoryBuilder builder = new FileRepositoryBuilder();
				Repository repository = builder.setGitDir(gitRepo).readEnvironment().findGitDir().build();

				m_git = new Git(repository);
				CloneCommand clone = Git.cloneRepository();
				clone.setProgressMonitor(m_monitor);
				clone.setBare(false);
				clone.setDirectory(m_workingDir);
				clone.setCloneAllBranches(true);
				clone.setURI(gitURL);
				clone.call();

				m_reporter.log(String.format("Cloned repo from %s ... ", gitURL));
			} else {
				m_git = Git.open(m_workingDir);
			}
		}
	}
}