precision mediump float;
varying vec2 v_texCoord;
varying float v_light;
uniform sampler2D s_texture;
uniform float f_offset;
void main() {
    vec4 color = texture2D( s_texture, vec2(v_texCoord.x + f_offset, v_texCoord.y ));
    gl_FragColor = vec4(color.x * v_light, color.y * v_light,color.z * v_light, 1);
}