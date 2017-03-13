package io.opentracing.contrib.web.servlet.filter;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletContext;

import io.opentracing.Span;
import io.opentracing.contrib.spanmanager.DefaultSpanManager;
import io.opentracing.contrib.spanmanager.SpanManager;

/**
 * This is passed to {@link ServletContext#setAttribute(String, Object)} holding server span.
 *
 * <p>This wrapper is necessary for higher levels (e.g. spring interceptor, jax-rs) to find out
 * if the span was finished or not.
 *
 * @author Pavol Loffay
 */
public class SpanWrapper {

    private SpanManager.ManagedSpan managedSpan;
    private AtomicBoolean finished = new AtomicBoolean();

    protected SpanWrapper(Span span) {
        managedSpan = DefaultSpanManager.getInstance().activate(span);
    }

    /**
     * @return server span
     */
    public Span get() {
        return managedSpan.getSpan();
    }

    /**
     * @return true if span has been finished
     */
    public boolean isFinished() {
        return finished.get();
    }

    /**
     * Idempotent finish
     */
    protected void finish() {
        if (finished.compareAndSet(false,true)) {
            managedSpan.getSpan().finish();
            managedSpan.deactivate();
        }
    }
}
