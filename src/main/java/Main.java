import entities.Camera;
import entities.Entity;
import entities.Frame;
import entities.FrameUtils;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderer.DisplayManager;
import renderer.Loader;
import renderer.MasterRenderer;
import textures.ModelTexture;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        CompressingThread compressingThread = new CompressingThread();
        //        try {
        //
        //            Context context = new InitialContext();
        //
        //            ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
        //            Destination queue = (Destination) context.lookup("myQueueLookup");
        //            Destination slaveQueue = (Destination) context.lookup("slaveQueue");
        //
        //            Connection connection = factory.createConnection("admin", "admin");
        //            connection.setExceptionListener(new MyExceptionListener());
        //            connection.start();
        //
        //            Session producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //            Session consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //
        //            MessageProducer messageProducer = producerSession.createProducer(queue);
        //
        //            //Start receiving objects
        //            MessageConsumer messageConsumer = consumerSession.createConsumer(slaveQueue);
        //            MessageReceiver receiver = new MessageReceiver(messageConsumer);
        ////            executorService.execute(receiver);
        //
        //            SendingThread sendingThread = new SendingThread().setMessageReceiver(receiver);

        Light light = new Light(new Vector3f(0, 0, -15), new Vector3f(1, 1, 1));
        Camera camera = new Camera();
        RawModel dragonModel = ObjLoader
                .loadObjFile(
                        "D:\\Facultate\\VSD-Slave\\src\\main\\resources\\dragon.obj", loader);

        RawModel stallModel = ObjLoader
                .loadObjFile(
                        "D:\\Facultate\\VSD-Slave\\src\\main\\resources\\stall.obj", loader);

        ModelTexture texture = new ModelTexture(
                loader.loadTexture(
                        "D:\\Facultate\\VSD-Slave\\src\\main\\resources\\stall.png"));
        TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, texture);
        TexturedModel stallTexturedModel = new TexturedModel(stallModel, texture);

        Entity dragonEntity = new Entity(dragonTexturedModel,
                new Vector3f(-10, 5, -65), 0, 0, 0, 1);

        Entity stallEntity = new Entity(stallTexturedModel,
                new Vector3f(-10, -5, -55), 0, 0, 0, 1);

        Map<TexturedModel, List<Entity>> entities = new HashMap<>();
        entities.put(dragonTexturedModel, Collections.singletonList(dragonEntity));
        entities.put(stallTexturedModel, Collections.singletonList(stallEntity));

        Frame frame = new Frame(entities);
        MasterRenderer renderer = new MasterRenderer();
//        renderer.setFrame(frame);

        while (!Display.isCloseRequested()) {
            //                ProcessingObject processingObject = receiver.getProcessingObject();

            //                if (null != processingObject && !processingObject.isConsumed()) {
            //                    Instant start = Instant.now();

            Instant cameraRendering = Instant.now();

            //                    System.out.println(
            //                            "Processing object: " + processingObject.getObjectName()
            //                                    + " Y = "
            //                                    + processingObject.getRy());

            //                    entity.increaseRotation(0f, processingObject.getRy(), 0.0f);
//            entity.increaseRotation(0f, 1, 0.0f);



            camera.move();

            FrameUtils.prepareFrame(frame, renderer);
            renderer.render(light, camera);
                                System.out.println("Camera shaders time = " + Duration
                                        .between(cameraRendering, Instant.now()).toMillis() + " ms.");

            DisplayManager.updateDisplay();

            //                    sendingThread
            //                            .setCompressingThread(compressingThread)
            //                            .setMessageProducer(messageProducer)
            //                            .setSession(producerSession)
            //                            .setProcessedObject(processedObject);
            //                    executorService.execute(sendingThread);
            //
            //                    processingObject.setConsumed(true);

            //                    executorService.awaitTermination(50, TimeUnit.MILLISECONDS);
            //                    executorService.execute(receiver);
            //                    System.out.println("Total loop time = " + Duration.between(start, Instant.now()).toMillis() +  " ms.");
            //                    System.out.println("------------------------------------------------------");
        }
        //            }
        //            connection.close();

        //        } catch (Exception exp) {
        //            System.out.println("Caught exception, exiting.");
        //            exp.printStackTrace(System.out);
        //            System.exit(1);
        //        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

    private static class MyExceptionListener implements ExceptionListener {
        public void onException(JMSException exception) {
            System.out.println("Connection ExceptionListener fired, exiting.");
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
