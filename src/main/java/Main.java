import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.ProcessedObject;
import entities.ProcessingObject;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import receiver.MessageReceiver;
import renderer.DisplayManager;
import renderer.Loader;
import renderer.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Renderer renderer = new Renderer(shader);

        CompressingThread compressingThread = new CompressingThread();
        try {

            Context context = new InitialContext();

            ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
            Destination queue = (Destination) context.lookup("myQueueLookup");
            Destination slaveQueue = (Destination) context.lookup("slaveQueue");

            Connection connection = factory.createConnection("admin", "admin");
            connection.setExceptionListener(new MyExceptionListener());
            connection.start();

            Session producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Session consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageProducer messageProducer = producerSession.createProducer(queue);

            //Start receiving objects
            MessageConsumer messageConsumer = consumerSession.createConsumer(slaveQueue);
            MessageReceiver receiver = new MessageReceiver(messageConsumer);
            executorService.execute(receiver);

            SendingThread sendingThread = new SendingThread().setMessageReceiver(receiver);

            Light light = new Light(new Vector3f(0, 0, -15), new Vector3f(1, 1, 1));
            Camera camera = new Camera();
            ModelTexture texture = new ModelTexture(
                    loader.loadTexture(
                            "D:\\Facultate\\VSD-Slave\\src\\main\\resources\\stall.png"));

            while (!Display.isCloseRequested()) {
                ProcessingObject processingObject = receiver.getProcessingObject();

                if (null != processingObject && !processingObject.isConsumed()) {
                    RawModel model = ObjLoader
                            .loadObjFile(
                                    "D:\\Facultate\\VSD-Slave\\src\\main\\resources\\" + receiver
                                            .getProcessingObject().getObjectName(), loader);

                    TexturedModel texturedModel = new TexturedModel(model, texture);

                    Entity entity = new Entity(texturedModel,
                            new Vector3f(processingObject.getX(),
                                    processingObject.getY(),
                                    processingObject.getZ()), 0, 0, 0, 1);

                    System.out.println(
                            "Processing object: " + processingObject.getObjectName()
                                    + " Y = "
                                    + processingObject.getRy());

                    entity.increaseRotation(0f, processingObject.getRy(), 0.0f);
                    camera.move();
                    renderer.prepare();
                    shader.start();
                    shader.loadLight(light);
                    shader.loadViewMatrix(camera);

                    ProcessedObject processedObject = renderer.render(entity, shader);
                    DisplayManager.updateDisplay();

                    sendingThread
                            .setCompressingThread(compressingThread)
                            .setMessageProducer(messageProducer)
                            .setSession(producerSession)
                            .setProcessedObject(processedObject);
                    executorService.execute(sendingThread);

                    processingObject.setConsumed(true);

                    executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
                    executorService.execute(receiver);
                }
            }
            connection.close();

        } catch (Exception exp) {
            System.out.println("Caught exception, exiting.");
            exp.printStackTrace(System.out);
            System.exit(1);
        }

        shader.cleanUp();
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
