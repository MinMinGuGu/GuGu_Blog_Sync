package com.gugumin.core.service;

import java.util.List;

/**
 * The interface Change paths.
 *
 * @author minmin
 * @date 2023 /03/31
 */
public interface IChangeFileNames {
    /**
     * Gets delete paths.
     *
     * @param payload the payload
     * @return the delete paths
     */
    List<String> getDeleteFileNames(String payload);

    /**
     * Gets add paths.
     *
     * @param payload the payload
     * @return the add paths
     */
    List<String> getAddFileNames(String payload);

    /**
     * Gets update paths.
     *
     * @param payload the payload
     * @return the update paths
     */
    List<String> getUpdateFileNames(String payload);
}
