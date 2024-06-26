#ifdef GL_ES
#define LOWP lowp
#define MED mediump
precision lowp float;
#else
#define LOWP
#define MED
#endif

uniform sampler2D u_texture;

varying MED vec2 v_texCoords0;
varying MED vec2 v_texCoords1;
varying MED vec2 v_texCoords2;
varying MED vec2 v_texCoords3;
varying MED vec2 v_texCoords4;

uniform float u_depth_min;
uniform float u_depth_max;

uniform vec4 u_outer_color;
uniform vec4 u_inner_color;

#ifdef DISTANCE_FALLOFF
uniform float u_depthRange;
#endif

void main() {
const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);


float depth = abs(
dot(texture2D(u_texture, v_texCoords0), bitShifts) +
dot(texture2D(u_texture, v_texCoords1), bitShifts) -
dot(4.0 * texture2D(u_texture, v_texCoords2), bitShifts) +
dot(texture2D(u_texture, v_texCoords3), bitShifts) +
dot(texture2D(u_texture, v_texCoords4), bitShifts)
);

if (depth > u_depth_min){
if(depth < u_depth_max){
gl_FragColor = u_inner_color;
}
else{
gl_FragColor = u_outer_color;
}
#ifdef DISTANCE_FALLOFF
		float centerDepth = dot(texture2D(u_texture, v_texCoords2), bitShifts);
gl_FragColor.a *= 1.0 - pow(centerDepth, u_depthRange);
#endif
	}
else{
gl_FragColor = vec4(1.0, 1.0, 1.0, 0.0);
}

}