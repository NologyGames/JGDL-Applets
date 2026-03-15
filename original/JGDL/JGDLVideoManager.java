/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLVideoManager.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

import java.awt.*;
import java.net.*;

public class JGDLVideoManager extends JGDLObject
{
	//!Define o tamanho do BackBuffer
	public JGDLVector					VideoSize = null;
	//!Lista de imagens carregadas.
	private JGDLList				Images = new JGDLList();
	//!Ponteiro de referência para a CGDLMain.
	JGDLMain					pr_Main = null;
	//! BackBuffer
	public JGDLImage					BackBuffer = new JGDLImage();
	public JGDLImage					Loading;
	//!Para carregar imagens
	public MediaTracker 			Media = null;

	//---------------------------------------------------------------------------- 
	// Name: JGDLVideoManager()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	JGDLVideoManager()
	{
		BackBuffer.pr_VideoManager	= this;
	}
	
	
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza o video manager
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		String str = "";
		//deleta imagens lidas
		for(int i = (int)Images.size()-1; i >= 0; i--)
		{
			JGDLImage pr_Image = (JGDLImage)Images.get(i);
			if(null != pr_Image)
			{
				str = "Undeleted Image: ";
				str += pr_Image.FileName;
				System.out.println(str);
				pr_Image.Release();
				pr_Image = null;
				break;
			}
		}
		Images.clear();
	
		//desalocando backbuffer
		if(null != BackBuffer)
		{
			BackBuffer.Release();
			BackBuffer = null;
		}
		
		Media = null;
		
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: inicializa o video manager
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Initialize()
	{
		//creating backbuffer
		BackBuffer.pr_VideoManager = this;
		BackBuffer.CreateBackBuffer(pr_Main);
		
		//iniciando MediaTracker
		Media = new MediaTracker(pr_Main);
	
	
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: LoadImage( char * cp_chFileName)
	// Desc: roda uma imagem e retorna seu ponteiro
	// Pams: nome do arquivo
	//---------------------------------------------------------------------------- 
	public JGDLImage LoadImage(String cp_chFileName)
	{
		//tenta encontrar uma imagem já lida
		for(int i = (int)Images.size()-1; i>=0; i--)
		{
			JGDLImage pr_Image;
			pr_Image = (JGDLImage)Images.get(i);
			//Se já existe
			if(0 == pr_Image.FileName.compareTo(cp_chFileName))
			{
				//Adiciona uma referência
				pr_Image.sReferences++;
				//Retorna a imagem
				return pr_Image;
			}
		}
	
		//Se não existe
		//Aloca a imagem
		JGDLImage p_Image = new JGDLImage();
		if(null != p_Image)
		{
			//Roda do arquivo
			p_Image.pr_VideoManager = this;
			if(!p_Image.LoadFromFile(cp_chFileName))
			{
				p_Image = null;
				return null;
			}

			//Adiciona a lista
			Images.add(p_Image);
		}
	
		//retorna a imagem
		return p_Image;
	}
	
	//--------------------------------------------------------
	// Name: LoadImageFromMT(String FileName)
	// Desc: Lê uma imagem do Media Tracker
	// Pams: FileName
	//-----------------------------------------------------
	public Image LoadImageFromMT(String FileName)
	{
		if(0 == FileName.compareTo(""))
		{
			return null;
		}
		
		FileName = FileName.replace('\\','/');
		Image img;
		//System.out.println(FileName);
		URL path = pr_Main.getClass().getResource(FileName);
		if(null != path)
		{
			img = pr_Main.getImage(path);
		}
		else
		{
	    img = pr_Main.getImage(pr_Main.getCodeBase(),FileName);
	  }
    if(null != img)
    {
      Media.addImage(img,Images.size());
      
      try
      {
        Media.waitForID(Images.size());
      }
      catch (InterruptedException e)
      {
//        System.out.println(e.getMessage());
      }

    }
		System.out.println("Image requested! ID: " + Images.size() + " - File: " + FileName);
		return img;
	}
	
	//--------------------------------------------------------
	// Name: DrawLoading(int iPercentReaded)
	// Desc: Desenha barra de loading no inicio da Applet
	// Pams: porcentagem lida
	//-----------------------------------------------------
	public boolean DrawLoading(float fPercentReaded)
	{
		int ix = 180;
		int iy = 310;
    BackBuffer.graph.setColor(Color.white);
    BackBuffer.graph.fillRect(0,0,(int)BackBuffer.Size.fx, (int)BackBuffer.Size.fy);
    //BackBuffer.graph.drawImage(Logo,30,40,this);

		JGDLFont.DrawText(this,ix,iy,pr_Main.getParameter("LOADING") + " " + pr_Main.GameName + " " + (int)fPercentReaded + "% ...",Color.decode("0x29166f"),new Font("Arial",Font.BOLD,11),JGDLFont.JGDLCENTERX);

    BackBuffer.graph.setColor(Color.darkGray);
    BackBuffer.graph.drawRect(ix - 85, iy - 25, 248, 11);
    Color cor = new Color(0,0,128);
    BackBuffer.graph.setColor(cor);
    BackBuffer.graph.fillRect(ix-82, iy - 22,(int)((fPercentReaded/100.0f)*242), 6);
    cor = null;
    if(null != Loading)
    {
    	BackBuffer.DrawImage(new JGDLVector(70,15),Loading,0,JGDLImage.JGDLMIRROR_NONE);
    }
    else
    {
 			Loading = LoadImage(pr_Main.ImagesDir + "loading.gif");
 			Loading.SetFrameSize(new JGDLVector(312,240));
    }
    pr_Main.getGraphics().drawImage(BackBuffer.image,0,0,pr_Main);
    return true;
	}

	//---------------------------------------------------------------------------- 
	// Name: DeleteImage(JGDLImage** p_Image)
	// Desc: deleta a imagem
	// Pams: referência ao ponteiro da imagem
	//---------------------------------------------------------------------------- 
	public void DeleteImage(JGDLImage p_Image)
	{
		if(null != p_Image)
		{
			//procura pela imagem na lista
			for(int i = (int)Images.size()-1; i >= 0; i--)
			{
				JGDLImage pr_Image = (JGDLImage)Images.get(i);
				
				if(pr_Image == p_Image)
				{
					if(pr_Image.sReferences > 0)
					{
//						System.out.println("Remove 1 image reference to " + pr_Image.FileName);
						pr_Image.sReferences--;
					}
					else
					{
//						System.out.println("Deleting image " + pr_Image.FileName);
						Media.removeImage(pr_Image.image);
						pr_Image.Release();
						pr_Image = null;
						Images.remove(i);
					}
					break;
				}
			}
		}
	}
}
