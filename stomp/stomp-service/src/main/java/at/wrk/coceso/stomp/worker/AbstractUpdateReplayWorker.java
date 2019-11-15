package at.wrk.coceso.stomp.worker;

import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is the basis for a worker which sends updates and replays initial data
 */
public abstract class AbstractUpdateReplayWorker implements UpdateReplayWorker {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Runnable POISON = () -> {
    };

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private final AmqpTemplate amqpTemplate;
    private final String exchange;

    /**
     * @param amqpTemplate The injected AmqpTemplate to use
     * @param exchange The name of the target exchange
     */
    public AbstractUpdateReplayWorker(AmqpTemplate amqpTemplate, String exchange) {
        this.amqpTemplate = requireNonNull(amqpTemplate, "AmqpTemplate must not be null");
        this.exchange = requireNonNull(exchange, "Exchange name must not be null");
    }

    @Override
    public void run() {
        LOG.info("Worker started running");

        while (true) {
            try {
                Runnable task = queue.take();
                if (task == POISON) {
                    LOG.info("Received poison pill, stopping worker for {}", exchange);
                    break;
                }

                // Just run the task (synchronously)
                LOG.debug("Running task for {}, {} remaining in queue", exchange, queue.size());
                task.run();
            } catch (InterruptedException e) {
                LOG.warn("InterruptedException on waiting for task for {}", exchange, e);
            } catch (Exception e) {
                // TODO Should the worker retry?
                LOG.error("Exception on running task for {}", exchange, e);
            }
        }
    }

    @Override
    public void close() {
        queue.add(POISON);
    }

    @Override
    public void addUpdate(Object update, String routingKey) {
        LOG.debug("Adding update {} with key {} to worker for {}", update, routingKey, exchange);
        queue.add(() -> amqpTemplate.convertAndSend(exchange, routingKey, update));
    }

    @Override
    public void requestReplay(String recipient, String key) {
        LOG.debug("Requesting replay for {} with key {} in {}", recipient, key, exchange);
        queue.add(() -> sendReplay(recipient, key));
    }

    private void sendReplay(String recipient, String key) {
        LOG.debug("Replaying messages to {} with key {} in {}", recipient, key, exchange);
        getData(key).forEach(item -> amqpTemplate.convertAndSend(recipient, item));
    }

    protected abstract Collection<?> getData(String key);
}
