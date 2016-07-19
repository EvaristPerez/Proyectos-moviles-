using UnityEngine;
using System.Collections;
using GooglePlayGames;
using UnityEngine.SocialPlatforms;

public class GooglePG : MonoBehaviour {

	static bool googleFirst= true;

	ShowToast toast;

	// Use this for initialization
	void Awake () {

		PlayGamesPlatform.Activate();	
		toast = GetComponent<ShowToast> ();
	}

	void Start()
	{
		if (Social.localUser.authenticated == false && googleFirst==true) {

			googleFirst = false;

			((PlayGamesPlatform)Social.Active).Authenticate ((bool success) => {

				if(!success)
				{
					toast.showToastOnUiThread("No se ha podido conectar");
				}
			});

		} 

	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
