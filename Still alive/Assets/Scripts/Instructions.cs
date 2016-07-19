using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class Instructions : MonoBehaviour {


	AudioSource ASInstructions;

	void Start () {
		
		ASInstructions = this.GetComponent<AudioSource> ();

		if(Social.localUser.authenticated){

			Social.ReportProgress ("CgkI3c2ekbAIEAIQBQ", 100.0f,	(bool success) => {
			});
		}

	}

	public void Back()
	{
		SceneManager.LoadScene("MenuPrincipal");
		ASInstructions.PlayOneShot (ASInstructions.clip);
	}
}
