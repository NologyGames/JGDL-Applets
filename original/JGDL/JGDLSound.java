/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLSound.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

import java.applet.AudioClip;

public class JGDLSound extends JGDLObject
{
	//! Nome do Arquivo de Som (.wav)
	String FileName = "";
	//! Referência para o gerenciador de Som
	JGDLSoundManager pr_SoundManager = null;
	//!audio clip
	AudioClip audio = null;

	//---------------------------------------------------------------------------- 
	// Name: JGDLSound()
	// Desc: consrutor da classe de som
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	JGDLSound()
	{
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza um som
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		Stop();
		FileName = null;
		audio = null;
		
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Stop()
	// Desc: Para de tocar um som, não faz o retorno do cursor de play
	// caso outro play for dado, vai continuar de onde parou
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Stop()
	{
		if(audio != null)
  	{
  		audio.stop();
  		return true;
  	}
  	
		return false;
	}
	
	
	//---------------------------------------------------------------------------- 
	// Name: Play(boolean bLoop)
	// Desc: Toca um som imediatamente
	// Pams: bLoopParam true se som vai tocar em loop, false caso contrario
	//---------------------------------------------------------------------------- 
	public boolean Play()
	{
  	if(audio != null && pr_SoundManager.bEnableSounds)
  	{
 			audio.play();
  		return true;
	  }
	  
		return false;
	}	

	//---------------------------------------------------------------------------- 
	// Name: Play(boolean bLoop)
	// Desc: Toca um som em loop
	// Pams: bLoopParam true se som vai tocar em loop, false caso contrario
	//---------------------------------------------------------------------------- 
	public boolean Loop()
	{
  	if(audio != null && pr_SoundManager.bEnableSounds)
  	{
 			audio.loop();
  		return true;
	  }
	  
		return false;
	}	

}
