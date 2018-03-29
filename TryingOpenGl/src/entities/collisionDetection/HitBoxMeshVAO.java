package entities.collisionDetection;

public class HitBoxMeshVAO {
    private float[] vertexPositions;
    private float[] vertexNormals;
    private int[] vertexIndices;
    public HitBoxMeshVAO(float[] positions, float[] normals, int[] indices){
        vertexPositions = positions;
        vertexNormals = normals;
        vertexIndices = indices;
    }

    public float[] getVertexPositions() {
        return vertexPositions;
    }

    public float[] getVertexNormals() {
        return vertexNormals;
    }

    public int[] getVertexIndices() {
        return vertexIndices;
    }
}
