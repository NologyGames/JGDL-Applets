/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLScene.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

abstract public class JGDLScene extends JGDLObject
{
	//!Lista de zero ou mais layers.
	private JGDLList Layers = new JGDLList();
	//!Lista de imagens que contém os tiles.
	public JGDLList TileImages = new JGDLList();
	//!Ponteiro	para o JGDLMain.
	public JGDLMain		   pr_Main = null;

	//---------------------------------------------------------------------------- 
	// Name: JGDLScene()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLScene()
	{
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a cena
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		if((Layers != null)&&(TileImages != null))
		{
			return true;
		}
		
		System.out.println("Releasing scene (" + Layers.size() + " layers)...");
		int i;
		//deleta as layers da cena
		for(i = (int)Layers.size()-1; i >= 0; i--)
		{
			JGDLLayer pr_Lay = (JGDLLayer)Layers.get(i);
			pr_Lay.Release();
			pr_Lay = null;
		}
		Layers.clear();
	
		System.out.println("Releasing scene tiles (" + TileImages.size() + " tile images)...");
		//deleta os tiles da cena
		for(i = (int)TileImages.size()-1; i >= 0; i--)
		{
			pr_Main.VideoManager.DeleteImage((JGDLImage)TileImages.get(i));
		}
		TileImages.clear();
		
		Layers = null;
		TileImages = null;

		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Update()
	// Desc: Faz a atualização da cena
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Update()
	{
		int iSize = (int)Layers.size();
		for(int i = 0; i < iSize; i++)
		{
			((JGDLLayer)Layers.get(i)).Update();
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Draw()
	// Desc: Desenha a cena. Esta rotina é virtual porque ela pode ser reescrita na cena.
	//		   Isso é usado em ocasiões onde a cena do usuário desenha objetos que a cena da GDL não
	//		   tem controle. Caso essa rotina seja reescrita na classe filha, deve-se chamar a Draw()
	//		   da classe pai, para que continue se desenhando os objetos já adicionados na cena.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Draw()
	{
		//faz um laço para desenhar todas as layers
		int iSize = (int)Layers.size();
		for(int i = 0; i < iSize; i++)
		{
			JGDLLayer pr_Layer = (JGDLLayer)Layers.get(i);
			if(pr_Layer != null && pr_Layer.bVisible)
			{
				pr_Layer.Draw();
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: DeleteLayer(JGDLLayer **p_Layer)
	// Desc: Deleta uma layer
	// Pams: referência ao ponteiro da layer
	//---------------------------------------------------------------------------- 
	public void DeleteLayer(JGDLLayer p_Layer)
	{
		if(null != p_Layer)
		{
			for(int i = (int)Layers.size()-1; i >=0; i--)
			{
				JGDLLayer pr_Lay = (JGDLLayer)Layers.get(i);
				if(pr_Lay == p_Layer)
				{
					pr_Lay = null;
					Layers.remove(i);
					break;
				}
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Scroll( JGDLVector &vScroll)
	// Desc: faz o scroll de todas as layers da cena
	// Pams: vetor de scroll
	//---------------------------------------------------------------------------- 
	public void Scroll( JGDLVector vScroll)
	{
		for(int i = (int)Layers.size()-1; i >=0; i--)
		{
			((JGDLLayer)Layers.get(i)).Scroll(vScroll);
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: AddTileImage( char * cp_chFileName,  JGDLVector & cFrameSize)
	// Desc: adiciona uma imagem para ser utilizada como tile nas layers
	//			 e retorna seu indice no vetor de imagens da cena
	// Pams: nome do arquivo de imagem, Tamanho do quadro da imagem
	//---------------------------------------------------------------------------- 
	public int AddTileImage(String cp_chFileName,  JGDLVector cFrameSize)
	{
		JGDLImage p_Image = pr_Main.VideoManager.LoadImage(cp_chFileName);
		if(p_Image != null)
		{
			p_Image.SetFrameSize(cFrameSize);
			TileImages.add(p_Image);
			return (int)TileImages.size()-1;
		}
		else
		{
			return -1;
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CreateLayer( JGDLVector &BrickSize, JGDLVector &cNumBricks)
	// Desc: cria uma layer na cena
	// Pams: tamanho o brick, matriz contendo o número de bricks
	//---------------------------------------------------------------------------- 
	public JGDLLayer CreateLayer( JGDLVector BrickSize, JGDLVector cNumBricks)
	{
		JGDLLayer p_Layer = CreateLayer(BrickSize);
		if(p_Layer != null)
		{
			p_Layer.CreateBricks(cNumBricks);
			return p_Layer;
		}
		return null;  
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CreateLayer()
	// Desc: cria uma layer na cena
	// Pams: tamanho do brick
	//---------------------------------------------------------------------------- 
	public JGDLLayer CreateLayer( JGDLVector BrickSize)
	{
		JGDLLayer p_Layer = new JGDLLayer();
		if(p_Layer != null)
		{
			p_Layer.BrickSize				= BrickSize;
			p_Layer.pr_Scene				= this;
			Layers.add(p_Layer);
			return p_Layer;
		}
		return null;  
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: Inicializa a cena. Essa rotina deve ser reescrita na cena, para que se possa
	//		 criar todos os objetos e fazer inicializações necessárias.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	abstract public boolean Initialize();

	//---------------------------------------------------------------------------- 
	// Name: Execute()
	// Desc: Executa a cena. Essa rotina deve ser reescrita na cena, para que se possa
	//		 fazer a execução da cena.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	abstract public void Execute();
}
