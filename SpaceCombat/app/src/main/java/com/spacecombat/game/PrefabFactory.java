package com.spacecombat.game;

import java.io.InputStream;

import android.content.Context;
import android.media.MediaPlayer;

import com.spacecombat.Animation;
import com.spacecombat.Audio;
import com.spacecombat.BoxCollider;
import com.spacecombat.Camera;
import com.spacecombat.CanvasGraphic;
import com.spacecombat.CanvasText;
import com.spacecombat.Collider;
import com.spacecombat.Component;
import com.spacecombat.FixedJoint;
import com.spacecombat.GameObject;
import com.spacecombat.GenericGraphic;
import com.spacecombat.GenericText;
import com.spacecombat.GraphicAnimation;
import com.spacecombat.Level;
import com.spacecombat.R;
import com.spacecombat.RigidBody;
import com.spacecombat.Tags;
import com.spacecombat.TextAnimation;
import com.spacecombat.Time;
import com.spacecombat.Util;
import com.spacecombat.Vector2;
import com.spacecombat.ai.AIBoss2;
import com.spacecombat.ai.AIBoss3;
import com.spacecombat.ai.AIBoss4;
import com.spacecombat.ai.AIBoss5;
import com.spacecombat.ai.AINodeFollower;
import com.spacecombat.ai.AIScript;
import com.spacecombat.ai.AIScriptEight;
import com.spacecombat.ai.AIScriptFive;
import com.spacecombat.ai.AIScriptFour;
import com.spacecombat.ai.AIScriptNine;
import com.spacecombat.ai.AIScriptOne;
import com.spacecombat.ai.AIScriptSix;
import com.spacecombat.ai.AIScriptTen;
import com.spacecombat.ai.AIScriptThree;
import com.spacecombat.ai.AIScriptTwo;
import com.spacecombat.ai.AllyAI;
import com.spacecombat.ai.Node;
import com.spacecombat.ai.AIPlayerStartLevel;
import com.spacecombat.weapons.Boss2WeaponHandler;
import com.spacecombat.weapons.Boss3WeaponHandler;
import com.spacecombat.weapons.Boss4WeaponHandler;
import com.spacecombat.weapons.Boss5WeaponHandler;
import com.spacecombat.weapons.ChargeLaser;
import com.spacecombat.weapons.FlameThrower;
import com.spacecombat.weapons.GunShipWeaponController;
import com.spacecombat.weapons.Laser;
import com.spacecombat.weapons.LockingLaser;
import com.spacecombat.weapons.LockingWeaponHandler;
import com.spacecombat.weapons.MachineGun;
import com.spacecombat.weapons.MissileLauncher;
import com.spacecombat.weapons.Phaser;
import com.spacecombat.weapons.PulseCannon;
import com.spacecombat.weapons.Weapon;
import com.spacecombat.weapons.WeaponController;
import com.spacecombat.weapons.WeaponHandler;

public class PrefabFactory {
	private static boolean useOpenGl = false;
	private static int defaultFps = 17;

	private static final int enemyTargets = Tags.player | Tags.ally;
	private static final int playerTargets = Tags.enemy;
	private static final int nodeTag = Tags.node;

	private static final Vector2 shootDown = new Vector2(0, 200);
	private static final Vector2 shootDownLeft = new Vector2(-8, 8);
	private static final Vector2 shootDownRight = new Vector2(8, 8);
	private static final Vector2 shootLeft = new Vector2(-8, 0);
	private static final Vector2 shootRight = new Vector2(8, 0);
	private static final Vector2 shootUpLeft = new Vector2(-8, -8);
	private static final Vector2 shootUpRight = new Vector2(8, -8);
	private static final Vector2 shootUp = new Vector2(0, -8);


	public static final int HARD = 0;
	public static final int MEDIUM = 1;
	public static final int EASY = 2;
	private static int difficulty = HARD;

	private static Context context;	

	private static Collider phaserCollider = null;

	public static int spawner = 0;

	public static GameObject createEscort(final String name, final Vector2 position)
	{
		//System.out.println("Creating Gunship");
		GameObject o = GameObject.getNew();

		
		final GenericGraphic graphic = PrefabFactory.createGraphic("escort",
				PrefabFactory.getImage("escort"),1);
		
		final RigidBody rigidBody = new RigidBody();
		final BoxCollider collider = new BoxCollider(new Vector2(64, 300));
		collider.setIgnoreTags(Tags.player | Tags.topOfScreen | Tags.ally);
		rigidBody.setCollider(collider);
	

		o.setName(name);
		o.setTags(Tags.ally);
		o.transform.position.x = position.x;
		o.transform.position.y = position.y;
		o.setRigidBody(rigidBody);
		
		Animation animation = Animation.getNew();
		animation.init("up", 0, 2, true,
				PrefabFactory.defaultFps, 64, 300);

		final GraphicAnimation glIdle = new GraphicAnimation(graphic, animation);
		glIdle.play();
		o.addComponent(glIdle);
		
		o.setRigidBody(rigidBody);
		
		//System.out.println("Adding Weapons");
		Weapon w[] = new Weapon[4];

		w[0] = new MissileLauncher(PrefabFactory.shootUp);
		w[0].setPowerLevel(3);
		w[0].setOffset(16, 0);

		w[1] = new LockingLaser(PrefabFactory.shootUp);
		w[1].setPowerLevel(3);
		w[1].setOffset(16,64);

		w[2] = new MissileLauncher(PrefabFactory.shootUp);
		w[2].setPowerLevel(3);
		w[2].setOffset(16,128);
		
		w[3] = new LockingLaser(PrefabFactory.shootUp);
		w[3].setPowerLevel(3);
		w[3].setOffset(16,256);
		o.addComponent(w[0]);
		o.addComponent(w[1]);
		o.addComponent(w[2]);
		o.addComponent(w[3]);

		//System.out.println("Added Weapons");
		
		final LockingWeaponHandler lwh = new LockingWeaponHandler(w[1],PrefabFactory.playerTargets,false);
		o.addComponent(lwh);

		final LockingWeaponHandler lwh2 = new LockingWeaponHandler(w[3],PrefabFactory.playerTargets,false);
		o.addComponent(lwh2);
		
		o.addComponent(new DestroyOnTopOfScreen(330));
		
		SimpleMovement s = new SimpleMovement();
		s.init(rigidBody, 0, -100);
		o.addComponent(s);
		
		GunShipWeaponController gswc = new GunShipWeaponController(w); 
		o.addComponent(gswc);
		
		return o;
	}
	
	public static GameObject createAlly(final String name,
			final Vector2 position, final String allyType, int allyScript, final String weapon) {
		// Load the texture for the cube once during Surface creation
		final GenericGraphic graphic = PrefabFactory.createGraphic(allyType,
				PrefabFactory.getImage(allyType),1);

		Animation idle = Animation.getNew();
		idle.init("idle", 0, 5, true,
				PrefabFactory.defaultFps, 32, 32);
		idle.setFrameIncrement(2);
		Animation death = Animation.getNew();
		death.init("death", 0, 1, true,
				PrefabFactory.defaultFps, 32, 32);

		Animation left = Animation.getNew();
		left.init("left", 3, 3, true,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation upleft = Animation.getNew();
		upleft.init("upleft", 8, 8, true,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation up = Animation.getNew();
		up.init("up", 0, 0, true,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation upright = Animation.getNew();
		upright.init("upright", 6, 6, true,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation right = Animation.getNew();
		right.init("right", 1, 1, true,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation downright = Animation.getNew();
		downright.init("downright", 5, 5, true,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation down = Animation.getNew();
		down.init("down", 2, 2, true,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation downleft = Animation.getNew();
		downleft.init("downleft", 7, 7, true,
				PrefabFactory.defaultFps, 32, 32);


		final GraphicAnimation glIdle = new GraphicAnimation(graphic, idle);
		final GraphicAnimation glDeath = new GraphicAnimation(graphic, death);
		final GraphicAnimation glLeft = new GraphicAnimation(graphic, left);
		final GraphicAnimation glUpLeft = new GraphicAnimation(graphic, upleft);
		final GraphicAnimation glUp = new GraphicAnimation(graphic, up);
		final GraphicAnimation glUpRight = new GraphicAnimation(graphic, upright);
		final GraphicAnimation glRight = new GraphicAnimation(graphic, right);
		final GraphicAnimation glDownRight = new GraphicAnimation(graphic, downright);
		final GraphicAnimation glDown = new GraphicAnimation(graphic, down);
		final GraphicAnimation glDownLeft = new GraphicAnimation(graphic, downleft);

		glIdle.play();


		
		final RigidBody rigidBody = new RigidBody();
		final Collider collider = new BoxCollider(new Vector2(32, 32));
		collider.setIgnoreTags(Tags.player | Tags.topOfScreen | Tags.ally);

		rigidBody.setCollider(collider);

		final GameObject o = GameObject.getNew();
		//final GameObject o = new GameObject();
		o.setName(name);
		o.setTags(Tags.ally | Tags.allyShip);
		o.transform.position.x = position.x;
		o.transform.position.y = position.y;
		o.setRigidBody(rigidBody);
		o.setDestroyOnLevelLoad(true);
		
		Weapon w = null;
		if (weapon.equals("laser")) {
			w = new Laser(PrefabFactory.shootUp);
		}
		if (weapon.equals("machin" +
				"egun")) {
			w = new MachineGun(PrefabFactory.shootUp);
		}
		if (weapon.equals("flamethrower")) {
			w = new FlameThrower(PrefabFactory.shootUp);
		}
		if (weapon.equals("pulsecannon")) {
			w = new PulseCannon(PrefabFactory.shootUp);
		}
		if (weapon.equals("missilelauncher")) {
			
			w = new MissileLauncher(PrefabFactory.shootUp);
			w.setPowerLevel(3);
		}
		if (weapon.equals("lockinglaser")) {
			w = new LockingLaser(PrefabFactory.shootUp);
			w.setPowerLevel(3);
			final LockingWeaponHandler lwh = new LockingWeaponHandler(w,PrefabFactory.playerTargets,false);
			o.addComponent(lwh);
		}
		
		final AllyAI ai = new AllyAI(allyType,w);
		o.addComponent(ai);
		o.addComponent(w);
		
		final HealthController hc = new HealthController(new Audio(PrefabFactory.createAudio("explosion"),"explosion"));
		
		if (allyType.equals("calumniator"))
		{
			ai.setSpeed(new Vector2(110, 110));
			ai.setAccel(new Vector2(20,20));
			hc.setHealth(120);
		}
		else if (allyType.equals("exemplar"))
		{
			ai.setSpeed(new Vector2(110, 110));
			ai.setAccel(new Vector2(20,20));
			hc.setHealth(120);
		}
		else if (allyType.equals("hunter"))
		{
			ai.setSpeed(new Vector2(110, 110));
			ai.setAccel(new Vector2(20,20));
			hc.setHealth(120);
		}
		else if (allyType.equals("paladin"))
		{
			ai.setSpeed(new Vector2(110, 110));
			ai.setAccel(new Vector2(20,20));
			hc.setHealth(120);
		}
		else if (allyType.equals("pariah"))
		{
			ai.setSpeed(new Vector2(110, 110));
			ai.setAccel(new Vector2(20,20));
			hc.setHealth(120);
		}
		else if (allyType.equals("renegade"))
		{
			ai.setSpeed(new Vector2(110, 110));
			ai.setAccel(new Vector2(20,20));
			hc.setHealth(120);
		}
		else if (allyType.equals("sentinel"))
		{
			ai.setSpeed(new Vector2(110, 110));
			ai.setAccel(new Vector2(20,20));
			hc.setHealth(120);
		}
		else
		{
			ai.setSpeed(new Vector2(110, 110));
			ai.setAccel(new Vector2(20,20));
			hc.setHealth(120);
		}
		
		//o.addComponent(hc);

		o.addComponent(glIdle);
		o.addComponent(glDeath);
		o.addComponent(glLeft);
		o.addComponent(glUpLeft);
		o.addComponent(glUp);
		o.addComponent(glUpRight);
		o.addComponent(glRight);
		o.addComponent(glDownRight);
		o.addComponent(glDown);
		o.addComponent(glDownLeft);

		ai.setFormation(allyScript);
		
		if (allyScript == 6)
		{
			o.addComponent(new DestroyOnOutOfBounds());
		}
		else if (allyScript >= 7)
		{
			o.addComponent(new DestroyOnTopOfScreen());
		}
		
		return o;
	}

	public static void setDifficulty(int difficulty)
	{
		PrefabFactory.difficulty = difficulty;
	}
	
	public static GameObject createEnemy(final String name,
			final Vector2 position, final int enemyType, final int scriptType,
			final boolean reverse) {
		// Load the texture for the cube once during Surface creation
		final GenericGraphic graphic = PrefabFactory.createGraphic("enemy",
				PrefabFactory.getImage("enemy"),8);

		final int startFrame = (enemyType - 1) * 8 + 6;
		final int endFrame = startFrame + 1;
		Animation idle = Animation.getNew();
		idle.init("idle", startFrame, endFrame,
				true, PrefabFactory.defaultFps, 32, 32);
		Animation death = Animation.getNew();
		death.init("death", startFrame - 6,
				startFrame-1, false, PrefabFactory.defaultFps, 32, 32);

		final GraphicAnimation glIdle = new GraphicAnimation(graphic, idle);
		final GraphicAnimation glDeath = new GraphicAnimation(graphic, death);
		glIdle.play();

		final RigidBody rigidBody = new RigidBody();
		final Collider collider = new BoxCollider(new Vector2(32, 32));
		collider.setIgnoreTags(PrefabFactory.playerTargets );
		rigidBody.setCollider(collider);

		final GameObject o = GameObject.getNew();
		
		//final GameObject o = new GameObject();
		o.setName(name);
		o.setTags(Tags.enemy);
		o.transform.position.x = position.x;
		o.transform.position.y = position.y;
		o.setRigidBody(rigidBody);

		Component ai = null;
		if (scriptType == 1) {
			ai = new AIScriptOne(reverse);
		} else if (scriptType == 2) {
			ai = new AIScriptTwo(reverse);
		} else if (scriptType == 3) {
			ai = new AIScriptThree(reverse);
		} else if (scriptType == 4) {
			ai = new AIScriptFour(PrefabFactory.enemyTargets);
		} else if (scriptType == 5) {
			ai = new AIScriptFive(PrefabFactory.enemyTargets);
		} else if (scriptType == 6) {
			ai = new AIScriptSix(reverse);
		} else if (scriptType == 7) {
			final GameObject [] nodes = {
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
			};			
			nodes[0] = GameObject.create(PrefabFactory.createNode(new Vector2(0,-32),0.0f));
			nodes[1] = GameObject.create(PrefabFactory.createNode(new Vector2(0,800-32),0.0f));
			nodes[2] = GameObject.create(PrefabFactory.createNode(new Vector2(400,800-32),0.0f));
			nodes[3] = GameObject.create(PrefabFactory.createNode(new Vector2(400,0),0.0f));
			nodes[4] = GameObject.create(PrefabFactory.createNode(new Vector2(0,0),0.0f));
			nodes[5] = GameObject.create(PrefabFactory.createNode(new Vector2(0,900),0.0f));
			ai = new AINodeFollower(nodes);
		} else if (scriptType == 8) {
			final GameObject [] nodes = {
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
			};
			nodes[0] = GameObject.create(PrefabFactory.createNode(new Vector2(400,-32),0.0f));
			nodes[1] = GameObject.create(PrefabFactory.createNode(new Vector2(400,800-32),0.0f));
			nodes[2] = GameObject.create(PrefabFactory.createNode(new Vector2(0,800-32),0.0f));
			nodes[3] = GameObject.create(PrefabFactory.createNode(new Vector2(0,0),0.0f));
			nodes[4] = GameObject.create(PrefabFactory.createNode(new Vector2(400,0),0.0f));
			nodes[5] = GameObject.create(PrefabFactory.createNode(new Vector2(400,900),0.0f));
			ai = new AINodeFollower(nodes);
		} else if (scriptType == 9) {
			final GameObject [] nodes = {
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
			};
			nodes[0] = GameObject.create(PrefabFactory.createNode(new Vector2(0,-32),0.0f));
			nodes[1] = GameObject.create(PrefabFactory.createNode(new Vector2(200,400-32),0.0f));
			nodes[2] = GameObject.create(PrefabFactory.createNode(new Vector2(200,100-32),0.0f));
			nodes[3] = GameObject.create(PrefabFactory.createNode(new Vector2(300,100),0.0f));
			nodes[4] = GameObject.create(PrefabFactory.createNode(new Vector2(300,900),0.0f));
			ai = new AINodeFollower(nodes);
		} else if (scriptType == 10) {
			final GameObject [] nodes = {
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
			};
			nodes[0] = GameObject.create(PrefabFactory.createNode(new Vector2(400,-32),0.0f));
			nodes[1] = GameObject.create(PrefabFactory.createNode(new Vector2(200,400-32),0.0f));
			nodes[2] = GameObject.create(PrefabFactory.createNode(new Vector2(200,100-32),0.0f));
			nodes[3] = GameObject.create(PrefabFactory.createNode(new Vector2(100,100),0.0f));
			nodes[4] = GameObject.create(PrefabFactory.createNode(new Vector2(100,900),0.0f));
			ai = new AINodeFollower(nodes);
		}
		else if (scriptType == 11) {
			ai = new AIScriptEight(reverse);
			//System.out.println("EIGHT");
		} else if (scriptType == 12) {
			//System.out.println("NINE");
			ai = new AIScriptNine(reverse);
		} else if (scriptType == 13) {
			//System.out.println("TEN");
			ai = new AIScriptTen(reverse);
		}
		else {
			ai = new AIScript();
		}

		HealthController hc = new HealthController(new Audio(PrefabFactory.createAudio("explosion"),"explosion"));
		hc.setHealth(100);

		float difficultyOffset = 0;
		if (PrefabFactory.difficulty == PrefabFactory.EASY)
		{
			difficultyOffset = 2f;
		}
		else if (PrefabFactory.difficulty == PrefabFactory.MEDIUM)
		{
			difficultyOffset = 1f;
		}
		
		if (enemyType == 1) {
			final Weapon w = new Phaser(PrefabFactory.shootDown, 3 + difficultyOffset);
			final WeaponHandler wh = new WeaponHandler(w);
			hc.setHealth(100);

			o.addComponent(w);
			o.addComponent(wh);
		}
		if (enemyType == 2) {
			final Weapon w = new Phaser(PrefabFactory.shootDown, 2 + difficultyOffset);
			final WeaponHandler wh = new WeaponHandler(w);
			hc.setHealth(100);

			o.addComponent(w);
			o.addComponent(wh);
		}
		if (enemyType == 3) {
			final Weapon w = new Phaser(PrefabFactory.shootDown, 3 + difficultyOffset);
			hc.setHealth(150);

			o.addComponent(w);

			if (PrefabFactory.difficulty == PrefabFactory.HARD)
			{
				final LockingWeaponHandler wh = new LockingWeaponHandler(w,
					PrefabFactory.enemyTargets,true);
				o.addComponent(wh);
			}
			else
			{
				final WeaponHandler wh = new WeaponHandler(w);
				o.addComponent(wh);
			}
  
			//final GameObject powerup = PrefabFactory.createPowerUp(o.transform.position, 0, true);
			//o.addComponent(new SpawnOnDestroy(powerup));
		}
		if (enemyType == 4) {
			final Weapon w = new Phaser(PrefabFactory.shootDown, 2 + difficultyOffset);
			final LockingWeaponHandler wh = new LockingWeaponHandler(w,
					PrefabFactory.enemyTargets,true);
			hc.setHealth(150);

			o.addComponent(w);
			o.addComponent(wh);
		}
		if (enemyType == 5 || enemyType == 6) {
			hc.setHealth(200);

			if (PrefabFactory.difficulty != PrefabFactory.EASY)
			{
				final Weapon w = new Phaser(PrefabFactory.shootLeft, 3 + difficultyOffset);
				final WeaponHandler wh = new WeaponHandler(w);
				o.addComponent(w);
				o.addComponent(wh);
			}
			
			final Weapon w2 = new Phaser(PrefabFactory.shootDownLeft, 3 + difficultyOffset);
			final WeaponHandler wh2 = new WeaponHandler(w2);
			o.addComponent(w2);
			o.addComponent(wh2);

			if (enemyType == 5) {
				final Weapon w3 = new Phaser(PrefabFactory.shootDown, 3 + difficultyOffset);
				final WeaponHandler wh3 = new WeaponHandler(w3);
				o.addComponent(w3);
				o.addComponent(wh3);
			}
			if (enemyType == 6) {
				final Weapon w3 = new Phaser(PrefabFactory.shootDown, 3 + difficultyOffset);
				o.addComponent(w3);
				
				if (PrefabFactory.difficulty == PrefabFactory.HARD)
				{
					final LockingWeaponHandler wh3 = new LockingWeaponHandler(w3,
						PrefabFactory.enemyTargets,true);
					o.addComponent(wh3);
				}
			}

			final Weapon w4 = new Phaser(PrefabFactory.shootDownRight, 3 + difficultyOffset);
			final WeaponHandler wh4 = new WeaponHandler(w4);
			o.addComponent(w4);
			o.addComponent(wh4);

			if (PrefabFactory.difficulty != PrefabFactory.EASY)
			{
				final Weapon w5 = new Phaser(PrefabFactory.shootRight, 3 + difficultyOffset);
				final WeaponHandler wh5 = new WeaponHandler(w5);
				o.addComponent(w5);
				o.addComponent(wh5);
			}
		}
		if (enemyType == 7 || enemyType == 8) {
			hc.setHealth(250);

			Weapon w3 = null;
			if (enemyType == 7 || PrefabFactory.difficulty != PrefabFactory.HARD) {
				w3 = new Phaser(PrefabFactory.shootDown, 0.3f + difficultyOffset, 8, 5);
			} else {
				w3 = new Phaser(PrefabFactory.shootDown, 0.3f + difficultyOffset, 14, 5);
			}
			o.addComponent(w3);
			
			if ((PrefabFactory.difficulty == PrefabFactory.MEDIUM || PrefabFactory.difficulty == PrefabFactory.HARD) && enemyType == 8)
			{
				final LockingWeaponHandler wh3 = new LockingWeaponHandler(w3,
					PrefabFactory.enemyTargets,true);
				o.addComponent(wh3);
			}
			else if (PrefabFactory.difficulty == PrefabFactory.HARD && enemyType == 7)
			{
				final LockingWeaponHandler wh3 = new LockingWeaponHandler(w3,
					PrefabFactory.enemyTargets,true);
				o.addComponent(wh3);
			}
		}
		if (enemyType == 9 || enemyType == 10) {
			hc.setHealth(250);

			float speed = 4.0f + difficultyOffset;
			if (enemyType == 10) {
				speed = 3.0f + difficultyOffset;
			}
			final Weapon w = new Phaser(PrefabFactory.shootUpLeft, speed);
			final WeaponHandler wh = new WeaponHandler(w);
			o.addComponent(w);
			o.addComponent(wh);

			final Weapon w2 = new Phaser(PrefabFactory.shootUpRight, speed);
			final WeaponHandler wh2 = new WeaponHandler(w2);
			o.addComponent(w2);
			o.addComponent(wh2);

			final Weapon w3 = new Phaser(PrefabFactory.shootDownLeft, speed);
			final WeaponHandler wh3 = new WeaponHandler(w3);
			o.addComponent(w3);
			o.addComponent(wh3);

			final Weapon w4 = new Phaser(PrefabFactory.shootDownRight, speed);
			final WeaponHandler wh4 = new WeaponHandler(w4);
			o.addComponent(w4);
			o.addComponent(wh4);
			
			final Weapon w5 = new Phaser(PrefabFactory.shootDown, 3 + difficultyOffset);
			o.addComponent(w5);
			
			if (PrefabFactory.difficulty == PrefabFactory.HARD)
			{
				final LockingWeaponHandler wh5 = new LockingWeaponHandler(w5,
					PrefabFactory.enemyTargets,true);
				o.addComponent(wh5);
			}
			else
			{
				final WeaponHandler wh5 = new WeaponHandler(w5);
				o.addComponent(wh5);
			}
		}
		if (enemyType == 11 || enemyType == 12) {
			hc.setHealth(100);

			Weapon w = null;
			if (enemyType == 11) {				
				w = new Phaser(PrefabFactory.shootDown, 3 + difficultyOffset);
			}
			if (enemyType == 12) {
				w = new Phaser(PrefabFactory.shootDown, 2 + difficultyOffset);
			}
			final LockingWeaponHandler wh = new LockingWeaponHandler(w,
					PrefabFactory.enemyTargets,true);

			o.addComponent(w);
			o.addComponent(wh);
		}
		if (enemyType == 13) {
			final Weapon w = new Phaser(PrefabFactory.shootDown, 3 + difficultyOffset);
			final LockingWeaponHandler wh = new LockingWeaponHandler(w,
					PrefabFactory.enemyTargets,true);

			o.addComponent(w);
			
			if (PrefabFactory.difficulty == PrefabFactory.HARD)
			{
				o.addComponent(wh);
			}
			else
			{
				final WeaponHandler wh2 = new WeaponHandler(w);
				o.addComponent(wh2);
			}

			hc.setHealth(50);
		}

		if (Util.randomNumber(0, 3) == 1)
		{
			final GameObject explosion1 = PrefabFactory.createExplosion(o.transform.position);
			o.addComponent(new SpawnOnDestroy(explosion1));

			final GameObject explosion2 = PrefabFactory.createExplosion(o.transform.position);
			o.addComponent(new SpawnOnDestroy(explosion2));

			final GameObject explosion3 = PrefabFactory.createExplosion(o.transform.position);
			o.addComponent(new SpawnOnDestroy(explosion3));
		}

		FaceOppositeSpeedDirection fosd = new FaceOppositeSpeedDirection(rigidBody);
		o.addComponent(fosd);
		
		o.addComponent(new DestroyOnOutOfBounds());
		o.addComponent(glIdle);
		o.addComponent(glDeath);
		o.addComponent(ai);
		o.addComponent(hc);

		return o;
	}

	private static GameObject createExplosion(final Vector2 position) {
		final GameObject o = GameObject.getNew();
		//GameObject o = new GameObject();
		o.setName("Explosion");

		final GenericGraphic graphic = PrefabFactory.createGraphic("explosion", PrefabFactory.getImage("explosion"),1);
		Animation explosion = Animation.getNew();
		explosion.init("explode", 0, 8, false,
				PrefabFactory.defaultFps, 47, 42);

		final GraphicAnimation glLeft = new GraphicAnimation(graphic, explosion);
		o.addComponent(glLeft);

		final RigidBody r = new RigidBody();

		SimpleMovement sm = SimpleMovement.getNew();
		sm.init(r,Util.randomNumber(0,64)-32,Util.randomNumber(0, 64)-32);
		
		final SetPositionOnCreate spoc = new SetPositionOnCreate(position);

		o.addComponent(r);
		o.addComponent(sm);
		o.addComponent(spoc);
		o.addComponent(new DestroyOnCreate(1));

		o.playAnimation("explode");
		return o;
	}

	public static GenericGraphic createGraphic(final String name,
			final InputStream is, final int layer) { 
		final CanvasGraphic temp = new CanvasGraphic();
		temp.create(name, is, layer);
		return temp;
	}

	public static GameObject createLevel(final String name, final int[] map, final int mapWidth,
			final int mapHeight, final String wadName) {
		
		//System.out.println("WADNAME:"+wadName);
		final GenericGraphic graphic = PrefabFactory.createGraphic(wadName, PrefabFactory.getImage(wadName),0);
		final Level l = new Level();
		//System.out.println("WADNAME2:"+graphic.getName());
		//System.out.println("SIZE:"+graphic.getHeight());

		l.createLevel(map, mapWidth, mapHeight, graphic, 64, 64);

		final GameObject o = GameObject.getNew();
		//GameObject o = new GameObject();		
		o.setName(name);
		o.setTags(Tags.level);
		o.addComponent(l);
		o.transform.position.x = 0;
		o.transform.position.y = 0;
		l.alignBottom();
		
		Audio audio = new Audio(PrefabFactory.createAudio("music_level"+Util.randomNumber(1, 5)),"music");
		o.addComponent(audio);
		audio.play();		

		final RigidBody rigidBody = new RigidBody();
		o.setRigidBody(rigidBody);

		return o;
	}
	
	private static MediaPlayer createAudio(String audioName) {
		
		if (audioName.equalsIgnoreCase("music_level1"))
		{
			return MediaPlayer.create(context, R.raw.music_level1);
		}
		if (audioName.equalsIgnoreCase("music_level2"))
		{
			return MediaPlayer.create(context, R.raw.music_level2);
		}
		if (audioName.equalsIgnoreCase("music_level3"))
		{
			return MediaPlayer.create(context, R.raw.music_level3);
		}
		if (audioName.equalsIgnoreCase("music_level4"))
		{
			return MediaPlayer.create(context, R.raw.music_level4);
		}
		if (audioName.equalsIgnoreCase("music_level5"))
		{
			return MediaPlayer.create(context, R.raw.music_level5);
		}

		if (audioName.equalsIgnoreCase("flamethrower"))
		{
			return MediaPlayer.create(context, R.raw.flamethrower);
		}
		if (audioName.equalsIgnoreCase("laser"))
		{
			return MediaPlayer.create(context, R.raw.laser);
		}
		if (audioName.equalsIgnoreCase("machinegun"))
		{
			return MediaPlayer.create(context, R.raw.machinegun);
		}
		if (audioName.equalsIgnoreCase("missilelauncher"))
		{
			return MediaPlayer.create(context, R.raw.missilelauncher);
		}
		if (audioName.equalsIgnoreCase("chargelaser"))
		{
			return MediaPlayer.create(context, R.raw.chargelaser);
		}
		if (audioName.equalsIgnoreCase("pulselaser"))
		{
			return MediaPlayer.create(context, R.raw.pulselaser);
		}
		if (audioName.equalsIgnoreCase("explosion"))
		{
			return MediaPlayer.create(context, R.raw.explosion);
		}
		
		//System.out.println(audioName);
		
		return null;
	}

	public static GameObject createNode(final Vector2 position, final float time)
	{
		final GameObject g = GameObject.getNew();
		//GameObject g = new GameObject();

		final RigidBody rigidBody = new RigidBody();
		final Collider collider = new BoxCollider(new Vector2(32, 32));
		collider.setIgnoreTags(Tags.player | Tags.ally | Tags.shot | Tags.spawner);
		g.setTags(PrefabFactory.nodeTag);

		rigidBody.setCollider(collider);

		g.setRigidBody(rigidBody);

		g.setName("Node");
		g.addComponent(new Node());
		g.transform.position.x = position.x;
		g.transform.position.y = position.y;
		return g;
	}
	
	public static GameObject createCamera(final String name, final Vector2 position)
	{
		final GameObject o = GameObject.getNew();
		o.setName(name);
		o.setTags(Tags.player | Tags.ally | Tags.camera);
		
		final RigidBody rigidBody = new RigidBody();
		o.setRigidBody(rigidBody);
		
		//this is the camera we will be using
		Camera c = new Camera();
		Camera.setMainCamera(c);
		o.addComponent(c);
				
		PlayerInput.setCameraScrollSpeed(LevelLoader.scrollSpeed);
		SimpleMovement sm = SimpleMovement.getNew();
		sm.init(rigidBody, 0, LevelLoader.scrollSpeed);
		o.addComponent(sm);
		
		//PositionEchoer pe = new PositionEchoer();
		//o.addComponent(pe);

		float moveIfLessThan = 80;
		float moveIfGreaterThan = 380;
		float maxRightMove = 128;
		float maxLeftMove = 0;
				
		PlayerFollower pf = new PlayerFollower(GameObject.findByName("player"), sm, maxLeftMove, maxRightMove, moveIfLessThan, moveIfGreaterThan, LevelLoader.scrollSpeed);
		o.addComponent(pf);
		
		GameObject go2 = GameObject.findByName("TopOfScreen");
		
		o.transform.position.x = position.x;
		o.transform.position.y = position.y;
		go2.transform.position.x = position.x;
		go2.transform.position.y = position.y;
		
		FixedJoint f = new FixedJoint(Camera.mainCamera.gameObject);
		go2.addComponent(f);
		
		
		return o;
	}


	public static GameObject createMenuCamera(final String name, final Vector2 position)
	{
		final GameObject o = GameObject.getNew();
		o.setName(name);
		o.setTags(Tags.player | Tags.ally | Tags.camera);

		final RigidBody rigidBody = new RigidBody();
		o.setRigidBody(rigidBody);

		//this is the camera we will be using
		Camera c = new Camera();
		Camera.setMainCamera(c);

		Audio audio = new Audio(PrefabFactory.createAudio("music_level1"),"music");
		o.addComponent(audio);
		audio.play();

		o.addComponent(c);
		o.addComponent(audio);

		return o;
	}

	public static GameObject createPlayer(final String name,
			final Vector2 position, final String playerType) {
		final GameObject o = GameObject.getNew();
		//final GameObject o = new GameObject();
		o.setName(name);
		o.setTags(Tags.player | Tags.ally);

		// Load the texture for the cube once during Surface creation
		final GenericGraphic graphic = PrefabFactory.createGraphic(playerType,
				PrefabFactory.getImage(playerType),10);
		Animation idle = Animation.getNew();
		idle.init("idle", 0, 5, true,
				PrefabFactory.defaultFps, 32, 32);
		idle.setFrameIncrement(2);
		
		Animation death = Animation.getNew();
		death.init("death", 0, 1, true,
				PrefabFactory.defaultFps, 32, 32);

		final GraphicAnimation glIdle = new GraphicAnimation(graphic, idle);
		final GraphicAnimation glDeath = new GraphicAnimation(graphic, death);
		glIdle.play();
		o.addComponent(glIdle);
		o.addComponent(glDeath);

		Animation left = Animation.getNew();
		left.init("left", 3, 3, true,
				PrefabFactory.defaultFps, 32, 32);
		Animation upleft = Animation.getNew();
		upleft.init("upleft", 8, 8, true,
				PrefabFactory.defaultFps, 32, 32);
		Animation up = Animation.getNew();
		up.init("up", 0, 0, true,
				PrefabFactory.defaultFps, 32, 32);
		Animation upright = Animation.getNew();
		upright.init("upright", 6, 6, true,
				PrefabFactory.defaultFps, 32, 32);
		Animation right = Animation.getNew();
		right.init("right", 1, 1, true,
				PrefabFactory.defaultFps, 32, 32);
		Animation downright = Animation.getNew();
		downright.init("downright", 5, 5, true,
				PrefabFactory.defaultFps, 32, 32);
		Animation down = Animation.getNew();
		down.init("down", 2, 2, true,
				PrefabFactory.defaultFps, 32, 32);
		Animation downleft = Animation.getNew();
		downleft.init("downleft", 7, 7, true,
				PrefabFactory.defaultFps, 32, 32);

		final GraphicAnimation glLeft = new GraphicAnimation(graphic, left);
		final GraphicAnimation glUpLeft = new GraphicAnimation(graphic, upleft);
		final GraphicAnimation glUp = new GraphicAnimation(graphic, up);
		final GraphicAnimation glUpRight = new GraphicAnimation(graphic, upright);
		final GraphicAnimation glRight = new GraphicAnimation(graphic, right);
		final GraphicAnimation glDownRight = new GraphicAnimation(graphic, downright);
		final GraphicAnimation glDown = new GraphicAnimation(graphic, down);
		final GraphicAnimation glDownLeft = new GraphicAnimation(graphic, downleft);

		o.addComponent(glLeft);
		o.addComponent(glUpLeft);
		o.addComponent(glUp);
		o.addComponent(glUpRight);
		o.addComponent(glRight);
		o.addComponent(glDownRight);
		o.addComponent(glDown);
		o.addComponent(glDownLeft);
		
		final RigidBody rigidBody = new RigidBody();
		o.setRigidBody(rigidBody);
		
		final BoxCollider collider = new BoxCollider(new Vector2(32, 32));
		collider.setIgnoreTags(Tags.player | Tags.ally | Tags.topOfScreen);
		rigidBody.setCollider(collider);

		final HealthController hc = new HealthController(new Audio(PrefabFactory.createAudio("explosion"),"explosion"));;
		hc.setHealth(101);
		if (com.spacecombat.Engine.isInvincible())
		{
			hc.setHealth(100000);
		}
		o.addComponent(hc);


		final Weapon [] weapons = new Weapon[4];
		weapons[0] = new Laser(PrefabFactory.shootUp);
		weapons[0].setUseMagazine(false);
		weapons[0].addAudio(new Audio(PrefabFactory.createAudio("laser"),"laser"));
		o.addComponent(weapons[0]);
		

		weapons[1] = new MachineGun(PrefabFactory.shootUp);
		weapons[1].setUseMagazine(false);
		weapons[1].addAudio(new Audio(PrefabFactory.createAudio("machinegun"),"machinegun"));
		o.addComponent(weapons[1]);

		weapons[2] = new FlameThrower(PrefabFactory.shootUp);
		weapons[2].setUseMagazine(false);
		weapons[2].addAudio(new Audio(PrefabFactory.createAudio("flamethrower"),"flamethrower"));
		o.addComponent(weapons[2]);

		weapons[3] = new PulseCannon(PrefabFactory.shootUp);
		weapons[3].setUseMagazine(false);
		weapons[3].addAudio(new Audio(PrefabFactory.createAudio("pulselaser"),"pulselaser"));
		o.addComponent(weapons[3]);
		
		
		Weapon [] secondaryWeapons = new Weapon[2];				
		
		secondaryWeapons[0] = new LockingLaser(PrefabFactory.shootUp);
		secondaryWeapons[0].setPowerLevel(0);
		o.addComponent(secondaryWeapons[0]);
		secondaryWeapons[0].addAudio(new Audio(PrefabFactory.createAudio("machinegun"),"machinegun"));
		final LockingWeaponHandler lwh = new LockingWeaponHandler(secondaryWeapons[0],PrefabFactory.playerTargets,false);				
		o.addComponent(lwh);				
		
		
		//Weapon [] terciaryWeapons = new Weapon[1];
		
		secondaryWeapons[1] = new MissileLauncher(PrefabFactory.shootUp);
		secondaryWeapons[1].setUseMagazine(false);
		secondaryWeapons[1].setPowerLevel(0);
		secondaryWeapons[1].addAudio(new Audio(PrefabFactory.createAudio("missilelauncher"),"machinegun"));
		o.addComponent(secondaryWeapons[1]);
		
		
		Weapon [] chargeWeapons = new Weapon[1];
		chargeWeapons[0] = new ChargeLaser(PrefabFactory.shootUp);
		o.addComponent(chargeWeapons[0]);		
		chargeWeapons[0].addAudio(new Audio(PrefabFactory.createAudio("chargelaser"),"chargelaser"));
		
		//RotateEachSecond res = new RotateEachSecond(45);
		//o.addComponent(res);
		
		final WeaponController [] wc = new WeaponController[3];
		wc[0] = new WeaponController(0,weapons);
		wc[1] = new WeaponController(0,secondaryWeapons);
		wc[2] = new WeaponController(0,chargeWeapons);
		
		PowerupController pc = new PowerupController(wc);
		o.addComponent(pc);
		o.addComponent(wc[0]);
		o.addComponent(wc[1]);
		o.addComponent(wc[2]);

		PlayerInput playerInput = new PlayerInput(wc,collider);
		o.addComponent(playerInput);

		o.transform.position.x = position.x;
		o.transform.position.y = position.y;

		GameObject.create(PrefabFactory.createHUDHealthShield(o,hc));	
		o.addComponent(new AIPlayerStartLevel(playerInput,hc));
		
		
		final GameObject explosion1 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion1));

		final GameObject explosion2 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion2));

		final GameObject explosion3 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion3));
		
		return o;
	}
	
	public static GameObject createHUDChargeBar(GameObject camera, GameObject player)
	{
		final GenericGraphic graphic = PrefabFactory.createGraphic("chargebar",
				PrefabFactory.getImage("chargebar"),11);
		
		GameObject go = GameObject.getNew();
		go.setTags(Tags.hud | Tags.player | Tags.ally);
		go.setDestroyOnLevelLoad(false);
		
		ChargeLaser cl = (ChargeLaser)player.getComponent(ChargeLaser.class);
		
		ChargeLaserHUD clh = new ChargeLaserHUD(cl);
		go.addComponent(clh);
		
		go.transform.position.x = camera.transform.position.x + HUDConstants.chargeBar.x;
		go.transform.position.y = camera.transform.position.y + HUDConstants.chargeBar.y;
		
		int sizeX = 26;
		int sizeY = 52;

		Animation gl100 = new Animation();
		gl100.init("0%", 10, 10, false,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl90 = new Animation();
		gl90.init("10%", 9, 9, false,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl80 = new Animation();
		gl80.init("20%", 8, 8, false,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl70 = new Animation();
		gl70.init("30%", 7, 7, false,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl60 = new Animation();
		gl60.init("40%", 6, 6, false,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl50 = new Animation();
		gl50.init("50%", 5, 5, false,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl40 = new Animation();
		gl40.init("60%", 4, 4, false,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl30 = new Animation();
		gl30.init("70%", 3, 3, false,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl20 = new Animation();
		gl20.init("80%",2, 2, false,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl10 = new Animation();
		gl10.init("90%", 1, 1, true,
				PrefabFactory.defaultFps, sizeX, sizeY);
		
		Animation gl0 = new Animation();
		gl0.init("100%", 0, 0, false,
				PrefabFactory.defaultFps, sizeX, sizeY);	

		final GraphicAnimation ga100 = new GraphicAnimation(graphic, gl100);
		final GraphicAnimation ga90 = new GraphicAnimation(graphic, gl90);
		final GraphicAnimation ga80 = new GraphicAnimation(graphic, gl80);
		final GraphicAnimation ga70 = new GraphicAnimation(graphic, gl70);
		final GraphicAnimation ga60 = new GraphicAnimation(graphic, gl60);
		final GraphicAnimation ga50 = new GraphicAnimation(graphic, gl50);
		final GraphicAnimation ga40 = new GraphicAnimation(graphic, gl40);
		final GraphicAnimation ga30 = new GraphicAnimation(graphic, gl30);
		final GraphicAnimation ga20 = new GraphicAnimation(graphic, gl20);
		final GraphicAnimation ga10 = new GraphicAnimation(graphic, gl10);
		final GraphicAnimation ga0 = new GraphicAnimation(graphic, gl0);

		go.addComponent(ga100);
		go.addComponent(ga90);
		go.addComponent(ga80);
		go.addComponent(ga70);
		go.addComponent(ga60);
		go.addComponent(ga50);
		go.addComponent(ga40);
		go.addComponent(ga30);
		go.addComponent(ga20);
		go.addComponent(ga10);
		go.addComponent(ga0);
		
		FixedJoint f = new FixedJoint(camera);
		go.addComponent(f);
		return go;
	}
	
	public static GameObject createMainMenu ()
	{				
		GameObject go = GameObject.getNew();				
		MainMenuHUD mmhud = new MainMenuHUD();
		go.addComponent(mmhud);
		
		Camera c = new Camera();
		Camera.setMainCamera(c);
		go.addComponent(c);
		
		return go;
	}
	
	public static GameObject createHUDHealthShield(GameObject g, HealthController hc)
	{
		final GenericGraphic graphic = PrefabFactory.createGraphic("shield",
				PrefabFactory.getImage("shield"),11);
		
		GameObject go = GameObject.getNew();
		go.setTags(Tags.hud | Tags.player | Tags.ally);
		go.setDestroyOnLevelLoad(false);
		
		go.transform.position.x = g.transform.position.x + 5;
		go.transform.position.y = g.transform.position.y + 5;
		
		RigidBody r = new RigidBody();		
		final BoxCollider collider = new BoxCollider(new Vector2(32, 32));		
		collider.setIgnoreTags(Tags.player | Tags.ally | Tags.topOfScreen);
		r.setCollider(collider);
		go.setRigidBody(r);
		
		Animation gl100 = new Animation();
		gl100.init("100%", 0, 1, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl90 = new Animation();
		gl90.init("90%", 1, 2, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl80 = new Animation();
		gl80.init("80%", 2, 3, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl70 = new Animation();
		gl70.init("70%", 3, 4, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl60 = new Animation();
		gl60.init("60%", 4, 5, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl50 = new Animation();
		gl50.init("50%", 5, 6, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl40 = new Animation();
		gl40.init("40%", 6, 7, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl30 = new Animation();
		gl30.init("30%", 7, 8, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl20 = new Animation();
		gl20.init("20%",8, 9, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl10 = new Animation();
		gl10.init("10%", 9, 10, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl0 = new Animation();
		gl0.init("0%", 10, 10, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation glRestored = new Animation();
		gl0.init("Restored", 10, 10, false,
				PrefabFactory.defaultFps, 32, 32);

		final GraphicAnimation ga100 = new GraphicAnimation(graphic, gl100);
		final GraphicAnimation ga90 = new GraphicAnimation(graphic, gl90);
		final GraphicAnimation ga80 = new GraphicAnimation(graphic, gl80);
		final GraphicAnimation ga70 = new GraphicAnimation(graphic, gl70);
		final GraphicAnimation ga60 = new GraphicAnimation(graphic, gl60);
		final GraphicAnimation ga50 = new GraphicAnimation(graphic, gl50);
		final GraphicAnimation ga40 = new GraphicAnimation(graphic, gl40);
		final GraphicAnimation ga30 = new GraphicAnimation(graphic, gl30);
		final GraphicAnimation ga20 = new GraphicAnimation(graphic, gl20);
		final GraphicAnimation ga10 = new GraphicAnimation(graphic, gl10);
		final GraphicAnimation ga0 = new GraphicAnimation(graphic, gl0);
		final GraphicAnimation gaRestored = new GraphicAnimation(graphic, glRestored);

		go.addComponent(ga100);
		go.addComponent(ga90);
		go.addComponent(ga80);
		go.addComponent(ga70);
		go.addComponent(ga60);
		go.addComponent(ga50);
		go.addComponent(ga40);
		go.addComponent(ga30);
		go.addComponent(ga20);
		go.addComponent(ga10);
		go.addComponent(ga0);
		go.addComponent(gaRestored);
		
		FixedJoint f = new FixedJoint(g);
		go.addComponent(f);

		HealthHUD hud = new HealthHUD(hc);
		go.addComponent(hud);
		
		return go;
	}
	
	public static GameObject createHUDHealthBar(GameObject g, HealthController hc)
	{
		final GenericGraphic graphic = PrefabFactory.createGraphic("shieldbar",
				PrefabFactory.getImage("shieldbar"),11);
		
		GameObject go = GameObject.getNew();
		go.setTags(Tags.hud | Tags.player | Tags.ally);
		go.setDestroyOnLevelLoad(false);
		
		go.transform.position.x = g.transform.position.x + HUDConstants.shieldBar.x;
		go.transform.position.y = g.transform.position.y + HUDConstants.shieldBar.y;
		
		RigidBody r = new RigidBody();
		
		/* 
		final BoxCollider collider = new BoxCollider(new Vector2(32, 32));		
		collider.setIgnoreTags(Tags.player | Tags.ally | Tags.topOfScreen | Tags.enemy);
		r.setCollider(collider);ll
		*/
		
		go.setRigidBody(r);
		
		Animation gl100 = new Animation();
		gl100.init("0%", 0, 0, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl90 = new Animation();
		gl90.init("10%", 1, 1, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl80 = new Animation();
		gl80.init("20%", 2, 2, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl70 = new Animation();
		gl70.init("30%", 3, 3, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl60 = new Animation();
		gl60.init("40%", 4, 4, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl50 = new Animation();
		gl50.init("50%", 5, 5, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl40 = new Animation();
		gl40.init("60%", 6, 6, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl30 = new Animation();
		gl30.init("70%", 7, 7, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl20 = new Animation();
		gl20.init("80%",8, 8, false,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl10 = new Animation();
		gl10.init("90%", 9, 9, true,
				PrefabFactory.defaultFps, 32, 32);
		
		Animation gl0 = new Animation();
		gl0.init("100%", 10, 10, false,
				PrefabFactory.defaultFps, 32, 32);		

		final GraphicAnimation ga100 = new GraphicAnimation(graphic, gl100);
		final GraphicAnimation ga90 = new GraphicAnimation(graphic, gl90);
		final GraphicAnimation ga80 = new GraphicAnimation(graphic, gl80);
		final GraphicAnimation ga70 = new GraphicAnimation(graphic, gl70);
		final GraphicAnimation ga60 = new GraphicAnimation(graphic, gl60);
		final GraphicAnimation ga50 = new GraphicAnimation(graphic, gl50);
		final GraphicAnimation ga40 = new GraphicAnimation(graphic, gl40);
		final GraphicAnimation ga30 = new GraphicAnimation(graphic, gl30);
		final GraphicAnimation ga20 = new GraphicAnimation(graphic, gl20);
		final GraphicAnimation ga10 = new GraphicAnimation(graphic, gl10);
		final GraphicAnimation ga0 = new GraphicAnimation(graphic, gl0);

		go.addComponent(ga100);
		go.addComponent(ga90);
		go.addComponent(ga80);
		go.addComponent(ga70);
		go.addComponent(ga60);
		go.addComponent(ga50);
		go.addComponent(ga40);
		go.addComponent(ga30);
		go.addComponent(ga20);
		go.addComponent(ga10);
		go.addComponent(ga0);
		
		FixedJoint f = new FixedJoint(g);
		go.addComponent(f);

		HealthBarHUD hud = new HealthBarHUD(hc);
		go.addComponent(hud);
		
		return go;
	}

	public static GameObject createPowerUp (final Vector2 position, int type, final boolean canChange) {
		// Load the texture for the cube once during Surface creation

		final GameObject o = GameObject.getNew();
		//GameObject o = new GameObject();
		o.setName("PowerUp");
		o.setTags(Tags.powerup);
		final GenericGraphic graphic = PrefabFactory.createGraphic("powerup", PrefabFactory.getImage("powerup"),9);

		Animation animation = Animation.getNew();
		animation.init("Laser", 0, 0, true, PrefabFactory.defaultFps,	32, 32);
		Animation animation2 = Animation.getNew();
		animation2.init("MachineGun", 1, 1, true, PrefabFactory.defaultFps,	32, 32);
		Animation animation3 = Animation.getNew();
		animation3.init("FlameThrower", 2, 2, true, PrefabFactory.defaultFps,	32, 32);
		Animation animation4 = Animation.getNew();
		animation4.init("PulseCannon", 3, 3, true, PrefabFactory.defaultFps,	32, 32);
		Animation animation5 = Animation.getNew();
		
		Animation animation9 = Animation.getNew();
		animation9.init("Ally", 5, 5, true, PrefabFactory.defaultFps,	32, 32);
		Animation animation10 = Animation.getNew();
		animation10.init("Support", 8, 8, true, PrefabFactory.defaultFps,	32, 32);
		Animation animation11 = Animation.getNew();
		animation11.init("GunShip", 10, 10, true, PrefabFactory.defaultFps,	32, 32);
		
		animation5.init("LockingLaser", 6, 6, true, PrefabFactory.defaultFps,	32, 32);
		Animation animation6 = Animation.getNew();
		animation6.init("MissileLauncher", 7, 7, true, PrefabFactory.defaultFps,	32, 32);
		
		Animation animation7 = Animation.getNew();
		animation7.init("ChargeLaser", 9, 9, true, PrefabFactory.defaultFps,	32, 32);
		
		Animation animation8 = Animation.getNew();
		animation8.init("Health", 4, 4, true, PrefabFactory.defaultFps,	32, 32);

		final GraphicAnimation glIdle = new GraphicAnimation(graphic, animation);
		final GraphicAnimation glIdle2 = new GraphicAnimation(graphic, animation2);
		final GraphicAnimation glIdle3 = new GraphicAnimation(graphic, animation3);
		final GraphicAnimation glIdle4 = new GraphicAnimation(graphic, animation4);
		final GraphicAnimation glIdle5 = new GraphicAnimation(graphic, animation5);
		final GraphicAnimation glIdle6 = new GraphicAnimation(graphic, animation6);
		final GraphicAnimation glIdle7 = new GraphicAnimation(graphic, animation7);
		final GraphicAnimation glIdle8 = new GraphicAnimation(graphic, animation8);
		final GraphicAnimation glIdle9 = new GraphicAnimation(graphic, animation9);
		final GraphicAnimation glIdle10 = new GraphicAnimation(graphic, animation10);
		final GraphicAnimation glIdle11 = new GraphicAnimation(graphic, animation11);

		o.addComponent(glIdle);
		o.addComponent(glIdle2);
		o.addComponent(glIdle3);
		o.addComponent(glIdle4);
		o.addComponent(glIdle5);
		o.addComponent(glIdle6);
		o.addComponent(glIdle7);
		o.addComponent(glIdle8);
		o.addComponent(glIdle9);
		o.addComponent(glIdle10);
		o.addComponent(glIdle11);

		glIdle2.setEnabled(false);
		glIdle3.setEnabled(false);
		glIdle4.setEnabled(false);
		glIdle5.setEnabled(false);
		glIdle6.setEnabled(false);
		glIdle7.setEnabled(false);
		glIdle8.setEnabled(false);
		glIdle9.setEnabled(false);
		glIdle10.setEnabled(false);
		glIdle11.setEnabled(false);
		glIdle.play();

		final BoxCollider collider = new BoxCollider(new Vector2(32, 32));
		collider.setIgnoreTags(Tags.powerup);

		final RigidBody rigidBody = new RigidBody();
		rigidBody.setCollider(collider);
		o.setRigidBody(rigidBody);

		SimpleMovement sm = SimpleMovement.getNew();
		sm.init(rigidBody, 0, 32);
		
		sm.setSpeed(0,32);
		o.addComponent(sm);

		final SetPositionOnCreate spoc = new SetPositionOnCreate(position);
		//System.out.println("TYPE:"+type);
		PowerUp p = new PowerUp(sm, type, canChange);
		o.addComponent(p);
		p.setType(type);
		o.addComponent(spoc);
		o.addComponent(new DestroyOnOutOfBounds());

		return o;
	}
	public static GameObject createRandomLevel ()
	{
		final GameObject o = GameObject.getNew();
		//GameObject o = new GameObject();
		o.addComponent(new RandomSpawner());
		return o;
	}

	private final static Vector2 v32by32 = new Vector2(32, 32);
	
	public static GameObject createShot(final String name,
			final Vector2 position, final Vector2 speed, int tags,
			final float damage, final int power, final float life) {
		// Load the texture for the cube once during Surface creation

		final GenericGraphic graphic = PrefabFactory.createGraphic(name,
				PrefabFactory.getImage(name),9);

		Animation animation = Animation.getNew();
		Collider collider = null;


		if (name.equals("phaser")) {
			animation.init("idle", 0, 6, true, PrefabFactory.defaultFps, 32, 32);;
		}
		else if (name.equals("pulse") || name.equals("hell"))
		{
			if (power < 0)		
			{
				animation.init("idle", 0, 4, false,PrefabFactory.defaultFps, 32, 32);
			}
			else
			{
				animation.init("idle", power, 4, false,PrefabFactory.defaultFps, 32, 32);
			}
		}
		else if (name.equals("flame"))
		{
			if (power < 0)		
			{
				animation.init("idle", 0, 8, false,PrefabFactory.defaultFps/8, 32, 32);
			}
			else
			{
				animation.init("idle", power, 8, false,PrefabFactory.defaultFps/8, 32, 32);
			}
		}
		else if (name.equals("missile"))
		{
			animation.init("idle", 0, 2, true, PrefabFactory.defaultFps,32, 32);
		}
		else if (name.equals("chargeLaser"))
		{
			animation.init("idle", 0, 2, true, PrefabFactory.defaultFps,32, 32);
		}
		else {
			if (power <= 9)
			{
				animation.init("idle", power, power, true, PrefabFactory.defaultFps,32, 32);
			}
			else
			{
				animation.init("idle", 9, 9, true, PrefabFactory.defaultFps,32, 32);
			}
		}

		collider = new BoxCollider(v32by32);
		collider.setIgnoreTags(tags);

		final GraphicAnimation glIdle = new GraphicAnimation(graphic, animation);
		glIdle.play();

		final ShotCollision sc = new ShotCollision();
		sc.setDamage(damage);

		final RigidBody rigidBody = new RigidBody();
		rigidBody.setCollider(collider);

		final GameObject o = GameObject.getNew();
		
		int newTags = tags | Tags.shot;
		o.setName(name);
		o.setTags(newTags);
		o.transform.position.x = position.x;
		o.transform.position.y = position.y;
		o.setRigidBody(rigidBody);
		o.addComponent(sc);
		
		if (!name.equals("missile"))
		{
			SimpleMovement basicShotMovement = SimpleMovement.getNew();
			basicShotMovement.init(rigidBody, speed.x, speed.y);
			o.addComponent(basicShotMovement);
		}
		else
		{
			HomingMovement basicShotMovement = new HomingMovement(PrefabFactory.playerTargets, rigidBody, speed.y);
			o.addComponent(basicShotMovement);			
		}

		if (name.equals("missile") || name.equals("lockingLaser"))
		{
			FaceSpeedDirection fsd = new FaceSpeedDirection(rigidBody);
			o.addComponent(fsd);
		}
		
		o.addComponent(glIdle);		

		o.addComponent(new DestroyOnOutOfBounds());
		o.destroyAfter(life);

		return o;
	}

	
	public static GameObject createHUD (GameObject camera, GameObject player)
	{
		GameObject go = GameObject.getNew();
		go.setTags(Tags.hud | Tags.player | Tags.ally);
		RigidBody rigidBody = new RigidBody();
		go.setRigidBody(rigidBody);
						
		HealthController c = (HealthController)player.getComponent(HealthController.class);
		ScoreHUD sh = new ScoreHUD(c);
		go.addComponent(sh);
		
		GenericGraphic graphic = PrefabFactory.createGraphic("hud1", PrefabFactory.getImage("hud1"),11);
		
		Animation idle = new Animation();
		idle.init("idle", 0, 0, false, PrefabFactory.defaultFps, 98, 32);
		
		final GraphicAnimation ga100 = new GraphicAnimation(graphic, idle);
		
		
		FixedJoint fj = new FixedJoint(camera);
		go.addComponent(fj);
		go.addComponent(ga100);
		go.playAnimation("idle");

		go.transform.position.x = camera.transform.position.x + HUDConstants.hud.x;
		go.transform.position.y = camera.transform.position.y + HUDConstants.hud.y;
		
		return go;		
	}
	
	public static GameObject createSpawner (final float spawnX, final float spawnY, final GameObject objectToCreate)
	{
		final GameObject spawner = GameObject.getNew();
		//GameObject spawner = new GameObject();
		final RigidBody rigidBody = new RigidBody();
		final Collider c = new BoxCollider(new Vector2(800,32));
		rigidBody.setCollider(c);

		spawner.setTags(Tags.spawner);
		c.setIgnoreTags(Tags.spawner | Tags.shot | Tags.player | Tags.enemy | Tags.powerup);

		spawner.setRigidBody(rigidBody);
		spawner.addComponent(new SpawnOnCollision(objectToCreate));

		/*
		SimpleMovement s = SimpleMovement.getNew(); 
		s.init(rigidBody, 0, 10.0f);
		spawner.addComponent(s);
		 */
		
		PrefabFactory.spawner++;
		spawner.setName("spawner " + PrefabFactory.spawner + " " + objectToCreate.getName());

		spawner.transform.position.x = spawnX;
		spawner.transform.position.y = spawnY;

		return spawner;
	}

	public static GameObject createDifficultyButton (String difficulty, int x, int y)
	{
		GameObject go = GameObject.getNew();
		go.setTags(Tags.hud);
		RigidBody rigidBody = new RigidBody();

		rigidBody.setCollider(new BoxCollider(new Vector2(100,32)));
		go.setRigidBody(rigidBody);

		GenericGraphic graphic = PrefabFactory.createGraphic("diff"+difficulty, PrefabFactory.getImage("difficulty"),32);

		DifficultyButton dc = null;
		Animation idle = new Animation();

		if (difficulty.equals("easy")) {
			idle.init("idle", 0, 0, false, PrefabFactory.defaultFps, 98, 32);
			dc = new DifficultyButton(DifficultyButton.EASY);
		}
		else if (difficulty.equals("normal") || difficulty.equals("medium")) {
			idle.init("idle", 1, 1, false, PrefabFactory.defaultFps, 98, 32);
			dc = new DifficultyButton(DifficultyButton.MEDIUM);
		}
		else if (difficulty.equals("hard")) {
			idle.init("idle", 2, 2, false, PrefabFactory.defaultFps, 98, 32);
			dc = new DifficultyButton(DifficultyButton.HARD);
		}
		else {
			throw new RuntimeException("Difficulty Not Found:" + difficulty);
		}

		final GraphicAnimation ga100 = new GraphicAnimation(graphic, idle);

		go.addComponent(ga100);
		go.playAnimation("idle");
		go.addComponent(dc);

		go.transform.position.x = x;
		go.transform.position.y = y;
		go.setDestroyOnLevelLoad(true);
		return go;
	}

	public static GameObject createGenericGraphic (String image, int posx, int posy, int width, int height)
	{
		GameObject go = GameObject.getNew();
		go.setTags(Tags.hud);
		RigidBody rigidBody = new RigidBody();
		rigidBody.setCollider(new BoxCollider(new Vector2(100,32)));
		go.setRigidBody(rigidBody);

		GenericGraphic graphic = PrefabFactory.createGraphic(image, PrefabFactory.getImage(image),30);

		Animation idle = new Animation();
		idle.init("idle", 0, 0, false, PrefabFactory.defaultFps, width, height);
		final GraphicAnimation ga100 = new GraphicAnimation(graphic, idle);

		go.addComponent(ga100);
		go.playAnimation("idle");

		go.transform.position.x = posx;
		go.transform.position.y = posy;
		go.setDestroyOnLevelLoad(true);
		return go;
	}

	public static GameObject createTopOfScreen ()
	{
		final GameObject go = GameObject.getNew();
		//GameObject go = new GameObject();
		go.setName("TopOfScreen");
		
		go.transform.position.x = 0; 
		go.transform.position.y = 0;
		
		final RigidBody r = new RigidBody();
		final Collider c = new BoxCollider(new Vector2(800,32));
		//go.setTags(Tags.topOfScreen);
		//c.setIgnoreTags(Tags.shot);
		r.setCollider(c);
		go.setRigidBody(r);		
		
		//PositionEchoer pe = new PositionEchoer();
		//go.addComponent(pe);

		
		return go;
	}

	//consider changing to a hashmap!
	public static InputStream elaser;
	public static InputStream laser;
	public static InputStream enemy;
	public static InputStream getImage(final String name) {
		if (name.equals("calumniator")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.pcalumniator);
		}
		else if (name.equals("exemplar")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.pexemplar);
		}
		else if (name.equals("escort")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.escort);
		}
		else if (name.equals("hunter")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.phunter);

		}
		else if (name.equals("paladin")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.ppaladin);
		}
		else if (name.equals("pariah")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.ppariah);
		}
		else if (name.equals("sentinel")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.psentinel);
		}
		else if (name.equals("renegade")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.prenegade);
		}
		else if (name.equals("chargebar")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.chargebar);
		}
		else if (name.equals("laser")) {
			return laser;
		}
		else if (name.equals("bullet")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.hmgun);
		} 
		else if (name.equals("lockingLaser")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.hlockinglaser);
		}
		else if (name.equals("chargeLaser")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.hchargelaser);
		}
		else if (name.equals("flame")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.hflame);
		}
		else if (name.equals("pulse")) {
			return PrefabFactory.context.getResources().openRawResource(
					+  R.drawable.hpulse);
		}
		else if (name.equals("hud1")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.hud1);
		}
		else if (name.equals("phaser")) {
			return elaser;
		}
		else if (name.equals("enemy")) {
			return enemy;
		}
		else if (name.equals("powerup")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.powerup);
		}
		else if (name.equals("level4")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level4);
		}
		else if (name.equals("level1")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level1);
		}
		else if (name.equals("level2")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level2);
		}
		else if (name.equals("level3")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level3);
		}
		else if (name.equals("level5")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level5);
		}
		else if (name.equals("level6")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level6);
		}
		else if (name.equals("level7")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level7);
		}
		else if (name.equals("level8")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level8);
		}
		else if (name.equals("level9")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level9);
		}
		else if (name.equals("level10")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level10);
		}
		else if (name.equals("level11")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level11);
		}
		else if (name.equals("level12")) {
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.level12);
		}
		else if (name.equals("explosion")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.exp1);
		}
		else if (name.equals("missile"))
		{
			return PrefabFactory.context.getResources().openRawResource(+ R.drawable.hmissile);
		}
		else if (name.equals("shield")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.shield);
		}
		else if (name.equals("shieldbar")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.shieldbar);
		}
		else if (name.equals("boss2")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.boss2);
		}
		else if (name.equals("boss3")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.boss3);
		}
		else if (name.equals("boss4")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.boss4);
		}
		else if (name.equals("boss5")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.boss5);
		}
		else if (name.equals("difficulty")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.difficulty);
		}
		else if (name.equals("splash")) {
			return PrefabFactory.context.getResources().openRawResource(
					+ R.drawable.splash);
		}

		throw new RuntimeException("image "+name+" not found!");
	}


	public static void setContext(final Context c) {
		PrefabFactory.context = c;
		init();
	}

	private static void init ()
	{
		laser = PrefabFactory.context.getResources().openRawResource(+ R.drawable.hlaser);
		elaser = PrefabFactory.context.getResources().openRawResource(+ R.drawable.helaser);
		enemy = PrefabFactory.context.getResources().openRawResource(+ R.drawable.enemy);
	}

	public static GameObject createBoss(String name, Vector2 position, int bossType, String nextLevel) {
		/*
		GameObject pl = GameObject.findByName("player");
		pl.addComponent(new LoadLevelAfter(nextLevel, 300));
		*/
		if (bossType == 2)
		{
			return createBoss2(name,position,bossType,nextLevel);
		}
		if (bossType == 3)
		{
			return createBoss3(name,position,bossType,nextLevel);
		}
		if (bossType == 4)
		{
			return createBoss4(name,position,bossType,nextLevel);
		}
		if (bossType == 5)
		{
			return createBoss5(name,position,bossType,nextLevel);
		}
		
		return createBoss3(name,position,bossType,nextLevel);
	}
	
	public static GameObject createBoss2(String name, Vector2 position, int bossType, String nextLevel) {
		// Load the texture for the cube once during Surface creation
		final GenericGraphic graphic = PrefabFactory.createGraphic("boss2",
				PrefabFactory.getImage("boss2"),8);

		final int startFrame = 0;
		final int endFrame = 2;
		
		Animation idle = Animation.getNew();
		idle.init("idle", startFrame, endFrame,
				true, PrefabFactory.defaultFps, 208, 98);

		Animation death = Animation.getNew();
		death.init("death", 0,
				2, false, PrefabFactory.defaultFps, 208, 98);

		final GraphicAnimation glIdle = new GraphicAnimation(graphic, idle);
		final GraphicAnimation glDeath = new GraphicAnimation(graphic, death);
		glIdle.play();

		final RigidBody rigidBody = new RigidBody();
		final Collider collider = new BoxCollider(new Vector2(208, 98));
		collider.setIgnoreTags( PrefabFactory.playerTargets );
		rigidBody.setCollider(collider);

		final GameObject o = GameObject.getNew();
		//final GameObject o = new GameObject();
		o.setName("boss2");
		o.setTags(Tags.enemy);
		o.transform.position.x = position.x;
		o.transform.position.y = position.y;
		o.setRigidBody(rigidBody);

		
/*
			final GameObject [] nodes = {
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
			};			
			/*
			nodes[0] = GameObject.create(PrefabFactory.createNode(new Vector2(0,-32),0.0f));
			nodes[1] = GameObject.create(PrefabFactory.createNode(new Vector2(0,800-32),0.0f));
			nodes[2] = GameObject.create(PrefabFactory.createNode(new Vector2(400,800-32),0.0f));
			nodes[3] = GameObject.create(PrefabFactory.createNode(new Vector2(400,0),0.0f));
			nodes[4] = GameObject.create(PrefabFactory.createNode(new Vector2(0,0),0.0f));
			nodes[5] = GameObject.create(PrefabFactory.createNode(new Vector2(0,900),0.0f));
			ai = new AINodeFollower(nodes);*/

		final HealthController hc = new HealthController(new Audio(PrefabFactory.createAudio("explosion"),"explosion"));
		hc.setHealth(5000);

		Component ai = new AIBoss2(hc);
		
		final Weapon [] weapons = new Weapon[8];
		
		weapons[0] = new Phaser(PrefabFactory.shootLeft, 3);
		o.addComponent(weapons[0]);
		weapons[0].setOffset(86, 82);

		weapons[1] = new Phaser(PrefabFactory.shootDownLeft, 3);
		o.addComponent(weapons[1]);
		weapons[1].setOffset(86, 82);

		weapons[2] = new Phaser(PrefabFactory.shootDownRight, 3);
		o.addComponent(weapons[2]);
		weapons[2].setOffset(86, 82);

		weapons[3] = new Phaser(PrefabFactory.shootRight, 3);
		o.addComponent(weapons[3]);
		weapons[3].setOffset(86, 82);
		
		weapons[4] = new Phaser(PrefabFactory.shootDown, 3);
		o.addComponent(weapons[4]);
		weapons[4].setOffset(32-18, 80);

		weapons[5] = new Phaser(PrefabFactory.shootDown, 3);
		o.addComponent(weapons[5]);
		weapons[5].setOffset(146+18, 80);
		
		weapons[6] = new Phaser(PrefabFactory.shootDown, 0.3f, 8, 5);
		final LockingWeaponHandler wh4 = new LockingWeaponHandler(weapons[6],PrefabFactory.enemyTargets,false);
		weapons[6].setOffset(32, 80);
		o.addComponent(weapons[6]);
		o.addComponent(wh4);

		weapons[7] = new Phaser(PrefabFactory.shootDown, 0.3f, 8, 5);
		final LockingWeaponHandler wh5 = new LockingWeaponHandler(weapons[7],PrefabFactory.enemyTargets,false);
		weapons[7].setShots(100);
		weapons[7].setOffset(146, 80);
		o.addComponent(weapons[7]);
		o.addComponent(wh5);
		
		Boss2WeaponHandler bwh = new Boss2WeaponHandler(weapons,hc);
		o.addComponent(bwh);

		final GameObject explosion1 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion1));

		final GameObject explosion2 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion2));

		final GameObject explosion3 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion3));

		LoadLevelOnDestroy llod = new LoadLevelOnDestroy(nextLevel);
		o.addComponent(llod);
		//o.addComponent(new DestroyOnOutOfBounds());
		o.addComponent(glIdle);
		o.addComponent(glDeath);
		o.addComponent(ai);
		o.addComponent(hc);
		o.addComponent(new StopMovementOnCreate());
		
		//System.out.println("boss2");
		
		return o;
	}

	public static GameObject createBoss3(String name, Vector2 position, int bossType, String nextLevel) {
		// Load the texture for the cube once during Surface creation
		final GenericGraphic graphic = PrefabFactory.createGraphic("boss3",
				PrefabFactory.getImage("boss3"),8);

		final int startFrame = 0;
		final int endFrame = 2;
		
		Animation idle = Animation.getNew();
		idle.init("idle", startFrame, endFrame,
				true, PrefabFactory.defaultFps, 230, 140);

		Animation death = Animation.getNew();
		death.init("death", 0,
				2, false, PrefabFactory.defaultFps, 230, 140);

		final GraphicAnimation glIdle = new GraphicAnimation(graphic, idle);
		final GraphicAnimation glDeath = new GraphicAnimation(graphic, death);
		glIdle.play();

		final RigidBody rigidBody = new RigidBody();
		final Collider collider = new BoxCollider(new Vector2(230, 138));		
		collider.setIgnoreTags(PrefabFactory.playerTargets);
		rigidBody.setCollider(collider);
		
		final GameObject o = GameObject.getNew();
		//final GameObject o = new GameObject();
		
		o.setName("boss3");
		o.setTags(Tags.enemy);
		o.transform.position.x = position.x;
		o.transform.position.y = position.y;
		o.setRigidBody(rigidBody);

/*
			final GameObject [] nodes = {
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
					GameObject.getNew(),
			};			
			/*
			nodes[0] = GameObject.create(PrefabFactory.createNode(new Vector2(0,-32),0.0f));
			nodes[1] = GameObject.create(PrefabFactory.createNode(new Vector2(0,800-32),0.0f));
			nodes[2] = GameObject.create(PrefabFactory.createNode(new Vector2(400,800-32),0.0f));
			nodes[3] = GameObject.create(PrefabFactory.createNode(new Vector2(400,0),0.0f));
			nodes[4] = GameObject.create(PrefabFactory.createNode(new Vector2(0,0),0.0f));
			nodes[5] = GameObject.create(PrefabFactory.createNode(new Vector2(0,900),0.0f));
			ai = new AINodeFollower(nodes);
*/

		final HealthController hc = new HealthController(new Audio(PrefabFactory.createAudio("explosion"),"explosion"));
		hc.setHealth(5000);

		Component ai = new AIBoss3(hc);
		
		final Weapon [] weapons = new Weapon[8];
		
		weapons[0] = new Phaser(PrefabFactory.shootDown, 1.5f, 8 ,5);
		o.addComponent(weapons[0]);
		weapons[0].setOffset(117-16, 139-16);
		weapons[0].setShotDelay(0.0f);

		weapons[1] = new Phaser(PrefabFactory.shootDown, 1.5f, 8, 5);
		o.addComponent(weapons[1]);
		weapons[1].setOffset(98-16, 139-16);
		weapons[1].setShotDelay(0.5f);

		weapons[2] = new Phaser(PrefabFactory.shootDown, 1.5f, 8, 5);
		o.addComponent(weapons[2]);
		weapons[2].setOffset(137-16, 139-16);
		weapons[2].setShotDelay(1.0f);
		
				
		weapons[3] = new Phaser(PrefabFactory.shootDown, 3);
		o.addComponent(weapons[3]);
		weapons[3].setOffset(75-16, 116-16);
		
		weapons[4] = new Phaser(PrefabFactory.shootDown, 3);
		o.addComponent(weapons[4]);
		weapons[4].setOffset(160-16, 116-16);
				
		weapons[5] = new Phaser(PrefabFactory.shootDown, 3);
		weapons[5].setOffset(117-16, 139-16);		
		weapons[5].setShotDelay(Time.getTime()+1);
		final LockingWeaponHandler wh7 = new LockingWeaponHandler(weapons[5],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[5]);
		o.addComponent(wh7);
		
		weapons[6] = new Phaser(PrefabFactory.shootDown, 0.3f, 8, 5);
		final LockingWeaponHandler wh4 = new LockingWeaponHandler(weapons[6],PrefabFactory.enemyTargets,false);
		weapons[6].setOffset(1-16, 122-16);
		o.addComponent(weapons[6]);
		o.addComponent(wh4);

		weapons[7] = new Phaser(PrefabFactory.shootDown, 0.3f, 8, 5);
		final LockingWeaponHandler wh5 = new LockingWeaponHandler(weapons[7],PrefabFactory.enemyTargets,false);
		weapons[7].setShots(100);
		weapons[7].setOffset(233-16, 122-16);
		o.addComponent(weapons[7]);
		o.addComponent(wh5);
		
		Boss3WeaponHandler bwh = new Boss3WeaponHandler(weapons,hc);
		o.addComponent(bwh);

		final GameObject explosion1 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion1));

		final GameObject explosion2 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion2));

		final GameObject explosion3 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion3));

		LoadLevelOnDestroy llod = new LoadLevelOnDestroy(nextLevel);
		o.addComponent(llod);
		//o.addComponent(new DestroyOnOutOfBounds());
		o.addComponent(glIdle);
		o.addComponent(glDeath);
		o.addComponent(ai);
		o.addComponent(hc);
		o.addComponent(new StopMovementOnCreate());
		
		//System.out.println("boss3");
		
		return o;
	}
	
	public static GameObject createBoss4(String name, Vector2 position, int bossType, String nextLevel) {
		// Load the texture for the cube once during Surface creation
		final GenericGraphic graphic = PrefabFactory.createGraphic("boss4",
				PrefabFactory.getImage("boss4"),8);

		final int startFrame = 0;
		final int endFrame = 2;
		
		Animation idle = Animation.getNew();
		idle.init("idle", startFrame, endFrame,
				true, PrefabFactory.defaultFps, 208, 98);

		Animation death = Animation.getNew();
		death.init("death", 0,
				2, false, PrefabFactory.defaultFps, 208, 98);

		final GraphicAnimation glIdle = new GraphicAnimation(graphic, idle);
		final GraphicAnimation glDeath = new GraphicAnimation(graphic, death);
		glIdle.play();

		final RigidBody rigidBody = new RigidBody();
		final Collider collider = new BoxCollider(new Vector2(208, 98));		
		collider.setIgnoreTags(PrefabFactory.playerTargets);
		rigidBody.setCollider(collider);
		
		final GameObject o = GameObject.getNew();
		//final GameObject o = new GameObject();
		
		o.setName("boss4");
		o.setTags(Tags.enemy);
		o.transform.position.x = position.x;
		o.transform.position.y = position.y;
		o.setRigidBody(rigidBody);

		final HealthController hc = new HealthController(new Audio(PrefabFactory.createAudio("explosion"),"explosion"));
		hc.setHealth(5000);

		Component ai = new AIBoss4(hc);
		
		final Weapon [] weapons = new Weapon[12];
		
		weapons[0] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[0].setOffset(18-16, 75-16);
		weapons[0].setShotDelay(0.0f);
		final LockingWeaponHandler wh0 = new LockingWeaponHandler(weapons[0],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[0]);
		o.addComponent(wh0);

		weapons[1] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[1].setOffset(42-16, 83-16);
		weapons[1].setShotDelay(0.5f);
		final LockingWeaponHandler wh1 = new LockingWeaponHandler(weapons[1],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[1]);
		o.addComponent(wh1);

		weapons[2] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[2].setOffset(73-16, 96-16);
		weapons[2].setShotDelay(1.0f);
		final LockingWeaponHandler wh2 = new LockingWeaponHandler(weapons[2],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[2]);
		o.addComponent(wh2);
				
		weapons[3] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[3].setOffset(136-16, 96-16);
		weapons[3].setShotDelay(1.5f);
		final LockingWeaponHandler wh3 = new LockingWeaponHandler(weapons[3],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[3]);
		o.addComponent(wh3);
		
		weapons[4] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[4].setOffset(166-16, 81-16);
		weapons[4].setShotDelay(2.0f);
		final LockingWeaponHandler wh4 = new LockingWeaponHandler(weapons[4],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[4]);
		o.addComponent(wh4);
		
				
		weapons[5] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[5].setOffset(191-16, 75-16);		
		weapons[5].setShotDelay(2.5f);
		final LockingWeaponHandler wh5 = new LockingWeaponHandler(weapons[5],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[5]);
		o.addComponent(wh5);

		weapons[6] = new Phaser(PrefabFactory.shootLeft, 3);
		o.addComponent(weapons[6]);
		weapons[6].setOffset(105-16, 86-16);

		weapons[7] = new Phaser(PrefabFactory.shootDownLeft, 3);
		o.addComponent(weapons[7]);
		weapons[7].setOffset(105-16, 86-16);

		weapons[8] = new Phaser(PrefabFactory.shootDownRight, 3);
		o.addComponent(weapons[8]);
		weapons[8].setOffset(105-16, 86-16);

		weapons[9] = new Phaser(PrefabFactory.shootRight, 3);
		o.addComponent(weapons[9]);
		weapons[9].setOffset(105-16, 86-16);
		
		weapons[10] = new Phaser(PrefabFactory.shootDown, 3);
		o.addComponent(weapons[10]);
		weapons[10].setOffset(105-16, 86-16);
		
		weapons[11] = new Phaser(PrefabFactory.shootDown, 0.3f, 8, 5);
		final LockingWeaponHandler wh11 = new LockingWeaponHandler(weapons[11],PrefabFactory.enemyTargets,false);
		weapons[11].setOffset(105-16, 63-16);
		o.addComponent(weapons[11]);
		o.addComponent(wh11);


		
		Boss4WeaponHandler bwh = new Boss4WeaponHandler(weapons,hc);
		o.addComponent(bwh);

		final GameObject explosion1 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion1));

		final GameObject explosion2 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion2));

		final GameObject explosion3 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion3));

		LoadLevelOnDestroy llod = new LoadLevelOnDestroy(nextLevel);
		o.addComponent(llod);
		//o.addComponent(new DestroyOnOutOfBounds());
		o.addComponent(glIdle);
		o.addComponent(glDeath);
		o.addComponent(ai);
		o.addComponent(hc);
		o.addComponent(new StopMovementOnCreate());
		
		//System.out.println("boss4");
		
		return o;
	}

	
	public static GameObject createBoss5(String name, Vector2 position, int bossType, String nextLevel) {
		// Load the texture for the cube once during Surface creation
		final GenericGraphic graphic = PrefabFactory.createGraphic("boss5",
				PrefabFactory.getImage("boss5"),8);

		final int startFrame = 0;
		final int endFrame = 2;
		
		Animation idle = Animation.getNew();
		idle.init("idle", startFrame, endFrame,
				true, PrefabFactory.defaultFps, 478, 298);

		Animation death = Animation.getNew();
		death.init("death", 0,
				2, false, PrefabFactory.defaultFps, 478, 298);

		final GraphicAnimation glIdle = new GraphicAnimation(graphic, idle);
		final GraphicAnimation glDeath = new GraphicAnimation(graphic, death);
		glIdle.play();

		final RigidBody rigidBody = new RigidBody();
		final Collider collider = new BoxCollider(new Vector2(478, 200));		
		collider.setIgnoreTags(PrefabFactory.playerTargets);
		rigidBody.setCollider(collider);
		
		final GameObject o = GameObject.getNew();
		//final GameObject o = new GameObject();
		
		o.setName("boss5");
		o.setTags(Tags.enemy);
		o.transform.position.x = position.x;
		o.transform.position.y = position.y;
		o.setRigidBody(rigidBody);

		final HealthController hc = new HealthController(new Audio(PrefabFactory.createAudio("explosion"),"explosion"));
		hc.setHealth(5000);

		Component ai = new AIBoss5(hc);
		
		final Weapon [] weapons = new Weapon[23];
		
		weapons[0] = new Phaser(PrefabFactory.shootLeft, 6);
		weapons[0].setOffset(82, 183);
		weapons[0].setShotDelay(0.0f);
		o.addComponent(weapons[0]);
		
		weapons[1] = new Phaser(PrefabFactory.shootDownLeft, 6);
		weapons[1].setOffset(82, 183);
		weapons[1].setShotDelay(0.2f);
		o.addComponent(weapons[1]);
		
		weapons[2] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[2].setOffset(82, 183);
		weapons[2].setShotDelay(0.4f);
		o.addComponent(weapons[2]);
		
		weapons[3] = new Phaser(PrefabFactory.shootDownRight, 6);
		weapons[3].setOffset(82, 183);
		weapons[3].setShotDelay(0.6f);
		o.addComponent(weapons[3]);
		
		weapons[4] = new Phaser(PrefabFactory.shootRight, 6);
		weapons[4].setOffset(82, 183);
		weapons[4].setShotDelay(0.8f);
		o.addComponent(weapons[4]);

		weapons[5] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[5].setOffset(127, 201);
		weapons[5].setShotDelay(2.0f);
		final LockingWeaponHandler wh5 = new LockingWeaponHandler(weapons[5],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[5]);
		o.addComponent(wh5);

		weapons[6] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[6].setOffset(179, 222);
		weapons[6].setShotDelay(2.5f);
		final LockingWeaponHandler wh6 = new LockingWeaponHandler(weapons[6],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[6]);
		o.addComponent(wh6);
				
		weapons[7] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[7].setOffset(300, 222);
		weapons[7].setShotDelay(3.0f);
		final LockingWeaponHandler wh7 = new LockingWeaponHandler(weapons[7],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[7]);
		o.addComponent(wh7);
		
		weapons[8] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[8].setOffset(352, 202);
		weapons[8].setShotDelay(3.5f);
		final LockingWeaponHandler wh8 = new LockingWeaponHandler(weapons[8],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[8]);
		o.addComponent(wh8);
		
		weapons[9] = new Phaser(PrefabFactory.shootLeft, 3);
		o.addComponent(weapons[9]);
		weapons[9].setShotDelay(0.8f);
		weapons[9].setOffset(398, 183);

		weapons[10] = new Phaser(PrefabFactory.shootDownLeft, 3);
		o.addComponent(weapons[10]);
		weapons[10].setShotDelay(0.6f);
		weapons[10].setOffset(398, 183);
		
		weapons[11] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[11].setOffset(398, 183);
		weapons[11].setShotDelay(0.4f);
		o.addComponent(weapons[11]);

		weapons[12] = new Phaser(PrefabFactory.shootDownRight, 3);
		o.addComponent(weapons[12]);
		weapons[12].setShotDelay(0.2f);
		weapons[12].setOffset(398, 183);

		weapons[13] = new Phaser(PrefabFactory.shootRight, 3);
		o.addComponent(weapons[13]);
		weapons[13].setShotDelay(0.0f);
		weapons[13].setOffset(398, 183);
		
	
		weapons[14] = new Phaser(PrefabFactory.shootDown, 0.3f, 8, 5);
		weapons[14].setOffset(51, 154);
		weapons[14].setShotDelay(0.0f);
		final LockingWeaponHandler wh14 = new LockingWeaponHandler(weapons[14],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[14]);
		o.addComponent(wh14);

		weapons[15] = new Phaser(PrefabFactory.shootDown, 0.3f, 8, 5);
		weapons[15].setOffset(428, 154);
		weapons[15].setShotDelay(0.3f*8);
		final LockingWeaponHandler wh15 = new LockingWeaponHandler(weapons[15],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[15]);
		o.addComponent(wh15);

		weapons[16] = new Phaser(PrefabFactory.shootDown, 0.3f, 8, 5);
		weapons[16].setOffset(135-16, 170-16);
		weapons[16].setShotDelay(0.5f);
		final LockingWeaponHandler wh16 = new LockingWeaponHandler(weapons[16],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[16]);
		o.addComponent(wh16);

		weapons[17] = new Phaser(PrefabFactory.shootDown, 0.3f, 8, 5);
		weapons[17].setOffset(345-16, 170-16);
		weapons[17].setShotDelay(1.0f);
		final LockingWeaponHandler wh17 = new LockingWeaponHandler(weapons[17],PrefabFactory.enemyTargets,false);
		o.addComponent(weapons[17]);
		o.addComponent(wh17);


		weapons[18] = new Phaser(PrefabFactory.shootLeft, 6);
		weapons[18].setOffset(239-16, 297-16);
		weapons[18].setShotDelay(0.0f);
		o.addComponent(weapons[0]);
		
		weapons[19] = new Phaser(PrefabFactory.shootDownLeft, 6);
		weapons[19].setOffset(239-16, 297-16);
		weapons[19].setShotDelay(0.2f);
		o.addComponent(weapons[19]);
		
		weapons[20] = new Phaser(PrefabFactory.shootDown, 6);
		weapons[20].setOffset(239-16, 297-16);
		weapons[20].setShotDelay(0.4f);
		o.addComponent(weapons[20]);
		
		weapons[21] = new Phaser(PrefabFactory.shootDownRight, 6);
		weapons[21].setOffset(239-16, 297-16);
		weapons[21].setShotDelay(0.2f);
		o.addComponent(weapons[21]);
		
		weapons[22] = new Phaser(PrefabFactory.shootRight, 6);
		weapons[22].setOffset(239-16, 297-16);
		weapons[22].setShotDelay(0.0f);
		o.addComponent(weapons[22]);

		
		
		Boss5WeaponHandler bwh = new Boss5WeaponHandler(weapons,hc);
		o.addComponent(bwh);

		final GameObject explosion1 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion1));

		final GameObject explosion2 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion2));

		final GameObject explosion3 = PrefabFactory.createExplosion(o.transform.position);
		o.addComponent(new SpawnOnDestroy(explosion3));

		LoadLevelOnDestroy llod = new LoadLevelOnDestroy(nextLevel);
		o.addComponent(llod);
		//o.addComponent(new DestroyOnOutOfBounds());
		o.addComponent(glIdle);
		o.addComponent(glDeath);
		o.addComponent(ai);
		o.addComponent(hc);
		o.addComponent(new StopMovementOnCreate());
		
		//System.out.println("boss4");
		
		return o;
	}

}
