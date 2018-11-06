package entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {
    private Vector3f position;
    private Vector3f colour;

    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Light setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public Vector3f getColour() {
        return colour;
    }

    public Light setColour(Vector3f colour) {
        this.colour = colour;
        return this;
    }
}