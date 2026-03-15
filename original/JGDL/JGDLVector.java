/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLVector.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

import java.math.*;

public class JGDLVector extends JGDLObject
{
	//!Eixo X do vetor.
	public float fx;
	//!Eixo Y do vetor.
	public float fy;

	//---------------------------------------------------------------------------- 
	// Name: JGDLVector()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLVector()
	{
		fx = fy = 0.0f;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: JGDLVector(float fxParam, float fyParam)
	// Desc: inicializa o vetor com parâmetros
	// Pams: jgdlvector
	//---------------------------------------------------------------------------- 
	public JGDLVector(JGDLVector Other)
	{
		fx = Other.fx;
		fy = Other.fy;
	}
	//---------------------------------------------------------------------------- 
	// Name: JGDLVector(float fxParam, float fyParam)
	// Desc: inicializa o vetor com parâmetros
	// Pams: fx,fy
	//---------------------------------------------------------------------------- 
	public JGDLVector(float fxParam, float fyParam)
	{
		fx = fxParam;
		fy = fyParam;
	}

	//---------------------------------------------------------------------------- 
	// Name: JGDLVector(int ix, int iy)
	// Desc: inicializao vetor com parâmetros inteiros
	// Pams: componente x, conponente y
	//---------------------------------------------------------------------------- 
	public JGDLVector(int ix, int iy)
	{
		fx = (float)ix;
		fy = (float)iy;
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
	// Name: operator =( JGDLVector v)
	// Desc: atribui a este vetor
	// Pams: outro vector
	//---------------------------------------------------------------------------- 
	public void atrib (JGDLVector cv)
	{
		fx = cv.fx;
		fy = cv.fy;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator =( JGDLVector v)
	// Desc: atribui a este vetor
	// Pams: outro vector
	//---------------------------------------------------------------------------- 
	public void atrib (float fxParam, float fyParam)
	{
		fx = fxParam;
		fy = fyParam;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator =( JGDLVector v)
	// Desc: atribui a este vetor
	// Pams: outro vector
	//---------------------------------------------------------------------------- 
	public void atrib(int ixParam, int iyParam)
	{
		fx = (float)ixParam;
		fy = (float)iyParam;
	}

	//---------------------------------------------------------------------------- 
	// Name: operator ==( JGDLVector v)
	// Desc: compara o vetor
	// Pams: outro vector
	//---------------------------------------------------------------------------- 
	public boolean operatoreqeq (JGDLVector cv)
	{
		return ((fx == cv.fx) && (fy == cv.fy));
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator ==( JGDLVector v)
	// Desc: compara o vetor
	// Pams: outro vector
	//---------------------------------------------------------------------------- 
	public boolean operatornoteq (JGDLVector cv)
	{
		return (fx != cv.fx || fy != cv.fy);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Magnitude()
	// Desc: retorna o módulo do vetor
	// Pams: none
	//---------------------------------------------------------------------------- 
	public float Magnitude()
	{
		return (float)Math.sqrt((fx * fx) + (fy * fy));
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Normalize()
	// Desc: trasforma este vetor em vetor unitário
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void Normalize()
	{
		float fMag = Magnitude();
		fx /= fMag;
		fy /= fMag;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator-( JGDLVector v)
	// Desc: retorna a subtração do vetor
	// Pams: outro vetor
	//---------------------------------------------------------------------------- 
	public JGDLVector operatorminus (JGDLVector cv)
	{
		return new JGDLVector(fx - cv.fx, fy - cv.fy);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator +( JGDLVector v)
	// Desc: retorna a soma do vetor
	// Pams: outro vetor
	//---------------------------------------------------------------------------- 
	public JGDLVector operatorplus (JGDLVector cv)
	{
		return new JGDLVector(fx + cv.fx, fy + cv.fy);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator+=( JGDLVector v)
	// Desc: soma outro vetor a este
	// Pams: outro vetor
	//---------------------------------------------------------------------------- 
	public void operatorpluseq (JGDLVector cv)
	{
		fx += cv.fx;
		fy += cv.fy;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator -=( JGDLVector v)
	// Desc: subtrai outro vetor deste 
	// Pams: outro vetor
	//---------------------------------------------------------------------------- 
	public void operatorminuseq (JGDLVector cv)
	{
		fx -= cv.fx;
		fy -= cv.fy;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator*(float fVal)
	// Desc: retorna a multiplicação do vetor pela ante
	// Pams: ante
	//---------------------------------------------------------------------------- 
	public JGDLVector operatormult (float cfVal)
	{
		return new JGDLVector(fx*cfVal,fy*cfVal);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator/(float fVal)
	// Desc: retorna a divisão do vetor pela ante
	// Pams: ante
	//---------------------------------------------------------------------------- 
	public JGDLVector operatordiv (float cfVal)
	{
		return new JGDLVector(fx/cfVal,fy/cfVal);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator *=( float fVal)
	// Desc: multiplica o vetor pela ante
	// Pams: ante
	//---------------------------------------------------------------------------- 
	public void operatormulteq (float cfVal)
	{
		fx *= cfVal;
		fy *= cfVal;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: operator /=( float fVal)
	// Desc: divide o vetor pela ante
	// Pams: ante
	//---------------------------------------------------------------------------- 
	public void operatordiveq (float cfVal)
	{
		fx /= cfVal;
		fy /= cfVal;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: DotProduct( JGDLVector v)
	// Desc: Retorna o produto escalar dos vetors
	// Pams: vetor
	//---------------------------------------------------------------------------- 
	public float DotProduct( JGDLVector cv)
	{
		return ( (fx*cv.fx) + (fy*cv.fy) );
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Floor()
	// Desc: elimina as casa descimais das componentes do vetor
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Floor()
	{
		fx = (float)Math.floor(fx);
		fy = (float)Math.floor(fy);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Ceil()
	// Desc: aredonda os elementos do vetor para cima
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Ceil()
	{
		fx = (float)Math.ceil(fx);
		fy = (float)Math.ceil(fy);
	}	
}
