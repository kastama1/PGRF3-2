package solids;

import lwjglutils.OGLBuffers;

public class Cube {
    private OGLBuffers buffers;

    public Cube() {
        float[] vertexBuffer = {
                // bottom (z-) face
                1, 0, 0, 0, 0, -1,
                0, 0, 0, 0, 0, -1,
                1, 1, 0, 0, 0, -1,
                0, 1, 0, 0, 0, -1,
                // top (z+) face
                1, 0, 1, 0, 0, 1,
                0, 0, 1, 0, 0, 1,
                1, 1, 1, 0, 0, 1,
                0, 1, 1, 0, 0, 1,
                // x+ face
                1, 1, 0, 1, 0, 0,
                1, 0, 0, 1, 0, 0,
                1, 1, 1, 1, 0, 0,
                1, 0, 1, 1, 0, 0,
                // x- face
                0, 1, 0, -1, 0, 0,
                0, 0, 0, -1, 0, 0,
                0, 1, 1, -1, 0, 0,
                0, 0, 1, -1, 0, 0,
                // y+ face
                1, 1, 0, 0, 1, 0,
                0, 1, 0, 0, 1, 0,
                1, 1, 1, 0, 1, 0,
                0, 1, 1, 0, 1, 0,
                // y- face
                1, 0, 0, 0, -1, 0,
                0, 0, 0, 0, -1, 0,
                1, 0, 1, 0, -1, 0,
                0, 0, 1, 0, -1, 0
        };

        int[] indexBuffer = new int[36];

        for (int i = 0; i < 6; i++) {
            indexBuffer[i * 6] = i * 4;
            indexBuffer[i * 6 + 1] = i * 4 + 1;
            indexBuffer[i * 6 + 2] = i * 4 + 2;
            indexBuffer[i * 6 + 3] = i * 4 + 1;
            indexBuffer[i * 6 + 4] = i * 4 + 2;
            indexBuffer[i * 6 + 5] = i * 4 + 3;
        }
        OGLBuffers.Attrib[] attribs = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        buffers = new OGLBuffers(vertexBuffer, attribs, indexBuffer);
    }

    public OGLBuffers getBuffers() {
        return buffers;
    }
}
