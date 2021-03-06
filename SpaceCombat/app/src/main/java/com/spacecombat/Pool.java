package com.spacecombat;


public class Pool {

	private Poolable [] pool = null;
	private boolean [] used;
	private Class<? extends Poolable> poolableType;

	public void init (final Class<? extends Poolable> type, final int size)
	{
		this.used = new boolean[size];
		this.pool = new Poolable[size];

		this.poolableType = type;
		for (int x = 0; x < size; x++)
		{
			try {
				this.pool[x]=(this.poolableType.newInstance());
				this.used[x] = false;
				this.pool[x].setPoolId(x);
			} catch (final InstantiationException e) {
				throw new RuntimeException(e);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		//System.out.println("Pool initialized:"+type.getSimpleName() + " " + this.pool.length);
	}

	public void release (Poolable p)
	{				
		try
		{
			this.used[p.getPoolId()] = false;
		}
		catch (Exception e)
		{
			throw new RuntimeException("POOL IS CORRUPT:" + poolableType.getSimpleName() + " " + this.pool.length + " thought this existed:" + p.getPoolId());
		}
	}

	
	private int lastX = 0;
	public Poolable retreive ()
	{
		//loop around the pool so each time we retrieve,
		//we don't hit stuff that is already used
		int finish = lastX - 1;
		if (finish < 0)
		{
			finish = this.pool.length;
		}
		
		for (int x = lastX; x != finish; x++)
		{
			if (x == pool.length)
			{
				x = 0;
				if (x == finish)
				{
					break;
				}
			}
			
			if (!this.used[x])
			{
				//we hit something, so the next one is probably free
				lastX = x + 1;
				this.used[x] = true;
				this.pool[x].clean();
				return this.pool[x];
			}
		}

		//grow pool
		boolean [] newUsed = new boolean[this.used.length + this.used.length / 4];
		Poolable [] newPool = new Poolable[newUsed.length];
		
		for (int x = 0; x < used.length; x++)
		{
			newUsed[x] = used[x];
			newPool[x] = pool[x];
		}
		
		try
		{
			for (int x = used.length; x < newUsed.length; x++)
			{			
				newPool[x]=(this.poolableType.newInstance());
				newUsed[x] = false;
				newPool[x].setPoolId(x);
			}
		} catch (final InstantiationException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		this.used = newUsed;
		this.pool = newPool;
		
		lastX = used.length;
		
		return retreive();
	}
	
	public void printPool ()
	{
		int used = 0;
		int free = 0;
		int total = 0;
		
		for (int x = 0; x < this.pool.length; x++)
		{
			total++;
			if (this.used[x])
			{
				used++;
			}
			else
			{
				free++;
			}
		}
		
		//System.out.println("POOL:" + used + "/" + total + "  ("+free+ "left)");
	}
}
