import entities.Frame;
import entities.FrameUtils;
import entities.Light;
import entities.ProcessedObject;
import entities.ProcessingFrame;
import entities.Scene;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderer.DisplayManager;
import renderer.Loader;
import renderer.MasterRenderer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jogamp.common.util.locks.Lock.TIMEOUT;

public class Slave {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        ExecutorService executorService = Executors.newFixedThreadPool(1);

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
//            MessageReceiver receiver = new MessageReceiver(messageConsumer);
//            executorService.execute(receiver);

            SendingThread sendingThread = new SendingThread()
                    .setExecutorService(executorService);

            Light light = new Light(new Vector3f(0, 0, -15), new Vector3f(1, 1, 1));

            Scene scene = new Scene();
            scene.setFrames(FramesUtils.createFrames(loader));

            MasterRenderer renderer = new MasterRenderer();

            boolean canConsume = true;
            while (!Display.isCloseRequested()) {
                if(canConsume) {
//                    ProcessingFrame processingFrame = receiver.getProcessingFrame();

                    try {
                        Instant msg = Instant.now();

                        Message message = messageConsumer.receive(TIMEOUT);
                        System.out.println("Receiving one msg = " + Duration.between(msg, Instant.now())
                                .toMillis() + " ms");

                        ObjectMessage objectMessage = (ObjectMessage) message;

                        ProcessingFrame processingFrame = (ProcessingFrame) objectMessage.getObject();

                        if (null != processingFrame && !processingFrame.isConsumed()) {
                            canConsume = false;
                            Instant start = Instant.now();

                            System.out.println("Frame name: " + processingFrame.getName() + " "
                                    + "with key: "
                                    + processingFrame.getKeyboard());

                            Instant cameraRendering = Instant.now();
                            Frame frame = scene.getFrames()
                                    .get(processingFrame.getName());
                            frame.getCamera()
                                    .move(processingFrame.getKeyboard(), processingFrame.getMouseWheel());

                            FrameUtils.prepareFrame(frame, renderer);
                            ProcessedObject processedObject = renderer.render(light, frame
                                    .getCamera());

                            System.out.println("Camera shaders time = " + Duration.between
                                    (cameraRendering,
                                    Instant.now())
                                    .toMillis() + " ms.");

//                            DisplayManager.updateDisplay();

                            sendingThread.setCompressingThread(compressingThread)
                                    .setMessageProducer(messageProducer)
                                    .setSession(producerSession)
                                    .setProcessedObject(processedObject);
//                            executorService.execute(sendingThread);

                            sendingThread.run();
                            processingFrame.setConsumed(true);

//                            executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
                            //                        executorService.execute(receiver);
                            System.out.println(
                                    "Total loop time = " + Duration.between(start, Instant.now())
                                            .toMillis() + " ms.");
                            System.out.println(
                                    "------------------------------------------------------");

                            canConsume = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            connection.close();

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
}
