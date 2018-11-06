package renderer;

import entities.Entity;
import entities.ProcessedObject;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import toolBox.Maths;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

/**
 * Handles the rendering of a model to the screen.
 *
 * @author Karl
 */
public class Renderer {

    private static final float FOV = 50;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    byte[] depth = new byte[DisplayManager.WIDTH * DisplayManager.HEIGHT * 4];
    byte[] pxs = new byte[DisplayManager.WIDTH * DisplayManager.HEIGHT * 3];

    public Renderer(StaticShader staticShader) {
        createProjectionMatrix();
        staticShader.start();
        staticShader.loadProjectionMatrix(projectionMatrix);
        staticShader.stop();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(1, 1, 1, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public ProcessedObject render(Entity entity, StaticShader shader) {
        TexturedModel model = entity.getModel();
        RawModel rawModel = model.getRawModel();

        GL30.glBindVertexArray(model.getRawModel().getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        Matrix4f transformationMatrix = Maths
                .createTransformationMatrix(entity.getPosition(), entity.getRotX(),
                        entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getModelTexture().getTextureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
                GL11.GL_UNSIGNED_INT, 0);

        ByteBuffer depthBuffer = BufferUtils
                .createByteBuffer(DisplayManager.WIDTH * DisplayManager.HEIGHT * 4);
        ByteBuffer pixels = BufferUtils
                .createByteBuffer(DisplayManager.WIDTH * DisplayManager.HEIGHT * 3);

        GL11.glReadPixels(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL_DEPTH_COMPONENT,
                GL_FLOAT, depthBuffer);
        GL11.glReadPixels(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL_RGB,
                GL_UNSIGNED_BYTE, pixels);

        depthBuffer.get(depth);
        pixels.get(pxs);

//        processedObject.setDepthBuffer(depth);
//        processedObject.setPixels(pxs);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

        return new ProcessedObject(depth, pxs);
    }

}