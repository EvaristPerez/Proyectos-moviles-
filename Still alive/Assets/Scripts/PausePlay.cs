using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using UnityEngine.SceneManagement;

public class PausePlay : MonoBehaviour {

	public Button playPause;
	public Sprite playSprite, pauseSprite;
	public Canvas menuPause;

	bool pause;
	CanvasGroup groupPause;
	AudioSource ASButton;

	void Start () {

		pause = false;
		groupPause = menuPause.GetComponent<CanvasGroup> ();
		ASButton = playPause.GetComponent<AudioSource> ();

	}

	public void Change()
	{
		ASButton.PlayOneShot (ASButton.clip);

		if (pause == false) {

			pause = true;
			playPause.image.sprite = playSprite;
			Time.timeScale = 0f;

			groupPause.alpha = 1.0f;
			groupPause.interactable = true;
		} 

		else {

			pause = false;
			playPause.image.sprite = pauseSprite;
			Time.timeScale = 1f;

			groupPause.alpha = 0.0f;
			groupPause.interactable = false;

		}
	}

	public void returnMenu()
	{
		Time.timeScale = 1f;
		SceneManager.LoadScene("MenuPrincipal");
	}

	public void returnSelectorMenu()
	{
		Time.timeScale = 1f;
		SceneManager.LoadScene("SelectorNiveles");
	}

}
