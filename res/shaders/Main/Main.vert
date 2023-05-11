#version 330
in vec2 inPosition;

// Model, view, projection
uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProj;

// Mode
uniform int uModeObject;

const float PI = 3.1416;
const float delta = 0.001;

vec3 getSphereObject1(vec2 position) {
    float azimut = position.y * 2.f * PI;
    float zenit = position.x * PI;

    float r = 1.f;

    float x = r * sin(zenit) * cos(azimut);
    float y = 1 * r * sin(zenit) * sin(azimut);
    float z = 2 * r * cos(zenit);

    return vec3(x, y, z);
}

vec3 getObject(vec2 position){
    switch (uModeObject){
        case 0:
        return getSphereObject1(position);
        case 1:
        return getSphereObject1(position);
        default :
        return getSphereObject1(position);
    }
}

vec3 getNormal(vec2 position){
    vec3 u = getObject(vec2(position.x + delta, position.y)) - getObject(vec2(position.x - delta, position.y));
    vec3 v = getObject(vec2(position.x, position.y + delta)) - getObject(vec2(position.x, position.y - delta));

    return cross(u, v);
}

vec3 getTangent(vec2 position){
    vec3 p1 = vec3(position.x + delta, position.y, getObject(position).z);
    vec3 p2 = vec3(position.x - delta, position.y, getObject(position).z);
    vec3 t = (p1 - p2);

    return normalize(t);
}

void main() {
    vec3 newPos = getObject(inPosition);
    vec3 nor = getNormal(inPosition);

    vec4 objectPosition = uView * uModel * vec4(newPos, 1.f);

    gl_Position = uProj * objectPosition;
}
