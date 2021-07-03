package com.zerologic.pong.engine.components.gui.uitext;

import com.zerologic.pong.Game;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.stb.*;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL40.*;

import static org.lwjgl.stb.STBTruetype.*;

import java.nio.FloatBuffer;
import java.util.Vector;

public class UIText {

    private String text;

    private final FloatBuffer x = BufferUtils.createFloatBuffer(1);
    private final FloatBuffer y = BufferUtils.createFloatBuffer(1);
    private final Vector<STBTTAlignedQuad> bakedChars = new Vector<>();

    // X and Y of the actual quad
    private Vector2f pos;
    private float fontSize;

    private Vector2f size;
    private Vector4f color; // RGBA

    private int VAO;
    private int VBO;
    private int EBO;

    static int[] indices = {
        0, 1, 3,
        1, 2, 3
    };

    // Used to set position explicitly after creating
    public UIText(String text, float fontSize) {
        this.text = text;
        this.fontSize = fontSize;
        this.pos = new Vector2f(0f, 0f);
        this.color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f); // Set font color to black by default

        init();
    }

    public UIText(String text, float fontSize, float x, float y) {
        this.text = text;
        this.fontSize = fontSize;
        this.pos = new Vector2f(x, y);
        this.color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f); // Set font color to black by default

        init(); // Call initialize method from constructor
    }

    public UIText(int text, float fontSize, float x, float y) {
        this.text = Integer.toString(text);
        this.fontSize = fontSize;
        this.pos = new Vector2f(x, y);
        this.color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f); // Set font color to black by default

        init(); // Call initialize method from constructor
    }

    public UIText(float text, float fontSize, float x, float y) {
        this.text = Float.toString(text);
        this.fontSize = fontSize;
        this.pos = new Vector2f(x, y);
        this.color = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f); // Set font color to black by default

        init(); // Call initialize method from constructor
    }

    private void init() {
        // For debug purposes
        // System.out.println("Ascent: " + STBFontLoader.getAscent() + " Descent: " + -STBFontLoader.getDescent() + " LineGap: " + STBFontLoader.getLineGap());
        // System.out.println("Xpos: " + this.x.get(0) + " Ypos: " + this.y.get(0));

        // Activate the shader and set the color
        UIFontLoader.getShaderProgram().use();
        UIFontLoader.getShaderProgram().setVector4f(this.color, "color");

        // Initialize fontloader with given size and clear buffers and baked chars, reset origin
        UIFontLoader.generateBitmap(fontSize); // Load a font bitmap with the desired size
        bakedChars.clear();
        resetPosBuffers();

        // Create baked chars for each of the characters in the string
        for (int i = 0; i < text.length(); i++) {

            // If newline, set virtual cursor accordingly
            if (text.charAt(i)=='\n') {
                // Virtual cursor positions
                // float vCursorX = this.x.get(0);
                float vCursorY = this.y.get(0);
                this.putPosBuffer(0, getNewlineYOff(vCursorY)); // Put these 2 values in the x and y pos buffers respectively
                continue;
            }

            STBTTAlignedQuad q = STBTTAlignedQuad.create();
            stbtt_GetBakedQuad(UIFontLoader.getFontBySize(fontSize).getCharData(), UIFontLoader.getFontBySize(fontSize).getBmpSize(), UIFontLoader.getFontBySize(fontSize).getBmpSize(), text.charAt(i) - 32, x, y, q, true);

            bakedChars.add(q);
        }

        size = new Vector2f(bakedChars.lastElement().x1(), fontSize);

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 4, GL_FLOAT, false, 16, 0);
        glEnableVertexAttribArray(0);
    }

    // Draw only code, must set shader in renderer class!
    public void draw() {
        // Activate text shader to change color of each text object
        UIFontLoader.getShaderProgram().use();
        UIFontLoader.getShaderProgram().setVector4f(this.color, "color");

        glBindTexture(GL_TEXTURE_2D, UIFontLoader.getFontBySize(fontSize).textureID());
        glBindVertexArray(VAO);

        for(STBTTAlignedQuad q : bakedChars) {
            float[] charVerts = {
                q.x0(), q.y0(),   q.s0(), q.t0(), // Top left
                q.x1(), q.y0(),   q.s1(), q.t0(), // Top right
                q.x1(), q.y1(),   q.s1(), q.t1(), // Bottom right
                q.x0(), q.y1(),   q.s0(), q.t1()  // Bottom left
            };

            glBindBuffer(GL_ARRAY_BUFFER, VBO);
            glBufferData(GL_ARRAY_BUFFER, charVerts, GL_DYNAMIC_DRAW);

            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        }
    }

    private float getNewlineYOff(float vCursorY) {
        return vCursorY + (((UIFontLoader.getFontBySize(fontSize).ascent() - UIFontLoader.getFontBySize(fontSize).descent() + UIFontLoader.getFontBySize(fontSize).lineGap()) * UIFontLoader.getFontBySize(fontSize).scale()));
    }

    private void resetPosBuffers() {
        this.x.position(0);
        this.x.put(0);
        this.x.flip();

        this.y.position(0);
        this.y.put(UIFontLoader.getFontBySize(fontSize).ascent() * UIFontLoader.getFontBySize(fontSize).scale());
        this.y.flip();
    }

    private void putPosBuffer(float xpos, float ypos) {
        this.x.position(0);
        this.x.put(xpos);
        this.x.flip();

        this.y.position(0);
        this.y.put(ypos);
        this.y.flip();
    }

    public void setColor(float r, float g, float b, float a) {
        // Check if color isn't the same as current so no need to create a new vec every time
        if (!(this.color.x == r && this.color.y == g && this.color.z == b && this.color.w == a))
        {
            this.color = new Vector4f(r, g, b, a);
            UIFontLoader.getShaderProgram().use();
            UIFontLoader.getShaderProgram().setVector4f(this.color, "color");
        }
    }

    public void setColor(Vector4f color) {
        if(!color.equals(this.color)) {
            this.color = color;
            UIFontLoader.getShaderProgram().use();
            UIFontLoader.getShaderProgram().setVector4f(this.color, "color");
        }
    }

    public float x() {
        return this.pos.x;
    }

    public float y() {
        return this.pos.y;
    }

    public float width() {
        return this.size.x + 3f;
    } // Add padding to the right of 3 pixels

    public float height() {
        return this.size.y;
    }

    public Vector2f pos() {
        return this.pos;
    }

    public void setPos(float x, float y) {
        this.pos.x = x;
        this.pos.y = y;
    }

    public String text() {
        return this.text;
    }

    public void setText(String value) {
        if (!value.equals(text)) {
            text = value;
            init();
        }
    }

    public void setText(int value) {
        if (!Integer.toString(value).equals(this.text)) {
            this.text = Integer.toString(value);
            init();
        }
    }

    public float fontSize() {
        return this.fontSize;
    }

    public void setFontSize(float value) {
        fontSize = value;
        init();
    }
}