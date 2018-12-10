package entities;

import models.TexturedModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frame {
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private Camera camera;

    public Frame(Map<TexturedModel, List<Entity>> entities) {
        this.entities = entities;
        this.camera = new Camera();
    }

    public Frame() {
    }

    public Map<TexturedModel, List<Entity>> getEntities() {
        return entities;
    }

    public Frame setEntities(
            Map<TexturedModel, List<Entity>> entities) {
        this.entities = entities;
        return this;
    }

    public Camera getCamera() {
        return camera;
    }

    public Frame setCamera(Camera camera) {
        this.camera = camera;
        return this;
    }
}
