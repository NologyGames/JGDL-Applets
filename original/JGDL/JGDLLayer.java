/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLLayer.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

public class JGDLLayer extends JGDLObject
{
	public final static JGDLVector TempSize = new JGDLVector();
	public final static JGDLVector TempPos = new JGDLVector();
	//!Ponteiro para a cena.
	JGDLScene	pr_Scene = null;

	//!Número de bricks em x e y na layer.
	private JGDLVector NumBricks = new JGDLVector();

	//!Tamanho dos bricks na layer.
	JGDLVector BrickSize = new JGDLVector();

	//!Layer offset - deslocamento da layer em relação ao vídeo.
	private JGDLVector Offset = new JGDLVector();

	//!Velocidade da layer no Scroll.
	public JGDLVector Speed = new JGDLVector();

  //!Flag de visibilidade
	public boolean bVisible = true;
	
	//!Vetor de Bricks (tijolos) que preenchem a layer.
	private JGDLBrick[]	p_Bricks = null;

	//!Lista de sprites da layer.
	JGDLList Sprites = new JGDLList();

	//---------------------------------------------------------------------------- 
	// Name: JGDLLayer()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLLayer()
	{
		Speed.fx = Speed.fy = 1.0f;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza o layer
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
//		System.out.println("Releasing Layer (" + Sprites.size() + " sprites)...");

		//limpa os sprites da layer
		int i;
		for(i = (int)Sprites.size()-1; i >= 0; i--)
		{
			JGDLSprite pr_Spr = (JGDLSprite)Sprites.get(i);
			pr_Spr.Release();
			pr_Spr = null;
		}
		Sprites.clear();
	
		//limpa os bricks da layer
		ClearBricks();

		NumBricks = null;
		BrickSize = null;
		Offset = null;
		Speed = null;
		Sprites = null;
		
		return true;
	}
	
	
	//---------------------------------------------------------------------------- 
	// Name: Update()
	// Desc: Atualiza a layer
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Update()
	{
		JGDLSprite p_Sprite;
		for(int i = (int)Sprites.size()-1; i >=0; i-- )
		{
			p_Sprite = (JGDLSprite)Sprites.get(i);
			if(!p_Sprite.bFreezed)
			{
				p_Sprite.Update();
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Draw()
	// Desc: Pinta a layer
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Draw()
	{
		//pr_Scene.pr_Main.VideoManager.BackBuffer.graph.drawString("SCR: X: " + Offset.fx + " Y: " + Offset.fy,0,30);
		
		if(p_Bricks != null)
		{
			JGDLVector vLayerSize = TempSize;
			vLayerSize.fx = NumBricks.fx*BrickSize.fx;
			vLayerSize.fy = NumBricks.fy*BrickSize.fy;
			while(Offset.fx < 0.0f)
			{
				Offset.fx += vLayerSize.fx;
			}
	
			while(Offset.fy < 0.0f)
			{
				Offset.fy += vLayerSize.fy;
			}
			
			vLayerSize = null;
	
			int i = 0,j = 0;
	
			JGDLVector Pos = TempPos;
			Pos.fx = Offset.fx;
			Pos.fy = Offset.fy;
	
			Pos.Floor();
			while(Pos.fx > 0)
			{
				Pos.fx -= BrickSize.fx;
				i--;
				i = (i < 0)? ((int)NumBricks.fx) - 1 : i;
			}
	
			while(Pos.fy > 0)
			{
				Pos.fy -= BrickSize.fy;
				j--;
				j = (j < 0)? ((int)NumBricks.fy) - 1 : j;
			}
	
			float fXPos = Pos.fx;
			int iStarti = i;
			int iSize = (int)NumBricks.fx;
			int jSize = (int)NumBricks.fy;
			for(; Pos.fy < (int)pr_Scene.pr_Main.VideoManager.VideoSize.fy; j = (j+1)%jSize)
			{
				for(; Pos.fx < (int)pr_Scene.pr_Main.VideoManager.VideoSize.fx; i = (i+1)%iSize)
				{
					if( p_Bricks[i + (j*iSize)] != null)
					{						
						pr_Scene.pr_Main.VideoManager.BackBuffer.DrawImage(Pos,(JGDLImage)pr_Scene.TileImages.get(p_Bricks[i + (j*iSize)].iImage),p_Bricks[i + (j*iSize)].iFrame,JGDLImage.JGDLMIRROR_NONE);
					}
					Pos.fx += BrickSize.fx;
				}
				Pos.fy += BrickSize.fy;
				i = iStarti;
				Pos.fx = fXPos;
			}
			Pos = null;
		}
	
		//Pinta os sprites da layer
		int iSprites = (int)Sprites.size();
		for(int i = 0; i < iSprites; i++)
		{
			if(((JGDLSprite)Sprites.get(i)).bVisible)
			{
				((JGDLSprite)Sprites.get(i)).Draw();
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CreateSprite()
	// Desc: Creates an sprite an adds it to the scene
	// Pams: 
	//---------------------------------------------------------------------------- 
	public JGDLSprite CreateSprite(String cp_chFileName, JGDLVector cFrameSize)
	{
		JGDLSprite p_Sprite = new JGDLSprite();
		if(p_Sprite != null)
		{
			p_Sprite.pr_Layer = this;
			p_Sprite.pr_Main  = pr_Scene.pr_Main;
			p_Sprite.Create(pr_Scene.pr_Main.ImagesDir + cp_chFileName,cFrameSize);
			Sprites.add(p_Sprite);
			return p_Sprite;
		}
		return null;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Scroll( JGDLVector & vScroll)
	// Desc: Faz o Scroll(movimentação) da layer
	// Pams: Tamanho do deslocamento
	//---------------------------------------------------------------------------- 
	public void Scroll( JGDLVector cScroll)
	{
		Offset.fx += (cScroll.fx*Speed.fx);
		Offset.fy += (cScroll.fy*Speed.fy);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CreateBricks( JGDLVector &cNumBricks)
	// Desc: aloca a matrix de bricks da layer
	// Pams: tamanho da matriz
	//---------------------------------------------------------------------------- 
	public void CreateBricks(JGDLVector cNumBricks)
	{
		//limpa o vetor de bricks
		ClearBricks();
		NumBricks = cNumBricks;
		NumBricks.Floor();
		int iSize = (int)NumBricks.fx*(int)NumBricks.fy;
		p_Bricks = new JGDLBrick[iSize];
		
		//for(int i = 0; i < p_Bricks.length; i++)
		//{}
	
		//memset(p_Bricks,0,iSize*sizeof(JGDLBrick*));
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ClearBricks()
	// Desc: deleta todos os bricks e desaloca o vetor
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void ClearBricks()
	{
		if(p_Bricks != null)
		{
			int iSize = (int)NumBricks.fx*(int)NumBricks.fy;
			for(int i = 0; i < iSize; i++)
			{
				if(p_Bricks[i] != null)
				{
					p_Bricks[i].Release();
					p_Bricks[i] = null;
				}
			}
			p_Bricks = null;
		}	
	}
	
	//---------------------------------------------------------------------------- 
	// Name: SetBrick( JGDLVector& cBrickPos, int iImage, int iFrame)
	// Desc: seta o brick
	// Pams: posição do brick, indices da imagem, numero do quadro
	//---------------------------------------------------------------------------- 
	public void SetBrick( JGDLVector cBrickPos, int iImage, int iFrame)
	{
		if((p_Bricks != null) && (cBrickPos.fy >= 0) && (cBrickPos.fy < NumBricks.fy) 
			&& (cBrickPos.fx >= 0) && (cBrickPos.fx < NumBricks.fx) )
		{
			int iPos = ((int)cBrickPos.fx) + ((int)cBrickPos.fy*(int)NumBricks.fx);
			if(p_Bricks[iPos] == null)
			{
				p_Bricks[iPos] = new JGDLBrick();
			}
			p_Bricks[iPos].iFrame = iFrame;
			p_Bricks[iPos].iImage = iImage;
		}
	}
}
