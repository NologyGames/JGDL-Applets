/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JGDLList.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

public class JGDLList extends JGDLObject
{
	int 						 iBuffSize;
	int 						 iDataSize;
	private Object[] data = null;
	
	//---------------------------------------------------------------------------- 
	// Name: JGDLList()
	// Desc: Construtor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLList()
	{
		iBuffSize = 128;
		iDataSize = 0;
		data = new Object[iBuffSize];
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ReallocData(int iNewSize)
	// Desc: reallocates the list
	// Pams: new size
	//---------------------------------------------------------------------------- 
	private void ReallocData(int iNewSize)
	{
		Object [] newdata = new Object[iNewSize];

		if(null != data)
		{
			System.arraycopy(data,0,newdata,0,iDataSize);
		}

		for(int i = 0; i < iDataSize; i++)
		{
			data[i] = null;
		}
		
		data 		= newdata;
		newdata = null;
		iBuffSize = iNewSize;
	
	}


	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a imagem
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		clear();
		data			= null;
		iBuffSize = 0;
		
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: clear()
	// Desc: Limpa a lista
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void clear()
	{
		if(data != null)
		{
			for(int i = 0; i < iDataSize; i++)
			{
				data[i] = null;
			}
			iDataSize = 0;
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: set()
	// Desc: Atribui um valor a um elemento da lista
	// Pams: indice do elemento, valor
	//---------------------------------------------------------------------------- 
	public void set(int iIndex,Object value)
	{
		if((data!=null)&&(iIndex < data.length))
		{
			data[iIndex] = value;
		}
	}

	//---------------------------------------------------------------------------- 
	// Name: push_back()
	// Desc: Adiciona um elemento no fim da lista
	// Pams: elemento
	//---------------------------------------------------------------------------- 
	public void push_back(Object element)
	{
		add(element);
	}
	//---------------------------------------------------------------------------- 
	// Name: push_front()
	// Desc: Adiciona um elemento no início da lista
	// Pams: elemento
	//---------------------------------------------------------------------------- 
	public void push_front(Object element)
	{
		if(iDataSize + 1 >= iBuffSize)
		{
			ReallocData(iBuffSize + 128);
		}
		
		for(int i = iDataSize; i > 0; i--)		
		{
			data[i] = data[i-1];
		}
		
		data[0] = element;
		iDataSize++;
		
	}
	
	//---------------------------------------------------------------------------- 
	// Name: add()
	// Desc: Adiciona um elemento no fim da lista
	// Pams: elemento
	//---------------------------------------------------------------------------- 
	public void add(Object element)
	{
		if(iDataSize + 1 >= iBuffSize)
		{
			ReallocData(iBuffSize + 128);
		}
		data[iDataSize] = element;
		iDataSize++;
		

	}
	
	//---------------------------------------------------------------------------- 
	// Name: size()
	// Desc: Retorna o tamanho da lista
	// Pams: elemento
	//---------------------------------------------------------------------------- 
	public int size()
	{
		return iDataSize;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: get()
	// Desc: Pega um elemento da lista
	// Pams: indice do elemento
	//---------------------------------------------------------------------------- 
	public Object get(int iIndex)
	{
		if(iIndex < size())
		{
			return data[iIndex];
		}
		
		return null;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: remove()
	// Desc: Remove um elemento da lista
	// Pams: elemento
	//---------------------------------------------------------------------------- 
	public void remove(int iIndex)
	{
		if(iIndex >= 0 && iIndex < iDataSize)
		{
			for(int i = iIndex; i < iDataSize-1;i++)
			{
				data[i] = data[i+1];
			}
			data[iDataSize-1] = null;
			iDataSize--;
		}
	}	
	
}
