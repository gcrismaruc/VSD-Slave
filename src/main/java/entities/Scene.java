package entities;

import java.util.Map;

public class Scene {
    private Map<String, Frame> frames;

    public Scene() {
    }

    public Map<String, Frame> getFrames() {
        return frames;
    }

    public Scene setFrames(Map<String, Frame> frames) {
        this.frames = frames;
        return this;
    }
}
