#version 330

in vec3 viewWS;
in vec3 normalWS;

out vec4 outColor;

uniform mat4 uSkyModel;

uniform samplerCube uTextureID;

float fastFresnel(vec3 I, vec3 N){
    return 10.f * pow(dot(I, N), 4.0);
}

void main() {
    vec3 reflectedWS = reflect(normalize(-viewWS), normalize(normalWS));
    vec4 reflectedColor = vec4(texture(uTextureID, normalize(mat3(uSkyModel) * reflectedWS)).rgb, 1.f) * vec4(1.f, 1.f, 0.8, 1.f);

    vec3 refractedWS = refract(normalize(-viewWS), normalize(normalWS), 1.03);
    vec4 refractedColor = vec4(texture(uTextureID, normalize(mat3(uSkyModel) * refractedWS)).rgb, 1.f) * vec4(0.7, 0.7, 1.f, 1.f);

    float fresnelTerm = clamp(fastFresnel(normalize(vec3(0.f, 0.f, 1.f)), normalize(normalWS)), 0.f, 1.f);

    outColor = reflectedColor;
}
