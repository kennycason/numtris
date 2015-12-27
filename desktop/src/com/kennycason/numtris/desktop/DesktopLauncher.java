package com.kennycason.numtris.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kennycason.numtris.Numtris;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Numtris";
		config.width = Numtris.WIDTH * Numtris.DIM + Numtris.HUD_WIDTH;
		config.height = Numtris.HEIGHT * Numtris.DIM;
		new LwjglApplication(new Numtris(), config);
	}
}
