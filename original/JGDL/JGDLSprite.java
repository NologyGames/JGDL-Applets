/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLSprite.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

import java.awt.*;

public class JGDLSprite extends JGDLObject
{
	public final static 	JGDLVector TempPos1  = new JGDLVector();
	public final static 	JGDLVector TempPos2  = new JGDLVector();
	public final static 	JGDLFrame  TempFrame = new JGDLFrame();

	//!Flag de visibilidade.
	public boolean			bVisible = true;
	
	//!Flag de congelamento
	public boolean			bFreezed = false;

	//!Ponteiro para o JGDLMain.
	public JGDLMain	pr_Main = null;

	//!Ponteiro para a layer à qual este sprite pertence.
	public JGDLLayer	pr_Layer = null;

	//!Posicão XY.
	public JGDLVector position = new JGDLVector();

	//!Lista de animações.
	private JGDLList	Animations = new JGDLList();

	//!Animação corrente.
	public int iCurrentAnim = -1;
	
	//!Tamanho da Janela do frame
	public JGDLVector window = new JGDLVector();

	//!Imagem fonte.
	public JGDLImage pr_Image = null;

	//!flag de espelhamento.
	public byte byMirror = JGDLImage.JGDLMIRROR_NONE;


  //!acumulador de tempo para movimentação
  private JGDLTimeAccumulator MoveTime = new JGDLTimeAccumulator();
  
  //!Posição inicial da movimentação
  private JGDLVector vFrom = new JGDLVector();
  
  //!Posição final da movimentação
  private JGDLVector vTo = new JGDLVector();
  
	//---------------------------------------------------------------------------- 
	// Name: JGDLSprite()
	// Desc: desfault ructor
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLSprite()
	{
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza o Sprite
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
	//	System.out.print("Releasing sprite: ");
		if(pr_Main != null)
		{
			if(pr_Image != null)
			{
//				System.out.println("image: " + pr_Image.FileName);
				pr_Main.VideoManager.DeleteImage(pr_Image);
				pr_Image = null;
			}
		}
		ClearAnimations();
		
		position 		= null;
		Animations 	= null;
		window 			= null;
		MoveTime		= null;
		vFrom				= null;
		vTo					= null;
		
		return false;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: StopMove()
	// Desc: para a movimentação do sprite
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void StopMove()
	{
		MoveTime.iTimeAccum = MoveTime.iTimeLimit;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Create()
	// Desc: Cria o sprite
	// Pams: nome do arquivo de imagem, tamanho do frame
	//---------------------------------------------------------------------------- 
	public boolean Create(String cp_chFileName, JGDLVector cFrameSize)
	{
		if(pr_Main != null)
		{
			pr_Image = pr_Main.VideoManager.LoadImage(cp_chFileName);
			if(pr_Image == null)
			{
//				System.out.println("IMAGEM NULA!!");
			}
			pr_Image.SetFrameSize(cFrameSize);
			
			//setando a janela do frame
			window.atrib(cFrameSize);
		}
		return false;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Draw()
	// Desc: pinta o sprite no back buffer
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void Draw()
	{
		if(pr_Main != null)
		{
			JGDLVector Pos = TempPos1;
			Pos.fx = position.fx;
			Pos.fy = position.fy;
			Pos.Floor();
			window.Floor();
			
			int iCurrentFrame = ((iCurrentAnim >= 0) && (iCurrentAnim < (int)Animations.size())) ? ((JGDLAnimation)Animations.get(iCurrentAnim)).GetCurrentFrame() : 0;
			if(iCurrentFrame >= 0 && iCurrentFrame < pr_Image.Frames.size())
			{
				JGDLFrame pr_SrcFrame = (JGDLFrame)pr_Image.Frames.get(iCurrentFrame);
				JGDLVector pr_SrcWindow = TempPos2;
				pr_SrcWindow.fx = pr_SrcFrame.GetWidth();
				pr_SrcWindow.fy = pr_SrcFrame.GetHeight();
				pr_SrcWindow.Floor();
				window.fx = (Math.abs(pr_SrcWindow.fx) > pr_Image.FrameSize.fx)? pr_Image.FrameSize.fx : window.fx;
				window.fy = (Math.abs(pr_SrcWindow.fy) > pr_Image.FrameSize.fy)? pr_Image.FrameSize.fy : window.fy;
				//se não é igual então redimensiona frame
				if(window.operatornoteq(pr_SrcWindow))
				{				
					//JGDLFont.DrawText(pr_Main.VideoManager,100,200,"SrcFrame: Right: " + pr_SrcFrame.iRight,Color.yellow,new Font("Arial",Font.ITALIC|Font.BOLD,20));
					//pr_Main.showStatus("FrameWindow é diferente!");
	
					TempFrame.atrib(pr_SrcFrame);
	
					//tratando janelas
					if(window.fx > 0.0f)
					{
						if(window.fx < pr_Image.FrameSize.fx)
						{
							pr_SrcFrame.iRight = pr_SrcFrame.iLeft + (int)window.fx;
						}
						else
						{
							pr_SrcFrame.iRight = pr_SrcFrame.iLeft + (int)pr_Image.FrameSize.fx;
						}
					}
					else
					{
						window.fx = (window.fx < -pr_Image.FrameSize.fx) ? -pr_Image.FrameSize.fx : window.fx; 
						pr_SrcFrame.iRight	= pr_SrcFrame.iLeft + (int)pr_Image.FrameSize.fx;
						pr_SrcFrame.iLeft		= pr_SrcFrame.iRight + (int)window.fx;
						pr_SrcFrame.iLeft		= (pr_SrcFrame.iLeft >=0)? pr_SrcFrame.iLeft : 0;
						Pos.fx += pr_Image.FrameSize.fx + window.fx;
					}
	
					//tratando janelas
					if(window.fy > 0.0f)
					{
						if(window.fy < pr_Image.FrameSize.fy)
						{
							pr_SrcFrame.iBottom = pr_SrcFrame.iTop + (int)window.fy;
						}
						else
						{
							pr_SrcFrame.iBottom = pr_SrcFrame.iTop + (int)pr_Image.FrameSize.fy;
						}
					}
					else
					{
						window.fy = (window.fy < -pr_Image.FrameSize.fy) ? -pr_Image.FrameSize.fy : window.fy; 
						pr_SrcFrame.iBottom	= pr_SrcFrame.iTop + (int)pr_Image.FrameSize.fy;
						pr_SrcFrame.iTop		= pr_SrcFrame.iBottom + (int)window.fy;
						pr_SrcFrame.iTop		= (pr_SrcFrame.iTop >=0)? pr_SrcFrame.iTop : 0;
						Pos.fy += pr_Image.FrameSize.fy + window.fy;
					}
	
	
					//JGDLFont.DrawText(pr_Main.VideoManager,100,220,"SrcFrame: Right: " + pr_SrcFrame.iRight + " SrcFrame: Bottom " + pr_SrcFrame.iBottom,Color.yellow,new Font("Arial",Font.ITALIC|Font.BOLD,20));
					pr_Main.VideoManager.BackBuffer.DrawImage(Pos,pr_Image,iCurrentFrame,byMirror);
	
					//restaurando source frame
					pr_SrcFrame.atrib(TempFrame);
	
				}
				else
				{
					pr_Main.VideoManager.BackBuffer.DrawImage(Pos,pr_Image,iCurrentFrame,byMirror);
				}
	
				Pos = null;
				pr_SrcWindow = null;
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Update()
	// Desc: updates the sprite
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void Update()
	{
		if(iCurrentAnim >=0 && iCurrentAnim < (int)Animations.size())
		{
			((JGDLAnimation)Animations.get(iCurrentAnim)).Update();
		}
		UpdateMove();
	}
	
	
	//---------------------------------------------------------------------------- 
	// Name: UpdateMove()
	// Desc: updates the sprite move
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void UpdateMove()
	{
		if(!MoveTime.Ended())
		{
			MoveTime.Update();
			if(!MoveTime.Ended())
			{
				float fMult = ((float)MoveTime.iTimeAccum)/(float)MoveTime.iTimeLimit;
	
				position.fx = vFrom.fx + ((vTo.fx - vFrom.fx)*fMult);
				position.fy = vFrom.fy + ((vTo.fy - vFrom.fy)*fMult);
				
			}
			else
			{
				position.fx = vTo.fx;
				position.fy = vTo.fy;
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: AddAnimation()
	// Desc: Adiciona uma animação ao sprite
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void AddAnimation(int iFPSParam, boolean bRepeat,int[] FramesPam)
	{
	  JGDLAnimation p_Animation = new JGDLAnimation();
		p_Animation.pr_Main					= pr_Main;
		p_Animation.bRepeat					= bRepeat;
		p_Animation.iFramesPerSecond = iFPSParam;
	
		//System.out.println(new Integer(FramesPam.length).toString());
		for(int i = 0; i < FramesPam.length; i++)
		{
			//System.out.println(new Integer(FramesPam[i]).toString());
			p_Animation.Frames.add(new Integer(FramesPam[i]));
		}
		
		Animations.add(p_Animation);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ResetAnimation()
	// Desc: Reseta a animação corrente
	// Pams: none
	//---------------------------------------------------------------------------- 
	public void ResetAnimation()
	{
		if(iCurrentAnim >= 0 && iCurrentAnim < (int)Animations.size())
		{
			((JGDLAnimation)Animations.get(iCurrentAnim)).Reset();
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: SetCurrentAnimation(short sAnim)
	// Desc: seta a animação corrente
	// Pams: animação
	//---------------------------------------------------------------------------- 
	public void SetCurrentAnimation(int iAnim)
	{
		if(iAnim != iCurrentAnim)
		{
			iCurrentAnim = iAnim;
			ResetAnimation();
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: ClearAnimations()
	// Desc: Deleta todas as animações do vetor
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void ClearAnimations()
	{
		if(null != Animations)
		{
			for(int i = (int)Animations.size()-1; i >=0; i--)
			{
				JGDLAnimation pr_Anim = (JGDLAnimation)Animations.get(i);
				pr_Anim.Release();
				pr_Anim = null;
			}
			Animations.clear();
		}
	}
	//---------------------------------------------------------------------------- 
	// Name: GetClone(JGDLSprite p_Clone)
	// Desc: retorna um clone do sprite atual
	// Pams: variavle a receber o clone
	//---------------------------------------------------------------------------- 
	public void GetClone(JGDLSprite p_Clone)
	{
		if(p_Clone != null)
		{
			p_Clone.bVisible			= bVisible;
			p_Clone.byMirror			= byMirror;
	
			p_Clone.pr_Image			= pr_Image;
			pr_Image.sReferences++;
	
			p_Clone.pr_Layer			= pr_Layer;
			p_Clone.pr_Main			= pr_Main;
			p_Clone.position.atrib(position);
			p_Clone.iCurrentAnim = iCurrentAnim;
			p_Clone.window.atrib(window);
	
	
			p_Clone.ClearAnimations();
			int iSize = (int)Animations.size();
			for(int i = 0; i < iSize; i++)
			{
				p_Clone.Animations.add(((JGDLAnimation)Animations.get(i)).GetClone());
			}
		}
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetClone(boolean bAddToLayer)
	// Desc: retorna um clone do sprite atual
	// Pams: flag indicando que o sprite deve ser adicionado a layer
	//---------------------------------------------------------------------------- 
	public JGDLSprite GetClone(boolean bAddToLayer)
	{
		JGDLSprite p_Clone		= new JGDLSprite();
		if(p_Clone != null)
		{
			p_Clone.bVisible			= bVisible;
			p_Clone.byMirror			= byMirror;
	
			p_Clone.pr_Image			= pr_Image;
			pr_Image.sReferences++;
	
			p_Clone.pr_Layer			= pr_Layer;
			p_Clone.pr_Main			= pr_Main;
			p_Clone.position.atrib(position);
			p_Clone.iCurrentAnim = iCurrentAnim;
			p_Clone.window.atrib(window);
	
	
			int iSize = (int)Animations.size();
			for(int i = 0; i < iSize; i++)
			{
				p_Clone.Animations.add(((JGDLAnimation)Animations.get(i)).GetClone());
			}
			if(bAddToLayer && (pr_Layer != null))
			{
//				System.out.println("clone: add to layer");
				pr_Layer.Sprites.add(p_Clone);
			}
			return p_Clone;
		}
		return null;
	}

	//---------------------------------------------------------------------------- 
	// Name: GetCenterPos()
	// Desc: retorna a posição do centro do sprite
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void GetCenterPos(JGDLVector vRet)
	{
		vRet.fx = position.fx + ((pr_Image.FrameSize.fx)*0.5f);
		vRet.fy = position.fy + ((pr_Image.FrameSize.fy)*0.5f);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: SetCenterPos(JGDLVector CenterPos)
	// Desc: posiciona o sprite a partir do centro
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public void SetCenterPos(JGDLVector CenterPos)
	{
		position.fx = CenterPos.fx - (pr_Image.FrameSize.fx*0.5f);
		position.fy = CenterPos.fy - (pr_Image.FrameSize.fy*0.5f);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: MoveTo(JGDLVector NewPos, int iMoveTime)
	// Desc: move o sprite para a posição
	// Pams: nova posição, tempo do mevimento
	//---------------------------------------------------------------------------- 
	public void MoveTo(JGDLVector NewPos, int iMoveTime)
	{
		vFrom.fx = position.fx;
		vFrom.fy = position.fy;
		
		vTo.fx 	 = NewPos.fx;
		vTo.fy 	 = NewPos.fy;
		
		MoveTime.Init(pr_Main,iMoveTime);
	}

	//---------------------------------------------------------------------------- 
	// Name: MoveTo(JGDLVector NewPos, int iMoveTime)
	// Desc: move o sprite para a posição
	// Pams: nova posição, tempo do mevimento
	//---------------------------------------------------------------------------- 
	public void MoveTo(float fx, float fy, int iMoveTime)
	{
		vFrom.fx = position.fx;
		vFrom.fy = position.fy;
		
		vTo.fx 	 = fx;
		vTo.fy 	 = fy;
		
		MoveTime.Init(pr_Main,iMoveTime);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: MoveTo(JGDLVector NewPos, int iMoveTime)
	// Desc: move o centro do sprite para a posição
	// Pams: novo centro, tempo do mevimento
	//---------------------------------------------------------------------------- 
	public void MoveCenterTo(JGDLVector NewCenter, int iMoveTime)
	{
		vFrom.fx = position.fx ;
		vFrom.fy = position.fy;
		
		vTo.fx 	 = NewCenter.fx - (pr_Image.FrameSize.fx*0.5f);
		vTo.fy 	 = NewCenter.fy - (pr_Image.FrameSize.fy*0.5f);
		
		MoveTime.Init(pr_Main,iMoveTime);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: IsMouseOver()
	// Desc: retorna true se o mouse está sobre o sprite
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean IsMouseOver()
	{
		float fRight  = position.fx + window.fx;
		float fBottom = position.fy + window.fy;
		JGDLVector MousePos = pr_Main.InputManager.GetMousePos();
		return (position.fx <= MousePos.fx && MousePos.fx <= fRight &&
						position.fy <= MousePos.fy && MousePos.fy <= fBottom);
	}
	//---------------------------------------------------------------------------- 
	// Name: Clicked(int iButton = 0)
	// Desc: retorna true se o mouse clicou neste sprite
	// Pams: indice do botao
	//---------------------------------------------------------------------------- 
	public boolean Clicked(int iButton)
	{
		return (IsMouseOver() && pr_Main.InputManager.MouBtnPressed(iButton));
	}
	
	
	//---------------------------------------------------------------------------- 
	// Name: EndedAnimation()
	// Desc: retorna tru se a animação corrente é finita e acabou
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean EndedAnimation()
	{
			return ((iCurrentAnim >= 0) && (iCurrentAnim < (int)Animations.size())) ? ((JGDLAnimation)Animations.get(iCurrentAnim)).Ended() : false;
	} 
	
	//---------------------------------------------------------------------------- 
	// Name: GetCurrentAnimation()
	// Desc: retorna o indice da animação corrente
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public int GetCurrentAnimation()
	{
		return iCurrentAnim;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetCurrentAnimation()
	// Desc: retorna um ponteiro para a animação corrente
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLAnimation GetCurrentAnimationPointer()
	{
		return ((iCurrentAnim >= 0) && (iCurrentAnim < (int)Animations.size())) ? ((JGDLAnimation)Animations.get(iCurrentAnim)) : null;
	}
	
}
