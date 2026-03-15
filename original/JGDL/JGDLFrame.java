/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLFrame.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

public class JGDLFrame extends JGDLObject
{
	//!Posição X à esquerda
	public int iLeft;
	//!Posição Y acima
	public int iTop;
	//!Posição X à direita
	public int iRight;
	//!Posição Y abaixo
	public int iBottom;

	//---------------------------------------------------------------------------- 
	// Name: JGDLFrame()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLFrame()
	{
		iLeft = iTop = iBottom = iRight = 0;
	}	
	
	//---------------------------------------------------------------------------- 
	// Name: GetWidth()
	// Desc: retorna a largura do quadro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public int GetWidth()
	{
		return iRight - iLeft;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetHeight()
	// Desc: retorna a altura do quadro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public int GetHeight()
	{
		return iBottom - iTop;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: IsValid()
	// Desc: retorna true se o quadro for válido
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean IsValid()
	{
		return (iBottom >= iTop && iRight >= iLeft);
	}

	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a classe
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		return true;
	}

	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a classe
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void atrib(JGDLFrame frame)
	{
		iBottom = frame.iBottom;
		iLeft = frame.iLeft;
		iRight = frame.iRight;
		iTop = frame.iTop;
	}
}
