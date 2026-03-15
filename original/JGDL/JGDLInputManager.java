/*
=======================================================
JGDL - Java Game Development Library
Implementation of the class JCGDLInputManager.
Copyright 2003, Nology Softwares. All rights reserved.
=======================================================
*/

package JGDL;

import java.awt.*;
import java.awt.event.*;

public class JGDLInputManager extends JGDLObject implements KeyListener,MouseListener,FocusListener,MouseMotionListener
{
	private static final byte JGDL_UNPRESSED 	= 0;
	private static final byte JGDL_DOWN			 	= 2;
	private static final int	JGDL_KEYS				= 256;
	private static final int	JGDL_BTNS				= 2;
	
	//!Ponteiro de referência para a CGDLMain.
	JGDLMain					pr_Main = null;
	
	//vetor de teclas
	private byte[] KeyboardState = new byte[JGDL_KEYS];
	//vetor de teclas
	private byte[] LastKeyboardState = new byte[JGDL_KEYS];
	//vetor de teclas
	private byte[] LastKeyboardState2 = new byte[JGDL_KEYS];

	//vetor de botões do mouse
	private byte[] MouseState = new byte[JGDL_BTNS];

	//vetor de botões do mouse
	private byte[] LastMouseState = new byte[JGDL_BTNS];
	
	//vetor de botões do mouse
	private byte[] LastMouseState2 = new byte[JGDL_BTNS];
	
	//!Posição atual do mouse.
	private JGDLVector MousePos = new JGDLVector();

	//!Posição do mouse vinda da aplicação Windows.
	private JGDLVector MouseWindowsPos = new JGDLVector();

	//---------------------------------------------------------------------------- 
	// Name: JGDLInputManager()
	// Desc: rutor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	JGDLInputManager()
	{
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Release()
	// Desc: finaliza o input manager
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Release()
	{
		pr_Main.removeMouseListener(this);
		pr_Main.removeMouseMotionListener(this);
		pr_Main.removeFocusListener(this);
		//pr_Main.removeKeyListener(this);

		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Initialize()
	// Desc: Inicia o teclado e mouse, usando DirectInput.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Initialize()
	{
		//adicionando teclado como escuta
		pr_Main.addKeyListener(this);
		//adicionando mouse como escuta
		pr_Main.addMouseListener(this);
		//adicionando mouse como escuta
		pr_Main.addMouseMotionListener(this);
		//adicionando mouse focus listener
		pr_Main.addFocusListener(this);
		
		int i;
		//keys
		for(i = 0; i < LastKeyboardState.length; i++)
		{
			KeyboardState[i] = LastKeyboardState[i] = LastKeyboardState2[i] = JGDL_UNPRESSED;
		}
		//mouse
		for(i = 0; i < LastMouseState.length; i++)
		{
			MouseState[i] = LastMouseState[i] = LastMouseState2[i] = JGDL_UNPRESSED;
		}
			
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: Read()
	// Desc: Lê entradas do teclado e mouse.
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public boolean Read()
	{
		int i;
		//keys
		for(i = 0; i < LastKeyboardState.length; i++)
		{
			LastKeyboardState2[i] = LastKeyboardState[i];
			LastKeyboardState[i]  = KeyboardState[i];
		}
		//mouse
		for(i = 0; i < LastMouseState.length; i++)
		{
			LastMouseState2[i] = LastMouseState[i];
			LastMouseState[i]  = MouseState[i];
		}
		return true;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: GetMousePos()
	// Desc: Retorna a posição atual do mouse
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public JGDLVector GetMousePos()
	{
		return MousePos;
	}
	
	//---------------------------------------------------------------------------- 
	// Name: KeyDown( int iKeyCode)
	// Desc: Verifica se uma tecla está pressionada.
	// Pams: código da tecla
	//---------------------------------------------------------------------------- 
	public boolean KeyDown(int iKeyCode)
	{
		return (LastKeyboardState[iKeyCode] == JGDL_DOWN);
	}

	//---------------------------------------------------------------------------- 
	// Name: KeyPressed( int iKeyCode)
	// Desc: Verifica se uma tecla foi pressionada neste instante.
	// Pams: código da tecla
	//---------------------------------------------------------------------------- 
	public boolean KeyPressed( int iKeyCode)
	{
		return (LastKeyboardState[iKeyCode] == JGDL_DOWN && LastKeyboardState2[iKeyCode] == JGDL_UNPRESSED);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: KeyReleased( int iKeyCode)
	// Desc: Verifica se uma tecla foi largada neste instante.
	// Pams: código da tecla
	//---------------------------------------------------------------------------- 
	public boolean KeyReleased( int iKeyCode)
	{
		return (LastKeyboardState[iKeyCode] == JGDL_UNPRESSED && LastKeyboardState2[iKeyCode] == JGDL_DOWN);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: MouBtnDown(int iBtnCode)
	// Desc: Verifica se um botão do mouse está pressionado.
	// Pams: código do botão (ex: 0, 1, 2)
	//---------------------------------------------------------------------------- 
	public boolean MouBtnDown(int iBtnCode)
	{	
		return (LastMouseState[iBtnCode] == JGDL_DOWN);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: MouBtnPressed(int iBtnCode)
	// Desc: Verifica se um botão do mouse foi pressionado neste instante.
	// Pams: código do botão (ex: 0, 1, 2)
	//---------------------------------------------------------------------------- 
	public boolean MouBtnPressed(int iBtnCode)
	{
		return (LastMouseState[iBtnCode] == JGDL_DOWN && LastMouseState2[iBtnCode] == JGDL_UNPRESSED);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: MouBtnReleased(int iBtnCode)
	// Desc: Verifica se um botão do mouse foi largado neste instante.
	// Pams: código do botão (ex: 0, 1, 2)
	//---------------------------------------------------------------------------- 
	public boolean MouBtnReleased(int iBtnCode)
	{
		return (LastMouseState2[iBtnCode] == JGDL_DOWN && LastMouseState[iBtnCode] == JGDL_UNPRESSED);
	}
	
	//---------------------------------------------------------------------------- 
	// Name: SetWindowsMousePos( JGDLVector &cNewPos)
	// Desc: Seta a nova posição do mouse vinda da aplicação Windows.
	// Pams: posição
	//---------------------------------------------------------------------------- 
	public void SetWindowsMousePos(JGDLVector cNewPos)
	{
		MouseWindowsPos = cNewPos;
	}


  //--------------------------------------------------------
  // Name: keyPressed(KeyEvent evt)
  // Desc: key pressed event handler
  // Pams: event
  //-----------------------------------------------------
  public void keyPressed(KeyEvent evt)
  {
		if(evt.getKeyCode() < JGDL_KEYS)
		{
			KeyboardState[evt.getKeyCode()] 		= JGDL_DOWN;
		}
  }
  
  //--------------------------------------------------------
  // Name: keyReleased(KeyEvent e)
  // Desc: key released event handler
  // Pams: event
  //-----------------------------------------------------
  public void keyReleased(KeyEvent evt)
  {
		KeyboardState[evt.getKeyCode()] 		= JGDL_UNPRESSED;
  }

  //--------------------------------------------------------
  // Name: keyTyped(KeyEvent e)
  // Desc: key typeed event handler
  // Pams: event
  //-----------------------------------------------------
  public void keyTyped(KeyEvent evt)
  {
		//System.out.print("Key Typed: ");
		//System.out.println(evt.getKeyCode());
  }

  //---------------------------------------------------------------------------- 
  // Name: mousePressed(MouseEvent e) 
  // Desc: Used to handle mouse button pressed
  // Pams: mouse event
  //---------------------------------------------------------------------------- 
	public void mousePressed(MouseEvent evt) 
	{
		//if is MetaDown then the right button was clicked
		int iIndex = (evt.isMetaDown()) ? 1 : 0;
	
		LastMouseState[iIndex] 	= MouseState[iIndex]; 
		MouseState[iIndex] 			= JGDL_DOWN;
		
		//System.out.println("Mouse Pressed: ");
		//System.out.print(iIndex);
	}
	
  //---------------------------------------------------------------------------- 
  // Name: mouseReleased(MouseEvent e) 
  // Desc: Used to handle mouse button release
  // Pams: mouse event
  //---------------------------------------------------------------------------- 
	public void mouseReleased(MouseEvent evt) 
	{
		//if is MetaDown then the right button was released
		int iIndex = (evt.isMetaDown()) ? 1 : 0;

		LastMouseState[iIndex] 	= MouseState[iIndex]; 
		MouseState[iIndex] 			= JGDL_UNPRESSED;
		//System.out.println("Mouse Released: ");
		//System.out.print(iIndex);
	}

  //---------------------------------------------------------------------------- 
  // Name: mouseClicked(MouseEvent e)
  // Desc: Used to handle mouse click
  // Pams: mouse event
  //---------------------------------------------------------------------------- 
	public void mouseClicked(MouseEvent evt)
	{
	/*	//if is MetaDown then the right button was clicked
		int iIndex = (evt.isMetaDown()) ? 1 : 0;
	
		LastMouseState[iIndex] 	= MouseState[iIndex]; 
		MouseState[iIndex] 			= JGDL_DOWN;*/
	}
	
  //---------------------------------------------------------------------------- 
  // Name: mouseEntered(MouseEvent e)
  // Desc: Called when the mouse enters in the applet
  // Pams: mouse event
  //---------------------------------------------------------------------------- 
	public void mouseEntered(MouseEvent evt) 
	{
	}
	
  //---------------------------------------------------------------------------- 
  // Name: mouseExited(MouseEvent e)
  // Desc: Called when the mouse exits in the applet
  // Pams: mouse event
  //---------------------------------------------------------------------------- 
	public void mouseExited(MouseEvent evt) 
	{
	}
  //---------------------------------------------------------------------------- 
  // Name: focusGained(FocusEvent e)
  // Desc: Chamado quando o se dá o foco na Applet. Continua o jogo.
  // Pams: evento de foco
  //---------------------------------------------------------------------------- 
	public void focusGained(FocusEvent e)
	{
/*		if(pr_Main != null)
		{
			pr_Main.Resume();
		}*/
	}
	
  //---------------------------------------------------------------------------- 
  // Name:focusLost(FocusEvent e)
  // Desc: Chamado quando o se perde o foco na Applet. Pausa o jogo.
  // Pams: evento de foco
  //---------------------------------------------------------------------------- 
	public void focusLost(FocusEvent e)
	{
		if(pr_Main != null && pr_Main.bPauseOnLostFocus)
		{
			pr_Main.Pause();
		}
	}

  //---------------------------------------------------------------------------- 
  // Name: mouseDragged(MouseEvent e)
  // Desc: chamado quando o mouse é arrastado.
  // Pams: mouse event
  //---------------------------------------------------------------------------- 
  public void mouseDragged(MouseEvent e)
  {
		MousePos.fx = e.getX();
		MousePos.fy = e.getY();
  }

  //---------------------------------------------------------------------------- 
  // Name: mouseMoved(MouseEvent e)
  // Desc: chamado quando o mouse é movido
  // Pams: mouse event
  //---------------------------------------------------------------------------- 
	public void mouseMoved(MouseEvent e)
	{
		MousePos.fx = e.getX();
		MousePos.fy = e.getY();
	}
	
}
