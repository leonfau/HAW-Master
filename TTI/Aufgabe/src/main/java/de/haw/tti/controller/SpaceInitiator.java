package de.haw.tti.controller;

import java.util.concurrent.TimeUnit;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitAlreadyDeployedException;
import org.openspaces.admin.space.SpaceDeployment;

public class SpaceInitiator {

	public static void createSpace() {
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
}
