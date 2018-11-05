package entities;

import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;

public class Entity {
    private TexturedModel model;
    private Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
            float scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }


    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }

    public TexturedModel getModel() {
        return model;
    }

    public Entity setModel(TexturedModel model) {
        this.model = model;
        return this;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Entity setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public float getRotX() {
        return rotX;
    }

    public Entity setRotX(float rotX) {
        this.rotX = rotX;
        return this;
    }

    public float getRotY() {
        return rotY;
    }

    public Entity setRotY(float rotY) {
        this.rotY = rotY;
        return this;
    }

    public float getRotZ() {
        return rotZ;
    }

    public Entity setRotZ(float rotZ) {
        this.rotZ = rotZ;
        return this;
    }

    public float getScale() {
        return scale;
    }

    public Entity setScale(float scale) {
        this.scale = scale;
        return this;
    }
}
