package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
    public static final float MOUSE_VELOCITY = 10f;
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;

    public Camera(){

    }

    public void move() {
        if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
            pitch -= 0.9f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
            pitch += 0.9f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
            yaw += 0.9f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
            yaw -= 0.9f;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_Z)){
            position.x += 0.9f;
            position.y += 0.9f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_Y)){
            position.z += 0.9f;
            position.x += 0.9f;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_X)){
            position.y += 0.9f;
            position.z += 0.9f;
        }

        if(Mouse.isButtonDown(0)) {
            position.x -= Mouse.getDX() * 0.05f;
            position.y -= Mouse.getDY() * 0.05f;
        }

        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            position.z += MOUSE_VELOCITY;
        } else if (dWheel > 0){
            position.z -= MOUSE_VELOCITY;
        }

//        System.out.println("X = " + position.x + " Y = " + position.y + " Z = " + position.z);


    }
    public Vector3f getPosition() {
        return position;
    }

    public Camera setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public float getPitch() {
        return pitch;
    }

    public Camera setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public float getYaw() {
        return yaw;
    }

    public Camera setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float getRoll() {
        return roll;
    }

    public Camera setRoll(float roll) {
        this.roll = roll;
        return this;
    }
}
