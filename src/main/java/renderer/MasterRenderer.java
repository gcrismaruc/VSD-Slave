package renderer;

import entities.Camera;
import entities.Entity;
import entities.Frame;
import entities.Light;
import entities.ProcessedObject;
import models.TexturedModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import shaders.StaticShader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

public class MasterRenderer {
    private StaticShader shader = new StaticShader();
    private Renderer renderer = new Renderer(shader);

    private Frame frame = new Frame();

    byte[] depth = new byte[DisplayManager.WIDTH * DisplayManager.HEIGHT * 4];
    byte[] pxs = new byte[DisplayManager.WIDTH * DisplayManager.HEIGHT * 3];

    public ProcessedObject render(Light light, Camera camera) {
        renderer.prepare();
        shader.start();
        shader.loadLight(light);
        shader.loadViewMatrix(camera);

        renderer.render(frame);

        ByteBuffer depthBuffer = BufferUtils.createByteBuffer(
                DisplayManager.WIDTH * DisplayManager.HEIGHT * 4);
        ByteBuffer pixels = BufferUtils.createByteBuffer(
                DisplayManager.WIDTH * DisplayManager.HEIGHT * 3);

        GL11.glReadPixels(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL_DEPTH_COMPONENT,
                GL_FLOAT, depthBuffer);
        GL11.glReadPixels(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL_RGB,
                GL_UNSIGNED_BYTE, pixels);

        depthBuffer.get(depth);
        pixels.get(pxs);

        shader.stop();
        frame.getEntities()
                .clear();

        return new ProcessedObject(depth, pxs);

    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = frame.getEntities()
                .get(entityModel);

        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            frame.getEntities()
                    .put(entityModel, newBatch);
        }

    }

    public Frame getFrame() {
        return frame;
    }

    public MasterRenderer setFrame(Frame frame) {
        this.frame = frame;
        return this;
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
