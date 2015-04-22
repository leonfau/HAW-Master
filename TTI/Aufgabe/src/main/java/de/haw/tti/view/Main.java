package de.haw.tti.view;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitAlreadyDeployedException;
import org.openspaces.admin.space.SpaceDeployment;
import org.openspaces.core.GigaSpace;

public class Main extends BasicGame {

	public Main(String title)
	{
		super(title);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		//Create Space
		ProcessingUnit pu = null;
		try {
		    Admin admin = new AdminFactory().createAdmin();
		    
		    GridServiceManager esm = admin.getGridServiceManagers().waitForAtLeastOne();
		    pu = esm.deploy(new SpaceDeployment("mySpace").partitioned(1, 0));
		    pu.waitFor(1, 30, TimeUnit.SECONDS);
		} catch (ProcessingUnitAlreadyDeployedException e)  { 
		    //already deployed, do nothing 
		}
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
