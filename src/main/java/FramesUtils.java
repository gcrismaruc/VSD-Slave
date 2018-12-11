import entities.Entity;
import entities.Frame;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderer.Loader;
import textures.ModelTexture;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FramesUtils {

    public static  Map<String, Frame> createFrames(Loader loader) {
        RawModel dragonModel = ObjLoader
                .loadObjFile(
                        "src/main/resources/dragon.obj", loader);

        RawModel stallModel = ObjLoader
                .loadObjFile(
                        "src/main/resources/stall.obj", loader);

        ModelTexture texture = new ModelTexture(
                loader.loadTexture(
                        "src/main/resources/stall.png"));
        TexturedModel dragonTexturedModel = new TexturedModel(dragonModel, texture);
        TexturedModel stallTexturedModel = new TexturedModel(stallModel, texture);

        Entity dragonEntityFrame1 = new Entity(dragonTexturedModel,
                new Vector3f(-15, 5, -65), 0, 100, 0, 1);

        Entity stallEntityFrame1 = new Entity(stallTexturedModel,
                new Vector3f(-10, -5, -55), 0, 180, 0, 1);

        Entity dragonEntityFrame2 = new Entity(dragonTexturedModel,
                new Vector3f(15, 5, -65), 0, 0, 0, 1);

        Entity stallEntityFrame2 = new Entity(stallTexturedModel,
                new Vector3f(10, -5, -55), 0, 180, 0, 1);

        Entity dragonEntityFrame3 = new Entity(dragonTexturedModel,
                new Vector3f(0, -10, -65), 0, 0, 0, 1);

        Map<TexturedModel, List<Entity>> entitiesFrame1 = new HashMap<>();
        entitiesFrame1.put(dragonTexturedModel, Collections.singletonList(dragonEntityFrame1));
        entitiesFrame1.put(stallTexturedModel, Collections.singletonList(stallEntityFrame1));

        Frame frame1 = new Frame(entitiesFrame1);

        Map<TexturedModel, List<Entity>> entitiesFrame2 = new HashMap<>();
        entitiesFrame2.put(dragonTexturedModel, Collections.singletonList(dragonEntityFrame2));
        entitiesFrame2.put(stallTexturedModel, Collections.singletonList(stallEntityFrame2));

        Frame frame2 = new Frame(entitiesFrame2);

        Map<TexturedModel, List<Entity>> entitiesFrame3 = new HashMap<>();
        entitiesFrame3.put(dragonTexturedModel, Collections.singletonList(dragonEntityFrame3));

        Frame frame3 = new Frame(entitiesFrame3);

        Map<String, Frame> frames = new HashMap<>();
        frames.put("frame1", frame1);
        frames.put("frame2", frame2);
        frames.put("frame3", frame3);

        return frames;
    }
}
