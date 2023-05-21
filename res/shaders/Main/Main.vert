#version 330
in vec2 inPosition;

out vec3 viewWS;
out vec3 normalWS;

// Model, view, projection
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProj;

const float PI = 3.1416;
const float delta = 0.001;

vec3 getSphereObject(vec2 position) {
    float azimut = position.y * 2.f * PI;
    float zenit = position.x * PI;

    float r = 1.f;

    float x = r * sin(zenit) * cos(azimut);
    float y = 1 * r * sin(zenit) * sin(azimut);
    float z = 2 * r * cos(zenit);

    return vec3(x, y, z);
}

vec3 getNormal(vec2 position){
    vec3 u = getSphereObject(vec2(position.x + delta, position.y)) - getSphereObject(vec2(position.x - delta, position.y));
    vec3 v = getSphereObject(vec2(position.x, position.y + delta)) - getSphereObject(vec2(position.x, position.y - delta));

    return cross(u, v);
}

void main() {
    vec3 newPos = getSphereObject(inPosition);
    vec4 objectPosition = uView * uModel * vec4(newPos, 1.f);
    gl_Position = uProj * objectPosition;

    vec3 normal = getNormal(inPosition);

    viewWS = (uModel * vec4(newPos, 1.0) - inverse(uView) * vec4(vec3(0.), 1.)).xyz;
    normalWS = inverse(transpose(mat3(uModel))) * normal;
}
