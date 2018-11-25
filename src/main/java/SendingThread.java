import entities.ProcessedObject;
import receiver.MessageReceiver;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SendingThread implements Runnable {
    private static final int DELIVERY_MODE = DeliveryMode.NON_PERSISTENT;

    private ProcessedObject processedObject;
    private CompressingThread compressingThread;
    private MessageProducer messageProducer;
    private MessageReceiver messageReceiver;
    private Session session;
    private ExecutorService executorService;

    public SendingThread(ProcessedObject processedObject, CompressingThread compressingThread) {
        this.processedObject = processedObject;
        this.compressingThread = compressingThread;
    }

    public SendingThread() {
    }

    @Override
    public void run() {
        Instant start = Instant.now();
        compressingThread.setObject(processedObject);
        executorService.execute(compressingThread);

        try {
            executorService.awaitTermination(20, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ObjectMessage objectMessage = null;
        try {
            objectMessage = session
                    .createObjectMessage(compressingThread.getCompressedOutput());
        } catch (JMSException e) {
            e.printStackTrace();
        }

        try {
            messageProducer.send(objectMessage, DELIVERY_MODE, Message.DEFAULT_PRIORITY,
                    Message.DEFAULT_TIME_TO_LIVE);
            messageReceiver.setNeedToConsume(true);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        System.out.println("Sending time: " + Duration.between(start, Instant.now()).toMillis() + " ms.");
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public SendingThread setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public MessageProducer getMessageProducer() {
        return messageProducer;
    }

    public SendingThread setMessageProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
        return this;
    }

    public MessageReceiver getMessageReceiver() {
        return messageReceiver;
    }

    public SendingThread setMessageReceiver(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
        return this;
    }

    public Session getSession() {
        return session;
    }

    public SendingThread setSession(Session session) {
        this.session = session;
        return this;
    }

    public CompressingThread getCompressingThread() {
        return compressingThread;
    }

    public SendingThread setCompressingThread(CompressingThread compressingThread) {
        this.compressingThread = compressingThread;
        return this;
    }

    public ProcessedObject getProcessedObject() {
        return processedObject;
    }

    public SendingThread setProcessedObject(ProcessedObject processedObject) {
        this.processedObject = processedObject;
        return this;
    }
}
