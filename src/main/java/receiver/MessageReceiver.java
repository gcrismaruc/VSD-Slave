package receiver;

import entities.ProcessingFrame;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import java.time.Duration;
import java.time.Instant;

public class MessageReceiver implements Runnable {

    private MessageConsumer messageConsumer;
    private static int TIMEOUT = 10000;
    ProcessingFrame processingFrame;
    private boolean needToConsume = true;

    public MessageReceiver(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void run() {
        while (needToConsume) {
            try {
                Instant msg = Instant.now();

                Message message = messageConsumer.receive(TIMEOUT);
                System.out.println("Receiving one msg = " + Duration
                        .between(msg, Instant.now()).toMillis() + " ms");

                ObjectMessage objectMessage = (ObjectMessage) message;

                processingFrame = (ProcessingFrame) objectMessage.getObject();
                needToConsume = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            //            try {
            //                Thread.sleep(10);
            //            } catch (InterruptedException e) {
            //                e.printStackTrace();
            //            }
        }
    }

    public ProcessingFrame getProcessingFrame() {
        return processingFrame;
    }

    public MessageReceiver setProcessingFrame(ProcessingFrame processingFrame) {
        this.processingFrame = processingFrame;
        return this;
    }

    public MessageReceiver setNeedToConsume(boolean needToConsume) {
        this.needToConsume = needToConsume;
        return this;
    }



    public boolean isNeedToConsume() {
        return needToConsume;
    }
}
