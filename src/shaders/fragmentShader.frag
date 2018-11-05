#version 150

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 lightColour;


void main(void){

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float nDot1 = dot(unitNormal, unitLightVector);
    float brigthness = max(nDot1, 0.0);
    vec3 diffuse = brigthness * lightColour;
	out_Color = vec4(diffuse, 1.0) * texture(textureSampler, pass_textureCoordinates);

}