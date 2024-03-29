package com.zerologic.pong.engine.components;

import static org.lwjgl.opengl.GL46.*;

import org.joml.Vector2f;
import com.zerologic.pong.engine.Texture;

/**
 * 
 * @author Dilan Shabani
 */

public class GameObject {

	// Vertex data objects
	int VAO, VBO, EBO;

	// Size/pos vector
	public Vector2f pos;
	public Vector2f size;

	// Texture loading
	Texture texture = new Texture("src/main/resources/textures/def.png", false, GL_RGB);

	/**
	 * Create a new {@code GameObject} with the given x and y parameters and given
	 * size.
	 * 
	 * @author Dilan Shabani
	 * @param x      The x position to place the {@code GameObject}.
	 * @param y      The y position to place the {@code GameObject}.
	 * @param width  The width to give to the {@code GameObject}.
	 * @param height The height to give to the {@code GameObject}.
	 */

	public GameObject(float x, float y, float width, float height) {
		pos = new Vector2f(x, y);
		size = new Vector2f(width, height);
		
		// Vertex data
		float[] data = {
			// Vertex positions	   // Tex coords
			0.0f,   0.0f,   0.0f, 1.0f, // Top left
			size.x, 0.0f,   1.0f, 1.0f, // Top right
			size.x, size.y, 1.0f, 0.0f, // Bottom right
			0.0f,   size.y, 0.0f, 0.0f  // Bottom left
		};
		
		int[] indices = {
			0, 1, 3,
			1, 2, 3
		};

		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);

		VBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

		EBO = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		glVertexAttribPointer(0, 4, GL_FLOAT, false, 16, 0);
		glEnableVertexAttribArray(0);

		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	/**
	 * Create a new {@code GameObject} with initial position (0, 0) and given size.
	 * 
	 * @author Dilan Shabani
	 * @param width  The width to give to the {@code GameObject}.
	 * @param height The height to give to the {@code GameObject}.
	 */
	
	public GameObject(float width, float height) {
		pos = new Vector2f(0, 0);
		size = new Vector2f(width, height);
		
		// Vertex data
		float[] data = {
			// Vertex positions	   // Tex coords
			0.0f,   0.0f,   0.0f, 1.0f, // Top left
			size.x, 0.0f,   1.0f, 1.0f, // Top right
			size.x, size.y, 1.0f, 0.0f, // Bottom right
			0.0f,   size.y, 0.0f, 0.0f  // Bottom left
		};
		
		int[] indices = {
			0, 1, 3,
			1, 2, 3
		};

		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);

		VBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, VBO);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

		EBO = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		glVertexAttribPointer(0, 4, GL_FLOAT, false, 16, 0);
		glEnableVertexAttribArray(0);

		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	protected void draw() {
		texture.use();
		glBindVertexArray(VAO);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
	}

	public float width() {
		return this.size.x;
	}

	public float height() {
		return this.size.y;
	}

	/**
	 * @param width  The width to give to the {@code GameObject}.
	 * @param height The height to give to the {@code GameObject}.
	 * @deprecated
	 */

	public void setSize(float width, float height) {
		size.x = width;
		size.y = height;
	}

	/**
	 * @return The x position of the {@code GameObject}.
	 */

	public float x() {
		return this.pos.x;
	}

	/**
	 * @return The y position of the {@code GameObject}.
	 */

	public float y() {
		return this.pos.y;
	}

	/**
	 * Retains the x position of the object.
	 * @param add The amount to increment the x position of the {@code GameObject}.
	 */

	public void addToX(float add) {
		this.pos.x += add;
	}

	/**
	 * Retains the y position of the object.
	 * @param add The amount to increment the y position of the {@code GameObject}.
	 */

	public void addToY(float add) {
		this.pos.y += add;
	}

	/**
	 * This method retains the current position and increments it via the given parameters.
	 *
	 * @param x The amount to increment the x position of the {@code GameObject}.
	 * @param y The amount to increment the y position of the {@code GameObject}.
	 */

	public void addPos(float x, float y) {
		this.pos.x += x;
		this.pos.y += y;
	}

	/**
	 * @param x The x position to place the {@code GameObject}.
	 * @param y The y position to place the {@code GameObject}.
	 */

	public void setPos(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	/**
	 * @param filepath        The file path to the texture to be applied.
	 * @param flipImageOnLoad Whether or not to flip the image on load.
	 * @param type            The type of color channel the texture will use (GL_RGB
	 *                        or GL_RGBA).
	 */

	public void setTexture(String filepath, boolean flipImageOnLoad, int type) {
		texture = new Texture(filepath, flipImageOnLoad, type);
	}

	/**
	 * @param texture The {@code Texture} to be applied.
	 */

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}