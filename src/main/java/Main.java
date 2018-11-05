import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.ProcessedObject;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
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
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Main {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        ProcessedObject processedObject = new ProcessedObject();
        Renderer renderer = new Renderer(shader, processedObject);

        RawModel model = ObjLoader
                .loadObjFile("D:\\Facultate\\VSD-Slave\\src\\main\\resources\\dragon.obj", loader);
        ModelTexture texture = new ModelTexture(
                loader.loadTexture("D:\\Facultate\\VSD-Slave\\src\\main\\resources\\stall.png"));
        TexturedModel texturedModel = new TexturedModel(model, texture);

        Entity entity = new Entity(texturedModel, new Vector3f(0, 0, -55), 0, 0, 0, 1);

        Light light = new Light(new Vector3f(0, 0, -15), new Vector3f(1, 1, 1));
        Camera camera = new Camera();

        SendingThread sendingThread = new SendingThread();
        CompressingThread compressingThread = new CompressingThread();
        try {

            Context context = new InitialContext();

            ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
            Destination queue = (Destination) context.lookup("myQueueLookup");

            Connection connection = factory.createConnection("admin", "admin");
            connection.setExceptionListener(new MyExceptionListener());
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageProducer messageProducer = session.createProducer(queue);

            sendingThread.setCompressingThread(compressingThread);
            sendingThread.setMessageProducer(messageProducer);
            sendingThread.setSession(session);

            while (!Display.isCloseRequested()) {
                // game logic
                entity.increaseRotation(0f, 1f, 0.0f);
                //            entity.increasePosition(0.0f, 0.0f, 0.1f);
                camera.move();
                renderer.prepare();
                shader.start();
                shader.loadLight(light);
                shader.loadViewMatrix(camera);
                processedObject = renderer.render(entity, shader);
                DisplayManager.updateDisplay();

                sendingThread.setProcessedObject(processedObject);
                sendingThread.run();
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
