package de.haw.tti.view;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import de.haw.tti.controller.SpaceInitiator;

public class Main extends BasicGame {

	public Main(String title)
	{
		super(title);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		SpaceInitiator.createSpace();
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		//TODO Call logic
		
	}

	
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		//TODO Render Streets
		g.setBackground(new Color(20,150,20));
		
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new Main("TTI Praktikum"));
			appgc.setDisplayMode(800, 600, false);
			appgc.setShowFPS(false);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
