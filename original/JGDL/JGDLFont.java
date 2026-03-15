/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JGDLFont.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class JGDLFont extends JGDLObject
{
	public static final byte JGDLNONE 			= 0;
	public static final byte JGDLCENTERX 	= 1;
	public static final byte JGDLCENTERY 	= 2;
	public static final byte JGDLCENTER 		= 3;
	
	//---------------------------------------------------------------------------- 
	// Name: JGDLList()
	// Desc: Construtor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLFont()
	{
	}

	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a imagem
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		return true;
	}

	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a imagem
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public static final void DrawText(JGDLVideoManager pr_VideoManager,int iX, int iY, String Text,Color color,Font font)
	{
		DrawText(pr_VideoManager,iX,iY,Text,color,font,JGDLNONE);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetTextWidth(Font fnt, String Text)
	// Desc: retorna a largura do texto
	// Pams: font e texto
	//---------------------------------------------------------------------------- 
	public static final int GetTextWidth(JGDLVideoManager pr_VideoManager, Font fnt, String Text)
	{
		//pegando cor e fontes correntes
		Color CurColor = pr_VideoManager.BackBuffer.graph.getColor();
		Font CurFont = pr_VideoManager.BackBuffer.graph.getFont();
		
		//colocando fonte
		pr_VideoManager.BackBuffer.graph.setFont(fnt);

		FontMetrics fm = pr_VideoManager.BackBuffer.graph.getFontMetrics();
		int iX = fm.stringWidth(Text);

		pr_VideoManager.BackBuffer.graph.setFont(CurFont);
		
		return iX;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a imagem
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public static final void DrawText(JGDLVideoManager pr_VideoManager,int iX, int iY, String Text,Color color,Font font,byte byAlign)
	{
		//pegando cor e fontes correntes
		Color CurColor = pr_VideoManager.BackBuffer.graph.getColor();
		Font CurFont = pr_VideoManager.BackBuffer.graph.getFont();
		
		//colocando fonte e cor
		pr_VideoManager.BackBuffer.graph.setColor(color);
		pr_VideoManager.BackBuffer.graph.setFont(font);
		
		//alinhando texto
		switch(byAlign)
		{
			case JGDLCENTERX:
			{
				FontMetrics fm = pr_VideoManager.BackBuffer.graph.getFontMetrics();
				iX = (int)((pr_VideoManager.BackBuffer.Size.fx*0.5f) - (float)fm.stringWidth(Text)*0.5f);
				break;
			}
			case JGDLCENTERY:
			{
				FontMetrics fm = pr_VideoManager.BackBuffer.graph.getFontMetrics();
				iY = (int)((pr_VideoManager.BackBuffer.Size.fy*0.5f) - (float)fm.getHeight()*0.05f);
				//pr_VideoManager.pr_Main.showStatus("iY: " + iX);
				break;
			}
			case JGDLCENTER:
			{
				FontMetrics fm = pr_VideoManager.BackBuffer.graph.getFontMetrics();
				iX = (int)((pr_VideoManager.BackBuffer.Size.fx*0.5f) - (float)fm.stringWidth(Text)*0.5f);
				iY = (int)((pr_VideoManager.BackBuffer.Size.fy*0.5f) - (float)fm.getHeight()*0.05f);
				//pr_VideoManager.pr_Main.showStatus("TextWidth: " + fm.stringWidth(Text) + " - iX: " + iX + " - iY: " + iY);
				break;
			}
		}

		//desenhando texto
		pr_VideoManager.BackBuffer.graph.drawString(Text,iX,iY);
		
		//restaurando fonte e cor
		pr_VideoManager.BackBuffer.graph.setColor(CurColor);
		pr_VideoManager.BackBuffer.graph.setFont(CurFont);
	}
}

