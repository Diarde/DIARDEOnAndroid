uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
attribute vec3 vNormal;
varying float v_light;
void main() {
    gl_Position = uMVPMatrix * vPosition;
    v_light = 0.5 + abs(dot(vNormal, vec3(0.5,0.5,0)));
}