/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLBrick.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

public class JGDLBrick extends JGDLObject
{
	//!Índice da imagem no vetor de tiles da cena.
	public int iImage;
	//!Índice do frame na imagem.
	public int	iFrame;

	//---------------------------------------------------------------------------- 
	// Name: CGDLBrick()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	JGDLBrick()
	{
		iImage = 0;
		iFrame = 0;
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
}
