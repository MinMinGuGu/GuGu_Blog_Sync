package com.gugumin.core.service.impl;

import com.gugumin.core.components.I18nHelper;
import com.gugumin.core.config.CoreConfig;
import com.gugumin.core.service.IGitService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The type Git service.
 *
 * @author minmin
 * @date 2023 /03/09
 */
@Slf4j
@Service
public class GitServiceImpl implements IGitService {
    private static final String LOG_GIT_CLONE_SUCCESS = "log_git_clone_success";
    private static final String LOG_GIT_RELATION_SUCCESS = "log_git_relation_success";
    private static final String LOG_GIT_INIT_FAILED = "log_git_init_failed";
    private static final String LOG_GIT_COMMIT_MSG = "log_git_commit_msg";
    private static final String LOG_GIT_PUSH_POST = "log_git_push_post";
    private static final String LOG_GIT_PUSH_SUCCESS = "log_git_push_success";
    private static final String LOG_GIT_PUSH_FAILED = "log_git_push_failed";
    private static final String LOG_GIT_PULL_SUCCESS = "log_git_pull_success";
    private static final String LOG_GIT_PULL_FAILED = "log_git_pull_failed";

    private final CoreConfig coreConfig;

    private final I18nHelper i18nHelper;

    /**
     * Instantiates a new Git service.
     *
     * @param coreConfig the config
     * @param i18nHelper the 18 n helper
     */
    public GitServiceImpl(CoreConfig coreConfig, I18nHelper i18nHelper) {
        this.coreConfig = coreConfig;
        this.i18nHelper = i18nHelper;
    }

    @Override
    public void initRepository() {
        Path repositoryPath = coreConfig.getRepositoryPath();
        try {
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(coreConfig.getGit().getRepository())
                    .setDirectory(repositoryPath.toFile());
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(coreConfig.getGit().getUsername(), coreConfig.getGit().getToken()));
            cloneCommand.call().close();
            log.info(i18nHelper.getI18nMessage(LOG_GIT_CLONE_SUCCESS));
            Git open = Git.open(repositoryPath.toFile());
            open.checkout()
                    .setName("main")
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .setStartPoint("origin/main")
                    .call();
            open.close();
            log.info(i18nHelper.getI18nMessage(LOG_GIT_RELATION_SUCCESS));
        } catch (GitAPIException | IOException e) {
            log.error(i18nHelper.getI18nMessage(LOG_GIT_INIT_FAILED));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pushRepository() {
        log.info(i18nHelper.getI18nMessage(LOG_GIT_PUSH_POST));
        Path repositoryPath = coreConfig.getRepositoryPath();
        try {
            Git open = Git.open(repositoryPath.toFile());
            open.add().addFilepattern(".").call();
            open.commit().setMessage(i18nHelper.getI18nMessage(LOG_GIT_COMMIT_MSG)).call();
            open.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(coreConfig.getGit().getUsername(), coreConfig.getGit().getToken())).setPushAll().call();
            open.close();
            log.info(i18nHelper.getI18nMessage(LOG_GIT_PUSH_SUCCESS));
        } catch (IOException | GitAPIException e) {
            log.error(i18nHelper.getI18nMessage(LOG_GIT_PUSH_FAILED));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateRepository() {
        Path repositoryPath = coreConfig.getRepositoryPath();
        try (Git git = Git.open(repositoryPath.toFile())) {
            PullCommand pullCommand = git.pull()
                    .setRemote("origin");
            pullCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(coreConfig.getGit().getUsername(), coreConfig.getGit().getToken()));
            pullCommand.call();
            log.info(i18nHelper.getI18nMessage(LOG_GIT_PULL_SUCCESS));
        } catch (Exception e) {
            log.error(i18nHelper.getI18nMessage(LOG_GIT_PULL_FAILED));
            throw new RuntimeException(e);
        }
    }
}
