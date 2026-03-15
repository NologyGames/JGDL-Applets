/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLObject.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

public abstract class JGDLObject
{
	//---------------------------------------------------------------------------- 
	// Name: JGDLObject(void)
	// Desc: Construtor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLObject()
	{
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ReleaseObject(JGDLObject pr_Object)
	// Desc: Libera e desaloca um objeto da JGDL
	// Pams: objeto
	//---------------------------------------------------------------------------- 
	public static final void ReleaseObject(JGDLObject pr_Object)
	{
		if(pr_Object != null)
		{
			pr_Object.Release();
			pr_Object = null;
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release(void)
	// Desc: finaliza a classe
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public abstract boolean Release();
	
	//---------------------------------------------------------------------------- 
	// Name: finalize()
	// Desc: chamada pelo garbage collector para desalocar a class
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	protected void finalize() throws Throwable
	{
		Release();
	}
}
