package entities;

import renderer.MasterRenderer;

public class FrameUtils {
    public static void prepareFrame(Frame frame, MasterRenderer renderer) {

        frame.getEntities().forEach((model, entities1) -> {
            entities1.forEach(entity -> {
                entity.increaseRotation(0f, 1f, 0f);
                renderer.processEntity(entity);
            });
        });
    }
}
