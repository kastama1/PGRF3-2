#version 330
in vec3 inPosition;
in vec3 inNormal;

out vec3 vertPosition;

// Model, view, projection
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProj;

uniform mat4 uSkyModel;

void main() {
    vertPosition = 2 * inPosition.xyz - 1.0;

    gl_Position = uProj * uView * uSkyModel * vec4(inPosition, 1.0);
}
