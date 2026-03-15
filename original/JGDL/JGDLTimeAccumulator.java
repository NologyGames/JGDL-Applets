package JGDL;

public class JGDLTimeAccumulator extends JGDLObject
{
	//!Ponteiro para o JGDLMain.
	JGDLMain	pr_Main = null;
	
	//limite de tempo
	public int iTimeLimit 	= 0;
	
	//accumulador de tempo
	public int iTimeAccum 	= 0;

	//---------------------------------------------------------------------------- 
	// Name: Init(JGDLMain	pr_MainParam, int iTimeLimitParam)
	// Desc: inicializa o acumulador de tempo
	// Pams: main, limite de tempo
	//---------------------------------------------------------------------------- 
	public void Init(JGDLMain	pr_MainParam, int iTimeLimitParam)
	{
		pr_Main 		= pr_MainParam;
		iTimeLimit 	= iTimeLimitParam;
		iTimeAccum  = 0;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Ended()
	// Desc: retorna true se o acumulador de tempo acabou
	// Pams: main, limite de tempo
	//---------------------------------------------------------------------------- 
	public boolean Ended()
	{
		return iTimeAccum >= iTimeLimit;
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
	// Name: Restart()
	// Desc: reinicia
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Restart()
	{
		iTimeAccum %= iTimeLimit;
	}

	//---------------------------------------------------------------------------- 
	// Name: Restart()
	// Desc: reinicia o acumulador de tempo
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Update()
	{
		if(null != pr_Main)
		{
			iTimeAccum += pr_Main.TimeHandler.iFrameTime;
		}
	}
}