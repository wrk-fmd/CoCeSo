//package at.wrk.coceso.replay.listener;
//
//import at.wrk.coceso.dto.CocesoExchangeNames;
//import at.wrk.fmd.mls.event.EventBus;
//import at.wrk.fmd.mls.replay.ReplayConstants;
//import at.wrk.fmd.mls.replay.handler.AbstractReplayListener;
//import org.springframework.amqp.rabbit.annotation.Exchange;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.QueueBinding;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//@RabbitListener(bindings = @QueueBinding(
//        value = @Queue,
//        exchange = @Exchange(ReplayConstants.REPLAY_TRIGGER_EXCHANGE),
//        key = CocesoExchangeNames.STOMP_CONCERNS
//))
//class ConcernReplayListener extends AbstractReplayListener {
//
//    @Autowired
//    public ConcernReplayListener(EventBus eventBus) {
//        super(eventBus, CocesoExchangeNames.STOMP_CONCERNS);
//    }
//}
