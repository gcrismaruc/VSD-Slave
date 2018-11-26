package main;

import entities.ProcessedObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import receiver.KafkaMessageReceiver;

import javax.jms.DeliveryMode;
import javax.jms.Session;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;

public class SendingThread implements Runnable {
    private static final int DELIVERY_MODE = DeliveryMode.NON_PERSISTENT;

    private ProcessedObject processedObject;
    private CompressingThread compressingThread;
    private KafkaProducer<String, ProcessedObject> messageProducer;
    private KafkaMessageReceiver messageReceiver;
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

        messageProducer
                .send(new ProducerRecord<>(Slave.getProducerProperty().getProperty("kafka.topic"),
                        processedObject));

        messageReceiver.setNeedToConsume(true);

        System.out.println(
                "Sending time: " + Duration.between(start, Instant.now()).toMillis() + " ms.");
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public SendingThread setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public KafkaProducer<String, ProcessedObject> getMessageProducer() {
        return messageProducer;
    }

    public SendingThread setMessageProducer(
            KafkaProducer<String, ProcessedObject> messageProducer) {
        this.messageProducer = messageProducer;
        return this;
    }

    public KafkaMessageReceiver getMessageReceiver() {
        return messageReceiver;
    }

    public SendingThread setMessageReceiver(KafkaMessageReceiver messageReceiver) {
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
