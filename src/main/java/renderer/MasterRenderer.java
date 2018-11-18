package renderer;

import entities.Camera;
import entities.Entity;
import entities.Frame;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;

import java.util.ArrayList;
import java.util.List;

public class MasterRenderer {
    private StaticShader shader = new StaticShader();
    private Renderer renderer = new Renderer(shader);

    private Frame frame =  new Frame();

    public void render(Light light, Camera camera) {
        renderer.prepare();
        shader.start();
        shader.loadLight(light);
        shader.loadViewMatrix(camera);

        renderer.render(frame);

        shader.stop();
        frame.getEntities().clear();

    }


    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = frame.getEntities().get(entityModel);

        if(batch != null) {
            batch.add(entity);
        }  else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            frame.getEntities().put(entityModel, newBatch);
        }

    }

    public Frame getFrame() {
        return frame;
    }

    public MasterRenderer setFrame(Frame frame) {
        this.frame = frame;
        return this;
    }

    public void cleanUp(){
        shader.cleanUp();
    }
}
