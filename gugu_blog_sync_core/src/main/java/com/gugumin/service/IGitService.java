package com.gugumin.service;

/**
 * The interface Git service.
 *
 * @author minmin
 * @date 2023 /03/09
 */
public interface IGitService {
    /**
     * Init repository.
     */
    void initRepository();

    /**
     * Push repository.
     */
    void pushRepository();

    /**
     * Update repository.
     */
    void updateRepository();
}
