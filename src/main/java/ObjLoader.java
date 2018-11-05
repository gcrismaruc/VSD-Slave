import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderer.Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjLoader {

    public static RawModel loadObjFile(String fileName, Loader loader) {
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(new File(fileName));
        } catch (Exception e) {
            System.err.println("File couldn't be loaded.");
        }

        BufferedReader reader = new BufferedReader(fileReader);
        String line;

        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<String> fs = new ArrayList<>();

        float[] verticesArray;
        float[] normalsArray = null;
        float[] texturesArray = null;
        int[] indicesArray;

        try {
            while ((line = reader.readLine()) != null) {
                try {

                    //                    line = reader.readLine();
                    line = line.replaceAll("  ", " ");
                    String[] currentLine = line.split(" ");
                    if (line.startsWith("v ")) {
                        Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                                Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                        vertices.add(vertex);
                    } else if (line.startsWith("vt ")) {
                        Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                                Float.parseFloat(currentLine[2]));
                        textures.add(texture);
                    } else if (line.startsWith("vn ")) {
                        Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                                Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                        normals.add(normal);
                    } else if (line.startsWith("f ")) {
                        //                        texturesArray = new float[vertices.size() * 2];
                        //                        normalsArray = new float[vertices.size() * 3];
                        //                        break;
                        fs.add(line);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            //            while (line != null) {
            //                if (!line.startsWith("f ")) {
            //                    line = reader.readLine();
            //                    continue;
            //                }

            texturesArray = new float[vertices.size() * 2];
            normalsArray = new float[vertices.size() * 3];

            for (String line1 : fs) {

                String[] vertex1;
                String[] vertex2;
                String[] vertex3;

                String[] currentLine = line1.split(" ");
                if (currentLine[1].contains("//")) {
                    vertex1 = currentLine[1].split("//");
                } else {
                    vertex1 = currentLine[1].split("/");
                }

                if (currentLine[2].contains("//")) {
                    vertex2 = currentLine[2].split("//");
                } else {
                    vertex2 = currentLine[2].split("/");
                }

                if (currentLine[3].contains("//")) {
                    vertex3 = currentLine[3].split("//");
                } else {
                    vertex3 = currentLine[3].split("/");
                }

                processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);

                //line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;

        for(Vector3f vertex : vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for(int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return  loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
    }

    private static void processVertex(String[] vertexData, List<Integer> indices,
            List<Vector2f> textures, List<Vector3f> normals, float[] textureArray,
            float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        try {
            Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
            textureArray[currentVertexPointer * 2] = currentTex.x;
            textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;

            Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
            normalsArray[currentVertexPointer * 3] = currentNorm.x;
            normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
            normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
        } catch (Exception e) {
            System.err.println("Process vertex: " + vertexData[0] + " " + vertexData[1] + " " + vertexData[2] + " " + e.getMessage() + e.getCause());
        }
    }
}
