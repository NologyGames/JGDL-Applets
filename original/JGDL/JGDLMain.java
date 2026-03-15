/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLMain.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

import java.util.*;
import java.awt.*;
import java.applet.*;

abstract public class JGDLMain extends Applet implements Runnable
{
	//!Mostrar informações do jogo (FPS e MousePos)
	protected boolean bShowInfo = true;
	//!Cor das informações do jogo (FPS e MousePos)
	protected Color InfoColor = new Color(0,255,0);
	//!Lista de imagens a serem carregadas
	private JGDLList ImagesList = new JGDLList();
	//!Lista de sons a serem carregados
	private JGDLList SoundsList = new JGDLList();
	//!Diretório de imagens
	String ImagesDir = "Surfaces/";
	//!Diretório de sons
	String SoundsDir = "Sounds/";
	
	//!Referência para a cena corrente do jogo.
	private JGDLScene pr_CurScene = null;
	//!Thread executora do jogo
	private Thread Executor = new Thread(this);
	//!Flags de renderização e sinal de jogo rodando
	private boolean bRendering = false,bRunning = false;
	//!Dimensões do jogo
	private Dimension GameDimension = new Dimension();
	//!Flag que indica se o jogo está pausado.
	private boolean bPaused = false;
	//!Diretório corrente para abrir aqruivos (vem do HTML)
	protected String CurDir = "";
	//!Nome do jogo (vem do HTML)
	protected String GameName = "";
	//!Gerenciador de vídeo.
	public JGDLVideoManager VideoManager = new JGDLVideoManager();
	//!Gerenciador de dispositivos de entrada.
	public JGDLInputManager InputManager = new JGDLInputManager();
	//!Gerenciador de efeitos sonoros e música.
	public JGDLSoundManager SoundManager = new JGDLSoundManager();
	//!Controla o tempo ou relógio do jogo.
	public JGDLTimeHandler	TimeHandler = new JGDLTimeHandler();

	//!se verdadeiro, pausa o jogo quando perde o foco
	//senao continua executando normalmente
	public boolean bPauseOnLostFocus = true;
	
	//!randomizador
	public Random Randomizer = new Random();


	Font InfoFont  = new Font("Arial",Font.BOLD,12);
	Font PauseFont = new Font("Arial",Font.ITALIC|Font.BOLD,20);


	//---------------------------------------------------------------------------- 
	// Name: JGDLMain()
	// Desc: ructor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLMain()
	{
		VideoManager.pr_Main	= this;
		InputManager.pr_Main  	= this;
		SoundManager.pr_Main  	= this;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: AddImage(String filename)
	// Desc: Adiciona uma imagem à lista
	// Pams: nome do arquivo
	//---------------------------------------------------------------------------- 
	protected void AddImage(String filename)
	{
		ImagesList.add(ImagesDir + filename);		
	}

	//---------------------------------------------------------------------------- 
	// Name: AddSound(String filename)
	// Desc: Adiciona um som à lista
	// Pams: nome do arquivo
	//---------------------------------------------------------------------------- 
	protected void AddSound(String filename)
	{
		SoundsList.add(SoundsDir + filename);		
	}

	//---------------------------------------------------------------------------- 
	// Name: destroy()
	// Desc: A Applet está sendo destruída
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void destroy()
	{
//		System.out.println("destroying... (Executor = null)");
		Executor = null;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a GDL Main 
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
//		System.out.println("releasing main...");

	    bRunning = false;
	    /*try  { Thread.sleep(80); } 
	    catch (InterruptedException e) { ; }*/
	
	    while(bRendering)
	    {
	      try  { Thread.sleep(30); } 
	      catch (InterruptedException e) { ; }
	    }
	    
//		System.out.println("releasing scene...");
		//setando cena corrente como nula
		if(pr_CurScene != null)
		{
			pr_CurScene.Release();
			pr_CurScene = null;
		}	
		
		System.out.println("releasing video...");
		//liberando video
		VideoManager.Release();
		
		System.out.println("releasing input...");
		//liberando entradas
		InputManager.Release();
		
		System.out.println("releasing sound...");
		//liberando som
		SoundManager.Release();
		
		ImagesList.clear();
		ImagesList = null;
		SoundsList.clear();
		SoundsList = null;
	
		return true;
	}

	//--------------------------------------------------------
	// Name: LoadResources()
	// Desc: load resources from media trackers
	// Pams: none
	//-----------------------------------------------------
	private void LoadResources()
	{
//		System.out.println("LoadResources()");
		//carregando imagens
		int iCurFile = 0;
		int iSize = ImagesList.size() + SoundsList.size();
		int i = 0;
		for(i = 0; i < ImagesList.size(); i++)
		{
			VideoManager.LoadImage((String)ImagesList.get(iCurFile));
			iCurFile++;
			float fPercent = ((float)iCurFile/(float)iSize)*100.0f;
			VideoManager.DrawLoading(fPercent);
//			System.out.println((int)fPercent + "%");
		}
		
		//carregando sons
		for(i = 0; i < SoundsList.size(); i++)
		{
			SoundManager.LoadSound((String)SoundsList.get(i));
			iCurFile++;
			float fPercent = ((float)iCurFile/(float)iSize)*100.0f;
			VideoManager.DrawLoading(fPercent);
//			System.out.println((int)fPercent + "%");
		}
	}
	
	//--------------------------------------------------------
	// Name: run()
	// Desc: runs the thread
	// Pams: none
	//-----------------------------------------------------
	public void run()
	{
//		System.out.println("run()");
		
//		bPaused = true;

		//carregando sons e imagens
		LoadResources();
		
		//initializes the game
		InitGame();
		
		while (bRunning)
		{
			bRendering = true;
			
			//loop do jogo
			Loop();
			
			bRendering = false;
		}
		
//		System.out.println("saindo do run()");
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: inicializa a GDL Main
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Initialize()
	{
//		System.out.println("Init Video...");
		
		//inicializando video
		if(!VideoManager.Initialize())
		{
			return false;
		}
	
//		System.out.println("Init Input...");
		//inicializando entradas
		if(!InputManager.Initialize())
		{
			return false;
		}
	
		System.out.println("Init Sound...");
		//inicializa o som
		SoundManager.Initialize();
	
		return true;	
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Loop()
	// Desc: loop principal do jogo
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Loop()
	{
		//lê entradas
		InputManager.Read();
	
		//atualiza tempo
		TimeHandler.Update();
	
		//limpa o backbuffer
		VideoManager.BackBuffer.Clear(Color.black);

		//desenha cena
		if(pr_CurScene != null)
		{
			pr_CurScene.Draw();
			
			//somente atualiza o jogo se ele não estiver pausado
			if(!bPaused)
			{
				pr_CurScene.Update();
				pr_CurScene.Execute();
			}
		}
		
		if(bShowInfo)
		{
			JGDLFont.DrawText(VideoManager,0,10,"FPS: " + TimeHandler.GetFPS(),InfoColor,InfoFont);
			JGDLFont.DrawText(VideoManager,0,20,"MOU: X: " + InputManager.GetMousePos().fx + " Y: " + InputManager.GetMousePos().fy,InfoColor,InfoFont);
			String s = "Seconds:";
			long l = (System.currentTimeMillis()%60000);
			l /= 1000;
			s += l; 
			JGDLFont.DrawText(VideoManager,300,20,s,InfoColor,InfoFont);
			
		}
		
		//mostra mensagem de pausa...
		if(bPaused)
	 	{
	 		DrawPause();
	 	}
		
		//troca frontbuffer com backbuffer
		getGraphics().drawImage(VideoManager.BackBuffer.image,0,0,this);
	}

	//---------------------------------------------------------------------------- 
	// Name: DrawPause()
	// Desc: Desenha a mensagem de pausa
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	protected void DrawPause()
	{
		String line1 = "CLICK ANYWHERE ON THE GAME",line2 = "TO CONTINUE PLAYING!";

		String Param1	= getParameter("PAUSELINE1");
		line1 	= (Param1 != null) ? Param1 : line1;
		String Param2	= getParameter("PAUSELINE2");
		line2 	= (Param2 != null) ? Param2 : line2;
		
		String s;

		s = line1;
		int iPosX = ((int)VideoManager.VideoSize.fx - JGDLFont.GetTextWidth(VideoManager, PauseFont,s))>>1;

		JGDLFont.DrawText(VideoManager,iPosX   ,141,s,Color.black,PauseFont,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,iPosX-1 ,140,s,Color.yellow,PauseFont,JGDLFont.JGDLNONE);
		
		s = line2;
		iPosX = ((int)VideoManager.VideoSize.fx - JGDLFont.GetTextWidth(VideoManager, PauseFont,s))>>1;
		
		JGDLFont.DrawText(VideoManager,iPosX   ,161,s,Color.black,PauseFont,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,iPosX-1 ,160,s,Color.yellow,PauseFont,JGDLFont.JGDLNONE);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: SetCurrentScene(JGDLScene pr_NewScene)
	// Desc: Ajusta a cena corrente do jogo
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void SetCurrentScene(JGDLScene pr_NewScene)
	{
		if(pr_NewScene != null)
		{	
			//se já existia uma cena ativa, então libera ela.
			if(pr_CurScene != null)
			{
				pr_CurScene.Release();
				pr_CurScene = null;
			}
	
			//atribui a nova cena corrente
			pr_CurScene = pr_NewScene;
			
			//atribui o main à cena
			pr_CurScene.pr_Main = this;
	
			//inicializa a nova cena
			pr_CurScene.Initialize();
			TimeHandler.Reset();
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Pause()
	// Desc: Pauses the game
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Pause()
	{
		bPaused = true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: IsPaused()
	// Desc: returna true se o jogo está em pausa
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean IsPaused()
	{
		return bPaused;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Resume()
	// Desc: Resumes the game after the pause
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Resume()
	{
		if(bPaused)
		{
			TimeHandler.Reset();
			bPaused = false;
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: void init()
	// Desc: initializes the applet
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void init()
	{
		System.out.println("init()");
		
		bRunning            = true;
		
		String Param;
	
/*		Param = getParameter("ConnectionType");
		String ConnectType 				= (Param != null) ? Param : "";
	
		if(0 == ConnectType.compareTo("remote"))
		{
			//current dir for remote server
			CurDir = getCodeBase().toString();
		}
		else
		{
			//current dir for local server
			CurDir = getCodeBase().getFile();
			//showStatus(CurDir);
			CurDir = CurDir.replace('|',':');
			System.out.println("CurrentDir: " + CurDir);
		}*/
		
		//game limits
		VideoManager.VideoSize = new JGDLVector(new Integer(getParameter("width")).intValue(),new Integer(getParameter("height")).intValue());
		
		//getting game parameters
		Param 		= getParameter("GameName");
		GameName 	= (Param != null) ? Param : GameName;
		
		//pegando diretórios de arquivos
		Param 		= getParameter("SURFACESDIR");
		ImagesDir 	= (Param != null) ? Param : ImagesDir;
		ImagesDir 	+= "/";
		Param 		= getParameter("SOUNDSDIR");
		SoundsDir 	= (Param != null) ? Param : SoundsDir;
		SoundsDir 	+= "/";

		//inicializando a JGDL
		Initialize();
		
		AddResources();

		//inicializando thread de execucao do jogo
		Executor.start();	    
	}
	
	//---------------------------------------------------------------------------- 
	// Name: paint(Graphics g) 
	// Desc: Pinta a applet
	// Pams: gráfico da applet
	//---------------------------------------------------------------------------- 
	public void paint(Graphics g) 
	{
		//flipping images...
		while(!g.drawImage(VideoManager.BackBuffer.image,0,0,this));
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetCurrentScene()
	// Desc: Retorna a cena corrente
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLScene GetCurrentScene()
	{
		return pr_CurScene;
	}

	//---------------------------------------------------------------------------- 
	// Name: stop()
	// Desc: Stops the thread
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
  public void stop()
  {
  	System.out.println("stop()");
		Release();
  }

	//---------------------------------------------------------------------------- 
	// Name: getAppletInfo()
	// Desc: Returns information from the applet
	// Pams: none
	//---------------------------------------------------------------------------- 
	public String getAppletInfo()
	{
		return GameName + "\nJGDL ©2003, Nology Softwares";
	}
	
	protected abstract void AddResources();
	protected abstract void InitGame();
}
