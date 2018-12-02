package receiver;

import entities.ProcessingFrame;
import main.Slave;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.time.Instant;

public class KafkaMessageReceiver implements Runnable {

    ProcessingFrame processingFrame;
    private boolean needToConsume = true;
    private KafkaConsumer<String, ProcessingFrame> consumer;

    public KafkaMessageReceiver(KafkaConsumer<String, ProcessingFrame> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        while (needToConsume) {
            try {
                Instant msg = Instant.now();

                System.out.println("Subscribed to topic " + Slave.getConsumerProperties()
                        .getProperty("kafka.topic"));

                System.out.println("Trying to receive message from kafka...");

                ConsumerRecords<String, ProcessingFrame> records = consumer.poll(Duration.ofMillis(20));

                while (records.isEmpty()) {
                    records = consumer.poll(Duration.ofMillis(20));
                }

                for (ConsumerRecord<String, ProcessingFrame> record : records) {
                    System.out.printf("partition = %s, offset = %d, key = %s, value = %s\n",
                            record.partition(), record.offset(), record.key(), record.value());
                    processingFrame = record.value();
                }
//                consumer.commitSync();


                needToConsume = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ProcessingFrame getProcessingFrame() {
        return processingFrame;
    }

    public KafkaMessageReceiver setProcessingFrame(ProcessingFrame processingFrame) {
        this.processingFrame = processingFrame;
        return this;
    }

    public KafkaMessageReceiver setNeedToConsume(boolean needToConsume) {
        this.needToConsume = needToConsume;
        return this;
    }

    public boolean isNeedToConsume() {
        return needToConsume;
    }
}
