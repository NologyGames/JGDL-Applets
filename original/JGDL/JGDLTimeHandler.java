/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLTimeHandler.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

public class JGDLTimeHandler extends JGDLObject
{
	private static final byte JGDLMINFRAMEINTERVAL = 5;
	private static final byte JGDLMAXFRAMEINTERVAL = 50;
	
	//!Ligar esse flag para prevenir slowdown.
	public boolean bPreventLowdown;
	//!Armazena o momento em milisegundos em que o frame é desenhado.
	public long iFrameTime;
	//!Armazena o momento em segundos em que o frame é desenhado.
	public float fFrameTime;
	//!Armazena o último tempo obtido (em milisegundos) .
	private long uiLastTime;
	//!Tempo acumulado desde o último tempo obtido.
	private int iTimeAcum;
	//!Quantos quadros se passaram durante este período.
	private int	iFrameCount;
	//!Número de quadros por segundos.
	private int	iFps;

	//---------------------------------------------------------------------------- 
	// Name: JGDLTimeHandler()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLTimeHandler()
	{
		bPreventLowdown = false;
		Initialize();
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza o time handler
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: inicializa o time handler
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Initialize()
	{
		uiLastTime  = System.currentTimeMillis();
		fFrameTime  = 0.0f;
		iFrameTime  = 0;
		iFps        = 0;
		iFrameCount = 0;
		iTimeAcum   = 0;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Update()
	// Desc: Atualiza o time handler.
	//		 Este método é chamado a cada quadro de animação
	// Pams: nenhum 
	//---------------------------------------------------------------------------- 	
	public void Update()
	{
		iFrameTime = 0;
		long uiCurrentTime;
		
		do
		{
			uiCurrentTime	= System.currentTimeMillis();
			iFrameTime		= (uiCurrentTime >  uiLastTime)? uiCurrentTime - uiLastTime : 0;
			uiLastTime		= (uiCurrentTime >= uiLastTime)? uiLastTime : uiCurrentTime;
			if(iFrameTime < JGDLMINFRAMEINTERVAL)
			{
				try
				{
					Thread.yield();
					Thread.sleep(JGDLMINFRAMEINTERVAL);
				}
				catch(Exception e)
				{
				}
			}
		}while(!(iFrameTime >= JGDLMINFRAMEINTERVAL));
	
		if(bPreventLowdown && (iFrameTime > JGDLMAXFRAMEINTERVAL))
		{
			iFrameTime = JGDLMAXFRAMEINTERVAL;
		}
	
		iTimeAcum  += iFrameTime;
		iFrameCount++;
		fFrameTime = (float)iFrameTime * 0.001f;
	
		if(iTimeAcum >= 1000)
		{
			iFps				= iFrameCount;
			iFrameCount = 0;
			iTimeAcum   = 0;
		}
	
		uiLastTime = uiCurrentTime;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetFPS()
	// Desc: retorna o número de quadros por segundo
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public int GetFPS()
	{
		return iFps;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Reset()
	// Desc: Reinicializa o time handler
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Reset()
	{
		uiLastTime = System.currentTimeMillis();
	}
}
