using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using UnityEngine.SceneManagement;
using GooglePlayGames;
using UnityEngine.SocialPlatforms;

public class Menu : MonoBehaviour {
	
	public Button play, instructions, volume, logros;
	public Sprite volumeOn, volumeOff;

	AudioSource ASPlay, ASInstructions, ASLogros;

	ShowToast toast;

	bool volumeValue;

	void Start () {

		ASPlay = play.GetComponent<AudioSource> ();
		ASInstructions = instructions.GetComponent<AudioSource> ();
		ASLogros = logros.GetComponent<AudioSource> ();
		toast = GetComponent<ShowToast> ();

		MySaveGame getData = SaveGameSystem.LoadGame ("PartidaGuardada") as MySaveGame;

		if (getData != null) {

			volumeValue = getData.getSonido ();
		} 

		else {

			volumeValue = true;
		}

		initialUIVolume ();
	}
	
	public void Play()
	{
		SceneManager.LoadScene("SelectorNiveles");
		ASPlay.PlayOneShot (ASPlay.clip);
	}

	public void Instructions()
	{
		SceneManager.LoadScene("Instrucciones");
		ASInstructions.PlayOneShot (ASPlay.clip);
	}

	public void Logros()
	{
		if (Social.localUser.authenticated == true) {

			Social.ShowAchievementsUI ();
			ASLogros.PlayOneShot (ASLogros.clip);
			
		} 

		else {

			toast.showToastOnUiThread ("No estás conectado a Google Play Games");
		}


	
	}

	public void ChangeVolume()
	{
		if (volumeValue == true) {

			volume.image.sprite = volumeOff;
			volumeValue = false;
			AudioListener.pause = true;

		}

		else {

			volume.image.sprite = volumeOn;
			volumeValue = true;
			AudioListener.pause = false;

		}

		saveVolume ();
	}

	void initialUIVolume()
	{
		if (volumeValue == true) {

			volume.image.sprite = volumeOn;
			AudioListener.pause = false;
		}

		else {

			volume.image.sprite = volumeOff;
			AudioListener.pause = true;

		}
	}

	void saveVolume()
	{
		MySaveGame guardar = SaveGameSystem.LoadGame ("PartidaGuardada") as MySaveGame;

		if (guardar == null) {

			guardar = new MySaveGame ();
		}

		guardar.setSonido (volumeValue);

		SaveGameSystem.SaveGame (guardar, "PartidaGuardada");
	}
}
