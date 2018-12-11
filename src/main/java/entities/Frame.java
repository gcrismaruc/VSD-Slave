package entities;

import models.TexturedModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frame {
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    public Frame(Map<TexturedModel, List<Entity>> entities) {
        this.entities = entities;
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
}
