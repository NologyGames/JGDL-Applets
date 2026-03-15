
import JGDL.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;

class PopBoard extends JGDLObject
{
	public class DeleteItem
	{
		int iDeleteTime  = 0;
		PopPiece p_Piece = null;
		 
	}

	public static final int MATSIZE = 77;
	public static final int MATX		= 7;
	public static final int MATY		= 11;
	
	//used by check columns function
	private boolean [] bEmptyColumns = new boolean[7];
	public boolean bFear = false;

	private JGDLTimeAccumulator	ShakeTimer = new JGDLTimeAccumulator();
	
	PopPiece [] Matrix 				= new PopPiece[77];
	PopPiece [] NewPieces 		= new PopPiece[7];
	JGDLSprite[] Balls				= new JGDLSprite[7];
	PopPiece [] RemoveList		= new PopPiece[144];
	DeleteItem [] DeleteList	= new DeleteItem[144];
	
	JGDLTimeAccumulator RemoveTimer = new JGDLTimeAccumulator();
	JGDLTimeAccumulator DeleteTimer = new JGDLTimeAccumulator();
	
	int iDeleteSize 				= 0;
	int iRemoveSize 				= 0;
	
	PopLevel 	p_Level = null;
	public 		JGDLVector Pos = new JGDLVector();
	public 		JGDLSprite p_Pan, p_PanTop, p_Shake;
	int 	 		iPieces = 0;
	float 		fDownSpeed = 175.0f;
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
	// Name: PopBoard()
	// Desc: construtora
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	PopBoard()
	{
		for(int i =0; i < 144; i++)
		{
			DeleteList[i] = new DeleteItem();
		}
	} 
	
	//---------------------------------------------------------------------------- 
	// Name: Execute()
	// Desc: Executa o tabuleiro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Execute()
	{
		UpdateBoardPos();
		int index;
		boolean bShake;
		for(int i = 0; i < MATX; i++)
		{
			bShake = (Matrix[i] != null && !Matrix[i].bFalling);

			for(int j = 0; j < MATY; j++)
			{
				index = i + (j*MATX);
				if(Matrix[index] != null)
				{
					Matrix[index].bShake = bShake;
					
					if(!Matrix[index].bFalling && Matrix[index].pr_Sprite.Clicked(0))				
					{
						if(Matrix[index].PowerUpType == PopPiece.WPU_NONE)
						{
							p_Level.TempVector.atrib(i,j);
							ClearVisited();
							DeleteRecursive(p_Level.TempVector);
						}
						else
						{
							Matrix[index].ExecutePU();
							Matrix[index].Release();
							p_Level.FreePieces.push_back(Matrix[index]);
							Matrix[index] = null;
						}
					}
				}
			}
		}
		
		HandleFalling();
		UpdatePieces();
		UpdateRemoveList();
		CheckColumns();
	}
	
	//---------------------------------------------------------------------------- 
	// Name: HandleLevelUp()
	// Desc: Trata o estado de level up
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void HandleLevelUp()
	{
		if(Pos.fy < PopLevel.WBOARDY)
		{
			Pos.fy += p_Level.pr_Main.TimeHandler.fFrameTime * ((p_Level.iState == PopLevel.WLS_GAMEOVER)? 100.0f : 50.0f);
			Pos.fy = (Pos.fy < PopLevel.WBOARDY)? Pos.fy : PopLevel.WBOARDY;
		}
		else
		{
			if(Pos.fy > PopLevel.WBOARDY)
			{
				Pos.fy -= p_Level.pr_Main.TimeHandler.fFrameTime * ((p_Level.iState == PopLevel.WLS_GAMEOVER)? 100.0f : 50.0f);
				Pos.fy = (Pos.fy > PopLevel.WBOARDY)? Pos.fy : PopLevel.WBOARDY;
			}
		}
		UpdateBoardPos();
		HandleFalling();
		UpdatePieces();
		UpdateRemoveList();
		CheckColumns();
		
	}
	
	//---------------------------------------------------------------------------- 
	// Name: UpdateBoardPos()
	// Desc: Atualiza a posição dos sprites do tabuleiro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void UpdateBoardPos()
	{
		if(p_Pan != null)
		{
			p_Pan.position.atrib(Pos);
			p_Pan.position.fx -= 5.0f;
			p_Pan.position.fy -= 20.0f;
		}
		if(p_PanTop != null)
		{
			p_PanTop.position.atrib(Pos);
			p_PanTop.position.fx -= 5.0f;
			p_PanTop.position.fy -= 20.0f;
		}
		if(p_Shake != null)
		{
			p_Shake.position.atrib(Pos);
			p_Shake.position.fx -= 2.0f;
			p_Shake.position.fy -= 50.0f;

			if(p_Level.iState == PopLevel.WLS_GAME)
			{
				ShakeTimer.Update();
				if(iRemoveSize != 0)
				{
					ShakeTimer.iTimeLimit = 1000;
					ShakeTimer.Restart();
				}
				
				if(ShakeTimer.Ended())
				{
					p_Shake.bFreezed = true;
				}
				else
				{
					p_Shake.bFreezed = false;
				}
			}
			else
			{
				p_Shake.bFreezed = true;
			}
		}
		UpdateBalls();
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CreateLine()
	// Desc: Cria uma lina de peças
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean CreateLine()
	{
		boolean bNewLine = false;
		do
		{
			int index;
			for(int i = 0; i < MATX; i++)
			{
				//moves all piece one line down
				for(int j = MATY -1 ; j > 0; j--)
				{
					index = (j*MATX) + i;
					if(null == Matrix[index] && null != Matrix[index - MATX])
					{
						Matrix[index] 				= Matrix[index - MATX];
						Matrix[index - MATX] 	= null;
					}
				}
				//j == 0
				if(null == Matrix[i])
				{
					if(null != NewPieces[i])
					{
						NewPieces[i].pr_Sprite.bVisible = true;
						Matrix[i] = NewPieces[i];
						Matrix[i].Update();
						bNewLine = true;
					}
	
					NewPieces[i] = p_Level.GetRandomPiece();
	
//					NewPieces[i]->AnimationTimer.Init(pr_Mode->pr_Level->pr_Game, 500 + (pr_Mode->pr_Level->pr_Game->Randomizer.randInt()%1000));
	
					NewPieces[i].Pos.fx = (float)(i * PopPiece.SIZEX);
					NewPieces[i].Pos.fy = (float)-PopPiece.SIZEY;
					NewPieces[i].p_Board = this;
					NewPieces[i].bFalling = true;
					if(NewPieces[i].pr_Sprite != null)
					{
						NewPieces[i].pr_Sprite.bVisible = false;
					}
				}
				else
				{
					return false;
				}
			}
		}while(!bNewLine);
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Init()
	// Desc: Inicializa o tabuleiro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Init()
	{
		ShakeTimer.Init(p_Level.pr_Main,20);
		RemoveTimer.Init(p_Level.pr_Main,80);
		ClearRemoveList();
		UpdateBoardPos();
		bFear = false;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: HandleFalling()
	// Desc: Trata a queda das peças
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void HandleFalling()
	{
		iPieces		= 0;
//		iStopped	= 0;
		float fJLimmit;
		float fxPos; 
		//Speed here is defined as 140 pixels per second
		float fMove = 140.0f * p_Level.pr_Main.TimeHandler.fFrameTime;
		int index = 0;
		for(int i = 0; i < MATX; i++)	
		{
			for(int j = MATY-1; j >= 0; j--)
			{
				index = (j*MATX) + i;
				if(Matrix[index] != null)
				{
					Matrix[index].iIndex = index;
					iPieces ++;
					fxPos = (float)(i*(PopPiece.SIZEX));
					if(Matrix[index].Pos.fx < fxPos)
					{
						Matrix[index].Pos.fx += fMove;
	
						if(Matrix[index].Pos.fx > fxPos)
						{
							Matrix[index].Pos.fx = fxPos;
						}
					}
	
					if(Matrix[index].Pos.fx > fxPos)
					{
						Matrix[index].Pos.fx -= fMove;
	
						if(Matrix[index].Pos.fx < fxPos)
						{
							Matrix[index].Pos.fx = fxPos;
						}
					}
	
					fJLimmit = (float)(j*(PopPiece.SIZEY));
					if(Matrix[index].Pos.fy < fJLimmit)
					{
						Matrix[index].Pos.fy += fDownSpeed * p_Level.pr_Main.TimeHandler.fFrameTime;
						Matrix[index].bFalling = true;
	
						if(Matrix[index].Pos.fy > fJLimmit)
						{
							//Matrix[index + MATX] is equal MAT(i,j+1)
							if(j < MATY-1 && null == Matrix[index + MATX])
							{
								Matrix[index + MATX]				= Matrix[index];
								Matrix[index + MATX].iIndex	= index + MATX;
								Matrix[index]								= null;
							}
							else
							{
								Matrix[index].Pos.fy = fJLimmit;
								Matrix[index].bFalling = false;
//								iStopped++;
							}
						}
					}
					else
					{
						//Matrix[index + MATX] is equal MAT(i,j+1)
						if(j < MATY-1 && null == Matrix[index + MATX])
						{
							Matrix[index + MATX]				= Matrix[index];
							Matrix[index + MATX].iIndex	= index + MATX;
							Matrix[index]								= null;
						}
						else
						{
							Matrix[index].Pos.fy = fJLimmit;
							Matrix[index].bFalling = false;
//								iStopped++;
						}
					}
				}//if mat(i,j)
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: UpdatePiece()
	// Desc: Atualiza as peças do tabuleiro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void UpdatePieces()
	{
		iPieces = 0;
		for(int i = 0; i < MATSIZE; i++)
		{
			if(Matrix[i] != null)
			{
				Matrix[i].bShake = (p_Level.iState == PopLevel.WLS_GAME)? Matrix[i].bShake : false;
				Matrix[i].bFear = (p_Level.iState == PopLevel.WLS_GAME)? bFear : false;
				Matrix[i].Update();
				if(Matrix[i].Exploded())
				{
					p_Level.FreePieces.push_back(Matrix[i]);
					Matrix[i].Release();
					Matrix[i] = null;
				}
				else
				{
					iPieces ++;
				}
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ExplodeAll()
	// Desc: Explode todos os minhos do tabuleiro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void ExplodeAll()
	{
		ClearRemoveList();
		for(int i = 0 ; i < MATSIZE; i++)
		{
			if(Matrix[i] != null)
			{
				Matrix[i].Explode();
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: UpdateRemoveList()
	// Desc: Atualiza a list de peças a remover
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void UpdateRemoveList()
	{
		//atualiza a delete list
		int index;
		for(int i = iDeleteSize -1; i >= 0; i--)
		{
			DeleteList[i].iDeleteTime -= p_Level.pr_Main.TimeHandler.iFrameTime;
			if(DeleteList[i].iDeleteTime <= 0)			
			{
				index = DeleteList[i].p_Piece.iIndex;
				if(Matrix[index] != null)
				{
					p_Level.FreePieces.push_back(Matrix[index]);
					Matrix[index].Release();
					Matrix[index] = null;
					p_Level.iPoints += (p_Level.iState == PopLevel.WLS_GAME)? 31 : 1;
					p_Level.iFoundCorns++;
				
				}
				
				DeleteList[i].p_Piece = null;
				DeleteList[i].iDeleteTime = 0;
				
				DeleteItem p_Aux = DeleteList[i];
				DeleteList[i] = DeleteList[iDeleteSize-1];
				DeleteList[iDeleteSize-1] = p_Aux;
				iDeleteSize --;
			}
		}
		
		//atualiza a remove list
		RemoveTimer.Update();
		if(RemoveTimer.Ended())
		{
			int iDeletes	= (int)(RemoveTimer.iTimeAccum/RemoveTimer.iTimeLimit);
			for(int i = iRemoveSize -1 ; i >= 0 && iDeletes > 0; i-- )
			{
				iDeletes--;
				if(Matrix[RemoveList[i].iIndex] != null)
				{
					int iSnd;
					do
					{
						iSnd = Math.abs(p_Level.pr_Main.Randomizer.nextInt())%6;
					}
					while(iSnd == p_Level.iLastPopSnd);
					
					if(null != p_Level.p_PopSounds[iSnd])
					{
						p_Level.p_PopSounds[iSnd].Play();
					}
					p_Level.iLastPopSnd = iSnd;
					p_Level.iPoints += (p_Level.iState == PopLevel.WLS_GAME)? 31 : 1;
					p_Level.iFoundCorns++;
					
					//pr_Mode->iFoundCorns++;
					RemoveList[i].pr_Sprite.GetCenterPos(p_Level.TempVector);
					p_Level.Effects.CreatePopCorn(p_Level.TempVector);
					p_Level.FreePieces.push_back(Matrix[RemoveList[i].iIndex]);
					Matrix[RemoveList[i].iIndex].Release();
					Matrix[RemoveList[i].iIndex] = null;
					RemoveList[i] = null;
					iRemoveSize--;
				}
			}
	
			RemoveTimer.Restart();
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: IsInRemoveList(PopPiece Piece)
	// Desc: retorna true se a peça está na remove list
	// Pams: peça
	//---------------------------------------------------------------------------- 
	public boolean IsInRemoveList(PopPiece Piece)
	{
		int i;
		for(i = 0; i < iRemoveSize; i++)
		{
			if(Piece == RemoveList[i])
			{
				return true;
			}
		}
		for(i =0; i < iDeleteSize; i++)
		{
			if(Piece == DeleteList[i].p_Piece)
			{
				return true;
			}
		}
		return false;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: AddToRemoveList(PopPiece p_Piece)
	// Desc: AAdiciona uma peça a lista de removes
	// Pams: peça
	//---------------------------------------------------------------------------- 
	public void AddToRemoveList(PopPiece p_Piece)
	{
		if(null != p_Piece)
		{
			int i;
			for(i = iRemoveSize - 1; i >= 0; i--) 
			{
				if(RemoveList[i] == p_Piece)
				{
					return;
				}
			}
			
			for(i = 0; i < iDeleteSize; i++)
			{
				if(DeleteList[i].p_Piece == p_Piece)
				{
					return;
				}
			}
			
//			p_Piece->pr_Sprite->SetCurrentAnimSet(WPopPiece::WPA_SURPRISE);
			RemoveList[iRemoveSize] = p_Piece;
			iRemoveSize++;
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: ClearRemoveList()
	// Desc: Limpa a lista de remoções
	// Pams: peça
	//---------------------------------------------------------------------------- 
	public void ClearRemoveList()
	{
		for(int i = 0; i < 144; i++)		
		{
			RemoveList[i] = null;
			DeleteList[i].p_Piece = null;
			DeleteList[i].iDeleteTime = 0;
		}
		iRemoveSize = 0;
		iDeleteSize = 0;
	}
	//---------------------------------------------------------------------------- 
	// Name: DeleteRecursive(void)
	// Desc: deletes phones recursively
	// Pams: JGDLVector Pos
	//---------------------------------------------------------------------------- 
	void DeleteRecursive(JGDLVector MatrixPos)
	{
		int iSize = p_Level.FreeVectors.size();

		JGDLVector Pos;
		if(iSize > 0)
		{
			Pos = (JGDLVector)p_Level.FreeVectors.get(iSize-1);
			p_Level.FreeVectors.remove(iSize-1);
		}
		else
		{
			Pos = new JGDLVector();
		}
		
		Pos.atrib(MatrixPos);

		int index = (int)(Pos.fx + (MATX*Pos.fy));
		int iNewIndex;
		if(Matrix[index] != null && !Matrix[index].bVisited)
		{
			Matrix[index].bVisited = true;
			Pos.fx++;
			iNewIndex = (int)(Pos.fx + ((float)MATX*Pos.fy));
			if(Pos.fx < MATX && Matrix[iNewIndex] != null && Matrix[iNewIndex].SameID(Matrix[index]))
			{
				DeleteRecursive(Pos);
			}

			Pos.fx-=2;
			iNewIndex = (int)(Pos.fx + (MATX*Pos.fy));
			if(Pos.fx >=0 && Matrix[iNewIndex] != null && Matrix[iNewIndex].SameID(Matrix[index]))
			{
				DeleteRecursive(Pos);
			}

			Pos.fx++;
			Pos.fy++;
			iNewIndex = (int)(Pos.fx + (MATX*Pos.fy));
			if(Pos.fy < MATY && Matrix[iNewIndex] != null && Matrix[iNewIndex].SameID(Matrix[index]))
			{
				DeleteRecursive(Pos);
			}

			Pos.fy-=2;
			iNewIndex = (int)(Pos.fx + (MATX*Pos.fy));
			if(Pos.fy >= 0 && Matrix[iNewIndex] != null && Matrix[iNewIndex].SameID(Matrix[index]))
			{
				DeleteRecursive(Pos);
			}
			AddToRemoveList(Matrix[index]);
		}
		p_Level.FreeVectors.push_back(Pos);
	}
	//---------------------------------------------------------------------------- 
	// Name: ClearVisited()
	// Desc: limpa os flags de visitação das peças
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void ClearVisited()
	{
		for(int i = 0; i < MATSIZE; i++)
		{
			if(null != Matrix[i])
			{
				Matrix[i].bVisited = false;
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CheckColumns()
	// Desc: checks the columns 
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void CheckColumns()
	{
		int iCol,iLine,index;
		for(iCol = 0; iCol < MATX; iCol++)
		{
			bEmptyColumns[iCol] = true;
			for(iLine = 0; iLine < MATY; iLine++)
			{
				index = iCol + (iLine*MATX) ;
				if(Matrix[index]!= null)
				{
					bEmptyColumns[iCol] = false;
	        break;
				}
			}
		}
		for(iCol = 3; iCol > 0; iCol--)
		{
	    if(bEmptyColumns[iCol])
			{
				for(iLine = 0; iLine < MATY; iLine++)
				{
					
					if(null != Matrix[iCol-1 + (iLine*MATX)])
					{
						bEmptyColumns[iCol] = false;
					}
					Matrix[iCol + (iLine*MATX)]		= Matrix[iCol-1 + (iLine*MATX)];
					Matrix[iCol-1 + (iLine*MATX)] = null;
				}
			}
		}
		for(iCol = 3; iCol < 6; iCol++)
		{
	    if(bEmptyColumns[iCol])
			{
				for(iLine = 0; iLine < MATY; iLine++)
				{
					if(null != Matrix[iCol+1 + (iLine*MATX)])
					{
						bEmptyColumns[iCol] = false;
					}
					Matrix[iCol + (iLine*MATX)]		= Matrix[iCol+1 + (iLine*MATX)];
					Matrix[iCol+1 + (iLine*MATX)] = null;
				}
			}
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: RenderBalls()
	// Desc: Renders the board balls
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void UpdateBalls()
	{
		if(null != p_Level)
		{
			int iLights = (p_Level.NewLineTimer.iTimeLimit > 0)? ((p_Level.NewLineTimer.iTimeAccum + 100)*7)/p_Level.NewLineTimer.iTimeLimit : 7;
			for(int i = 0; i < 7; i++)
			{
				if(i <= iLights && null != NewPieces[i])
				{
					Balls[i].SetCurrentAnimation((int)NewPieces[i].byID -1);
				}
				else
				{
					Balls[i].SetCurrentAnimation(5);
				}
				Balls[i].position.fx = Pos.fx + 5.0f + (float)(i*PopPiece.SIZEX);
				Balls[i].position.fy = Pos.fy - 8.0f;
				Balls[i].bVisible = true;
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: InitializeBalls()
	// Desc: inicializa as bolas da panela
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void InitializeBalls()
	{
		Balls[0] = p_Level.p_Balls.GetClone(true);
		Balls[1] = p_Level.p_Balls.GetClone(true);
		Balls[2] = p_Level.p_Balls.GetClone(true);
		Balls[3] = p_Level.p_Balls.GetClone(true);
		Balls[4] = p_Level.p_Balls.GetClone(true);
		Balls[5] = p_Level.p_Balls.GetClone(true);
		Balls[6] = p_Level.p_Balls.GetClone(true);
	}
	


	//---------------------------------------------------------------------------- 
	// Name: Clear();
	// Desc: limpa o tabuleiro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Clear()
	{
		int i;
		
		ClearRemoveList();
		
		for(i = 0 ; i < MATX; i++)
		{
			if(null !=NewPieces[i])
			{
				p_Level.FreePieces.push_back(NewPieces[i]);
				NewPieces[i].Release();
				NewPieces[i] = null;
			}
		}
		for(i = 0; i < MATSIZE; i++)
		{
			if(null != Matrix[i])
			{
				AddToRemoveList(Matrix[i]);
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: DeletePiece(PopPiece p_Piece)
	// Desc: Deleta uma peça do tabuleiro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void DeletePiece(PopPiece p_Piece, int iTime)
	{
		if(iDeleteSize < 143 && !IsInRemoveList(p_Piece))
		{
			DeleteList[iDeleteSize].p_Piece 		= p_Piece;
			DeleteList[iDeleteSize].iDeleteTime = iTime;
			iDeleteSize++;
		}
	}
}
