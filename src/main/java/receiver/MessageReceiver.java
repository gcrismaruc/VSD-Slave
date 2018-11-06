package receiver;

import entities.ProcessingObject;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

public class MessageReceiver implements Runnable{

    private Session session;
    private MessageConsumer messageConsumer;
    private static int TIMEOUT = 10000;
    ProcessingObject processingObject;

    public MessageReceiver(Session session, MessageConsumer messageConsumer) {
        this.session = session;
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Message message = messageConsumer.receive(TIMEOUT);
                ObjectMessage objectMessage = (ObjectMessage) message;

                processingObject = (ProcessingObject) objectMessage.getObject();
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ProcessingObject getProcessingObject() {
        return processingObject;
    }

    public MessageReceiver setProcessingObject(ProcessingObject processingObject) {
        this.processingObject = processingObject;
        return this;
    }
}
