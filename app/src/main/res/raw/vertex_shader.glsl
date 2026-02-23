    attribute vec4 aPosition;
    attribute vec2 aTexCoord;
    varying vec2 vTexCoord;
    uniform mat4 uTransform;

    void main() {
        gl_Position = aPosition;
        vTexCoord = (uTransform * vec4(aTexCoord, 0.0, 1.0)).xy;
    }