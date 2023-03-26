package com.gugumin.event;

import com.gugumin.CoreTest;
import com.gugumin.core.event.AddArticleEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * The type Event test.
 *
 * @author minmin
 * @date 2023 /03/26
 */
class EventTest extends CoreTest {
    @Resource
    private ApplicationEventPublisher publisher;

    /**
     * Listener test.
     */
    @Test
    public void listenerTest() {
        publisher.publishEvent(new AddArticleEvent(this, Collections.emptyList()));
    }
}