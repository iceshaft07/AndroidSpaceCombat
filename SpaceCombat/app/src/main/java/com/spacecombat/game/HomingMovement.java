package com.spacecombat.game;

import java.util.LinkedList;
import java.util.List;

import com.spacecombat.Component;
import com.spacecombat.GameObject;
import com.spacecombat.RigidBody;
import com.spacecombat.Tags;
import com.spacecombat.Util;
import com.spacecombat.Vector2;

public class HomingMovement extends Component {
	private float speed = 0;
	private int targets = 0;
	private final int ignoreTargets = Tags.powerup | Tags.shot;
	private final RigidBody rigidBody;	
	private GameObject target = null;
	private List<GameObject> gos = null;
	
	private final Vector2 temp = new Vector2();
	private final int accel = 8;
	
	private boolean foundOne = false;

	public HomingMovement (final int tags, final RigidBody r, final float speed)
	{
		this.targets = tags;
		this.rigidBody = r;
		this.speed = speed;
		
		this.rigidBody.speed.y = speed;
		this.rigidBody.speed.x = 0;
		this.foundOne = false;
	}

	public void calculateTrajectory() {

		if (this.target == null || this.target.isDestroyed())
		{
			return;
		}

		this.temp.x = (this.target.transform.position.x - this.gameObject.transform.position.x);
		this.temp.y = (this.target.transform.position.y - this.gameObject.transform.position.y);				

		this.temp.normalize();
		
		this.temp.x = this.temp.x * -this.speed;
		this.temp.y = this.temp.y * -this.speed;
		
		if (this.rigidBody.speed.x < temp.x-accel)
		{
			this.rigidBody.speed.x+=accel;	
		}
		else if (this.rigidBody.speed.x > temp.x+accel)
		{
			this.rigidBody.speed.x-=accel;	
		}
		else
		{
			this.rigidBody.speed.x = temp.x;
		}
		
		if (this.rigidBody.speed.y < temp.y-accel)
		{
			this.rigidBody.speed.y+=accel;	
		}
		else if (this.rigidBody.speed.y > temp.y+accel)
		{
			this.rigidBody.speed.y-=accel;	
		}	
		else
		{
			this.rigidBody.speed.y = temp.y;
		}
	}

	public void search() 
	{
		if (this.gos == null)
		{//
			this.gos = new LinkedList<GameObject>();
		}
		this.gos = GameObject.findAllByTags(this.targets, this.ignoreTargets, this.gos);

		if (this.gos == null || this.gos.size() == 0) {
			this.target = null;
			return;
		}

		final int x = Util.randomNumber(0, this.gos.size() - 1);

		this.target = this.gos.get(x);
		this.foundOne = true;
	}

	public void setSpeed(final float speed) {
		this.speed = speed;
	}

	@Override
	public void update() {

		if (!this.foundOne && (this.target == null || this.target.isDestroyed()))
		{
			search();
		}
		calculateTrajectory();
	}
}
