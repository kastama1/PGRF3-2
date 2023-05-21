#version 330
in vec3 vertPosition;

out vec4 outColor;

uniform samplerCube uTextureID;

void main() {
    outColor = vec4(texture(uTextureID, vertPosition).rgb, 1.0);
}
