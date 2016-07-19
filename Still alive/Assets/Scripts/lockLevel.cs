using UnityEngine.UI;
using UnityEngine;
using System.Collections;

public class lockLevel : MonoBehaviour {

	public Sprite medals1, medals2, medals3;

	void Start () {

		MySaveGame getData = SaveGameSystem.LoadGame ("PartidaGuardada") as MySaveGame;

		int childsGeneral = transform.childCount;
		
		for (int i = 0; i < childsGeneral; i++) {

			if (getData != null) {

				if (getData.getNivelBloqueado (i+1) == false) {

					if(i!=0)
					{
						transform.GetChild (i).GetComponent<Button> ().enabled = true;

						transform.GetChild(i).GetChild(3).GetComponent<Image>().enabled = false;	
					}

					int numMedals = getData.getMedallasNivel (i+1);

					switch (numMedals) {

						case 1:

							transform.GetChild (i).GetChild (1).GetComponent<Image> ().sprite = medals1;

							break;

						case 2:

							transform.GetChild (i).GetChild (1).GetComponent<Image> ().sprite = medals2;

							break;

						case 3:

							transform.GetChild (i).GetChild (1).GetComponent<Image> ().sprite = medals3;

							break;

						default:
							break;
					}

				}

			} 
				
		}

	}
	

}
