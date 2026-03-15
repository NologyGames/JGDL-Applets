/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLSoundManager.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

import java.awt.*;
import java.net.URL;
import java.applet.AudioClip;

public class JGDLSoundManager extends JGDLObject
{
	
	public boolean bEnableSounds = true;
	//!Ponteiro de referência para a CGDLMain.
	JGDLMain					pr_Main = null;
	//!Para carregar sons	
	private MediaTracker 	Media = null;
	//!Lista de imagens carregadas.
	private JGDLList				Sounds = new JGDLList();

	//---------------------------------------------------------------------------- 
	// Name: JGDLSoundManager()
	// Desc: construtor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLSoundManager()
	{
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza o sound manager
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		// limpa o vetor de sons
		Sounds.clear();
		Sounds = null;
		Media = null;
	
		return true;
	}
	
	//--------------------------------------------------------
	// Name: LoadSoundFromMT(String FileName)
	// Desc: Lê uma imagem do Media Tracker
	// Pams: FileName
	//-----------------------------------------------------
	private AudioClip LoadSoundFromMT(String FileName)
	{
		if(0 == FileName.compareTo(""))
		{
			return null;
		}
		
		FileName = FileName.replace('\\','/');
		//System.out.println(FileName);
		
		AudioClip audio;
		URL path = pr_Main.getClass().getResource(FileName);
		if(null != path)
		{
			audio = pr_Main.getAudioClip(path);
		}
		else
		{
	    audio = pr_Main.getAudioClip(pr_Main.getCodeBase(),FileName);
		}
		
	
    if(null != audio)
    {
      try
      {
        Media.waitForID(Sounds.size());
      }
      catch (InterruptedException e)
      {
//        System.out.println(e.getMessage());
      }

    }
		System.out.println("Sound requested! ID: " + Sounds.size() + " - File: " + FileName);
		return audio;
	}

	
	//---------------------------------------------------------------------------- 
	// Name: LoadSound(char *cp_chFileName,boolean bol bIsFX)
	// Desc: Insere um novo som no vetor de sons do Manager o inicializa
	// Pams: cp_chFileName nome do arquivo de som (.wav)
	//---------------------------------------------------------------------------- 
	public JGDLSound LoadSound(String cp_chFileName)
	{
		//tenta encontrar uma imagem já lida
		for(int i = (int)Sounds.size()-1; i>=0; i--)
		{
			JGDLSound pr_Sound;
			pr_Sound = (JGDLSound)Sounds.get(i);
			//System.out.println("pr_Sound.FileName: " + pr_Sound.FileName + " - cp_chFileName: " + cp_chFileName);
			//Se já existe
			if(0 == pr_Sound.FileName.compareTo(pr_Main.SoundsDir + cp_chFileName))
			{
				//Adiciona uma referência
				//pr_Sound.sReferences++;
				//Retorna a imagem
				return pr_Sound;
			}
		}
	
		//Se não existe
		//Aloca a imagem
		JGDLSound pr_Sound = new JGDLSound();
		if(null != pr_Sound)
		{
			//Roda do arquivo
			pr_Sound.pr_SoundManager = this;
			
			//setando nome
			pr_Sound.FileName = pr_Main.SoundsDir + cp_chFileName;
			
			//carregando som
			pr_Sound.audio = LoadSoundFromMT(pr_Main.SoundsDir + cp_chFileName);

			//Adiciona a lista
			Sounds.add(pr_Sound);
		}
	
		//retorna o som
		return pr_Sound;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: Inicializa o DirectSound e cria o buffer primário 
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Initialize()
	{
		//iniciando MediaTracker
		Media = new MediaTracker(pr_Main);

		return true;
	}	

}
