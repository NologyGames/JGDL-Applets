/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCMyScene.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

import JGDL.*;

import java.awt.event.*;

public class MyScene extends JGDLScene
{

	JGDLLayer p_BKLayer = null;
	JGDLLayer p_Layer = null;
	JGDLSprite p_Sprite = null;
	JGDLSprite p_Clone = null;
	JGDLSprite p_Cursor = null;
	JGDLSound p_Sound = null;


	//---------------------------------------------------------------------------- 
	// Name: MyScene()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	MyScene()
	{
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza a cena
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		JGDLObject.ReleaseObject(p_Clone);
		
		super.Release();
	
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: Inicializa a cena. Essa rotina deve ser reescrita na cena, para que se possa
	//		   criar todos os objetos e fazer inicializações necessárias.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Initialize()
	{
		p_Sound = pr_Main.SoundManager.LoadSound("Sounds/carhorn.au");
		//pr_Main.iPercentReaded = 50;
		//pr_Main.VideoManager.DrawLoading();
		AddTileImage("Surfaces/backgroundjava.gif",new JGDLVector(128,350));

		p_BKLayer = pr_Main.GetCurrentScene().CreateLayer(new JGDLVector(128,350),new JGDLVector(1,1));
		p_BKLayer.Speed.fx = 0.3f;
		p_BKLayer.SetBrick(new JGDLVector(0,0),0,0);

		p_Layer		= pr_Main.GetCurrentScene().CreateLayer(new JGDLVector(32.0f,32.0f));
	

		p_Sprite	= p_Layer.CreateSprite("Surfaces/KangooFrames13.gif",new JGDLVector(37,45));
		p_Sprite.position.fx = 30;
		p_Sprite.position.fy = 50;
		int[] Frames = new int[5];
		Frames[0] = 0;
		Frames[1] = 1;
		Frames[2] = 2;
		Frames[3] = 3;
		Frames[4] = 4;
		//p_Sprite.byMirror = JGDLImage.JGDLMIRROR_LEFTRIGHT|JGDLImage.JGDLMIRROR_UPDOWN;
		p_Sprite.AddAnimation(15,true,Frames);
		Frames = null;

		//pr_Main.iPercentReaded = 100;
		//pr_Main.VideoManager.DrawLoading();


		//p_Sprite.AddAnimation(15,true,6,6,7,8,9,10,11);
		//p_Sprite.AddAnimation(15,false,6,12,13,14,15,16,17);
		p_Sprite.SetCurrentAnimation(0);

		//CGDLSound *p_Sound = pr_Main.SoundManager.LoadSound("C:\\Temp\\pulo.wav",true);
		//CGDLSound *p_Sound2= pr_Main.SoundManager.LoadSound("C:\\Temp\\pulo.wav",true);
	
		/*p_Sprite	= p_Layer.CreateSprite("C:\\temp\\spr_cholland.bmp",JGDLVector(80,80));
	
		AddTileImage("C:\\temp\\tiles.bmp",JGDLVector(32,32));
	
		p_BKLayer.Speed.fx = 0.5f;
		p_Layer.Speed.fx = 1.0f;
	
		//p_BKLayer.CreateBricks(JGDLVector(1,1));
		p_BKLayer.SetBrick(JGDLVector(0,0),1,0);
	
		p_Sprite.AddAnimation(15,true,6,0,1,2,3,4,5);
		p_Sprite.AddAnimation(15,true,6,6,7,8,9,10,11);
		p_Sprite.AddAnimation(15,false,6,12,13,14,15,16,17);
		p_Sprite.SetCurrentAnimation(1);
		p_Sprite.position = JGDLVector(300,200);
	
		p_Clone = p_Sprite.GetClone(true);
		p_Clone.position.fx -= 100.0f;
		p_Clone.byMirror = JGDLImage.JGDLMIRROR_LEFTRIGHT;
		p_Clone.SetCurrentAnimation(0);
	
		p_Cursor	= p_Layer.CreateSprite("C:\\temp\\cursor.bmp",JGDLVector(30,30));*/
	
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Execute()
	// Desc: Executa a cena. Essa rotina deve ser reescrita na cena, para que se possa
	//		   fazer a execução da cena.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Execute()
	{
		pr_Main.GetCurrentScene().Scroll(new JGDLVector(1,0));
		//p_Cursor.position = pr_Main.InputManager.GetMousePos();
		/*if(pr_Main.InputManager.MouBtnPressed(0))
		{
			p_Sprite.bVisible = false;
		}
		if(pr_Main.InputManager.MouBtnPressed(1))
		{
			p_Sprite.bVisible = true;
		}*/
		if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_P))
		{
			p_Sound.Play();
		}
		if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_A))
		{
			p_Sprite.bVisible = false;
		}
		if(pr_Main.InputManager.KeyPressed(KeyEvent.VK_S))
		{
			p_Sprite.bVisible = true;
		}
		if(pr_Main.InputManager.KeyDown(KeyEvent.VK_UP))
		{
			p_Sprite.position.fy -= 2.0f;
		}
		if(pr_Main.InputManager.KeyDown(KeyEvent.VK_DOWN))
		{
			p_Sprite.position.fy += 2.0f;
		}
		if(pr_Main.InputManager.KeyDown(KeyEvent.VK_LEFT))
		{
			p_Sprite.position.fx -= 2.0f;
		}
		if(pr_Main.InputManager.KeyDown(KeyEvent.VK_RIGHT))
		{
			p_Sprite.position.fx += 2.0f;
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Draw()
	// Desc: asf
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void Draw()
	{
		//chama primeiro o Draw do pai
		super.Draw();
	
		//adicione seus os objetos que você quer desenhar abaixo:
	}

}
