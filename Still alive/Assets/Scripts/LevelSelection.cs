using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.SceneManagement;


public class LevelSelection : MonoBehaviour {

	public void loadLevel(int level)
	{
		MySaveGame getData = SaveGameSystem.LoadGame ("PartidaGuardada") as MySaveGame;

		if (getData != null) {

			if (getData.getNivelBloqueado (level) == false) {

				SceneManager.LoadScene ("Nivel" + level);
			}

		} 

		else if (level == 1) {

			SceneManager.LoadScene ("Nivel" + level);

		}

	}
}
