package main;

import entities.Camera;
import entities.Entity;
import entities.Frame;
import entities.FrameUtils;
import entities.Light;
import entities.ProcessedObject;
import entities.ProcessingFrame;
import entities.Scene;
import models.RawModel;
import models.TexturedModel;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import receiver.KafkaMessageReceiver;
import renderer.DisplayManager;
import renderer.Loader;
import renderer.MasterRenderer;
import textures.ModelTexture;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Slave {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        CompressingThread compressingThread = new CompressingThread();
        try {

            //Start sending requests to slaves
            KafkaProducer<String, ProcessedObject> producer = new KafkaProducer<String, ProcessedObject>(
                    getProducerProperty());

            //Start receiving objects
            KafkaConsumer<String, ProcessingFrame> consumer = new KafkaConsumer<String, ProcessingFrame>(
                    getConsumerProperties());
            consumer.subscribe(
                    Arrays.asList(Slave.getConsumerProperties().getProperty("kafka.topic")));

            KafkaMessageReceiver receiver = new KafkaMessageReceiver(consumer);
            executorService.execute(receiver);

            SendingThread sendingThread = new SendingThread().setMessageReceiver(receiver)
                    .setExecutorService(executorService);

            Light light = new Light(new Vector3f(0, 0, -15), new Vector3f(1, 1, 1));
            Camera camera = new Camera();
            RawModel dragonModel = ObjLoader.loadObjFile("src/main/resources/dragon.obj", loader);

            RawModel stallModel = ObjLoader.loadObjFile("src/main/resources/stall.obj", loader);

            ModelTexture texture = new ModelTexture(
                    loader.loadTexture("src/main/resources/stall.png"));
            TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, texture);
            TexturedModel stallTexturedModel = new TexturedModel(stallModel, texture);

            Entity dragonEntityFrame1 = new Entity(dragonTexturedModel, new Vector3f(-10, 5, -65),
                    0, 0, 0, 1);

            Entity stallEntityFrame1 = new Entity(stallTexturedModel, new Vector3f(-10, -5, -55), 0,
                    0, 0, 1);

            Entity dragonEntityFrame2 = new Entity(dragonTexturedModel, new Vector3f(10, 5, -65), 0,
                    0, 0, 1);

            Entity stallEntityFrame2 = new Entity(stallTexturedModel, new Vector3f(10, -5, -55), 0,
                    0, 0, 1);

            Map<TexturedModel, List<Entity>> entitiesFrame1 = new HashMap<>();
            entitiesFrame1.put(dragonTexturedModel, Collections.singletonList(dragonEntityFrame1));
            entitiesFrame1.put(stallTexturedModel, Collections.singletonList(stallEntityFrame1));

            Frame frame1 = new Frame(entitiesFrame1);

            Map<TexturedModel, List<Entity>> entitiesFrame2 = new HashMap<>();
            entitiesFrame2.put(dragonTexturedModel, Collections.singletonList(dragonEntityFrame2));
            entitiesFrame2.put(stallTexturedModel, Collections.singletonList(stallEntityFrame2));

            Frame frame2 = new Frame(entitiesFrame2);

            Scene scene = new Scene();
            Map<String, Frame> frames = new HashMap<>();
            frames.put("frame1", frame1);
            frames.put("frame2", frame2);

            scene.setFrames(frames);

            MasterRenderer renderer = new MasterRenderer();

            while (!Display.isCloseRequested()) {
                ProcessingFrame processingFrame = receiver.getProcessingFrame();
                if (null != processingFrame && !processingFrame.isConsumed()) {
                    Instant start = Instant.now();

                    Instant cameraRendering = Instant.now();
                    camera.move(processingFrame.getKeyboard(), processingFrame.getMouseWheel());

                    FrameUtils.prepareFrame(scene.getFrames().get(processingFrame.getName()),
                            renderer);
                    ProcessedObject processedObject = renderer.render(light, camera);

                    System.out.println("Camera shaders time = " + Duration
                            .between(cameraRendering, Instant.now()).toMillis() + " ms.");

                                        DisplayManager.updateDisplay();

                    sendingThread.setCompressingThread(compressingThread)
                            .setMessageProducer(producer)
                            .setProcessedObject(processedObject);
                    executorService.execute(sendingThread);

                    processingFrame.setConsumed(true);
                    consumer.commitSync();

                    executorService.awaitTermination(50, TimeUnit.MILLISECONDS);
                    executorService.execute(receiver);
                    System.out.println(
                            "Total loop time = " + Duration.between(start, Instant.now()).toMillis()
                                    + " ms.");
                    System.out.println("------------------------------------------------------");
                }
            }

            renderer.cleanUp();
            loader.cleanUp();

            //            DisplayManager.closeDisplay();

        } catch (Exception exp) {
            System.out.println("Caught exception, exiting.");
            exp.printStackTrace(System.out);

            loader.cleanUp();
            //            DisplayManager.closeDisplay();
            System.exit(1);

        }

    }

    private static class MyExceptionListener implements ExceptionListener {
        public void onException(JMSException exception) {
            System.out.println("Connection ExceptionListener fired, exiting.");
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }

    public static Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("kafka.topic", "slave-commands");
        properties.put("enable.auto.commit", "false");
        properties.put("compression.type", "gzip");
        properties.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", entities.ProcessingFrameDeserializer.class);
//        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("max.partition.fetch.bytes", "2097152");
        properties.put("max.poll.records", "1");
        properties.put("auto.offset.reset", "latest");
        properties.put("group.id", "slave-group");

        return properties;
    }

    public static Properties getProducerProperty() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("acks", "all");
        properties.put("retries", "0");
        properties.put("batch.size", "20971520");
        properties.put("linger.ms", "33");
        properties.put("max.request.size", "20971520");
        properties.put("compression.type", "gzip");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", entities.ProcessedObjectSerializer.class);
        properties.put("kafka.topic", "slave-output");

        return properties;
    }
}
