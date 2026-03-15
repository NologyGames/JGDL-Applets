

import JGDL.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;

class PopLevel extends JGDLScene
{
	
	public class puConfig
	{
		int 	iFirst;
		int 	iInserts;
		int		iType;
	}
	
	int iLinesCreated = 0;
	int iPuConfigs = 0;
	
	puConfig [] PUConfigs = new puConfig[2];

	
	//Estados do jogo
	public static final int WLS_GAME 			= 1,
													WLS_LEVELUP 	= 2,
													WLS_INITGAME 	= 3,
													WLS_PREGAME 	= 4,
													WLS_MENUTAG		= 5,
													WLS_GAMEOVER  = 6,
													WLS_INITLEVEL	= 7,
													WLS_NONE			= 8,
													WLS_MAIN			= 9,
													WLS_CONGRATS	= 10;

	
	
	public static final float WBOARD1X = 21.0f;
	public static final float WBOARD2X = 308.0f;
	public static final float WBOARDY	 = 77.0f;


	//Last Pop Sound
	public int iLastPopSnd;	
	
	public int iPoints;
	public int iLoadLines;
	public JGDLTimeAccumulator LoadLinesTimer = new JGDLTimeAccumulator(),
														 NewLineTimer   = new JGDLTimeAccumulator();
	
	public JGDLList   FreePieces	= new JGDLList();
	public JGDLList   FreeSprites = new JGDLList();
	public JGDLList		FreeVectors = new JGDLList();
	
	public JGDLVector TempVector = new JGDLVector();
	public JGDLSprite p_BackGround,
										p_SensorBack,
										p_SensorPoint,
										p_PopsBar,
										p_Balls,
										p_BkgMenu,
										p_BtnOpenMenu,
										p_BtnDownload,
										p_BtnNoSound,
										p_PopupFrame,
										p_PopupTitle,
										p_PopupDownload,
										p_PopupNextLevel,
										p_PopupPlayAgain,
										p_MainBkg,
										p_MainClickHere,
										p_Congrats;
	
	boolean bIsMenuOpen = false;
	
	public JGDLSprite [] Flames  = new JGDLSprite[18];
	
	public PopPiece 	 [] Pieces = new PopPiece[4];
	public PopPiece		 [] PowerUps = new PopPiece[2];
	public PopBoard	 [] Boards = new PopBoard[2];
	
	JGDLSound		 	p_PopGround 			=	null;
	JGDLSound		 	p_NewLine	 				= null;
	JGDLSound [] 	p_PopSounds 			= new JGDLSound [6];
	JGDLSound 		p_SndBomb 				= null;
	JGDLSound 		p_SndLineRemove		= null;


	//fonte corrente
	Font Arial 		= new Font("Arial",Font.BOLD,12);
	Font SmallRed = new Font("Arial",Font.PLAIN,9);
	Color DarkRed = new Color(127,0,0);

	public PopEffectManager Effects = new PopEffectManager();
	//Estado do jogo
	public int 			iState = WLS_MAIN;
	
	//Fase atual
	public int			iCurrentLevel;
	public int			iCornsToFind;
	public int			iFoundCorns;	
	
	//Flexibilidade da balança
	public float			 fFlexibility = 0.0f;
	
	//---------------------------------------------------------------------------- 
	// Name: Execute()
	// Desc: Executa a cena. Essa rotina deve ser reescrita na cena, para que se possa
	//		   fazer a execução da cena.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Execute()
	{
		p_MainClickHere.position.fx = p_MainBkg.position.fx + 228;
		p_MainClickHere.position.fy = p_MainBkg.position.fy + 200;

		switch(iState)
		{
			case WLS_MAIN:
			{
				if(p_MainClickHere.IsMouseOver())
				{
					p_MainClickHere.SetCurrentAnimation(1);
				}
				else
				{
					p_MainClickHere.SetCurrentAnimation(0);
				}

				if(p_MainClickHere.Clicked(0))
				{
					InitLevel();
					p_PopupFrame.MoveTo(p_PopupFrame.position.fx,337,400);
					ChangeState(WLS_PREGAME);

					for(int i = 0; i < 18; i++)
					{
						Flames[i].SetCurrentAnimation(0);
						Flames[i].GetCurrentAnimationPointer().uiTimeAccum = Math.abs(pr_Main.Randomizer.nextInt())%200;
					}
					p_MainBkg.MoveTo(0,-337,400);
				}
				break;
			}
			case WLS_PREGAME:
			{
				LoadLinesTimer.Update();
				if(LoadLinesTimer.Ended())
				{
					LoadLinesTimer.Restart();
					if(!Boards[0].CreateLine() || !Boards[1].CreateLine())
					{
						ChangeState(WLS_GAMEOVER);
						break;
					}
					else
					{
						CheckPowerUp();
					}
					if(null != p_NewLine)
					{
						p_NewLine.Play();
					}
					
					iLoadLines--;
					if(iLoadLines <= 0)
					{
						iState = WLS_GAME;
					}
				}
				for(int i = 0 ; i < 18; i++)
				{
					if(Flames[i].iCurrentAnim == 0 && Flames[i].EndedAnimation())
					{
						Flames[i].SetCurrentAnimation(1);
						Flames[i].GetCurrentAnimationPointer().uiTimeAccum = Math.abs(pr_Main.Randomizer.nextInt());
					}
				}
				UpdateSensor();
				UpdateBoards();
				Effects.Update();
				
				break;
			}
			case WLS_GAME:
			{
				NewLineTimer.Update();
				if(Boards[0].p_PanTop.Clicked(0) || Boards[1].p_PanTop.Clicked(0))
				{
					NewLineTimer.iTimeAccum = NewLineTimer.iTimeLimit;
				}
				if(NewLineTimer.Ended() || pr_Main.InputManager.MouBtnPressed(1))
				{
					NewLineTimer.Restart();
					if(!Boards[0].CreateLine() || !Boards[1].CreateLine())
					{
						ChangeState(WLS_GAMEOVER);
						break;
					}
					else
					{
						CheckPowerUp();
					}
				}
				
				UpdateSensor();
				for(int i = 0 ; i < 18; i++)
				{
					if(Flames[i].iCurrentAnim == 0 && Flames[i].EndedAnimation())
					{
						Flames[i].SetCurrentAnimation(1);
						Flames[i].GetCurrentAnimationPointer().uiTimeAccum = Math.abs(pr_Main.Randomizer.nextInt());
					}
				}
				UpdateBoards();
				Effects.Update();
				HandleMenu();
				
				/*int iNextLevel = -2;
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_0))
				{
					iNextLevel = -1;
				}
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_1))
				{
					iNextLevel = 0;
				}
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_2))
				{
					iNextLevel = 1;
				}
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_3))
				{
					iNextLevel = 2;
				}
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_4))
				{
					iNextLevel = 3;
				}
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_5))
				{
					iNextLevel = 4;
				}
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_6))
				{
					iNextLevel = 5;
				}
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_7))
				{
					iNextLevel = 6;
				}
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_8))
				{
					iNextLevel = 7;
				}
				if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_9))
				{
					iNextLevel = 8;
				}
				if((iFoundCorns >= iCornsToFind) || (iNextLevel != -2))
				{	
					iCurrentLevel = (iNextLevel != -2) ? iNextLevel : iCurrentLevel;				
					ChangeState(WLS_LEVELUP);
				}*/
				if(iFoundCorns >= iCornsToFind)
				{	
					ChangeState(WLS_LEVELUP);
				}
				
				CheckBoardsDiference();
				break;
			}
			case WLS_MENUTAG:
			{
				HandleMenu();
				break;
			}
			
		
			case WLS_LEVELUP:
			{
				Boards[0].HandleLevelUp();
				Boards[1].HandleLevelUp();
				if(Boards[0].iPieces == 0 && Boards[1].iPieces == 0 &&
				   Boards[0].Pos.fy == WBOARDY && Boards[1].Pos.fy == WBOARDY)
				{
					ChangeState((iCurrentLevel < 9)? WLS_INITLEVEL : WLS_CONGRATS);
				}
				Effects.Update();
				break;
			}

			case WLS_GAMEOVER:
			{
				Boards[0].HandleLevelUp();
				Boards[1].HandleLevelUp();
				if(Boards[0].iPieces == 0 && Boards[1].iPieces == 0 &&
				   Boards[0].Pos.fy == WBOARDY && Boards[1].Pos.fy == WBOARDY && 
				   p_PopupFrame.position.fy == 337.0f)
				{
					p_PopupFrame.MoveTo(p_PopupFrame.position.fx,72,400);
				}
				
				Effects.Update();
				break;
			}
			
			case WLS_CONGRATS:
			case WLS_INITLEVEL:
			{
				Effects.Update();
				break;
			}
			
		}
		
		HandlePopUp();


	}
	
	//---------------------------------------------------------------------------- 
	// Name: Draw()
	// Desc: Seria usada para pintar a cane mas está sendo utilizada para tratar o pause
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Draw()
	{
		super.Draw();
		
		DrawScore();
		if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_P)/* || p_Pause.Clicked(0)*/)
		{
			if(pr_Main.IsPaused())
			{
				pr_Main.Resume();
			}
			else
			{
				pr_Main.Pause();
			}
		}
		else
		{
			if(pr_Main.IsPaused() && pr_Main.InputManager.MouBtnPressed(0))
			{
				pr_Main.Resume();
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetVetctor()
	// Desc: returna um vetor
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLVector GetVector()
	{
		JGDLVector p_Ret;
		int iSize = FreeVectors.size();
		if(iSize > 0)
		{
			p_Ret = (JGDLVector)FreeVectors.get(iSize-1);
			FreeVectors.remove(iSize-1);
		}
		else
		{
			p_Ret = new JGDLVector();
		}
		return p_Ret;
	}

	//---------------------------------------------------------------------------- 
	// Name: GetRandomPiece()
	// Desc: retorna uma peça aleatória
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	PopPiece GetRandomPiece()
	{
		int iRand = pr_Main.Randomizer.nextInt()%4;		
		iRand = (iRand < 0)? -iRand : iRand;
//		System.out.println(iRand);
		return Pieces[iRand].GetClone();
	}
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: Inicializa a cena. Essa rotina deve ser reescrita na cena, para que se possa
	//		   criar todos os objetos e fazer inicializações necessárias.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Initialize()
	{
		int i;
		int [] Anim = new int[1];
		
		//background layer
		JGDLLayer p_LayerBKG, p_BoardsFront = null;
		
		TempVector.atrib(448,336);
		//adicionando fundo
		p_LayerBKG 						= CreateLayer(TempVector);
		p_BoardsFront					= CreateLayer(TempVector);

		p_BackGround 					= p_LayerBKG.CreateSprite("bkg_Movies.gif",new JGDLVector(448,336));
		
		JGDLSprite p_Stove1		= p_LayerBKG.CreateSprite("spr_Stove.gif", new JGDLVector(140,16));
		p_Stove1.position.fx 	= 10.0f;
		p_Stove1.position.fy	= 320.0f; 

		int [] FireAnim1 = new int[8];
		FireAnim1[0] = 0; FireAnim1[1] = 1; FireAnim1[2] = 2; FireAnim1[3] = 3;
		FireAnim1[4] = 4; FireAnim1[5] = 5; FireAnim1[6] = 6; FireAnim1[7] = 7;
		
		
		
		int [] FireAnim2 = new int[9];
		FireAnim2[0] = 7; FireAnim2[1] = 6; FireAnim2[2] = 5; FireAnim2[3] = 4;
		FireAnim2[4] = 3; FireAnim2[5] = 2; FireAnim2[6] = 1; FireAnim2[7] = 0;
		FireAnim2[8] = -1;
		
		int [] FireAnim3 = new int[3];
		FireAnim3[0] = 5; FireAnim3[1] = 6; FireAnim3[2] = 7;
		
		for(i = 0 ; i < 9; i++)
		{
			Flames[i] = p_LayerBKG.CreateSprite("spr_StoveFire.gif", new JGDLVector(14,21));
			Flames[i].AddAnimation(33,false, FireAnim1);
			Flames[i].AddAnimation(16,true , FireAnim3);
			Flames[i].AddAnimation(33,false, FireAnim2);
			Flames[i].position.fx = 13.0f + ((float)i*14.7f);
			Flames[i].position.fy	= 313.0f;
			Flames[i].SetCurrentAnimation(1);
			Flames[i].GetCurrentAnimationPointer().uiTimeAccum = Math.abs(pr_Main.Randomizer.nextInt());
			Flames[i].SetCurrentAnimation(0);
			Flames[i].GetCurrentAnimationPointer().uiTimeAccum = Math.abs(pr_Main.Randomizer.nextInt())%200;
		}

		JGDLSprite p_Stove2		= p_LayerBKG.CreateSprite("spr_Stove.gif", new JGDLVector(140,16));
		p_Stove2.position.fx 	= 298.0f;
		p_Stove2.position.fy	= 320.0f; 

		for(i = 9 ; i < 18; i++)
		{
			Flames[i] = p_LayerBKG.CreateSprite("spr_StoveFire.gif", new JGDLVector(14,21));
			Flames[i].AddAnimation(33,false, FireAnim1);
			Flames[i].AddAnimation(16,true , FireAnim3);
			Flames[i].AddAnimation(33,false, FireAnim2);
			Flames[i].position.fx = 302.0f + ((float)(i-9)*14.7f);
			Flames[i].position.fy	= 313.0f;
			Flames[i].SetCurrentAnimation(1);
			Flames[i].GetCurrentAnimationPointer().uiTimeAccum = Math.abs(pr_Main.Randomizer.nextInt());
			Flames[i].SetCurrentAnimation(0);
			Flames[i].GetCurrentAnimationPointer().uiTimeAccum = Math.abs(pr_Main.Randomizer.nextInt())%30;
		}
		
		
		Boards[0] 									= new PopBoard();
		Boards[0].p_Level						= this;
		Boards[0].p_Pan 						= p_LayerBKG.CreateSprite("spr_pan.gif",new JGDLVector(129,213));
		Boards[0].p_PanTop					= p_BoardsFront.CreateSprite("spr_pan.gif",new JGDLVector(129,213));
		Boards[0].p_PanTop.window.fy= 20.0f;
		Boards[0].p_Shake						= p_BoardsFront.CreateSprite("spr_alavanca.gif",new JGDLVector(124,41));
		
		int [] ShakeAnim = new int[9];
		ShakeAnim[0] = 0; ShakeAnim[1] = 2; ShakeAnim[2] = 4; ShakeAnim[3] = 6; 
		ShakeAnim[4] = 8; ShakeAnim[5] = 1; ShakeAnim[6] = 3; ShakeAnim[7] = 5;
		ShakeAnim[8] = 7;
		
		Boards[0].p_Shake.AddAnimation(20,true,ShakeAnim);
		Boards[0].p_Shake.SetCurrentAnimation(0);
		Boards[0].p_Shake.bFreezed = true;
		
		
		
		Boards[1] 									= new PopBoard();
		Boards[1].p_Level 					= this;
		Boards[1].p_Pan 						= p_LayerBKG.CreateSprite("spr_pan.gif",new JGDLVector(129,213));
		Boards[1].p_PanTop					= p_BoardsFront.CreateSprite("spr_pan.gif",new JGDLVector(129,213));
		Boards[1].p_PanTop.window.fy= 20.0f;
		Boards[1].p_Shake						= p_BoardsFront.CreateSprite("spr_alavanca.gif",new JGDLVector(124,41));
		ShakeAnim[0] = 0; ShakeAnim[1] = 7; ShakeAnim[2] = 5; ShakeAnim[3] = 3; 
		ShakeAnim[4] = 1; ShakeAnim[5] = 8; ShakeAnim[6] = 6; ShakeAnim[7] = 4;
		ShakeAnim[8] = 2;
		Boards[1].p_Shake.AddAnimation(20,true,ShakeAnim);
		Boards[1].p_Shake.SetCurrentAnimation(0);
		Boards[1].p_Shake.bFreezed = true;

		p_Balls = p_BoardsFront.CreateSprite("spr_colorlights.gif", new JGDLVector(6,6));
		for(i = 0; i < 6; i++)
		{
			Anim[0] = i;
			p_Balls.AddAnimation(1,false,Anim);
		}
		p_Balls.bVisible = false;

		Boards[1].InitializeBalls();
		Boards[0].InitializeBalls();

		//Game Pieces
		Pieces[0] 						= new PopPiece();
		Pieces[0].p_Board			= Boards[0];
		Pieces[0].byID				= 1;
		Pieces[0].pr_Sprite		= p_LayerBKG.CreateSprite("inp_RedCorn.gif",new JGDLVector(17,17));
		Pieces[0].pr_Sprite.bVisible = false;
		Pieces[0].pr_Sprite.position.atrib(0,0);
		
		Pieces[1] 						= new PopPiece();
		Pieces[1].p_Board			= Boards[0];
		Pieces[1].byID				= 2;
		Pieces[1].pr_Sprite		= p_LayerBKG.CreateSprite("inp_DarkGreenCorn.gif",new JGDLVector(17,17));
		Pieces[1].pr_Sprite.bVisible = false;
		Pieces[1].pr_Sprite.position.atrib(17,0);

		Pieces[2] 						= new PopPiece();
		Pieces[2].p_Board			= Boards[0];
		Pieces[2].byID				= 3;
		Pieces[2].pr_Sprite		= p_LayerBKG.CreateSprite("inp_BlueCorn.gif",new JGDLVector(17,17));
		Pieces[2].pr_Sprite.bVisible = false;
		Pieces[2].pr_Sprite.position.atrib(34,0);

		Pieces[3] 						= new PopPiece();
		Pieces[3].p_Board			= Boards[0];
		Pieces[3].byID				= 4;
		Pieces[3].pr_Sprite		= p_LayerBKG.CreateSprite("inp_YellowCorn.gif",new JGDLVector(17,17));
		Pieces[3].pr_Sprite.bVisible = false;
		Pieces[3].pr_Sprite.position.atrib(51,0);

		PowerUps[0]											= new PopPiece();
		PowerUps[0].p_Board							= Boards[0];
		PowerUps[0].byID								= 125;
		PowerUps[0].pr_Sprite						= p_LayerBKG.CreateSprite("inp_DarkGreenCorn.gif",new JGDLVector(17,17));
		Anim[0] = 56;
		PowerUps[0].pr_Sprite.AddAnimation(10,false,Anim);
		PowerUps[0].pr_Sprite.SetCurrentAnimation(0);
		PowerUps[0].pr_Sprite.bVisible = false;
		PowerUps[0].PowerUpType = PopPiece.WPU_BOMB;
		
		
		PowerUps[1]											= new PopPiece();
		PowerUps[1].p_Board							= Boards[0];
		PowerUps[1].byID								= 125;
		PowerUps[1].pr_Sprite						= p_LayerBKG.CreateSprite("inp_DarkGreenCorn.gif",new JGDLVector(17,17));
		Anim[0] = 75;
		PowerUps[1].pr_Sprite.AddAnimation(10,false,Anim);
		PowerUps[1].pr_Sprite.SetCurrentAnimation(0);
		PowerUps[1].pr_Sprite.position.fx = 17;
		PowerUps[1].pr_Sprite.bVisible = false;
		PowerUps[1].PowerUpType = PopPiece.WPU_LINEREMOVE;
		
		//PowerUps[0].pr_Sprite.bVisible	= false;
		
		AddPiecesAnimations();

		JGDLSprite p_LayInfo = p_LayerBKG.CreateSprite("lay_info.gif",new JGDLVector(127,166));
		p_LayInfo.position.fx = 161.0f;
		p_LayInfo.position.fy = 0.0f;
		
	
		Effects.p_Level = this;
		Effects.Initialize();
		pr_Main.TimeHandler.Reset();
		
		p_SensorBack = p_LayerBKG.CreateSprite("lay_sensorback.gif",new JGDLVector(79,51));
		p_SensorBack.position.fx = 184.0f;
		p_SensorBack.position.fy = 105.0f;
		for(i = 0; i < 4; i++)
		{
			Anim[0] = i;
			p_SensorBack.AddAnimation(1,false,Anim);
		}
		p_SensorBack.SetCurrentAnimation(1);

		p_SensorPoint = p_LayerBKG.CreateSprite("inp_SensorDisplay.gif",new JGDLVector(44,19));
		p_SensorPoint.position.fx = 202.0f;
		p_SensorPoint.position.fy = 117.0f;

		for(i = 0; i < 50 ; i++)
		{
			Anim[0] = i;
			p_SensorPoint.AddAnimation(10,true,Anim);
		}
		p_SensorPoint.SetCurrentAnimation(0);
		
		
		p_PopsBar = p_LayerBKG.CreateSprite("spr_PopsBar.gif",new JGDLVector(80,16));
		Anim[0] = 0;
		p_PopsBar.AddAnimation(1,false,Anim);
		Anim[0] = 1;
		p_PopsBar.AddAnimation(1,false,Anim);
		p_PopsBar.position.fx = 184.0f;
		p_PopsBar.position.fy = 60.0f;
		p_PopsBar.SetCurrentAnimation(1);
		
	
		iCurrentLevel	= 0;
		
		p_PopSounds[0] = pr_Main.SoundManager.LoadSound("sfx_PopCorn1.au");		
		p_PopSounds[1] = pr_Main.SoundManager.LoadSound("sfx_PopCorn2.au");		
		p_PopSounds[2] = pr_Main.SoundManager.LoadSound("sfx_PopCorn3.au");		
		p_PopSounds[3] = pr_Main.SoundManager.LoadSound("sfx_PopCorn4.au");		
		p_PopSounds[4] = pr_Main.SoundManager.LoadSound("sfx_PopCorn5.au");		
		p_PopSounds[5] = pr_Main.SoundManager.LoadSound("sfx_PopCorn6.au");		
		p_PopGround		 = pr_Main.SoundManager.LoadSound("sfx_PopGround.au");		
		p_NewLine			 = pr_Main.SoundManager.LoadSound("sfx_MenuMove.au");		
		p_SndBomb			 = pr_Main.SoundManager.LoadSound("sfx_PUBomb.au");
		p_SndLineRemove= pr_Main.SoundManager.LoadSound("sfx_PULine.au");
		
		InitializeMenu();		

		InitializePopup();
		
		TempVector.atrib(448,336);
		JGDLLayer MainLayer = CreateLayer(TempVector);

		p_MainBkg = MainLayer.CreateSprite("spr_MainScreen.gif", new JGDLVector(448,336));
		
		p_MainClickHere = MainLayer.CreateSprite("btn_menubuttons.gif", new JGDLVector(100,21));
		Anim[0] = 10;	p_MainClickHere.AddAnimation(10,false,Anim);
		Anim[0] = 11;	p_MainClickHere.AddAnimation(10,false,Anim);
		p_MainClickHere.SetCurrentAnimation(0);

		PUConfigs[0] = new puConfig();
		PUConfigs[1] = new puConfig();

		InitLevel();
		iState = WLS_MAIN;
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: AddPUConfig(int iInserts, int iType)
	// Desc: Adiciona uma configuração de power up
	// Pams: Lines/Columns to create PU, Power up type
	//---------------------------------------------------------------------------- 
	public void AddPUConfig(int iFirst, int iInserts, int iType)
	{
		PUConfigs[iPuConfigs].iFirst 		= iFirst;
		PUConfigs[iPuConfigs].iInserts 	= iInserts;
		PUConfigs[iPuConfigs].iType     = iType;
		iPuConfigs++;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CheckPowerUp(int iBoardParam)
	// Desc: verifica a inserção de power ups
	// Pams: Lines/Columns to create PU, Power up type
	//---------------------------------------------------------------------------- 
	public void CheckPowerUp()
	{
		iLinesCreated++;
		if(iLinesCreated != 0)
		{
			for(int i = 0; i < iPuConfigs; i++)
			{
				if(iLinesCreated >= PUConfigs[i].iFirst && 0 == ((iLinesCreated - PUConfigs[i].iFirst)%PUConfigs[i].iInserts))
				{
					int iBoard;
					int iCol;
	
					iBoard	= 0;
					iCol		= RandRange(0,14);
					if(iCol >= 7)
					{
						iBoard++;
						iCol -= 7;
					}
	
					if(Boards[iBoard].NewPieces[iCol] != null)
					{
						Boards[iBoard].NewPieces[iCol].Release();
						FreePieces.push_back(Boards[iBoard].NewPieces[iCol]);
						Boards[iBoard].NewPieces[iCol] = null;
					}
					Boards[iBoard].NewPieces[iCol]											= PowerUps[PUConfigs[i].iType].GetClone();
					Boards[iBoard].NewPieces[iCol].p_Board							= Boards[iBoard];
					Boards[iBoard].NewPieces[iCol].bFalling							= true;
					Boards[iBoard].NewPieces[iCol].Pos.fx								= ((float)iCol) * PopPiece.SIZEX;
					Boards[iBoard].NewPieces[iCol].Pos.fy								= -PopPiece.SIZEY;
//					System.out.println("Board:" + iBoard + " Column:" + iCol);
				}
			}
		}
	}

	
	//---------------------------------------------------------------------------- 
	// Name: InitLevel()
	// Desc: inicializa o nivel
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void InitLevel()
	{
		iLinesCreated = 0;
		iPuConfigs = 0;
	

		iLoadLines = 3;
		LoadLinesTimer.Init(pr_Main,300);

		Boards[0].Pos.fx = WBOARD1X;
		
		Boards[0].Pos.fy = WBOARDY;
		Boards[1].Pos.fx = WBOARD2X;
		Boards[1].Pos.fy = WBOARDY;
	
		Boards[0].Init();
		Boards[1].Init();
		iState = WLS_PREGAME;

		fFlexibility = 3.5f + (((float)iCurrentLevel)*0.23f);
		
		iFoundCorns = 0;
		switch(iCurrentLevel)
		{
			case 0:
			{
				fFlexibility = 1.0f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 150;
				NewLineTimer.Init(pr_Main,6500);
				break;
			}
			case 1:
			{
				fFlexibility = 1.0f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 160;
				NewLineTimer.Init(pr_Main,6500);
				break;
			}
			case 2:
			{
				fFlexibility = 1.0f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 170;
				NewLineTimer.Init(pr_Main,6000);
				AddPUConfig(4,6,PopPiece.WPU_LINEREMOVE);
				break;
			}
			case 3:
			{
				fFlexibility = 1.3f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 180;
				NewLineTimer.Init(pr_Main,6000);
				AddPUConfig(4,6,PopPiece.WPU_LINEREMOVE);
				break;
			}
			case 4:
			{
				fFlexibility = 1.3f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 200;
				NewLineTimer.Init(pr_Main,5500);
				AddPUConfig(4,6,PopPiece.WPU_BOMB);
				break;
			}
			case 5:
			{
				fFlexibility = 1.3f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 220;
				NewLineTimer.Init(pr_Main,5000);
				AddPUConfig(4,6,PopPiece.WPU_BOMB);
				break;
			}
			case 6:
			{
				fFlexibility = 2.0f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 240;
				NewLineTimer.Init(pr_Main,5000);
				AddPUConfig(4,6,PopPiece.WPU_LINEREMOVE);
				AddPUConfig(7,6,PopPiece.WPU_BOMB);
				break;
			}
			case 7:
			{
				fFlexibility = 2.0f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 260;
				NewLineTimer.Init(pr_Main,4500);
				AddPUConfig(7,6,PopPiece.WPU_LINEREMOVE);
				AddPUConfig(4,6,PopPiece.WPU_BOMB);
				break;
			}
			case 8:
			{
				fFlexibility = 2.0f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 280;
				NewLineTimer.Init(pr_Main,4500);
				AddPUConfig(4,6,PopPiece.WPU_LINEREMOVE);
				AddPUConfig(7,6,PopPiece.WPU_BOMB);
				break;
			}
			case 9:
			{
				fFlexibility = 2.0f + (((float)iCurrentLevel)*0.23f);
				iCornsToFind = 300;
				NewLineTimer.Init(pr_Main,4000);
				AddPUConfig(7,6,PopPiece.WPU_LINEREMOVE);
				AddPUConfig(4,6,PopPiece.WPU_BOMB);
				break;
			}
		}

	}
	
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a cena
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: UpdateBoards()
	// Desc: atualiza os tabuleiros
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public static final float WSPEEDLIMMIT = 90.0f;
	void UpdateBoards()
	{
		float fSpeed = (float)((Boards[0].iPieces - Boards[1].iPieces));
		fSpeed*= fFlexibility;
		fSpeed = (fSpeed < -WSPEEDLIMMIT)? -WSPEEDLIMMIT : fSpeed;
		fSpeed = (fSpeed > WSPEEDLIMMIT)? WSPEEDLIMMIT : fSpeed;
	
		if(fSpeed == 0.0f)
		{
			if((Math.abs(Boards[0].Pos.fy - Boards[1].Pos.fy)) < 1.0f)
			{
				fSpeed = 0.0f;
			}
			else
			{
				fSpeed = (Boards[1].Pos.fy - Boards[0].Pos.fy)/3.0f;
				fSpeed = (fSpeed < 0)? -5.0f: 5.0f;
			}
		}
	
/*		if(!bFreezeBoards)
		{
			if(bSlowBoards)
			{
				fSpeed *= 0.3f;
			}*/
//			int iHeight = pr_Main.VideoManager.BackBuffer.FrameSize.fy;
//			float fMax = (float)(iHeight - Boards[0].GetBoardSize().iy); 
			float fMax = pr_Main.VideoManager.BackBuffer.FrameSize.fy - 187.0f;
	
			float fMult = fSpeed * pr_Main.TimeHandler.fFrameTime;
//			if(bEnableFire || ((Boards[0].Pos.fy + fMult < fMax) && (Boards[1].Pos.fy - fMult < fMax)))
//			{
				Boards[0].Pos.fy += fMult;
				Boards[1].Pos.fy -= fMult;
//			}
//		}
	
		Boards[0].Execute();
		Boards[1].Execute();
	}
	//---------------------------------------------------------------------------- 
	// Name: RandRange(int iMin, int iMax) 
	// Desc: Returns a random number
	// Pams: none
	//---------------------------------------------------------------------------- 
	public int RandRange(int iMin, int iMax) 
	{
		int iRand = pr_Main.Randomizer.nextInt();
		iRand = Math.abs(iRand);
		return (iMin + (iRand)%(iMax - iMin));
	}
	
	//---------------------------------------------------------------------------- 
	// Name: UpdateSensor()
	// Desc: atualiza o sensor da balança
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void UpdateSensor()
	{
		if(p_SensorPoint != null)
		{
			int iMaxDif = 144;
			int iAnims	= 25;
			int iAnim = 0;
			int iDif = 0;
			boolean bSum = false;
	
			if((int)Boards[0].Pos.fy > (int)Boards[1].Pos.fy)
			{
				iDif = (int)Boards[0].Pos.fy - (int)Boards[1].Pos.fy;
				bSum = true;
			}
			else
			{
				iDif = (int)Boards[1].Pos.fy - (int)Boards[0].Pos.fy;
				bSum = false;
			}
	
			iDif = (iDif < iMaxDif)? iDif : iMaxDif-1;
			iDif = (iDif < iMaxDif)? iDif : iMaxDif-1;
	
			iAnim = (iDif*iAnims)/iMaxDif;
			p_SensorPoint.SetCurrentAnimation(iAnim + ((bSum)? 25 : 0));
			if(p_SensorBack != null)
			{
				if(iAnim > 15)
				{
					if(!bSum)
					{
						p_SensorBack.SetCurrentAnimation(3);
					}
					else
					{
						p_SensorBack.SetCurrentAnimation(2);
					}
				}
				else
				{
					if(iAnim < 5)
					{
						p_SensorBack.SetCurrentAnimation(0);
					}
					else
					{
						p_SensorBack.SetCurrentAnimation(1);
					}
				}
	
				if(iAnim > 16 )
				{
					if(!bSum)
					{
						Boards[1].bFear = true;
					}
					else
					{
						Boards[0].bFear = true;
					}
				}
				else
				{
					Boards[0].bFear		= false;
					Boards[1].bFear		= false;
				}
			}
		}
		
	}

	//---------------------------------------------------------------------------- 
	// Name: DrawScore()
	// Desc: mostra o scroe para o usuário
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void DrawScore()
	{
		
		if(iState != WLS_MAIN)
		{
			//Level
			DrawCenterXShadow(pr_Main.getParameter("LEVEL"),15);
			DrawCenterXShadow("" + (iCurrentLevel+1),28);
	
			//Level
			DrawCenterXShadow(pr_Main.getParameter("POPCORNS"),42);
			DrawCenterXShadow("" + (iFoundCorns) + " / " + (iCornsToFind),55);
	
			if(iState != WLS_GAMEOVER && iState != WLS_INITLEVEL && iState != WLS_CONGRATS)
			{
				//Score
				DrawCenterXShadow(pr_Main.getParameter("SCORE"),91);
				DrawCenterXShadow("" + iPoints,104);
				
				p_PopsBar.window.fx *= iFoundCorns;
				p_PopsBar.window.fx /= iCornsToFind;
				p_PopsBar.SetCurrentAnimation(0);
				p_PopsBar.Draw();
			
				p_PopsBar.SetCurrentAnimation(1);
				p_PopsBar.window.fx = p_PopsBar.pr_Image.FrameSize.fx;
				
				
				JGDLFont.DrawText(pr_Main.VideoManager,190, 123,""+Boards[0].iPieces,DarkRed,SmallRed);
				JGDLFont.DrawText(pr_Main.VideoManager,247, 123,""+Boards[1].iPieces,DarkRed,SmallRed);
			}		
		}		

	}
	
	//---------------------------------------------------------------------------- 
	// Name: DrawScore()
	// Desc: mostra o scroe para o usuário
	// Pams: none
	//---------------------------------------------------------------------------- 
	private void DrawCenterXShadow(String s, int iY)	
	{
		int iPos = 224 - (JGDLFont.GetTextWidth(pr_Main.VideoManager, Arial,s)>>1);
		
		JGDLFont.DrawText(pr_Main.VideoManager,iPos,    iY,s,Color.darkGray,Arial);
		JGDLFont.DrawText(pr_Main.VideoManager,iPos-1,iY-1,s,Color.yellow,Arial);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: AddPiecesAnimations()
	// Desc: Adds the pieces animations
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void AddPiecesAnimations()
	{
		//Surprise
		int []Surprise = new int [10];
		Surprise[0] = 0; Surprise[1] = 1;
		Surprise[2] = 2; Surprise[3] = 3;
		Surprise[4] = 4; Surprise[5] = 5;
		Surprise[6] = 6; Surprise[7] = 7;
		Surprise[8] = 6; Surprise[9] = 6;
		
		Pieces[0].pr_Sprite.AddAnimation(15,true,Surprise);
		Pieces[1].pr_Sprite.AddAnimation(15,true,Surprise);
		Pieces[2].pr_Sprite.AddAnimation(15,true,Surprise);
		Pieces[3].pr_Sprite.AddAnimation(15,true,Surprise);

		//Stopped 1
		int [] Stopped1 = new int[10];
		Stopped1[0] = 40; Stopped1[1] = 40; Stopped1[2] = 40;
		Stopped1[3] = 7;  Stopped1[4] = 40; Stopped1[5] = 40;
		Stopped1[6] = 40; Stopped1[7] = 8; Stopped1[8]  = 8;
		Stopped1[9] = 40;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,Stopped1);
		Pieces[1].pr_Sprite.AddAnimation(5,true,Stopped1);
		Pieces[2].pr_Sprite.AddAnimation(5,true,Stopped1);
		Pieces[3].pr_Sprite.AddAnimation(5,true,Stopped1);
		
	
		//Stopped 2
		int [] Stopped2 = new int[12];
		Stopped2[0] = 39; Stopped2[1]  = 39; Stopped2[2]  = 37;
		Stopped2[3] = 37; Stopped2[4]  = 37; Stopped2[5]  = 37;
		Stopped2[6] = 30; Stopped2[7]  = 37; Stopped2[8]  = 37;
		Stopped2[9] = 37; Stopped2[10] = 39; Stopped2[11] = 39;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,Stopped2);
		Pieces[1].pr_Sprite.AddAnimation(5,true,Stopped2);
		Pieces[2].pr_Sprite.AddAnimation(5,true,Stopped2);
		Pieces[3].pr_Sprite.AddAnimation(5,true,Stopped2);

		//Stopped 3
		int [] Stopped3 = new int[12];
		Stopped3[0] = 37; Stopped3[1]  = 37; Stopped3[2]  = 30;
		Stopped3[3] = 37; Stopped3[4]  = 37; Stopped3[5]  = 37;
		Stopped3[6] = 37; Stopped3[7]  = 37; Stopped3[8]  = 37;
		Stopped3[9] = 37; Stopped3[10] = 37; Stopped3[11] = 37;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,Stopped3);
		Pieces[1].pr_Sprite.AddAnimation(5,true,Stopped3);
		Pieces[2].pr_Sprite.AddAnimation(5,true,Stopped3);
		Pieces[3].pr_Sprite.AddAnimation(5,true,Stopped3);
				
		//Falled on top
		int [] FalledOnTop = new int[6];
		FalledOnTop[0] = 20; FalledOnTop[1] = 21; FalledOnTop[2] = 19;
		FalledOnTop[0] = 17; FalledOnTop[1] = 28; FalledOnTop[2] = 28;
		
		Pieces[0].pr_Sprite.AddAnimation(11,false,FalledOnTop);
		Pieces[1].pr_Sprite.AddAnimation(11,false,FalledOnTop);
		Pieces[2].pr_Sprite.AddAnimation(11,false,FalledOnTop);
		Pieces[3].pr_Sprite.AddAnimation(11,false,FalledOnTop);
		
		//Falled 1
		int [] Falled1 = new int[15];
		
		Falled1[0] = 21;  Falled1[1]  = 22; Falled1[2]  = 23;
		Falled1[3] = 24;  Falled1[4]  = 25; Falled1[5]  = 29;
		Falled1[6] = 29;  Falled1[7]  = 29; Falled1[8]  = 26;
		Falled1[9] = 27;  Falled1[10] = 27; Falled1[11] = 29;
		Falled1[12] = 29; Falled1[13] = 37; Falled1[14] = 37;
		
		Pieces[0].pr_Sprite.AddAnimation(11,false,Falled1);
		Pieces[1].pr_Sprite.AddAnimation(11,false,Falled1);
		Pieces[2].pr_Sprite.AddAnimation(11,false,Falled1);
		Pieces[3].pr_Sprite.AddAnimation(11,false,Falled1);
		
		//Falled 2
		int [] Falled2 = new int[15];
		
		Falled2[0] = 21;  Falled2[1]  = 22; Falled2[2]  = 23;
		Falled2[3] = 24;  Falled2[4]  = 25; Falled2[5]  = 28;
		Falled2[6] = 28;  Falled2[7]  = 28; Falled2[8]  = 26;
		Falled2[9] = 26;  Falled2[10] = 27; Falled2[11] = 27;
		Falled2[12] = 26; Falled2[13] = 29; Falled2[14] = 29;
		
		Pieces[0].pr_Sprite.AddAnimation(12,false,Falled2);
		Pieces[1].pr_Sprite.AddAnimation(12,false,Falled2);
		Pieces[2].pr_Sprite.AddAnimation(12,false,Falled2);
		Pieces[3].pr_Sprite.AddAnimation(12,false,Falled2);
		
		//Sad
		int [] Sad = new int[12];
		Sad[0] = 16;  Sad[1]  = 16; Sad[2]  = 17;
		Sad[3] = 17;  Sad[4]  = 17; Sad[5]  = 85;
		Sad[6] = 85;  Sad[7]  = 82; Sad[8]  = 85;
		Sad[9] = 16;  Sad[10] = 16; Sad[11] = 16;
		Pieces[0].pr_Sprite.AddAnimation(6,true,Sad);
		Pieces[1].pr_Sprite.AddAnimation(6,true,Sad);
		Pieces[2].pr_Sprite.AddAnimation(6,true,Sad);
		Pieces[3].pr_Sprite.AddAnimation(6,true,Sad);
		
		//Heat Drops 1
		int []HeatDrops1 = new int[5];
		HeatDrops1[0] = 58; HeatDrops1[1] = 59; HeatDrops1[2] = 60;
		HeatDrops1[3] = 61; HeatDrops1[4] = 59;
		Pieces[0].pr_Sprite.AddAnimation(4,true,HeatDrops1);
		Pieces[1].pr_Sprite.AddAnimation(4,true,HeatDrops1);
		Pieces[2].pr_Sprite.AddAnimation(4,true,HeatDrops1);
		Pieces[3].pr_Sprite.AddAnimation(4,true,HeatDrops1);
		
		//Heat Drops 2
		int []HeatDrops2 = new int[3];
		HeatDrops2[0] = 60; HeatDrops2[1] = 58; HeatDrops2[2] = 62;
		Pieces[0].pr_Sprite.AddAnimation(4,true,HeatDrops2);
		Pieces[1].pr_Sprite.AddAnimation(4,true,HeatDrops2);
		Pieces[2].pr_Sprite.AddAnimation(4,true,HeatDrops2);
		Pieces[3].pr_Sprite.AddAnimation(4,true,HeatDrops2);
		
		//Feeling Hot 1
		int []FeelingHot1 = new int[7];
		FeelingHot1[0] = 63; FeelingHot1[1] = 64; FeelingHot1[2] = 65;
		FeelingHot1[3] = 66; FeelingHot1[4] = 67; FeelingHot1[5] = 68;
		FeelingHot1[6] = 69; 

		Pieces[0].pr_Sprite.AddAnimation(4,true,FeelingHot1);
		Pieces[1].pr_Sprite.AddAnimation(4,true,FeelingHot1);
		Pieces[2].pr_Sprite.AddAnimation(4,true,FeelingHot1);
		Pieces[3].pr_Sprite.AddAnimation(4,true,FeelingHot1);
		
		//Feeling Hot 2
		int []FeelingHot2 = new int[5];
		FeelingHot2[0] = 68; FeelingHot2[1] = 69; FeelingHot2[2] = 70;
		FeelingHot2[3] = 71; FeelingHot2[4] = 72;

		Pieces[0].pr_Sprite.AddAnimation(4,true,FeelingHot2);
		Pieces[1].pr_Sprite.AddAnimation(4,true,FeelingHot2);
		Pieces[2].pr_Sprite.AddAnimation(4,true,FeelingHot2);
		Pieces[3].pr_Sprite.AddAnimation(4,true,FeelingHot2);
		
		//Feeling Hot 3
		int []FeelingHot3 = new int[5];
		FeelingHot3[0] = 72; FeelingHot3[1] = 72; FeelingHot3[2] = 73;
		FeelingHot3[3] = 74; FeelingHot3[4] = 74;

		Pieces[0].pr_Sprite.AddAnimation(4,true,FeelingHot3);
		Pieces[1].pr_Sprite.AddAnimation(4,true,FeelingHot3);
		Pieces[2].pr_Sprite.AddAnimation(4,true,FeelingHot3);
		Pieces[3].pr_Sprite.AddAnimation(4,true,FeelingHot3);
		
		//Burning 1
		int [] Burning1 = new int [10];
		Burning1[0] = 76; Burning1[1] = 77; Burning1[2] = 78;
		Burning1[3] = 79; Burning1[4] = 79; Burning1[5] = 80;
		Burning1[6] = 78; Burning1[7] = 78; Burning1[8] = 78;
		Burning1[9] = 78;
	
		Pieces[0].pr_Sprite.AddAnimation(12,true,Burning1);
		Pieces[1].pr_Sprite.AddAnimation(12,true,Burning1);
		Pieces[2].pr_Sprite.AddAnimation(12,true,Burning1);
		Pieces[3].pr_Sprite.AddAnimation(12,true,Burning1);
		
		//Burning 2
		int [] Burning2 = new int [10];
		Burning2[0] = 76; Burning2[1] = 77; Burning2[2] = 78;
		Burning2[3] = 79; Burning2[4] = 80; Burning2[5] = 78;
		Burning2[6] = 78; Burning2[7] = 80; Burning2[8] = 78;
		Burning2[9] = 78;
	
		Pieces[0].pr_Sprite.AddAnimation(12,true,Burning2);
		Pieces[1].pr_Sprite.AddAnimation(12,true,Burning2);
		Pieces[2].pr_Sprite.AddAnimation(12,true,Burning2);
		Pieces[3].pr_Sprite.AddAnimation(12,true,Burning2);

		//Happy Left
		int [] HappyLeft = new int[11];
		HappyLeft[0] = 36; HappyLeft[1] = 40; HappyLeft[2] = 42;
		HappyLeft[3] = 43; HappyLeft[4] = 33; HappyLeft[5] = 32;
		HappyLeft[6] = 32; HappyLeft[7] = 31; HappyLeft[8] = 31;
		HappyLeft[9] = 35; HappyLeft[10] = 31;
	
		Pieces[0].pr_Sprite.AddAnimation(5,true,HappyLeft);
		Pieces[1].pr_Sprite.AddAnimation(5,true,HappyLeft);
		Pieces[2].pr_Sprite.AddAnimation(5,true,HappyLeft);
		Pieces[3].pr_Sprite.AddAnimation(5,true,HappyLeft);
		
		//Happy Right
		int [] HappyRight = new int[11];
		HappyRight[0] = 36; HappyRight[1] = 40; HappyRight[2] = 47;
		HappyRight[3] = 48; HappyRight[4] = 34; HappyRight[5] = 32;
		HappyRight[6] = 32; HappyRight[7] = 31; HappyRight[8] = 31;
		HappyRight[9] = 35; HappyRight[10] = 31;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,HappyRight);
		Pieces[1].pr_Sprite.AddAnimation(5,true,HappyRight);
		Pieces[2].pr_Sprite.AddAnimation(5,true,HappyRight);
		Pieces[3].pr_Sprite.AddAnimation(5,true,HappyRight);
		
		//Blink Left
		int [] BlinkLeft = new int[11];
		BlinkLeft[0] = 40; BlinkLeft[1] = 39; BlinkLeft[2] = 40;
		BlinkLeft[3] = 42; BlinkLeft[4] = 43; BlinkLeft[5] = 44;
		BlinkLeft[6] = 43; BlinkLeft[7] = 43; BlinkLeft[8] = 43;
		BlinkLeft[9] = 40; BlinkLeft[10] = 40;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,BlinkLeft);
		Pieces[1].pr_Sprite.AddAnimation(5,true,BlinkLeft);
		Pieces[2].pr_Sprite.AddAnimation(5,true,BlinkLeft);
		Pieces[3].pr_Sprite.AddAnimation(5,true,BlinkLeft);
		
		//Blink Right
		int [] BlinkRight = new int[11];
		BlinkRight[0] = 40; BlinkRight[1] = 39; BlinkRight[2] = 40;
		BlinkRight[3] = 47; BlinkRight[4] = 48; BlinkRight[5] = 49;
		BlinkRight[6] = 48; BlinkRight[7] = 48; BlinkRight[8] = 48;
		BlinkRight[9] = 40; BlinkRight[10] = 40;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,BlinkRight);
		Pieces[1].pr_Sprite.AddAnimation(5,true,BlinkRight);
		Pieces[2].pr_Sprite.AddAnimation(5,true,BlinkRight);
		Pieces[3].pr_Sprite.AddAnimation(5,true,BlinkRight);

		//Kiss Left
		int [] KissLeft = new int[14];
		KissLeft[0] = 40; KissLeft[1] = 41; KissLeft[2] = 40;
		KissLeft[3] = 42; KissLeft[4] = 43; KissLeft[5] = 44;
		KissLeft[6] = 43; KissLeft[7] = 43; KissLeft[8] = 43;
		KissLeft[9] = 45; KissLeft[10] = 45; KissLeft[11] = 43;
		KissLeft[12] = 43; KissLeft[13] = 43;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,KissLeft);
		Pieces[1].pr_Sprite.AddAnimation(5,true,KissLeft);
		Pieces[2].pr_Sprite.AddAnimation(5,true,KissLeft);
		Pieces[3].pr_Sprite.AddAnimation(5,true,KissLeft);

		//Kiss Right
		int [] KissRight = new int[14];
		KissRight[0] = 40; KissRight[1] = 41; KissRight[2] = 40;
		KissRight[3] = 47; KissRight[4] = 48; KissRight[5] = 49;
		KissRight[6] = 48; KissRight[7] = 48; KissRight[8] = 48;
		KissRight[9] = 50; KissRight[10] = 50; KissRight[11] = 48;
		KissRight[12] = 48; KissRight[13] = 48;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,KissRight);
		Pieces[1].pr_Sprite.AddAnimation(5,true,KissRight);
		Pieces[2].pr_Sprite.AddAnimation(5,true,KissRight);
		Pieces[3].pr_Sprite.AddAnimation(5,true,KissRight);
		
		//tongue Right
		int [] TongueRight = new int[14];
		TongueRight[0] = 39; TongueRight[1] = 9; TongueRight[2] = 9;
		TongueRight[3] = 10; TongueRight[4] = 10; TongueRight[5] = 10;
		TongueRight[6] = 11; TongueRight[7] = 11; TongueRight[8] = 10;
		TongueRight[9] = 10; TongueRight[10] = 10; TongueRight[11] = 16;
		TongueRight[12] = 16; TongueRight[13] = 16;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,TongueRight);
		Pieces[1].pr_Sprite.AddAnimation(5,true,TongueRight);
		Pieces[2].pr_Sprite.AddAnimation(5,true,TongueRight);
		Pieces[3].pr_Sprite.AddAnimation(5,true,TongueRight);
	
		//tongue left
		int [] TongueLeft = new int[14];
		TongueLeft[0] = 39; TongueLeft[1] = 13; TongueLeft[2] = 13;
		TongueLeft[3] = 14; TongueLeft[4] = 14; TongueLeft[5] = 14;
		TongueLeft[6] = 15; TongueLeft[7] = 15; TongueLeft[8] = 14;
		TongueLeft[9] = 14; TongueLeft[10] = 14; TongueLeft[11] = 16;
		TongueLeft[12] = 16; TongueLeft[13] = 16;
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,TongueLeft);
		Pieces[1].pr_Sprite.AddAnimation(5,true,TongueLeft);
		Pieces[2].pr_Sprite.AddAnimation(5,true,TongueLeft);
		Pieces[3].pr_Sprite.AddAnimation(5,true,TongueLeft);
		
		//Fly Left
		int [] FlyLeft = new int[2];
		FlyLeft[0] = 54; FlyLeft[1] = 55;
		Pieces[0].pr_Sprite.AddAnimation(12,true,FlyLeft);
		Pieces[1].pr_Sprite.AddAnimation(12,true,FlyLeft);
		Pieces[2].pr_Sprite.AddAnimation(12,true,FlyLeft);
		Pieces[3].pr_Sprite.AddAnimation(12,true,FlyLeft);
		
		//Fly Right
		int [] FlyRight = new int[2];
		FlyRight[0] = 53; FlyRight[1] = 52;
		Pieces[0].pr_Sprite.AddAnimation(12,true,FlyRight);
		Pieces[1].pr_Sprite.AddAnimation(12,true,FlyRight);
		Pieces[2].pr_Sprite.AddAnimation(12,true,FlyRight);
		Pieces[3].pr_Sprite.AddAnimation(12,true,FlyRight);
		
		//Angry1
		int [] Angry1 = new int[20];
		Angry1[0] = 37;  Angry1[1] = 37;  Angry1[2] = 37;
		Angry1[3] = 30;  Angry1[4] = 37;  Angry1[5] = 29;
		Angry1[6] = 29;  Angry1[7] = 19;  Angry1[8] = 88;
		Angry1[9] = 88;  Angry1[10] = 26; Angry1[11] = 26;
		Angry1[12] = 28; Angry1[13] = 28; Angry1[14] = 28;
		Angry1[15] = 17; Angry1[16] = 17; Angry1[17] = 29;
		Angry1[18] = 29; Angry1[19] = 19; 

		Pieces[0].pr_Sprite.AddAnimation(5,true,Angry1);
		Pieces[1].pr_Sprite.AddAnimation(5,true,Angry1);
		Pieces[2].pr_Sprite.AddAnimation(5,true,Angry1);
		Pieces[3].pr_Sprite.AddAnimation(5,true,Angry1);
		
		//Angry2
		int [] Angry2 = new int[10];
		Angry2[0] = 37;  Angry2[1] = 37;  Angry2[2] = 37;
		Angry2[3] = 12;  Angry2[4] = 12;  Angry2[5] = 12;
		Angry2[6] = 88;  Angry2[7] = 88;  Angry2[8] = 88;
		Angry2[9] = 12; 
		
		Pieces[0].pr_Sprite.AddAnimation(5,true,Angry2);
		Pieces[1].pr_Sprite.AddAnimation(5,true,Angry2);
		Pieces[2].pr_Sprite.AddAnimation(5,true,Angry2);
		Pieces[3].pr_Sprite.AddAnimation(5,true,Angry2);
		
		//Smoke
		int [] Smoke = new int[6];
		Smoke[0] = 78;  Smoke[1] = 78;  Smoke[2] = 78;
		Smoke[3] = 80;  Smoke[4] = 78;  Smoke[5] = 78;
		Pieces[0].pr_Sprite.AddAnimation(5,true,Smoke);
		Pieces[1].pr_Sprite.AddAnimation(5,true,Smoke);
		Pieces[2].pr_Sprite.AddAnimation(5,true,Smoke);
		Pieces[3].pr_Sprite.AddAnimation(5,true,Smoke);
		
		//Falling
		int [] Falling = new int[3];
		Falling[0] = 17; Falling[1] = 19; Falling[2] = 20;
		Pieces[0].pr_Sprite.AddAnimation(14,false,Falling);
		Pieces[1].pr_Sprite.AddAnimation(14,false,Falling);
		Pieces[2].pr_Sprite.AddAnimation(14,false,Falling);
		Pieces[3].pr_Sprite.AddAnimation(14,false,Falling);
		
		//Falling on top
		int [] FallingOnTop = new int[1];
		FallingOnTop[0] = 28;
		Pieces[0].pr_Sprite.AddAnimation(10,false,FallingOnTop);
		Pieces[1].pr_Sprite.AddAnimation(10,false,FallingOnTop);
		Pieces[2].pr_Sprite.AddAnimation(10,false,FallingOnTop);
		Pieces[3].pr_Sprite.AddAnimation(10,false,FallingOnTop);
		
			
	
	}
	
	//---------------------------------------------------------------------------- 
	// Name: HandleMenu()
	// Desc: trata o menu
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void HandleMenu()
	{
		if(bIsMenuOpen)
		{
			Boards[0].p_Shake.bFreezed = true;
			Boards[1].p_Shake.bFreezed = true;
			if(p_BtnOpenMenu.IsMouseOver())
			{
				p_BtnOpenMenu.SetCurrentAnimation(4);
			}
			else
			{
				p_BtnOpenMenu.SetCurrentAnimation(3);
			}

			if(p_BtnOpenMenu.Clicked(0))
			{
				bIsMenuOpen = false;
				p_BtnOpenMenu.MoveTo(167,311,400);
				iState = WLS_GAME;
				int i = 0;
				for(i = 0; i < PopBoard.MATSIZE; i++)
				{
					if(null != Boards[0].Matrix[i] && null != Boards[0].Matrix[i].pr_Sprite)
					{
						Boards[0].Matrix[i].pr_Sprite.bFreezed = false;
					}
					if(null != Boards[1].Matrix[i] && null != Boards[1].Matrix[i].pr_Sprite)
					{
						Boards[1].Matrix[i].pr_Sprite.bFreezed = false;
					}
				}
				for(i = 0; i < Flames.length; i++)
				{
					Flames[i].bFreezed = false;
				}
				
			}
			
			if(p_BtnNoSound.IsMouseOver())
			{
				if(pr_Main.SoundManager.bEnableSounds)
				{
					p_BtnNoSound.SetCurrentAnimation(1);
				}
				else
				{
					p_BtnNoSound.SetCurrentAnimation(3);
				}
			}
			else
			{
				if(pr_Main.SoundManager.bEnableSounds)
				{
					p_BtnNoSound.SetCurrentAnimation(0);
				}
				else
				{
					p_BtnNoSound.SetCurrentAnimation(2);
				}
			}
			if(p_BtnNoSound.Clicked(0))
			{
				pr_Main.SoundManager.bEnableSounds = !pr_Main.SoundManager.bEnableSounds;
			}
			
			if(p_BtnDownload.IsMouseOver())
			{
				p_BtnDownload.SetCurrentAnimation(1);
			}
			else
			{
				p_BtnDownload.SetCurrentAnimation(0);
			}
			if(p_BtnDownload.Clicked(0))
			{
				OpenURL();
			}
		}
		else
		{
			if(p_BtnOpenMenu.IsMouseOver())
			{
				p_BtnOpenMenu.SetCurrentAnimation(1);
			}
			else
			{
				p_BtnOpenMenu.SetCurrentAnimation(0);
			}
			if(p_BtnOpenMenu.Clicked(0))
			{
				bIsMenuOpen = true;
				p_BtnOpenMenu.MoveTo(167,239,400);
				iState = WLS_MENUTAG;
				int i =0;
				for(i = 0; i < PopBoard.MATSIZE; i++)
				{
					if(null != Boards[0].Matrix[i] && null != Boards[0].Matrix[i].pr_Sprite)
					{
						Boards[0].Matrix[i].pr_Sprite.bFreezed = true;
					}
					if(null != Boards[1].Matrix[i] && null != Boards[1].Matrix[i].pr_Sprite)
					{
						Boards[1].Matrix[i].pr_Sprite.bFreezed = true;
					}
				}
				for(i = 0; i < Flames.length; i++)
				{
					Flames[i].bFreezed = true;
				}
			}
		}

		p_BkgMenu.position.fx = p_BtnOpenMenu.position.fx;
		p_BkgMenu.position.fy = p_BtnOpenMenu.position.fy + 30.0f;
		
		p_BtnDownload.position.fx = p_BtnOpenMenu.position.fx + 7.0f;
		p_BtnDownload.position.fy = p_BtnOpenMenu.position.fy + 40.0f;
		
		p_BtnNoSound.position.fx = p_BtnOpenMenu.position.fx + 7.0f;
		p_BtnNoSound.position.fy = p_BtnOpenMenu.position.fy + 70.0f;
		
		
	}
	
	//---------------------------------------------------------------------------- 
	// Name: OpenURL(String urlName)
	// Desc: Opens the download URL
	// Pams: urlName
	//---------------------------------------------------------------------------- 
	void OpenURL()
	{
   	try 
    { 
	     pr_Main.getAppletContext().showDocument(new
	        URL(pr_Main.getParameter("BUYURL")), pr_Main.getParameter("TARGET")); 
    } 
    catch(Exception e) 
    { 
        System.out.println("" + e); 
    }
	}
	
	//---------------------------------------------------------------------------- 
	// Name: InitializeMenu()
	// Desc: Inicializa o menu
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void InitializeMenu()
	{
		TempVector.atrib(448,336);
		JGDLLayer p_MenuLayer = CreateLayer(TempVector);
		

		p_BkgMenu = p_MenuLayer.CreateSprite("spr_BkgMenu.gif", new JGDLVector(114,127));
		p_BkgMenu.position.fx = 167.0f;
		p_BkgMenu.position.fy = 239.0f;
		
		p_BtnOpenMenu = p_MenuLayer.CreateSprite("btn_MenuTag.gif", new JGDLVector(114,30));
		p_BtnOpenMenu.position.fx = 167.0f;
		p_BtnOpenMenu.position.fy = 311.0f;
		
		p_BtnDownload = p_MenuLayer.CreateSprite("btn_menubuttons.gif", new JGDLVector(100,21));
		int i = 0;
		int [] Anim = new int[1];
		Anim[0] = 0;
		p_BtnDownload.AddAnimation(10,false,Anim);
		Anim[0] = 1;
		p_BtnDownload.AddAnimation(10,false,Anim);
		p_BtnDownload.position.fx = 172.0f;
		p_BtnDownload.position.fy = 242.0f;
		
		p_BtnNoSound = p_MenuLayer.CreateSprite("btn_menubuttons.gif", new JGDLVector(100,21));
		
		Anim[0] = 2; p_BtnNoSound.AddAnimation(10,false,Anim);
		Anim[0] = 3; p_BtnNoSound.AddAnimation(10,false,Anim);
		Anim[0] = 4; p_BtnNoSound.AddAnimation(10,false,Anim);
		Anim[0] = 5; p_BtnNoSound.AddAnimation(10,false,Anim);
		
		p_BtnDownload.position.fx = 172.0f;
		p_BtnDownload.position.fy = 272.0f;
		
		
		for( i = 0; i < 6; i++)
		{
			Anim[0] = i;
			p_BtnOpenMenu.AddAnimation(10,false,Anim);
		}
		HandleMenu();
	}
	
	//---------------------------------------------------------------------------- 
	// Name: InitializePopup()
	// Desc: Inicializa a mensagem popup
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void InitializePopup()
	{
		int [] Anim = new int[1];
		
		TempVector.atrib(448,336);
		JGDLLayer p_PopupLayer = CreateLayer(TempVector);
		
		p_PopupFrame 			= p_PopupLayer.CreateSprite("men_PopUp.gif", new JGDLVector(234,141));
		p_PopupFrame.position.fx = 107.0f;
		p_PopupFrame.position.fy = 337.0f;
		
		p_PopupTitle 			= p_PopupLayer.CreateSprite("men_PopUpTitle.gif", new JGDLVector(192,43));
		Anim[0] = 0;	p_PopupTitle.AddAnimation(10,false,Anim);
		Anim[0] = 1;	p_PopupTitle.AddAnimation(10,false,Anim);
		
		p_PopupDownload		= p_PopupLayer.CreateSprite("btn_menubuttons.gif", new JGDLVector(100,21));
		Anim[0] = 0;	p_PopupDownload.AddAnimation(10,false,Anim);
		Anim[0] = 1;	p_PopupDownload.AddAnimation(10,false,Anim);
		p_PopupDownload.SetCurrentAnimation(0);
		
		p_PopupNextLevel = p_PopupLayer.CreateSprite("btn_menubuttons.gif", new JGDLVector(100,21));
		Anim[0] = 6;	p_PopupNextLevel.AddAnimation(10,false,Anim);
		Anim[0] = 7;	p_PopupNextLevel.AddAnimation(10,false,Anim);
		p_PopupNextLevel.SetCurrentAnimation(0);
		
		p_PopupPlayAgain = p_PopupLayer.CreateSprite("btn_menubuttons.gif", new JGDLVector(100,21));
		Anim[0] = 8;	p_PopupPlayAgain.AddAnimation(10,false,Anim);
		Anim[0] = 9;	p_PopupPlayAgain.AddAnimation(10,false,Anim);
		
		p_Congrats = p_PopupLayer.CreateSprite("spr_Congrats.gif", new JGDLVector(217,92));
		
		p_PopupPlayAgain.SetCurrentAnimation(0);
		
		p_PopupTitle.position.fx = p_PopupFrame.position.fx + 21.0f;
		p_PopupTitle.position.fy = p_PopupFrame.position.fy + 20.0f;
		p_Congrats.position.fx	 = p_PopupFrame.position.fx + 11.0f;
		p_Congrats.position.fy	 = p_PopupFrame.position.fy + 11.0f;
		p_Congrats.bVisible 		 = false;
		
		
		p_PopupPlayAgain.position.fx = p_PopupFrame.position.fx + 10.0f;
		p_PopupPlayAgain.position.fy = p_PopupFrame.position.fy + 90.0f;

		p_PopupNextLevel.position.fx = p_PopupFrame.position.fx + 10.0f;
		p_PopupNextLevel.position.fy = p_PopupFrame.position.fy + 90.0f;
		
		p_PopupDownload.position.fx = p_PopupFrame.position.fx + 120.0f;
		p_PopupDownload.position.fy = p_PopupFrame.position.fy + 90.0f;

	}
	
	//---------------------------------------------------------------------------- 
	// Name: HandlePopUp()
	// Desc: Trata a mensagem de Pop up
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void HandlePopUp()
	{
		switch(iState)
		{
			case WLS_CONGRATS:
			case WLS_GAMEOVER:
			{
				p_PopupNextLevel.bVisible = false;
				p_PopupPlayAgain.bVisible = true;
				
				p_PopupTitle.SetCurrentAnimation(0);
				p_PopupPlayAgain.SetCurrentAnimation(p_PopupPlayAgain.IsMouseOver()? 1 : 0);
				p_PopupDownload.SetCurrentAnimation(p_PopupDownload.IsMouseOver()? 1 : 0);
				
				if(p_PopupPlayAgain.Clicked(0))
				{
					iCurrentLevel = 0;
					iPoints = 0;
					InitLevel();
					p_PopupFrame.MoveTo(p_PopupFrame.position.fx,337,400);
					ChangeState(WLS_PREGAME);

					for(int i = 0; i < 18; i++)
					{
						Flames[i].SetCurrentAnimation(0);
						Flames[i].GetCurrentAnimationPointer().uiTimeAccum = Math.abs(pr_Main.Randomizer.nextInt())%200;
					}
				}
				
				if(p_PopupDownload.Clicked(0))
				{
					OpenURL();
				}

				break;
			}
			case WLS_INITLEVEL:
			{
				p_PopupNextLevel.bVisible = true;
				p_PopupPlayAgain.bVisible = false;
				
				p_PopupTitle.SetCurrentAnimation(1);
				p_PopupNextLevel.SetCurrentAnimation(p_PopupNextLevel.IsMouseOver()? 1 : 0);
				p_PopupDownload.SetCurrentAnimation(p_PopupDownload.IsMouseOver()? 1 : 0);
				
				if(p_PopupNextLevel.Clicked(0))
				{
					iCurrentLevel++;
					InitLevel();
					p_PopupFrame.MoveTo(p_PopupFrame.position.fx,337,400);
					ChangeState(WLS_PREGAME);

					for(int i = 0; i < 18; i++)
					{
						Flames[i].SetCurrentAnimation(0);
						Flames[i].GetCurrentAnimationPointer().uiTimeAccum = Math.abs(pr_Main.Randomizer.nextInt())%200;
					}
				}
				
				if(p_PopupDownload.Clicked(0))
				{
					OpenURL();
				}
				break;
			}
		}
		
		if(iState != WLS_CONGRATS)
		{
			p_Congrats.bVisible = false;
			p_PopupTitle.bVisible = true;
			p_PopupTitle.position.fx = p_PopupFrame.position.fx + 21.0f;
			p_PopupTitle.position.fy = p_PopupFrame.position.fy + 20.0f;
			
			p_PopupPlayAgain.position.fx = p_PopupFrame.position.fx + 10.0f;
			p_PopupPlayAgain.position.fy = p_PopupFrame.position.fy + 90.0f;
	
			p_PopupNextLevel.position.fx = p_PopupFrame.position.fx + 10.0f;
			p_PopupNextLevel.position.fy = p_PopupFrame.position.fy + 90.0f;
			
			p_PopupDownload.position.fx = p_PopupFrame.position.fx + 120.0f;
			p_PopupDownload.position.fy = p_PopupFrame.position.fy + 90.0f;
		}
		else
		{
			p_Congrats.bVisible				= true;
			p_PopupTitle.bVisible 		= false;
			p_Congrats.position.fx	= p_PopupFrame.position.fx + 10.0f;
			p_Congrats.position.fy	= p_PopupFrame.position.fy + 12.0f;
			
			p_PopupPlayAgain.position.fx = p_PopupFrame.position.fx + 10.0f;
			p_PopupPlayAgain.position.fy = p_PopupFrame.position.fy + 105.0f;
	
			p_PopupNextLevel.position.fx = p_PopupFrame.position.fx + 10.0f;
			p_PopupNextLevel.position.fy = p_PopupFrame.position.fy + 105.0f;
			
			p_PopupDownload.position.fx = p_PopupFrame.position.fx + 120.0f;
			p_PopupDownload.position.fy = p_PopupFrame.position.fy + 105.0f;
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ChangeState(int iNewState)
	// Desc: muda o estado do jogo
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void ChangeState(int iNewState)
	{
		switch(iNewState)
		{
			case WLS_LEVELUP:
			{
				Boards[0].Clear();
				Boards[1].Clear();
				for(int i = 0 ; i < 18; i++)
				{
					Flames[i].SetCurrentAnimation(2);
				}
				Boards[0].p_Shake.bFreezed = true;
				Boards[1].p_Shake.bFreezed = true;
				break;
			}
			case WLS_CONGRATS:
			{
				p_PopupFrame.MoveTo(p_PopupFrame.position.fx,72,400);
				break;
			}
			case WLS_INITLEVEL:
			{
				p_PopupFrame.MoveTo(p_PopupFrame.position.fx,72,400);
				break;
			}
			case WLS_GAMEOVER:
			{
				Boards[0].ExplodeAll();
				Boards[1].ExplodeAll();
				for(int i = 0 ; i < 18; i++)
				{
					Flames[i].SetCurrentAnimation(2);
				}
				Boards[0].p_Shake.bFreezed = true;
				Boards[1].p_Shake.bFreezed = true;
				break;
			}
			
		}
		iState = iNewState;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CheckBoardsDiference()
	// Desc: Verifica a diferença entre os tabuleiros
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void CheckBoardsDiference()
	{
		if(Math.abs(Boards[0].Pos.fy - Boards[1].Pos.fy) > 158.0f )
		{
			ChangeState(WLS_GAMEOVER);
			return;
		}
	}
}
