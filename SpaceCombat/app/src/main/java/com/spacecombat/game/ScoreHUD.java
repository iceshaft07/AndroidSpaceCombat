package com.spacecombat.game;

import com.spacecombat.Camera;
import com.spacecombat.Component;
import com.spacecombat.GUI;
import com.spacecombat.GameObject;
import com.spacecombat.TextAnimation;
import com.spacecombat.Vector2;

public class ScoreHUD extends Component 
{
	public int nextUpdate = 1000;
	public int updateIncrement = 2500;
	public float updateIncrementIncrement = 1.1f;
	public boolean createPowerup = false;
	private HealthController healthController;
	private int lastScore = 0;

	public ScoreHUD (HealthController hc)
	{	
		this.healthController = hc;
		lastScore = 0;
	}
	
	String display = "000000";
	public void onGUI ()
	{			
		if (PlayerData.score >= nextUpdate)
		{
			updateIncrement *= updateIncrementIncrement;
			nextUpdate += updateIncrement;			
			createPowerup = true;
		}
		
		if (lastScore != PlayerData.score)
		{
			if (PlayerData.score < 10)
				display = "000000"+PlayerData.score;
			else if (PlayerData.score < 100)
				display = "00000"+PlayerData.score;
			else if (PlayerData.score < 1000)
				display = "0000"+PlayerData.score;
			else if (PlayerData.score < 10000)
				display = "000"+PlayerData.score;
			else if (PlayerData.score < 100000)
				display = "00"+PlayerData.score;
			else if (PlayerData.score < 1000000)
				display = "0"+PlayerData.score;
			else
			{
				display = "" + PlayerData.score;
			} 			
		}
		GUI.drawText(display, HUDConstants.scoreBoard.x, HUDConstants.scoreBoard.y, 100, 100);
	 
		lastScore = PlayerData.score;
	}
	
	public void onAfterUpdate ()
	{
		if (healthController.health <= 0)
		{
			PlayerData.setScore(0);
		}

		if (createPowerup)
		{
			//GameObject.create(PrefabFactory.createPowerUp(new Vector2(Camera.mainCamera.gameObject.transform.position.x + 200, Camera.mainCamera.gameObject.transform.position.y), -1, true));
		}
		createPowerup = false;
	}
}
