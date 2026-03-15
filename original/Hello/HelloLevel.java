/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCMyScene.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

import JGDL.*;

import java.awt.event.*;
import java.awt.*;
import java.net.*;

public class HelloLevel extends JGDLScene
{
	public final static JGDLVector TempPos1 = new JGDLVector();
	public final static JGDLVector TempPos2 = new JGDLVector();
	public final static JGDLVector TempPos3 = new JGDLVector();
	public final static JGDLVector TempPos4 = new JGDLVector();
	public final static JGDLVector TempPos5 = new JGDLVector();
	public final static JGDLVector TempPos6 = new JGDLVector();
	public final static JGDLVector TempPos7 = new JGDLVector();
	public final static JGDLVector TempPos8 = new JGDLVector();

	public final static JGDLVector TempPos9 = new JGDLVector();
	public final static JGDLVector TempPos10 = new JGDLVector();
	public final static JGDLVector TempPos11 = new JGDLVector();
	public final static JGDLVector TempPos12 = new JGDLVector();
	public final static JGDLVector TempPos13 = new JGDLVector();
//	public final static JGDLVector TempPosFromMatrix = new JGDLVector();
	
	public class puConfig
	{
		int 	iFirst;
		int 	iInserts;
		byte 	bType;
	}
	//power up inserts
	int iInserts;
	
	
	HelloPiece [] PowerUps = new HelloPiece [3];
	
	int iPuConfigs = 0;
	
	puConfig [] PUConfigs = new puConfig[3];
	
	//Search Class
	MySearch Search 									= new MySearch(this);
	HelloEffectManager	EffectManager = new HelloEffectManager(this);
	
	byte bPathID;
	
	//fonte corrente
	Font Arial = new Font("Arial",Font.BOLD,12);


	byte bySide = 0;
	//phone move time
	public final static int WMOVEPHONETIME = 150;
	//level states
	public final static int WLS_PAUSEEND 		= 0,
													WLS_INITLEVEL 	= 1,
													WLS_STARTGAME 	= 2,
													WLS_GAME 				= 3,
													WLS_GAMEOVER 		= 4,
													WLS_PREGAMEOVER = 5,
													WLS_LEVELUP 		= 6,
													WLS_ENDGAME			= 7;

	//sounds
	JGDLSound[]	p_SndTone				= new JGDLSound[7];
	JGDLSound p_SndNewPhones		= null;
	JGDLSound p_SndPUBomb				= null;
	JGDLSound p_SndPULinCol			= null;
	JGDLSound p_SndClkPhone   	= null;
	JGDLSound p_SndLUBlockShow 	= null;
	JGDLSound p_SndLUBlockExpld = null;
	JGDLSound p_SndMenuOK 			= null;
	
//	HelloPiece [] MyPieces = new HelloPiece[20];

  // level state	
	int State = WLS_STARTGAME;
	//phones fade time accumulator
	JGDLTimeAccumulator PreGameOverTimer    = new JGDLTimeAccumulator();
	
	//phones fade time accumulator
	JGDLTimeAccumulator FadeTimer    = new JGDLTimeAccumulator();

	//newline time accumulator
	JGDLTimeAccumulator NewLineTimer = new JGDLTimeAccumulator();

	//newblock time accumulator
	JGDLTimeAccumulator NewBlockTimer = new JGDLTimeAccumulator();
	
  // phones fade list
	JGDLList FadeList 	= new JGDLList();

	JGDLList FreePieces	= new JGDLList();
	
  //fade flag
	boolean bFading 					= false;
	
	//innsert phones flag
	boolean bInsertPhones 		= false;
	
	//true if the route is ok
	boolean bRouteOK = false;

  //true if the game board is full
	public boolean bIsBoardFull;
	//current phone
	JGDLVector CurrPhone			= new JGDLVector(-1,-1);
	
	//current mouse position
	JGDLVector CurrMousePos		= new JGDLVector(-1,-1);

	//current connection position
	JGDLVector ConnectPos		= new JGDLVector(-1,-1);
	
  //matrix size
	public final static int MatSizeX = 9;
	public final static int MatSizeY = 10;
	
	//game pointer
	JGDLLayer p_LayerGame 	= null;
	JGDLLayer p_LayerFront  = null;
	
	JGDLSprite p_PannelDownload = null;
	JGDLSprite p_Pause					= null;
	JGDLSprite p_LuImages 			= null;
	//Selection sprite
	JGDLSprite p_Selection,
						 //Next line sprite
	           p_NextLine,
						 //level bar sprite 
	           p_LevelBar,
						 //level bar back ground
	           p_LevelBarBack;
	
	//Level Up Screen
	JGDLSprite pr_lubrick  	= null,
						 pr_luback   	= null,
						 pr_lutitle  	= null,
						 pr_download 	= null,
						 pr_continue 	= null,
						 pr_playagain = null,
						 pr_congrats	= null;
	
	//Help Screen
	JGDLSprite	pr_helpback = null,
							pr_helpOK   = null;
							
	
	boolean bLUBricksExploded = false;
	
	//phones matrix
	HelloPiece[] ScreenMatrix = new HelloPiece[MatSizeX*MatSizeY];
	
 // path
	JGDLList Path     	= new JGDLList();
	JGDLList FreeVectors	= new JGDLList();
	int[]    PathAnim 	=	 new int[MatSizeX*MatSizeY];
	
	//phone types
	HelloPiece[] Phones = new HelloPiece[4];
	
	//wire types
	HelloPiece[] Wires  = new HelloPiece[4];
	
	//points
	public int iPoints,
			//phones to collect
	    iPhonesToCollect = 50,
			//phones collected
	    iPhonesCollected,
			//level index
	    iLevel;
	
	//ringo sprites
//	JGDLSprite[] Ringo  = new JGDLSprite[2];

  //upleft corer
  JGDLVector UpLeft = new JGDLVector(9,9);

	//---------------------------------------------------------------------------- 
	// Name: HelloLevel()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	HelloLevel()
	{
	}
	

	//---------------------------------------------------------------------------- 
	// Name: ValidatePos(JGDLVector Pos)
	// Desc: returns true if the given matrix position is valid
	// Pams: matrix posistion
	//---------------------------------------------------------------------------- 
	boolean ValidatePos(JGDLVector Pos)
	{
		return (Pos.fx >= 0.0f && Pos.fy >= 0.0f && (int)Pos.fx < MatSizeX && (int)Pos.fy < MatSizeY);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Matp(JGDLVector Pos)
	// Desc: Returns the pointer to a piece given the matrix position
	// Pams: none
	//---------------------------------------------------------------------------- 
	HelloPiece Mat(int iX,int iY)
	{
		return ScreenMatrix[iX + (iY*MatSizeX)];
	}
	//---------------------------------------------------------------------------- 
	// Name: Matp(JGDLVector Pos)
	// Desc: Returns the pointer to a piece given the matrix position
	// Pams: none
	//---------------------------------------------------------------------------- 
	HelloPiece Matp(JGDLVector Pos)
	{
		return ScreenMatrix[(int)Pos.fx + (int)(((int)Pos.fy)*MatSizeX)];
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetRandomPhone()
	// Desc: returns a randow phone
	// Pams: none
	//---------------------------------------------------------------------------- 
	public HelloPiece GetRandomPhone()
	{
		int index = pr_Main.Randomizer.nextInt();
		index = Math.abs(index);
		index %= Phones.length;
		return Phones[index].GetClone();
	}

	//---------------------------------------------------------------------------- 
	// Name: UpdateMatrixPieces()
	// Desc: Updates the matrix pieces
	// Pams: none
	//---------------------------------------------------------------------------- 
	public boolean UpdateMatrixPieces()
	{
		if(State == WLS_GAME)
		{
			FadeTimer.Update();
			if(FadeTimer.Ended())
			{
	
				int iPieces;
				if(FadeTimer.iTimeLimit != 0)
				{
		      iPieces = FadeTimer.iTimeAccum/FadeTimer.iTimeLimit;
				}
				else
				{
					iPieces = 1;
				}
				int iSize = (int)FadeList.size();
				if(iPieces < iSize)
				{
					if(FadeTimer.iTimeLimit > 3)
					{
						FadeTimer.iTimeLimit -= 3;
					}
				}
				else
				{
					FadeTimer.iTimeLimit = 80;
				}
				FadeTimer.Restart();
				HelloPiece p_Piece;
				for(iSize--; iSize >= 0 && iPieces>0; iSize--)
				{
					p_Piece = (HelloPiece)FadeList.get(iSize);
					iPieces--;
					if(p_Piece.bIsPowerUp)
					{
						JGDLVector Pos = TempPos8;
						Pos.atrib(p_Piece.iIndex%MatSizeX,p_Piece.iIndex/MatSizeX);
						p_Piece.ExecutePU(Pos);
						Pos = null;
					}
					p_Piece.FadeOut();
					FadeList.remove(iSize);
					p_Piece = null;
				}
			}
		}
	
		boolean bRet = false;
	
		int jSize;
	
		bFading = false;
	
		bIsBoardFull = true;
		for(int i = 0; i < ScreenMatrix.length; i++)
		{
			if(null != ScreenMatrix[i])
			{
				ScreenMatrix[i].iIndex = i;
				ScreenMatrix[i].Update();
				if(ScreenMatrix[i].bEffect != HelloPiece.WPE_NONE)
				{
					if(ScreenMatrix[i].EndedEffect())
					{
						jSize = FadeList.size();
						for(jSize--; jSize >= 0; jSize--)
						{
							if(FadeList.get(jSize) == ScreenMatrix[i])
							{
								FadeList.remove(jSize);
							}
						}
						ScreenMatrix[i].Release();
						AddFreePiece(ScreenMatrix[i]);
						ScreenMatrix[i] = null;
						
	
						// play bip phone only game inside
						if (State == WLS_GAME)
						{
							int iIndex = Math.abs(pr_Main.Randomizer.nextInt())%7;
							if (null != p_SndTone[iIndex])
							{
								p_SndTone[iIndex].Play();
							}
						}
						bRet = true;
					}
					else
					{
						bFading = true;
					}
				}
			}
			else
			{
				bIsBoardFull = false;
			}
		}
		
		if(null != p_Selection && ValidatePos(CurrPhone) && null != Matp(CurrPhone))
		{
			Matp(CurrPhone).pr_Sprite.GetCenterPos(TempPos1);
			p_Selection.SetCenterPos(TempPos1);
		}
	
		return bRet;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: PosFromMatrix(int iX, int iY)
	// Desc: returns the position of the pice given the position on the matrix
	// Pams: matrix position
	//---------------------------------------------------------------------------- 
	void PosFromMatrix(JGDLVector pRet, int iX, int iY)
	{
		pRet.fx = UpLeft.fx + (float)(iX*HelloPiece.WPIECEWIDTH);
		pRet.fy = UpLeft.fy + (float)(iY*HelloPiece.WPIECEHEIGHT);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: PosFromMatrix(int iX, int iY)
	// Desc: returns the position of the pice given the position on the matrix
	// Pams: matrix position
	//---------------------------------------------------------------------------- 
	void PosFromMatrix(JGDLVector pRet, JGDLVector MatPos)
	{
		PosFromMatrix(pRet, (int)MatPos.fx,(int)MatPos.fy);
	}
	//---------------------------------------------------------------------------- 
	// Name: NewLineUp()
	// Desc: creates a new line of phones
	// Pams: none
	//---------------------------------------------------------------------------- 
	boolean NewLineUp()
	{
	//	iLines++;
		boolean bRet = true;
		boolean bMakeRoute = false;
		//moves lines from up to down
		int i,j;
		int iMin = -1,iMax = -1;
		JGDLVector Pos;
		for(i = 0 ; i < MatSizeX; i++)
		{
			for(j = MatSizeY -2; j >= 0; j--)
			{
				if(null != ScreenMatrix[i + j*MatSizeX] && !ScreenMatrix[i + j*MatSizeX].bUp)
				{
					if(null == ScreenMatrix[i + (j+1)*MatSizeX])
					{
						iMin = (iMin == -1)? i: iMin;
						iMax = i;
						Pos = TempPos2;
						PosFromMatrix(Pos, i,j+1);
						ScreenMatrix[i + (j+1)*MatSizeX] = ScreenMatrix[i + j*MatSizeX];
						ScreenMatrix[i + j*MatSizeX] = null;
						ScreenMatrix[i + (j+1)*MatSizeX].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
						ScreenMatrix[i + (j+1)*MatSizeX].iIndex = (((j+1)*MatSizeX) + i);
						if((int)CurrPhone.fx == i && (int)CurrPhone.fy == j && !bMakeRoute)
						{
							CurrPhone.fy++;
							bMakeRoute = true;
						}
						Pos = null;
					}
				}//if
			}//for
		}//for
	
		//create a new line up
		for(i = 0 ; i < MatSizeX; i++)
		{
			if(null == ScreenMatrix[i])
			{
				ScreenMatrix[i] = GetRandomPhone();
				Pos = TempPos2;
				PosFromMatrix(Pos,i,0);
				Pos.fy -= HelloPiece.WPIECEHEIGHT;
				ScreenMatrix[i].pr_Sprite.position.fx = Pos.fx;
				ScreenMatrix[i].pr_Sprite.position.fy = Pos.fy;
				Pos.fy += HelloPiece.WPIECEHEIGHT;
				ScreenMatrix[i].pr_Sprite.MoveTo(Pos, WMOVEPHONETIME);
				//is going down
				ScreenMatrix[i].bUp = false;
				bRet = false;
				Pos = null;
			}
		}
	
		CheckPowerUpLine(0, false,iMin, iMax);
		if(bMakeRoute)
		{
			TraceRoute(CurrPhone, CurrMousePos);
		}
		//play sound new phones comming
		if ((null != p_SndNewPhones) && (State == WLS_GAME))
		{
			p_SndNewPhones.Play();
		}
		return bRet;
  }
	//---------------------------------------------------------------------------- 
	// Name: NewColLeft()
	// Desc: creates a new column of phones
	// Pams: none
	//---------------------------------------------------------------------------- 
  boolean NewColRight()
  {
	 	boolean bRet = true;
		boolean bMakeRoute = false;
		int i =0 ,j =0 ;
		int iMin = -1,iMax = -1;
		JGDLVector Pos;
		for(j = 0; j < MatSizeY; j++)
		{
			for(i = 0; i < MatSizeX -1; i++)        
			{
				if(null == ScreenMatrix[i + j*MatSizeX] && null != ScreenMatrix[(i+1) + j*MatSizeX])
				{
					iMin = (iMin == -1)? j: iMin;
					iMax = j;
					if((int)CurrPhone.fx == i+1 && (int)CurrPhone.fy == j)
					{
						CurrPhone.fx = (float)i;
						bMakeRoute = true;
					}
					Pos = TempPos2;
					PosFromMatrix(Pos, i,j);
					ScreenMatrix[(i+1) + j*MatSizeX].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
					ScreenMatrix[i + j*MatSizeX] 					= ScreenMatrix[(i+1) + j*MatSizeX];
					ScreenMatrix[i + j*MatSizeX].iIndex 	= ((j*MatSizeX) + i);
					ScreenMatrix[(i+1) + j*MatSizeX] 			= null;
					Pos = null;
				}
			}
		}
		//creates the new phones
	
		// already done by the last for
		//i = MatSizeX -1;
	
		for(j = 0; j < MatSizeY; j++)
		{
			if(null == ScreenMatrix[i + j*MatSizeX])
			{
				bRet = false;
				ScreenMatrix[i + j*MatSizeX] = GetRandomPhone();
				Pos = TempPos2;
				PosFromMatrix(Pos, i,j);
				Pos.fx += HelloPiece.WPIECEWIDTH;
				ScreenMatrix[i + j*MatSizeX].pr_Sprite.position.fx = Pos.fx;
				ScreenMatrix[i + j*MatSizeX].pr_Sprite.position.fy = Pos.fy;
				Pos.fx -= HelloPiece.WPIECEWIDTH;
				ScreenMatrix[i + j*MatSizeX].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
				ScreenMatrix[i + j*MatSizeX].bUp = false;
			}
		}
		CheckPowerUpColumn(MatSizeX -1, false, iMin, iMax);
	
		if(bMakeRoute)
		{
			TraceRoute(CurrPhone, CurrMousePos);
		}
		//play sound new phones comming
		if ((null != p_SndNewPhones) && (State == WLS_GAME))
		{
			p_SndNewPhones.Play();
		}
		return bRet;
  }

	//---------------------------------------------------------------------------- 
	// Name: UpdatePhonePos()
	// Desc: updates phone positions
	// Pams: none
	//---------------------------------------------------------------------------- 
  void UpdatePhonePos()
  {
	 
		boolean bMove = false;
	
		do
		{
			JGDLVector Pos;
			bMove = false;
			//moves lines from up to down
			int i,j;
			for(i = 0 ; i < MatSizeX; i++)
			{
				for(j = 0; j < MatSizeY -1 ; j++)
				{
					if(null == ScreenMatrix[i + j*MatSizeX] && 
					   null != ScreenMatrix[i + (j+1)*MatSizeX] && 
					   !ScreenMatrix[i + (j+1)*MatSizeX].bUp)
					{
						bMove							= true;
						ScreenMatrix[i + j*MatSizeX] = ScreenMatrix[i + (j+1)*MatSizeX];
	
						if((int)CurrPhone.fx == i && (int)CurrPhone.fy == j+1)
						{
							CurrPhone.fy--;
						}
						ScreenMatrix[i + j*MatSizeX].iIndex	= (j*MatSizeX) + i;
						ScreenMatrix[i + (j+1)*MatSizeX] = null;
						Pos = TempPos2;
						PosFromMatrix(Pos, i,j);
						ScreenMatrix[i + j*MatSizeX].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
						Pos = null;
					}//if
				}//for
			}//for
	
			for(i = 0 ; i < MatSizeX; i++)
			{
				for(j = MatSizeY -1 ; j > 0  ; j--)
				{
					if(null == ScreenMatrix[i + j*MatSizeX] &&
					   null != ScreenMatrix[i + (j-1)*MatSizeX] && 
					   ScreenMatrix[i + (j-1)*MatSizeX].bUp)
					{
						bMove				= true;
						ScreenMatrix[i + j*MatSizeX]		= ScreenMatrix[i + (j-1)*MatSizeX];
						if((int)CurrPhone.fx == i && (int)CurrPhone.fy ==j-1)
						{
							CurrPhone.fy++;
						}
						ScreenMatrix[i + j*MatSizeX].iIndex	= (j*MatSizeX) + i;
						ScreenMatrix[i + (j-1)*MatSizeX]	  = null;
						Pos = TempPos2;
						PosFromMatrix(Pos, i,j);
						ScreenMatrix[i + j*MatSizeX].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
						Pos = null;
					}//if
				}//for
			}//for
		}while(bMove);
 	}
	//---------------------------------------------------------------------------- 
	// Name: InsertPhones()
	// Desc: insert phones into the board
	// Pams: none
	//---------------------------------------------------------------------------- 
  public boolean InsertPhones()
  {
  	bySide = (byte)((bySide+1) % 4);
		boolean bGameOver = false;
		switch(bySide)
		{
			case 0:
			{
				bGameOver = NewColLeft();
				break;
			}
			case 1:
			{
				bGameOver = NewLineUp();
				break;
			}
			case 2:
			{
				bGameOver = NewColRight();
				break;
			}
			case 3:
			{
				bGameOver = NewLineDown();
				break;
			}
		}
		if(bGameOver)
		{
//			pr_Level->ChangeState(WLS_GAMEOVER);
			return false;
		}
		return true;  	
  }
	//---------------------------------------------------------------------------- 
	// Name: NewColLeft()
	// Desc: creates a new column of phones
	// Pams: none
	//---------------------------------------------------------------------------- 
  boolean NewColLeft()
  {
		boolean bRet = true;
		int i,j;
		boolean bMakeRoute = false;
		int iMin = -1,iMax = -1;
	
		JGDLVector Pos;
		for(j = 0; j < MatSizeY; j++)
		{
			for(i = MatSizeX - 1; i > 0 ; i--)        
			{
				if(null == ScreenMatrix[i + j*MatSizeX] && null != ScreenMatrix[(i-1) + j*MatSizeX])
				{
					iMin = (iMin == -1)? j: iMin;
					iMax = j;
					if((int)CurrPhone.fx == i-1 && (int)CurrPhone.fy == j)
					{
						CurrPhone.fx = (int)i;
						bMakeRoute = true;
					}
					Pos = TempPos2;
					PosFromMatrix(Pos,i,j);
					ScreenMatrix[(i-1) + j*MatSizeX].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
					ScreenMatrix[i + j*MatSizeX] 					= ScreenMatrix[(i-1) + j*MatSizeX];
					ScreenMatrix[i + j*MatSizeX].iIndex 	= (((j)*MatSizeX) + i);
					ScreenMatrix[(i-1) + j*MatSizeX] 			= null;
					Pos = null;
				}
			}
		}
		//creates the new phones
		for(j = 0; j < MatSizeY; j++)
		{
			if(null == ScreenMatrix[j*MatSizeX])
			{
				bRet = false;
				ScreenMatrix[j*MatSizeX] = GetRandomPhone();
				Pos = TempPos2;
				PosFromMatrix(Pos,0,j);
				Pos.fx -= HelloPiece.WPIECEWIDTH;
				ScreenMatrix[j*MatSizeX].pr_Sprite.position.fx = Pos.fx;
				ScreenMatrix[j*MatSizeX].pr_Sprite.position.fy = Pos.fy;
	
				Pos.fx += HelloPiece.WPIECEWIDTH;
				ScreenMatrix[j*MatSizeX].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
				ScreenMatrix[j*MatSizeX].bUp = true;
			}
		}
		CheckPowerUpColumn(0, true,iMin,iMax);
	
		if(bMakeRoute)
		{
			TraceRoute(CurrPhone, CurrMousePos);
		}
		//play sound new phones comming
		if ((null != p_SndNewPhones) && (State == WLS_GAME))
		{
			p_SndNewPhones.Play();
		}
		return bRet;
  }
	//---------------------------------------------------------------------------- 
	// Name: NewLineDown
	// Desc: creates a new line of phones
	// Pams: none
	//---------------------------------------------------------------------------- 
	boolean NewLineDown()
	{
	  boolean bRet = true;
		boolean bMakeRoute = false;
		//moves lines from up to down
		int i,j;
		int iMin = -1,iMax = -1;
		JGDLVector Pos;
		for(i = 0 ; i < MatSizeX; i++)
		{
			for(j = 1; j < MatSizeY; j++)
			{
				
				if(null != ScreenMatrix[i + (j*MatSizeX)] && ScreenMatrix[i + (j*MatSizeX)].bUp)
				{
					if(null == ScreenMatrix[i + ((j-1)*MatSizeX)])
					{
						iMin = (iMin == -1)? i: iMin;
						iMax = i;
						Pos = TempPos2;
						PosFromMatrix(Pos,i,j-1);
						ScreenMatrix[i + (j-1)*MatSizeX] = ScreenMatrix[i + (j*MatSizeX)];
						ScreenMatrix[i + j*MatSizeX] = null;
						ScreenMatrix[i + (j-1)*MatSizeX].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
						ScreenMatrix[i + (j-1)*MatSizeX].iIndex = i + ((j-1)*MatSizeX);
						if((int)CurrPhone.fx == i && (int)CurrPhone.fy == j && !bMakeRoute)
						{
							CurrPhone.fy --;
							bMakeRoute = true;
						}
						Pos = null;
					}
				}//if
			}//for
		}//for
	
		//create a new line down
		for(i = 0 ; i < MatSizeX; i++)
		{
			if(ScreenMatrix[i + ((MatSizeY-1)*MatSizeX)] == null)
			{
				ScreenMatrix[i + ((MatSizeY-1)*MatSizeX)] = GetRandomPhone();
				Pos = TempPos2;
				PosFromMatrix(Pos,i,MatSizeY-1);
				Pos.fy += HelloPiece.WPIECEHEIGHT;
				ScreenMatrix[i + ((MatSizeY-1)*MatSizeX)].pr_Sprite.position.fx = Pos.fx;
				ScreenMatrix[i + ((MatSizeY-1)*MatSizeX)].pr_Sprite.position.fy = Pos.fy;
				Pos.fy -= HelloPiece.WPIECEHEIGHT;
				ScreenMatrix[i + ((MatSizeY-1)*MatSizeX)].pr_Sprite.MoveTo(Pos, WMOVEPHONETIME);
				//is going up
				ScreenMatrix[i + ((MatSizeY-1)*MatSizeX)].bUp = true;
				bRet = false;
				Pos = null;
			}
		}
		CheckPowerUpLine(MatSizeY-1, true, iMin, iMax);
	
		if(bMakeRoute)
		{
			TraceRoute(CurrPhone,CurrMousePos);
		}
		
		if ((null != p_SndNewPhones) && (State == WLS_GAME))
		{
			p_SndNewPhones.Play();
		}
		return bRet;
	}
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a cena
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		ClearScreenMatrix();
		ScreenMatrix = null;
		if(null != Phones)
		{
			for(int i =0; i < Phones.length; i++)
			{
				if(null != Phones[i])
				{
					Phones[i].Release();
					Phones[i] = null;
				}
				
				if(null != Wires[i])
				{
					Wires[i].Release();
					Wires[i] = null;
				}
			}
			Phones = null;
			Wires  = null; 
		}
		super.Release();
		/**** Colocar o release da animações do ringo ****/
	
	  UpLeft = null;

  	JGDLVector CurrPhone			= null;
		JGDLVector CurrMousePos		= null;

		NewLineTimer 			= null;
		FadeTimer    			= null;
		PreGameOverTimer 	= null;
		if(null != Search)
		{
			Search.Release();
			Search = null;
		}
		if(null != PUConfigs)
		{
			PUConfigs[0] = null;
			PUConfigs[1] = null;
			PUConfigs[2] = null;
			PUConfigs = null;
		}		
		
		if(null != p_SndTone)
		{
			p_SndTone[0] = null;
			p_SndTone[1] = null;
			p_SndTone[2] = null;
			p_SndTone[3] = null;
			p_SndTone[4] = null;
			p_SndTone[5] = null;
			p_SndTone[6] = null;
			p_SndTone    = null;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: Inicializa a cena. Essa rotina deve ser reescrita na cena, para que se possa
	//		   criar todos os objetos e fazer inicializações necessárias.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Initialize()
	{
		//background layer
		JGDLLayer p_LayerBKG = null;
		//adicionando fundo
		p_LayerBKG = CreateLayer(new JGDLVector(448,336));
		JGDLSprite p_Center = p_LayerBKG.CreateSprite("spr_boardcenter.gif", new JGDLVector(285,315));
		p_Center.position.fx = 7.0f;
		p_Center.position.fy = 7.0f;
		//criando a layer de jogo
		p_LayerGame		= pr_Main.GetCurrentScene().CreateLayer(new JGDLVector(32.0f,32.0f));
		
		p_LayerFront  = pr_Main.GetCurrentScene().CreateLayer(new JGDLVector(32.0f,32.0f));
		p_LayerFront.bVisible = false;
		
		//up border
		JGDLSprite p_up = p_LayerFront.CreateSprite("spr_borderup.gif",new JGDLVector(292,7));
		p_up.position.fx = 0;
		p_up.position.fy = 0;
		
		//left border
		JGDLSprite p_left = p_LayerFront.CreateSprite("spr_borderleft.gif",new JGDLVector(7,329));
		p_left.position.fx = 0;
		p_left.position.fy = 7;
		
		//down border
		JGDLSprite p_down = p_LayerFront.CreateSprite("spr_borderdown.gif",new JGDLVector(285,14));
		p_down.position.fx = 7;
		p_down.position.fy = 322;

		//p_right pannel
		JGDLSprite p_right = p_LayerFront.CreateSprite("spr_rightpannel.gif",new JGDLVector(156,336));
		p_right.position.fx = 292;
		p_right.position.fy = 0;
		
		p_PannelDownload = p_LayerFront.CreateSprite("btn_DownloadFreeTrialMenu.gif",new JGDLVector(79,23));
		p_PannelDownload.position.fx = 302;
		p_PannelDownload.position.fy = 300+100;
		
		int [] Anim = {0};
		p_PannelDownload.AddAnimation(10,false,Anim);
		
		Anim[0] = 1;
		p_PannelDownload.AddAnimation(10,false,Anim);
	
		p_Pause = p_LayerFront.CreateSprite("btn_Pause.gif",new JGDLVector(58,23));
		p_Pause.position.fx = 302 + 78;
		p_Pause.position.fy = p_PannelDownload.position.fy;

		Anim[0] = 0;
		p_Pause.AddAnimation(10,false,Anim);
		Anim[0] = 1;
		p_Pause.AddAnimation(10,false,Anim);
	
		//sprite de próxima linha
		p_NextLine = p_LayerFront.CreateSprite("spr_NextLine.gif",new JGDLVector(284,7));
		p_NextLine.position.fx = 7;
		p_NextLine.position.fy = 322;
		int[] NLFrames = {0};
		p_NextLine.AddAnimation(15,true,NLFrames);
		p_NextLine.SetCurrentAnimation(0);
		
		//sprite de seleção
		p_Selection = p_LayerGame.CreateSprite("spr_selection.gif",new JGDLVector(32,32));
		p_Selection.bVisible 		= false;
		p_Selection.position.fx = 100;
		p_Selection.position.fy = 100;
		
		//sprite de completamento de fase
		p_LevelBarBack = p_LayerFront.CreateSprite("spr_levelbar.gif",new JGDLVector(14,140));
		p_LevelBarBack.position.fx = 420;
		p_LevelBarBack.position.fy = 150;
		
		p_LevelBar = p_LevelBarBack.GetClone(true);
		int[] LBFrames = {1};
		p_LevelBar.AddAnimation(15,true,LBFrames);
		p_LevelBar.SetCurrentAnimation(0);

		//create phones, phones end and wires
		CreatePhones();
		
		//animacoes do Ringo
/*		Ringo[0] = p_LayerFront.CreateSprite("RingoStopped.gif",new JGDLVector(105,175));
		Ringo[0].position.fx = 300;
		Ringo[0].position.fy = 120;*/
		
	
		EffectManager.Initialize();
		InitSounds();

/*		for(int i = 0; i < 20; i++)
		{
			MyPieces[i] = GetRandomPhone();
		}*/
		p_LuImages = p_LayerGame.CreateSprite("spr_TelasLevelUp.gif", new JGDLVector(97,110));
		int []LuAnims = {0,1,-1};
		p_LuImages.AddAnimation(1,true,LuAnims);
		p_LuImages.SetCurrentAnimation(0);
		p_LuImages.bVisible = false;
		
		PUConfigs[0] = new puConfig();
		PUConfigs[1] = new puConfig();
		PUConfigs[2] = new puConfig();
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: InitSounds()
	// Desc: initializes the sounds
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void InitSounds()
	{
		//tone sounds
		p_SndTone[0] = pr_Main.SoundManager.LoadSound("sfx_Tone1.au");
		p_SndTone[1] = pr_Main.SoundManager.LoadSound("sfx_Tone2.au");
		p_SndTone[2] = pr_Main.SoundManager.LoadSound("sfx_Tone3.au");
		p_SndTone[3] = pr_Main.SoundManager.LoadSound("sfx_Tone4.au");
		p_SndTone[4] = pr_Main.SoundManager.LoadSound("sfx_Tone5.au");
		p_SndTone[5] = pr_Main.SoundManager.LoadSound("sfx_Tone6.au");
		p_SndTone[6] = pr_Main.SoundManager.LoadSound("sfx_Tone7.au");
		
		p_SndNewPhones		= pr_Main.SoundManager.LoadSound("sfx_NewPhones.au");
		p_SndPUBomb				= pr_Main.SoundManager.LoadSound("sfx_PUBomb.au");
		p_SndPULinCol			= pr_Main.SoundManager.LoadSound("sfx_PULinCol.au");
		p_SndClkPhone			= pr_Main.SoundManager.LoadSound("sfx_ClickPhone.au");
		p_SndLUBlockShow	= pr_Main.SoundManager.LoadSound("sfx_LUBlockShow.au");
		p_SndLUBlockExpld	= pr_Main.SoundManager.LoadSound("sfx_LUBlockExplode.au");
		p_SndMenuOK				= pr_Main.SoundManager.LoadSound("sfx_MenuOpen.au");
		
		
	}

	//---------------------------------------------------------------------------- 
	// Name: CheckPowerUpLine(int iLine, bool bUp, int iMinCol, int iMaxCol)
	// Desc: checks a insertion of a power up in the lines
	// Pams: columns index, up flag, min line, max line
	//---------------------------------------------------------------------------- 
	public void CheckPowerUpLine(int iLine, boolean bUp, int iMinCol, int iMaxCol)
	{
		iMinCol = (iMinCol< 0 || iMinCol >= MatSizeX)? 0 : iMinCol;
		iMaxCol = (iMaxCol < 0 || iMaxCol >= MatSizeX)? MatSizeX : iMaxCol;
		if(iMinCol >= iMaxCol)
		{
			iMinCol = 0;
			iMaxCol = MatSizeX;
		}
		iInserts++;
		if(0 != iInserts && iLine >= 0 && iLine < MatSizeY)
		{
			JGDLVector Pos;
			int iSize = iPuConfigs;
			for(int i = 0; i < iSize; i++)
			{
				puConfig cfg = PUConfigs[i];
				if(iInserts >= cfg.iFirst && 0 == ((iInserts - cfg.iFirst)%cfg.iInserts))
				{
					int iRand = Math.abs(pr_Main.Randomizer.nextInt());
					int iCol = iMinCol + (iRand%(iMaxCol - iMinCol));
					int index = iCol + (iLine*MatSizeX);

					if(null != ScreenMatrix[index])
					{
						ScreenMatrix[index].Release();
						AddFreePiece(ScreenMatrix[index]);
						ScreenMatrix[index] = null;
					}
					ScreenMatrix[index] 						= PowerUps[cfg.bType].GetClone();
					ScreenMatrix[index].bType				= cfg.bType;
					ScreenMatrix[index].bIsPowerUp	= true;
					ScreenMatrix[index].bUp					= bUp;
	
					Pos = TempPos2;
					PosFromMatrix(Pos, iCol,iLine);
	
					Pos.fy += (bUp)? (float)HelloPiece.WPIECEHEIGHT: -(float)HelloPiece.WPIECEHEIGHT;
					ScreenMatrix[index].pr_Sprite.position.atrib(Pos);
	
					Pos.fy += (bUp)? -(float)HelloPiece.WPIECEHEIGHT: (float)HelloPiece.WPIECEHEIGHT;
					ScreenMatrix[index].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
				}
			}
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: AddFreePiece(HelloPiece p_Piece)
	// Desc: Adds a piece to the free list
	// Pams: piece
	//---------------------------------------------------------------------------- 
	public void AddFreePiece(HelloPiece p_Piece)
	{
		int iSize = FreePieces.size();
		for(iSize--; iSize >= 0; iSize--)
		{
			if(p_Piece == FreePieces.get(iSize))
			{
				return;
			}
		}
		FreePieces.push_back(p_Piece);
	}
	//---------------------------------------------------------------------------- 
	// Name: ChangeState(int iNewState)
	// Desc: changes the game state
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void ChangeState(int iNewState)
	{
		pr_Main.bPauseOnLostFocus = true;
		float fPos = 165.0f;
		switch(iNewState)
		{
			case WLS_ENDGAME:
			{
				pr_Main.bPauseOnLostFocus = false;
				//clears the matrix
				for(int i = 0; i < ScreenMatrix.length; i++)
				{
					if(null != ScreenMatrix[i])
					{
						ScreenMatrix[i].Release();
						AddFreePiece(ScreenMatrix[i]);
						ScreenMatrix[i] = null;
					}
				}
				p_NextLine.window.fx = 0.0f;
				iPhonesCollected = 0;

				JGDLVector NewPosBack				= TempPos3;
				NewPosBack.atrib(7,7);
				
				JGDLVector NewPosPlayagain	= TempPos4;
				NewPosPlayagain.atrib(fPos,298);
				
				JGDLVector NewPosDownload		= TempPos5;
				NewPosDownload.atrib(15,298);
				pr_congrats.position.fx			= 7.0f - 400.0f;
				pr_congrats.position.fy			= 7.0f;
				pr_congrats.MoveTo(NewPosBack,500);
				
				pr_playagain.position.fx = fPos - 400.0f;
				pr_playagain.MoveTo(NewPosPlayagain,500);
				
				pr_download.position.fx = 15.0f - 400.0f;
				pr_download.MoveTo(NewPosDownload,500);
	
	
				NewPosBack 			= null;
				NewPosPlayagain = null;
				NewPosDownload  = null;
				break;
			}
			case WLS_PREGAMEOVER:
			{
				PreGameOverTimer.Init(pr_Main,2000);
				for(int i =0; i < ScreenMatrix.length; i++)
				{
					if(null != ScreenMatrix[i])
					{
						ScreenMatrix[i].bShake = true;
					}
				}
				break;
			}
			
			case WLS_GAMEOVER:
			{
				pr_Main.bPauseOnLostFocus = false;
				//clears the matrix
				for(int i = 0; i < ScreenMatrix.length; i++)
				{
					if(null != ScreenMatrix[i])
					{
						ScreenMatrix[i].Release();
						AddFreePiece(ScreenMatrix[i]);
						ScreenMatrix[i] = null;
					}
				}
				p_NextLine.window.fx = 0.0f;
				iPhonesCollected = 0;

				JGDLVector NewPosBack				= TempPos3;
				NewPosBack.atrib(7,7);
				
				JGDLVector NewPosPlayagain	= TempPos4;
				NewPosPlayagain.atrib(fPos,298);
				
				JGDLVector NewPosDownload		= TempPos5;
				NewPosDownload.atrib(15,298);
				
				JGDLVector NewPosTitle			= TempPos6;

				if(pr_Main.iLang == JGDLMain.WLANG_ENG)
				{
					NewPosTitle.atrib(66,7);
				}
				else
				{
					NewPosTitle.atrib(48,7);
				}
				
				pr_luback.position.fx				= 7.0f - 400.0f;
				
				pr_luback.MoveTo(NewPosBack,500);
				
				pr_playagain.position.fx = fPos - 400.0f;
				pr_playagain.MoveTo(NewPosPlayagain,500);
				
				pr_download.position.fx = 15.0f - 400.0f;
				pr_download.MoveTo(NewPosDownload,500);
	
				pr_lutitle.SetCurrentAnimation(1);
				pr_lutitle.position.fx = 68.0f - 400.0f;
				pr_lutitle.position.fy = 7.0f;
				pr_lutitle.MoveTo(NewPosTitle,500);
	
				NewPosBack 			= null;
				NewPosPlayagain = null;
				NewPosTitle			= null;
				NewPosDownload  = null;
				break;
			}
			case WLS_STARTGAME:
			case WLS_INITLEVEL:
			{
				if(iLevel>=9)
				{
					ChangeState(WLS_ENDGAME);
					return;
				}
				pr_Main.bPauseOnLostFocus = false;
				//clears the matrix
				for(int i = 0; i < ScreenMatrix.length; i++)
				{
					if(null != ScreenMatrix[i])
					{
						ScreenMatrix[i].Release();
						AddFreePiece(ScreenMatrix[i]);
						ScreenMatrix[i] = null;
					}
				}
				p_NextLine.window.fx = 0.0f;
				iPhonesCollected = 0;
				iLevel++;

				JGDLVector NewPosBack 		 = TempPos3;
				NewPosBack.atrib(7,7);
				
				JGDLVector NewPosContinue  = TempPos4;
				NewPosContinue.atrib(fPos,298);
				
				JGDLVector NewPosDownload  = TempPos5;
				NewPosDownload.atrib(15,298);
				
				JGDLVector NewPosTitle 		 = TempPos6;

				if(pr_Main.iLang == JGDLMain.WLANG_ENG)
				{
					NewPosTitle.atrib(66,7);
				}
				else
				{
					NewPosTitle.atrib(48,7);
				}
				
				pr_luback.position.fx 	= 7.0f - 400.0f;
				pr_luback.MoveTo(NewPosBack,500);
				
				pr_continue.position.fx = NewPosContinue.fx - 400.0f;
				pr_continue.MoveTo(NewPosContinue,500);
				
				pr_download.position.fx = 15.0f - 400.0f;
				pr_download.MoveTo(NewPosDownload,500);
	
				pr_lutitle.SetCurrentAnimation(0);
				pr_lutitle.position.fx = 68.0f - 400.0f;
				pr_lutitle.position.fy = 7.0f;
				pr_lutitle.MoveTo(NewPosTitle,500);
				p_LuImages.position.fx = pr_luback.position.fx + 68;
				p_LuImages.position.fy = pr_luback.position.fy + 167;
	
				NewPosBack 			= null;
				NewPosContinue  = null;
				NewPosTitle			= null;
				NewPosDownload  = null;
			
				break;
			}
			case WLS_GAME:
			{
				InitGameMode();
				break;
			}
		}
		State = iNewState;
		float fx = 298;
		if(State == WLS_GAME || State == WLS_LEVELUP)
		{
			if(p_PannelDownload.position.fy == 300+100)
			p_PannelDownload.position.fx = fx;
			p_PannelDownload.MoveTo(fx,300.0f,500);
		}
		else
		{
			if(p_PannelDownload.position.fy == 300.0f)
			{
				p_PannelDownload.MoveTo(fx,400.0f,500);
			}
		}
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
	// Name: HandleLevelUp()
	// Desc: Handles the level up game state
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void HandleLevelUp()
	{
		if(FadeList.size() > 0)
		{
			int iSize = (int)FadeList.size();
			for(iSize--; iSize >=0; iSize--)
			{
				((HelloPiece)FadeList.get(iSize)).FadeOut();
			}
			
			if(UpdateMatrixPieces())
			{
				UpdatePhonePos();
			}
		}
		else
		{
	
			NewBlockTimer.Update();
			
			if(NewBlockTimer.Ended())
			{
				NewBlockTimer.Restart();
				boolean bInsert = false;
				int i;
				int iFreePieces = FreePieces.size();
				
				for(i = 0; i < ScreenMatrix.length; i++)			
				{
					if(null == ScreenMatrix[i])
					{
						if(iFreePieces > 0)
						{
							ScreenMatrix[i] = (HelloPiece)FreePieces.get(iFreePieces-1);
							iFreePieces--;
							FreePieces.remove(iFreePieces);
						}
						else
						{
							ScreenMatrix[i] = new HelloPiece(this);
						}
						ScreenMatrix[i].pr_Sprite = pr_lubrick.GetClone(false);
						ScreenMatrix[i].pr_Sprite.position.fx = (float)(i%MatSizeX);
						ScreenMatrix[i].pr_Sprite.position.fx *= (float)HelloPiece.WPIECEWIDTH;
						ScreenMatrix[i].pr_Sprite.position.fx += UpLeft.fx;
						ScreenMatrix[i].pr_Sprite.position.fy = (float)(i/MatSizeX);
						ScreenMatrix[i].pr_Sprite.position.fy *= (float)HelloPiece.WPIECEHEIGHT;
						ScreenMatrix[i].pr_Sprite.position.fy += UpLeft.fx;
						bInsert = true;
						break;
					}
				}
				if(!bInsert)
				{
					if(!bLUBricksExploded)
					{
						bLUBricksExploded = true;
						for(i = 0; i < ScreenMatrix.length; i++)			
						{
							if(null != ScreenMatrix[i])
							{
								ScreenMatrix[i].Explode();
							}
						}
						p_SndLUBlockShow.Stop();
						p_SndLUBlockExpld.Play();
					}
					else
					{
						boolean bEnded = true;
						for(i = 0; i < ScreenMatrix.length; i++)			
						{
							if(null != ScreenMatrix[i])
							{
								if(!ScreenMatrix[i].EndedEffect())
								{
									bEnded = false;
									break;
								}
							}
						}
						
						if(bEnded)
						{
							ChangeState(WLS_INITLEVEL);
							return;
						}
					}
				}
			}
			else
			{
				if(!bLUBricksExploded)
				{
					p_SndLUBlockShow.Play();
				}
			}
			for(int i = 0; i < ScreenMatrix.length; i++)
			{
				if(null != ScreenMatrix[i])
				{
					ScreenMatrix[i].Update();
					if(ScreenMatrix[i].bEffect == HelloPiece.WPE_FADE && ScreenMatrix[i].EndedEffect())
					{
						ScreenMatrix[i].Release();
						AddFreePiece(ScreenMatrix[i]);
						ScreenMatrix[i] = null;
					}
				}
			}
		}//else
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CheckPowerUpColumn
	// Desc: checks a insertion of a power up in the columns
	// Pams: columns index, up flag, min line, max line
	//---------------------------------------------------------------------------- 
	public void CheckPowerUpColumn(int iCol, boolean bUp,int iMinLine, int iMaxLine)
	{
		iMinLine = (iMinLine < 0 || iMinLine >= MatSizeY)? 0: iMinLine; 
		iMaxLine = (iMaxLine < 0 || iMaxLine >= MatSizeY)? MatSizeY : iMaxLine;
		
		if(iMinLine >= iMaxLine)
		{
			iMinLine = 0;
			iMaxLine = MatSizeY;
		}
	
		iInserts++;
		if(iInserts != 0 && iCol >= 0 && iCol < MatSizeX)
		{
			JGDLVector Pos;
			int iSize = (int)iPuConfigs;
			for(int i = 0; i < iSize; i++)
			{
				puConfig cfg = PUConfigs[i];
				if(iInserts >= cfg.iFirst && 0 == ((iInserts - cfg.iFirst)%cfg.iInserts))
				{
					int iRand = Math.abs(pr_Main.Randomizer.nextInt());
					int iLine = iMinLine + (iRand%(iMaxLine - iMinLine));
					int index = iCol + (iLine*MatSizeX);
					if(null != ScreenMatrix[index])
					{
						ScreenMatrix[index].Release();
						AddFreePiece(ScreenMatrix[index]);
						ScreenMatrix[index] = null;
					}
					ScreenMatrix[index]	 						= PowerUps[cfg.bType].GetClone();
					ScreenMatrix[index].bIsPowerUp	= true;
					ScreenMatrix[index].bUp					= bUp;
	
					Pos = TempPos2;
					PosFromMatrix(Pos,iCol,iLine);
	
					Pos.fx += (bUp)? -(float)HelloPiece.WPIECEWIDTH: (float)HelloPiece.WPIECEWIDTH;
					ScreenMatrix[index].pr_Sprite.position.atrib(Pos);
	
					Pos.fx += (bUp)? (float)HelloPiece.WPIECEWIDTH: -(float)HelloPiece.WPIECEWIDTH;
					ScreenMatrix[index].pr_Sprite.MoveTo(Pos,WMOVEPHONETIME);
				}
			}
			Pos = null;
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: AddPUConfig(int iInserts, int iType)
	// Desc: Adds a Power Up Configuration
	// Pams: Lines/Columns to create PU, Power up type
	//---------------------------------------------------------------------------- 
	public void AddPUConfig(int iFirst, int iInserts, byte bType)
	{
		PUConfigs[iPuConfigs].iFirst 		= iFirst;
		PUConfigs[iPuConfigs].iInserts 	= iInserts;
		PUConfigs[iPuConfigs].bType     = bType;
		iPuConfigs++;
	}
	//---------------------------------------------------------------------------- 
	// Name: InitGameMode()
	// Desc: initializes the game mode
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void InitGameMode()
	{
/*		try
		{
			pr_Main.VideoManager.Media.waitForAll();
		}
		catch(Exception e){}*/
//		pr_Main.VideoManager.BackBuffer.image.flush();
//		pr_Main.VideoManager.i
		
		NewBlockTimer.Init(pr_Main,50);
		bLUBricksExploded = false;
		bFading 					= false;
		bInsertPhones 		= false;
		CurrPhone.fx      = -1.0f;
		CurrPhone.fy      = -1.0f;
		CurrMousePos.fx		= -1.0f;
		CurrMousePos.fy		= -1.0f;
		FadeList.clear();
		FadeTimer.Init(pr_Main,110);
		iPuConfigs = 0;
		iInserts					= 0;
		

		switch(iLevel)
		{
			default:
			{
				iLevel  = 0;
			}
			case 0:
			{
				NewLineTimer.Init(pr_Main,6000);
				iPhonesToCollect = 70;
				break;
			}
			case 1:
			{
				NewLineTimer.Init(pr_Main,5000);
				iPhonesToCollect = 80;
				AddPUConfig(3,5,HelloPiece.WPU_COLREMOVE);
				break;
			}
			case 2:
			{
				NewLineTimer.Init(pr_Main,4300);
				iPhonesToCollect = 90;
				AddPUConfig(3,5,HelloPiece.WPU_COLREMOVE);
				break;
			}
			case 3:
			{
				NewLineTimer.Init(pr_Main,3800);
				iPhonesToCollect = 90;
				AddPUConfig(4,7,HelloPiece.WPU_COLREMOVE);
				AddPUConfig(7,7,HelloPiece.WPU_LINEREMOVE);
				break;
			}
			case 4:
			{
				NewLineTimer.Init(pr_Main,3300);
				iPhonesToCollect = 100;
				AddPUConfig(4,7,HelloPiece.WPU_COLREMOVE);
				AddPUConfig(7,7,HelloPiece.WPU_LINEREMOVE);
				break;
			}
			case 5:
			{
				NewLineTimer.Init(pr_Main,3200);
				iPhonesToCollect = 110;
				AddPUConfig(4 , 10,HelloPiece.WPU_BOMB);
				AddPUConfig(7 , 10,HelloPiece.WPU_LINEREMOVE);
				AddPUConfig(10, 10,HelloPiece.WPU_COLREMOVE);
				break;
			}
			case 6:
			{
				NewLineTimer.Init(pr_Main,3000);
				iPhonesToCollect = 120;
				AddPUConfig(4 , 10,HelloPiece.WPU_BOMB);
				AddPUConfig(7 , 10,HelloPiece.WPU_LINEREMOVE);
				AddPUConfig(10, 10,HelloPiece.WPU_COLREMOVE);
				break;
			}
			case 7:
			{
				NewLineTimer.Init(pr_Main,2900);
				iPhonesToCollect = 130;
				AddPUConfig(4 , 10,HelloPiece.WPU_BOMB);
				AddPUConfig(7 , 10,HelloPiece.WPU_LINEREMOVE);
				AddPUConfig(10, 10,HelloPiece.WPU_COLREMOVE);
				break;
			}
			case 8:
			{
				NewLineTimer.Init(pr_Main,2700);
				iPhonesToCollect = 140;
				AddPUConfig(4 , 10,HelloPiece.WPU_BOMB);
				AddPUConfig(7 , 10,HelloPiece.WPU_LINEREMOVE);
				AddPUConfig(10, 10,HelloPiece.WPU_COLREMOVE);
				break;
			}
			case 9:
			{
				NewLineTimer.Init(pr_Main,2500);
				iPhonesToCollect = 150;
				AddPUConfig(4 , 10,HelloPiece.WPU_BOMB);
				AddPUConfig(7 , 10,HelloPiece.WPU_LINEREMOVE);
				AddPUConfig(10, 10,HelloPiece.WPU_COLREMOVE);
				break;
			}
		}
		InsertPhones();
		InsertPhones();
		InsertPhones();
		InsertPhones();
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CreatePhones()
	// Desc: Cria os fones, fones end e wires
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	private void CreatePhones()
	{
		
		pr_luback		= p_LayerGame.CreateSprite("bkg_levelup.gif",new JGDLVector(284,322));
		pr_luback.position.fx = 7.0f;
		pr_luback.position.fy = 7.0f;
		pr_luback.bVisible = false;
		
		int [] Anim = {0};
		if(pr_Main.iLang == JGDLMain.WLANG_ENG)
		{
			pr_lutitle = p_LayerGame.CreateSprite("spr_lutitle.gif", new JGDLVector(175,45));
			pr_lutitle.position.fx = 68.0f;
		}

		if(pr_Main.iLang == JGDLMain.WLANG_PORT)
		{
			pr_lutitle = p_LayerGame.CreateSprite("spr_lutitle.gif", new JGDLVector(209,45));
			pr_lutitle.position.fx = 51.0f;
		}
		pr_lutitle.position.fy = 7.0f;
		pr_lutitle.bVisible = false;
		pr_lutitle.AddAnimation(10,false,Anim);
		Anim[0] = 1;
		pr_lutitle.AddAnimation(10,false,Anim);
		pr_lutitle.SetCurrentAnimation(0);
		 
		
		
		Anim[0] = 0;
		pr_continue	= p_LayerGame.CreateSprite("btn_ContinueGame.gif",new JGDLVector(116,23));
		pr_continue.position.fx = 150;
		pr_continue.position.fy = 298;
		pr_continue.bVisible = false;
		pr_continue.AddAnimation(10,false,Anim);
		Anim[0] = 1;
		pr_continue.AddAnimation(10,false,Anim);
		
		pr_download = p_LayerGame.CreateSprite("btn_DownloadFreeTrial.gif",new JGDLVector(140,23));
		pr_download.position.fx = 10;
		pr_download.position.fy = 298;
		pr_download.bVisible = false;
		Anim[0] = 0;
		pr_download.AddAnimation(10,false,Anim);
		Anim[0] = 1;
		pr_download.AddAnimation(10,false,Anim);

		pr_playagain = p_LayerGame.CreateSprite("btn_PlayAgain.gif",new JGDLVector(116,23));
		pr_playagain.bVisible = false;
		pr_playagain.position.fx = 184;
		pr_playagain.position.fy = 298;
		Anim[0] = 0;
		pr_playagain.AddAnimation(10,false,Anim);
		Anim[0] = 1;
		pr_playagain.AddAnimation(10,false,Anim);
				

		pr_lubrick	= p_LayerGame.CreateSprite("spr_lubrick.gif",new JGDLVector(31,31));
		pr_lubrick.bVisible = false;
		
		//Congrats
		pr_congrats = p_LayerGame.CreateSprite("spr_congrats.gif",new JGDLVector(284,322));
		pr_congrats.position.fx = 7.0f;
		pr_congrats.position.fy = 7.0f;
		pr_congrats.bVisible 		= false;
		
		//Help
		pr_helpback = p_LayerGame.CreateSprite("bkg_TelaHelp.gif",new JGDLVector(284,322));
		pr_helpback.position.fx = 7.0f;
		pr_helpback.position.fy = 7.0f;
		pr_helpback.bVisible 		= false;
		
		pr_helpOK = p_LayerGame.CreateSprite("btn_ClickToPlay.gif",new JGDLVector(150,23));		
		pr_helpOK.position.fx = 135;
		pr_helpOK.position.fy = 303;
		pr_helpOK.bVisible		= false;
		
		Anim[0] = 0;
		pr_helpOK.AddAnimation(10,false,Anim);
		Anim[0] = 1;
		pr_helpOK.AddAnimation(10,false,Anim);

		//Power Ups
		JGDLSprite p_PowerUps = p_LayerGame.CreateSprite("spr_PowerUPS.gif",new JGDLVector(31,31));

		Anim[0] = 0;
		p_PowerUps.bVisible		= false;
		p_PowerUps.AddAnimation(10,false,Anim);
		
		Anim[0] = 1;
		p_PowerUps.AddAnimation(10,false,Anim);
		
		Anim[0] = 2;
		p_PowerUps.AddAnimation(10,false,Anim);
		
		PowerUps[0]							= new HelloPiece(this);
		PowerUps[0].byID				= 127;
		PowerUps[0].bType				= HelloPiece.WPU_LINEREMOVE;
		PowerUps[0].pr_Sprite 	= p_PowerUps.GetClone(false);
		PowerUps[0].bIsPowerUp	= true;
		PowerUps[0].pr_Sprite.SetCurrentAnimation(0);
		
		PowerUps[1]							= new HelloPiece(this);
		PowerUps[1].byID				= 127;
		PowerUps[1].bType				= HelloPiece.WPU_COLREMOVE;
		PowerUps[1].pr_Sprite 	= p_PowerUps.GetClone(false);
		PowerUps[1].bIsPowerUp	= true;
		PowerUps[1].pr_Sprite.SetCurrentAnimation(1);
		
		PowerUps[2]							= new HelloPiece(this);
		PowerUps[2].byID				= 127;
		PowerUps[2].bType				= HelloPiece.WPU_BOMB;
		PowerUps[2].pr_Sprite 	= p_PowerUps.GetClone(false);
		PowerUps[2].bIsPowerUp	= true;
		PowerUps[2].pr_Sprite.SetCurrentAnimation(2);
		

		
		
		
		for(int i = 0; i < Phones.length; i++)
		{
			//phones
			Phones[i] 											= new HelloPiece(this);
			Phones[i].byID									= (byte)i;
			Phones[i].pr_Level 							= this;
			Phones[i].pr_Sprite	            = p_LayerGame.CreateSprite("spr_phone" + (i+1) + ".gif",new JGDLVector(31,31));
			Phones[i].pr_Sprite.bVisible    = false;
			Phones[i].pr_Sprite.position.fx = 30+(i*30);
			Phones[i].pr_Sprite.position.fy = 50;
			
			//phones end
			Phones[i].pr_Fade = p_LayerGame.CreateSprite("spr_phone" + (i+1) + "_end.gif",new JGDLVector(49,49));
			Phones[i].pr_Fade.bVisible    = false;
			Phones[i].pr_Fade.position.fx = 30+(i*30);
			Phones[i].pr_Fade.position.fy = 100;
			int FadeOut[] = {0,1,2,3,4,5,6,7,8,9,-1};
			int FadeIn[] = {9,8,7,6,5,4,3,2,1,0};
			Phones[i].pr_Fade.AddAnimation(30,false,FadeOut);
			Phones[i].pr_Fade.AddAnimation(30,false,FadeIn);
			Phones[i].pr_Fade.SetCurrentAnimation(0);

			//phones wires
			Wires[i] = new HelloPiece(this);
			
			Wires[i].pr_Sprite = p_LayerGame.CreateSprite("spr_wire" + (i+1) + ".gif",new JGDLVector(31,31));
			Wires[i].pr_Sprite.bVisible = false;
			Wires[i].pr_Sprite.position.fx = 30+(i*30);
			Wires[i].pr_Sprite.position.fy = 150;
			AddWireAnimations(Wires[i]);
			Wires[i].pr_Sprite.SetCurrentAnimation(0);
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: AddWireAnimations(HelloPiece p_Wire)
	// Desc: adiciona as animações do fio
	// Pams: fio
	//---------------------------------------------------------------------------- 
	void AddWireAnimations(HelloPiece p_Wire)
	{
		int Anim[] = new int[1];
		//0
		Anim[0] = 3;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//1
		Anim[0] = 4;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//2
		Anim[0] = 0;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//3
		Anim[0] = 2;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//4
		Anim[0] = 1;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//5
		Anim[0] = 5;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//6
		Anim[0] = 6;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//7
		Anim[0] = 13;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//8
		Anim[0] = 7;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//9
		Anim[0] = 14;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//10
		Anim[0] = 11;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//11
		Anim[0] = 17;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//12
		Anim[0] = 15;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//13
		Anim[0] = 10;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//14
		Anim[0] = 16;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//15
		Anim[0] = 9;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//16
		Anim[0] = 12;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		//17
		Anim[0] = 8;
		p_Wire.pr_Sprite.AddAnimation(15,false,Anim);
		Anim = null;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetMouseMatrix(void)
	// Desc: returns the matrix position of the mouse
	// Pams: none
	//---------------------------------------------------------------------------- 
	boolean GetMouseMatrixPos(JGDLVector MousePos)
	{
		MousePos.fx = pr_Main.InputManager.GetMousePos().fx;
		MousePos.fy = pr_Main.InputManager.GetMousePos().fy;
		MousePos.fx -= UpLeft.fx;
		MousePos.fy -= UpLeft.fy;
		MousePos.fx /= HelloPiece.WPIECEWIDTH;
		MousePos.fy /= HelloPiece.WPIECEHEIGHT;
		MousePos.Floor();
	
		if(MousePos.fx >= 0 && MousePos.fx < MatSizeX && MousePos.fy >=0 && MousePos.fy < MatSizeY)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	//---------------------------------------------------------------------------- 
	// Name: CanSelect(const W2DPoint & MatPos)
	// Desc: returns true of the given position can be selected
	// Pams: Matrix position
	//---------------------------------------------------------------------------- 
	public boolean CanSelect(JGDLVector MatPos)
	{
		if(!ValidatePos(MatPos) || null == Matp(MatPos) || (Matp(MatPos).bEffect != HelloPiece.WPE_NONE))
		{
			return  false;
		}
		if(MatPos.fx > 0 && null == Mat((int)MatPos.fx -1, (int)MatPos.fy))
		{
			return true;
		}
		if(MatPos.fy > 0 && null == Mat((int)MatPos.fx , (int)MatPos.fy -1 ))
		{
			return true;
		}
		if((int)MatPos.fx < MatSizeX -1 && null == Mat((int)MatPos.fx + 1, (int)MatPos.fy))
		{
			return true;
		}
		if((int)MatPos.fy < MatSizeY -1 && null == Mat((int)MatPos.fx, (int)MatPos.fy + 1))
		{
			return true;
		}
		return false;
	}
	//---------------------------------------------------------------------------- 
	// Name: GetNearestConnectionPos(const W2DPoint & Pos)
	// Desc: Returns the nearest connection pos given the position
	// Pams: position
	//---------------------------------------------------------------------------- 
	void GetNearestConnectionPos(JGDLVector pRet,JGDLVector MatPos)
	{
		JGDLVector Pos = pRet;
		Pos.atrib(MatPos);
		if(null == Matp(Pos) || (null != Matp(Pos) && CanSelect(Pos)))
		{
			return;
		}
		else
		{
			int   iLeft		= 200,
						iRight	= 200,
						iUp			= 200,
						iDown		= 200;
			
			JGDLVector pLeft = TempPos9;
			JGDLVector pRight= TempPos10;
			JGDLVector pUp   = TempPos11;
			JGDLVector pDown = TempPos12;

			pLeft.atrib(Pos);
			pRight.atrib(Pos);
			pUp.atrib(Pos);
			pDown.atrib(Pos);
			
			//Check Left
			for(pLeft.fx = Pos.fx - 1; pLeft.fx >=0; pLeft.fx--)
			{
				if(CanSelect(pLeft))
				{
					iLeft = (int)(Pos.fx - pLeft.fx);
					break;
				}
			}
			//Check Right
			for(pRight.fx = Pos.fx + 1; (int)pRight.fx < MatSizeX; pRight.fx++)
			{
				if(CanSelect(pRight))
				{
					iRight = (int)(pRight.fx - Pos.fx);
					break;
				}
			}
			//Check Up
			for(pUp.fy = Pos.fy-1; pUp.fy >=0; pUp.fy --)
			{
				if(CanSelect(pUp))
				{
					iUp = (int)(Pos.fy - pUp.fy);
					break;
				}
			}
	
			//CheckDown
			for(pDown.fy = Pos.fy + 1; pDown.fy < MatSizeY; pDown.fy++)
			{
	      if(CanSelect(pDown))
				{
					iDown = (int)(pDown.fy - Pos.fy);
					break;
				}
			}
	
			if(iDown != 200 && iDown <= iLeft && iDown <= iRight && iDown <= iUp)
			{
				pRet.atrib(pDown);
			}
			if(iUp != 200 && iUp <= iLeft && iUp <= iRight && iUp <= iDown)
			{
				pRet.atrib(pUp);
			}
			if(iLeft != 200 && iLeft <= iRight && iLeft <= iDown && iLeft <= iUp)
			{
				pRet.atrib(pLeft);
			}
			if(iRight != 200 && iRight <= iLeft && iRight <= iUp && iRight <= iDown)
			{
				pRet.atrib(pRight);
			}
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: SetCurrPhone(JGDLVector vPos)
	// Desc: sets the current selected phone
	// Pams: position
	//---------------------------------------------------------------------------- 
	public void SetCurrPhone(JGDLVector Pos)
	{
		if (null != p_SndClkPhone)
		{
			p_SndClkPhone.Play();
		}

		CurrPhone.atrib(Pos);

	}
	//---------------------------------------------------------------------------- 
	// Name: ClearVisited()
	// Desc: Clears the visited flags
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void ClearVisited()
	{
		for(int i = 0; i < ScreenMatrix.length; i++)
		{
			if(ScreenMatrix[i] != null)
			{
				ScreenMatrix[i].bVisited = false;
			}
		}
	}

	//---------------------------------------------------------------------------- 
	// Name: ClearPath()
	// Desc: Clears the path
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void ClearPath()
	{
		if(null != Path)
		{
			int 		iSize = Path.size();
			int			jSize;
			boolean bfound = false;
			for(int i = 0; i < iSize; i++)
			{
				bfound = false;
				jSize = FreeVectors.size();
				for(int j = 0; j < jSize; j++)
				{
					if(FreeVectors.get(j) == Path.get(i))
					{
						bfound = true;
						break;
					}
				}
				if(!bfound)
				{
					FreeVectors.push_back(Path.get(i));
				}
			}
			Path.clear();
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: IsInFadeList(HelloPiece p_Piece)
	// Desc: returns true if the piece is in the fade list
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean IsInFadeList(HelloPiece p_Piece)
	{
		if(null != p_Piece)
		{
			int iSize = (int)FadeList.size();
			for(iSize--; iSize >=0; iSize--)
			{
				if(FadeList.get(iSize) == p_Piece)
				{
					return true;
				}
			}
			return (p_Piece.bEffect == HelloPiece.WPE_FADE);
		}
		return false;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ExecuteGameMode()
	// Desc: Executa o modo de jogo
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void ExecuteGameMode()
	{
		if(null != ScreenMatrix)
		{
			boolean bNewLine = false;
			int i,j;
			int iRemoves;
			JGDLVector MousePos = TempPos7;
			if(bInsertPhones)
			{
				if(FadeList.size() != 0 || bFading)
				{
					//clears the fade list
					int iSize = (int)FadeList.size();
					HelloPiece p_Piece;
					for(iSize--; iSize >= 0 ; iSize--)
					{
						p_Piece = (HelloPiece)FadeList.get(iSize);
						if(p_Piece.bIsPowerUp)
						{
							JGDLVector Pos = TempPos8;
							Pos.atrib(p_Piece.iIndex%MatSizeX,p_Piece.iIndex/MatSizeX);
							p_Piece.ExecutePU(Pos);
							Pos = null;
						}
						p_Piece.FadeOut();
						p_Piece = null;
						FadeList.remove(iSize);
					}
					FadeList.clear();
				}
				else
				{
					if(!InsertPhones())
					{
						return;
					}
	
					bNewLine						= true;
					bInsertPhones				= false;
				}//else
	
			}
	
			if(State == WLS_GAME)
			{
/*				if(!bFreezeLine)
				{*/
				  float fx = p_NextLine.window.fx;
				  
				  p_NextLine.window.fx		= p_NextLine.pr_Image.FrameSize.fx;

					if(NewLineTimer.Ended() || p_NextLine.Clicked(0))
					{
						bInsertPhones		= true;
						NewLineTimer.iTimeAccum = 0;
						UpdatePhonePos();
//						CheckAnimations();
					}
					NewLineTimer.Update();
					p_NextLine.window.fx = fx;
/*				}
				else
				{
					FreezeLineTimer.Update();
					if(FreezeLineTimer.Ended())
					{
						bFreezeLine = false;
					}
				}*/
			}
			if(GetMouseMatrixPos(MousePos))
			{
				if(!CanSelect(CurrPhone))
				{
					CurrPhone.fx = -1.0f;
					CurrPhone.fy = -1.0f;
					ClearPath();
				}
				if(!MousePos.operatoreqeq(CurrMousePos) || bNewLine)
				{
					CurrMousePos.fx = MousePos.fx;
					CurrMousePos.fy = MousePos.fy;
					JGDLVector NewConnect = TempPos13;
					
					GetNearestConnectionPos(NewConnect, CurrMousePos);
	
					if(!NewConnect.operatoreqeq(ConnectPos) || bNewLine)
					{
						ConnectPos.atrib(NewConnect);
						TraceRoute(CurrPhone, ConnectPos);
					}
					NewConnect = null;
				}
				if(pr_Main.InputManager.MouBtnPressed(1))
				{
					CurrPhone.fy = CurrPhone.fx = -1;
					ConnectPos.atrib(CurrPhone);
					ClearPath();
				}
				else
				{
					HelloPiece p_CurrPhone;
					HelloPiece p_MousePhone;
					for(i = 0 ; i < MatSizeX; i++)
					{
						for(j = 0; j < MatSizeY; j++)
						{
							if(null != Mat(i,j))
							{
								if(Mat(i,j).pr_Sprite.Clicked(0) && !IsInFadeList(Mat(i,j)))
								{
									if(CurrPhone.fx == -1.0f || CurrPhone.fy == -1.0f)
									{
										if(CanSelect(CurrMousePos))
										{
											SetCurrPhone(CurrMousePos);
										}
									}
									else
									{
										if(CurrPhone.operatoreqeq(CurrMousePos))
										{
											if(Mat(i,j).bIsPowerUp)
											{
												HelloPiece p_Aux = Mat(i,j);
												p_Aux.ExecutePU(CurrMousePos);
												CurrMousePos.fx = p_Aux.iIndex%MatSizeX;
												CurrMousePos.fy = p_Aux.iIndex/MatSizeX;
												DeleteRecursive(CurrMousePos);
												CurrPhone.fx = CurrPhone.fy = -1;
											}
											else
											{
												CurrPhone.fx = CurrPhone.fy = -1;
											}
										}
										else
										{
											if(!ConnectPos.operatoreqeq(CurrPhone) && null != Matp(CurrPhone) 
											    && null != Matp(ConnectPos) &&
												((Matp(CurrPhone).SameID(Matp(ConnectPos)))	&& bRouteOK))
											{
												iRemoves = (int)FadeList.size();
	
												CurrMousePos.atrib(ConnectPos);
												p_CurrPhone		= Matp(CurrPhone);
												p_MousePhone	= Matp(CurrMousePos);
												ClearVisited();
												if(p_CurrPhone.bIsPowerUp)
												{
													p_CurrPhone.ExecutePU(CurrMousePos);
												}
												if(p_MousePhone.bIsPowerUp)
												{
													p_CurrPhone.ExecutePU(CurrMousePos);
												}
												//ExecutePU can change pieces places
												CurrPhone.fx = p_CurrPhone.iIndex%MatSizeX;
												CurrPhone.fy = p_CurrPhone.iIndex/MatSizeX;
	
												CurrMousePos.fx = p_MousePhone.iIndex%MatSizeX;
												CurrMousePos.fy = p_MousePhone.iIndex/MatSizeX;
	
												DeleteRecursive(CurrPhone);
												DeleteRecursive(CurrMousePos);
												iRemoves = (int)FadeList.size() - iRemoves;
//												pr_Level->Effects.CreateCongrats(iRemoves);
												UpdatePhonePos();
												CurrPhone.fx = CurrPhone.fy = -1;
												//full break
												i = MatSizeX;
												j = MatSizeY;
	
											}
											else
											{
												if(CanSelect(CurrMousePos))
												{
													SetCurrPhone(CurrMousePos);
												}
											}
										}
									}
									ClearPath();
								}//if clicked
								else
								{
									if(Mat(i,j).pr_Sprite.IsMouseOver()
										&& pr_Main.InputManager.MouBtnReleased(0))
									{
										if(!CurrPhone.operatoreqeq(ConnectPos) && ValidatePos(CurrPhone) && 
												ValidatePos(ConnectPos) &&
												null != Matp(CurrPhone) && null != Matp(ConnectPos) &&
												((Matp(CurrPhone).SameID(Matp(ConnectPos)))	&& bRouteOK))
										{
											CurrMousePos.atrib(ConnectPos);
											p_CurrPhone		= Matp(CurrPhone);
											p_MousePhone	= Matp(CurrMousePos);
											ClearVisited();
											if(p_CurrPhone.bIsPowerUp)
											{
												p_CurrPhone.ExecutePU(CurrMousePos);
											}
											if(p_MousePhone.bIsPowerUp)
											{
												p_CurrPhone.ExecutePU(CurrMousePos);
											}
											//ExecutePU can change pieces places
											CurrPhone.fx = p_CurrPhone.iIndex%MatSizeX;
											CurrPhone.fy = p_CurrPhone.iIndex/MatSizeX;
	
											CurrMousePos.fx = p_MousePhone.iIndex%MatSizeX;
											CurrMousePos.fy = p_MousePhone.iIndex/MatSizeX;
	
											DeleteRecursive(CurrPhone);
											DeleteRecursive(CurrMousePos);
											UpdatePhonePos();
											CurrPhone.fx = CurrPhone.fy = -1;
											ClearPath();
	
											//full break
											i = MatSizeX;
											j = MatSizeY;
										}
									}
								}
							}
						}
					}
				}
			}
			
			//Updates matrix
			if(UpdateMatrixPieces())
			{
				UpdatePhonePos();
			}
			if(iPhonesCollected >= iPhonesToCollect)
			{
				ChangeState(WLS_LEVELUP);
			}
			else
			{
				if(bIsBoardFull)
				{
					ChangeState(WLS_PREGAMEOVER);
				}
			}
			MousePos = null;
		}//null != ScreenMatrix

	}

	//---------------------------------------------------------------------------- 
	// Name: DeleteRecursive(void)
	// Desc: deletes phones recursively
	// Pams: JGDLVector Pos
	//---------------------------------------------------------------------------- 
	void DeleteRecursive(JGDLVector Pos)
	{
		if(ValidatePos(Pos)&& null != Matp(Pos) && !Matp(Pos).bVisited )
		{
			JGDLVector NewPos;
			int iFreeVectors = FreeVectors.size();
			if(iFreeVectors > 0)
			{
				iFreeVectors--;
				NewPos = (JGDLVector)FreeVectors.get(iFreeVectors);
				FreeVectors.remove(iFreeVectors);
			}
			else
			{
				NewPos = new JGDLVector();
			}
			Matp(Pos).bVisited = true;
	
			if(!Matp(Pos).bIsPowerUp)
			{
				NewPos.atrib(Pos);
				NewPos.fx ++;
				if(ValidatePos(NewPos) && null != Matp(NewPos) && !Matp(NewPos).bIsPowerUp &&
					Matp(NewPos).SameID(Matp(Pos)))
				{
					DeleteRecursive(NewPos);
				}
	
				NewPos.atrib(Pos);
				NewPos.fx --;
				if(ValidatePos(NewPos) &&  null != Matp(NewPos) &&  !Matp(NewPos).bIsPowerUp &&
					Matp(NewPos).SameID(Matp(Pos))  && 
					Matp(NewPos).bUp == Matp(NewPos).bUp)
				{
					DeleteRecursive(NewPos);
				}
	
				NewPos.atrib(Pos);
				NewPos.fy ++;
				if(ValidatePos(NewPos) &&  null != Matp(NewPos) &&  !Matp(NewPos).bIsPowerUp &&
					Matp(NewPos).SameID(Matp(Pos))  && 
					Matp(NewPos).bUp == Matp(NewPos).bUp)
				{
					DeleteRecursive(NewPos);
				}
	
				NewPos.atrib(Pos);
				NewPos.fy --;
				if(ValidatePos(NewPos) &&  null != Matp(NewPos) &&  !Matp(NewPos).bIsPowerUp &&
					Matp(NewPos).SameID(Matp(Pos))  && 
					Matp(NewPos).bUp == Matp(NewPos).bUp)
				{
					DeleteRecursive(NewPos);
				}
			}
			RemovePiece(Pos);
			FreeVectors.push_back(NewPos);
			NewPos = null;
		}
	}


	//---------------------------------------------------------------------------- 
	// Name: RemovePiece(int iIndex)
	// Desc: Places a piece in the remove/fade list
	// Pams: piece index
	//---------------------------------------------------------------------------- 
	void RemovePiece(JGDLVector v )
	{
		RemovePiece((int)v.fx + (((int)v.fy)*MatSizeX));
	}
	
	//---------------------------------------------------------------------------- 
	// Name: RemovePiece(int iIndex)
	// Desc: Places a piece in the remove/fade list
	// Pams: piece index
	//---------------------------------------------------------------------------- 
	void RemovePiece(int iIndex)
	{
		if(null != ScreenMatrix[iIndex] && ScreenMatrix[iIndex].bEffect == HelloPiece.WPE_NONE)
		{
			int iSize = FadeList.size();
			for(iSize--; iSize >=0; iSize--)
			{
				if(FadeList.get(iSize) == ScreenMatrix[iIndex])
				{
					return;
				}
			}
			ScreenMatrix[iIndex].iIndex = iIndex;
			FadeList.push_back(ScreenMatrix[iIndex]);
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: AjustPathAnimations(void)
	// Desc: Ajusts the path animations
	// Pams: none
	//---------------------------------------------------------------------------- 
	public static final byte  WPREVLEFT 	= 1,
														WPREVRIGHT 	= 2,
														WPREVUP 		= 3,
														WPREVDOWN 	= 4,
														WNEXTLEFT 	= 8,
														WNEXTRIGHT 	= 16,
														WNEXTUP			= 32,
														WNEXTDOWN 	= 64;
														
	private void AjustPathAnimations()
	{
		int i;
		int iSize = Path.size();
		byte bPrev = 0, bNext = 0;
	
		// play wire animatiom
	
		/*if (g_PhoneGame.p_SndDoWire)
		{
			g_PhoneGame.p_SndDoWire->Play();
		}*/
	
		for(i = 1; i < iSize-1; i ++)
		{
			if(((JGDLVector)Path.get(i-1)).fx < ((JGDLVector)Path.get(i)).fx)
			{
				bPrev = WPREVLEFT;
			}
			else
			{
				if(((JGDLVector)Path.get(i-1)).fx > ((JGDLVector)Path.get(i)).fx)
				{
					bPrev = WPREVRIGHT;
				}
				else
				{
					if(((JGDLVector)Path.get(i-1)).fy < ((JGDLVector)Path.get(i)).fy)
					{
						bPrev = WPREVUP;
					}
					else
					{
						bPrev = WPREVDOWN;
					}
				}
			}
			if(((JGDLVector)Path.get(i+1)).fx < ((JGDLVector)Path.get(i)).fx)
			{
				bNext = WNEXTLEFT;
			}
			else
			{
				if(((JGDLVector)Path.get(i+1)).fx > ((JGDLVector)Path.get(i)).fx)
				{
					bNext = WNEXTRIGHT;
				}
				else
				{
					if(((JGDLVector)Path.get(i+1)).fy < ((JGDLVector)Path.get(i)).fy)
					{
						bNext = WNEXTUP;
					}
					else
					{
						bNext = WNEXTDOWN;
					}
				}
			}
			switch(bPrev | bNext)
			{
				case WPREVUP | WNEXTDOWN:
				case WPREVDOWN | WNEXTUP:
				{
					PathAnim[i] = 0;
					break;
				}
				case WPREVLEFT | WNEXTRIGHT:
				case WNEXTLEFT | WPREVRIGHT:
				{
					PathAnim[i] = 1;
					break;
				}
				case WPREVLEFT | WNEXTUP:
				case WPREVUP | WNEXTLEFT:
				{
					PathAnim[i] = 3;
//					Path[i]->iAnimSet = 3;
					break;
				}
				case WPREVLEFT | WNEXTDOWN:
				case WPREVDOWN | WNEXTLEFT:
				{
					PathAnim[i] = 5;
//					Path[i]->iAnimSet = 5;
					break;
				}
				case WPREVRIGHT | WNEXTUP:
				case WPREVUP | WNEXTRIGHT:
				{
					PathAnim[i] = 2;
//					Path[i]->iAnimSet = 2;
					break;
				}
				case WPREVRIGHT | WNEXTDOWN:
				case WPREVDOWN | WNEXTRIGHT:
				{
					PathAnim[i] = 4;
//					Path[i]->iAnimSet = 4;
					break;
				}
			}
		}//for
		i--;
		//ajust last wire
		if(i>= 0 && i< iSize-1)
		{
			switch(bPrev | bNext)
			{
				case WPREVUP | WNEXTDOWN:
				{
					PathAnim[i] = 7;
//					Path[i]->iAnimSet = 7;
					break;
				}
				case WPREVDOWN | WNEXTUP:
				{
					PathAnim[i] = 6;
//					Path[i]->iAnimSet = 6;
					break;
				}
				case WPREVLEFT | WNEXTRIGHT:
				{
					PathAnim[i] = 8;
//					Path[i]->iAnimSet = 8;
					break;
				}
				case WNEXTLEFT | WPREVRIGHT:
				{
					PathAnim[i] = 9;
//					Path[i]->iAnimSet = 9;
					break;
				}
				case WPREVLEFT | WNEXTUP:
				{
					PathAnim[i] = 14;
//					Path[i]->iAnimSet = 14;
					break;
				}
				case WPREVUP | WNEXTLEFT:
				{
					PathAnim[i] = 15;
//					Path[i]->iAnimSet = 15;
					break;
				}
				case WPREVLEFT | WNEXTDOWN:
				{
					PathAnim[i] = 12;
//					Path[i]->iAnimSet = 12;
					break;
				}
				case WPREVDOWN | WNEXTLEFT:
				{
					PathAnim[i] = 13;
					break;
				}
				case WPREVRIGHT | WNEXTUP:
				{
					PathAnim[i] = 17;
//					Path[i]->iAnimSet = 17;
					break;
				}
				case WPREVUP | WNEXTRIGHT:
				{
					PathAnim[i] = 16;
//					Path[i]->iAnimSet = 16;
					break;
				}
				case WPREVRIGHT | WNEXTDOWN:
				{
					PathAnim[i] = 11;
//					Path[i]->iAnimSet = 11;
					break;
				}
				case WPREVDOWN | WNEXTRIGHT:
				{
					PathAnim[i] = 10;
//					Path[i]->iAnimSet = 10;
					break;
				}
			}
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: TraceRoute(W2DPoint TragetPos)
	// Desc: traces the route to the target pos
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void TraceRoute(JGDLVector FromPos, JGDLVector ToPos)
	{
		bRouteOK = false;
		if(ValidatePos(FromPos) && ValidatePos(ToPos) && null != Matp(FromPos))
		{
			bPathID = Matp(FromPos).byID;

			ClearPath();
			Search.Search(FromPos, ToPos);
			if(null != Path)
			{
				bRouteOK = true;
				JGDLVector Pos;
				int iSize = FreeVectors.size();
				if(iSize > 0)
				{
					iSize--;
					Pos = (JGDLVector)FreeVectors.get(iSize);
					FreeVectors.remove(iSize);
				}
				else
				{
					Pos = new JGDLVector();
				}
				Pos.atrib(FromPos);
				Path.push_front(Pos);
				AjustPathAnimations();
			}
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: DrawPath()
	// Desc: Drawsc the wire path
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void DrawPath()
	{
		int index = (bPathID >= 0 && bPathID < (byte)Phones.length)? (int)bPathID : 0;
		if(null != Path)
		{
			int iSize  = Path.size();
			for(int i = 1; i < iSize -1; i ++)
			{
				Wires[index].pr_Sprite.position.atrib((JGDLVector)Path.get(i));
				Wires[index].pr_Sprite.position.fx *= HelloPiece.WPIECEWIDTH;
				Wires[index].pr_Sprite.position.fx += UpLeft.fx;
				Wires[index].pr_Sprite.position.fy *= HelloPiece.WPIECEHEIGHT;
				Wires[index].pr_Sprite.position.fy += UpLeft.fy;
				Wires[index].pr_Sprite.SetCurrentAnimation(PathAnim[i]);				
				Wires[index].pr_Sprite.Draw();
			}
		}
	}

	//---------------------------------------------------------------------------- 
	// Name: Execute()
	// Desc: Executa a cena. Essa rotina deve ser reescrita na cena, para que se possa
	//		   fazer a execução da cena.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Execute()
	{
		p_Pause.position.fy = p_PannelDownload.position.fy;
		p_LuImages.position.fx = pr_luback.position.fx + 68;
		p_LuImages.position.fy = pr_luback.position.fy + 167;

		switch(State)
		{
			case WLS_ENDGAME:
			{
				if(pr_playagain.IsMouseOver())
				{
					pr_playagain.SetCurrentAnimation(1);
				}
				else
				{
					pr_playagain.SetCurrentAnimation(0);
				}
				if(pr_playagain.Clicked(0))
				{
					iLevel = 0;
					iPoints = 0;
					p_SndMenuOK.Play();
					ChangeState(WLS_GAME);
				}
				
				if(pr_download.IsMouseOver())
				{
					pr_download.SetCurrentAnimation(1);
				}
				else
				{
					pr_download.SetCurrentAnimation(0);
				}
				
				if(pr_download.Clicked(0))
				{
					OpenURL();
				}
				break;
			}
			case WLS_PREGAMEOVER:
			{
				if(!PreGameOverTimer.Ended())
				{
					PreGameOverTimer.Update();
				}
				else
				{
					boolean bEnded = true;
					for(int i =0; i < ScreenMatrix.length; i++)
					{
						if(null != ScreenMatrix[i])
						{
							if(ScreenMatrix[i].bShake)
							{
								bEnded = false;
								ScreenMatrix[i].bShake = false;
								ScreenMatrix[i].Explode();
							}
							else
							{
								if(!ScreenMatrix[i].EndedEffect())
								{
									bEnded = false;
								}
							}
						}
					}
					if(bEnded)
					{
						ChangeState(WLS_GAMEOVER);
					}
				}
				UpdateMatrixPieces();
				break;
			}
			case WLS_STARTGAME:
			{
				if(pr_helpOK.IsMouseOver())
				{
					pr_helpOK.SetCurrentAnimation(1);
				}
				else
				{
					pr_helpOK.SetCurrentAnimation(0);
				}
				if(pr_helpOK.Clicked(0))
				{
					p_SndMenuOK.Play();
					ChangeState(WLS_GAME);
					
				}
				break;
			}
			case WLS_INITLEVEL:
			{
				if(pr_continue.IsMouseOver())
				{
					pr_continue.SetCurrentAnimation(1);
				}
				else
				{
					pr_continue.SetCurrentAnimation(0);
				}
				if(pr_continue.Clicked(0))
				{
					p_SndMenuOK.Play();
					ChangeState(WLS_GAME);
					
				}
				
				if(pr_download.IsMouseOver())
				{
					pr_download.SetCurrentAnimation(1);
				}
				else
				{
					pr_download.SetCurrentAnimation(0);
				}
				
				if(pr_download.Clicked(0))
				{
					OpenURL();
				}
				break;
			} 
			case WLS_GAMEOVER:
			{
				if(pr_playagain.IsMouseOver())
				{
					pr_playagain.SetCurrentAnimation(1);
				}
				else
				{
					pr_playagain.SetCurrentAnimation(0);
				}
				if(pr_playagain.Clicked(0))
				{
					iLevel = 0;
					iPoints = 0;
					p_SndMenuOK.Play();
					ChangeState(WLS_GAME);
				}
				
				if(pr_download.IsMouseOver())
				{
					pr_download.SetCurrentAnimation(1);
				}
				else
				{
					pr_download.SetCurrentAnimation(0);
				}
				
				if(pr_download.Clicked(0))
				{
					OpenURL();
				}
				break;
			} 
			case WLS_GAME:
			{
				ExecuteGameMode();
 				break;
 			}
 			case WLS_LEVELUP:
 			{
				HandleLevelUp();
 				break;
 			}
		}

		if(p_PannelDownload.bVisible)
		{
			if(p_PannelDownload.IsMouseOver())
			{
				p_PannelDownload.SetCurrentAnimation(1);
			}
			else
			{
				p_PannelDownload.SetCurrentAnimation(0);
			}
			if(p_PannelDownload.Clicked(0))
			{
				OpenURL();
			}
		}
		if(p_Pause.bVisible)
		{
			if(p_Pause.IsMouseOver())
			{
				p_Pause.SetCurrentAnimation(1);
			}
			else
			{
				p_Pause.SetCurrentAnimation(0);
			}
		}


		p_LevelBar.window.fy		= (float)-(((int)p_LevelBar.pr_Image.FrameSize.fy*iPhonesCollected)/iPhonesToCollect);
				  
	  p_NextLine.window.fx		= p_NextLine.pr_Image.FrameSize.fx;
	  if(!p_NextLine.IsMouseOver())
	  {
			p_NextLine.window.fx		= (p_NextLine.pr_Image.FrameSize.fx*(float)NewLineTimer.iTimeAccum)/(float)NewLineTimer.iTimeLimit;
		}
		p_NextLine.position.fx	= 7;
		p_NextLine.position.fy	= 322;
		EffectManager.Update();

		if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_S))
		{
			pr_Main.SoundManager.bEnableSounds = !pr_Main.SoundManager.bEnableSounds;
		}
	}

  
	//---------------------------------------------------------------------------- 
	// Name: Draw()
	// Desc: asf
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Draw()
	{
		//chama primeiro o Draw do pai
		super.Draw();

		switch(State)
		{
			case WLS_ENDGAME:
			{
				p_LayerFront.Draw();
				pr_congrats.Draw();
				pr_playagain.Draw();
				pr_download.Draw();
				break;
			}
			case WLS_GAMEOVER:
			{
				p_LayerFront.Draw();
				pr_luback.Draw();
				pr_playagain.Draw();
				pr_download.Draw();
				pr_lutitle.Draw();
				break;
			}
			case WLS_INITLEVEL:
			{
				p_LayerFront.Draw();
				pr_luback.Draw();
				pr_continue.Draw();
				pr_download.Draw();
				pr_lutitle.Draw();
				p_LuImages.Draw();
				break;
			}
			case WLS_STARTGAME:
			{
				p_LayerFront.Draw();
				pr_helpback.Draw();
				pr_helpOK.Draw();
				break;
			}
			default:
			{
				//renders the phones
				for(int i = 0; i < ScreenMatrix.length; i++)
				{
					if(null != ScreenMatrix[i])
					{
						ScreenMatrix[i].Render();
					}
				}
				if(CurrPhone.fx != -1.0f && CurrPhone.fy != -1.0f && p_Selection != null)
				{
					p_Selection.Draw();
				}
				DrawPath();
				p_LayerFront.Draw();
				break;
			}
		}

		switch(pr_Main.iLang)
		{
			case JGDLMain.WLANG_ENG:
			{
				JGDLFont.DrawText(pr_Main.VideoManager,306,111,"Level: " + (iLevel+1),Color.darkGray,Arial);
				JGDLFont.DrawText(pr_Main.VideoManager,305,110,"Level: " + (iLevel+1),Color.yellow,Arial);
				JGDLFont.DrawText(pr_Main.VideoManager,306,128,"Points: " + (iPoints),Color.darkGray,Arial);
				JGDLFont.DrawText(pr_Main.VideoManager,305,127,"Points: " + (iPoints),Color.yellow,Arial);
			//fones a coletar
				JGDLFont.DrawText(pr_Main.VideoManager,381,111,"" + iPhonesCollected + " of " + iPhonesToCollect,Color.darkGray,Arial);
				JGDLFont.DrawText(pr_Main.VideoManager,380,110,"" + iPhonesCollected + " of " + iPhonesToCollect,Color.yellow,Arial);
					break;
			}
			case JGDLMain.WLANG_PORT:
			{
				JGDLFont.DrawText(pr_Main.VideoManager,306,111,"Fase: " + (iLevel+1),Color.darkGray,Arial);
				JGDLFont.DrawText(pr_Main.VideoManager,305,110,"Fase: " + (iLevel+1),Color.yellow,Arial);
				JGDLFont.DrawText(pr_Main.VideoManager,306,128,"Pontos: " + (iPoints),Color.darkGray,Arial);
				JGDLFont.DrawText(pr_Main.VideoManager,305,127,"Pontos: " + (iPoints),Color.yellow,Arial);
			//fones a coletar
				JGDLFont.DrawText(pr_Main.VideoManager,381,111,"" + iPhonesCollected + " de " + iPhonesToCollect,Color.darkGray,Arial);
				JGDLFont.DrawText(pr_Main.VideoManager,380,110,"" + iPhonesCollected + " de " + iPhonesToCollect,Color.yellow,Arial);
				break;
			}
		}

		EffectManager.Draw();
	
		if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_P) || p_Pause.Clicked(0))
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
	// Name: ClearScreenMatrix()
	// Desc: clears the screen matrix phones
	// Pams: none
	//---------------------------------------------------------------------------- 
	void ClearScreenMatrix()
	{
		if(null != ScreenMatrix)
		{
			for(int i = 0; i < ScreenMatrix.length; i++)
			{
				if(null != ScreenMatrix[i])
				{
					ScreenMatrix[i].Release();
					AddFreePiece(ScreenMatrix[i]);
					ScreenMatrix[i] = null;
				}
			}
		}
	}

}
