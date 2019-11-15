package at.wrk.coceso.radio.config;

import at.wrk.coceso.radio.api.queues.RadioQueueNames;
import at.wrk.coceso.radio.replay.RadioUpdateReplayWorker;
import at.wrk.coceso.stomp.saga.message.AddWorkerMessage;
import com.codebullets.sagalib.MessageStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class WorkerConfig {

    @Bean
    public ExecutorService workerExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Autowired
    public void configureRadioUpdateReplayWorker(RadioUpdateReplayWorker worker, MessageStream messageStream) {
        workerExecutor().submit(worker);
        messageStream.add(new AddWorkerMessage(RadioQueueNames.CALLS_RECEIVED, worker));
    }
}
