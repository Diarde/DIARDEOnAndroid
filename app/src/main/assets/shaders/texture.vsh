uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
attribute vec3 vNormal;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
varying float v_light;
void main() {
    gl_Position = uMVPMatrix * vPosition;
    v_texCoord = a_texCoord;
    v_light = 0.6 + abs(dot(vNormal, vec3(0.5,0.5,0)));
}