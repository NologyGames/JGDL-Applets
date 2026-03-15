
import JGDL.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;

class PopEffectManager extends JGDLObject
{
	public static final int WET_POPCORN 	= 1,
													WET_EXPLOSION = 2;
	
	public class Effect
	{
		byte 									Type;
		JGDLSprite						p_Sprite = null;
		JGDLVector      			vPos = new JGDLVector();
		JGDLVector      			vSpeed = new JGDLVector();
		JGDLTimeAccumulator		timer = new JGDLTimeAccumulator();
	}
	
	//Efeitos do gerenciador
	Effect [] Effects = new Effect[1024];
	
	//Nro de efeitos no gerenciador
	int iEffects = 0;
	JGDLSprite p_PopCorn 		= null;
	JGDLSprite p_Explosion	= null;
	
	public PopLevel p_Level = null;
	
	//---------------------------------------------------------------------------- 
	// Name: PopEffectManager()
	// Desc: Construtor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public PopEffectManager()
	{
		for(int i = 0 ; i < 1024; i++)
		{
			Effects[i] = new Effect();
		}
		iEffects = 0;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: Inicializa o gerenciador de efeitos
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Initialize()
	{
		JGDLLayer p_EffectLayer = p_Level.CreateLayer(new JGDLVector(448,336));
		p_PopCorn = p_EffectLayer.CreateSprite("inp_PopCorn.gif",new JGDLVector(35,35));
		p_PopCorn.bVisible = false;
		int [] p_Frames = new int[1];
		int i = 0;
		for(i = 0; i < 20; i++)
		{
			p_Frames[0] = i;
			p_PopCorn.AddAnimation(10,false,p_Frames);
		}
		iEffects = 0;
		
		p_Explosion = p_EffectLayer.CreateSprite("spr_Explosion.gif",new JGDLVector(17,17));
		int [] ExplodeAnim = new int[17];
		i = 0;
		for(i = 0; i < 16; i++)
		{
			ExplodeAnim[i] = i;
		}
		ExplodeAnim[16] = -1;
		p_Explosion.AddAnimation(20,false,ExplodeAnim);
		p_Explosion.bVisible = false;
		
		
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: Libera os recursos internos da classe
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		return true;
	}

	//---------------------------------------------------------------------------- 
	// Name: RemoveEffect()
	// Desc: removes one effect from the effect list
	// Pams: nonr
	//---------------------------------------------------------------------------- 
	public void RemoveEffect(int iRemoveIndex)
	{
		if(iRemoveIndex >= 0 && iRemoveIndex < iEffects)
		{
			if(iRemoveIndex < iEffects-1)
			{
				Effect Temp						= Effects[iRemoveIndex];
				Effects[iRemoveIndex] = Effects[iEffects -1];
				Effects[iEffects -1]	= Temp;
				if(Temp.p_Sprite != null)
				{
					p_Level.FreeSprites.push_back(Temp.p_Sprite);
					Temp.p_Sprite = null;
				}
			}
			iEffects--;
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: CreateExplosion(JGDLVector vCenter)
	// Desc: Cria um efeito de explosão
	// Pams: center pos
	//---------------------------------------------------------------------------- 
	public void CreateExplosion(JGDLVector vCenter)
	{
		if(iEffects < 1023)
		{
			Effects[iEffects].timer.Init(p_Level.pr_Main,1000);
			int iSize = p_Level.FreeSprites.size();
			if(iSize > 0)
			{
				Effects[iEffects].p_Sprite = (JGDLSprite)p_Level.FreeSprites.get(iSize-1);
				p_Level.FreeSprites.remove(iSize-1);
				p_Explosion.GetClone(Effects[iEffects].p_Sprite);
			}
			else
			{
				Effects[iEffects].p_Sprite = p_Explosion.GetClone(true);
			}
			int iRand = Math.abs(p_Level.pr_Main.Randomizer.nextInt());
			Effects[iEffects].p_Sprite.SetCurrentAnimation(0);
			Effects[iEffects].p_Sprite.ResetAnimation();
			Effects[iEffects].p_Sprite.SetCenterPos(vCenter);
			Effects[iEffects].p_Sprite.bVisible = true;
			
			Effects[iEffects].Type = WET_EXPLOSION;
			iEffects++;
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CreatePopCorn(JGDLVector vCenter)
	// Desc: cria um efeito pipoca
	// Pams: center pos
	//---------------------------------------------------------------------------- 
	public void CreatePopCorn(JGDLVector vCenter)
	{
		if(iEffects < 1023)
		{
			Effects[iEffects].timer.Init(p_Level.pr_Main,5000);
			int iSize = p_Level.FreeSprites.size();
			if(iSize > 0)
			{
				Effects[iEffects].p_Sprite = (JGDLSprite)p_Level.FreeSprites.get(iSize-1);
				p_Level.FreeSprites.remove(iSize-1);
				p_PopCorn.GetClone(Effects[iEffects].p_Sprite);
			}
			else
			{
				Effects[iEffects].p_Sprite = p_PopCorn.GetClone(true);
			}
			int iRand = Math.abs(p_Level.pr_Main.Randomizer.nextInt());
			Effects[iEffects].p_Sprite.SetCurrentAnimation(iRand%20);
			Effects[iEffects].p_Sprite.SetCenterPos(vCenter);
			Effects[iEffects].p_Sprite.bVisible = true;
			Effects[iEffects].vSpeed.fx = p_Level.RandRange(-200,200);
			Effects[iEffects].vSpeed.fy = -p_Level.RandRange(140,630);
			Effects[iEffects].vPos.atrib(vCenter);
			
			
			Effects[iEffects].Type = WET_POPCORN;
			iEffects++;
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: Update()
	// Desc: Updates the effect manager
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void Update()
	{
		for(int i = iEffects -1 ; i >= 0; i--)
		{
			switch(Effects[i].Type)
			{
				case WET_POPCORN:
				{
					float fGravity = 1000.0f;
					Effects[i].vPos.fx += Effects[i].vSpeed.fx * p_Level.pr_Main.TimeHandler.fFrameTime;
					Effects[i].vPos.fy += (Effects[i].vSpeed.fy * p_Level.pr_Main.TimeHandler.fFrameTime) + ((fGravity * p_Level.pr_Main.TimeHandler.fFrameTime * p_Level.pr_Main.TimeHandler.fFrameTime)*0.5f);
					Effects[i].vSpeed.fy += fGravity * p_Level.pr_Main.TimeHandler.fFrameTime;
					Effects[i].p_Sprite.SetCenterPos(Effects[i].vPos);
					if(Effects[i].p_Sprite.position.fy > 336)
					{
						RemoveEffect(i);
					}
					break;
				}
			}
			Effects[i].timer.Update();
			Effects[i].p_Sprite.Update();
			if(Effects[i].timer.Ended())
			{
				RemoveEffect(i);
			}
		}
	}

}
