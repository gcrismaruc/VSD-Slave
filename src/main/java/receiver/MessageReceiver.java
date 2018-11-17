package receiver;

import entities.ProcessingObject;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;

public class MessageReceiver implements Runnable{

    private MessageConsumer messageConsumer;
    private static int TIMEOUT = 10000;
    ProcessingObject processingObject;
    private boolean needToConsume = true;

    public MessageReceiver(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void run() {
        while(needToConsume) {
            try {
                System.out.println("here");
                Message message = messageConsumer.receive(TIMEOUT);
                ObjectMessage objectMessage = (ObjectMessage) message;

                processingObject = (ProcessingObject) objectMessage.getObject();
                needToConsume = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public MessageReceiver setNeedToConsume(boolean needToConsume) {
        this.needToConsume = needToConsume;
        return this;
    }

    public ProcessingObject getProcessingObject() {
        return processingObject;
    }

    public MessageReceiver setProcessingObject(ProcessingObject processingObject) {
        this.processingObject = processingObject;
        return this;
    }

    public boolean isNeedToConsume() {
        return needToConsume;
    }
}
