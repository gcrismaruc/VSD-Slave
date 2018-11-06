import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.ProcessedObject;
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

public class Main {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        //        ProcessedObject processedObject = new ProcessedObject();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Renderer renderer = new Renderer(shader);

        //        SendingThread sendingThread = new SendingThread();
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
            MessageReceiver receiver = new MessageReceiver(consumerSession, messageConsumer);

            Thread recvThread = new Thread(receiver);
            recvThread.start();

            Light light = new Light(new Vector3f(0, 0, -15), new Vector3f(1, 1, 1));
            Camera camera = new Camera();
            ModelTexture texture = new ModelTexture(
                    loader.loadTexture(
                            "D:\\Facultate\\VSD-Slave\\src\\main\\resources\\stall.png"));

            while (!Display.isCloseRequested()) {
                if (null != receiver.getProcessingObject()) {
                    RawModel model = ObjLoader
                            .loadObjFile(
                                    "D:\\Facultate\\VSD-Slave\\src\\main\\resources\\" + receiver
                                            .getProcessingObject().getObjectName(), loader);

                    TexturedModel texturedModel = new TexturedModel(model, texture);

                    Entity entity = new Entity(texturedModel,
                            new Vector3f(receiver.getProcessingObject().getX(),
                                    receiver.getProcessingObject().getY(),
                                    receiver.getProcessingObject().getZ()), 0, 0, 0, 1);

                    entity.increaseRotation(0f, receiver.getProcessingObject().getRy(), 0.0f);
                    //            entity.increasePosition(0.0f, 0.0f, 0.1f);
                    camera.move();
                    renderer.prepare();
                    shader.start();
                    shader.loadLight(light);
                    shader.loadViewMatrix(camera);
                    ProcessedObject processedObject = renderer.render(entity, shader);
                    DisplayManager.updateDisplay();

                    SendingThread sendingThread = new SendingThread()
                            .setCompressingThread(compressingThread)
                            .setMessageProducer(messageProducer)
                            .setSession(producerSession)
                            .setProcessedObject(processedObject);
                    executorService.execute(sendingThread);
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
