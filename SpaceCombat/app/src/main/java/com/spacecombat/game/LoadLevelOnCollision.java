package com.spacecombat.game;

import com.spacecombat.BoxCollider;
import com.spacecombat.Collider;
import com.spacecombat.Component;
import com.spacecombat.GameObject;
import com.spacecombat.ai.AIPlayerEndLevel;

public class LoadLevelOnCollision extends Component {

	private boolean isFired;
	private final String level;

	public LoadLevelOnCollision (final String level) 
	{
		this.isFired = false;
		this.level = level;
	}

	@Override
	public void  collide (final GameObject whatIHit)
	{
		if (whatIHit.getName().equals("TopOfScreen"))
		{
			if (this.isFired)
			{
				return;
			}
			this.isFired = true;

			GameObject player = GameObject.findByName("player");
			if (player == null)
			{

			}
			PlayerInput pi = (PlayerInput)player.getComponent(PlayerInput.class);
			HealthController hc = (HealthController)player.getComponent(HealthController.class);
			BoxCollider bc = (BoxCollider)player.getRigidBody().getCollider();
			player.addComponent(new AIPlayerEndLevel(pi, hc, bc, level));
			//LevelLoader.loadLevel(this.level,false);
		}
	}
}
