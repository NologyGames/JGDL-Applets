
import JGDL.*;
import java.awt.*;

public class HelloGame extends JGDLMain
{
	private HelloLevel Scene = new HelloLevel();
	
	//---------------------------------------------------------------------------- 
	// Name: HelloGame()
	// Desc: Construtor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public HelloGame()
	{
		bShowInfo = false;
		InfoColor = Color.red;
		iLang = WLANG_ENG;
	}
	
	public void run()
	{
/*		if(InkUtil.INVALID == InkUtil.getGameCanvasType(this,"HelloGame"))
		{
			Font Arial = new Font("Arial",Font.BOLD,16);
			
			VideoManager.BackBuffer.Clear(Color.darkGray);
			String str = InkUtil.loadString("Zacflja\174jk/\174`i{xn}j!/l`a{nl{/\174z\177\177`}{Ohnbjg`z\174j!l`b");
			JGDLFont.DrawText(VideoManager,230,142,str,Color.black, Arial,JGDLFont.JGDLCENTERX);
			JGDLFont.DrawText(VideoManager,224,140,str,Color.yellow, Arial,JGDLFont.JGDLCENTERX);
			//troca frontbuffer com backbuffer
			getGraphics().drawImage(VideoManager.BackBuffer.image,0,0,this);
		}
		else
		{*/
			super.run();
//		}
	}

	//---------------------------------------------------------------------------- 
	// Name: AddResources()
	// Desc: Adiciona os arquivos de sons e imagens que devem ser carregados
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	protected void AddResources()
	{
		System.out.println("AddResources()");

		//Effects
		AddImage("spr_explosion.gif");
		AddImage("spr_UpArrow.gif");
		AddImage("spr_DownArrow.gif");
		AddImage("spr_LeftArrow.gif");
		AddImage("spr_RightArrow.gif");
		AddImage("spr_PowerUPS.gif");
		
		//board sprites
		AddImage("spr_borderdown.gif");
		AddImage("spr_borderleft.gif");
		AddImage("spr_borderup.gif");
		AddImage("spr_boardcenter.gif");
		AddImage("spr_rightpannel.gif");
		
		AddImage("spr_NextLine.gif");
		AddImage("spr_phone1.gif");
		AddImage("spr_phone2.gif");
		AddImage("spr_phone3.gif");
		AddImage("spr_phone4.gif");
		AddImage("spr_phone1_end.gif");
		AddImage("spr_phone2_end.gif");
		AddImage("spr_phone3_end.gif");
		AddImage("spr_phone4_end.gif");
		AddImage("spr_wire1.gif");
		AddImage("spr_wire2.gif");
		AddImage("spr_wire3.gif");
		AddImage("spr_wire4.gif");
		AddImage("spr_selection.gif");
		AddImage("spr_levelbar.gif");
		
		//level up
		AddImage("spr_lutitle.gif");		
		AddImage("bkg_levelup.gif");		
		AddImage("btn_ContinueGame.gif");		
		AddImage("btn_DownloadFreeTrial.gif");		
		AddImage("spr_lubrick.gif");		
		AddImage("btn_PlayAgain.gif");		
		AddImage("spr_congrats.gif");
		//help
		AddImage("bkg_TelaHelp.gif");
		AddImage("btn_ClickToPlay.gif");
		AddImage("btn_DownloadFreeTrialMenu.gif");
		AddImage("btn_Pause.gif");
		AddImage("spr_TelasLevelUp.gif");
		
		GameName = "Hello!";
/*		AddSound("sfx_Tone1.au");
		AddSound("sfx_Tone2.au");
		AddSound("sfx_Tone3.au");
		AddSound("sfx_Tone4.au");
		AddSound("sfx_Tone5.au");
		AddSound("sfx_Tone6.au");
		AddSound("sfx_Tone7.au");
		AddSound("sfx_NewPhones.au");
		AddSound("sfx_PUBomb.au");
		AddSound("sfx_ClickPhone.au");
		AddSound("sfx_PULinCol.au");
		AddSound("sfx_LUBlockShow.au");
		AddSound("sfx_LUBlockExplode.au");
		AddSound("sfx_MenuOpen.au");*/
	}

	//---------------------------------------------------------------------------- 
	// Name: InitGame()
	// Desc: Ponto de entrada do jogo
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	protected void InitGame()
	{
		System.out.println("InitGame()");
		super.SetCurrentScene(Scene);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza o jogo corrente
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		System.out.println("releasing my game...");
		super.Release();
		
		Scene.Release();
		Scene = null;
		
		return true;
	}

/*	//---------------------------------------------------------------------------- 
	// Name: DrawPause()
	// Desc: Desenha a mensagem de pausa
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	protected void DrawPause()
	{
		Font Arial = new Font("Arial",Font.ITALIC|Font.BOLD,20);
		JGDLFont.DrawText(VideoManager,50,142,"CLICK ANYWHERE ON THE GAME",Color.darkGray, Arial,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,48,140,"CLICK ANYWHERE ON THE GAME",Color.yellow, Arial,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,80,162,"TO CONTINUE PLAYING!",Color.darkGray,Arial,JGDLFont.JGDLNONE);
		JGDLFont.DrawText(VideoManager,78,160,"TO CONTINUE PLAYING!",Color.yellow,Arial,JGDLFont.JGDLNONE);
	}*/

}