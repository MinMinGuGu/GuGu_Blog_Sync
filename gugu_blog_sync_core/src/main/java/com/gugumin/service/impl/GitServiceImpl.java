package com.gugumin.service.impl;

import com.gugumin.config.Config;
import com.gugumin.service.IGitService;
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
    private final Config config;

    /**
     * Instantiates a new Git service.
     *
     * @param config the config
     */
    public GitServiceImpl(Config config) {
        this.config = config;
    }

    @Override
    public void initRepository() {
        Path repositoryPath = config.getRepositoryPath();
        try {
            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(config.getGit().getRepository())
                    .setDirectory(repositoryPath.toFile());
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getGit().getUsername(), config.getGit().getToken()));
            cloneCommand.call().close();
            log.info("克隆git仓库成功");
            Git open = Git.open(repositoryPath.toFile());
            open.checkout()
                    .setName("main")
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .setStartPoint("origin/main")
                    .call();
            open.close();
            log.info("本地分支已经关联远端分支");
        } catch (GitAPIException | IOException e) {
            log.error("初始化git仓库时失败");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pushRepository() {
        log.info("准备开始将本地仓库同步到git远程仓库");
        Path repositoryPath = config.getRepositoryPath();
        try {
            Git open = Git.open(repositoryPath.toFile());
            open.add().addFilepattern(".").call();
            open.commit().setMessage("提交站点文章").call();
            open.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getGit().getUsername(), config.getGit().getToken()))
                    .setPushAll()
                    .call();
            open.close();
            log.info("成功将本地仓库同步到git远程仓库");
        } catch (IOException | GitAPIException e) {
            log.error("将站点文章同步到git远程仓库出错");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateRepository() {
        Path repositoryPath = config.getRepositoryPath();
        try (Git git = Git.open(repositoryPath.toFile())) {
            PullCommand pullCommand = git.pull()
                    .setRemote("origin");
            pullCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(config.getGit().getUsername(), config.getGit().getToken()));
            pullCommand.call();
            log.info("更新本地分支成功");
        } catch (Exception e) {
            log.error("更新git仓库失败");
            throw new RuntimeException(e);
        }
    }
}
