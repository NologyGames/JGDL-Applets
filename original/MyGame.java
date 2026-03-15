
import JGDL.*;

public class MyGame extends JGDLMain
{
	private MyScene Scene = new MyScene();
	
	//---------------------------------------------------------------------------- 
	// Name: MyGame()
	// Desc: Construtor padrão
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	public MyGame()
	{
		bShowInfo = false;
	}

	//---------------------------------------------------------------------------- 
	// Name: AddResources()
	// Desc: Adiciona os arquivos de sons e imagens que devem ser carregados
	// Pams: nenhum
	//---------------------------------------------------------------------------- 
	protected void AddResources()
	{
		System.out.println("AddResources()");
		ImagesList.add("Surfaces/backgroundjava.gif");
		ImagesList.add("Surfaces/KangooFrames13.gif");
		ImagesList.add("Surfaces/BausFinal.gif");
		ImagesList.add("Surfaces/final_pq.gif");
		ImagesList.add("Surfaces/ItemFrames5.gif");
		ImagesList.add("Surfaces/RockFinal.gif");
		SoundsList.add("Sounds/carhorn.au");
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

}