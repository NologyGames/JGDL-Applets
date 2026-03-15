
import JGDL.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;

class PopPiece extends JGDLObject
{
	public static final int
			//0 - Surpresa
			WPA_SURPRISE = 0,

			//1 - Parado1
			WPA_STOPED1 = 1,
      
			//2 - Parado2
			WPA_STOPED2 = 2,

			//3 - Parado3
			WPA_STOPED3 = 3,

			//4 - Caiu em cima
			WPA_FALLEDONTOP = 4,

			//5 - Queda1
			WPA_FALLED1 = 5,

			//6 - Queda2
			WPA_FALLED2 = 6,

			//7 - Triste
			WPA_SAD = 7,

			//8 - Gotas Calor1
			WPA_HEATDROPS1 = 8,

			//9 - Gotas Calor2
			WPA_HEATDROPS2 = 9,

			//10 - Sentindo calor 1
			WPA_FELLINGHOT1 = 10,

			//11 - Sentindo Calor 2
			WPA_FELLINGHOT2 = 11,

			//12 - Sentindo Calor 3
			WPA_FELLINGHOT3 = 12,

			//13 - Queimando1
			WPA_BURNING1 = 13,

			//14 - Queimando2
			WPA_BURNING2 = 14,

			//15 - Feliz Esquerdo
			WPA_HAPPYLEFT = 15,

			//16 - Feliz Direito
			WPA_HAPPYRIGHT = 16,

			//17 - Piscar Esquerda
			WPA_BLINKLEFT = 17,

			//18 - Piscar Direita
			WPA_BLINKRIGHT = 18,

			//19 - PiscarBeijar Esquerda
			WPA_BLINKKISSLEFT = 19,

			//20 - PiscarBeijar Direita
			WPA_BLINKKISSRIGHT = 20,

			//21 - Lingua Direita
			WPA_TONGUERIGHT = 21,

			//22 - Lingua Esquerda
			WPA_TONGUELEFT = 22,


			//23 - Voar Esquerda
			WPA_FLYLEFT = 23,

			//24 - Voar Direita
			WPA_FLYRIGHT = 24,

			//25 - Bravo
			WPA_ANGRY1 = 25,

			//26 - Bravo2
			WPA_ANGRY2 = 26,

			//27 - Fumacinha
			WPA_SMOKE1 = 27,

			//28 - Queda
			WPA_FALLING = 28,

			//29 - Caindo em cima
			WPA_FALLINGONTOP = 29;
	public static final int
				WPU_BOMB				= 0,
				WPU_LINEREMOVE	= 1,
				WPU_NONE 				= 2;
				

	public static final int
	
				WPE_NONE 		= 0,
				WPE_EXPLODE = 1;
			
		

	
	public static final int SIZEY = 17;
	public static final int SIZEX = 17;
	

	public JGDLTimeAccumulator AnimationTimer = new JGDLTimeAccumulator();
	
	public JGDLVector Pos 				= new JGDLVector(),
										ExplodePos 	= new JGDLVector(),
										ExplodeSpeed= new JGDLVector();
	
	public boolean bPlayFall  = true;
	public boolean bFalling 	= true;
	public boolean bShake			= false;
	public boolean bVisited		= false;
	public boolean bFear			= false;
	
	public PopBoard p_Board 	= null;
	public int iIndex 				= 0;
	public byte byID					= 0;
	
	
	public int PowerUpType    = WPU_NONE;
	public int Effect 				= WPE_NONE;

	// Sprite
	public JGDLSprite pr_Sprite = null;

	int				 iShakeTime;
	
	JGDLVector vShake = new JGDLVector();
	JGDLVector pShake = new JGDLVector();
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a cena
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		if(null != pr_Sprite)
		{
			pr_Sprite.bVisible = false;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Update()
	// Desc: atualiza a peça
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Update()
	{
	  //updating shake
		if(bShake)
		{
			
			iShakeTime += p_Board.p_Level.pr_Main.TimeHandler.iFrameTime;
			if(iShakeTime >= 120)
			{
				iShakeTime %= 120;
				vShake.fx = (float)Math.cos(p_Board.p_Level.pr_Main.Randomizer.nextDouble()*6.2831853071);
				vShake.fy = (float)Math.sin(p_Board.p_Level.pr_Main.Randomizer.nextDouble()*6.2831853071);
			}
			else
			{
				if(iShakeTime < 60)
				{
					pShake.fx = (vShake.fx*2.0f*(float)iShakeTime)/60.0f;
					pShake.fy = (vShake.fy*2.0f*(float)iShakeTime)/60.0f;
				}
				else
				{
					pShake.fx = (vShake.fx*2.0f*(float)(120 - iShakeTime))/60.0f;
					pShake.fy = (vShake.fy*2.0f*(float)(120 - iShakeTime))/60.0f;
				}
			}
		}
		else
		{
			pShake.fx = pShake.fy = 0;
		}

		switch(Effect)
		{
			case WPE_EXPLODE:
			{
				if(ExplodePos.fy < 350.0f)
				{
					//X
					ExplodePos.fx += ExplodeSpeed.fx*p_Board.p_Level.pr_Main.TimeHandler.fFrameTime;
	
					//Y
					ExplodePos.fy = ExplodePos.fy + (ExplodeSpeed.fy*p_Board.p_Level.pr_Main.TimeHandler.fFrameTime) + ((2450.0f*p_Board.p_Level.pr_Main.TimeHandler.fFrameTime * p_Board.p_Level.pr_Main.TimeHandler.fFrameTime)*0.5f);
					ExplodeSpeed.fy = ExplodeSpeed.fy + (2450.0f * p_Board.p_Level.pr_Main.TimeHandler.fFrameTime);
					pr_Sprite.position.atrib(ExplodePos);
				}
				break;
			}
			
			default:
			{
				pr_Sprite.position.atrib(pShake.fx + Pos.fx + p_Board.Pos.fx, Pos.fy + p_Board.Pos.fy + pShake.fy);
				break;
			}
			
		}

		UpdateAnimation();
		
		
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ExecutePU()
	// Desc: executa o power up
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void ExecutePU()
	{
		switch(PowerUpType)
		{
			case WPU_LINEREMOVE:
			{
				int i;
				int iLine = iIndex/p_Board.MATX;
				
				p_Board.p_Level.p_SndLineRemove.Play();
				JGDLVector Pos = p_Board.p_Level.GetVector();
				int index;
				for(i = 0; i < p_Board.MATX; i++)
				{
					index = (iLine*p_Board.MATX)+i;
					if(p_Board.Matrix[index] != null && p_Board.Matrix[index] != this)
					{
						p_Board.DeletePiece(p_Board.Matrix[index],100);
					}
					Pos.fx = i * SIZEX;
					Pos.fy = iLine * SIZEY;
					Pos.fx += p_Board.Pos.fx + 8.0f;
					Pos.fy += p_Board.Pos.fy + 8.0f;
					p_Board.p_Level.Effects.CreateExplosion(Pos);
				}
				
				int iLinePos = (iLine * SIZEX) + (int)(p_Board.Pos.fy) + (int)(SIZEY*0.5f);

				PopBoard p_OtherBoard = null;
				if(p_Board.p_Level.Boards[0] == p_Board)
				{
					p_OtherBoard = p_Board.p_Level.Boards[1];
				}
				else
				{
					p_OtherBoard = p_Board.p_Level.Boards[0];
				}
				
				int iOtherLine = (iLinePos - (int)p_OtherBoard.Pos.fy)/ SIZEY;
	
				if(iOtherLine >= 0 && iOtherLine < p_OtherBoard.MATY)
				{
					for(i = 0; i < p_OtherBoard.MATX; i++)
					{
						index = (iOtherLine*p_OtherBoard.MATX)+i;
						if(p_OtherBoard.Matrix[index] != null)
						{
							p_OtherBoard.DeletePiece(p_OtherBoard.Matrix[index],100);
						}
						Pos.fx = i * SIZEX;
						Pos.fy = iOtherLine * SIZEY;
						Pos.fx += p_OtherBoard.Pos.fx + 8.0f;
						Pos.fy += p_OtherBoard.Pos.fy + 8.0f;
						p_Board.p_Level.Effects.CreateExplosion(Pos);
					}
				}
				break;
			}
			case WPU_BOMB:
			{
				int iSize = p_Board.MATSIZE;
				
				JGDLVector MyPos = p_Board.p_Level.GetVector();
				MyPos.atrib((iIndex%p_Board.MATX) * SIZEX, (iIndex/p_Board.MATX)*SIZEY);
				
				MyPos.fx += p_Board.Pos.fx;
				MyPos.fy += p_Board.Pos.fy;
				
				p_Board.p_Level.p_SndBomb.Play();
/*				if (g_PopGame.p_Sounds[WPopGame::WPS_PUBOMB])
				{
					g_PopGame.p_Sounds[WPopGame::WPS_PUBOMB]->Play();
				}*/
				JGDLVector Pos = p_Board.p_Level.GetVector();
				
				JGDLVector vDist = p_Board.p_Level.GetVector();
				
				for(int i =0 ; i < iSize; i++)
				{
					Pos.fx = (i%p_Board.MATX)*SIZEX;
					Pos.fy = (i/p_Board.MATX)*SIZEY;
					Pos.fx += p_Board.Pos.fx;
					Pos.fy += p_Board.Pos.fy;
					
					vDist.fx = MyPos.fx - Pos.fx;
					vDist.fy = MyPos.fy - Pos.fy;
					
					if(vDist.Magnitude() <= 35)
					{
						Pos.fx += 8.0f;
						Pos.fy += 8.0f;
						
						p_Board.p_Level.Effects.CreateExplosion(Pos);
						if(p_Board.Matrix[i] != null && p_Board.Matrix[i] != this && p_Board.Matrix[i].Effect == WPE_NONE)
						{
							p_Board.DeletePiece(p_Board.Matrix[i],100);
						}
					}
				}
				p_Board.p_Level.FreeVectors.push_back(Pos);
				p_Board.p_Level.FreeVectors.push_back(MyPos);
				p_Board.p_Level.FreeVectors.push_back(vDist);
				break;
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Explode()
	// Desc: Explode a peça
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Explode()
	{
		if(null != pr_Sprite)
		{
			bShake					= false;
			bFear						= false;
			pr_Sprite.StopMove();
			ExplodeSpeed.fy	= -(float)p_Board.p_Level.RandRange(210,280);
			ExplodeSpeed.fx	= (float)p_Board.p_Level.RandRange(35,105);
			if(p_Board.p_Level.pr_Main.Randomizer.nextInt()%2 == 0)
			{
				ExplodeSpeed.fx	= - ExplodeSpeed.fx;
			}
			ExplodePos.atrib(pr_Sprite.position);
	
			Effect			=		WPE_EXPLODE;
			int iRand = Math.abs(p_Board.p_Level.pr_Main.Randomizer.nextInt());
			if(PowerUpType == WPU_NONE)
			{
				pr_Sprite.SetCurrentAnimation((iRand%2 == 0)? WPA_BURNING1 : WPA_BURNING2);
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Exploded()
	// Desc: retorna true se a peça já explodiu
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Exploded()
	{
		return (Effect == WPE_EXPLODE && ExplodePos.fy >= 350.0f);
	}

	//---------------------------------------------------------------------------- 
	// Name: GetClone()
	// Desc: retorna um clone da peça atual
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	PopPiece GetClone()
	{
		PopPiece pr_Piece = null;
		int iSize = p_Board.p_Level.FreePieces.size();
		if(iSize > 0)
		{
			pr_Piece = (PopPiece)p_Board.p_Level.FreePieces.get(iSize-1);
			p_Board.p_Level.FreePieces.remove(iSize -1);
		}
		else
		{
			pr_Piece = new PopPiece();			
		}
		if(pr_Sprite != null)
		{
			if(pr_Piece.pr_Sprite != null)
			{
				pr_Sprite.GetClone(pr_Piece.pr_Sprite);
			}
			else
			{
				iSize = p_Board.p_Level.FreeSprites.size();
				if(iSize > 0)
				{
					pr_Piece.pr_Sprite = (JGDLSprite)p_Board.p_Level.FreeSprites.get(iSize-1);
					p_Board.p_Level.FreeSprites.remove(iSize-1);
					pr_Sprite.GetClone(pr_Piece.pr_Sprite);
				}
				else
				{
					pr_Piece.pr_Sprite = pr_Sprite.GetClone(true);
				}
			}
		}
		else
		{
			pr_Piece.pr_Sprite = null;
		}
		if(null != pr_Sprite)
		{
			pr_Sprite.bVisible = false;
		}
		
		pr_Piece.PowerUpType	= PowerUpType;
		pr_Piece.Effect				= WPE_NONE;
		pr_Piece.byID 				= byID;
		pr_Piece.bVisited			= false;
		pr_Piece.bFalling 		= true;
		pr_Piece.bShake   		= false;
		pr_Piece.bFear				= false;
		pr_Piece.bPlayFall 		= true;
		return pr_Piece;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: SameID(PopPiece p_Piece)
	// Desc: retorna true se as duas peças tem o mesmo ID
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean SameID(PopPiece p_Piece)
	{
		return (p_Piece.byID == byID);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: UpdateAnimation()
	// Desc: atualiza a animação das peças
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void UpdateAnimation()
	{
		if(PowerUpType == WPU_NONE)
		{
			int iMatPosX = iIndex%p_Board.MATX;
			int iMatPosY = iIndex/p_Board.MATX;
			
			int iBoardPosX = iMatPosX * SIZEX;
			int iBoardPosY = iMatPosY * SIZEY;
			
			if(bFear)
			{
				AnimationTimer.Update();
				if(AnimationTimer.Ended())
				{
					CheckHeatAnimation();
				}
				return;
			}
			switch(Effect)
			{
				case WPE_NONE:
				{
					if(bFalling || ((float)iBoardPosY) > Pos.fy)
					{
						pr_Sprite.SetCurrentAnimation(WPA_FALLING);
						SetBottomPieceAnim(WPA_FALLINGONTOP);
					}
					else
					{
						if(pr_Sprite.IsMouseOver())
						{
							pr_Sprite.SetCurrentAnimation(WPA_SURPRISE);
						}
						else
						{
							switch(pr_Sprite.iCurrentAnim)
							{
								case WPA_SURPRISE:
								{
									CheckStopedAnimation();
									break;
								}
	
								case WPA_FALLING:
								{
									int iRand = Math.abs(p_Board.p_Level.pr_Main.Randomizer.nextInt());
									int iAnim = (iRand%2 == 1)? WPA_FALLED1 : WPA_FALLED2;
									if(p_Board.p_Level.p_PopGround != null && bPlayFall)
									{
										bPlayFall = false;
										p_Board.p_Level.p_PopGround.Play();
									}
									pr_Sprite.SetCurrentAnimation(iAnim);
									SetBottomPieceAnim(WPA_FALLEDONTOP);
									break;
								}
								case WPA_FALLINGONTOP:
								{
	
									if(iMatPosY > 0 && p_Board.Matrix[iIndex - p_Board.MATX] != null)
									{
										if(p_Board.Matrix[iIndex - p_Board.MATX].pr_Sprite.iCurrentAnim != WPA_FALLING)
										{
											pr_Sprite.SetCurrentAnimation(WPA_FALLEDONTOP);
										}
									}
									break;
								}
								case WPA_FLYLEFT:
								case WPA_FLYRIGHT:
								{
									CheckStopedAnimation();
									break;
								}
	
								case WPA_FALLEDONTOP:
								case WPA_FALLED2:
								case WPA_FALLED1:
								{
									if(pr_Sprite.EndedAnimation())
									{
										CheckStopedAnimation();
									}
									break;
								}
								case WPA_FELLINGHOT1:
								case WPA_FELLINGHOT2:
								case WPA_FELLINGHOT3:
								case WPA_HEATDROPS1:
								case WPA_HEATDROPS2:
								{
									if(Pos.fy + p_Board.Pos.fy < 283)
									{
										CheckStopedAnimation();
										break;
									}
									//Else go to default
								}
	
								default:
								{
									AnimationTimer.Update();
									if(AnimationTimer.Ended())
									{
										if(/*pr_Board->pr_Mode->bEnableFire &&*/ Pos.fy + p_Board.Pos.fy >= 283)
										{
											CheckHeatAnimation();
										}
										else
										{
											CheckStopedAnimation();
										}
									}
									break;
								}
							}
						}
					}
					break;
				}
			}
		}
/*		else
		{
			pr_Sprite.SetCurrentAnimation(0);
		}*/
	}
	//---------------------------------------------------------------------------- 
	// Name: GetLeftPiece()
	// Desc: retorna a peça a esquerda desta
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	PopPiece GetLeftPiece()
	{
		return (iIndex % p_Board.MATX > 0)? p_Board.Matrix[iIndex-1] : null;
	}

	//---------------------------------------------------------------------------- 
	// Name: GetRightPiece()
	// Desc: retorna a peça a direita desta
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	PopPiece GetRightPiece()
	{
		return (iIndex % p_Board.MATX < p_Board.MATX - 1)? p_Board.Matrix[iIndex+1] : null;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CheckStoppedAnimaton()
	// Desc: Verifica que animação de parado utilizar
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void CheckStopedAnimation()
	{
		if(PowerUpType == WPU_NONE)
		{
			int iRand = Math.abs(p_Board.p_Level.pr_Main.Randomizer.nextInt());
			PopPiece p_Left  = GetLeftPiece();
			PopPiece p_Right = GetRightPiece();
			int iSides = 0;
			if(p_Left != null)
			{
				if(p_Left.SameID(this))
				{
					iSides |= 1;
				}
				else
				{
					iSides |=2;
				}
			}
			if(p_Right != null)
			{
				if(p_Right.SameID(this))
				{
					iSides |= 4;
				}
				else
				{
					iSides |= 8;
				}
			}
			switch(iSides)
			{
				//no neighboards
				case 0:
				{
					switch(iRand % 6)
					{
						case 0:	pr_Sprite.SetCurrentAnimation(WPA_STOPED1); break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_STOPED2); break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_STOPED3); break;
						case 3: pr_Sprite.SetCurrentAnimation(WPA_ANGRY1); break;
						case 4: pr_Sprite.SetCurrentAnimation(WPA_ANGRY2); break;
						case 5: pr_Sprite.SetCurrentAnimation(WPA_SAD); break;
					}
					break;
				}
				//neighboard on left same ID
				case 1:
				{
					switch(iRand % 6)
					{
						case 0:	pr_Sprite.SetCurrentAnimation(WPA_STOPED1); break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_STOPED2); break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_STOPED3); break;
						case 3: pr_Sprite.SetCurrentAnimation(WPA_BLINKLEFT); break;
						case 4: pr_Sprite.SetCurrentAnimation(WPA_BLINKKISSLEFT); break;
						case 5: pr_Sprite.SetCurrentAnimation(WPA_HAPPYLEFT); break;
					}
					break;
				}
				//neighboard on left diferent ID
				case 2:
				{
					switch(iRand % 5)
					{
						case 0:	pr_Sprite.SetCurrentAnimation(WPA_STOPED1); break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_STOPED2); break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_STOPED3); break;
						case 3: pr_Sprite.SetCurrentAnimation(WPA_HAPPYLEFT); break;
						case 4: pr_Sprite.SetCurrentAnimation(WPA_TONGUELEFT); break;
					}
					break;
				}
				//neighboard on right same ID
				case 4:
				{
					switch(iRand % 6)
					{
						case 0:	pr_Sprite.SetCurrentAnimation(WPA_STOPED1);				break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_STOPED2);				break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_STOPED3);				break;
						case 3: pr_Sprite.SetCurrentAnimation(WPA_BLINKRIGHT);			break;
						case 4: pr_Sprite.SetCurrentAnimation(WPA_BLINKKISSRIGHT); break;
						case 5: pr_Sprite.SetCurrentAnimation(WPA_HAPPYRIGHT);		break;
					}
					break;
				}
				//neighboard on right same ID & neighboard on left same ID
				case 5:
				{
					switch(iRand % 9)
					{
						case 0:	pr_Sprite.SetCurrentAnimation(WPA_STOPED1);				break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_STOPED2);				break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_STOPED3);				break;
						case 3: pr_Sprite.SetCurrentAnimation(WPA_BLINKRIGHT);			break;
						case 4: pr_Sprite.SetCurrentAnimation(WPA_BLINKKISSRIGHT); break;
						case 5: pr_Sprite.SetCurrentAnimation(WPA_HAPPYRIGHT);		break;
						case 6: pr_Sprite.SetCurrentAnimation(WPA_BLINKLEFT);			break;
						case 7: pr_Sprite.SetCurrentAnimation(WPA_BLINKKISSLEFT); break;
						case 8: pr_Sprite.SetCurrentAnimation(WPA_HAPPYLEFT);		break;
					}
					break;
				}
				//neighboard on right same ID & neighboard on left dif ID
				case 6:
				{
					switch(iRand % 8)
					{
						case 0:	pr_Sprite.SetCurrentAnimation(WPA_STOPED1);				break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_STOPED2);				break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_STOPED3);				break;
						case 3: pr_Sprite.SetCurrentAnimation(WPA_BLINKRIGHT);			break;
						case 4: pr_Sprite.SetCurrentAnimation(WPA_BLINKKISSRIGHT); break;
						case 5: pr_Sprite.SetCurrentAnimation(WPA_HAPPYRIGHT);		break;
						case 6: pr_Sprite.SetCurrentAnimation(WPA_TONGUELEFT);			break;
						case 7: pr_Sprite.SetCurrentAnimation(WPA_HAPPYLEFT);		break;
					}
					break;
				}
				//neighboard on right dif ID 
				case 8:
				{
					switch(iRand % 5)
					{
						case 0:	pr_Sprite.SetCurrentAnimation(WPA_STOPED1);				break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_STOPED2);				break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_STOPED3);				break;
						case 3: pr_Sprite.SetCurrentAnimation(WPA_TONGUERIGHT);			break;
						case 4: pr_Sprite.SetCurrentAnimation(WPA_HAPPYRIGHT);			break;
					}
					break;
				}
				//neighboard on right dif ID & neighboard on left same ID
				case 9:
				{
					switch(iRand % 8)
					{
						case 0:	pr_Sprite.SetCurrentAnimation(WPA_STOPED1);				break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_STOPED2);				break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_STOPED3);				break;
						case 3: pr_Sprite.SetCurrentAnimation(WPA_BLINKLEFT);			break;
						case 4: pr_Sprite.SetCurrentAnimation(WPA_BLINKKISSLEFT);	break;
						case 5: pr_Sprite.SetCurrentAnimation(WPA_HAPPYLEFT);			break;
						case 6: pr_Sprite.SetCurrentAnimation(WPA_TONGUERIGHT);		break;
						case 7: pr_Sprite.SetCurrentAnimation(WPA_HAPPYRIGHT);			break;
					}
					break;
				}
				//neighboard on right dif ID & neighboard on left dif ID
				case 10:
				{
					switch(iRand % 7)
					{
						case 0:	pr_Sprite.SetCurrentAnimation(WPA_STOPED1);				break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_STOPED2);				break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_STOPED3);				break;
						case 3: pr_Sprite.SetCurrentAnimation(WPA_TONGUELEFT);		break;
						case 4: pr_Sprite.SetCurrentAnimation(WPA_HAPPYLEFT);			break;
						case 5: pr_Sprite.SetCurrentAnimation(WPA_TONGUERIGHT);		break;
						case 6: pr_Sprite.SetCurrentAnimation(WPA_HAPPYRIGHT);			break;
					}
					break;
				}
			}
			AnimationTimer.Init(p_Board.p_Level.pr_Main, 800 + (Math.abs(p_Board.p_Level.pr_Main.Randomizer.nextInt())%1000));
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CheckHeatAnimation()
	// Desc: Verifica que animação de calor utilizar
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void CheckHeatAnimation()
	{
		if(PowerUpType == WPU_NONE)
		{
			if(Pos.fy + p_Board.Pos.fy >= 308 || bFear)
			{
				int iRand = Math.abs(p_Board.p_Level.pr_Main.Randomizer.nextInt());
				switch(iRand % 2)
				{
					case 0: pr_Sprite.SetCurrentAnimation(WPA_HEATDROPS1);	break;
					case 1: pr_Sprite.SetCurrentAnimation(WPA_HEATDROPS2);	break;
				}
			}
			else
			{
				if(Pos.fy + p_Board.Pos.fy >= 283)
				{
					int iRand = Math.abs(p_Board.p_Level.pr_Main.Randomizer.nextInt());
					switch(iRand % 3)
					{
						case 0: pr_Sprite.SetCurrentAnimation(WPA_FELLINGHOT1);	break;
						case 1: pr_Sprite.SetCurrentAnimation(WPA_FELLINGHOT2);	break;
						case 2: pr_Sprite.SetCurrentAnimation(WPA_FELLINGHOT3);	break;
					}
				}
			}
			AnimationTimer.Init(p_Board.p_Level.pr_Main, 400 + (Math.abs(p_Board.p_Level.pr_Main.Randomizer.nextInt())%700));
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: SetBottomPieceAnim(int iAnim)
	// Desc: Seta a animação da peça abaixo desta
	// Pams: indice da animação
	//---------------------------------------------------------------------------- 
	public void SetBottomPieceAnim(int iAnim)
	{
		if(!p_Board.IsInRemoveList(this))
		{
			int index = 0;
	
			for(int iy = (iIndex/p_Board.MATX) + 1; iy < p_Board.MATY; iy++)
			{
				index = (iy*p_Board.MATX) + iIndex%p_Board.MATX;
				if(p_Board.Matrix[index] != null && p_Board.Matrix[index].pr_Sprite.iCurrentAnim != WPA_FALLING && p_Board.Matrix[index].PowerUpType == WPU_NONE)
				{
					p_Board.Matrix[index].pr_Sprite.SetCurrentAnimation((p_Board.Matrix[index].PowerUpType == WPU_NONE)? iAnim : 0);
					break;
				}
			}
		}
	}
	
}
