/*
 * @(#)Popcorn.java 1.0 04/03/11
 *
 * You can modify the template of this file in the
 * directory ..\JCreator\Templates\Template_2\Project_Name.java
 *
 * You can also create your own project template by making a new
 * folder in the directory ..\JCreator\Template\. Use the other
 * templates as examples.
 *
 */

import JGDL.*;
import java.awt.*;
import java.util.*;

public class Popcorn extends JGDLMain 
{

	private PopLevel Scene = new PopLevel();
	Font Arial = new Font("Arial",Font.ITALIC|Font.BOLD,20);

	//---------------------------------------------------------------------------- 
	// Name: Popcorn()
	// Desc: Construtor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public Popcorn()
	{
		GameName = "Super Popcorn Machine (web version)";
	}	
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza o jogo corrente
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		Scene.Release();
		Scene = null;
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: InitGame()
	// Desc: Ponto de entrada do jogo
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	protected void InitGame()
	{
//		System.out.println("InitGame()");
		super.SetCurrentScene(Scene);
		bShowInfo = false;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: AddResources()
	// Desc: Adiciona os arquivos de sons e imagens que devem ser carregados
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	protected void AddResources()
	{

//		System.out.println("AddResources()");

		//BackGround
		
		AddImage("inp_YellowCorn.gif");
		AddImage("inp_BlueCorn.gif");
		AddImage("inp_DarkGreenCorn.gif");
		AddImage("inp_RedCorn.gif");
		AddImage("inp_PopCorn.gif");
		AddImage("bkg_Movies.gif");
		AddImage("spr_pan.gif");
		AddImage("inp_SensorDisplay.gif");
		AddImage("lay_sensorback.gif");
		AddImage("lay_info.gif");
		AddImage("spr_PopsBar.gif");
		AddImage("spr_colorlights.gif");
		AddImage("spr_Stove.gif");
		AddImage("spr_StoveFire.gif");
		AddImage("spr_BkgMenu.gif");
		AddImage("btn_MenuTag.gif");
		AddImage("btn_menubuttons.gif");
		AddImage("men_PopUp.gif");
		AddImage("men_PopUpTitle.gif");
		
		AddImage("spr_MainScreen.gif");
		AddImage("spr_Congrats.gif");
		AddImage("spr_Explosion.gif");

		AddSound("sfx_PopCorn1.au");		
		AddSound("sfx_PopCorn2.au");		
		AddSound("sfx_PopCorn3.au");		
		AddSound("sfx_PopCorn4.au");		
		AddSound("sfx_PopCorn5.au");		
		AddSound("sfx_PopCorn6.au");		
		AddSound("sfx_PopGround.au");
		AddSound("sfx_MenuMove.au");
		AddSound("sfx_PUBomb.au");
		AddSound("sfx_PULine.au");

		GameName = "Super Popcorn Machine";
	}
	//---------------------------------------------------------------------------- 
	// Name: run()
	// Desc: loop do jogo
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void run()
	{
		super.run();
	}

	//---------------------------------------------------------------------------- 
	// Name: DrawPause()
	// Desc: Desenha a mensagem de pausa
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	/*protected void DrawPause()
	{
		CLIQUE EM QUALQUER PARTE DA TELA PARA VOLTAR A JOGAR!

		JGDLFont.DrawText(VideoManager,50,142,"CLICK ANYWHERE ON THE GAME",Color.darkGray, Arial,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,48,140,"CLICK ANYWHERE ON THE GAME",Color.yellow, Arial,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,80,162,"TO CONTINUE PLAYING!",Color.darkGray,Arial,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,78,160,"TO CONTINUE PLAYING!",Color.yellow,Arial,JGDLFont.JGDLNONE);

		JGDLFont.DrawText(VideoManager,50,142,"CLICK ANYWHERE ON THE GAME",Color.darkGray, Arial,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,48,140,"CLICK ANYWHERE ON THE GAME",Color.yellow, Arial,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,80,162,"TO CONTINUE PLAYING!",Color.darkGray,Arial,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,78,160,"TO CONTINUE PLAYING!",Color.yellow,Arial,JGDLFont.JGDLNONE);
	}*/

}
