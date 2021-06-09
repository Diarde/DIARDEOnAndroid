precision mediump float;
varying float v_light;

void main() {
    gl_FragColor = vec4(v_light, v_light, v_light, 1);
}