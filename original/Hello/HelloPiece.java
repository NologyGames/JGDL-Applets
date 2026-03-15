
import JGDL.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;

class HelloPiece extends JGDLObject
{
	  public final static JGDLVector TempPos1 = new JGDLVector();
	  public final static JGDLVector TempPos2 = new JGDLVector();
	  public final static JGDLVector TempPos3 = new JGDLVector();
	  public final static JGDLVector TempPos4 = new JGDLVector();
	  public final static JGDLVector TempPos5 = new JGDLVector();
	  
		public final static JGDLList		pUps	= new JGDLList();
		public final static	JGDLVector	vDist = new JGDLVector();;
		//definições
		public final static int   WPIECEWIDTH  = 31;
		public final static int   WPIECEHEIGHT = 31;

    //power up type
		public final static byte  WPU_NONE 					= -1;
		public final static byte  WPU_LINEREMOVE 		= 0;
		public final static byte  WPU_COLREMOVE			= 1;
		public final static byte  WPU_BOMB 					= 2;
    

    //piece effect		
		public final static byte  WPE_NONE 		= 0;
		public final static byte  WPE_CHANGE 	= 1;
		public final static byte  WPE_EXPLODE 	= 2;
		public final static byte  WPE_FADE 		= 3;
		public final static float WGRAVITY 		= 5500.0f;
	
 
	  boolean bExecuted = false;

		int				 iShakeTime;
		
		JGDLVector vShake = new JGDLVector();
		JGDLVector pShake = new JGDLVector();
		//used to power up fade
		boolean		 bFade;
		//new id when id must change
		byte			byNewID;
		
		JGDLVector ExplodePos   = new JGDLVector();
		JGDLVector ExplodeSpeed = new JGDLVector();

		boolean						bShake;

		//flag used for power up
		boolean					 bIsPowerUp;
		HelloLevel       pr_Level;

		//piece effect
		byte 						 bEffect;

		//Piece Sprite
		JGDLSprite pr_Sprite = null;

		//Fade Sprite
		JGDLSprite pr_Fade = null;

		//Piece ID
		byte byID;

		//flag used to determine paths
		boolean bIsGoal;

		//flag used on mode 1
		boolean bUp;

		//used on sibling deletion
		boolean bVisited;

		byte    byConnections;

		//piece index int the screen matrix
		int iIndex;
		
	
		//power up type
		byte bType;
		//---------------------------------------------------------------------------- 
		// Name: HelloPiece()
		// Desc: Construtor padrão
		// Pams: nenhum
		//---------------------------------------------------------------------------- 
		HelloPiece(HelloLevel p_LevelParam)		
		{
			iShakeTime		= 0;
			bShake				= false;
			bFade					= false;
			pr_Level			= p_LevelParam;
			iIndex				= -1;
			bIsPowerUp		= false;
			byConnections = 0;
			bVisited			= false;
			bUp						= true;
			bIsGoal				= false;
			byID					= 0;
			pr_Sprite			= null;
			pr_Fade				= null;
			bEffect				= WPE_NONE;
			bType 				= WPU_NONE;
		}

		//---------------------------------------------------------------------------- 
		// Name: ExecutePU()
		// Desc: Executes the power up
		// Pams: none
		//---------------------------------------------------------------------------- 
		public void ExecutePU(JGDLVector ClickPos)
		{
			if(!bExecuted && bEffect != WPE_FADE)
			{
				bExecuted = true;
				switch(bType)
				{
					case WPU_LINEREMOVE:
					{
						int i;
						JGDLVector Pos = TempPos3;
						Pos.atrib(ClickPos);
						if(Pos.fx >= 0 && Pos.fx < pr_Level.MatSizeX && 
							 Pos.fy >= 0 && Pos.fy < pr_Level.MatSizeY)
						{
		
							// will song
							if (null != pr_Level.p_SndPULinCol)
							{
								pr_Level.p_SndPULinCol.Play();
							}
		
							for(i = (int)Pos.fx-1; i >= 0; i--)
							{
								if(null != pr_Level.Mat(i,(int)Pos.fy))
								{
									pr_Level.RemovePiece(i + (((int)Pos.fy)*pr_Level.MatSizeX));
								}
							}
							for(i = (int)Pos.fx+1; i < pr_Level.MatSizeX; i++)
							{
								if(null != pr_Level.Mat(i,(int)Pos.fy))
								{
									pr_Level.RemovePiece(i + (((int)Pos.fy)*pr_Level.MatSizeX));
								}
							}
							
							pr_Level.PosFromMatrix(TempPos2, ClickPos);
							pr_Level.EffectManager.CreateLineRemove(TempPos2);
						}
						Pos = null;
						break;
					}
					case WPU_COLREMOVE:
					{
						JGDLVector Pos = TempPos4;
						Pos.atrib(ClickPos);
						if(Pos.fx >= 0 && Pos.fx < pr_Level.MatSizeX && 
							 Pos.fy >= 0 && Pos.fy < pr_Level.MatSizeY)
						{
		
							// will song
							if (null != pr_Level.p_SndPULinCol)
							{
								pr_Level.p_SndPULinCol.Play();
							}
		
							for(int i = 0; i < pr_Level.MatSizeY; i++)
							{
								if(null != pr_Level.Mat((int)Pos.fx,i) && pr_Level.Mat((int)Pos.fx,i) != this)
								{
									pr_Level.RemovePiece((int)Pos.fx+ (i*pr_Level.MatSizeX));
								}
							}
		
							pr_Level.PosFromMatrix(TempPos2, ClickPos);
							pr_Level.EffectManager.CreateColRemove(TempPos2);
						}
	//					pr_Mode->pr_Level->pr_MMain->Ringo.AnimateMulti(WRS_HAPPY1,WRS_HAPPY1,WRS_LOOKTOPLAYER,WRS_END);
						Pos = null;
						break;
					}
					case WPU_BOMB:
					{
						if (null != pr_Level.p_SndPUBomb)
						{
							pr_Level.p_SndPUBomb.Play();
						}
						int iClick = (((int)ClickPos.fy)*pr_Level.MatSizeX) + (int)ClickPos.fx;
						if(null != pr_Level.ScreenMatrix[iClick])
						{
							JGDLVector ExpldPos = TempPos2;
							pr_Level.PosFromMatrix(ExpldPos, ClickPos);
							ExpldPos.fx += ((float)WPIECEWIDTH)*0.5f;
							ExpldPos.fy += ((float)WPIECEHEIGHT)*0.5f;
							
							pr_Level.EffectManager.CreateBombExplosion(ExpldPos);
			
							pUps.clear();
							vDist.atrib(0,0);
							int iSize = pr_Level.MatSizeX*pr_Level.MatSizeY;
							for(int i =0 ; i < iSize; i++)
							{
								if(null != pr_Level.ScreenMatrix[i] && i != iClick && pr_Level.ScreenMatrix[i].bEffect == WPE_NONE)
								{
									vDist.fx = pr_Level.ScreenMatrix[i].pr_Sprite.position.fx - pr_Level.ScreenMatrix[iClick].pr_Sprite.position.fx;
									vDist.fy = pr_Level.ScreenMatrix[i].pr_Sprite.position.fy - pr_Level.ScreenMatrix[iClick].pr_Sprite.position.fy;
									
									if(vDist.Magnitude() <= 70.0f)
									{
										pr_Level.ScreenMatrix[i].pr_Sprite.GetCenterPos(TempPos1);
										pr_Level.EffectManager.CreateBombExplosion(TempPos1);
										if(pr_Level.ScreenMatrix[i].bIsPowerUp)
										{
											pUps.push_back(pr_Level.ScreenMatrix[i]);
										}
										pr_Level.ScreenMatrix[i].Explode();
									}
								}
							}
							pr_Level.ScreenMatrix[iClick].Explode();
							for(int i = (int)pUps.size()-1; i >= 0; i--)
							{
								if(pUps.get(i) != this)
								{
									((HelloPiece)pUps.get(i)).ExecutePU(ClickPos);
								}
							}
						}
						break;
					}
				}
			}
		}
		
		//---------------------------------------------------------------------------- 
		// Name: GetClone()
		// Desc: returns a clone of this piece
		// Pams: none
		//---------------------------------------------------------------------------- 
		public HelloPiece GetClone()
		{
			
		  HelloPiece pr_Piece = null;
		  int iSize = pr_Level.FreePieces.size();
		  
		  if(iSize > 0)
		  {
		    pr_Piece = (HelloPiece)pr_Level.FreePieces.get(iSize-1);
		    pr_Level.FreePieces.remove(iSize-1);
		  }
		  else
		  {
		  	pr_Piece = new HelloPiece(pr_Level);
		  }
		
			if(null != pr_Sprite)
			{
				if(null != pr_Piece.pr_Sprite)
				{
					pr_Sprite.GetClone(pr_Piece.pr_Sprite);
				}
				else
				{
					pr_Piece.pr_Sprite	= pr_Sprite.GetClone(false);
				}
			}
			else
			{
				pr_Piece.pr_Sprite = null;
			}
			
			if(null != pr_Fade)
			{
				if(null != pr_Piece.pr_Fade)
				{
					pr_Fade.GetClone(pr_Piece.pr_Fade);
				}
				else
				{
					pr_Piece.pr_Fade		= pr_Fade.GetClone(false);
				}
			}
			else
			{
				pr_Piece.pr_Fade = null;
			}
			
			
			pr_Piece.bType					= bType;	
			pr_Piece.byID						= byID;
			pr_Piece.byConnections 	= byConnections;
			pr_Piece.bIsPowerUp			= bIsPowerUp;
			pr_Piece.bExecuted			= false;
			bEffect									= WPE_NONE;
		
			return pr_Piece;
		}
		
		//---------------------------------------------------------------------------- 
		// Name: EndedEffect
		// Desc: returns true if the current piece effect has ended
		// Pams: none
		//---------------------------------------------------------------------------- 
		public boolean EndedEffect()
		{
			switch(bEffect)
			{
				case WPE_CHANGE:
				{
					return false;
				}
				case WPE_EXPLODE:
				{
					return (ExplodePos.fy >= 400);
				}
				case WPE_FADE:
				{
					if(null != pr_Fade)
					{
						return pr_Fade.EndedAnimation();
					}
					else
					{
						return true;
					}
				}//case
				case WPE_NONE:
				{
					break;
				}
			}
			return true;
	
		}

		//---------------------------------------------------------------------------- 
		// Name: DeleteArtifacts()
		// Desc: deletes de piece artifacts
		// Pams: none
		//---------------------------------------------------------------------------- 
		void DeleteArtifacts()
		{
			if(null != pr_Sprite)
			{
				pr_Sprite.Release();
				pr_Sprite = null;
			}
			
			if(null != pr_Fade)
			{
				pr_Fade.Release();
				pr_Fade = null;
			}
		}

		//---------------------------------------------------------------------------- 
		// Name: Update()
		// Desc: updates the phone piece
		// Pams: none
		//---------------------------------------------------------------------------- 
		public void Update() 
		{
		  //updating shake
			if(bShake)
			{
				
				iShakeTime += pr_Level.pr_Main.TimeHandler.iFrameTime;
				if(iShakeTime >= 120)
				{
					iShakeTime %= 120;
					vShake.fx = (float)Math.cos(pr_Level.pr_Main.Randomizer.nextDouble()*6.2831853071);
					vShake.fy = (float)Math.sin(pr_Level.pr_Main.Randomizer.nextDouble()*6.2831853071);
				}
				else
				{
					if(iShakeTime < 60)
					{
						pShake.fx = (vShake.fx*5.0f*(float)iShakeTime)/60.0f;
						pShake.fy = (vShake.fy*5.0f*(float)iShakeTime)/60.0f;
					}
					else
					{
						pShake.fx = (vShake.fx*5.0f*(float)(120 - iShakeTime))/60.0f;
						pShake.fy = (vShake.fy*5.0f*(float)(120 - iShakeTime))/60.0f;
					}
				}
			}
			else
			{
				pShake.fx = pShake.fy = 0;
			}
		
			if(null != pr_Sprite)
			{
				switch(bEffect)
				{
					case WPE_CHANGE:
					{
						if(null != pr_Fade)
						{
							pr_Sprite.Update();

							JGDLVector pCenter = TempPos1;
							pr_Sprite.GetCenterPos(pCenter);

							pr_Fade.SetCenterPos(pCenter);
							pr_Fade.Update();
							if(pr_Fade.EndedAnimation())
							{
								//is fade out!!!
								if(pr_Fade.GetCurrentAnimation() == 0)
								{
									DeleteArtifacts();
									pr_Sprite = pr_Level.Phones[byNewID].pr_Sprite.GetClone(false);
									pr_Sprite.SetCenterPos(pCenter);
	
									pr_Fade   = pr_Level.Phones[byNewID].pr_Fade.GetClone(false);
									pr_Fade.SetCenterPos(pCenter);
									pr_Fade.SetCurrentAnimation(1);
									pr_Fade.ResetAnimation();
									byID = byNewID;
								}
								//if fade in!!!
								if(pr_Fade.GetCurrentAnimation() == 1)
								{
	                bEffect = WPE_NONE;
								}
							}
						}
						break;
					}
					case WPE_EXPLODE:
					{
						if(ExplodePos.fy < 500)
						{
							float fGravity = 3500.0f;
							float fTime = pr_Level.pr_Main.TimeHandler.fFrameTime;
							//X
							ExplodePos.fx += ExplodeSpeed.fx*pr_Level.pr_Main.TimeHandler.fFrameTime;
		
							//Y
							ExplodePos.fy = ExplodePos.fy + (ExplodeSpeed.fy*fTime) + ((fGravity*fTime*fTime)*0.5f);
							ExplodeSpeed.fy = ExplodeSpeed.fy + (fGravity*fTime);
							pr_Sprite.position = ExplodePos;
						}
						break;
					}
					case WPE_FADE:
					{
						if(bIsPowerUp && !bFade && null != pr_Sprite)
						{
							if(pr_Sprite.EndedAnimation())
							{
								bFade = true;
								pr_Fade.ResetAnimation();
							}
							pr_Sprite.Update();
						}
						if(bFade && null != pr_Fade)
						{
							pr_Sprite.GetCenterPos(TempPos1);
							pr_Fade.SetCenterPos(TempPos1);
							pr_Fade.Update();
						}
						break;
					}
					case WPE_NONE:
					{
						pr_Sprite.Update();
						break;
					}
				}
			}
		}
		//---------------------------------------------------------------------------- 
		// Name: SameID()
		// Desc: returns true if the piece have the same id
		// Pams: none
		//---------------------------------------------------------------------------- 
		boolean SameID(HelloPiece p_OtherPiece)
		{
			return (p_OtherPiece.byID == byID || bIsPowerUp || p_OtherPiece.bIsPowerUp);
		}
		//---------------------------------------------------------------------------- 
		// Name: Render()
		// Desc: renders the phone piece
		// Pams: none
		//---------------------------------------------------------------------------- 
		public void Render()
		{
			pr_Sprite.position.fx += pShake.fx;
			pr_Sprite.position.fy += pShake.fy;
			switch (bEffect)
			{
				case WPE_CHANGE:
				case WPE_FADE:
				{
					if(bIsPowerUp && !bFade && null != pr_Sprite)
					{
						pr_Sprite.Draw();
					}
					else
					{
						if(null != pr_Fade)
						{
							pr_Fade.Draw();
						}
					}
					break;
				}
				case WPE_EXPLODE:
				case WPE_NONE:
				{
					if(null != pr_Sprite)
					{
						pr_Sprite.Draw();
					}
					break;
				}
			}
			pr_Sprite.position.fx -= pShake.fx;
			pr_Sprite.position.fy -= pShake.fy;
		}

		//---------------------------------------------------------------------------- 
		// Name: RandRange(int iMin, int iMax) 
		// Desc: Returns a random number
		// Pams: none
		//---------------------------------------------------------------------------- 
		public int RandRange(int iMin, int iMax) 
		{
			int iRand = pr_Level.pr_Main.Randomizer.nextInt();
			iRand = Math.abs(iRand);
			return (iMin + (iRand)%iMax);
		}
		
		//---------------------------------------------------------------------------- 
		// Name: ChangeID(Byte byNewID)
		// Desc: Changes the piece id	
		// Pams: new piece id
		//---------------------------------------------------------------------------- 
		void ChangeID(byte byNewIDParam)
		{
			if(!bIsPowerUp && bEffect == WPE_NONE && byNewIDParam >= 0 && byNewIDParam < pr_Level.Phones.length)
			{
				pr_Fade.SetCurrentAnimation(0);
				pr_Fade.ResetAnimation();
				byNewID = byNewIDParam;
				bEffect = WPE_CHANGE;
			}
		}
		//---------------------------------------------------------------------------- 
		// Name: FadeOut()
		// Desc: fade out
		// Pams: none
		//---------------------------------------------------------------------------- 
		public void FadeOut()
		{
			if(bEffect == WPE_NONE || bEffect == WPE_CHANGE)
			{
				if (null != pr_Sprite)
				{
					// phone is ringing
/*					if (pr_Sprite.GetCurrentAnimation() == 2)
					{
						if (g_PhoneGame.p_SndRingPhone[byID])
						{
							g_PhoneGame.p_SndRingPhone[byID]->Stop();
						}
					}*/
				}
				if(bIsPowerUp)
				{
					bFade = false;
					if(null != pr_Sprite)
					{
						pr_Sprite.SetCurrentAnimation(1);
						pr_Sprite.ResetAnimation();
					}
				}
				else
				{
					bFade = true;
				}
				if(null != pr_Fade)
				{
					pr_Fade.SetCurrentAnimation(0);
					pr_Fade.ResetAnimation();
				}
				bEffect = WPE_FADE;
				
				if(null != pr_Level)
				{
	//   		pr_Mode->pr_Level->Effects.CreatePoints((int)uiPoints,pr_Sprite->GetCenterPos());
	        pr_Level.iPoints += 321;
	        pr_Level.iPhonesCollected++;
				}
			}
		}
		//---------------------------------------------------------------------------- 
		// Name: explode()
		// Desc: explodes a game piece
		// Pams: none
		//---------------------------------------------------------------------------- 
		public void Explode()
		{
			if(null != pr_Sprite)
			{
				pr_Sprite.StopMove();
				ExplodeSpeed.fy	= -(float)RandRange(300,500);
		
				ExplodeSpeed.fx	= (float)RandRange(50,150);
				if((pr_Level.pr_Main.Randomizer.nextInt()%2) == 0)
				{
					ExplodeSpeed.fx	= - ExplodeSpeed.fx;
				}
				ExplodePos.atrib(pr_Sprite.position);
		
				bEffect			=		WPE_EXPLODE;
			}
		}

		//---------------------------------------------------------------------------- 
		// Name: Release()
		// Desc: Releases the class resources
		// Pams: none
		//---------------------------------------------------------------------------- 
		public boolean Release()
		{
/*			pShake 				= null;
			vShake 				= null;
			ExplodePos 		= null;
			ExplodeSpeed 	= null;*/
			
	/*		if(null != pr_Sprite)
			{
				pr_Sprite.Release();
			}
			
			if(null != pr_Fade)
			{
				pr_Fade.Release();
			}*/

			iShakeTime		= 0;
			bShake				= false;
			bFade					= false;
			iIndex				= -1;
			bIsPowerUp		= false;
			byConnections = 0;
			bVisited			= false;
			bUp						= true;
			bIsGoal				= false;
			bEffect				= WPE_NONE;

			return true;
		}
	
}
