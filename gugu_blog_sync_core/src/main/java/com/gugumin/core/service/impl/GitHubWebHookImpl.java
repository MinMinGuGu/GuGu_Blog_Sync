package com.gugumin.core.service.impl;

import com.gugumin.core.config.CoreConfig;
import com.gugumin.core.service.IGitService;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * The type Git hub web hook.
 *
 * @author minmin
 * @date 2023 /03/31
 */
@Service
public class GitHubWebHookImpl extends BaseWebHookImpl {
    /**
     * Instantiates a new Github webhook.
     *
     * @param coreConfig the core config
     * @param gitService the git service
     * @param publisher  the publisher
     */
    protected GitHubWebHookImpl(CoreConfig coreConfig, IGitService gitService, ApplicationEventPublisher publisher) {
        super(coreConfig, gitService, publisher);
    }

    @Override
    public List<String> getDeleteFileNames(String payload) {
        return getFileNames(JsonPath.read(payload, "$.commits..removed"));
    }

    @Override
    public List<String> getAddFileNames(String payload) {
        return getFileNames(JsonPath.read(payload, "$.commits..added"));
    }

    @Override
    public List<String> getUpdateFileNames(String payload) {
        return getFileNames(JsonPath.read(payload, "$.commits..modified"));
    }

    private List<String> getFileNames(JSONArray jsonArray) {
        List<String> fileNameList = new LinkedList<>();
        for (Object arrayObj : jsonArray) {
            if (arrayObj instanceof JSONArray) {
                JSONArray array = (JSONArray) arrayObj;
                for (Object obj : array) {
                    fileNameList.add(obj.toString());
                }
            }
        }
        return fileNameList;
    }
}
