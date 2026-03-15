/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLImage.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

import java.awt.*;

public class JGDLImage extends JGDLObject
{
	public final static byte JGDLMIRROR_NONE				= 0;
	public final static byte JGDLMIRROR_LEFTRIGHT		= 1;
	public final static byte JGDLMIRROR_UPDOWN			= 2;
	
	public final static JGDLFrame TempFrame  = new JGDLFrame();


 	public Image        image 				= null;
	public Graphics		graph				= null;

	//!Número de referências existentes para esta imagem.
	short sReferences = 0;

	//!Nome do arquivo de imagem (bitmap).
	String FileName = "";

	//!Dimensões da imagem em pixels.
	public JGDLVector Size = new JGDLVector();

	//!Tamanho do quadro que será pintado na tela, em pixels.
	public JGDLVector FrameSize = new JGDLVector();

	//!Referência para o gerenciador de vídeo.
	JGDLVideoManager pr_VideoManager = null;

	//!Lista de quadros de animação da imagem.
	JGDLList Frames = new JGDLList();

	//---------------------------------------------------------------------------- 
	// Name: JGDLImage()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLImage()
	{
		//p_Surface			= null;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a imagem
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
	  	image 		= null;
	  	graph 		= null;
			ClearFrames();
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: CreateBackBuffer(Component app)
	// Desc: Cria a imagem como back buffer
	// Pams: application
	//---------------------------------------------------------------------------- 
	public boolean CreateBackBuffer(Component app)
	{
		if(pr_VideoManager != null)
		{
			Release();
			
			//criar aqui
		    Size.fx = app.getSize().width;
		    Size.fy = app.getSize().height;
		    image = app.createImage((int)Size.fx,(int)Size.fy);

		    if(graph != null)
		    {
		      graph.dispose();
		    }
		    
		    graph = image.getGraphics();

//		    System.out.println("BackBuffer Criado!");
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Load( char *cp_chFileName)
	// Desc: Roda a imagem do arquivo
	// Pams: nome do arquivo
	//---------------------------------------------------------------------------- 
	public boolean LoadFromFile(String cp_chFileNameParam)
	{
		Release();
		if(cp_chFileNameParam != "")
		{
			FileName = cp_chFileNameParam;
			
			//criar aqui
			image = pr_VideoManager.LoadImageFromMT(FileName);
	
			Size.fx = image.getWidth(pr_VideoManager.pr_Main);
			Size.fy = image.getHeight(pr_VideoManager.pr_Main);
			
			return true;	
		}
		return false;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: DrawImage(JGDLFrame p_DestFrame, JGDLImage p_SrcImage,JGDLFrame p_SrcFrame)
	// Desc: Pinta uma imagem sobre esta 
	// Pams: Frame Destino, Imagem Fonte, Frame Fonte
	//---------------------------------------------------------------------------- 
	public boolean DrawImage(JGDLFrame p_DestFrame, JGDLImage p_SrcImage,JGDLFrame p_SrcFrame,byte byMirror)
	{
		while(p_SrcImage == null);
		switch(byMirror)
		{
			case JGDLMIRROR_NONE:
			{
				while(!graph.drawImage(p_SrcImage.image,p_DestFrame.iLeft,p_DestFrame.iTop,p_DestFrame.iRight,p_DestFrame.iBottom,p_SrcFrame.iLeft,p_SrcFrame.iTop,p_SrcFrame.iRight,p_SrcFrame.iBottom,pr_VideoManager.pr_Main));
				return true;
			}
			case JGDLMIRROR_LEFTRIGHT:
			{
				int iSrcWidth     = p_SrcFrame.GetWidth();
				int iSrcHeight		= p_SrcFrame.GetHeight();
				int iX = p_DestFrame.iLeft;
				int iY = p_DestFrame.iTop;
				
				if(iX + iSrcWidth > (int)Size.fx)
				{
					p_SrcFrame.iLeft	  += iX + iSrcWidth - (int)Size.fx;
					p_DestFrame.iRight -= iX + iSrcWidth - (int)Size.fx;
				}	
				if(iY + iSrcHeight > (int)Size.fy)
				{
					p_SrcFrame.iBottom	-= iY + iSrcHeight - (int)Size.fy;
					p_DestFrame.iBottom	-= iY + iSrcHeight - (int)Size.fy;
				}	
				if(iY < 0)
				{
					p_SrcFrame.iTop		-= iY;
					p_DestFrame.iTop		-= iY;
				}	
				if(iX < 0)
				{
					p_SrcFrame.iRight		+= iX;
					p_DestFrame.iLeft		-= iX;
				}
				
				//desenhando....
//				while(!graph.drawImage(p_SrcImage.image,p_DestFrame.iLeft,p_DestFrame.iTop,p_DestFrame.iRight,p_DestFrame.iBottom,p_SrcFrame.iRight,p_SrcFrame.iTop,p_SrcFrame.iLeft,p_SrcFrame.iBottom,pr_VideoManager.pr_Main));
				
				return true;
			}
			case JGDLMIRROR_UPDOWN:
			{
				int iSrcWidth     = p_SrcFrame.GetWidth();
				int iSrcHeight		= p_SrcFrame.GetHeight();
				int iX = p_DestFrame.iLeft;
				int iY = p_DestFrame.iTop;
	
				if(iX + iSrcWidth > (int)Size.fx)
				{
					p_SrcFrame.iRight  -= iX + iSrcWidth - (int)Size.fx;
					p_DestFrame.iRight -= iX + iSrcWidth - (int)Size.fx;
				}
				if(iY + iSrcHeight > (int)Size.fy)
				{
					p_SrcFrame.iTop	    += iY + iSrcHeight - (int)Size.fy;
					p_DestFrame.iBottom	-= iY + iSrcHeight - (int)Size.fy;
				}
				if(iY < 0)
				{
					p_SrcFrame.iBottom	+= iY;
					p_DestFrame.iTop		-= iY;
				}
				if(iX < 0)
				{
					p_SrcFrame.iLeft		-= iX;
					p_DestFrame.iLeft		-= iX;
				}
	
				//desenhando...
//				while(!graph.drawImage(p_SrcImage.image,p_DestFrame.iLeft,p_DestFrame.iTop,p_DestFrame.iRight,p_DestFrame.iBottom,p_SrcFrame.iLeft,p_SrcFrame.iBottom,p_SrcFrame.iRight,p_SrcFrame.iTop,pr_VideoManager.pr_Main));

				return true;
			}
			case JGDLMIRROR_LEFTRIGHT | JGDLMIRROR_UPDOWN:
			{
				int iSrcWidth     = p_SrcFrame.GetWidth();
				int iSrcHeight		= p_SrcFrame.GetHeight();
				int iX = p_DestFrame.iLeft;
				int iY = p_DestFrame.iTop;
	
				if(iX + iSrcWidth > (int)Size.fx)
				{
					p_SrcFrame.iLeft	  += iX + iSrcWidth - (int)Size.fx;
					p_DestFrame.iRight -= iX + iSrcWidth - (int)Size.fx;
				}
	
				if(iY + iSrcHeight > (int)Size.fy)
				{
					p_SrcFrame.iTop	    += iY + iSrcHeight - (int)Size.fy;
					p_DestFrame.iBottom	-= iY + iSrcHeight - (int)Size.fy;
				}
	
				if(iY < 0)
				{
					p_SrcFrame.iBottom	+= iY;
					p_DestFrame.iTop		-= iY;
				}
	
				if(iX < 0)
				{
					p_SrcFrame.iRight		+= iX;
					p_DestFrame.iLeft		-= iX;
				}
	
				//desenhando...
//				while(!graph.drawImage(p_SrcImage.image,p_DestFrame.iLeft,p_DestFrame.iTop,p_DestFrame.iRight,p_DestFrame.iBottom,p_SrcFrame.iRight,p_SrcFrame.iBottom,p_SrcFrame.iLeft,p_SrcFrame.iTop,pr_VideoManager.pr_Main));

				return true;
			}
		}
		
		return false;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Clear(Color color)
	// Desc: Pinta a surface com a cor fornacida
	// Pams: cor
	//---------------------------------------------------------------------------- 
	public void Clear(Color color)
	{
	    graph.setColor(color);
	    graph.fillRect(0,0,(int)Size.fx,(int)Size.fy);	
	}
	
	//---------------------------------------------------------------------------- 
	// Name: SetFrameSize( JGDLVector cSizeParam)
	// Desc: Seta o tamannho do Frame para esta imagem
	// Pams: Tamanho do frame
	//---------------------------------------------------------------------------- 
	public void SetFrameSize(JGDLVector cSizeParam)
	{
		if(FrameSize != cSizeParam)
		{
			FrameSize = cSizeParam;
			FrameSize.fx = (FrameSize.fx > Size.fx)? Size.fx : FrameSize.fx;
			FrameSize.fy = (FrameSize.fy > Size.fy)? Size.fy : FrameSize.fy;
			ClearFrames();
			if(FrameSize.fx >= 1.0f && FrameSize.fy >= 1.0f)
			{
				int iSizeX = ((int)Size.fx)/((int)FrameSize.fx);
				int iSizeY = ((int)Size.fy)/((int)FrameSize.fy);
	
				for(int j = 0; j < iSizeY; j++)
				{
					for(int i = 0; i < iSizeX; i++)
					{
						JGDLFrame p_Frame = new JGDLFrame();
						p_Frame.iTop			= j*(int)FrameSize.fy;
						p_Frame.iBottom	= (j+1)*(int)FrameSize.fy;
						p_Frame.iLeft		= i*(int)FrameSize.fx;
						p_Frame.iRight		= (i+1)*(int)FrameSize.fx;
						Frames.add(p_Frame);
					}
				}
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ClearFrames()
	// Desc: limpa os frames da imagem
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void ClearFrames()
	{
		for(int i = (int)Frames.size()-1; i >=0; i--)
		{
			Frames.set(i,null);
		}
		Frames.clear();
	}
	
	//---------------------------------------------------------------------------- 
	// Name: DrawImage()
	// Desc: Pinta uma imagem sobre a outra
	// Pams: posição no destino, imagem fonte, numero do frame na imagem fonte
	//---------------------------------------------------------------------------- 
	public boolean DrawImage( JGDLVector cPosition, JGDLImage p_SrcImage, int iFrame,byte byMirror)
	{
		if((iFrame < 0) || (iFrame >= (int)p_SrcImage.Frames.size()))
		{
			return false;
		}
		JGDLFrame Source = (JGDLFrame)p_SrcImage.Frames.get(iFrame);
		JGDLFrame Dest  = TempFrame;
		Dest.iLeft		= (int)cPosition.fx;
		Dest.iTop		= (int)cPosition.fy;
		Dest.iBottom	= Dest.iTop  + Source.GetHeight();
		Dest.iRight	  	= Dest.iLeft + Source.GetWidth();
	
		DrawImage(Dest,p_SrcImage,Source,byMirror);
		Dest = null;
		
		return true;
	}
}
