/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLAnimation.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

public class JGDLAnimation extends JGDLObject
{
	//!Ponteiro para o main da GDL.
	public JGDLMain pr_Main = null;
	//!Indica que a animação recomeça ao acabar se true.
	public boolean bRepeat;
	//!Indices dos quadros de animação na imagem.
	public JGDLList Frames = new JGDLList();
	//!Número de quadros por segundo na animação.
	public int iFramesPerSecond;
	//!Acumulator de tempo (usado para trocar quadros de animação).
	public int uiTimeAccum;
	//!Índice do quadro atual no vetor de frames.
	private int iCurrentFrame;

	//---------------------------------------------------------------------------- 
	// Name: JGDLAnimation()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLAnimation()
	{
		bRepeat				= false;
		iCurrentFrame		= 0;
		iFramesPerSecond	= 1;
		pr_Main				= null;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a animação
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		Frames.clear();
		Frames = null;
		
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Ended()
	// Desc: retorna true se a animação acabou
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Ended()
	{
		return (bRepeat)? false : iCurrentFrame >= Frames.size();
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Update()
	// Desc: Atualiza a animação
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Update()
	{
		if(null != pr_Main)
		{
			//pega o intervalo entre os quadros
			 int iFrameInterval = (iFramesPerSecond != 0) ? 1000/iFramesPerSecond : 1;
	
			//Atualiza o acumulador de tempo
			uiTimeAccum += pr_Main.TimeHandler.iFrameTime;
	
			//Se o acumulador eh maior que o intervalo entre quadros
			if(uiTimeAccum >= iFrameInterval)
			{
				//Calcula quantos quadros se passaram
				int iSum = (int)(uiTimeAccum / iFrameInterval);
	
				//Soma os quadros ao quadro corrente
				if(bRepeat)
				{
	        		iCurrentFrame += iSum;
					iCurrentFrame %= Frames.size();
				}
				else
				{
					iCurrentFrame += (iCurrentFrame < (char)Frames.size())? iSum : 0;
				}
				//reduz o acumulador ao resto da sua divisão pelo intervalo
				uiTimeAccum %= iFrameInterval;
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetCurrentFrame()
	// Desc: Retorna o quadro corrente da animação
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public int GetCurrentFrame()
	{
		Integer iRet = (Integer) (((iCurrentFrame >= 0) && (iCurrentFrame < (int)Frames.size())) ? Frames.get(iCurrentFrame) : Frames.get(Frames.size()-1));
		return iRet.intValue();
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Reset()
	// Desc: Reseta a animação
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Reset()
	{
		uiTimeAccum		= 0;
		iCurrentFrame = 0;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetClone()
	// Desc: retorna um clone da animação
	// Pams: 
	//---------------------------------------------------------------------------- 
	public JGDLAnimation GetClone()
	{
		JGDLAnimation p_Clone	= new JGDLAnimation();
		if(null != p_Clone)
		{
			p_Clone.bRepeat					= bRepeat;
			p_Clone.iCurrentFrame		= iCurrentFrame;
			p_Clone.iFramesPerSecond = iFramesPerSecond;
			p_Clone.pr_Main					= pr_Main;
			p_Clone.uiTimeAccum			= uiTimeAccum;
	
			int iSize = Frames.size();
			for(int i = 0; i < iSize; i++)
			{
				p_Clone.Frames.add(Frames.get(i));
			}
			return p_Clone;
		}
		return null;	
	}
}
