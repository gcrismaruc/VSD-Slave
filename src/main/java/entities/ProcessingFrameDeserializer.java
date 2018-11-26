package entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class ProcessingFrameDeserializer implements Deserializer {
    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public ProcessingFrame deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        ProcessingFrame processingFrame = null;
        try {
            processingFrame = mapper.readValue(bytes, ProcessingFrame.class);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return processingFrame;
    }

    @Override
    public void close() {

    }
}
